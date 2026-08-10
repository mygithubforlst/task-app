package com.nrec.service.app.service;

import com.nrec.service.app.model.dto.CategoryDto;
import com.nrec.service.app.model.qo.CategoryCreateQo;

import java.util.List;

/**
 * 分类业务接口。所有方法内部强制以「当前登录用户」为数据边界。
 */
public interface ICategoryService {

    List<CategoryDto> listAll();

    CategoryDto create(CategoryCreateQo qo);

    void delete(String id);
}
