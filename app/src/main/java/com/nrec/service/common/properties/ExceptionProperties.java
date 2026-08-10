package com.nrec.service.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务异常码处理
 * @author chenjia
 */

@Component
@ConfigurationProperties(prefix = "nrec.exception")
public class ExceptionProperties {

    /** 服务编码前缀*/
    private int codePrefix= 99;

    /** 异常码位数*/
    private int codeLength= 4;

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public int getCodePrefix() {
        return codePrefix;
    }

    public void setCodePrefix(int codePrefix) {
        this.codePrefix = codePrefix;
    }


}
