package com.nrec.service.app.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * 用户注册请求。
 */
@Data
@Accessors(chain = true)
@ApiModel("用户注册请求")
public class RegisterQo {

    @NotNull(message = "用户名不能缺少")
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 3, max = 20, message = "用户名长度必须为3-20位")
    @MatchPattern(pattern = "^[A-Za-z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotNull(message = "密码不能缺少")
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 50, message = "密码长度必须为6-50位")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @Length(max = 100, message = "邮箱长度不能超过100")
    @ApiModelProperty(value = "邮箱（可空）")
    private String email;
}
