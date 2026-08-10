package com.nrec.service.app.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nrec.service.app.entity.TaskItem;
import com.nrec.service.app.model.SuperMapper;
import com.nrec.service.app.model.dto.TaskDto;
import com.nrec.service.app.model.qo.TaskPageQo;
import org.apache.ibatis.annotations.Param;

/**
 * 任务表 Mapper。分页查询通过 XML 自定义 SQL（LEFT JOIN 取分类名 + 强制当前用户隔离），
 * 由 MyBatis-Plus 分页插件自动接管分页与 count。
 */
public interface TaskItemMapper extends SuperMapper<TaskItem> {

    /**
     * 任务分页查询。
     *
     * @param page   分页对象（MP 自动填充 records / total）
     * @param q      查询条件（status / categoryId / keyword），不含 user_id
     * @param userId 当前登录用户 ID（服务端强制注入，前端无法伪造）
     */
    IPage<TaskDto> selectTaskPage(IPage<TaskDto> page,
                                  @Param("q") TaskPageQo q,
                                  @Param("userId") String userId);
}
