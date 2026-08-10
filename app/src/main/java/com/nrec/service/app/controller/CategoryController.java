package com.nrec.service.app.controller;

import com.nrec.base.common.model.Result;
import com.nrec.service.app.common.ValidationUtils;
import com.nrec.service.app.model.dto.CategoryDto;
import com.nrec.service.app.model.qo.CategoryCreateQo;
import com.nrec.service.app.service.ICategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "分类管理")
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @ApiOperation("分类列表（当前用户）")
    @GetMapping
    public Result<List<CategoryDto>> list() {
        return Result.buildSuccess(categoryService.listAll(), "查询成功");
    }

    @ApiOperation("创建分类")
    @PostMapping
    public Result<CategoryDto> create(@RequestBody CategoryCreateQo qo) {
        ValidationUtils.validate(qo);
        return Result.buildSuccess(categoryService.create(qo), "创建成功");
    }

    @ApiOperation("删除分类（其下任务仅置空分类）")
    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable("id") String id) {
        categoryService.delete(id);
        return Result.buildSuccess(null, "删除成功");
    }
}
