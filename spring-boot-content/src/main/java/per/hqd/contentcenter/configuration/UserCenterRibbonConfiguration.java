package per.hqd.contentcenter.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import per.hqd.ribbonConfiguration.RibbonConfiguration;

@Configuration
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)//负载均衡哪个服务名
public class UserCenterRibbonConfiguration {
}
