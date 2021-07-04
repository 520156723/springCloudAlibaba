package per.hqd.contentcenter.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

//configuration包下的配置类写法代替的是yml中的配置
//这个类不能加@Configuration注解，因为父子上下文重复问题
public class GlobalFeignConfiguration {

    @Bean
    public Logger.Level level(){
        return Logger.Level.FULL;
    }
}
