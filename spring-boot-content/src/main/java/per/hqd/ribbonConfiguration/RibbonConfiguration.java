package per.hqd.ribbonConfiguration;

import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import per.hqd.contentcenter.configuration.NacosSameClusterWeightedRule;

@Configuration//该注解用于定义配置类，可替换xml配置文件，该注解下还会有多个@Bean，初始化spring容器
//即 把xml中的<Bean>生成方式 改成 注解的生成方式
public class RibbonConfiguration {//该类位于springboot启动类同级之外的目的是，防止上下文重叠

    @Bean// ribbonRule 相当于 <Bean id>
    public IRule ribbonRule(){
        return new NacosSameClusterWeightedRule();
    }

    /*@Bean//自定义客户端负载均衡
    public IPing ping(){
        return new PingUrl();
    }*/
}
