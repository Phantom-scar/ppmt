package io.agileintelligence.ppmt.service;

import io.agileintelligence.ppmt.domain.AppUser;
import io.agileintelligence.ppmt.domain.Project;
import io.agileintelligence.ppmt.domain.Task;
import io.agileintelligence.ppmt.domain.TaskStatus;
import io.agileintelligence.ppmt.repository.AppUserRepository;
import io.agileintelligence.ppmt.repository.ProjectRepository;
import io.agileintelligence.ppmt.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
// import java.util.Set;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public Task saveTask(Task task) {
        Project project = findProject(task.getProjectIdentifier());
        String assigneeUsername = resolveAssigneeUsername(task.getAssigneeUsername());
        validateAssigneeBelongsToProject(project, assigneeUsername);

        task.setProjectIdentifier(project.getProjectIdentifier());
        task.setAssigneeUsername(assigneeUsername);
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }

        return taskRepository.save(task);
    }

    public Task updateTask(String taskId, Task taskDetails) {
        Task task = findTaskById(taskId);

        if (taskDetails.getTitle() != null) {
            task.setTitle(taskDetails.getTitle());
        }
        if (taskDetails.getDescription() != null) {
            task.setDescription(taskDetails.getDescription());
        }
        if (taskDetails.getDueDate() != null) {
            task.setDueDate(taskDetails.getDueDate());
        }
        if (taskDetails.getStatus() != null) {
            task.setStatus(taskDetails.getStatus());
        }
        if (taskDetails.getProjectIdentifier() != null && !taskDetails.getProjectIdentifier().isBlank()) {
            Project p = findProject(taskDetails.getProjectIdentifier());
            task.setProjectIdentifier(p.getProjectIdentifier());
        }
        if (taskDetails.getAssigneeUsername() != null && !taskDetails.getAssigneeUsername().isBlank()) {
            String assigneeUsername = resolveAssigneeUsername(taskDetails.getAssigneeUsername());
            Project project = findProject(task.getProjectIdentifier());
            validateAssigneeBelongsToProject(project, assigneeUsername);
            task.setAssigneeUsername(assigneeUsername);
        }

        return taskRepository.save(task);
    }

    public Task findTaskById(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public List<Task> findAllTasks() {
        AppUser currentUser = getCurrentUser();
        if (currentUser.getRole().name().equals("ADMIN")) {
            return taskRepository.findAll();
        }

        // Members should see tasks assigned to them OR unassigned tasks (available to claim)
        String username = currentUser.getUsername();
        return taskRepository.findAll().stream()
                .filter(task -> task.getAssigneeUsername() == null || username.equals(task.getAssigneeUsername()))
                .toList();
    }

    public void deleteTaskById(String taskId) {
        Task task = findTaskById(taskId);
        taskRepository.delete(task);
    }

    private Project findProject(String projectIdentifier) {
        if (projectIdentifier == null || projectIdentifier.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project identifier is required");
        }

        Project project = projectRepository.findByProjectIdentifier(projectIdentifier.toUpperCase());
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project not found");
        }
        return project;
    }

    private String resolveAssigneeUsername(String assigneeUsername) {
        if (assigneeUsername == null || assigneeUsername.isBlank()) {
            return getCurrentUser().getUsername();
        }

        appUserRepository.findByUsername(assigneeUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee user not found"));
        return assigneeUsername;
    }

    private void validateAssigneeBelongsToProject(Project project, String assigneeUsername) {
        if (project.getMembers() == null || !project.getMembers().contains(assigneeUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee must be a member of the project");
        }
    }

    private AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String username = authentication.getName();
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user not found"));
    }
}