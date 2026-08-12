package com.nrec.service.app.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 任务响应。不直接暴露 userId，减少前端伪造归属的可能。
 */
@Data
@Accessors(chain = true)
@ApiModel("任务信息")
public class TaskDto {

    @ApiModelProperty(value = "任务ID")
    private String id;

    @ApiModelProperty(value = "任务标题")
    private String title;

    @ApiModelProperty(value = "任务描述")
    private String description;

    @ApiModelProperty(value = "任务状态：0-待办 1-进行中 2-已完成")
    private String status;

    @ApiModelProperty(value = "截止时间")
    private LocalDateTime dueDate;

    @ApiModelProperty(value = "所属分类ID")
    private String categoryId;

    @ApiModelProperty(value = "所属分类名称")
    private String categoryName;

    @ApiModelProperty(value = "乐观锁版本号（更新时原样回传）")
    private Long version;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedAt;
}
