package geektime.spring.springbucks.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect             // 这是一个切面类
@Component          // 创建Bean
@Slf4j
public class PerformanceAspect {
//  @Around("execution(* geektime.spring.springbucks.repository..*(..))")
    @Around("repositoryOps()")
    public Object logPerformance(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();    // 方法执行前
        String name = "-";
        String result = "Y";
        try {
            name = pjp.getSignature().toShortString();
            return pjp.proceed();       // 执行后续调用
        } catch (Throwable t) {
            result = "N";
            throw t;
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("{};{};{}ms", name, result, endTime - startTime);  // 记录方法执行的耗时
        }
    }

    // 拦截：所有类的所有方法的执行
    @Pointcut("execution(* geektime.spring.springbucks.repository..*(..))")
    private void repositoryOps() {
    }
}
