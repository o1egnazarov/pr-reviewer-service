package ru.noleg.prreviewerservice.service;

import java.util.List;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;

public interface UserService {
  UserEntity setActive(String userId, boolean isActive);

  List<PullRequestEntity> getReviewByUserId(String userId);
}
