package com.nrec.service.common.properties;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 类说明
 * swagger 配置
 * @author zhuyf
 * @date 2020/10/13
 */
@Component
@ConfigurationProperties(prefix = "nrec.swagger")
@RefreshScope
@Getter
@Setter
public class Swagger2Properties {

    @ApiModelProperty("在线文档作者")
    private String author="";

    @ApiModelProperty("作者联系人邮箱")
    private String email="";

    @ApiModelProperty("接口简介")
    private String description="swagger在线接口文档";

    @ApiModelProperty("扫描基础包路径，一般指向 返回实体和控制层")
    private String basePackage="com.nrec.service.app.controller";

    @ApiModelProperty("基础包路径分割符")
    private String split=";";
}
