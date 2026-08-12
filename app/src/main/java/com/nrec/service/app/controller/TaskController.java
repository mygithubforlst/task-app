package com.nrec.service.app.controller;

import com.nrec.base.common.model.Result;
import com.nrec.base.common.model.TablePage;
import com.nrec.service.app.common.ValidationUtils;
import com.nrec.service.app.model.dto.TaskDto;
import com.nrec.service.app.model.dto.TaskStatisticsDto;
import com.nrec.service.app.model.qo.TaskCreateQo;
import com.nrec.service.app.model.qo.TaskPageQo;
import com.nrec.service.app.model.qo.TaskStatusQo;
import com.nrec.service.app.model.qo.TaskUpdateQo;
import com.nrec.service.app.service.ITaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "任务管理")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskService taskService;

    @ApiOperation("任务分页查询")
    @PostMapping("/page")
    public Result<TablePage<TaskDto>> page(@RequestBody TaskPageQo qo) {
        ValidationUtils.validate(qo);
        return Result.buildSuccess(taskService.pageTasks(qo), "查询成功");
    }

    @ApiOperation("任务详情")
    @GetMapping("/{id}")
    public Result<TaskDto> detail(@PathVariable("id") String id) {
        return Result.buildSuccess(taskService.detail(id), "查询成功");
    }

    @ApiOperation("创建任务")
    @PostMapping
    public Result<TaskDto> create(@RequestBody TaskCreateQo qo) {
        ValidationUtils.validate(qo);
        return Result.buildSuccess(taskService.create(qo), "创建成功");
    }

    @ApiOperation("更新任务")
    @PutMapping("/{id}")
    public Result<TaskDto> update(@PathVariable("id") String id, @RequestBody TaskUpdateQo qo) {
        ValidationUtils.validate(qo);
        return Result.buildSuccess(taskService.update(id, qo), "更新成功");
    }

    @ApiOperation("修改任务状态")
    @PutMapping("/{id}/status")
    public Result<Object> updateStatus(@PathVariable("id") String id, @RequestBody TaskStatusQo qo) {
        ValidationUtils.validate(qo);
        taskService.updateStatus(id, qo);
        return Result.buildSuccess(null, "状态更新成功");
    }

    @ApiOperation("删除任务")
    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable("id") String id) {
        taskService.delete(id);
        return Result.buildSuccess(null, "删除成功");
    }

    @ApiOperation("逾期任务列表（due_date 已过且未完成）")
    @GetMapping("/overdue")
    public Result<List<TaskDto>> overdue() {
        return Result.buildSuccess(taskService.listOverdue(), "查询成功");
    }

    @ApiOperation("任务统计（总数 / 待办 / 进行中 / 已完成 / 逾期）")
    @GetMapping("/statistics")
    public Result<TaskStatisticsDto> statistics() {
        return Result.buildSuccess(taskService.statistics(), "查询成功");
    }
}
