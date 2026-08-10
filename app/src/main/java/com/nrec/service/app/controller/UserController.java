package com.nrec.service.app.controller;

import com.nrec.base.common.model.Result;
import com.nrec.service.app.common.ValidationUtils;
import com.nrec.service.app.model.dto.UserDto;
import com.nrec.service.app.model.qo.PasswordUpdateQo;
import com.nrec.service.app.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口：当前用户、修改密码（需携带有效 Token，且均为本人操作）。
 */
@Slf4j
@RestController
@Api(tags = "用户管理")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/me")
    public Result<UserDto> me() {
        UserDto dto = userService.getCurrentUser();
        return Result.buildSuccess(dto, "获取成功");
    }

    @ApiOperation("修改密码（校验原密码）")
    @PutMapping("/password")
    public Result<Boolean> updatePassword(@RequestBody PasswordUpdateQo qo) {
        ValidationUtils.validate(qo);
        userService.changePassword(qo);
        return Result.buildSuccess(true, "密码修改成功");
    }
}
