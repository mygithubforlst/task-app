package com.nrec.service.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nrec.base.common.exception.ServiceException;
import com.nrec.service.app.common.BizCode;
import com.nrec.service.app.entity.TaskCategory;
import com.nrec.service.app.entity.TaskItem;
import com.nrec.service.app.mapper.TaskCategoryMapper;
import com.nrec.service.app.mapper.TaskItemMapper;
import com.nrec.service.app.model.dto.CategoryDto;
import com.nrec.service.app.model.qo.CategoryCreateQo;
import com.nrec.service.app.security.SecurityContextUtil;
import com.nrec.service.app.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<TaskCategoryMapper, TaskCategory> implements ICategoryService {

    @Autowired
    private TaskItemMapper taskItemMapper;

    @Override
    public List<CategoryDto> listAll() {
        String userId = currentUserId();
        List<TaskCategory> list = baseMapper.selectList(
                new QueryWrapper<TaskCategory>().eq("user_id", userId).orderByDesc("created_at"));
        return list.stream().map(c -> {
            CategoryDto dto = new CategoryDto();
            BeanUtils.copyProperties(c, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public CategoryDto create(CategoryCreateQo qo) {
        String userId = currentUserId();
        Long cnt = baseMapper.selectCount(
                new QueryWrapper<TaskCategory>().eq("user_id", userId).eq("name", qo.getName()));
        if (cnt != null && cnt > 0) {
            throw new ServiceException(BizCode.DUPLICATE_CATEGORY, "分类名称已存在");
        }
        TaskCategory cat = new TaskCategory();
        cat.setName(qo.getName());
        cat.setUserId(userId);
        cat.setCreatedAt(LocalDateTime.now());
        baseMapper.insert(cat);
        CategoryDto dto = new CategoryDto();
        BeanUtils.copyProperties(cat, dto);
        return dto;
    }

    @Override
    public void delete(String id) {
        String userId = currentUserId();
        TaskCategory cat = baseMapper.selectOne(
                new QueryWrapper<TaskCategory>().eq("id", id).eq("user_id", userId));
        if (cat == null) {
            throw new ServiceException(BizCode.NOT_FOUND, "分类不存在");
        }
        baseMapper.deleteById(id);
        // 仅置空该分类下任务的 category_id（符合作业 §4.4），任务本身不删除
        UpdateWrapper<TaskItem> uw = new UpdateWrapper<>();
        uw.eq("user_id", userId).eq("category_id", id);
        uw.set("category_id", null);
        taskItemMapper.update(null, uw);
    }

    private String currentUserId() {
        String userId = SecurityContextUtil.getCurrentUserId();
        if (userId == null) {
            throw new ServiceException(BizCode.PARAM_ERROR, "未登录或登录状态已失效");
        }
        return userId;
    }
}
