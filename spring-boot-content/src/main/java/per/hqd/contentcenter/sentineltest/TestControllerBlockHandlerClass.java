package per.hqd.contentcenter.sentineltest;

import com.sun.deploy.security.BlockedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestControllerBlockHandlerClass {

    /**
     * 如果被保护的资源限流或者降级了，就会抛BlockedException并进入该方法
     * @param s 参数必须跟资源方法一样
     * @param e
     * @return 返回值也应该一样
     */
    public static String block(String s, BlockedException e){
        log.warn("限流，或者降级了", e);
        return "限流。或者降级了";
    }
}
