package ru.noleg.prreviewerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.exception.NotFoundException;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.service.impl.UserServiceDefaultImpl;
import ru.noleg.prreviewerservice.utils.UserTestUtil;

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
}