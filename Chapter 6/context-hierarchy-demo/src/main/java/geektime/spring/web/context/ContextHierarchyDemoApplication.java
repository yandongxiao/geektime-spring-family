package geektime.spring.web.context;

import geektime.spring.web.foo.FooConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
@Slf4j
public class ContextHierarchyDemoApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ContextHierarchyDemoApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 这里有两个ApplicationContext，fooContext是父Context，barContext是子Context
        // 通过注解的方式创建的Bean，都会被放置在fooContext下；通过xml配置的Bean，都会被放置在barContext下面。
        // 两个Context下面的Bean的名称是可以相同的。
        ApplicationContext fooContext = new AnnotationConfigApplicationContext(FooConfig.class);
        ClassPathXmlApplicationContext barContext = new ClassPathXmlApplicationContext(
                new String[]{"applicationContext.xml"}, fooContext);

        // 获取fooContext下面的bean，它是否被增强，取决于fooAspect这个Bean是否被创建
        TestBean bean = fooContext.getBean("testBeanX", TestBean.class);
        bean.hello();

        log.info("=============");

        // 获取barContext下面的bean, 它是否被增强，取决于<aop:aspectj-autoproxy/>是否被配置
        // 如果fooContext下创建了fooAspect，那么这个bean就会被增强；否则不会被增强
        // 如果只想在barContext下被增强，xml中还需要增加如下的配置:
        // <!--<bean id="fooAspect" class="geektime.spring.web.foo.FooAspect" />-->
        bean = barContext.getBean("testBeanX", TestBean.class);
        bean.hello();

        bean = barContext.getBean("testBeanY", TestBean.class);
        bean.hello();
    }
}
