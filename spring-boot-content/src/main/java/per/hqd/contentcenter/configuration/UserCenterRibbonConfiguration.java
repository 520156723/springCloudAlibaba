package per.hqd.contentcenter.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;
import per.hqd.ribbonConfiguration.RibbonConfiguration;

@Configuration
@RibbonClient(name = "user-center", configuration = RibbonConfiguration.class)//负载均衡哪个服务名
public class UserCenterRibbonConfiguration {
}
