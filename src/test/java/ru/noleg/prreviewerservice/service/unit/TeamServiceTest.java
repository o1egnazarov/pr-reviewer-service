package ru.noleg.prreviewerservice.service.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.exception.NotFoundException;
import ru.noleg.prreviewerservice.exception.TeamAlreadyExistException;
import ru.noleg.prreviewerservice.exception.UserAlreadyExistException;
import ru.noleg.prreviewerservice.repository.TeamRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.service.impl.TeamServiceDefaultImpl;
import ru.noleg.prreviewerservice.utils.UserTestUtil;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TeamServiceDefaultImpl teamService;

    @Test
    void createTeam_shouldSaveTeam_whenTitleIsUniqueAndMembersPresent() {
        // Arrange
        String teamTitle = "Team A";
        Set<UserEntity> members = Set.of(
                UserTestUtil.createUser("u1", "user1", true),
                UserTestUtil.createUser("u2", "user2", true),
                UserTestUtil.createUser("u3", "user2", false)
        );

        when(teamRepository.existsByTitle(teamTitle)).thenReturn(false);
        when(userRepository.existsById("u1")).thenReturn(false);
        when(userRepository.existsById("u2")).thenReturn(false);
        when(userRepository.existsById("u3")).thenReturn(false);
        when(teamRepository.save(any(TeamEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TeamEntity result = teamService.createTeam(teamTitle, members);

        // Assert
        assertNotNull(result);
        assertEquals(teamTitle, result.getTitle());
        assertEquals(3, result.getMembers().size());
        verify(teamRepository).existsByTitle(teamTitle);
        verify(teamRepository).save(result);
    }

    @Test
    void createTeam_shouldThrowException_whenTitleAlreadyExists() {
        // Arrange
        String teamTitle = "Team A";
        when(teamRepository.existsByTitle(teamTitle)).thenReturn(true);

        // Act | Assert
        TeamAlreadyExistException ex = assertThrows(TeamAlreadyExistException.class,
                () -> teamService.createTeam(teamTitle, Collections.emptySet())
        );

        assertEquals(ErrorCode.TEAM_EXISTS, ex.getErrorCode());
        verify(teamRepository).existsByTitle(teamTitle);
        verify(teamRepository, never()).save(any());
    }

    @Test
    void createTeam_shouldHandleNullMembers() {
        // Arrange
        String teamTitle = "Team B";
        when(teamRepository.existsByTitle(teamTitle)).thenReturn(false);
        when(teamRepository.save(any(TeamEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TeamEntity result = teamService.createTeam(teamTitle, null);

        // Assert
        assertNotNull(result);
        assertEquals(teamTitle, result.getTitle());
        assertTrue(result.getMembers().isEmpty());
        verify(teamRepository).existsByTitle(teamTitle);
        verify(teamRepository).save(result);
    }

    @Test
    void createTeam_shouldHandleEmptyMembers() {
        // Arrange
        String teamTitle = "Team B";
        when(teamRepository.existsByTitle(teamTitle)).thenReturn(false);
        when(teamRepository.save(any(TeamEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TeamEntity result = teamService.createTeam(teamTitle, Set.of());

        // Assert
        assertNotNull(result);
        assertEquals(teamTitle, result.getTitle());
        assertTrue(result.getMembers().isEmpty());
        verify(teamRepository).existsByTitle(teamTitle);
        verify(teamRepository).save(result);
    }

    @Test
    void createTeam_shouldThrownException_whenUserAlreadyInTeam() {
        // Arrange
        String teamTitle = "Team A";
        Set<UserEntity> members = Set.of(
                UserTestUtil.createUser("u1", "user1", true)
        );

        when(teamRepository.existsByTitle(teamTitle)).thenReturn(false);
        when(userRepository.existsById("u1")).thenReturn(true);

        // Act | Assert
        UserAlreadyExistException ex = assertThrows(UserAlreadyExistException.class,
                () -> teamService.createTeam(teamTitle, members)
        );

        assertEquals(ErrorCode.USER_EXISTS, ex.getErrorCode());
        verify(teamRepository, never()).save(any(TeamEntity.class));
    }

    @Test
    void getTeam_shouldReturnTeam_whenTeamExists() {
        // Arrange
        String title = "Team A";
        TeamEntity team = new TeamEntity();
        team.setTitle(title);

        when(teamRepository.findByTitle(title)).thenReturn(Optional.of(team));

        // Act
        TeamEntity result = teamService.getTeam(title);

        // Assert
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        verify(teamRepository).findByTitle(title);
    }

    @Test
    void getTeam_shouldThrowException_whenTeamNotFound() {
        // Arrange
        String title = "Unknown Team";
        when(teamRepository.findByTitle(title)).thenReturn(Optional.empty());

        // Act | Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> teamService.getTeam(title)
        );

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
        verify(teamRepository).findByTitle(title);
    }
}