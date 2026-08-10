package com.nrec.service.app.service;

import com.nrec.base.common.model.TablePage;
import com.nrec.service.app.model.dto.TaskDto;
import com.nrec.service.app.model.qo.TaskCreateQo;
import com.nrec.service.app.model.qo.TaskPageQo;
import com.nrec.service.app.model.qo.TaskStatusQo;
import com.nrec.service.app.model.qo.TaskUpdateQo;

/**
 * 任务业务接口。所有方法内部强制以「当前登录用户」为数据边界。
 */
public interface ITaskService {

    TablePage<TaskDto> pageTasks(TaskPageQo qo);

    TaskDto detail(String id);

    TaskDto create(TaskCreateQo qo);

    TaskDto update(String id, TaskUpdateQo qo);

    void updateStatus(String id, TaskStatusQo qo);

    void delete(String id);
}
