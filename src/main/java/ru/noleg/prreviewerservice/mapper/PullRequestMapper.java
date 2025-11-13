package ru.noleg.prreviewerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.openapitools.jackson.nullable.JsonNullable;
import ru.noleg.api.models.CreatePullRequest201Response;
import ru.noleg.api.models.GetReview200Response;
import ru.noleg.api.models.PullRequest;
import ru.noleg.api.models.PullRequestShort;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.PullRequestStatus;
import ru.noleg.prreviewerservice.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PullRequestMapper {

    @Mapping(target = "pullRequestId", source = "id")
    @Mapping(target = "pullRequestName", source = "title")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "status", expression = "java(mapStatus(entity.getStatus()))")
    PullRequestShort toShort(PullRequestEntity entity);

    List<PullRequestShort> toShortList(List<PullRequestEntity> entities);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "pullRequests", source = "pullRequests")
    GetReview200Response toGetReviewResponse(String userId, List<PullRequestEntity> pullRequests);

    default PullRequestShort.StatusEnum mapStatus(PullRequestStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case OPEN -> PullRequestShort.StatusEnum.OPEN;
            case MERGED -> PullRequestShort.StatusEnum.MERGED;
        };
    }
//    @Mapping(target = "pr", source = "entity")
//    CreatePullRequest201Response toCreateResponse(PullRequestEntity entity);
//
//    @Mapping(target = "pullRequestId", source = "id")
//    @Mapping(target = "pullRequestName", source = "title")
//    @Mapping(target = "authorId", source = "author.id")
//    @Mapping(target = "status", expression = "java(mapStatus(entity.getStatus()))")
//    @Mapping(target = "assignedReviewers", expression = "java(mapReviewers(entity.getReviewers()))")
//    @Mapping(target = "createdAt", expression = "java(toJsonNullable(java.sql.Timestamp.valueOf(entity.getId() != null ? java.time.LocalDateTime.now() : null)))")
//    // пример
//    @Mapping(target = "mergedAt", expression = "java(toJsonNullable(entity.getMergedAt() != null ? java.sql.Timestamp.valueOf(entity.getMergedAt()) : null))")
//    PullRequest toPullRequest(PullRequestEntity entity);
//
//    // === Вспомогательные методы ===
//
//    default List<String> mapReviewers(Set<UserEntity> reviewers) {
//        if (reviewers == null) return List.of();
//        return reviewers.stream()
//                .map(UserEntity::getId)
//                .toList();
//    }
//
//    default JsonNullable<Date> toJsonNullable(Object value) {
//        return switch (value) {
//            case Date date -> JsonNullable.of(date);
//            case LocalDateTime ldt -> JsonNullable.of(java.sql.Timestamp.valueOf(ldt));
//            case null, default -> JsonNullable.undefined();
//        };
//    }
}
