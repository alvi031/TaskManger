
package org.example.taskmanger.service;

import org.example.taskmanger.database.dto.TaskCreateEditDto;
import org.example.taskmanger.database.dto.TaskReadDto;
import org.example.taskmanger.database.entity.Category;
import org.example.taskmanger.database.entity.Task;
import org.example.taskmanger.database.entity.TaskStatus;
import org.example.taskmanger.database.entity.User;
import org.example.taskmanger.database.repository.CategoryRepository;
import org.example.taskmanger.database.repository.TaskRepository;
import org.example.taskmanger.database.repository.UserRepository;
import org.example.taskmanger.dto.event.TaskEvent;
import org.example.taskmanger.exception.TaskNotFoundException;
import org.example.taskmanger.kafka.TaskEventProducer;
import org.example.taskmanger.mapper.TaskMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TasksService tasksService;

    @Test
    void findById() {
        Long taskId = 1L;
        Task task = mock(Task.class);
        TaskReadDto expectedDto = mock(TaskReadDto.class);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));
        when(taskMapper.map(task))
                .thenReturn(expectedDto);

        TaskReadDto actual = tasksService.findById(taskId);

        assertSame(expectedDto, actual);
        verify(taskRepository).findById(taskId);
        verify(taskMapper).map(task);
    }

    @Test
    void findById_notFound() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> tasksService.findById(taskId)
        );

        verify(taskMapper, never()).map(any());
    }

    @Test
    void delete() {
        Long taskId = 1L;
        Task task = mock(Task.class);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        tasksService.delete(taskId);

        verify(taskRepository).findById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void delete_NotFound() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> tasksService.delete(taskId)
        );

        verify(taskRepository, never()).deleteById(anyLong());
    }


}
