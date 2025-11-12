package ru.noleg.prreviewerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.api.controllers.TeamsApi;
import ru.noleg.api.models.AddTeam201Response;
import ru.noleg.api.models.Team;
import ru.noleg.prreviewerservice.service.TeamService;

import java.util.HashSet;

@RestController
public class TeamController implements TeamsApi {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

//    @Override
//    public ResponseEntity<AddTeam201Response> addTeam(Team team) {
//        return teamService.createTeam(team.getTeamName(), new HashSet<>(team.getMembers()));
//    }
//
//    @Override
//    public ResponseEntity<Team> getTeam(String teamName) {
//        return TeamsApi.super.getTeam(teamName);
//    }
}
