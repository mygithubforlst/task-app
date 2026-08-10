package com.nrec.service.app.controller;

import com.nrec.base.common.exception.ServiceException;
import com.nrec.service.app.BaseWebTest;
import com.nrec.service.app.common.BizCode;
import com.nrec.service.app.model.dto.CategoryDto;
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
 * 分类管理接口测试：覆盖 正常（列表/创建/删除）、异常（分类名重复）、越权（删除他人分类 -> NOT_FOUND）及无 Token 401。
 */
public class CategoryControllerTest extends BaseWebTest {

    @Test
    void list_success() throws Exception {
        when(categoryService.listAll())
                .thenReturn(Collections.singletonList(new CategoryDto().setId("c1").setName("工作")));
        mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                .header("Authorization", bearer("u1", "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void create_success() throws Exception {
        when(categoryService.create(any())).thenReturn(new CategoryDto().setId("c1").setName("工作"));
        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                .header("Authorization", bearer("u1", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"工作\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("工作"));
    }

    @Test
    void create_duplicateName_throws() throws Exception {
        when(categoryService.create(any()))
                .thenThrow(new ServiceException(BizCode.DUPLICATE_CATEGORY, "分类名称已存在"));
        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                .header("Authorization", bearer("u1", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"工作\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").value("分类名称已存在"));
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/c1")
                .header("Authorization", bearer("u1", "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    void delete_crossUser_forbidden() throws Exception {
        // 用户 u1 试图删除属于他人(u2)的分类
        doThrow(new ServiceException(BizCode.NOT_FOUND, "分类不存在"))
                .when(categoryService).delete(anyString());
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/c-other")
                .header("Authorization", bearer("u1", "alice")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").value("分类不存在"));
    }

    @Test
    void list_withoutToken_401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.succ").value(false))
                .andExpect(jsonPath("$.code").value("401"));
    }
}
