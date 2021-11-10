package security;

import cn.hutool.core.util.ArrayUtil;
import filter.IgnoreUrlsRemoveJwtFilter;
import hanlder.CustomAccessDeniedHandler;
import hanlder.CustomAuthenticationEntryPoint;
import hanlder.CustomAuthenticationFailureHandler;
import hanlder.CustomAuthenticationSuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.LinkedList;

/**
 * @Author Zero
 * @Date 2021/7/15 20:26
 * @Since 1.8
 * @Description
 **/
@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
public class WebfluxSecurityConfig {

    //鉴权管理器
    @Resource
    private CustomAuthorizationManager defaultAuthorizationManager;

    //用户信息查询
    @Resource
    private UserDetailsServiceImpl userDetailsServiceImpl;

    //认证成功管理器
    @Resource
    private CustomAuthenticationSuccessHandler defaultAuthenticationSuccessHandler;

    //认证失败处理器
    @Resource
    private CustomAuthenticationFailureHandler defaultAuthenticationFailureHandler;

    //认证管理器
    @Resource
    private CustomTokenAuthenticationManager tokenAuthenticationManager;

    //认证信息管理器
    @Resource
    private CustomSecurityContextRepository defaultSecurityContextRepository;

    //未认证处理器
    @Resource
    private CustomAuthenticationEntryPoint defaultAuthenticationEntryPoint;

    //权限拒绝处理器
    @Resource
    private CustomAccessDeniedHandler defaultAccessDeniedHandler;

    //白名单去除token请求头filter
    @Resource
    private IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter;

    //路径白名单
    @Resource
    private IgnoreUrlConfig ignoreUrlConfig;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
        httpSecurity
                // 登录认证处理
                .authenticationManager(reactiveAuthenticationManager())
                .securityContextRepository(defaultSecurityContextRepository);
                //对白名单路径，直接移除jwt
        httpSecurity.addFilterAt(ignoreUrlsRemoveJwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                // 请求拦截处理
                .authorizeExchange()
                .pathMatchers(ArrayUtil.toArray(ignoreUrlConfig.getUrl(), String.class)).permitAll() //白名单配置
            .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().access(defaultAuthorizationManager)//鉴权管理器配置
            .and()
                .exceptionHandling()
                .accessDeniedHandler(defaultAccessDeniedHandler)
                .authenticationEntryPoint(defaultAuthenticationEntryPoint)
            .and()
            .formLogin()
                // 自定义处理
                .authenticationSuccessHandler(defaultAuthenticationSuccessHandler)
                .authenticationFailureHandler(defaultAuthenticationFailureHandler)
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(defaultAuthenticationEntryPoint)
            .and()
                .exceptionHandling()
                .accessDeniedHandler(defaultAccessDeniedHandler)
            .and()
                .csrf().disable();
        httpSecurity
            .oauth2Login()
                .authenticationManager(reactiveAuthenticationManager());
        return httpSecurity.build();

    }

    /**
     * BCrypt密码编码
     */
    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 注册用户信息验证管理器，可按需求添加多个按顺序执行
     */
    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authentication -> {
            // 其他登陆方式 (比如手机号验证码登陆) 可在此设置不得抛出异常或者 Mono.error
            return Mono.empty();
        });
        // 必须放最后不然会优先使用用户名密码校验但是用户名密码不对时此 AuthenticationManager 会调用 Mono.error 造成后面的 AuthenticationManager 不生效
        managers.add(new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsServiceImpl));
        managers.add(tokenAuthenticationManager);
        return new DelegatingReactiveAuthenticationManager(managers);
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
