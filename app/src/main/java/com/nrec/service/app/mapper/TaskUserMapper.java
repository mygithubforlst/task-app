package com.nrec.service.app.mapper;

import com.nrec.service.app.entity.TaskUser;
import com.nrec.service.app.model.SuperMapper;
import org.springframework.stereotype.Repository;

/**
 * 用户表 task_user 的 Mapper（由 @MapperScan("com.nrec.service.*.mapper") 自动扫描）。
 */
@Repository
public interface TaskUserMapper extends SuperMapper<TaskUser> {
}
