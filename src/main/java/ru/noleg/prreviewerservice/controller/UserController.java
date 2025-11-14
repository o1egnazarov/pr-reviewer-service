package ru.noleg.prreviewerservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.api.controllers.UsersApi;
import ru.noleg.api.models.GetReview200Response;
import ru.noleg.api.models.SetIsActive200Response;
import ru.noleg.api.models.SetIsActiveRequest;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.mapper.PullRequestMapper;
import ru.noleg.prreviewerservice.mapper.UserMapper;
import ru.noleg.prreviewerservice.service.PullRequestService;
import ru.noleg.prreviewerservice.service.UserService;

import java.util.List;

@RestController
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PullRequestMapper pullRequestMapper;

    public UserController(UserService userService,
                          UserMapper userMapper,
                          PullRequestMapper pullRequestMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.pullRequestMapper = pullRequestMapper;
    }

    @Override
    public ResponseEntity<GetReview200Response> getReview(String userId) {
        List<PullRequestEntity> pullRequestsByUserId = userService.getReviewByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pullRequestMapper.toGetReviewResponse(userId, pullRequestsByUserId));
    }

    @Override
    public ResponseEntity<SetIsActive200Response> setIsActive(
            SetIsActiveRequest setIsActiveRequest
    ) {
        UserEntity userEntity = userService.setActive(setIsActiveRequest.getUserId(), setIsActiveRequest.getIsActive());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userMapper.mapToUserResponse(userEntity));
    }
}
