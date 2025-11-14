package ru.noleg.prreviewerservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    List<UserEntity> findByTeamAndIsActiveTrue(TeamEntity teamEntity);

    @Modifying
    @Query("""
            UPDATE UserEntity u
            SET u.isActive = false
            WHERE u.team.title = :teamTitle
            AND u.isActive = true
            """)
    void deactivateUsersByTeamTitle(@Param("teamTitle") String teamTitle);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE u.team.title = :teamTitle
            AND u.isActive = true
            AND u.id NOT IN :excluded
            """)
    List<UserEntity> findActiveReplacements(
            @Param("teamTitle") String teamTitle,
            @Param("excluded") Set<String> excludedUserIds);

    @EntityGraph(attributePaths = {"reviewingPullRequestEntities"})
    Optional<UserEntity> findWithReviewerPullRequestsById(String id);
}
