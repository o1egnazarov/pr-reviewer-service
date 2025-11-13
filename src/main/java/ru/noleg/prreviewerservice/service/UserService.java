package ru.noleg.prreviewerservice.service;

import ru.noleg.prreviewerservice.entity.UserEntity;

public interface UserService {
    UserEntity setActive(String userId, boolean isActive);
}
