package ru.noleg.prreviewerservice.utils;

import ru.noleg.prreviewerservice.entity.UserEntity;

public class UserTestUtil {
    public static UserEntity createUser(String id, String username, boolean active) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setActive(active);
        return user;
    }
}
