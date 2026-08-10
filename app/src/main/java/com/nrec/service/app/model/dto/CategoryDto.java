package com.nrec.service.app.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 分类响应。
 */
@Data
@Accessors(chain = true)
@ApiModel("分类信息")
public class CategoryDto {

    @ApiModelProperty(value = "分类ID")
    private String id;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;
}
