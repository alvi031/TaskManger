package org.example.taskmanger;

import org.example.taskmanger.database.dto.TaskCreateEditDto;
import org.example.taskmanger.database.dto.TaskReadDto;
import org.example.taskmanger.database.entity.*;
import org.example.taskmanger.database.repository.CategoryRepository;
import org.example.taskmanger.database.repository.TaskRepository;
import org.example.taskmanger.database.repository.UserRepository;
import org.example.taskmanger.kafka.TaskEventProducer;
import org.example.taskmanger.service.TasksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TasksServiceIntegrationTest {

    @Autowired
    private TasksService service;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockitoBean
    private TaskEventProducer producer;

    @Test
    void create() {
        User user = createUser();
        Category category = createCategory();

        TaskCreateEditDto dto = new TaskCreateEditDto();
        dto.setTitle("New task");
        dto.setDescription("New description");
        dto.setTaskStatus(TaskStatus.TODO);
        dto.setUserId(user.getId());
        dto.setCategoryId(category.getId());

        TaskReadDto result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals("New task", result.getTitle());
        assertTrue(taskRepository.existsById(result.getId()));
        verify(producer).send(any());
    }

    @Test
    void findById() {
        Task task = createTask(createUser(), createCategory());

        TaskReadDto result = service.findById(task.getId());

        assertEquals(task.getId(), result.getId());
        assertEquals("Test task", result.getTitle());
    }

    @Test
    void delete() {
        Task task = createTask(createUser(), createCategory());
        Long id = task.getId();

        service.delete(id);

        assertFalse(taskRepository.existsById(id));
    }

    private User createUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setEmail("test@example.com");
        return userRepository.saveAndFlush(user);
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("Test category");
        return categoryRepository.saveAndFlush(category);
    }

    private Task createTask(User user, Category category) {
        Task task = Task.builder()
                .title("Test task")
                .description("Test description")
                .taskStatus(TaskStatus.TODO)
                .createdAt(java.time.LocalDateTime.now())
                .user(user)
                .category(category)
                .build();

        return taskRepository.saveAndFlush(task);
    }
}