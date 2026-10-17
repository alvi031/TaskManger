package org.example.taskmanger.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskEvent(
        UUID eventId,
        TaskEventType eventType,
        Long taskId,
        Long userId,
        String title,
        String email,
        LocalDateTime occurredAt
) {
}