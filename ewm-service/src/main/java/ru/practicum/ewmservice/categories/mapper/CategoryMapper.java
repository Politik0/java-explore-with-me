package ru.practicum.ewmservice.categories.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.categories.dto.CategoryDto;
import ru.practicum.ewmservice.categories.model.Category;

@Component
public class CategoryMapper {
    private final ModelMapper modelMapper;

    public CategoryMapper() {
        modelMapper = new ModelMapper();
    }

    public CategoryDto convertToDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    public Category convertFromDto(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }
}