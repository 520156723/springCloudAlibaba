package per.hqd.usercenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import per.hqd.usercenter.rocketmq.MySink;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("per.hqd.usercenter.dao")//扫描该包的接口
@EnableBinding({Sink.class, MySink.class})// MySink是自定义的一个接口，添加到这里才能注入
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}
