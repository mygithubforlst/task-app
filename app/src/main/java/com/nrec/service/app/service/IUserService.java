package com.nrec.service.app.service;

import com.nrec.service.app.model.dto.UserDto;
import com.nrec.service.app.model.qo.PasswordUpdateQo;

/**
 * 用户服务：当前用户、修改密码（均为本人操作）。
 */
public interface IUserService {

    /**
     * 获取当前登录用户信息（不含密码）。
     *
     * @return 当前用户
     */
    UserDto getCurrentUser();

    /**
     * 修改当前用户密码：校验原密码后更新。
     *
     * @param qo 修改密码请求
     */
    void changePassword(PasswordUpdateQo qo);
}
