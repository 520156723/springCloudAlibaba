package per.hqd.contentcenter;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import per.hqd.contentcenter.dao.content.ShareMapper;
import per.hqd.contentcenter.domain.dto.user.UserDTO;
import per.hqd.contentcenter.domain.entity.content.Share;
import per.hqd.contentcenter.feignClient.TestBaiduFeignClient;
import per.hqd.contentcenter.feignClient.TestUserCenterFeignClient;
import per.hqd.contentcenter.service.content.TestService;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {

    private final ShareMapper shareMapper;

    private final DiscoveryClient discoveryClient;

    private final TestUserCenterFeignClient testUserCenterFeignClient;

    private final TestBaiduFeignClient testBaiduFeignClient;

    private final TestService testService;

    @GetMapping("/test")
    public List<Share> test(){
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
     * @return
     */
    @GetMapping("test2")
    public List<ServiceInstance> getInstance(){
       return this.discoveryClient.getInstances("user-center");
    }

    @GetMapping("test-get")
    public UserDTO query(UserDTO userDTO){
        return testUserCenterFeignClient.query(userDTO);
    }

    //@RequestMapping(value = "/test-post", method = RequestMethod.POST)
    @PostMapping("/test-post")
    public UserDTO post(@RequestBody UserDTO userDTO){
        return testUserCenterFeignClient.post(userDTO);
    }

    @GetMapping("/baidu")
    public String index(){
        return this.testBaiduFeignClient.index();
    }

    @GetMapping("test-a")
    public String testA(){
        this.testService.common();
        return "test-a";
    }

    @GetMapping("test-b")
    public String testB(){
        this.testService.common();
        return "test-b";
    }

    @GetMapping("/test-hot")
    @SentinelResource("hot")
    public String testHot(
            @RequestParam(required = false) String a,
            @RequestParam(required = false) String b
    ){
        return a + " " + b;
    }

}
