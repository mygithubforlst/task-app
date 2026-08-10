package com.nrec.service.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nrec.base.common.exception.ServiceException;
import com.nrec.service.app.common.BizCode;
import com.nrec.service.app.entity.TaskUser;
import com.nrec.service.app.mapper.TaskUserMapper;
import com.nrec.service.app.model.dto.LoginDto;
import com.nrec.service.app.model.dto.UserDto;
import com.nrec.service.app.model.qo.LoginQo;
import com.nrec.service.app.model.qo.RegisterQo;
import com.nrec.service.app.security.JwtUtil;
import com.nrec.service.app.service.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private TaskUserMapper taskUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserDto register(RegisterQo qo) {
        Long count = taskUserMapper.selectCount(
                new QueryWrapper<TaskUser>().eq("username", qo.getUsername()));
        if (count != null && count > 0) {
            throw new ServiceException(BizCode.DUPLICATE_USER, "用户名已存在，请更换用户名");
        }
        TaskUser user = new TaskUser();
        user.setUsername(qo.getUsername());
        // BCrypt 哈希存储，绝不明文
        user.setPassword(passwordEncoder.encode(qo.getPassword()));
        user.setEmail(qo.getEmail());
        user.setEnabled("1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        taskUserMapper.insert(user);

        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        dto.setCreatedTime(user.getCreatedAt());
        return dto;
    }

    @Override
    public LoginDto login(LoginQo qo) {
        TaskUser user = taskUserMapper.selectOne(
                new QueryWrapper<TaskUser>().eq("username", qo.getUsername()));
        if (user == null) {
            throw new ServiceException(BizCode.PARAM_ERROR, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(qo.getPassword(), user.getPassword())) {
            throw new ServiceException(BizCode.PARAM_ERROR, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        long expiresAt = System.currentTimeMillis() + jwtUtil.getExpirationMillis();

        LoginDto loginDto = new LoginDto();
        loginDto.setToken(token);
        loginDto.setExpiresAt(expiresAt);
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setCreatedTime(user.getCreatedAt());
        loginDto.setUser(userDto);
        return loginDto;
    }
}
