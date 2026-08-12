package com.nrec.service.app.model.qo;

import com.nrec.service.app.model.TaskStatusCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.oval.constraint.CheckWith;
import net.sf.oval.constraint.NotNull;

/**
 * 任务状态修改请求。状态仅允许 "0"、"1"、"2"。
 */
@Data
@Accessors(chain = true)
@ApiModel("任务状态修改请求")
public class TaskStatusQo {

    @NotNull(message = "任务状态不能缺少")
    @CheckWith(value = TaskStatusCheck.class, message = "任务状态必须为 0、1 或 2")
    @ApiModelProperty(value = "任务状态：0-待办 1-进行中 2-已完成", required = true)
    private String status;

    @ApiModelProperty(value = "乐观锁版本号（由详情接口返回，更新时原样回传；不匹配则报冲突）")
    private Long version;
}
