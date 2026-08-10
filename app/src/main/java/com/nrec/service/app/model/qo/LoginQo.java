package com.nrec.service.app.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * 用户登录请求。
 */
@Data
@Accessors(chain = true)
@ApiModel("用户登录请求")
public class LoginQo {

    @NotNull(message = "用户名不能缺少")
    @NotEmpty(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotNull(message = "密码不能缺少")
    @NotEmpty(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
