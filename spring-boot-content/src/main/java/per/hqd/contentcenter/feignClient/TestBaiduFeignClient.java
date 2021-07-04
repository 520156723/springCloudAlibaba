package per.hqd.contentcenter.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "baidu", url = "http://www.baidu.com")//这里的name是任意的，但必须得有
public interface TestBaiduFeignClient {

    @GetMapping("")
    String index();
}
