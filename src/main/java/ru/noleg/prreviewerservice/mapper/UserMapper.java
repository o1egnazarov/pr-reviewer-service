package ru.noleg.prreviewerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.noleg.api.models.SetIsActive200Response;
import ru.noleg.prreviewerservice.entity.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
  @Mapping(target = "user.userId", source = "id")
  @Mapping(target = "user.username", source = "username")
  @Mapping(target = "user.isActive", source = "active")
  @Mapping(target = "user.teamName", source = "team.title")
  SetIsActive200Response mapToUserResponse(UserEntity userEntity);
}
