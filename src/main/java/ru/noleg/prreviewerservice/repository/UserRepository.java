package ru.noleg.prreviewerservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.model.UserAssignmentCountModel;

public interface UserRepository extends JpaRepository<UserEntity, String> {
  List<UserEntity> findByTeamAndIsActiveTrue(TeamEntity teamEntity);

  @Query(
      """
            SELECT u.id FROM UserEntity u WHERE u.id IN :ids
            """)
  Set<String> findExistingIds(@Param("ids") Set<String> ids);

  @Modifying
  @Query(
      """
                    UPDATE UserEntity u
                    SET u.isActive = false
                    WHERE u.team.title = :teamTitle
                    AND u.isActive = true
                    """)
  void deactivateUsersByTeamTitle(@Param("teamTitle") String teamTitle);

  @Query(
      """
                    SELECT u FROM UserEntity u
                    WHERE u.team.title = :teamTitle
                    AND u.isActive = true
                    AND u.id NOT IN :excluded
                    """)
  List<UserEntity> findActiveReplacements(
      @Param("teamTitle") String teamTitle, @Param("excluded") Set<String> excludedUserIds);

  @EntityGraph(attributePaths = {"reviewingPullRequestEntities"})
  Optional<UserEntity> findWithReviewerPullRequestsById(String id);

  @Query(
      """
            SELECT new ru.noleg.prreviewerservice.model.UserAssignmentCountModel(u.id, COUNT(pr))
            FROM UserEntity u
            LEFT JOIN u.reviewingPullRequestEntities pr
            GROUP BY u.id
            """)
  List<UserAssignmentCountModel> countAssigmentByUser();

  long countByIsActiveTrue();

  long countByIsActiveFalse();
}
