package com.nrec.service.common.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 实体类父类
 *
 * @author chenjia
 * @date 2017/11/1
 */
public class SuperEntity<T extends Model> extends Model {

    private String id;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
