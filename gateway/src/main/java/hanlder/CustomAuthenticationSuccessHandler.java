package hanlder;

import com.alibaba.fastjson.JSONObject;
import com.zero.common.response.CommonReturnType;
import com.zero.common.util.JWTUtil;
import com.zero.common.util.JwtTokenUtil;
import entity.SecurityUserDetail;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Zero
 * @Date 2021/7/15 17:44
 * @Since 1.8
 * @Description
 **/
@Component
public class CustomAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    //token过期时间
    private int jwtTokenExpired = 60 * 60 * 24;

    //token刷新时间
    private int jwtTokenRefreshExpired =60 * 60 * 72;

    //Sign
    private String SIGN="JFASDOGJREIOJGIROEJGMVL;SDAMFJLK;REWT94528&*（&（9u9043-38654fdasj43925u！@#%……&g";

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return Mono.defer(() -> Mono.just(webFilterExchange.getExchange().getResponse()).flatMap(response -> {
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            // 生成JWT token
            Map<String, Object> map = new HashMap<>(2);
            SecurityUserDetail userDetails = (SecurityUserDetail) authentication.getPrincipal();
            map.put("userId", userDetails.getUserId());
            map.put("username", userDetails.getUsername());
            map.put("roles",userDetails.getAuthorities());
            String token = JwtTokenUtil.generateToken(map, SIGN, jwtTokenExpired);
            String refreshToken = JwtTokenUtil.generateToken(map, userDetails.getUsername(), jwtTokenRefreshExpired);
            Map<String, Object> tokenMap = new HashMap<>(2);
            tokenMap.put("token", token);
            tokenMap.put("refreshToken", refreshToken);
            DataBuffer dataBuffer = dataBufferFactory.wrap(JSONObject.toJSONString(CommonReturnType.success(tokenMap)).getBytes());
            return response.writeWith(Mono.just(dataBuffer));
        }));
    }
}
