package geektime.spring.data.reactive.mongodbdemo;

import geektime.spring.data.reactive.mongodbdemo.converter.MoneyReadConverter;
import geektime.spring.data.reactive.mongodbdemo.converter.MoneyWriteConverter;
import geektime.spring.data.reactive.mongodbdemo.model.Coffee;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@SpringBootApplication
@Slf4j
public class MongodbDemoApplication implements ApplicationRunner {
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    private CountDownLatch cdl = new CountDownLatch(2);

    public static void main(String[] args) {
        SpringApplication.run(MongodbDemoApplication.class, args);
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(
                Arrays.asList(new MoneyReadConverter(),
                        new MoneyWriteConverter()));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//		startFromInsertion(() -> log.info("Runnable"));
        startFromInsertion(() -> {      // 它是在另外一个线程中完成的.
            log.info("Runnable");
            decreaseHighPrice();
        });

        log.info("after starting");

//		decreaseHighPrice();

        cdl.await();
    }

    private void startFromInsertion(Runnable runnable) {
        mongoTemplate.insertAll(initCoffee())
                .publishOn(Schedulers.elastic())            // 这是要在elastic线程池中处理initCoffee()的结果
                .doOnNext(c -> log.info("Next: {}", c))        // 打印每个Coffee对象
                .doOnComplete(runnable)                        // 当mongoTemplate将所有数据插入成功后，执行runnable对象
                .doFinally(s -> {
                    cdl.countDown();
                    log.info("Finnally 1, {}", s);
                })
                .count()        // 上面的操作返回的是一个Flx
                .subscribe(c -> log.info("Insert {} records", c));
    }

    private void decreaseHighPrice() {
        mongoTemplate.updateMulti(query(where("price").gte(3000L)),
                new Update().inc("price", -500L)
                        .currentDate("updateTime"), Coffee.class)
                .doFinally(s -> {
                    cdl.countDown();
                    log.info("Finnally 2, {}", s);
                })
                .subscribe(r -> log.info("Result is {}", r));
    }

    private List<Coffee> initCoffee() {
        Coffee espresso = Coffee.builder()
                .name("espresso")
                .price(Money.of(CurrencyUnit.of("CNY"), 20.0))
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        Coffee latte = Coffee.builder()
                .name("latte")
                .price(Money.of(CurrencyUnit.of("CNY"), 30.0))
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        return Arrays.asList(espresso, latte);
    }
}
