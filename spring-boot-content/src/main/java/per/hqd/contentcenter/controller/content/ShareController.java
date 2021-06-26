package per.hqd.contentcenter.controller.content;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.hqd.contentcenter.domain.dto.content.ShareDTO;
import per.hqd.contentcenter.service.content.ShareService;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareController {

    private final ShareService shareService;

    @GetMapping("/{id}")
    public ShareDTO findById(@PathVariable Integer id){
        return this.shareService.findById(id);
    }
}
