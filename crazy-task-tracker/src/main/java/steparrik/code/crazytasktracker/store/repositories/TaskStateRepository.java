package steparrik.code.crazytasktracker.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import steparrik.code.crazytasktracker.store.entities.ProjectEntity;
import steparrik.code.crazytasktracker.store.entities.TaskEntity;
import steparrik.code.crazytasktracker.store.entities.TaskStateEntity;

import java.util.Optional;

@Repository
public interface TaskStateRepository extends JpaRepository<TaskStateEntity, Integer> {
    Optional<TaskStateEntity> findById(Long id);
    Optional<TaskStateEntity> findByName(String name);

}
