package io.agileintelligence.ppmt.repository;

import io.agileintelligence.ppmt.domain.Task;
import io.agileintelligence.ppmt.domain.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface TaskRepository extends MongoRepository<Task, String> {
    long countByStatus(TaskStatus status);
    long countByDueDateBeforeAndStatusNot(LocalDate dueDate, TaskStatus status);
}