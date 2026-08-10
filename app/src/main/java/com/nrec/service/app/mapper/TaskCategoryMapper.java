package com.nrec.service.app.mapper;

import com.nrec.service.app.entity.TaskCategory;
import com.nrec.service.app.model.SuperMapper;

/**
 * 分类表 Mapper。增删改查与归属校验均复用 BaseMapper / ServiceImpl。
 */
public interface TaskCategoryMapper extends SuperMapper<TaskCategory> {
}
