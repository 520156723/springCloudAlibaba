package per.hqd.contentcenter.configuration;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;

@Slf4j
public class NacosWeightedRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        //读取配置文件并初始化Nacosweightedrule，一般留空
    }

    @Override
    public Server choose(Object key) {
        try {
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();//Ribbon的入口
            log.info("lb = {}", loadBalancer);
            String name = loadBalancer.getName();//想要请求的微服务名称
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();//拿到服务发现相关的api
            //nacos client自动通过基于权重的负载均衡算法。给我们选择一个实例
            Instance instance = namingService.selectOneHealthyInstance(name);
            log.info("选择的服务端口：{}, instance: {}", instance.getPort(), instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            return null;
        }
    }
}
