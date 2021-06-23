package per.hqd.contentcenter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j//等同于public static final Logger logger = LoggerFactory.getLogger(LombokTest.class);
public class LombokTest {
    //public static final Logger logger = LoggerFactory.getLogger(LombokTest.class);
    public static void main(String[] args) {
        UserRegisterDTO userRegisterDTO1 = new UserRegisterDTO();
        userRegisterDTO1.setEmail("xxx@yy.com");
        userRegisterDTO1.setPassword("zzz1");
        //等同于
        UserRegisterDTO.UserRegisterDTOBuilder userRegisterDTO2 = UserRegisterDTO.builder()
                .email("xxx@yy.com")
                .password("zzz2");

        log.info("构造出来的dto是 = {}", userRegisterDTO2);//等同于logger.info()
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UserRegisterDTO{
    private String email;
    private String password;
}
