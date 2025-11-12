package ru.noleg.prreviewerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.api.controllers.UsersApi;
import ru.noleg.api.models.GetReview200Response;
import ru.noleg.prreviewerservice.service.UserService;

@RestController
public class UserController implements UsersApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<GetReview200Response> getReview(String userId) {
        return UsersApi.super.getReview(userId);
    }

//    @Override
//    public ResponseEntity<SetIsActive200Response> setIsActive(SetIsActiveRequest setIsActiveRequest) {
//        return userService.setActive(setIsActiveRequest.getUserId(), setIsActiveRequest.getIsActive());
//    }
}
