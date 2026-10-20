package org.example.taskmanger.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanger.database.entity.OutboxEvent;
import org.example.taskmanger.dto.event.TaskEvent;
import org.example.taskmanger.kafka.TaskEventProducer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxService outboxService;
    private final TaskEventProducer taskEventProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 1000)
    public void publishPendingEvents() {
        var events = outboxService.getPendingEvents();

        for (OutboxEvent outboxEvent : events) {
            try {
                TaskEvent event = objectMapper.readValue(
                        outboxEvent.getPayload(),
                        TaskEvent.class
                );

                taskEventProducer.send(event).get();

                outboxService.markAsSent(outboxEvent.getEventId());

                log.info(
                        "Outbox event marked as SENT: eventId={}, taskId={}",
                        event.eventId(),
                        event.taskId()
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Outbox publishing interrupted");
                return;

            } catch (Exception e) {
                log.error(
                        "Failed to publish outbox event: eventId={}. " +
                                "Event will remain pending",
                        outboxEvent.getEventId(),
                        e
                );
            }
        }
    }
}
