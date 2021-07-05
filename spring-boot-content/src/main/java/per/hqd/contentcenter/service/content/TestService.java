package per.hqd.contentcenter.service.content;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestService {

    @SentinelResource("common")
    public String common() {
        log.info("common...");
        return "common";
    }
}
