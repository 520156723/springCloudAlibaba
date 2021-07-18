package per.hqd.contentcenter.rocketmq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 自定义spring cloud stream接口
 */
public interface MySource {

    String MY_OUTPUT = "my-output";//跟application.yml里的stream.bindings.my-output名字一致

    @Output(MY_OUTPUT)
    MessageChannel output();
}
