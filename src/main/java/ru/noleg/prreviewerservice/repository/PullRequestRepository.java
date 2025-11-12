package ru.noleg.prreviewerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;

import java.util.List;

public interface PullRequestRepository extends JpaRepository<PullRequestEntity, String> {
    List<PullRequestEntity> findByReviewersContains(UserEntity reviewer);
}
