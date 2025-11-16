package ru.noleg.prreviewerservice.mapper;

import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.noleg.api.models.AddTeam201Response;
import ru.noleg.api.models.Team;
import ru.noleg.api.models.TeamMember;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeamMapper {

  @Mapping(target = "id", source = "userId")
  @Mapping(target = "username", source = "username")
  @Mapping(target = "active", source = "isActive")
  @Mapping(target = "team", ignore = true)
  @Mapping(target = "reviewingPullRequestEntities", ignore = true)
  UserEntity toUserEntity(TeamMember member);

  Set<UserEntity> toUserEntitySet(List<TeamMember> members);

  @Mapping(target = "userId", source = "id")
  @Mapping(target = "username", source = "username")
  @Mapping(target = "isActive", source = "active")
  TeamMember toTeamMember(UserEntity entity);

  List<TeamMember> toTeamMemberList(Set<UserEntity> entities);

  @Mapping(target = "teamName", source = "title")
  @Mapping(target = "members", source = "members")
  Team toTeam(TeamEntity entity);

  @Mapping(target = "team", source = ".")
  AddTeam201Response toAddTeamResponse(TeamEntity entity);
}
