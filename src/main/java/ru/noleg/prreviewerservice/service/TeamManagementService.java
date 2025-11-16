package ru.noleg.prreviewerservice.service;

import java.util.List;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;

public interface TeamManagementService {
  List<PullRequestEntity> deactivateUsersAndReassign(String teamTitle);
}
