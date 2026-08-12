package com.nrec.service.app.model.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.time.LocalDateTime;

/**
 * 更新任务请求（标题、描述、截止时间、分类）。
 */
@Data
@Accessors(chain = true)
@ApiModel("更新任务请求")
public class TaskUpdateQo {

    @NotNull(message = "任务标题不能缺少")
    @NotEmpty(message = "任务标题不能为空")
    @Length(max = 200, message = "任务标题不能超过200个字符")
    @ApiModelProperty(value = "任务标题", required = true)
    private String title;

    @Length(max = 1000, message = "任务描述不能超过1000个字符")
    @ApiModelProperty(value = "任务描述")
    private String description;

    @ApiModelProperty(value = "截止时间")
    private LocalDateTime dueDate;

    @ApiModelProperty(value = "所属分类ID（可空）")
    private String categoryId;

    @ApiModelProperty(value = "乐观锁版本号（由详情接口返回，更新时原样回传；不匹配则报冲突）")
    private Long version;
}
