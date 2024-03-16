package steparrik.code.crazytasktracker.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import steparrik.code.crazytasktracker.api.dto.AskDTO;
import steparrik.code.crazytasktracker.api.dto.TaskDTO;
import steparrik.code.crazytasktracker.api.dto.TaskStateDTO;
import steparrik.code.crazytasktracker.api.exceptions.BadRequestException;
import steparrik.code.crazytasktracker.store.entities.ProjectEntity;
import steparrik.code.crazytasktracker.store.entities.TaskEntity;
import steparrik.code.crazytasktracker.store.entities.TaskStateEntity;
import steparrik.code.crazytasktracker.store.repositories.ProjectRepository;
import steparrik.code.crazytasktracker.store.repositories.TaskRepository;
import steparrik.code.crazytasktracker.store.repositories.TaskStateRepository;
import steparrik.code.crazytasktracker.store.services.ProjectService;
import steparrik.code.crazytasktracker.store.services.TaskService;
import steparrik.code.crazytasktracker.store.services.TaskStatesService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskController {
    final TaskStateRepository taskStateRepository;
    final ProjectRepository projectRepository;
    final ProjectService projectService;
    final TaskStatesService taskStatesService;
    final TaskRepository taskRepository;
    final TaskService taskService;

    public static final String FETCH_TASK = "/api/projects/{projectId}/task-states/{taskStateId}/tasks";
    public static final String CREATE_TASK = "/api/projects/{projectId}/task-states/{taskStateId}/tasks";
    public static final String DELETE_TASK= "/api/projects/{projectId}/task-states/{taskStateId}/tasks/{taskId}";
    public static final String EDIT_TASK =  "/api/projects/{projectId}/task-states/{taskStateId}/tasks/{taskId}";

    @GetMapping(FETCH_TASK)
    public List<TaskDTO> fetchTask(
            @PathVariable("projectId")Long projectId,
            @PathVariable("taskStateId")Long taskStateId
    ){
        ProjectEntity project = projectService.getProjectOrThrowException(projectId);
        TaskStateEntity taskState = taskStatesService.getTaskStateInProjectOrThrowException(taskStateId, project);


        return taskState.getTasks().stream().map(taskService::convertToTaskDTO).collect(Collectors.toList());


    }

    @PostMapping(CREATE_TASK)
    public TaskDTO createTask(
            @RequestParam("name") String name,
            @RequestParam("description")String description,
            @PathVariable("projectId")Long projectId,
            @PathVariable("taskStateId")Long taskStateId
    ){
        if(name==null || name.trim().isEmpty()){
            throw new BadRequestException("name should not be empty");
        }
        if(description == null || description.trim().isEmpty()){
            throw new BadRequestException("description should not be empty");
        }
        ProjectEntity project = projectService.getProjectOrThrowException(projectId);

        TaskStateEntity taskState = taskStatesService.getTaskStateInProjectOrThrowException(taskStateId, project);

        TaskEntity task = new TaskEntity();

        int ordinal = 0;
        if(taskState.getTasks().isEmpty()){
            ordinal = 1;
        }else{
            ordinal = taskState.getTasks().get(taskState.getTasks().size()-1).getOrdinal()+1;
        }

        task = taskRepository.saveAndFlush(
                TaskEntity.builder().name(name).description(description).ordinal(ordinal).build());

        taskState.getTasks().add(task);
        taskStateRepository.save(taskState);

        return taskService.convertToTaskDTO(task);
    }

    @DeleteMapping(DELETE_TASK)
    public AskDTO deleteTask(
            @PathVariable("projectId")Long projectId,
            @PathVariable("taskStateId")Long taskStateId,
            @PathVariable("taskId")Long taskId
    ){
        ProjectEntity project = projectService.getProjectOrThrowException(projectId);


        System.out.println(project);
        TaskStateEntity taskState = taskStatesService.getTaskStateInProjectOrThrowException(taskStateId, project);

        TaskEntity task = taskService.getTaskInTaskStateOrThrowException(taskId, taskState);
        System.out.println(task);

        taskRepository.delete(task);
        taskState.getTasks().remove(task);
        taskStateRepository.save(taskState);
        taskStatesService.updateTaskListToProject(taskState);

        return AskDTO.makeDefault(true);
    }

    @PatchMapping(EDIT_TASK)
    public TaskDTO editTaskStates(
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskStateId")Long taskStateId,
            @PathVariable("taskId")Long taskId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false)String description
    ){
        if((name==null || name.trim().isEmpty() ) && (description == null || description.trim().isEmpty())){
            throw new BadRequestException("Name or Description should not be empty ");
        }

        ProjectEntity project = projectService.getProjectOrThrowException(projectId);

        TaskStateEntity taskState = taskStatesService.getTaskStateInProjectOrThrowException(taskStateId, project);

        TaskEntity task = taskService.getTaskInTaskStateOrThrowException(taskId, taskState);

        if(name == null || name.trim().isEmpty()){
            task.setDescription(description);
        }else if(description == null || description.trim().isEmpty()){
            task.setName(name);
        }else{
            task.setName(name);
            task.setDescription(description);
        }
        taskRepository.save(task);

        return taskService.convertToTaskDTO(task);
    }
}
