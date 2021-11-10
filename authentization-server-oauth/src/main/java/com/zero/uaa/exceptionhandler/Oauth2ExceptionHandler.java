package com.zero.uaa.exceptionhandler;

import com.zero.common.response.CommonReturnType;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author Zero
 * @Date 2021/7/16 12:40
 * @Since 1.8
 * @Description
 **/
@ControllerAdvice
public class Oauth2ExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public CommonReturnType handleOauth2(OAuth2Exception e) {
        return CommonReturnType.fail(e.getMessage());
    }
}
