package com.zero.uaa.controller;

import com.zero.common.response.CommonReturnType;
import com.zero.uaa.dto.Oauth2TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

/**
 * @Author Zero
 * @Date 2021/7/16 12:16
 * @Since 1.8
 * @Description 自定义申请token认证返回结果
 **/
@RestController
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @PostMapping("/token")
    public CommonReturnType<Oauth2TokenDto> posstToken(Principal principal, @RequestParam Map<String,String> parameters) throws HttpRequestMethodNotSupportedException {
         OAuth2AccessToken auth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
         Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
                .token(auth2AccessToken.getValue())
                .refreshToken(auth2AccessToken.getRefreshToken().getValue())
                .expiresIn(auth2AccessToken.getExpiresIn())
                .tokenHead("Bearer ")
                .build();
        return CommonReturnType.success(oauth2TokenDto);
    }

}
