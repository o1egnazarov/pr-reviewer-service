package ru.noleg.prreviewerservice.service;

import ru.noleg.prreviewerservice.entity.PullRequestEntity;

public interface PullRequestService {
  PullRequestEntity createPullRequest(String prId, String title, String authorId);

  PullRequestEntity reassignReviewer(String pullRequestId, String oldReviewerId);

  PullRequestEntity mergePullRequest(String pullRequestId);
}
