package org.example.taskmanger.mapper;

import org.example.taskmanger.database.dto.CategoryReadDto;
import org.example.taskmanger.database.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements Mapper<Category, CategoryReadDto> {

    @Override
    public CategoryReadDto map(Category object) {

        CategoryReadDto dto = new CategoryReadDto();
        dto.setId(object.getId());
        dto.setName(object.getName());
        return dto;
    }
}