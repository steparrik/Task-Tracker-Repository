package steparrik.code.crazytasktracker.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import steparrik.code.crazytasktracker.store.entities.ProjectEntity;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer> {
    Optional<ProjectEntity> findByName(String name);

    void deleteById(Long id);
    Stream<ProjectEntity> streamAllBy();

    Stream<ProjectEntity> streamAllByNameStartsWithIgnoreCase(String prefix);

    Optional<ProjectEntity> findById(Long id);
}
