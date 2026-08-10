package com.nrec.service.app.model;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * mapper 父类，注意这个类不能让 mp 扫描
 *
 * @author chenjia
 * @date 2017/11/1
 */


public interface SuperMapper<T> extends BaseMapper<T> {

    // 这里可以放一些公共的方法
}
