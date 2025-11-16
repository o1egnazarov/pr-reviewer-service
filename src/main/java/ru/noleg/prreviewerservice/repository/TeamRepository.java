package ru.noleg.prreviewerservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.noleg.prreviewerservice.entity.TeamEntity;

public interface TeamRepository extends JpaRepository<TeamEntity, String> {
  @EntityGraph(attributePaths = {"members"})
  Optional<TeamEntity> findByTitle(String title);

  boolean existsByTitle(String title);
}
