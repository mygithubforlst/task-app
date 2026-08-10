package com.nrec.service.app.controller;

import com.nrec.base.common.exception.ServiceException;
import com.nrec.base.common.model.TablePage;
import com.nrec.service.app.BaseWebTest;
import com.nrec.service.app.common.BizCode;
import com.nrec.service.app.model.dto.TaskDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 任务管理接口测试：覆盖 正常（分页/详情/创建/更新/状态/删除）、
 * 异常（参数校验失败、状态非法）、越权（访问/删除他人任务 -> 服务抛 NOT_FOUND）以及无 Token 401。
 */
public class TaskControllerTest extends BaseWebTest {

    @Test
    void page_success() throws Exception {
        TaskDto dto = new TaskDto().setId("t1").setTitle("任务1");
        when(taskService.pageTasks(any()))
                .thenReturn(new TablePage<>(Collections.singletonList(dto), 1L));

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/page")
                .header("Authorization", bearer("u1", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"currentPage\":1,\"pageSize\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void detail_success() throws Exception {
        when(taskService.detail(anyString())).thenReturn(new TaskDto().setId("t1").setTitle("任务1"));
        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/t1")
                .header("Authorization", bearer("u1", "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("t1"));
    }

    @Test
    void create_success() throws Exception {
        when(taskService.create(any())).thenReturn(new TaskDto().setId("t1").setTitle("新任务"));
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .header("Authorization", bearer("u1", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"新任务\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("新任务"));
    }

    @Test
    void update_success() throws Exception {
        when(taskService.update(anyString(), any())).thenReturn(new TaskDto().setId("t1").setTitle("改后"));
        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/t1")
                .header("Authorization", bearer("u1", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"改后\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("改后"));
    }

    @Test
    void updateStatus_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/t1/status")
                .header("Authorization", bearer("u1", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("状态更新成功"));
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/t1")
                .header("Authorization", bearer("u1", "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    void create_titleTooLong_validationError() throws Exception {
        String longTitle = repeat("x", 201);
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .header("Authorization", bearer("u1", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"" + longTitle + "\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    void updateStatus_invalidStatus_validationError() throws Exception {
        // 状态 "9" 不合法，ValidationUtils 在到达服务前即拦截 -> 500
        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/t1/status")
                .header("Authorization", bearer("u1", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"9\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    void detail_crossUser_forbidden() throws Exception {
        // 模拟「用户 u1 试图访问属于他人(u2)的任务」：服务抛 NOT_FOUND
        when(taskService.detail(anyString()))
                .thenThrow(new ServiceException(BizCode.NOT_FOUND, "任务不存在"));
        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/t-other")
                .header("Authorization", bearer("u1", "alice")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").value("任务不存在"));
    }

    @Test
    void delete_crossUser_forbidden() throws Exception {
        doThrow(new ServiceException(BizCode.NOT_FOUND, "任务不存在"))
                .when(taskService).delete(anyString());
        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/t-other")
                .header("Authorization", bearer("u1", "alice")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").value("任务不存在"));
    }

    @Test
    void page_withoutToken_401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/page")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.succ").value(false))
                .andExpect(jsonPath("$.code").value("401"));
    }

    private static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
