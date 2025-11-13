package ru.noleg.prreviewerservice.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.DomainException;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.exception.NotFoundException;
import ru.noleg.prreviewerservice.repository.TeamRepository;
import ru.noleg.prreviewerservice.service.TeamService;

import java.util.Set;

@Service
@Transactional
public class TeamServiceDefaultImpl implements TeamService {

    private final TeamRepository teamRepository;

    public TeamServiceDefaultImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public TeamEntity createTeam(String teamTitle, Set<UserEntity> members) {
        this.validateTeamTitle(teamTitle);

        TeamEntity team = new TeamEntity();
        team.setTitle(teamTitle);
        this.addMembers(members, team);

        return teamRepository.save(team);
    }

    private void validateTeamTitle(String teamTitle) {
        if (teamRepository.existsByTitle(teamTitle)) {
            throw new DomainException(ErrorCode.TEAM_EXISTS, "Team with title " + teamTitle + " already exists!");
        }
    }

    private void addMembers(Set<UserEntity> members, TeamEntity team) {
        if (members != null && !members.isEmpty()) {
            members.forEach(member -> {
                UserEntity user = new UserEntity();
                user.setId(member.getId());
                user.setUsername(member.getUsername());
                user.setActive(member.isActive());
                team.addMember(user);
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TeamEntity getTeam(String title) {
        return teamRepository.findByTitle(title).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND, "Team with title " + title + " not found!")
        );
    }
}
