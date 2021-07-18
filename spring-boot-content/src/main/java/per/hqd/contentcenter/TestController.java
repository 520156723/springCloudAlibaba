package per.hqd.contentcenter;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import per.hqd.contentcenter.annotation.HqdAop;
import per.hqd.contentcenter.dao.content.ShareMapper;
import per.hqd.contentcenter.domain.dto.user.UserDTO;
import per.hqd.contentcenter.domain.entity.content.Share;
import per.hqd.contentcenter.feignClient.TestBaiduFeignClient;
import per.hqd.contentcenter.feignClient.TestUserCenterFeignClient;
import per.hqd.contentcenter.rocketmq.MySource;
import per.hqd.contentcenter.sentineltest.TestControllerBlockHandlerClass;
import per.hqd.contentcenter.service.content.TestService;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {

    private final ShareMapper shareMapper;

    private final DiscoveryClient discoveryClient;

    private final TestUserCenterFeignClient testUserCenterFeignClient;

    private final TestBaiduFeignClient testBaiduFeignClient;

    private final TestService testService;

    private final RestTemplate restTemplate;

    private final Source source;

    private final MySource mySource;

    @GetMapping("/test")
    public List<Share> test() {
        //做插入
        Share share = new Share();
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setTitle("docker");
        share.setAuthor("hqd");
        share.setUserId(4);
        shareMapper.insertSelective(share);
        //查询服务器
        List<Share> shares = shareMapper.selectAll();
        return shares;
    }

    /**
     * 查詢指定服务的所有实例信息
     *
     * @return
     */
    @GetMapping("test2")
    public List<ServiceInstance> getInstance() {
        return this.discoveryClient.getInstances("user-center");
    }

    @GetMapping("test-get")
    public UserDTO query(UserDTO userDTO) {
        return testUserCenterFeignClient.query(userDTO);
    }

    //@RequestMapping(value = "/test1-post", method = RequestMethod.POST)
    @PostMapping("/test-post")
    public UserDTO post(@RequestBody UserDTO userDTO) {
        return testUserCenterFeignClient.post(userDTO);
    }

    @GetMapping("/baidu")
    public String index() {
        return this.testBaiduFeignClient.index();
    }

    @GetMapping("test-a")
    public String testA() {
        this.testService.common();
        return "test1-a";
    }

    @GetMapping("test-b")
    public String testB() {
        this.testService.common();
        return "test1-b";
    }

    @GetMapping("/test-hot")
    @SentinelResource("hot")
    public String testHot(
            @RequestParam(required = false) String a,
            @RequestParam(required = false) String b
    ) {
        return a + " " + b;
    }

    @GetMapping("/test-sentinel-api")
    public String testSentinelAPI(
            @RequestParam(required = false) String a) {

        String resourceName = "test1-sentinel-api";
        ContextUtil.enter(resourceName, "test1-wfw");

        // 定义一个sentinel保护的资源，名称是test-sentinel-api
        Entry entry = null;
        try {

            entry = SphU.entry(resourceName);
            // 被保护的业务逻辑
            if (org.apache.commons.lang3.StringUtils.isBlank(a)) {
                throw new IllegalArgumentException("a不能为空");
            }
            return a;
        }
        // 如果被保护的资源被限流或者降级了，就会抛BlockException
        catch (BlockException e) {
            log.warn("限流，或者降级了", e);
            return "限流，或者降级了";
        } catch (IllegalArgumentException e2) {
            // 统计IllegalArgumentException【发生的次数、发生占比...】
            Tracer.trace(e2);
            return "参数非法！";
        } finally {
            if (entry != null) {
                // 退出entry
                entry.exit();
            }
            ContextUtil.exit();
        }
    }

    @GetMapping("/test-sentinel-resource")
    @SentinelResource(value = "test1-sentinel-api",
            blockHandler = "block",
            blockHandlerClass = TestControllerBlockHandlerClass.class)//定义保护的资源，2block方法用于捕捉异常
    public String testSentinelResource(@RequestParam(required = false) String a) {
        //被保护的业务逻辑
        if (StringUtils.isBlank(a)) {
            throw new IllegalArgumentException("a cannot be blank");
        }
        return a;
    }


    @GetMapping("/test/restTemplate-sentinel/{userId}")
    public UserDTO test(@PathVariable Integer userId) {
        return this.restTemplate.getForObject(
                "http://user-center/users/{userId}",
                UserDTO.class,
                userId
        );
    }


    @GetMapping("/testaop1")
    public String testAop1() {
        return "执行testAop";
    }

    @GetMapping("/testaop2")
    @HqdAop(name = "testAop2")
    public String testAop2() {
        return "执行testAop";
    }

    /**
     * spring cloud stream测试
     */
    @GetMapping("/test-stream-1")
    public String testStream1() {
        source.output()
                .send(
                        MessageBuilder
                                .withPayload("消息体")
                                .build()
                );
        return "success";
    }

    @GetMapping("/test-stream-2")
    public String testStream2() {
        mySource.output()
                .send(
                        MessageBuilder
                                .withPayload("my消息体")
                                .build()
                );
        return "success2";
    }
}
