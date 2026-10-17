package org.example.taskmanger.database.dto;

import lombok.Data;
import org.example.taskmanger.database.entity.TaskStatus;

import java.time.LocalDateTime;

@Data
public class TaskCreateEditDto {
    private String title;
    private String description;
    private TaskStatus taskStatus;
    private LocalDateTime createdAt;
    private Long userId;
    private Long categoryId;
}
