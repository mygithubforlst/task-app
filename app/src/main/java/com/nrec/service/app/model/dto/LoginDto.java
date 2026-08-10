package com.nrec.service.app.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录响应：返回 JWT、过期时间与用户信息（不含密码）。
 */
@Data
@Accessors(chain = true)
@ApiModel("登录响应")
public class LoginDto {

    @ApiModelProperty(value = "JWT 令牌")
    private String token;

    @ApiModelProperty(value = "过期时间（毫秒时间戳）")
    private Long expiresAt;

    @ApiModelProperty(value = "用户信息")
    private UserDto user;
}
