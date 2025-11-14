package ru.noleg.prreviewerservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.api.controllers.TeamManagementsApi;
import ru.noleg.api.models.DeactivateUsersByTeam200Response;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.mapper.PullRequestMapper;
import ru.noleg.prreviewerservice.service.TeamManagementService;

import java.util.List;

@RestController
@RequestMapping("/teamManagements/deactivate")
public class TeamManagementController implements TeamManagementsApi {

    private final TeamManagementService teamManagementService;
    private final PullRequestMapper pullRequestMapper;

    public TeamManagementController(TeamManagementService teamManagementService,
                                    PullRequestMapper pullRequestMapper) {
        this.teamManagementService = teamManagementService;
        this.pullRequestMapper = pullRequestMapper;
    }

    public ResponseEntity<DeactivateUsersByTeam200Response> deactivateUsersTeam(String teamName) {
        List<PullRequestEntity> pullRequest = teamManagementService.deactivateUsersAndReassign(teamName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pullRequestMapper.toDeactivateUsersResponse(pullRequest));
    }
}
