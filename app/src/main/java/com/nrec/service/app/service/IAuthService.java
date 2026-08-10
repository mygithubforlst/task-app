package com.nrec.service.app.service;

import com.nrec.service.app.model.dto.LoginDto;
import com.nrec.service.app.model.dto.UserDto;
import com.nrec.service.app.model.qo.LoginQo;
import com.nrec.service.app.model.qo.RegisterQo;

/**
 * 认证服务：注册、登录。
 */
public interface IAuthService {

    /**
     * 用户注册：用户名唯一校验 + BCrypt 加密存储。
     *
     * @param qo 注册请求
     * @return 用户信息（不含密码）
     */
    UserDto register(RegisterQo qo);

    /**
     * 用户登录：校验账号密码，返回 JWT 与用户信息。
     *
     * @param qo 登录请求
     * @return 登录结果（token + 过期时间 + 用户信息）
     */
    LoginDto login(LoginQo qo);
}
