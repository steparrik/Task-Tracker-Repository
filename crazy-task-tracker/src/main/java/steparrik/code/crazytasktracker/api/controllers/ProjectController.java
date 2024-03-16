package steparrik.code.crazytasktracker.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import steparrik.code.crazytasktracker.api.dto.AskDTO;
import steparrik.code.crazytasktracker.api.dto.ProjectDTO;
import steparrik.code.crazytasktracker.api.exceptions.BadRequestException;
import steparrik.code.crazytasktracker.api.exceptions.NotFoundException;
import steparrik.code.crazytasktracker.store.entities.ProjectEntity;
import steparrik.code.crazytasktracker.store.repositories.ProjectRepository;
import steparrik.code.crazytasktracker.store.services.ProjectService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectController {

    final ProjectRepository projectRepository;
    final ProjectService projectService;
    final ModelMapper modelMapper;

    public static final String FETCH_PROJECT = "/api/projects";
    public static final String CREATE_PROJECT = "/api/projects";
    public static final String EDIT_PROJECT = "/api/projects/{projectId}";
    public static final String DELETE_PROJECT = "/api/projects/{projectId}";
    public static final String CREATE_OR_UPDATE_PROJECT = "api/projects";
    public static final String GET_PROJECT_INFO = "api/projects/{projectId}";

    @GetMapping(GET_PROJECT_INFO)
    public ProjectDTO getProjectInfo(@PathVariable("projectId")Long id
    ){
        return projectService.convertToProjectDTO(projectService
                .getProjectOrThrowException(id));
    }


    @GetMapping(FETCH_PROJECT)
    public List<ProjectDTO> fetchProjects(
            @RequestParam(value = "prefix_name", required = false)Optional<String> optionalPrefixName
            ){

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName.map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);
        if(optionalPrefixName.isPresent()){
            projectStream = projectRepository.streamAllByNameStartsWithIgnoreCase(optionalPrefixName.get());
        }

        return projectStream.map(projectService::convertToProjectDTO)
                .collect(Collectors.toList());

    }


    @PostMapping(CREATE_PROJECT)
    public ProjectDTO createProject(
            @RequestParam String name
    ){

        projectRepository.findByName(name)
                .ifPresent(project -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists.", name));
                });

        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(name)
                        .build()
        );

        return projectService.convertToProjectDTO(project);
    }

    @PatchMapping(EDIT_PROJECT)
    public ProjectDTO editProject(
            @PathVariable("projectId") Long projectId,
            @RequestParam String name
    ){

        if(name.trim().isEmpty()){
            throw new BadRequestException("Name should not be empty");
        }
        ProjectEntity project = projectService.getProjectOrThrowException(projectId);

        projectService.findByNameOrThrowExc(name, projectId);

        project.setName(name);

        project = projectRepository.saveAndFlush(project);

        return projectService.convertToProjectDTO(project);
    }

    @DeleteMapping(DELETE_PROJECT)
    public AskDTO deleteProject(
            @PathVariable("projectId") Long projectId
    ){

        projectService.getProjectOrThrowException(projectId);


        projectRepository.deleteById(projectId);

        return AskDTO.makeDefault(true);
    }



//    @PutMapping(CREATE_OR_UPDATE_PROJECT)
//    public ProjectDTO editProject(
//            @RequestParam(value = "projectId" , required = false) Optional<Long> optionalProjectId,
//            @RequestParam (value = "projectName", required = false)Optional<String> optionalProjectName
//    ){
//
//        optionalProjectName = optionalProjectName.filter(projectName -> !projectName.trim().isEmpty());
//
//        boolean isCreate = !optionalProjectId.isPresent();
//
//        ProjectEntity project = optionalProjectId.map(this::getProjectOrThrowException)
//                .orElseGet(() -> ProjectEntity.builder().build());
//
//        if(isCreate && !optionalProjectName.isPresent()){
//            throw new BadRequestException("Project name should not be empty");
//        }
//
//        optionalProjectName.ifPresent(projectName -> {
//            projectRepository.findByName(projectName)
//                    .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project.getId()))
//                    .ifPresent(anotherProject -> {
//                        throw new BadRequestException(String.format("Project \"%s\" already exists.", projectName));
//                    });
//
//            project.setName(projectName);
//        });
//
//        final ProjectEntity savedProject = projectRepository.save(project);
//        return projectDTOFactory.makeProjectDTO(savedProject);
//    }



}
