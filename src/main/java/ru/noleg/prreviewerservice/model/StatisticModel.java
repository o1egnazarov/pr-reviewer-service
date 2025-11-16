package ru.noleg.prreviewerservice.model;

import java.util.List;

public record StatisticModel(
    long totalActiveUsers,
    long totalInactiveUsers,
    List<UserAssignmentCountModel> assignmentsByUser,
    long totalOpenPrsNeedingReview,
    List<PrAssignmentCountModel> assignmentsByPr,
    List<PrStatusCountModel> prStatusCountModels) {}
