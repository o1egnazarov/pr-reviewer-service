package ru.noleg.prreviewerservice.model;

import ru.noleg.prreviewerservice.entity.PullRequestStatus;

public record PrStatusCountModel(PullRequestStatus status, long count) {}
