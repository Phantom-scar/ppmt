package io.agileintelligence.ppmt.web;


import io.agileintelligence.ppmt.domain.Project;
import io.agileintelligence.ppmt.service.MapValidationErrorsService;
import io.agileintelligence.ppmt.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
// import java.lang.reflect.Field;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

@RestController
@RequestMapping({"/api/project", "/api/projects"})
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MapValidationErrorsService mapValidationErrorsService;

    @PostMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project , BindingResult result){

        ResponseEntity<?> errorMap = mapValidationErrorsService.mapValidationErrors(result);
        if(errorMap!=null)return errorMap;

        Project newProject = projectService.saveProject(project);

        return new ResponseEntity<Project>(newProject, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<?> getProjectById(@PathVariable String projectId){
        Project project = projectService.findProjectByIdentifier(projectId);

        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }

    @GetMapping({"", "/all"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public Iterable<Project> getAllProjects(){return projectService.findAllProjects();}

    @PutMapping("/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<?> updateProject(@PathVariable String projectId, @Valid @RequestBody Project project, BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorsService.mapValidationErrors(result);
        if(errorMap != null) {
            return errorMap;
        }

        Project updatedProject = projectService.updateProject(projectId, project);
        return new ResponseEntity<Project>(updatedProject, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProject(@PathVariable String projectId){
        projectService.deleteProjectByIdentifier(projectId);

        return new ResponseEntity<String>("Project "+projectId+" deleted successfully", HttpStatus.OK);
    }
}
