package com.zero.common.util;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author Zero
 * @Date 2021/7/13 19:30
 * @Since 1.8
 * @Description
 **/
@Component
public class TokenManager {

    //token有效时长
    private long tokenExpiration= 14 * 60 * 60 * 100;

    private String tokenSignKey = "fjsa;jfewioqr321531jklfdsjafkn321";


    //根据用户名生成token
    public String createToken(String username) {
        String token = Jwts.builder().setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(SignatureAlgorithm.HS256, tokenSignKey).compressWith(CompressionCodecs.GZIP).compact();
        return token;
    }
    //根据token得到用户名
    public String getUserInfoFromToken(String token) {
        String userInfo = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token).getBody().getSubject();
        return userInfo;
    }

}
