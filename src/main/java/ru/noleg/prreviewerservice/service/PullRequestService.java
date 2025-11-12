package ru.noleg.prreviewerservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.PullRequestStatus;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.DomainException;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.repository.PullRequestRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class PullRequestService {

    private final PullRequestRepository pullRequestRepository;
    private final UserRepository userRepository;

    public PullRequestService(PullRequestRepository pullRequestRepository,
                              UserRepository userRepository) {
        this.pullRequestRepository = pullRequestRepository;
        this.userRepository = userRepository;
    }

    public PullRequestEntity createPullRequest(String prId, String title, String authorId) {
        UserEntity author = this.validationAndGetAuthor(prId, authorId);
        Set<UserEntity> reviewers = this.findReviewers(author);

        PullRequestEntity pullRequestEntity = new PullRequestEntity();
        pullRequestEntity.setId(prId);
        pullRequestEntity.setTitle(title);
        pullRequestEntity.setAuthor(author);
        pullRequestEntity.setReviewers(reviewers);
        pullRequestEntity.setNeedMoreReviewers(reviewers.size() < 2);

        return pullRequestRepository.save(pullRequestEntity);
    }

    private UserEntity validationAndGetAuthor(String pullRequestId, String authorId) {
        if (pullRequestRepository.existsById(pullRequestId)) {
            throw new DomainException(ErrorCode.PR_EXISTS, "PR with id " + pullRequestId + " already exists!");
        }
        return userRepository.findById(authorId).orElseThrow(
                () -> new DomainException(ErrorCode.NOT_FOUND, "Author with id " + authorId + " not found!")
        );
    }

    private Set<UserEntity> findReviewers(UserEntity author) {
        if (author.getTeam() == null) {
            throw new DomainException(ErrorCode.NOT_FOUND, "Team not found for author with username: " +
                    author.getUsername()
            );
        }

        List<UserEntity> candidates = userRepository.findByTeamAndIsActiveTrue(author.getTeam())
                .stream()
                .filter(u -> !u.equals(author))
                .collect(Collectors.toList());

        Collections.shuffle(candidates);
        return candidates.stream()
                .limit(2)
                .collect(Collectors.toSet());
    }

    public PullRequestEntity reassignReviewer(String pullRequestId, String oldReviewerId) {
        PullRequestEntity pullRequestEntity = this.validationAndGetPullRequest(pullRequestId);
        UserEntity oldReviewer = this.validationAndGetOldReviewer(oldReviewerId, pullRequestEntity);

        UserEntity newReviewer = this.findNewReviewer(pullRequestEntity, oldReviewer);

        Set<UserEntity> reviewers = pullRequestEntity.getReviewers();
        reviewers.remove(oldReviewer);
        reviewers.add(newReviewer);
        pullRequestEntity.setReviewers(reviewers);

        return pullRequestRepository.save(pullRequestEntity);
    }

    private PullRequestEntity validationAndGetPullRequest(String pullRequestId) {
        PullRequestEntity pullRequestEntity = pullRequestRepository.findById(pullRequestId).orElseThrow(
                () -> new DomainException(ErrorCode.NOT_FOUND, "PR with id " + pullRequestId + " not found!")
        );

        if (pullRequestEntity.getStatus() == PullRequestStatus.MERGED) {
            throw new DomainException(ErrorCode.PR_MERGED, "Cannot reassign on merged PR!");
        }
        return pullRequestEntity;
    }

    private UserEntity validationAndGetOldReviewer(String oldReviewerId, PullRequestEntity pullRequestEntity) {
        UserEntity oldReviewer = userRepository.findById(oldReviewerId).orElseThrow(
                () -> new DomainException(ErrorCode.NOT_FOUND, "User with id " + oldReviewerId + " not found for author!")
        );

        if (!pullRequestEntity.getReviewers().contains(oldReviewer)) {
            throw new DomainException(ErrorCode.NOT_ASSIGNED, "Reviewer is not assigned to this PR!");
        }
        return oldReviewer;
    }

    private UserEntity findNewReviewer(PullRequestEntity pullRequestEntity, UserEntity oldReviewer) {
        UserEntity author = pullRequestEntity.getAuthor();
        if (author.getTeam() == null) {
            throw new DomainException(ErrorCode.NOT_FOUND, "Team not found for author");
        }

        return userRepository.findByTeamAndIsActiveTrue(author.getTeam())
                .stream()
                .filter(u -> !u.equals(author) && !u.equals(oldReviewer))
                .findFirst()
                .orElseThrow(
                        () -> new DomainException(ErrorCode.NO_CANDIDATE, "No active replacement candidate in team!")
                );
    }

    public PullRequestEntity mergePullRequest(String pullRequestId) {
        PullRequestEntity pullRequestEntity = pullRequestRepository.findById(pullRequestId).orElseThrow(
                () -> new DomainException(ErrorCode.NOT_FOUND, "PR with id " + pullRequestId + " not found!")
        );

        if (pullRequestEntity.getStatus() == PullRequestStatus.MERGED) {
            return pullRequestEntity;
        }

        pullRequestEntity.setStatus(PullRequestStatus.MERGED);
        pullRequestEntity.setMergedAt(LocalDateTime.now());

        return pullRequestRepository.save(pullRequestEntity);
    }

    @Transactional(readOnly = true)
    public List<PullRequestEntity> getReviewByUserId(String reviewerId) {
        UserEntity reviewer = userRepository.findById(reviewerId).orElseThrow(
                () -> new DomainException(ErrorCode.NOT_FOUND, "User with id " + reviewerId + " not found")
        );

        return pullRequestRepository.findByReviewersContains(reviewer);
    }
}
