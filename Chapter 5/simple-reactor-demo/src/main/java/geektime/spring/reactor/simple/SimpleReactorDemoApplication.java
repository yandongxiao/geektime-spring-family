package geektime.spring.reactor.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
@Slf4j
public class SimpleReactorDemoApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(SimpleReactorDemoApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// 按照下面的模式来使用Reactive Programming
		Flux.range(1, 6)
				.doOnRequest(n -> log.info("Request {} number", n))			// 订阅者线程：订阅者要订阅的数据总量，它会从发布者那里取出这么多的数据。缓存起来。
                // 这一行后面的代码，就会运行在elastic线程池当中完成。
				// 你可以在多个位置指定publishOn
				.doOnComplete(() -> log.info("Publisher COMPLETE 1"))		// 订阅者线程：这一句话的日志虽然是publisher ..., 但是它是在订阅者线程中才对。
				.publishOn(Schedulers.elastic())							// 以下操作将会在elastic线程池中完成。你可以在不同位置，指定publishOn.
				.map(i -> {													// 对发布内容进行调整
					log.info("Publish {}, {}", Thread.currentThread(), i);
//					return 10 / (i - 3);
					return i;
				})
				.doOnComplete(() -> log.info("Publisher COMPLETE 2"))		// 如果上面map中出现了除0错误，elastic线程池的任务当然是没有完成
				.subscribeOn(Schedulers.single())							// 指定单独的订阅线程
//				.onErrorResume(e -> {										// 在elastic线程中出现错误的两种处理方式
//					log.error("Exception {}", e.toString());
//					return Mono.just(-1);
//				})
//				.onErrorReturn(-1)
                // subscribe中指定了具体订阅的动作，但是它和subscribeOn指定的线程没关系！
				.subscribe(i -> log.info("Subscribe {}: {}", Thread.currentThread(), i),
						e -> log.error("error {}", e.toString()),
						() -> log.info("Subscriber COMPLETE")//,
//						s -> s.request(4)									// 指定订阅总量为4
				);
		Thread.sleep(2000);
	}
}

