package com.zero.common.controller;

import com.zero.common.error.BusinessException;
import com.zero.common.error.EmBusinessError;
import com.zero.common.error.ErrorMsgType;
import com.zero.common.response.CommonReturnType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lmwis
 * @description:基本的controller类，异常处理和部分校验
 * @date 2019-08-28 20:27
 * @Version 1.0
 */
@Slf4j
public class BaseController {

    public static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";

    // 定义exceptionHandler来解决controller层中未被吸收的exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex) {
        ErrorMsgType responseData ;
        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;
            responseData = packErrorCommonReturnType(businessException.getErrorCode()
                    , businessException.getErrorMsg());
        } else if (ex instanceof DataAccessException) { //数据库连接错误
            log.error(ex.getMessage());
            responseData = packErrorCommonReturnType(EmBusinessError.DATARESOURCE_CONNECT_FAILURE.getErrorCode()
                    , EmBusinessError.DATARESOURCE_CONNECT_FAILURE.getErrorMsg());
        } else if (ex instanceof HttpMessageNotReadableException) { // 序列化异常
            log.error(ex.getMessage());
            responseData = packErrorCommonReturnType(EmBusinessError.JSON_SEQUENCE_WRONG.getErrorCode()
                    , EmBusinessError.JSON_SEQUENCE_WRONG.getErrorMsg());
        } else if (ex instanceof MethodArgumentNotValidException) { // 参数校验异常
            log.error(ex.getMessage());
            Map<String,Object> map = new HashMap<>();
            ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors().forEach(fieldError -> map.put(fieldError.getField(),fieldError.getDefaultMessage()));
            responseData = packErrorCommonReturnType(EmBusinessError.PARAMETER_VALIDATION_ERROR.getErrorCode()
                    , map.toString());
        } else {
            log.error(ex.getMessage());
            responseData = packErrorCommonReturnType(EmBusinessError.UNKNOWN_ERROR.getErrorCode()
                    , ex.getMessage());
        }
        log.error("{"+responseData.toString()+"}");
        return CommonReturnType.create(responseData, "fail");
    }

    protected ErrorMsgType packErrorCommonReturnType(int errorCode, String errorMsg){
        return new ErrorMsgType(errorCode,errorMsg);
    }


    /**
     * 判断字符串是否为空
     *
     * @param args 校验参数
     * @return
     * @throws BusinessException
     */
    protected boolean validateNull(String... args) throws BusinessException {
        for (String s : args) {
            if (StringUtils.isEmpty(s)) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
            }
        }
        return true;
    }

    /**
     * 判断字符串是否为空字符串，int or long是否为0，对象是否为null
     *
     * @param args 校验参数
     * @return
     * @throws BusinessException
     */
    protected boolean validateNull(Object... args) throws BusinessException {
        for (Object o : args) {
            if ((o instanceof String && StringUtils.equals(o.toString(), ""))
                    || (o instanceof Integer && new Integer(o.toString()) == 0)
                    || (o instanceof Long && new Long(o.toString()) == 0)
                    || o == null) {
                return false;
            }
        }
        return true;
    }

}
