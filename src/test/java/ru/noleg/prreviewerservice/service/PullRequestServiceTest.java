package ru.noleg.prreviewerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.PullRequestStatus;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.DomainException;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.exception.NotFoundException;
import ru.noleg.prreviewerservice.repository.PullRequestRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.service.impl.PullRequestServiceDefaultImpl;
import ru.noleg.prreviewerservice.utils.UserTestUtil;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PullRequestServiceTest {
    @Mock
    private PullRequestRepository pullRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PullRequestServiceDefaultImpl pullRequestService;

    @Test
    void createPullRequest_ShouldCreatePR_WhenValidInput() {
        // Arrange
        String prId = "PR-1";
        String title = "Test PR";
        String authorId = "user-1";

        TeamEntity team = new TeamEntity();

        UserEntity author = UserTestUtil.createUser(authorId, "author", true);
        author.setTeam(team);

        UserEntity reviewer1 = UserTestUtil.createUser("r1", "rev1", true);
        reviewer1.setTeam(team);

        UserEntity reviewer2 = UserTestUtil.createUser("r2", "rev2", true);
        reviewer2.setTeam(team);


        when(pullRequestRepository.existsById(prId)).thenReturn(false);
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(userRepository.findByTeamAndIsActiveTrue(team)).thenReturn(List.of(author, reviewer1, reviewer2));
        when(pullRequestRepository.save(any(PullRequestEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PullRequestEntity result = pullRequestService.createPullRequest(prId, title, authorId);

        // Assert
        assertEquals(prId, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(author, result.getAuthor());
        assertEquals(2, result.getReviewers().size());
        assertFalse(result.isNeedMoreReviewers());
        verify(pullRequestRepository).save(any(PullRequestEntity.class));
    }

    @Test
    void createPullRequest_ShouldCreatePR_WhenNotEnoughReviewers() {
        // Arrange
        String prId = "PR-1";
        String title = "Test PR";
        String authorId = "user-1";

        TeamEntity team = new TeamEntity();

        UserEntity author = UserTestUtil.createUser(authorId, "author", true);
        author.setTeam(team);

        UserEntity reviewer1 = UserTestUtil.createUser("r1", "rev1", true);
        reviewer1.setTeam(team);


        when(pullRequestRepository.existsById(prId)).thenReturn(false);
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(userRepository.findByTeamAndIsActiveTrue(team)).thenReturn(List.of(author, reviewer1));
        when(pullRequestRepository.save(any(PullRequestEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PullRequestEntity result = pullRequestService.createPullRequest(prId, title, authorId);

        // Assert
        assertEquals(prId, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(author, result.getAuthor());
        assertEquals(1, result.getReviewers().size());
        assertTrue(result.isNeedMoreReviewers());
        verify(pullRequestRepository).save(any(PullRequestEntity.class));
    }

    @Test
    void createPullRequest_ShouldThrownException_WhenPrAlreadyExist() {
        // Arrange
        String prId = "PR-1-exist";
        String title = "Test PR";
        String authorId = "user-1";

        when(pullRequestRepository.existsById(prId)).thenReturn(true);

        // Act | Assert
        DomainException ex = assertThrows(DomainException.class,
                () -> pullRequestService.createPullRequest(prId, title, authorId)
        );

        assertEquals(ErrorCode.PR_EXISTS, ex.getErrorCode());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }

    @Test
    void createPullRequest_ShouldThrownException_WhenAuthorNotFound() {
        // Arrange
        String prId = "PR-1";
        String title = "Test PR";
        String authorId = "user-1";

        when(pullRequestRepository.existsById(prId)).thenReturn(false);
        when(userRepository.findById(authorId)).thenReturn(Optional.empty());

        // Act | Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> pullRequestService.createPullRequest(prId, title, authorId)
        );

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }

    @Test
    void createPullRequest_ShouldThrownException_WhenAuthorDoesntHaveTeam() {
        // Arrange
        String prId = "PR-1";
        String title = "Test PR";
        String authorId = "user-1";

        UserEntity author = UserTestUtil.createUser(authorId, "author", true);
        author.setTeam(null);


        when(pullRequestRepository.existsById(prId)).thenReturn(false);
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));

        // Act | Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> pullRequestService.createPullRequest(prId, title, authorId)
        );

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }

    @Test
    void mergePullRequest_ShouldSetMergedStatus_WhenNotMergedYet() {
        // Arrange
        String prId = "PR-1";
        PullRequestEntity pr = new PullRequestEntity();
        pr.setId(prId);
        pr.setStatus(PullRequestStatus.OPEN);

        when(pullRequestRepository.findById(prId)).thenReturn(Optional.of(pr));
        when(pullRequestRepository.save(any(PullRequestEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PullRequestEntity result = pullRequestService.mergePullRequest(prId);

        // Assert
        assertEquals(PullRequestStatus.MERGED, result.getStatus());
        assertNotNull(result.getMergedAt());
        verify(pullRequestRepository).save(any(PullRequestEntity.class));
    }

    @Test
    void mergePullRequest_ShouldThrownException_WhenPrNotFound() {
        // Arrange
        String prId = "PR-1-not-found";

        when(pullRequestRepository.findById(prId)).thenReturn(Optional.empty());

        // Act | Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> pullRequestService.mergePullRequest(prId)
        );

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }

    @Test
    void mergePullRequest_ShouldNothingWillHappen_WhenSetStatusFromMergedToMerged() {
        // Arrange
        String prId = "PR-1";
        PullRequestEntity pr = new PullRequestEntity();
        pr.setId(prId);
        pr.setStatus(PullRequestStatus.MERGED);
        LocalDateTime mergedAt = LocalDateTime.of(2004, 8, 8, 8, 0);
        pr.setMergedAt(mergedAt);

        when(pullRequestRepository.findById(prId)).thenReturn(Optional.of(pr));

        // Act
        PullRequestEntity result = pullRequestService.mergePullRequest(prId);

        // Assert
        assertEquals(PullRequestStatus.MERGED, result.getStatus());
        assertEquals(mergedAt, result.getMergedAt());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }

    @Test
    void reassignReviewer_ShouldReplaceReviewer_WhenValid() {
        // Arrange
        String prId = "PR-1";
        String oldReviewerId = "r1";

        TeamEntity team = new TeamEntity();

        UserEntity author = UserTestUtil.createUser("author", "author", false);
        author.setTeam(team);

        UserEntity oldReviewer = UserTestUtil.createUser(oldReviewerId, "oldReviewer", false);
        oldReviewer.setTeam(team);

        UserEntity newReviewer = UserTestUtil.createUser("r2", "newReviewer", true);
        newReviewer.setTeam(team);

        PullRequestEntity pr = new PullRequestEntity();
        pr.setId(prId);
        pr.setAuthor(author);
        pr.setStatus(PullRequestStatus.OPEN);
        pr.setReviewers(new HashSet<>(List.of(oldReviewer)));

        when(pullRequestRepository.findById(prId)).thenReturn(Optional.of(pr));
        when(userRepository.findById(oldReviewerId)).thenReturn(Optional.of(oldReviewer));
        when(userRepository.findByTeamAndIsActiveTrue(team)).thenReturn(List.of(newReviewer, author));
        when(pullRequestRepository.save(any(PullRequestEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PullRequestEntity result = pullRequestService.reassignReviewer(prId, oldReviewerId);

        // Assert
        assertTrue(result.getReviewers().contains(newReviewer));
        assertFalse(result.getReviewers().contains(oldReviewer));
        verify(pullRequestRepository).save(any(PullRequestEntity.class));
    }

    @Test
    void reassignReviewer_ShouldThrownException_WhenPullRequestNotFound() {
        // Arrange
        String prId = "PR-404";
        String oldReviewerId = "r1";
        when(pullRequestRepository.findById(prId)).thenReturn(Optional.empty());

        // Act | Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> pullRequestService.reassignReviewer(prId, oldReviewerId)
        );

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }

    @Test
    void reassignReviewer_ShouldThrownException_WhenPRAlreadyMerged() {
        // Arrange
        String prId = "PR-1";
        String oldReviewerId = "r1";

        PullRequestEntity mergedPR = new PullRequestEntity();
        mergedPR.setId(prId);
        mergedPR.setStatus(PullRequestStatus.MERGED);

        when(pullRequestRepository.findById(prId)).thenReturn(Optional.of(mergedPR));

        // Act | Assert
        DomainException ex = assertThrows(DomainException.class,
                () -> pullRequestService.reassignReviewer(prId, oldReviewerId)
        );

        assertEquals(ErrorCode.PR_MERGED, ex.getErrorCode());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }


    @Test
    void reassignReviewer_ShouldThrownException_WhenOldReviewerNotFound() {
        // Arrange
        String prId = "PR-1";
        String oldReviewerId = "r1";

        PullRequestEntity pr = new PullRequestEntity();
        pr.setId(prId);
        pr.setStatus(PullRequestStatus.OPEN);
        pr.setReviewers(Set.of());

        when(pullRequestRepository.findById(prId)).thenReturn(Optional.of(pr));
        when(userRepository.findById(oldReviewerId)).thenReturn(Optional.empty());

        // Act | Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> pullRequestService.reassignReviewer(prId, oldReviewerId)
        );

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }

    @Test
    void reassignReviewer_ShouldThrownException_WhenOldReviewerNotAssigned() {
        // Arrange
        String prId = "PR-1";
        String oldReviewerId = "r1";

        PullRequestEntity pr = new PullRequestEntity();
        pr.setId(prId);
        pr.setStatus(PullRequestStatus.OPEN);
        pr.setReviewers(new HashSet<>());

        UserEntity oldReviewer = UserTestUtil.createUser(oldReviewerId, "oldReviewer", true);

        when(pullRequestRepository.findById(prId)).thenReturn(Optional.of(pr));
        when(userRepository.findById(oldReviewerId)).thenReturn(Optional.of(oldReviewer));

        // Act | Assert
        DomainException ex = assertThrows(DomainException.class,
                () -> pullRequestService.reassignReviewer(prId, oldReviewerId)
        );

        assertEquals(ErrorCode.NOT_ASSIGNED, ex.getErrorCode());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }

    @Test
    void reassignReviewer_ShouldThrownException_WhenAuthorHasNoTeam() {
        // Arrange
        String prId = "PR-1";
        String oldReviewerId = "r1";

        UserEntity author = UserTestUtil.createUser("author", "author", true);
        author.setTeam(null);

        UserEntity oldReviewer = UserTestUtil.createUser(oldReviewerId, "oldReviewer", true);

        PullRequestEntity pr = new PullRequestEntity();
        pr.setId(prId);
        pr.setStatus(PullRequestStatus.OPEN);
        pr.setAuthor(author);
        pr.setReviewers(new HashSet<>(List.of(oldReviewer)));

        when(pullRequestRepository.findById(prId)).thenReturn(Optional.of(pr));
        when(userRepository.findById(oldReviewerId)).thenReturn(Optional.of(oldReviewer));

        // Act | Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> pullRequestService.reassignReviewer(prId, oldReviewerId)
        );

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }

    @Test
    void reassignReviewer_ShouldThrownException_WhenNoAvailableReplacementFound() {
        // Arrange
        String prId = "PR-1";
        String oldReviewerId = "r1";

        TeamEntity team = new TeamEntity();

        UserEntity author = UserTestUtil.createUser("author", "author", true);
        author.setTeam(team);

        UserEntity oldReviewer = UserTestUtil.createUser(oldReviewerId, "oldReviewer", true);
        oldReviewer.setTeam(team);

        PullRequestEntity pr = new PullRequestEntity();
        pr.setId(prId);
        pr.setAuthor(author);
        pr.setStatus(PullRequestStatus.OPEN);
        pr.setReviewers(new HashSet<>(List.of(oldReviewer)));

        when(pullRequestRepository.findById(prId)).thenReturn(Optional.of(pr));
        when(userRepository.findById(oldReviewerId)).thenReturn(Optional.of(oldReviewer));
        when(userRepository.findByTeamAndIsActiveTrue(team)).thenReturn(List.of(author, oldReviewer));

        // Act | Assert
        DomainException ex = assertThrows(DomainException.class,
                () -> pullRequestService.reassignReviewer(prId, oldReviewerId)
        );

        assertEquals(ErrorCode.NO_CANDIDATE, ex.getErrorCode());
        verify(pullRequestRepository, never()).save(any(PullRequestEntity.class));
    }


    @Test
    void getReviewByUserId_ShouldReturnReviews_WhenUserExists() {
        // Arrange
        String reviewerId = "r1";
        UserEntity reviewer = new UserEntity();
        reviewer.setId(reviewerId);

        PullRequestEntity pr1 = new PullRequestEntity();
        PullRequestEntity pr2 = new PullRequestEntity();

        when(userRepository.findById(reviewerId)).thenReturn(Optional.of(reviewer));
        when(pullRequestRepository.findByReviewersContains(reviewer)).thenReturn(List.of(pr1, pr2));

        // Act
        List<PullRequestEntity> result = pullRequestService.getReviewByUserId(reviewerId);

        // Assert
        assertEquals(2, result.size());
        verify(pullRequestRepository).findByReviewersContains(reviewer);
    }

    @Test
    void getReviewByUserId_ShouldThrownException_WhenUserNotExists() {
        // Arrange
        String reviewerId = "r1-notfound";
        UserEntity reviewer = new UserEntity();
        reviewer.setId(reviewerId);

        when(userRepository.findById(reviewerId)).thenReturn(Optional.empty());

        // Act | Assert
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> pullRequestService.getReviewByUserId(reviewerId)
        );

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
        verify(pullRequestRepository, never()).findByReviewersContains(reviewer);
    }
}