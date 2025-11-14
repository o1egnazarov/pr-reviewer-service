package ru.noleg.prreviewerservice.service.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.exception.NotFoundException;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.service.impl.UserServiceDefaultImpl;
import ru.noleg.prreviewerservice.utils.UserTestUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceDefaultImpl userService;

    @Test
    void setActive_shouldSetActive_whenUserExist() {
        // Arrange
        String userId = "u1";
        boolean isActive = true;
        UserEntity user = UserTestUtil.createUser(userId, "user 1", false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserEntity result = userService.setActive(userId, isActive);

        // Assert
        assertNotNull(result);
        assertTrue(result.isActive());
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    void setActive_shouldThrownException_whenUserNotFound() {
        // Arrange
        String userId = "not found";
        boolean isActive = true;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act | Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.setActive(userId, isActive)
        );

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void getReviewByUserId_ShouldReturnReviews_WhenUserExists() {
        // Arrange
        String reviewerId = "r1";
        UserEntity reviewer = new UserEntity();
        reviewer.setId(reviewerId);

        PullRequestEntity pr1 = new PullRequestEntity();
        PullRequestEntity pr2 = new PullRequestEntity();

        reviewer.setReviewingPullRequestEntities(List.of(pr1, pr2));

        when(userRepository.findWithReviewerPullRequestsById(reviewerId)).thenReturn(Optional.of(reviewer));

        // Act
        List<PullRequestEntity> result = userService.getReviewByUserId(reviewerId);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void getReviewByUserId_ShouldThrownException_WhenUserNotExists() {
        // Arrange
        String reviewerId = "r1-notfound";
        UserEntity reviewer = new UserEntity();
        reviewer.setId(reviewerId);

        when(userRepository.findWithReviewerPullRequestsById(reviewerId)).thenReturn(Optional.empty());

        // Act | Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.getReviewByUserId(reviewerId)
        );

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }
}