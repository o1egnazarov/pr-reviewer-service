package ru.noleg.prreviewerservice.service.e2e;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.noleg.prreviewerservice.entity.PullRequestEntity;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.repository.PullRequestRepository;
import ru.noleg.prreviewerservice.repository.TeamRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.utils.UserTestUtil;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserE2eTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private TeamRepository teamRepository;

  @Autowired private PullRequestRepository prRepository;

  private static final String userSetIsActivePath = "/users/setIsActive";
  private static final String usersGetReviewPath = "/users/getReview";

  @BeforeAll
  void setup() {
    TeamEntity team = new TeamEntity();
    team.setTitle("backend");
    teamRepository.save(team);

    UserEntity user1 = UserTestUtil.createUser("u1", "Oleg", true);
    user1.setTeam(team);

    UserEntity user2 = UserTestUtil.createUser("u2", "David", true);
    user2.setTeam(team);

    userRepository.saveAll(List.of(user1, user2));

    PullRequestEntity pr1 = new PullRequestEntity();
    pr1.setId("pr-1001");
    pr1.setTitle("Add search");
    pr1.setAuthor(user1);
    pr1.getReviewers().add(user2);
    prRepository.save(pr1);
  }

  @Test
  void setIsActive_ShouldUpdateUser() throws Exception {
    mockMvc
        .perform(
            post(userSetIsActivePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user_id\":\"u2\", \"is_active\": false}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.user_id").value("u2"))
        .andExpect(jsonPath("$.user.is_active").value(false));
  }

  @Test
  void getReview_ShouldReturnUserPrs() throws Exception {
    mockMvc
        .perform(get(usersGetReviewPath).param("user_id", "u2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user_id").value("u2"))
        .andExpect(jsonPath("$.pull_requests[0].pull_request_id").value("pr-1001"));
  }

  @Test
  void setIsActive_ShouldReturn404_WhenUserNotFound() throws Exception {
    mockMvc
        .perform(
            post(userSetIsActivePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user_id\":\"nonexistent\", \"is_active\": true}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getReview_ShouldReturn404_WhenUserNotFound() throws Exception {
    mockMvc
        .perform(get(usersGetReviewPath).param("user_id", "nonexistent"))
        .andExpect(status().isNotFound());
  }
}
