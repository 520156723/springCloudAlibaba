package per.hqd.contentcenter.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class AopTest {

    @Pointcut("execution(public * per.hqd.contentcenter.TestController.testAop(..))")
    public void test(){

    }

    @Before("test()")
    public void beforeTest(){
        System.out.println("执行之前。。。");
    }

    @After("test()")
    public void afterTest(){
        System.out.println("执行之后。。。");
    }

    @Around("test()")
    public Object aroundTest(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.currentTimeMillis();
        Object res = pjp.proceed();
        long end = System.currentTimeMillis();
        log.info(String.format("执行了%s毫秒", end - begin));
        return res;
    }
}
