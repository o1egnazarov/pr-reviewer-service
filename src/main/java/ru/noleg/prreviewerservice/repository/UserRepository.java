package ru.noleg.prreviewerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    List<UserEntity> findByTeamAndIsActiveTrue(TeamEntity teamEntity);
}
