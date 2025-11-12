package ru.noleg.prreviewerservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.DomainException;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.repository.TeamRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public TeamEntity createTeam(String teamTitle, Set<UserEntity> members) {
        members = this.validationAndGetMembers(teamTitle, members);

        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setTitle(teamTitle);
        teamEntity.setMembers(members);
        this.addedNewMembers(members, teamEntity);

        return teamRepository.save(teamEntity);
    }

    private Set<UserEntity> validationAndGetMembers(String teamTitle, Set<UserEntity> members) {
        if (teamRepository.existsByTitle(teamTitle)) {
            throw new DomainException(ErrorCode.TEAM_EXISTS, "Team with title " + teamTitle + " already exists!");
        }
        if (members == null) {
            members = new HashSet<>();
        }
        return members;
    }

    private void addedNewMembers(Set<UserEntity> members, TeamEntity teamEntity) {
        members.forEach(m -> {
            UserEntity newMember = new UserEntity();
            newMember.setId(m.getId());
            newMember.setUsername(m.getUsername());
            newMember.setActive(m.isActive());
            newMember.setTeam(teamEntity);
            teamEntity.addMember(newMember);
        });
    }

    @Transactional(readOnly = true)
    public TeamEntity getTeam(String title) {
        return teamRepository.findByTitle(title).orElseThrow(
                () -> new DomainException(ErrorCode.NOT_FOUND, "Team with title " + title + " not found!")
        );
    }
}
