package ru.noleg.prreviewerservice.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noleg.prreviewerservice.model.PrAssignmentCountModel;
import ru.noleg.prreviewerservice.model.PrStatusCountModel;
import ru.noleg.prreviewerservice.model.StatisticModel;
import ru.noleg.prreviewerservice.model.UserAssignmentCountModel;
import ru.noleg.prreviewerservice.repository.PullRequestRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.service.StatisticService;

@Service
@Transactional(readOnly = true)
public class StatisticServiceDefaultImpl implements StatisticService {

  private final PullRequestRepository pullRequestRepository;
  private final UserRepository userRepository;

  public StatisticServiceDefaultImpl(
      PullRequestRepository pullRequestRepository, UserRepository userRepository) {
    this.pullRequestRepository = pullRequestRepository;
    this.userRepository = userRepository;
  }

  @Override
  public StatisticModel getStatistics() {
    long needMoreReviewersCount = pullRequestRepository.countByNeedMoreReviewersTrue();
    long countTrueIsActive = userRepository.countByIsActiveTrue();
    long countFalseIsActive = userRepository.countByIsActiveFalse();

    List<UserAssignmentCountModel> userAssignmentCountModels =
        userRepository.countAssigmentByUser();
    List<PrAssignmentCountModel> prAssignmentCountModels =
        pullRequestRepository.countAssignmentByPr();
    List<PrStatusCountModel> prStatusCountModels = pullRequestRepository.countByStatus();

    return new StatisticModel(
        countTrueIsActive,
        countFalseIsActive,
        userAssignmentCountModels,
        needMoreReviewersCount,
        prAssignmentCountModels,
        prStatusCountModels);
  }
}
