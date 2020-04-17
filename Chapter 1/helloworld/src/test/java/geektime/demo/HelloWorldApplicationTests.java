package geektime.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloWorldApplicationTests {

    @Test
    public void contextLoads() {
        Long num1 = 10L;
        Long num2 = 10L;
        long num3 = 10L;
        assert num1 == num2;
        assert num1 == num3;
        assert num1.equals(num2);
    }
}

