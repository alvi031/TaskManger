package org.example.taskmanger.service;

import lombok.AllArgsConstructor;
import org.example.taskmanger.database.dto.CategoryReadDto;
import org.example.taskmanger.database.repository.CategoryRepository;
import org.example.taskmanger.exception.CategoryNotFoundException;
import org.example.taskmanger.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryReadDto> findAll(){
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::map)
                .toList();
    }

    public CategoryReadDto findById(Long id){
        return categoryRepository.findById(id)
                .map(categoryMapper::map)
                .orElseThrow(() ->
                new CategoryNotFoundException("Category with id " + id + " not found"));
    }

}
