package com.nrec.service.app.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * 修改密码请求（校验旧密码后修改）。
 */
@Data
@Accessors(chain = true)
@ApiModel("修改密码请求")
public class PasswordUpdateQo {

    @NotNull(message = "旧密码不能缺少")
    @NotEmpty(message = "旧密码不能为空")
    @ApiModelProperty(value = "旧密码", required = true)
    private String oldPassword;

    @NotNull(message = "新密码不能缺少")
    @NotEmpty(message = "新密码不能为空")
    @Length(min = 6, max = 50, message = "新密码长度必须为6-50位")
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;
}
