package com.nrec.service.app.integration;

import com.nrec.service.app.entity.TaskItem;
import com.nrec.service.app.mapper.TaskItemMapper;
import com.nrec.service.app.model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 仓储层集成测试：用真实 H2 内存库（不 mock Mapper）验证 MyBatis-Plus 拦截器真正生效。
 *
 * <p>激活 {@code test + integrationtest} 两个 profile：test 提供 JWT / Swagger 等基础配置，
 * integrationtest 将数据源切换为 H2 并初始化 schema-h2.sql。由此可覆盖：
 * ① 乐观锁（@Version）——基于过期版本号更新时 updateById 影响 0 行（MP 不抛异常，由业务层判断冲突）；
 * ② 逻辑删除（@TableLogic）——deleteById 仅置 deleted=1，物理行保留、业务查询不可见。</p>
 *
 * <p>注意：本测试刻意不继承 BaseWebTest（那会 @MockBean 掉所有 Mapper），以保留真实 SQL 执行。</p>
 */
@SpringBootTest
@ActiveProfiles({"test", "integrationtest"})
class RepositoryLockDeleteTest {

    @Autowired
    private TaskItemMapper taskItemMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("乐观锁：基于过期版本号更新应影响 0 行，且数据不被改写")
    void optimisticLock_shouldReturnZeroRowsWhenVersionStale() {
        // 1) 插入一条任务（version 初值 0）
        TaskItem item = new TaskItem()
                .setId("lock-1")
                .setTitle("乐观锁测试任务")
                .setStatus(TaskStatus.TODO)
                .setUserId("u-lock")
                .setVersion(0L);
        taskItemMapper.insert(item);

        // 2) 模拟「另一事务已提交」把 version 从 0 改为 1
        jdbcTemplate.update("UPDATE task_item SET version = 1 WHERE id = 'lock-1'");

        // 3) 仍用过期版本号(version=0)去更新 -> MP 追加 WHERE version=0 命中 0 行 -> 影响 0 行
        //    注意：必须用「更新前内存中持有旧版本号」的实体，而非重新 selectById
        //    （重新查询会带回已被他人改写的 version=1，从而误判为匹配）。
        TaskItem stale = new TaskItem()
                .setId("lock-1")
                .setTitle("被并发改写过")
                .setVersion(0L);
        int rows = taskItemMapper.updateById(stale);
        assertEquals(0, rows);

        // 4) 数据未被改写，版本仍是被他人改后的 1
        TaskItem after = taskItemMapper.selectById("lock-1");
        assertNotNull(after);
        assertEquals("乐观锁测试任务", after.getTitle());
        assertEquals(Long.valueOf(1L), after.getVersion());
    }

    @Test
    @DisplayName("乐观锁：版本号匹配时更新成功并自增版本")
    void optimisticLock_shouldSucceedWhenVersionMatches() {
        TaskItem item = new TaskItem()
                .setId("lock-2")
                .setTitle("乐观锁正常更新")
                .setStatus(TaskStatus.TODO)
                .setUserId("u-lock")
                .setVersion(0L);
        taskItemMapper.insert(item);

        TaskItem loaded = taskItemMapper.selectById("lock-2");
        loaded.setTitle("改后标题");
        assertDoesNotThrow(() -> taskItemMapper.updateById(loaded));

        TaskItem after = taskItemMapper.selectById("lock-2");
        assertNotNull(after);
        assertEquals("改后标题", after.getTitle());
        assertEquals(Long.valueOf(1L), after.getVersion());
    }

    @Test
    @DisplayName("逻辑删除：deleteById 后业务查询不可见，但物理行保留(deleted=1)")
    void logicalDelete_shouldKeepRowPhysically() {
        TaskItem item = new TaskItem()
                .setId("del-1")
                .setTitle("逻辑删除测试任务")
                .setStatus(TaskStatus.TODO)
                .setUserId("u-del")
                .setVersion(0L);
        taskItemMapper.insert(item);

        // 删除前业务查询可见
        assertNotNull(taskItemMapper.selectById("del-1"));

        // deleteById -> 逻辑删除（UPDATE deleted=1）
        taskItemMapper.deleteById("del-1");

        // 删除后业务查询（自动补 deleted=0）不可见
        assertNull(taskItemMapper.selectById("del-1"));

        // 但物理行仍在，且 deleted=1（用 JdbcTemplate 直查绕过逻辑删除过滤）
        Integer deleted = jdbcTemplate.queryForObject(
                "SELECT deleted FROM task_item WHERE id = 'del-1'", Integer.class);
        assertEquals(Integer.valueOf(1), deleted);
    }
}
