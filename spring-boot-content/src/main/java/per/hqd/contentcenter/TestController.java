package per.hqd.contentcenter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/test")
    public List<Share> test(){
        //做插入
        Share share = new Share();
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setTitle("title");
        share.setAuthor("hqd");
        shareMapper.insertSelective(share);
        //查询
        List<Share> shares = shareMapper.selectAll();
        return shares;
    }
}
