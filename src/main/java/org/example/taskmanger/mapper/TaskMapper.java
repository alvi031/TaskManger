package org.example.taskmanger.mapper;

import org.example.taskmanger.database.dto.TaskReadDto;
import org.example.taskmanger.database.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper implements Mapper<Task, TaskReadDto> {

    @Override
    public TaskReadDto map(Task object) {

        TaskReadDto dto = new TaskReadDto();

        dto.setId(object.getId());
        dto.setTitle(object.getTitle());
        dto.setDescription(object.getDescription());
        dto.setTaskStatus(object.getTaskStatus());
        dto.setCreatedAt(object.getCreatedAt());
        dto.setUserId(object.getUser().getId());
        dto.setCategoryId(object.getCategory().getId());

        return dto;
    }
}