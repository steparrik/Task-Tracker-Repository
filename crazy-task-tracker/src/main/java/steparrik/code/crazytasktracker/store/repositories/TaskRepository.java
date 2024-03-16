package steparrik.code.crazytasktracker.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import steparrik.code.crazytasktracker.store.entities.TaskEntity;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {
    Optional<TaskEntity>findByName(String name);
    Optional<TaskEntity> findById(Long id);
}
