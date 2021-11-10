package filter;
import cn.hutool.core.util.StrUtil;
import com.zero.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * @Author Zero
 * @Date 2021/7/15 23:14
 * @Since 1.8
 * @Description
 **/
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

            log.info("进入 global filter");
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (StrUtil.isEmpty(token)) {
                return chain.filter(exchange);
            }
            try {
                String realToken = token.replace("Bearer ", "");
                /*JWSObject jwsObject = JWSObject.parse(realToken);
                String userStr = jwsObject.getPayload().toString();
                JSONObject payloadJSON = jwsObject.getPayload().toJSONObject();*/
                Claims claims = JwtTokenUtil.parseJwtRsa256(realToken);
                log.info("客户端id:{}",claims.get("client_id"));
                ServerHttpRequest request = exchange
                        .getRequest()
                        .mutate()
                        .header("user", claims.get("client_id").toString())
                        .build();
                if ("admin".equals(claims.get("client_id").toString())){
                    String userName = claims.get("user_name").toString();
                    URI oldUri = exchange.getRequest().getURI();

                    URI newUri = new URI(oldUri.getPath()+"?"+oldUri.getQuery()+"&userName="+userName);
                    request = exchange
                            .getRequest()
                            .mutate()
                            .uri(newUri)
                            .build();
                }

                exchange = exchange.mutate().request(request).build();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
