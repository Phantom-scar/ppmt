package io.agileintelligence.ppmt.service;

import io.agileintelligence.ppmt.domain.AppUser;
import io.agileintelligence.ppmt.domain.Project;
import io.agileintelligence.ppmt.exceptions.ProjectIdException;
import io.agileintelligence.ppmt.exceptions.ProjectNotFoundException;
import io.agileintelligence.ppmt.repository.AppUserRepository;
import io.agileintelligence.ppmt.repository.ProjectRepository;
// import org.aspectj.weaver.reflect.IReflectionWorld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public Project saveProject(Project project){
        project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
        AppUser currentUser = getCurrentUser();
        project.setCreatedByUsername(currentUser.getUsername());
        project.setMembers(resolveMembers(project.getMemberUsernames(), currentUser));

        try {
            return projectRepository.save(project);
        } catch (DataIntegrityViolationException e) {
            throw new ProjectIdException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exists");
        }

    }

    public Project findProjectByIdentifier(String Id){
            Project project = projectRepository.findByProjectIdentifier(Id.toUpperCase());

            if(project == null){
                throw new ProjectNotFoundException("Project not found");
            }

            return project;
    }

    public Project updateProject(String projectId, Project projectDetails) {
        Project project = findProjectByIdentifier(projectId);

        if (projectDetails.getProjectName() != null) {
            project.setProjectName(projectDetails.getProjectName());
        }
        if (projectDetails.getDescription() != null) {
            project.setDescription(projectDetails.getDescription());
        }
        if (projectDetails.getStartDate() != null) {
            project.setStartDate(projectDetails.getStartDate());
        }
        if (projectDetails.getEndDate() != null) {
            project.setEndDate(projectDetails.getEndDate());
        }
        if (projectDetails.getMemberUsernames() != null && !projectDetails.getMemberUsernames().isEmpty()) {
            project.setMembers(resolveMembers(projectDetails.getMemberUsernames(), getCurrentUser()));
        }

        return projectRepository.save(project);
    }

    public Iterable<Project> findAllProjects(){
        return projectRepository.findAll();
    }


    public void deleteProjectByIdentifier(String projectId){
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null){
            throw new ProjectNotFoundException("Cannot delete project: "+projectId+" as it doesn't exist");
        }

        projectRepository.delete(project);

    }

    private Set<String> resolveMembers(Set<String> usernames, AppUser currentUser) {
        Set<String> members = new HashSet<>();
        if (currentUser != null) {
            members.add(currentUser.getUsername());
        }

        if (usernames != null) {
            for (String username : usernames) {
                if (username == null || username.isBlank()) {
                    continue;
                }
                members.add(username);
            }
        }

        return members;
    }

    private AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        return appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user not found"));
    }



}
