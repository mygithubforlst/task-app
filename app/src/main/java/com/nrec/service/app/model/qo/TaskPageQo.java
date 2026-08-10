package com.nrec.service.app.model.qo;

import com.nrec.base.common.model.PageBean;
import com.nrec.service.app.model.TaskStatusCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.sf.oval.constraint.CheckWith;

/**
 * 任务分页查询请求：继承脚手架 PageBean，额外增加状态/分类/标题关键字过滤。
 * 关键：Controller 只接收分页与过滤条件，查询范围（user_id）由服务端强制固定为当前用户。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel("任务分页查询请求")
public class TaskPageQo extends PageBean {

    @CheckWith(value = TaskStatusCheck.class, message = "任务状态必须为 0、1 或 2")
    @ApiModelProperty(value = "任务状态过滤：0-待办 1-进行中 2-已完成")
    private String status;

    @ApiModelProperty(value = "分类ID过滤")
    private String categoryId;

    @ApiModelProperty(value = "标题关键字过滤")
    private String keyword;
}
