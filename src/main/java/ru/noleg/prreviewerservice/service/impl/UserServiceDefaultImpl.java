package ru.noleg.prreviewerservice.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.exception.NotFoundException;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.service.UserService;

@Service
@Transactional
public class UserServiceDefaultImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceDefaultImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserEntity setActive(String userId, boolean isActive) {
    UserEntity userEntity =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorCode.NOT_FOUND, "User with id: " + userId + " not found"));

    userEntity.setActive(isActive);
    return userRepository.save(userEntity);
  }

  @Override
  public List<PullRequestEntity> getReviewByUserId(String userId) {
    UserEntity user =
        userRepository
            .findWithReviewerPullRequestsById(userId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorCode.NOT_FOUND, "User with id " + userId + " not found!"));
    return user.getReviewingPullRequestEntities();
  }
}
