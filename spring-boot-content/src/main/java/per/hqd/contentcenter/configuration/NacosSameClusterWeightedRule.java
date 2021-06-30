package per.hqd.contentcenter.configuration;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {

    @Autowired
    NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        try {
            //拿到配置文件中的集群名称BJ
            String clusterName = nacosDiscoveryProperties.getClusterName();
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();//Ribbon入口
            //拿到要请求的的微服务名
            String serviceName = loadBalancer.getName();
            //拿到服务发现相关的api
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            //只拿指定服务名的健康的所有实例
            List<Instance> instances = namingService.selectInstances(serviceName, true);
            //过滤同集群实例
            List<Instance> sameClusterInstances = instances.stream()
                    .filter(instance -> Objects.equals(clusterName, instance.getClusterName()))
                    .collect(Collectors.toList());
            //如果同集群实例挂了用其他集群实例
            List<Instance> instanceToBeChoose = new ArrayList<>();
            if (CollectionUtils.isEmpty(sameClusterInstances)) {
                instanceToBeChoose = instances;
                log.warn("发生了跨集群的调用，name = {}", serviceName);
            }else {
                instanceToBeChoose = sameClusterInstances;
            }
            //基于权重的负载均衡算法选择一个实例
            Instance instance = ExtendBalancer.myGetHostByRandomWeight(instanceToBeChoose);
            log.info("选择的实例是 port = {}， instance = {}", instance.getPort(), instance);
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("发生异常了", e);
            return null;
        }
    }
}

class ExtendBalancer extends Balancer{

    public static Instance myGetHostByRandomWeight(List<Instance> hosts){
        return getHostByRandomWeight(hosts);
    }
}