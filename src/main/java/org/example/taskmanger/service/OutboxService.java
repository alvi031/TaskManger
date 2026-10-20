
package org.example.taskmanger.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.taskmanger.database.entity.OutboxEvent;
import org.example.taskmanger.database.entity.OutboxStatus;
import org.example.taskmanger.database.repository.OutboxRepository;
import org.example.taskmanger.dto.event.TaskEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    @Transactional
    public void save(TaskEvent event) {
        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(event.eventId())
                    .taskId(event.taskId())
                    .eventType(event.eventType().name())
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Не удалось сериализовать TaskEvent", e
            );
        }
    }

    @Transactional(readOnly = true)
    public List<OutboxEvent> getPendingEvents() {
        return outboxRepository.findAllByStatus(OutboxStatus.PENDING);
    }

    @Transactional
    public void markAsSent(UUID eventId) {
        OutboxEvent event = outboxRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Outbox event not found: " + eventId
                ));

        event.setStatus(OutboxStatus.SENT);
        event.setSentAt(LocalDateTime.now());

        outboxRepository.save(event);
    }
}
