package org.example.taskmanger.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanger.dto.event.TaskEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventProducer {

    private static final String TOPIC = "task-events";

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public CompletableFuture<SendResult<String, TaskEvent>> send(TaskEvent event) {
        return kafkaTemplate.send(
                TOPIC,
                event.taskId().toString(),
                event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error(
                        "Failed to send task event: eventId={}, taskId={}",
                        event.eventId(),
                        event.taskId(),
                        exception
                );
            } else {
                log.info(
                        "Sent task event: eventId={}, taskId={}, topic={}",
                        event.eventId(),
                        event.taskId(),
                        result.getRecordMetadata().topic()
                );
            }
        });
    }
}
