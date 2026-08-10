package com.nrec.service.app.service.impl;

import com.nrec.base.common.exception.ServiceException;
import com.nrec.service.app.common.BizCode;
import com.nrec.service.app.entity.TaskUser;
import com.nrec.service.app.mapper.TaskUserMapper;
import com.nrec.service.app.model.dto.UserDto;
import com.nrec.service.app.model.qo.PasswordUpdateQo;
import com.nrec.service.app.security.SecurityContextUtil;
import com.nrec.service.app.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private TaskUserMapper taskUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto getCurrentUser() {
        String userId = SecurityContextUtil.getCurrentUserId();
        if (userId == null) {
            throw new ServiceException(BizCode.PARAM_ERROR, "未登录或登录已过期");
        }
        TaskUser user = taskUserMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(BizCode.NOT_FOUND, "用户不存在");
        }
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        dto.setCreatedTime(user.getCreatedAt());
        return dto;
    }

    @Override
    public void changePassword(PasswordUpdateQo qo) {
        String userId = SecurityContextUtil.getCurrentUserId();
        if (userId == null) {
            throw new ServiceException(BizCode.PARAM_ERROR, "未登录或登录已过期");
        }
        TaskUser user = taskUserMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(BizCode.NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(qo.getOldPassword(), user.getPassword())) {
            throw new ServiceException(BizCode.PWD_WRONG, "原密码错误");
        }
        // BCrypt 哈希存储新密码
        user.setPassword(passwordEncoder.encode(qo.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        taskUserMapper.updateById(user);
    }
}
