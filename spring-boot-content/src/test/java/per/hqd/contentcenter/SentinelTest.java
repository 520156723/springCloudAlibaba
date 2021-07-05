package per.hqd.contentcenter;

import org.springframework.web.client.RestTemplate;

public class SentinelTest {
    //测试限流
    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 10000; i++) {
            String object = restTemplate.getForObject("http://localhost:8010/actuator/sentinel", String.class);
            Thread.sleep(500);
        }
    }
}
