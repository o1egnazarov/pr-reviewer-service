package ru.noleg.prreviewerservice.service.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.PullRequestStatus;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.repository.PullRequestRepository;
import ru.noleg.prreviewerservice.repository.TeamRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.service.TeamManagementService;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TeamManagementPerformanceTest {

    @Autowired
    private TeamManagementService teamManagementService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    void testDeactivateUsersAndReassign_Performance() {
        // ----------------------------
        // 1. Подготовка данных
        // ----------------------------
        TeamEntity team = new TeamEntity();
        team.setTitle("backend");
        teamRepository.save(team);

        // 100 активных пользователей
        for (int i = 0; i < 100; i++) {
            UserEntity u = new UserEntity();
            u.setId(UUID.randomUUID().toString());
            u.setUsername("user_" + i);
            u.setActive(true);
            u.setTeam(team);
            userRepository.save(u);
        }

        // 200 PR, каждый с несколькими ревьюерами
        List<UserEntity> users = userRepository.findAll();

        for (int i = 0; i < 200; i++) {
            PullRequestEntity pr = new PullRequestEntity();
            pr.setId(UUID.randomUUID().toString());
            pr.setTitle("PR_" + i);
            pr.setStatus(PullRequestStatus.OPEN);
            pr.setAuthor(users.get(i % users.size()));

            pr.setReviewers(new HashSet<>(users.subList(0, 5))); // 5 ревьюеров

            pullRequestRepository.save(pr);
        }

        // ----------------------------
        // 2. Запуск замера
        // ----------------------------
        long start = System.currentTimeMillis();

        teamManagementService.deactivateUsersAndReassign("backend");

        long duration = System.currentTimeMillis() - start;

        System.out.println("Execution time = " + duration + " ms");

        // ----------------------------
        // 3. Проверка времени
        // ----------------------------
        Assertions.assertTrue(duration <= 100,
                "Метод должен выполняться ≤ 100 ms, но занял " + duration + " ms");
    }
}
