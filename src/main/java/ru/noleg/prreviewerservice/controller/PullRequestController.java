package ru.noleg.prreviewerservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.api.controllers.PullRequestsApi;
import ru.noleg.api.models.CreatePullRequest201Response;
import ru.noleg.api.models.CreatePullRequestRequest;
import ru.noleg.api.models.MergePullRequestRequest;
import ru.noleg.api.models.ReassignReviewer200Response;
import ru.noleg.api.models.ReassignReviewerRequest;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.mapper.PullRequestMapper;
import ru.noleg.prreviewerservice.service.PullRequestService;

@RestController
public class PullRequestController implements PullRequestsApi {

  private final PullRequestService pullRequestService;
  private final PullRequestMapper pullRequestMapper;

  public PullRequestController(
      PullRequestService pullRequestService, PullRequestMapper pullRequestMapper) {
    this.pullRequestService = pullRequestService;
    this.pullRequestMapper = pullRequestMapper;
  }

  @Override
  public ResponseEntity<CreatePullRequest201Response> createPullRequest(
      CreatePullRequestRequest createPullRequestRequest) {
    PullRequestEntity pullRequest =
        pullRequestService.createPullRequest(
            createPullRequestRequest.getPullRequestId(),
            createPullRequestRequest.getPullRequestName(),
            createPullRequestRequest.getAuthorId());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(pullRequestMapper.toCreateResponse(pullRequest));
  }

  @Override
  public ResponseEntity<CreatePullRequest201Response> mergePullRequest(
      MergePullRequestRequest mergePullRequestRequest) {
    PullRequestEntity pullRequest =
        pullRequestService.mergePullRequest(mergePullRequestRequest.getPullRequestId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(pullRequestMapper.toCreateResponse(pullRequest));
  }

  @Override
  public ResponseEntity<ReassignReviewer200Response> reassignReviewer(
      ReassignReviewerRequest reassignReviewerRequest) {
    PullRequestEntity pullRequest =
        pullRequestService.reassignReviewer(
            reassignReviewerRequest.getPullRequestId(), reassignReviewerRequest.getOldUserId());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            pullRequestMapper.toReassignResponse(
                pullRequest, reassignReviewerRequest.getOldUserId()));
  }
}
