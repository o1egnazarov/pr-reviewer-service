package ru.noleg.prreviewerservice.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.exception.NotFoundException;
import ru.noleg.prreviewerservice.repository.PullRequestRepository;
import ru.noleg.prreviewerservice.repository.TeamRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.service.TeamManagementService;

@Service
public class TeamManagementServiceDefaultImpl implements TeamManagementService {

  private final UserRepository userRepository;
  private final PullRequestRepository pullRequestRepository;
  private final TeamRepository teamRepository;

  public TeamManagementServiceDefaultImpl(
      UserRepository userRepository,
      PullRequestRepository pullRequestRepository,
      TeamRepository teamRepository) {
    this.userRepository = userRepository;
    this.pullRequestRepository = pullRequestRepository;
    this.teamRepository = teamRepository;
  }

  @Transactional
  @Override
  public List<PullRequestEntity> deactivateUsersAndReassign(String teamTitle) {
    Set<String> deactivatedUserIds = this.deactivateTeamUsers(teamTitle);
    return this.reassignOpenPullRequests(deactivatedUserIds);
  }

  private Set<String> deactivateTeamUsers(String teamTitle) {
    TeamEntity team =
        teamRepository
            .findByTitle(teamTitle)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorCode.NOT_FOUND, "Team with title " + teamTitle + " not found!"));

    List<UserEntity> activeUsers = userRepository.findByTeamAndIsActiveTrue(team);

    if (activeUsers.isEmpty()) {
      return Set.of();
    }

    userRepository.deactivateUsersByTeamTitle(teamTitle);

    return activeUsers.stream().map(UserEntity::getId).collect(Collectors.toSet());
  }

  private List<PullRequestEntity> reassignOpenPullRequests(Set<String> deactivatedUserIds) {
    if (deactivatedUserIds == null || deactivatedUserIds.isEmpty()) {
      return List.of();
    }

    List<PullRequestEntity> affectedPrs =
        pullRequestRepository.findAllOpenPrsWithReviewers(deactivatedUserIds);

    if (affectedPrs.isEmpty()) {
      return List.of();
    }

    for (PullRequestEntity pr : affectedPrs) {

      Set<UserEntity> reviewers = pr.getReviewers();

      List<UserEntity> removedReviewers =
          reviewers.stream().filter(r -> deactivatedUserIds.contains(r.getId())).toList();

      if (removedReviewers.isEmpty()) {
        continue;
      }

      Set<String> excluded = new HashSet<>();
      excluded.add(pr.getAuthor().getId());
      excluded.addAll(reviewers.stream().map(UserEntity::getId).toList());
      excluded.addAll(deactivatedUserIds);

      List<UserEntity> replacementCandidates =
          userRepository.findActiveReplacements(pr.getAuthor().getTeam().getTitle(), excluded);

      if (replacementCandidates.isEmpty()) {
        removedReviewers.forEach(reviewers::remove);
        continue;
      }

      Iterator<UserEntity> replacementIterator = replacementCandidates.iterator();

      for (UserEntity removed : removedReviewers) {
        if (!replacementIterator.hasNext()) {
          reviewers.remove(removed);
          continue;
        }
        UserEntity replacement = replacementIterator.next();
        reviewers.remove(removed);
        reviewers.add(replacement);
      }
    }
    return affectedPrs;
  }
}
