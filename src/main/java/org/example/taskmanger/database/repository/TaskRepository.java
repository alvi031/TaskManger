package org.example.taskmanger.database.repository;

import org.example.taskmanger.database.entity.Task;
import org.example.taskmanger.database.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCategoryId(Long id);

    List<Task> findByTaskStatus(TaskStatus status);
}
