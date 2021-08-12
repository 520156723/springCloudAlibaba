package per.hqd.contentcenter.controller.content;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.hqd.contentcenter.auth.CheckAuthorization;
import per.hqd.contentcenter.domain.dto.content.ShareAuditDTO;
import per.hqd.contentcenter.domain.entity.content.Share;
import per.hqd.contentcenter.service.content.ShareService;

@RestController()
@RequestMapping("/admin/shares")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareAdminController {

    private final ShareService shareService;

    @PutMapping("/audit/{id}")
    @CheckAuthorization("admin")//角色认证
    public Share auditById(@PathVariable Integer id, @RequestBody ShareAuditDTO auditDTO) {

        return this.shareService.auditById(id, auditDTO);
    }
}
