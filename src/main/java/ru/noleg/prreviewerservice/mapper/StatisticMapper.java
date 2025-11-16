package ru.noleg.prreviewerservice.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.noleg.api.models.AssignmentStats;
import ru.noleg.api.models.PullRequestAssignmentStats;
import ru.noleg.api.models.PullRequestStatusCount;
import ru.noleg.api.models.UserAssignmentStats;
import ru.noleg.prreviewerservice.entity.PullRequestStatus;
import ru.noleg.prreviewerservice.model.PrAssignmentCountModel;
import ru.noleg.prreviewerservice.model.PrStatusCountModel;
import ru.noleg.prreviewerservice.model.StatisticModel;
import ru.noleg.prreviewerservice.model.UserAssignmentCountModel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StatisticMapper {

  @Mapping(target = "totalActiveUsers", source = "totalActiveUsers")
  @Mapping(target = "totalInactiveUsers", source = "totalInactiveUsers")
  @Mapping(target = "userAssignments", source = "assignmentsByUser")
  @Mapping(target = "totalOpenPrsNeedingReview", source = "totalOpenPrsNeedingReview")
  @Mapping(target = "prAssignments", source = "assignmentsByPr")
  @Mapping(target = "prStatusCount", source = "prStatusCountModels")
  AssignmentStats toAssignmentStats(StatisticModel model);

  @Mapping(target = "userId", source = "userId")
  @Mapping(target = "assignedPRsCount", source = "prCount")
  UserAssignmentStats toUserAssignment(UserAssignmentCountModel model);

  List<UserAssignmentStats> toUserAssignmentList(List<UserAssignmentCountModel> models);

  @Mapping(target = "prId", source = "prId")
  @Mapping(target = "reviewersCount", source = "reviewerCount")
  PullRequestAssignmentStats toPrAssignment(PrAssignmentCountModel model);

  List<PullRequestAssignmentStats> toPrAssignmentList(List<PrAssignmentCountModel> models);

  @Mapping(target = "status", expression = "java(mapStatus(model.status()))")
  @Mapping(target = "count", source = "count")
  PullRequestStatusCount toPrStatusCount(PrStatusCountModel model);

  List<PullRequestStatusCount> toPrStatusCountList(List<PrStatusCountModel> models);

  default PullRequestStatusCount.StatusEnum mapStatus(PullRequestStatus status) {
    if (status == null) {
      return null;
    }
    return switch (status) {
      case OPEN -> PullRequestStatusCount.StatusEnum.OPEN;
      case MERGED -> PullRequestStatusCount.StatusEnum.MERGED;
    };
  }
}
