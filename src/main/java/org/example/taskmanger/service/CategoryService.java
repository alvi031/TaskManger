package org.example.taskmanger.service;

import lombok.AllArgsConstructor;
import org.example.taskmanger.database.entity.Category;
import org.example.taskmanger.database.repository.CategoryRepository;
import org.example.taskmanger.exception.CategoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public Category findById(Long id){
        return categoryRepository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException("Category with id " + id + " not found"));
    }

}
