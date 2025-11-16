package ru.noleg.prreviewerservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.model.PrAssignmentCountModel;
import ru.noleg.prreviewerservice.model.PrStatusCountModel;

public interface PullRequestRepository extends JpaRepository<PullRequestEntity, String> {
  List<PullRequestEntity> findByReviewersContains(UserEntity reviewer);

  @Query(
      """
                    SELECT DISTINCT pr FROM PullRequestEntity pr
                    JOIN FETCH pr.reviewers r
                    WHERE pr.status = 'OPEN'
                    AND r.id IN :userIds
                    """)
  List<PullRequestEntity> findAllOpenPrsWithReviewers(@Param("userIds") Set<String> userIds);

  @EntityGraph(attributePaths = {"reviewers"})
  Optional<PullRequestEntity> findWithReviewersById(String id);

  @Query(
      """
            SELECT new ru.noleg.prreviewerservice.model.PrAssignmentCountModel(pr.id, COUNT(r.id))
            FROM PullRequestEntity pr
            LEFT JOIN pr.reviewers r
            GROUP BY pr.id
            """)
  List<PrAssignmentCountModel> countAssignmentByPr();

  @Query(
      """
            SELECT new ru.noleg.prreviewerservice.model.PrStatusCountModel(pr.status, COUNT(pr))
            FROM PullRequestEntity pr
            GROUP BY pr.status
            """)
  List<PrStatusCountModel> countByStatus();

  long countByNeedMoreReviewersTrue();
}
