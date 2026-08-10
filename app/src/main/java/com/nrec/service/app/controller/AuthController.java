package com.nrec.service.app.controller;

import com.nrec.base.common.model.Result;
import com.nrec.service.app.common.ValidationUtils;
import com.nrec.service.app.model.dto.LoginDto;
import com.nrec.service.app.model.dto.UserDto;
import com.nrec.service.app.model.qo.LoginQo;
import com.nrec.service.app.model.qo.RegisterQo;
import com.nrec.service.app.service.IAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口：注册、登录（公开，无需 Token）。
 */
@Slf4j
@RestController
@Api(tags = "认证管理")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<UserDto> register(@RequestBody RegisterQo qo) {
        ValidationUtils.validate(qo);
        UserDto dto = authService.register(qo);
        return Result.buildSuccess(dto, "注册成功");
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginDto> login(@RequestBody LoginQo qo) {
        ValidationUtils.validate(qo);
        LoginDto dto = authService.login(qo);
        return Result.buildSuccess(dto, "登录成功");
    }
}
