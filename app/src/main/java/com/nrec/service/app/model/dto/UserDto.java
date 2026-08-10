package com.nrec.service.app.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户信息响应。绝不暴露 password / password 哈希。
 */
@Data
@Accessors(chain = true)
@ApiModel("用户信息")
public class UserDto {

    @ApiModelProperty(value = "用户ID")
    private String id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "状态：0-禁用 1-启用")
    private String enabled;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;
}
