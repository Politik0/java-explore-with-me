package ru.practicum.ewmservice.categories.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.categories.dto.CategoryDto;
import ru.practicum.ewmservice.categories.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto addNewCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Adding new category {}", categoryDto.getName());
        return categoryService.addNewCategory(categoryDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable long catId) {
        log.info("Deleting category, categoryId={}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable long catId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Updating category, categoryId={}, categoryName={}", catId, categoryDto.getName());
        return categoryService.updateCategory(catId, categoryDto);
    }
}
