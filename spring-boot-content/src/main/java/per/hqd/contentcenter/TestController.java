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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import per.hqd.contentcenter.annotation.HqdAop;
import per.hqd.contentcenter.dao.content.ShareMapper;
import per.hqd.contentcenter.domain.dto.user.UserDTO;
import per.hqd.contentcenter.domain.entity.content.Share;
import per.hqd.contentcenter.feignClient.TestBaiduFeignClient;
import per.hqd.contentcenter.feignClient.TestUserCenterFeignClient;
import per.hqd.contentcenter.sentineltest.TestControllerBlockHandlerClass;
import per.hqd.contentcenter.service.content.TestService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RefreshScope
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {

    private final ShareMapper shareMapper;

    private final DiscoveryClient discoveryClient;

    private final TestUserCenterFeignClient testUserCenterFeignClient;

    private final TestBaiduFeignClient testBaiduFeignClient;

    private final TestService testService;

    private final RestTemplate restTemplate;

    private final Source source;

    @GetMapping("/test")
    public List<Share> test() {
        //?????????
        Share share = new Share();
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setTitle("docker");
        share.setAuthor("hqd");
        share.setUserId(4);
        shareMapper.insertSelective(share);
        //???????????????
        List<Share> shares = shareMapper.selectAll();
        return shares;
    }

    /**
     * ???????????????????????????????????????
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

        // ????????????sentinel???????????????????????????test-sentinel-api
        Entry entry = null;
        try {

            entry = SphU.entry(resourceName);
            // ????????????????????????
            if (org.apache.commons.lang3.StringUtils.isBlank(a)) {
                throw new IllegalArgumentException("a????????????");
            }
            return a;
        }
        // ????????????????????????????????????????????????????????????BlockException
        catch (BlockException e) {
            log.warn("????????????????????????", e);
            return "????????????????????????";
        } catch (IllegalArgumentException e2) {
            // ??????IllegalArgumentException?????????????????????????????????...???
            Tracer.trace(e2);
            return "???????????????";
        } finally {
            if (entry != null) {
                // ??????entry
                entry.exit();
            }
            ContextUtil.exit();
        }
    }

    @GetMapping("/test-sentinel-resource")
    @SentinelResource(value = "test1-sentinel-api",
            blockHandler = "block",
            blockHandlerClass = TestControllerBlockHandlerClass.class)//????????????????????????2block????????????????????????
    public String testSentinelResource(@RequestParam(required = false) String a) {
        //????????????????????????
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
        return "??????testAop";
    }

    @GetMapping("/testaop2")
    @HqdAop(name = "testAop2")
    public String testAop2() {
        return "??????testAop";
    }

    /**
     * spring cloud stream??????
     */
    @GetMapping("/test-stream-1")
    public String testStream1() {
        source.output()
                .send(
                        MessageBuilder
                                .withPayload("?????????")
                                .build()
                );
        return "success";
    }

    @GetMapping("/tokenRelay/{userId}")
    public ResponseEntity<UserDTO> tokenRelay(@PathVariable Integer userId, HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Token", request.getHeader("X-Token"));
        return this.restTemplate.exchange(
                "http://user-center/users/{userId}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDTO.class,
                userId
        );
    }

    @Value("${your.configuration: null}")
    private String yourConfiguration;

    @GetMapping("/test-config")
    public String getYourConfiguration(){
        return this.yourConfiguration;
    }
}
