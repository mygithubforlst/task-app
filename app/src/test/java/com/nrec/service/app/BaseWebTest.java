package com.nrec.service.app;

import com.nrec.service.app.mapper.TaskCategoryMapper;
import com.nrec.service.app.mapper.TaskItemMapper;
import com.nrec.service.app.mapper.TaskUserMapper;
import com.nrec.service.app.security.JwtUtil;
import com.nrec.service.app.service.IAuthService;
import com.nrec.service.app.service.ICategoryService;
import com.nrec.service.app.service.ITaskService;
import com.nrec.service.app.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 测试基座（Web / 安全层）。
 *
 * <p>启动完整 Spring 上下文（test profile），用 H2 内存库替代 MySQL 使上下文无需本地数据库即可启动；
 * 四个 Service 与三个 Mapper 均以 {@code @MockBean} 替代（不执行任何真实 SQL），从而把测试焦点收敛到
 * 控制器 → ValidationUtils 校验 → Spring Security / JWT 过滤器 → ExceptionInterceptor 异常拦截 这条链路。
 * 通过应用自带的 {@link JwtUtil} 生成真实 Bearer Token，从而覆盖 JWT 鉴权过滤器链路（有效 / 缺失 / 非法）。</p>
 *
 * <p>所有子类继承本类即可获得 {@link #mockMvc} 与 {@link #bearer(String, String)} 助手。
 * 注意：本基类不修改任何生产代码，仅作用于 test classpath。</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseWebTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtUtil jwtUtil;

    @MockBean
    protected IAuthService authService;
    @MockBean
    protected ITaskService taskService;
    @MockBean
    protected ICategoryService categoryService;
    @MockBean
    protected IUserService userService;
    @MockBean
    protected TaskItemMapper taskItemMapper;
    @MockBean
    protected TaskCategoryMapper taskCategoryMapper;
    @MockBean
    protected TaskUserMapper taskUserMapper;

    /** 生成带指定用户身份的有效 Bearer Token（含 "Bearer " 前缀），供受保护接口鉴权使用。 */
    protected String bearer(String userId, String username) {
        return "Bearer " + jwtUtil.generateToken(userId, username);
    }
}
