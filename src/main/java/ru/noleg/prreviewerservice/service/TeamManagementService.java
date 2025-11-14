package ru.noleg.prreviewerservice.service;

import ru.noleg.prreviewerservice.entity.PullRequestEntity;

import java.util.List;

public interface TeamManagementService {
    List<PullRequestEntity> deactivateUsersAndReassign(String teamTitle);
}
