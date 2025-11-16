package ru.noleg.prreviewerservice.controller;

import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.api.controllers.TeamsApi;
import ru.noleg.api.models.AddTeam201Response;
import ru.noleg.api.models.Team;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.mapper.TeamMapper;
import ru.noleg.prreviewerservice.service.TeamService;

@RestController
public class TeamController implements TeamsApi {

  private final TeamService teamService;
  private final TeamMapper teamMapper;

  public TeamController(TeamService teamService, TeamMapper teamMapper) {
    this.teamService = teamService;
    this.teamMapper = teamMapper;
  }

  @Override
  public ResponseEntity<AddTeam201Response> addTeam(Team team) {
    Set<UserEntity> userEntitySet = teamMapper.toUserEntitySet(team.getMembers());

    TeamEntity createdTeam = teamService.createTeam(team.getTeamName(), userEntitySet);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(teamMapper.toAddTeamResponse(createdTeam));
  }

  @Override
  public ResponseEntity<Team> getTeam(String teamName) {
    TeamEntity teamEntity = teamService.getTeam(teamName);
    return ResponseEntity.status(HttpStatus.OK).body(teamMapper.toTeam(teamEntity));
  }
}
