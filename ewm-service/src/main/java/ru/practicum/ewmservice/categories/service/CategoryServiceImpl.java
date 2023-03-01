package ru.practicum.ewmservice.categories.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.categories.dto.CategoryDto;
import ru.practicum.ewmservice.categories.mapper.CategoryMapper;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.categories.repository.CategoryRepository;
import ru.practicum.ewmservice.events.repository.EventRepository;
import ru.practicum.ewmservice.exception.DataExistException;
import ru.practicum.ewmservice.exception.InvalidRequestException;
import ru.practicum.ewmservice.exception.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addNewCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.convertFromDto(categoryDto);
        Category categorySaved = saveCategory(category);
        log.info("Category saved, userSaved={}", categorySaved);
        return categoryMapper.convertToDto(categorySaved);
    }

    @Override
    public void deleteCategory(long catId) {
        getCategory(catId);
        long eventsWithCategories = eventRepository.countAllByCategoryId(catId);
        if (eventsWithCategories == 0) {
            categoryRepository.deleteById(catId);
        } else {
            throw  new InvalidRequestException(String.format("The category with id=%d is not empty", catId));
        }
        log.info("Category deleted, catId={}", catId);
    }

    @Override
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        Category category = getCategory(catId);
        category.setName(categoryDto.getName());
        Category categoryUpdated = saveCategory(category);
        log.info("Category updated, userSaved={}", categoryUpdated);
        return categoryMapper.convertToDto(categoryUpdated);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Page<Category> categories = categoryRepository.findAll(PageRequest.of(from / size, size));
        return categories.stream()
                .map(categoryMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        Category category = getCategory(catId);
        log.info("Category got from repository, category={}", category);
        return categoryMapper.convertToDto(category);
    }

    public Category getCategory(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Category with id=%d was not found", catId)));
    }

    private Category saveCategory(Category category) {
        try {
            return categoryRepository.save(category);
        } catch (RuntimeException e) {
            throw new DataExistException(String.format("Category with name=%s exists.", category.getName()));
        }
    }
}
