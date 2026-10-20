package org.example.taskmanger.database.repository;

import org.example.taskmanger.database.entity.OutboxEvent;
import org.example.taskmanger.database.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query("""
            SELECT e
            FROM OutboxEvent e
            WHERE e.status = :status
            ORDER BY e.createdAt ASC
            """)
    List<OutboxEvent> findAllByStatus(@Param("status") OutboxStatus status);
}
