package org.example.taskmanger.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanger.database.dto.TaskCreateEditDto;
import org.example.taskmanger.database.dto.TaskReadDto;
import org.example.taskmanger.database.entity.Task;
import org.example.taskmanger.database.entity.TaskStatus;
import org.example.taskmanger.database.repository.CategoryRepository;
import org.example.taskmanger.database.repository.TaskRepository;
import org.example.taskmanger.database.repository.UserRepository;
import org.example.taskmanger.dto.event.TaskEvent;
import org.example.taskmanger.dto.event.TaskEventType;
import org.example.taskmanger.exception.TaskNotFoundException;
import org.example.taskmanger.kafka.TaskEventProducer;
import org.example.taskmanger.mapper.TaskMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@AllArgsConstructor
public class TasksService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final TaskEventProducer taskEventProducer;


    @CacheEvict(value = "tasksList", allEntries = true)
    public TaskReadDto create(TaskCreateEditDto dto) {
        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .taskStatus(dto.getTaskStatus())
                .category(categoryRepository.getReferenceById(dto.getCategoryId()))
                .user(userRepository.getReferenceById(dto.getUserId()))
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Created task with id: {}", savedTask.getId());

        var user = userRepository.findById(dto.getUserId())
                .orElseThrow();

        TaskEvent event = new TaskEvent(
                UUID.randomUUID(),
                TaskEventType.TASK_CREATED,
                savedTask.getId(),
                user.getId(),
                savedTask.getTitle(),
                user.getEmail(),
                LocalDateTime.now()
        );

        taskEventProducer.send(event);

        return taskMapper.map(savedTask);
    }

    @Cacheable("tasksList")
    public List<TaskReadDto> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::map)
                .toList();
    }

    @Cacheable("task")
    public TaskReadDto findById(Long id) {
        log.info("Finding task with id: {}", id);
        return taskRepository.findById(id)
                .map(taskMapper::map)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task with id " + id + " not found"));

    }

    public List<TaskReadDto> findByCategoryId(Long id) {
        return taskRepository.findByCategoryId(id)
                .stream()
                .map(taskMapper::map)
                .toList();
    }

    public List<TaskReadDto> findByStatus(TaskStatus status) {
        return taskRepository.findByTaskStatus(status)
                .stream()
                .map(taskMapper::map)
                .toList();
    }

    @CacheEvict(value = "tasksList", allEntries = true)
    @CachePut("task")
    public TaskReadDto update(Long id, TaskCreateEditDto dto){
        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task with id " + id + " not found"));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(dto.getTaskStatus());
        task.setCategory(categoryRepository.getReferenceById(dto.getCategoryId()));
        task.setUser(userRepository.getReferenceById(dto.getUserId()));

        Task updatedTask = taskRepository.save(task);
        return taskMapper.map(updatedTask);
    }

    @Caching(evict = {
            @CacheEvict(value = "tasksList", allEntries = true),
            @CacheEvict(value = "task", key = "#id")
    })
    public void delete(long id) {
        taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task with id " + id + " not found"));
        taskRepository.deleteById(id);
        log.info("Deleted task with id: {}", id);
    }
}
