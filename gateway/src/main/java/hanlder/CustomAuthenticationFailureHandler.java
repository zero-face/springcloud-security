package hanlder;

import com.alibaba.fastjson.JSONObject;
import com.zero.common.response.CommonReturnType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @Author Zero
 * @Date 2021/7/15 17:33
 * @Since 1.8
 * @Description
 **/
@Component
public class CustomAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return Mono.defer(() -> Mono.just(webFilterExchange.getExchange().getResponse()).flatMap(response -> {
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            CommonReturnType result = null;
            // 账号不存在
            if (exception instanceof UsernameNotFoundException) {
                result = CommonReturnType.fail(exception.getMessage(),"认证失败");
                // 用户名或密码错误
            } else if (exception instanceof BadCredentialsException) {
                result = CommonReturnType.fail("账号或者密码错误","认证失败");
                // 账号已过期
            } else if (exception instanceof AccountExpiredException) {
                result = CommonReturnType.fail("账户过期","认证失败");
                // 账号已被锁定
            } else if (exception instanceof LockedException) {
                result = CommonReturnType.fail("账户被锁定","认证失败");
                // 用户凭证已失效
            } else if (exception instanceof CredentialsExpiredException) {
                result = CommonReturnType.fail("登录失效","认证失败");
                // 账号已被禁用
            } else if (exception instanceof DisabledException) {
                result = CommonReturnType.fail("账户被禁用","认证失败");
            } else if(exception instanceof NonceExpiredException) {
                //异地登录
                result = CommonReturnType.fail("异地登录");
            } else if(exception instanceof SessionAuthenticationException) {
                //session异常
                result = CommonReturnType.fail("session错误");
            } else {
                //其他未知异常
                result = CommonReturnType.fail(exception.getMessage());
            }
            DataBuffer dataBuffer = dataBufferFactory.wrap(JSONObject.toJSONString(result).getBytes());
            return response.writeWith(Mono.just(dataBuffer));
        }));
    }
}
