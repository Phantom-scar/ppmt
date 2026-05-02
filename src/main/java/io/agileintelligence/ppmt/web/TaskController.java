package io.agileintelligence.ppmt.web;

import io.agileintelligence.ppmt.domain.Task;
import io.agileintelligence.ppmt.service.MapValidationErrorsService;
import io.agileintelligence.ppmt.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private MapValidationErrorsService mapValidationErrorsService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<?> createTask(@Valid @RequestBody Task task, BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorsService.mapValidationErrors(result);
        if (errorMap != null) {
            return errorMap;
        }

        Task createdTask = taskService.saveTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public Iterable<Task> getAllTasks() {
        return taskService.findAllTasks();
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<Task> getTaskById(@PathVariable String taskId) {
        return ResponseEntity.ok(taskService.findTaskById(taskId));
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<?> updateTask(@PathVariable String taskId, @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(taskId, task);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTask(@PathVariable String taskId) {
        taskService.deleteTaskById(taskId);
        return ResponseEntity.ok("Task " + taskId + " deleted successfully");
    }
}