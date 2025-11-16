package ru.noleg.prreviewerservice.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.api.controllers.TeamManagementsApi;
import ru.noleg.api.models.DeactivateUsersByTeam200Response;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.mapper.PullRequestMapper;
import ru.noleg.prreviewerservice.service.TeamManagementService;

@RestController
public class TeamManagementController implements TeamManagementsApi {

  private final TeamManagementService teamManagementService;
  private final PullRequestMapper pullRequestMapper;

  public TeamManagementController(
      TeamManagementService teamManagementService, PullRequestMapper pullRequestMapper) {
    this.teamManagementService = teamManagementService;
    this.pullRequestMapper = pullRequestMapper;
  }

  @Override
  public ResponseEntity<DeactivateUsersByTeam200Response> deactivateUsersByTeam(String teamName) {
    List<PullRequestEntity> pullRequest =
        teamManagementService.deactivateUsersAndReassign(teamName);

    return ResponseEntity.status(HttpStatus.OK)
        .body(pullRequestMapper.toDeactivateUsersResponse(pullRequest));
  }
}
