package steparrik.code.crazytasktracker.store.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import steparrik.code.crazytasktracker.api.dto.TaskDTO;
import steparrik.code.crazytasktracker.api.dto.TaskStateDTO;
import steparrik.code.crazytasktracker.api.exceptions.NotFoundException;
import steparrik.code.crazytasktracker.store.entities.ProjectEntity;
import steparrik.code.crazytasktracker.store.entities.TaskEntity;
import steparrik.code.crazytasktracker.store.entities.TaskStateEntity;
import steparrik.code.crazytasktracker.store.repositories.TaskRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    public TaskDTO convertToTaskDTO(TaskEntity taskEntity) {
        System.out.println(taskEntity);
        return modelMapper.map(taskEntity, TaskDTO.class);
    }

    public TaskEntity getTaskOrThrowException(Long taskId) {
        return taskRepository
                .findById(taskId)
                .orElseThrow(() -> new NotFoundException(String.format("Task with %s does not exist.", taskId)));
    }

    public TaskEntity getTaskInTaskStateOrThrowException(Long taskId, TaskStateEntity taskState){
        TaskEntity task = getTaskOrThrowException(taskId);

        return taskState.getTasks().stream().filter(t->t.getId()
                .equals(taskId)).findFirst().orElseThrow(
                () -> new NotFoundException(String.format("Task  with %s does not exist in here project", taskId)));
    }

}
