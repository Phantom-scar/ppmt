package io.agileintelligence.ppmt.service;

import io.agileintelligence.ppmt.domain.TaskStatus;
import io.agileintelligence.ppmt.repository.ProjectRepository;
import io.agileintelligence.ppmt.repository.TaskRepository;
import io.agileintelligence.ppmt.web.dto.DashboardSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    public DashboardSummary getSummary() {
        long totalProjects = projectRepository.count();
        long totalTasks = taskRepository.count();
        long todoTasks = taskRepository.countByStatus(TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long doneTasks = taskRepository.countByStatus(TaskStatus.DONE);
        long overdueTasks = taskRepository.countByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.DONE);

        return new DashboardSummary(totalProjects, totalTasks, todoTasks, inProgressTasks, doneTasks, overdueTasks);
    }
}