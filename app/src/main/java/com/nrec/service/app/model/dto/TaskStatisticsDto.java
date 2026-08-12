package com.nrec.service.app.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 任务统计响应：总数 / 待办 / 进行中 / 已完成 / 逾期。
 * 统计范围均限定为「当前登录用户」。
 */
@Data
@Accessors(chain = true)
@ApiModel("任务统计")
public class TaskStatisticsDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务总数")
    private long total;

    @ApiModelProperty(value = "待办数量（status=0）")
    private long pending;

    @ApiModelProperty(value = "进行中数量（status=1）")
    private long inProgress;

    @ApiModelProperty(value = "已完成数量（status=2）")
    private long completed;

    @ApiModelProperty(value = "逾期数量（未完成且 due_date 已过）")
    private long overdue;
}
