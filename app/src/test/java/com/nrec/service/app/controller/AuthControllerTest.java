package com.nrec.service.app.controller;

import com.nrec.base.common.exception.ServiceException;
import com.nrec.service.app.BaseWebTest;
import com.nrec.service.app.common.BizCode;
import com.nrec.service.app.model.dto.LoginDto;
import com.nrec.service.app.model.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 认证接口测试：覆盖 正常（注册/登录成功）、异常（参数校验失败、用户名重复、密码错误）。
 * 注册/登录为公开接口，无需 Token。
 */
public class AuthControllerTest extends BaseWebTest {

    @Test
    void register_success() throws Exception {
        UserDto dto = new UserDto().setId("u1").setUsername("alice").setEmail("a@x.com");
        when(authService.register(any())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"Test@123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    void register_invalidUsername_tooShort() throws Exception {
        // 用户名 "ab" 违反 @Length(min=3) 与 @MatchPattern，触发 ValidationUtils -> 500
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"ab\",\"password\":\"Test@123456\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    void register_duplicateUser_throws() throws Exception {
        when(authService.register(any()))
                .thenThrow(new ServiceException(BizCode.DUPLICATE_USER, "用户名已存在"));
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"Test@123456\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").value("用户名已存在"));
    }

    @Test
    void login_success() throws Exception {
        LoginDto dto = new LoginDto().setToken("jwt-token").setExpiresAt(123L)
                .setUser(new UserDto().setId("u1").setUsername("alice"));
        when(authService.login(any())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"Test@123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.user.username").value("alice"));
    }

    @Test
    void login_wrongPassword_throws() throws Exception {
        when(authService.login(any()))
                .thenThrow(new ServiceException(BizCode.PWD_WRONG, "原密码错误"));
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"wrong\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").value("原密码错误"));
    }
}
