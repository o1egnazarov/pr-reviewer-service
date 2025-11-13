package ru.noleg.prreviewerservice.service;

import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;

import java.util.Set;

public interface TeamService {
    TeamEntity createTeam(String teamTitle, Set<UserEntity> members);

    TeamEntity getTeam(String title);
}
