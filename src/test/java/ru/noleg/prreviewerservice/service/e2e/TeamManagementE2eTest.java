package ru.noleg.prreviewerservice.service.e2e;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.PullRequestStatus;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.repository.PullRequestRepository;
import ru.noleg.prreviewerservice.repository.TeamRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.utils.UserTestUtil;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamManagementE2eTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;
  @Autowired private TeamRepository teamRepository;
  @Autowired private PullRequestRepository prRepository;

  private static final String deactivatePath = "/teamManagements/deactivate";

  @BeforeEach
  void setup() {
    prRepository.deleteAll();
    userRepository.deleteAll();
    teamRepository.deleteAll();

    TeamEntity team = new TeamEntity();
    team.setTitle("backend");

    UserEntity u1 = UserTestUtil.createUser("u1", "Alice", true);
    UserEntity u2 = UserTestUtil.createUser("u2", "Bob", true);
    UserEntity u3 = UserTestUtil.createUser("u3", "Carol", true);
    UserEntity u4 = UserTestUtil.createUser("u4", "Dave", true);

    team.addMember(u1);
    team.addMember(u2);
    team.addMember(u3);
    team.addMember(u4);

    teamRepository.save(team);

    PullRequestEntity pr = new PullRequestEntity();
    pr.setId("pr1");
    pr.setTitle("Test PR");
    pr.setAuthor(u1);
    pr.setStatus(PullRequestStatus.OPEN);

    pr.setReviewers(Set.of(u2, u3));
    prRepository.save(pr);
  }

  @Test
  void deactivateUsers_shouldReassignReviewers() throws Exception {
    mockMvc
        .perform(post(deactivatePath).param("team_name", "backend"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pull_requests").isArray())
        .andExpect(jsonPath("$.pull_requests.length()").value(1))
        .andExpect(jsonPath("$.pull_requests[0].pull_request_id").value("pr1"))
        .andExpect(jsonPath("$.pull_requests[0].status").value("OPEN"));

    assertThat(userRepository.findAll().stream().noneMatch(UserEntity::isActive)).isTrue();
  }

  @Test
  void deactivateUsers_noReplacements_reviewersRemoved() throws Exception {
    userRepository
        .findById("u4")
        .ifPresent(
            u -> {
              u.setActive(false);
              userRepository.save(u);
            });

    mockMvc
        .perform(post(deactivatePath).param("team_name", "backend"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pull_requests[0].pull_request_id").value("pr1"));

    PullRequestEntity updated = prRepository.findWithReviewersById("pr1").get();
    assertThat(updated.getReviewers().isEmpty()).isTrue();
  }

  @Test
  void deactivateUsers_teamNotFound() throws Exception {
    mockMvc
        .perform(post(deactivatePath).param("team_name", "unknown"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
  }
}
