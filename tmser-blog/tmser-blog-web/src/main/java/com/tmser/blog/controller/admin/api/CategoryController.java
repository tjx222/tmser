package com.tmser.blog.controller.admin.api;

import com.tmser.blog.model.dto.CategoryDTO;
import com.tmser.blog.model.entity.Category;
import com.tmser.blog.model.params.CategoryParam;
import com.tmser.blog.model.vo.CategoryVO;
import com.tmser.blog.service.CategoryService;
import com.tmser.blog.service.PostCategoryService;
import com.tmser.model.sort.Sort;
import com.tmser.spring.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Category controller.
 *
 * @author johnniang
 * @date 2019-03-21
 */
@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final PostCategoryService postCategoryService;

    public CategoryController(CategoryService categoryService,
                              PostCategoryService postCategoryService) {
        this.categoryService = categoryService;
        this.postCategoryService = postCategoryService;
    }

    @GetMapping("{categoryId:\\d+}")
    public CategoryDTO getBy(@PathVariable("categoryId") Integer categoryId) {
        return categoryService.convertTo(categoryService.getById(categoryId));
    }

    @GetMapping
    public List<? extends CategoryDTO> listAll(
            @SortDefault(sort = "priority, ASC") Sort sort,
            @RequestParam(name = "more", required = false, defaultValue = "false") boolean more) {
        if (more) {
            return postCategoryService.listCategoryWithPostCountDto(sort, true);
        }

        return categoryService.convertTo(categoryService.listAll(sort, true));
    }

    @GetMapping("tree_view")
    public List<CategoryVO> listAsTree(
            @SortDefault(sort = "priority, ASC") Sort sort) {
        return categoryService.listAsTree(sort);
    }

    @PostMapping
    public CategoryDTO createBy(@RequestBody @Valid CategoryParam categoryParam) {
        // Convert to category
        Category category = categoryParam.convertTo();

        // Save it
        return categoryService.convertTo(categoryService.create(category));
    }

    @PutMapping("{categoryId:\\d+}")
    public CategoryDTO updateBy(@PathVariable("categoryId") Integer categoryId,
                                @RequestBody @Valid CategoryParam categoryParam
    ) {
        Category categoryToUpdate = categoryService.getById(categoryId);
        categoryParam.update(categoryToUpdate);
        return categoryService.convertTo(categoryService.update(categoryToUpdate));
    }

    @PutMapping("/batch")
    public List<CategoryDTO> updateBatchBy(@RequestBody List<@Valid CategoryParam> categoryParams) {
        List<Category> categoriesToUpdate = categoryParams.stream()
                .filter(categoryParam -> Objects.nonNull(categoryParam.getId()))
                .map(categoryParam -> {
                    Category categoryToUpdate = categoryService.getById(categoryParam.getId());
                    categoryParam.update(categoryToUpdate);
                    return categoryToUpdate;
                })
                .collect(Collectors.toList());
        return categoryService.convertTo(categoryService.updateInBatch(categoriesToUpdate));
    }

    @DeleteMapping("{categoryId:\\d+}")
    public void deletePermanently(@PathVariable("categoryId") Integer categoryId) {
        categoryService.removeCategoryAndPostCategoryBy(categoryId);
    }
}
