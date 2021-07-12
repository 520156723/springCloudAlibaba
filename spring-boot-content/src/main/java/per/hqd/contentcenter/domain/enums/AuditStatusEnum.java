package per.hqd.contentcenter.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditStatusEnum {

    NOT_YET,//待审核
    PASS,//审核通过
    REJECT//审核不通过

}
