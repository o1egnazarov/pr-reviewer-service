package ru.noleg.prreviewerservice.mapper;

import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.noleg.api.models.CreatePullRequest201Response;
import ru.noleg.api.models.DeactivateUsersByTeam200Response;
import ru.noleg.api.models.GetReview200Response;
import ru.noleg.api.models.PullRequest;
import ru.noleg.api.models.PullRequestShort;
import ru.noleg.api.models.ReassignReviewer200Response;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.PullRequestStatus;
import ru.noleg.prreviewerservice.entity.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PullRequestMapper {

  @Mapping(target = "pullRequestId", source = "id")
  @Mapping(target = "pullRequestName", source = "title")
  @Mapping(target = "authorId", source = "author.id")
  @Mapping(target = "status", expression = "java(mapStatusShort(entity.getStatus()))")
  PullRequestShort toShort(PullRequestEntity entity);

  List<PullRequestShort> toShortList(List<PullRequestEntity> entities);

  @Mapping(target = "userId", source = "userId")
  @Mapping(target = "pullRequests", source = "pullRequests")
  GetReview200Response toGetReviewResponse(String userId, List<PullRequestEntity> pullRequests);

  default DeactivateUsersByTeam200Response toDeactivateUsersResponse(
      List<PullRequestEntity> pullRequests) {
    DeactivateUsersByTeam200Response response = new DeactivateUsersByTeam200Response();
    response.setPullRequests(toShortList(pullRequests));
    return response;
  }

  default PullRequestShort.StatusEnum mapStatusShort(PullRequestStatus status) {
    if (status == null) {
      return null;
    }
    return switch (status) {
      case OPEN -> PullRequestShort.StatusEnum.OPEN;
      case MERGED -> PullRequestShort.StatusEnum.MERGED;
    };
  }

  @Mapping(target = "pr", source = "entity")
  CreatePullRequest201Response toCreateResponse(PullRequestEntity entity);

  // TODO: ИСПРАВИТЬ REPLACED BY
  @Mapping(target = "pr", source = "entity")
  @Mapping(target = "replacedBy", source = "replacedByUserId")
  ReassignReviewer200Response toReassignResponse(PullRequestEntity entity, String replacedByUserId);

  @Mapping(target = "pullRequestId", source = "id")
  @Mapping(target = "pullRequestName", source = "title")
  @Mapping(target = "authorId", source = "author.id")
  @Mapping(target = "status", expression = "java(mapStatus(entity.getStatus()))")
  @Mapping(target = "assignedReviewers", expression = "java(mapReviewers(entity.getReviewers()))")
  @Mapping(target = "createdAt", source = "createdAt")
  @Mapping(target = "mergedAt", source = "mergedAt")
  PullRequest toPullRequest(PullRequestEntity entity);

  default List<String> mapReviewers(Set<UserEntity> reviewers) {
    if (reviewers == null) {
      return List.of();
    }
    return reviewers.stream().map(UserEntity::getId).toList();
  }

  default PullRequest.StatusEnum mapStatus(PullRequestStatus status) {
    if (status == null) {
      return null;
    }
    return switch (status) {
      case OPEN -> PullRequest.StatusEnum.OPEN;
      case MERGED -> PullRequest.StatusEnum.MERGED;
    };
  }
}
