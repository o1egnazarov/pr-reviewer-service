package ru.noleg.prreviewerservice.service;

import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity setActive(String userId, boolean isActive);

    List<PullRequestEntity> getReviewByUserId(String userId);
}
