package ru.noleg.prreviewerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.api.controllers.PullRequestsApi;
import ru.noleg.api.models.*;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.service.PullRequestService;

@RestController
public class PullRequestController implements PullRequestsApi {

    private final PullRequestService pullRequestService;

    public PullRequestController(PullRequestService pullRequestService) {
        this.pullRequestService = pullRequestService;
    }

    @Override
    public ResponseEntity<CreatePullRequest201Response> createPullRequest(
            CreatePullRequestRequest createPullRequestRequest
    ) {
        PullRequestEntity pullRequest = pullRequestService.createPullRequest(
                createPullRequestRequest.getPullRequestId(),
                createPullRequestRequest.getPullRequestName(),
                createPullRequestRequest.getAuthorId());

        return PullRequestsApi.super.createPullRequest(createPullRequestRequest);
    }

    @Override
    public ResponseEntity<CreatePullRequest201Response> mergePullRequest(MergePullRequestRequest mergePullRequestRequest) {
        return PullRequestsApi.super.mergePullRequest(mergePullRequestRequest);
    }

    @Override
    public ResponseEntity<ReassignReviewer200Response> reassignReviewer(ReassignReviewerRequest reassignReviewerRequest) {
        return PullRequestsApi.super.reassignReviewer(reassignReviewerRequest);
    }
}
