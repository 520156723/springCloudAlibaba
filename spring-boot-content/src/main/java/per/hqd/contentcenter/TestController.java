package per.hqd.contentcenter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import per.hqd.contentcenter.dao.content.ShareMapper;
import per.hqd.contentcenter.domain.entity.content.Share;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {

    private final ShareMapper shareMapper;

    private final DiscoveryClient discoveryClient;

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
}
