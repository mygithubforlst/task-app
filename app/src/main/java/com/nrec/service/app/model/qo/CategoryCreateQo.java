package com.nrec.service.app.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * 创建分类请求。
 */
@Data
@Accessors(chain = true)
@ApiModel("创建分类请求")
public class CategoryCreateQo {

    @NotNull(message = "分类名称不能缺少")
    @NotEmpty(message = "分类名称不能为空")
    @Length(max = 50, message = "分类名称不能超过50个字符")
    @ApiModelProperty(value = "分类名称", required = true)
    private String name;
}
