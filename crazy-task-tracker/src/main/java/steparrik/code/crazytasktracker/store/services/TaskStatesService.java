package steparrik.code.crazytasktracker.store.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import steparrik.code.crazytasktracker.api.dto.TaskStateDTO;
import steparrik.code.crazytasktracker.api.exceptions.BadRequestException;
import steparrik.code.crazytasktracker.api.exceptions.NotFoundException;
import steparrik.code.crazytasktracker.store.entities.ProjectEntity;
import steparrik.code.crazytasktracker.store.entities.TaskEntity;
import steparrik.code.crazytasktracker.store.entities.TaskStateEntity;
import steparrik.code.crazytasktracker.store.repositories.TaskStateRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskStatesService {
    private final TaskStateRepository taskStateRepository;
    private final ModelMapper modelMapper;

    public TaskStateEntity getTaskStateOrThrowException(Long taskStateId) {
        return taskStateRepository
                .findById(taskStateId)
                .orElseThrow(() -> new NotFoundException(String.format("Task state with %s does not exist.", taskStateId)));
    }

    public TaskStateEntity getTaskStateInProjectOrThrowException(Long taskStateId, ProjectEntity project){
        TaskStateEntity taskState = getTaskStateOrThrowException(taskStateId);

        return project.getTaskStates().stream().filter(ts->ts.getId()
                .equals(taskStateId)).findFirst().orElseThrow(
                () -> new NotFoundException(String.format("Task state with %s does not exist in here project", taskStateId)));
    }

    public void findByNameOrThrowExc(String name, Long id){
        taskStateRepository.findByName(name)
                .filter(anotherTaskState -> !Objects.equals(anotherTaskState.getId(), id))
                .ifPresent(anotherTaskState -> {
                    throw new BadRequestException(String.format("Task state %s already exists.", name));
                });


    }

    public void findByNameInProjectOrThrowExc(String name, ProjectEntity project){
        for(TaskStateEntity taskState : project.getTaskStates()){
            if(name.equals(taskState.getName())){
                throw new BadRequestException(String.format("Task state %s already exists.", name));
            }else{
                continue;
            }
        }
    }



    public TaskStateDTO convertToTaskStateDTO(TaskStateEntity taskStateEntity) {
        System.out.println(taskStateEntity);
        return modelMapper.map(taskStateEntity, TaskStateDTO.class);
    }

    public void updateTaskListToProject(TaskStateEntity taskState){

        List<TaskEntity> tasks = taskState.getTasks();

        System.out.println(tasks);

        for(int i = 1;i<=tasks.size();i++){
            tasks.get(i-1).setOrdinal(i);
        }
        taskState.setTasks(tasks);

        taskStateRepository.save(taskState);
    }



}


//Additional materials
//1. Convert toTSDTO
//        TaskStateDTO taskStateDTO = new TaskStateDTO();
//        taskStateDTO.setId(taskStateEntity.getId());
//        taskStateDTO.setName(taskStateEntity.getName());
//        taskStateDTO.setCreatedAt(taskStateEntity.getCreatedAt());
//        taskStateDTO.setOrdinal(taskStateEntity.getOrdinal());
//2.
