package com.predu.evertask.controller;

import com.predu.evertask.annotation.IsNotUnassignedUser;
import com.predu.evertask.annotation.IsOrganisationAdminOrAdmin;
import com.predu.evertask.domain.dto.project.ProjectCreateDto;
import com.predu.evertask.domain.dto.project.ProjectDto;
import com.predu.evertask.domain.dto.project.ProjectUpdateDto;
import com.predu.evertask.domain.model.Project;
import com.predu.evertask.domain.model.User;
import com.predu.evertask.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable UUID id) {
        return projectService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @IsNotUnassignedUser
    @GetMapping("/organisation")
    public ResponseEntity<List<ProjectDto>> getUserOrganisationProjects(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(projectService
                .findAllByOrganisation(user.getOrganisation().getId()));
    }

    @IsOrganisationAdminOrAdmin
    @PostMapping
    public ResponseEntity<ProjectCreateDto> createProject(@RequestBody @Valid ProjectCreateDto toCreate,
                                                          Authentication authentication) throws URISyntaxException, IllegalAccessException {

        User owner = (User) authentication.getPrincipal();

        Project created = projectService.create(toCreate, owner);

        return ResponseEntity
                .created(new URI("http://localhost:8080/api/projects/" + created.getId()))
                .body(toCreate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProject(@RequestBody @Valid ProjectUpdateDto toUpdate, @PathVariable UUID id) {
        if (!projectService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        projectService.findById(id).ifPresent(project -> projectService.update(id, toUpdate));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        if (!projectService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        projectService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
