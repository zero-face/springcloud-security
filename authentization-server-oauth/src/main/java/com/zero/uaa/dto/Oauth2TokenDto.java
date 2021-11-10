package com.zero.uaa.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author Zero
 * @Date 2021/7/16 12:21
 * @Since 1.8
 * @Description
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class Oauth2TokenDto {

    //令牌
    private String token;

    //刷新令牌
    private String refreshToken;

    //访问令牌前缀
    private String tokenHead;

    //有效时间
    private int expiresIn;

}
