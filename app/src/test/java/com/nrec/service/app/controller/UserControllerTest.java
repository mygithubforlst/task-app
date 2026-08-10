package com.nrec.service.app.controller;

import com.nrec.base.common.exception.ServiceException;
import com.nrec.service.app.BaseWebTest;
import com.nrec.service.app.common.BizCode;
import com.nrec.service.app.model.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户管理接口测试：覆盖 正常（获取当前用户、修改密码）、异常（原密码错误）及无 Token 401。
 */
public class UserControllerTest extends BaseWebTest {

    @Test
    void me_success() throws Exception {
        when(userService.getCurrentUser()).thenReturn(new UserDto().setId("u1").setUsername("alice"));
        mockMvc.perform(MockMvcRequestBuilders.get("/users/me")
                .header("Authorization", bearer("u1", "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/password")
                .header("Authorization", bearer("u1", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"oldPassword\":\"Test@123456\",\"newPassword\":\"New@123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("密码修改成功"));
    }

    @Test
    void updatePassword_wrongOld_throws() throws Exception {
        doThrow(new ServiceException(BizCode.PWD_WRONG, "原密码错误"))
                .when(userService).changePassword(any());
        mockMvc.perform(MockMvcRequestBuilders.put("/users/password")
                .header("Authorization", bearer("u1", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"oldPassword\":\"wrong\",\"newPassword\":\"New@123456\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").value("原密码错误"));
    }

    @Test
    void me_withoutToken_401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.succ").value(false))
                .andExpect(jsonPath("$.code").value("401"));
    }
}
