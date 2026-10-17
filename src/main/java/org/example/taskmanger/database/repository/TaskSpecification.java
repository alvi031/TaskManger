package org.example.taskmanger.database.repository;

import org.example.taskmanger.database.dto.TaskFilterDto;
import org.example.taskmanger.database.entity.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    public static Specification<Task> withFilter(TaskFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();

            if (filter.getStatus() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("taskStatus"),
                                filter.getStatus()
                        )
                );
            }

            if (filter.getFrom() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                filter.getFrom()
                        )
                );
            }

            if (filter.getTo() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("createdAt"),
                                filter.getTo()
                        )
                );
            }

            return predicate;
        };
    }
}
