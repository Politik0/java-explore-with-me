package ru.practicum.ewmservice.categories.service;

import ru.practicum.ewmservice.categories.dto.CategoryDto;
import ru.practicum.ewmservice.categories.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto addNewCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long catId);

    Category getCategory(long catId);
}
