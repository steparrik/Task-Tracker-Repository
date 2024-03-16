package steparrik.code.crazytasktracker.store.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import steparrik.code.crazytasktracker.api.dto.ProjectDTO;
import steparrik.code.crazytasktracker.api.dto.TaskStateDTO;
import steparrik.code.crazytasktracker.api.exceptions.BadRequestException;
import steparrik.code.crazytasktracker.api.exceptions.NotFoundException;
import steparrik.code.crazytasktracker.store.entities.ProjectEntity;
import steparrik.code.crazytasktracker.store.entities.TaskStateEntity;
import steparrik.code.crazytasktracker.store.repositories.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final ModelMapper modelMapper;
    private final ProjectRepository projectRepository;
    private final TaskStatesService taskStatesService;


    public ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException(String.format("Project with %s does not exist.", projectId)));
    }

    public ProjectEntity findByNameOrThrowExc(String name, Long projectId){
        projectRepository.findByName(name)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectId))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Project %s already exists.", name));
                });

        return projectRepository.findByName(name).get();
    }

    public void updateTaskStatesListToProject(ProjectEntity project){

        List<TaskStateEntity> taskStates = project.getTaskStates();

        System.out.println(taskStates);

        for(int i = 1;i<=taskStates.size();i++){
            taskStates.get(i-1).setOrdinal(i);
        }
        project.setTaskStates(taskStates);

        projectRepository.save(project);
    }


    public ProjectDTO convertToProjectDTO(ProjectEntity projectEntity){
        System.out.println(projectEntity);
        return modelMapper.map(projectEntity, ProjectDTO.class);
    }
}

