package per.hqd.contentcenter.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import per.hqd.contentcenter.annotation.HqdAop;

@Component
@Aspect
@Slf4j
public class AopTest {

    @Pointcut("execution(public * per.hqd.contentcenter.TestController.testAop1(..))")
    public void test1(){

    }

    @Pointcut("@annotation(hqdAop)")
    public void test2(HqdAop hqdAop){

    }

    @Before("test1()")
    public void beforeTest(){
        System.out.println("执行之前。。。");
    }

    @After("test1()")
    public void afterTest(){
        System.out.println("执行之后。。。");
    }

    @Around("test1()")
    public Object aroundTest(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.currentTimeMillis();
        Object res = pjp.proceed();
        long end = System.currentTimeMillis();
        log.info(String.format("执行了%s毫秒", end - begin));
        return res;
    }

    @Around(value = "test2(hqdAop)", argNames = "joinPoint,hqdAop")//或者直接@Around("@annotation(hqdAop)")
    public Object metric(ProceedingJoinPoint joinPoint, HqdAop hqdAop) throws Throwable {
        String name = hqdAop.name();
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long dur = System.currentTimeMillis() - start;
            log.info(String.format("%s执行了%s毫秒", name, dur));
        }
    }
}
