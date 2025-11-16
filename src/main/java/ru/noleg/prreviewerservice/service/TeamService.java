package ru.noleg.prreviewerservice.service;

import java.util.Set;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;

public interface TeamService {
  TeamEntity createTeam(String teamTitle, Set<UserEntity> members);

  TeamEntity getTeam(String title);
}
