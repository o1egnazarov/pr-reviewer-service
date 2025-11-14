package ru.noleg.prreviewerservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PullRequestRepository extends JpaRepository<PullRequestEntity, String> {
    List<PullRequestEntity> findByReviewersContains(UserEntity reviewer);

    @Query("""
            SELECT DISTINCT pr FROM PullRequestEntity pr
            JOIN FETCH pr.reviewers r
            WHERE pr.status = 'OPEN'
            AND r.id IN :userIds
            """)
    List<PullRequestEntity> findAllOpenPRsWithReviewers(@Param("userIds") Set<String> userIds);

    @EntityGraph(attributePaths = {"reviewers"})
    Optional<PullRequestEntity> findWithReviewersById(String id);
}

