package security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author Zero
 * @Date 2021/7/16 12:57
 * @Since 1.8
 * @Description 网关白名单
 **/
@Data
@Component
@EqualsAndHashCode(callSuper = false)
@ConfigurationProperties(prefix = "secure.ignore")
public class IgnoreUrlConfig {
    private List<String> url;

}
