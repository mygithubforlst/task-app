package com.nrec.service.common.aop;

import com.nrec.base.common.model.Result;
import com.nrec.service.common.properties.ExceptionProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;

/**
 *
 * @author chenjia
 * 异常统一处理
 */

@ControllerAdvice
@Slf4j
@Profile({"dev","test"})
public class ExceptionInterceptor {

    @Resource
    private ExceptionProperties exceptionProperties;

    /**
     * 未知异常处理
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception exception){
        log.error(exception.getMessage(),exception);
        Result<Object> result=Result.createFromException(exception);
        return this.appendExceptionCode(result);
    }

    /**
     * 处理追加编码
     */
    private Result<Object> appendExceptionCode(Result<Object> result){
        String code=result.getCode();
        if(StringUtils.isNotBlank(code)&&code.length()<exceptionProperties.getCodeLength()){
            result.setCode(exceptionProperties.getCodePrefix()+code);
        }
        return result;
    }
}
