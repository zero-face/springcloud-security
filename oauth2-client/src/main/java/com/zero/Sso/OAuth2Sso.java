package com.zero.Sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * @Author Zero
 * @Date 2021/7/16 13:52
 * @Since 1.8
 * @Description
 **/
@SpringBootApplication
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2Sso {
    public static void main(String[] args) {
        SpringApplication.run(OAuth2Sso.class, args);
    }
}
