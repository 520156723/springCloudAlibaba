package per.hqd.contentcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.alibaba.sentinel.annotation.SentinelRestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import per.hqd.contentcenter.rocketmq.MySource;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("per.hqd.contentcenter.dao")//扫描该包的接口
@EnableFeignClients//(defaultConfiguration = GlobalFeignConfiguration.class)
@EnableBinding({Source.class, MySource.class})// spring cloud stream
public class ContentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentApplication.class, args);
	}

	// 在spring容器中创建了一个对象，对象id是restTemplate，类型是RestTemplate
	// 等同于 <bean id="restTemplate" class="xxx.RestTemplate">
	@Bean
	@LoadBalanced
	@SentinelRestTemplate
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}
