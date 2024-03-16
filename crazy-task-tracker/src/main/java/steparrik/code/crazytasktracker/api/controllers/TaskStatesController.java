package steparrik.code.crazytasktracker.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import steparrik.code.crazytasktracker.api.dto.AskDTO;
import steparrik.code.crazytasktracker.api.dto.TaskStateDTO;
import steparrik.code.crazytasktracker.api.exceptions.BadRequestException;

import steparrik.code.crazytasktracker.store.entities.ProjectEntity;
import steparrik.code.crazytasktracker.store.entities.TaskStateEntity;
import steparrik.code.crazytasktracker.store.repositories.ProjectRepository;
import steparrik.code.crazytasktracker.store.repositories.TaskStateRepository;
import steparrik.code.crazytasktracker.store.services.ProjectService;
import steparrik.code.crazytasktracker.store.services.TaskStatesService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStatesController {
    final TaskStateRepository taskStateRepository;
    final ProjectRepository projectRepository;
    final ProjectService projectService;
    final TaskStatesService taskStatesService;

    public static final String FETCH_TASK_STATES = "/api/projects/{projectId}/task-states";
    public static final String CREATE_TASK_STATE = "/api/projects/{projectId}/task-states";
    public static final String DELETE_TASK_STATE = "/api/projects/{projectId}/task-states/{task-stateId}";
    public static final String EDIT_TASK_STATE = "/api/projects/{projectId}/task-states/{task-stateId}";

    @GetMapping(FETCH_TASK_STATES)
    public List<TaskStateDTO> fetchTaskStates(
            @PathVariable("projectId")Long id
    ){

        return projectService.getProjectOrThrowException(id).getTaskStates()
                .stream().map(taskStatesService::convertToTaskStateDTO).collect(Collectors.toList());


    }

    @PostMapping(CREATE_TASK_STATE)
    public TaskStateDTO createTaskState(
            @RequestParam("name") String name,
            @PathVariable("projectId")Long projectId
    ){
        if(name.trim().isEmpty()){
            throw new BadRequestException("name should not be empty");
        }

        ProjectEntity projectEntity = projectService.getProjectOrThrowException(projectId);


        taskStatesService.findByNameInProjectOrThrowExc(name, projectEntity);

        TaskStateEntity taskStateEntity = new TaskStateEntity();

        int ordinal = 0;
        if(projectEntity.getTaskStates().isEmpty()){
            ordinal = 1;
        }else{
            ordinal = projectEntity.getTaskStates().get(projectEntity.getTaskStates().size()-1).getOrdinal()+1;
        }

        taskStateEntity = taskStateRepository.saveAndFlush(
                TaskStateEntity.builder().name(name).ordinal(ordinal).tasks(new ArrayList<>()).build());

        projectEntity.getTaskStates().add(taskStateEntity);
        projectRepository.save(projectEntity);
        return taskStatesService.convertToTaskStateDTO(taskStateEntity);
    }

    @DeleteMapping(DELETE_TASK_STATE)
    public AskDTO deleteTaskStates(
            @PathVariable("projectId")Long projectId,
            @PathVariable("task-stateId")Long taskStateId
    ){
        ProjectEntity project = projectService.getProjectOrThrowException(projectId);
        TaskStateEntity taskStateEntity = taskStatesService.getTaskStateOrThrowException(taskStateId);
        taskStateRepository.delete(taskStateEntity);
        project.getTaskStates().remove(taskStateEntity);
        projectRepository.save(project);
        projectService.updateTaskStatesListToProject(project);

        return AskDTO.makeDefault(true);
    }

    @PatchMapping(EDIT_TASK_STATE)
    public TaskStateDTO editTaskStates(
            @PathVariable("projectId") Long projectId,
            @PathVariable("task-stateId")Long taskStateId,
            @RequestParam(name = "name") String name
    ){
        if(name.trim().isEmpty()){
            throw new BadRequestException("name should not be empty");
        }
        ProjectEntity project = projectService.getProjectOrThrowException(projectId);
        TaskStateEntity taskStateEntity = taskStatesService.getTaskStateInProjectOrThrowException(taskStateId, project);

        taskStateEntity.setName(name);
        taskStateRepository.saveAndFlush(taskStateEntity);

        return taskStatesService.convertToTaskStateDTO(taskStateEntity);
    }
}
