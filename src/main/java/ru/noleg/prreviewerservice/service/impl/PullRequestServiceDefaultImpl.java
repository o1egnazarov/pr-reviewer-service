package ru.noleg.prreviewerservice.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.PullRequestStatus;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.exception.NoAssignedForPrException;
import ru.noleg.prreviewerservice.exception.NoSuitableCandidatesException;
import ru.noleg.prreviewerservice.exception.NotFoundException;
import ru.noleg.prreviewerservice.exception.PrAlreadyExistException;
import ru.noleg.prreviewerservice.exception.PrMergedException;
import ru.noleg.prreviewerservice.repository.PullRequestRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.service.PullRequestService;

@Service
@Transactional
public class PullRequestServiceDefaultImpl implements PullRequestService {

  private final PullRequestRepository pullRequestRepository;
  private final UserRepository userRepository;

  public PullRequestServiceDefaultImpl(
      PullRequestRepository pullRequestRepository, UserRepository userRepository) {
    this.pullRequestRepository = pullRequestRepository;
    this.userRepository = userRepository;
  }

  @Override
  public PullRequestEntity createPullRequest(String prId, String title, String authorId) {
    this.validatePr(prId);
    UserEntity author = this.validateAndGetAuthor(authorId);
    Set<UserEntity> reviewers = this.findReviewers(author);

    PullRequestEntity pullRequestEntity = new PullRequestEntity();
    pullRequestEntity.setId(prId);
    pullRequestEntity.setTitle(title);
    pullRequestEntity.setAuthor(author);
    pullRequestEntity.setReviewers(reviewers);
    pullRequestEntity.setNeedMoreReviewers(reviewers.size() < 2);

    return pullRequestRepository.save(pullRequestEntity);
  }

  private void validatePr(String pullRequestId) {
    if (pullRequestRepository.existsById(pullRequestId)) {
      throw new PrAlreadyExistException(
          ErrorCode.PR_EXISTS, "PR with id " + pullRequestId + " already exists!");
    }
  }

  private UserEntity validateAndGetAuthor(String authorId) {
    UserEntity author =
        userRepository
            .findById(authorId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorCode.NOT_FOUND, "Author with id " + authorId + " not found!"));

    if (!author.isActive()) {
      throw new NoSuitableCandidatesException(
          ErrorCode.NO_CANDIDATE, "Author with id " + authorId + " is not active!");
    }
    this.validateTeamByAuthor(author);
    return author;
  }

  private Set<UserEntity> findReviewers(UserEntity author) {
    List<UserEntity> candidates =
        userRepository.findByTeamAndIsActiveTrue(author.getTeam()).stream()
            .filter(u -> !u.equals(author))
            .collect(Collectors.toList());

    Collections.shuffle(candidates);
    return candidates.stream().limit(2).collect(Collectors.toSet());
  }

  @Override
  public PullRequestEntity reassignReviewer(String pullRequestId, String oldReviewerId) {
    PullRequestEntity pullRequestEntity = this.validateAndGetPullRequest(pullRequestId);
    UserEntity oldReviewer = this.validateAndGetOldReviewer(oldReviewerId, pullRequestEntity);

    UserEntity newReviewer = this.findNewReviewer(pullRequestEntity, oldReviewer);

    Set<UserEntity> reviewers = pullRequestEntity.getReviewers();
    reviewers.remove(oldReviewer);
    reviewers.add(newReviewer);
    pullRequestEntity.setReviewers(reviewers);

    return pullRequestRepository.save(pullRequestEntity);
  }

  private PullRequestEntity validateAndGetPullRequest(String pullRequestId) {
    PullRequestEntity pullRequestEntity = this.getPullRequest(pullRequestId);

    if (pullRequestEntity.getStatus() == PullRequestStatus.MERGED) {
      throw new PrMergedException(ErrorCode.PR_MERGED, "Cannot reassign on merged PR!");
    }
    return pullRequestEntity;
  }

  private UserEntity validateAndGetOldReviewer(
      String oldReviewerId, PullRequestEntity pullRequestEntity) {
    UserEntity oldReviewer =
        userRepository
            .findById(oldReviewerId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorCode.NOT_FOUND,
                        "User with id " + oldReviewerId + " not found for author!"));

    if (!pullRequestEntity.getReviewers().contains(oldReviewer)) {
      throw new NoAssignedForPrException(
          ErrorCode.NOT_ASSIGNED, "Reviewer is not assigned to this PR!");
    }
    return oldReviewer;
  }

  private UserEntity findNewReviewer(PullRequestEntity pullRequestEntity, UserEntity oldReviewer) {
    UserEntity author = pullRequestEntity.getAuthor();

    this.validateTeamByAuthor(author);

    Set<String> excluded = Set.of(author.getId(), oldReviewer.getId());
    return userRepository.findActiveReplacements(author.getTeam().getTitle(), excluded).stream()
        .findAny()
        .orElseThrow(
            () ->
                new NoSuitableCandidatesException(
                    ErrorCode.NO_CANDIDATE, "No active replacement candidate in team!"));
  }

  private void validateTeamByAuthor(UserEntity author) {
    if (author.getTeam() == null) {
      throw new NotFoundException(
          ErrorCode.NOT_FOUND, "Team not found for author with username: " + author.getUsername());
    }
  }

  @Override
  public PullRequestEntity mergePullRequest(String pullRequestId) {
    PullRequestEntity pullRequestEntity = this.getPullRequest(pullRequestId);

    if (pullRequestEntity.getStatus() == PullRequestStatus.MERGED) {
      return pullRequestEntity;
    }

    pullRequestEntity.setStatus(PullRequestStatus.MERGED);
    pullRequestEntity.setMergedAt(LocalDateTime.now());

    return pullRequestRepository.save(pullRequestEntity);
  }

  private PullRequestEntity getPullRequest(String pullRequestId) {
    return pullRequestRepository
        .findWithReviewersById(pullRequestId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    ErrorCode.NOT_FOUND, "PR with id " + pullRequestId + " not found!"));
  }
}
