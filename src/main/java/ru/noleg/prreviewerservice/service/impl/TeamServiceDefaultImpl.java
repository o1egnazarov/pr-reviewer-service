package ru.noleg.prreviewerservice.service.impl;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.exception.NotFoundException;
import ru.noleg.prreviewerservice.exception.TeamAlreadyExistException;
import ru.noleg.prreviewerservice.exception.UserAlreadyExistException;
import ru.noleg.prreviewerservice.repository.TeamRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.service.TeamService;

@Service
@Transactional
public class TeamServiceDefaultImpl implements TeamService {

  private final TeamRepository teamRepository;
  private final UserRepository userRepository;

  public TeamServiceDefaultImpl(TeamRepository teamRepository, UserRepository userRepository) {
    this.teamRepository = teamRepository;
    this.userRepository = userRepository;
  }

  @Override
  public TeamEntity createTeam(String teamTitle, Set<UserEntity> members) {
    this.validateTeamTitle(teamTitle);

    TeamEntity team = this.buildTeam(teamTitle, members);
    return teamRepository.save(team);
  }

  private void validateTeamTitle(String teamTitle) {
    if (teamRepository.existsByTitle(teamTitle)) {
      throw new TeamAlreadyExistException(
          ErrorCode.TEAM_EXISTS, "Team with title " + teamTitle + " already exists!");
    }
  }

  private TeamEntity buildTeam(String teamTitle, Set<UserEntity> members) {
    TeamEntity team = new TeamEntity();
    team.setTitle(teamTitle);
    this.addMembers(members, team);
    return team;
  }

  private void addMembers(Set<UserEntity> members, TeamEntity team) {
    if (members != null && !members.isEmpty()) {
      this.validateUsers(members);
      members.forEach(member -> this.buildUser(team, member));
    }
  }

  private void validateUsers(Set<UserEntity> members) {
    Set<String> memberIds = members.stream().map(UserEntity::getId).collect(Collectors.toSet());

    Set<String> existingIds = userRepository.findExistingIds(memberIds);

    if (!existingIds.isEmpty()) {
      throw new UserAlreadyExistException(
          ErrorCode.USER_EXISTS, "Users with ids " + existingIds + " already exist");
    }
  }

  private void buildUser(TeamEntity team, UserEntity member) {
    UserEntity user = new UserEntity();
    user.setId(member.getId());
    user.setUsername(member.getUsername());
    user.setActive(member.isActive());
    team.addMember(user);
  }

  @Override
  @Transactional(readOnly = true)
  public TeamEntity getTeam(String title) {
    return teamRepository
        .findByTitle(title)
        .orElseThrow(
            () ->
                new NotFoundException(
                    ErrorCode.NOT_FOUND, "Team with title " + title + " not found!"));
  }
}
