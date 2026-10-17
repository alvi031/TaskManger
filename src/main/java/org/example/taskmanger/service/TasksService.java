package org.example.taskmanger.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanger.database.dto.TaskCreateEditDto;
import org.example.taskmanger.database.dto.TaskFilterDto;
import org.example.taskmanger.database.dto.TaskReadDto;
import org.example.taskmanger.database.entity.Task;
import org.example.taskmanger.database.entity.TaskStatus;
import org.example.taskmanger.database.repository.CategoryRepository;
import org.example.taskmanger.database.repository.TaskRepository;
import org.example.taskmanger.database.repository.TaskSpecification;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @CacheEvict(value = "tasksPages", allEntries = true)
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


//    @Cacheable("tasksPages")
    public Page<TaskReadDto> findAll(
            TaskFilterDto filter,
            Pageable pageable
    ) {
        var specification = TaskSpecification.withFilter(filter);

        return taskRepository.findAll(specification, pageable)
                .map(taskMapper::map);
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

    public List<TaskReadDto> findAllByUserIdAndStatus(
            Long userId,
            TaskStatus status
    ) {
        return taskRepository.findAllByUserIdAndStatus(userId, status)
                .stream()
                .map(taskMapper::map)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "tasksPages", allEntries = true)
    @CachePut(value = "task", key = "#id")
    public TaskReadDto update(Long id, TaskCreateEditDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task with id " + id + " not found"));

        TaskStatus previousStatus = task.getTaskStatus();

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(dto.getTaskStatus());
        task.setCategory(categoryRepository.getReferenceById(dto.getCategoryId()));
        task.setUser(userRepository.getReferenceById(dto.getUserId()));

        Task updatedTask = taskRepository.save(task);

        boolean taskWasCompleted = previousStatus == TaskStatus.DONE;
        boolean taskIsCompleted = updatedTask.getTaskStatus() == TaskStatus.DONE;

        if (!taskWasCompleted && taskIsCompleted) {
            var user = userRepository.findById(dto.getUserId())
                    .orElseThrow();

            TaskEvent event = new TaskEvent(
                    UUID.randomUUID(),
                    TaskEventType.TASK_COMPLETED,
                    updatedTask.getId(),
                    user.getId(),
                    updatedTask.getTitle(),
                    user.getEmail(),
                    LocalDateTime.now()
            );

            taskEventProducer.send(event);
            log.info("Task {} completed; completion event published", updatedTask.getId());
        }

        return taskMapper.map(updatedTask);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "tasksPages", allEntries = true),
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
