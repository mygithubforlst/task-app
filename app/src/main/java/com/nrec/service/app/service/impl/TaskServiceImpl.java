package com.nrec.service.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nrec.base.common.exception.ServiceException;
import com.nrec.base.common.model.TablePage;
import com.nrec.service.app.common.BizCode;
import com.nrec.service.app.entity.TaskCategory;
import com.nrec.service.app.entity.TaskItem;
import com.nrec.service.app.mapper.TaskCategoryMapper;
import com.nrec.service.app.mapper.TaskItemMapper;
import com.nrec.service.app.model.TaskStatus;
import com.nrec.service.app.model.dto.TaskDto;
import com.nrec.service.app.model.qo.TaskCreateQo;
import com.nrec.service.app.model.qo.TaskPageQo;
import com.nrec.service.app.model.qo.TaskStatusQo;
import com.nrec.service.app.model.qo.TaskUpdateQo;
import com.nrec.service.app.security.SecurityContextUtil;
import com.nrec.service.app.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private TaskItemMapper taskItemMapper;

    @Autowired
    private TaskCategoryMapper taskCategoryMapper;

    @Override
    public TablePage<TaskDto> pageTasks(TaskPageQo qo) {
        String userId = currentUserId();
        long current = qo.getCurrentPage() <= 0 ? 1 : qo.getCurrentPage();
        long size = qo.getPageSize() <= 0 ? 15 : qo.getPageSize();
        IPage<TaskDto> page = new Page<>(current, size);
        page = taskItemMapper.selectTaskPage(page, qo, userId);
        return new TablePage<>(page.getRecords(), page.getTotal());
    }

    @Override
    public TaskDto detail(String id) {
        String userId = currentUserId();
        TaskItem item = getOwned(id, userId);
        TaskDto dto = toDto(item);
        fillCategoryName(dto, item.getCategoryId());
        return dto;
    }

    @Override
    public TaskDto create(TaskCreateQo qo) {
        String userId = currentUserId();
        TaskItem item = new TaskItem();
        item.setTitle(qo.getTitle());
        item.setDescription(qo.getDescription());
        // 创建时状态固定为「待办 0」；QO 不含 status 字段
        item.setStatus(TaskStatus.TODO);
        item.setDueDate(qo.getDueDate());
        item.setUserId(userId);
        if (qo.getCategoryId() != null && !qo.getCategoryId().isEmpty()) {
            requireOwnedCategory(qo.getCategoryId(), userId);
            item.setCategoryId(qo.getCategoryId());
        }
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        taskItemMapper.insert(item);
        TaskDto dto = toDto(item);
        fillCategoryName(dto, item.getCategoryId());
        return dto;
    }

    @Override
    public TaskDto update(String id, TaskUpdateQo qo) {
        String userId = currentUserId();
        TaskItem item = getOwned(id, userId);
        if (qo.getTitle() != null) {
            item.setTitle(qo.getTitle());
        }
        if (qo.getDescription() != null) {
            item.setDescription(qo.getDescription());
        }
        if (qo.getDueDate() != null) {
            item.setDueDate(qo.getDueDate());
        }
        if (qo.getCategoryId() != null) {
            if (qo.getCategoryId().isEmpty()) {
                item.setCategoryId(null);
            } else {
                requireOwnedCategory(qo.getCategoryId(), userId);
                item.setCategoryId(qo.getCategoryId());
            }
        }
        item.setUpdatedAt(LocalDateTime.now());
        taskItemMapper.updateById(item);
        return detail(id);
    }

    @Override
    public void updateStatus(String id, TaskStatusQo qo) {
        String userId = currentUserId();
        if (!TaskStatus.isValid(qo.getStatus())) {
            throw new ServiceException(BizCode.INVALID_STATUS, "任务状态非法");
        }
        TaskItem item = getOwned(id, userId);
        item.setStatus(qo.getStatus());
        item.setUpdatedAt(LocalDateTime.now());
        taskItemMapper.updateById(item);
    }

    @Override
    public void delete(String id) {
        String userId = currentUserId();
        getOwned(id, userId); // 校验归属，查不到直接抛「数据不存在」
        taskItemMapper.deleteById(id);
    }

    // ===== 内部工具方法 =====

    private String currentUserId() {
        String userId = SecurityContextUtil.getCurrentUserId();
        if (userId == null) {
            throw new ServiceException(BizCode.PARAM_ERROR, "未登录或登录状态已失效");
        }
        return userId;
    }

    /** 查询并校验任务归属当前用户，查不到抛「数据不存在」 */
    private TaskItem getOwned(String id, String userId) {
        TaskItem item = taskItemMapper.selectOne(
                new QueryWrapper<TaskItem>().eq("id", id).eq("user_id", userId));
        if (item == null) {
            throw new ServiceException(BizCode.NOT_FOUND, "任务不存在");
        }
        return item;
    }

    /** 校验分类归属于当前用户，否则抛「分类不存在」 */
    private void requireOwnedCategory(String categoryId, String userId) {
        TaskCategory cat = taskCategoryMapper.selectOne(
                new QueryWrapper<TaskCategory>().eq("id", categoryId).eq("user_id", userId));
        if (cat == null) {
            throw new ServiceException(BizCode.NOT_FOUND, "分类不存在");
        }
    }

    private void fillCategoryName(TaskDto dto, String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) {
            return;
        }
        TaskCategory cat = taskCategoryMapper.selectById(categoryId);
        if (cat != null) {
            dto.setCategoryName(cat.getName());
        }
    }

    private TaskDto toDto(TaskItem item) {
        TaskDto dto = new TaskDto();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }
}
