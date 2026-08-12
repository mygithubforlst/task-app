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
import com.nrec.service.app.security.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

    @Autowired
    protected JwtProperties jwtProperties;

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

    /** 生成已过期（exp 落在过去）的 Bearer Token，用于校验「过期即拒」场景。 */
    protected String expiredBearer(String userId, String username) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .setIssuedAt(new Date(now - 20_000L))
                .setExpiration(new Date(now - 10_000L))
                .signWith(key)
                .compact();
        return "Bearer " + token;
    }

    /** 生成被篡改（payload 中段被改动）的 Bearer Token，用于校验「篡改即拒」场景。 */
    protected String tamperedBearer(String userId, String username) {
        String valid = jwtUtil.generateToken(userId, username);
        String[] parts = valid.split("\\.");
        String payload = parts[1];
        int len = payload.length();
        char last = payload.charAt(len - 1);
        char flipped = (last == 'A') ? 'B' : 'A';
        parts[1] = payload.substring(0, len - 1) + flipped;
        return "Bearer " + String.join(".", parts);
    }
}
