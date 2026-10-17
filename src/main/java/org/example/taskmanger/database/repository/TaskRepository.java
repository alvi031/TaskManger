package org.example.taskmanger.database.repository;

import org.example.taskmanger.database.entity.Task;
import org.example.taskmanger.database.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>,
        JpaSpecificationExecutor<Task> {

    @Query("""
        SELECT t
        FROM Task t
        WHERE t.user.id = :userId
          AND t.taskStatus = :status
        """)
    List<Task> findAllByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status
    );

    List<Task> findByCategoryId(Long id);

    List<Task> findByTaskStatus(TaskStatus status);
}
