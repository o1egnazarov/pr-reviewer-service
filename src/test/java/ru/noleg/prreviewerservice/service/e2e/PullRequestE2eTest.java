package ru.noleg.prreviewerservice.service.e2e;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.noleg.api.models.CreatePullRequestRequest;
import ru.noleg.api.models.MergePullRequestRequest;
import ru.noleg.api.models.ReassignReviewerRequest;
import ru.noleg.prreviewerservice.entity.TeamEntity;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.repository.PullRequestRepository;
import ru.noleg.prreviewerservice.repository.TeamRepository;
import ru.noleg.prreviewerservice.repository.UserRepository;
import ru.noleg.prreviewerservice.utils.UserTestUtil;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PullRequestE2eTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;
  @Autowired private TeamRepository teamRepository;
  @Autowired private PullRequestRepository prRepository;

  private static final String createPullRequestPath = "/pullRequest/create";
  private static final String mergePullRequestPath = "/pullRequest/merge";
  private static final String reassignPullRequestPath = "/pullRequest/reassign";

  @BeforeEach
  void setup() {
    prRepository.deleteAll();
    userRepository.deleteAll();
    teamRepository.deleteAll();

    TeamEntity team = new TeamEntity();
    team.setTitle("backend");

    final UserEntity a1 = UserTestUtil.createUser("u1", "username1", true);
    final UserEntity a2 = UserTestUtil.createUser("u2", "username2", true);
    final UserEntity a3 = UserTestUtil.createUser("u3", "username3", true);
    final UserEntity a4 = UserTestUtil.createUser("not-in-reviewers", "username4", true);

    team.addMember(a1);
    team.addMember(a2);
    team.addMember(a3);

    teamRepository.save(team);
    userRepository.save(a4);
  }

  @Test
  void createPr_shouldCreatePrSuccessfully() throws Exception {
    CreatePullRequestRequest req =
        new CreatePullRequestRequest()
            .pullRequestId("pr1_createPr")
            .pullRequestName("Test PR")
            .authorId("u1");

    mockMvc
        .perform(
            post(createPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("pr.pull_request_id").value("pr1_createPr"))
        .andExpect(jsonPath("pr.assigned_reviewers").isArray())
        .andExpect(jsonPath("pr.assigned_reviewers.length()").value(2));
  }

  @Test
  void createPr_should404_whenAuthorNotFound() throws Exception {
    CreatePullRequestRequest req =
        new CreatePullRequestRequest()
            .pullRequestId("pr1_createPr_404")
            .pullRequestName("Test")
            .authorId("xxx");

    mockMvc
        .perform(
            post(createPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isNotFound());
  }

  @Test
  void createPr_should400_whenAuthorInactive() throws Exception {
    userRepository
        .findById("u1")
        .ifPresent(
            u -> {
              u.setActive(false);
              userRepository.save(u);
            });

    CreatePullRequestRequest req =
        new CreatePullRequestRequest()
            .pullRequestId("pr1_createPr_400")
            .pullRequestName("Test")
            .authorId("u1");

    mockMvc
        .perform(
            post(createPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createPr_should409_whenPrExists() throws Exception {
    CreatePullRequestRequest req1 =
        new CreatePullRequestRequest()
            .pullRequestId("pr1_createPr_409")
            .pullRequestName("Test2")
            .authorId("u1");

    CreatePullRequestRequest req2 =
        new CreatePullRequestRequest()
            .pullRequestId("pr1_createPr_409")
            .pullRequestName("Test2")
            .authorId("u1");

    mockMvc
        .perform(
            post(createPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post(createPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
        .andExpect(status().isConflict());
  }

  @Test
  void reassignReviewer_shouldReassignSuccessfully() throws Exception {
    CreatePullRequestRequest req =
        new CreatePullRequestRequest()
            .pullRequestId("pr1_reassign")
            .pullRequestName("Test")
            .authorId("u1");

    mockMvc
        .perform(
            post(createPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated());

    ReassignReviewerRequest reqForReassign =
        new ReassignReviewerRequest().pullRequestId("pr1_reassign").oldUserId("u2");

    mockMvc
        .perform(
            post(reassignPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqForReassign)))
        .andExpect(status().isOk());
  }

  @Test
  void reassignReviewer_should400_whenReviewerNotAssigned() throws Exception {
    CreatePullRequestRequest req =
        new CreatePullRequestRequest()
            .pullRequestId("pr1_reassign_400")
            .pullRequestName("Test")
            .authorId("u1");

    mockMvc
        .perform(
            post(createPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated());

    ReassignReviewerRequest reqForReassign =
        new ReassignReviewerRequest()
            .pullRequestId("pr1_reassign_400")
            .oldUserId("not-in-reviewers");

    mockMvc
        .perform(
            post(reassignPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqForReassign)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void reassignReviewer_should409_whenOnMergedPr() throws Exception {
    CreatePullRequestRequest req =
        new CreatePullRequestRequest()
            .pullRequestId("pr1_reassign_merge_409")
            .pullRequestName("Test")
            .authorId("u1");

    mockMvc
        .perform(
            post(createPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated());

    MergePullRequestRequest mergeReq =
        new MergePullRequestRequest().pullRequestId("pr1_reassign_merge_409");
    mockMvc
        .perform(
            post(mergePullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mergeReq)))
        .andExpect(status().isOk());

    ReassignReviewerRequest reqForReassign =
        new ReassignReviewerRequest().pullRequestId("pr1_reassign_merge_409").oldUserId("u2");

    mockMvc
        .perform(
            post(reassignPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqForReassign)))
        .andExpect(status().isConflict());
  }

  @Test
  void reassign_noCandidates_400() throws Exception {
    userRepository
        .findAll()
        .forEach(
            u -> {
              if (!u.getId().equals("u1")) {
                u.setActive(false);
                userRepository.save(u);
              }
            });

    CreatePullRequestRequest req =
        new CreatePullRequestRequest()
            .pullRequestId("pr1_reassign_noCandidates_400")
            .pullRequestName("Test")
            .authorId("u1");

    mockMvc
        .perform(
            post(createPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated());

    ReassignReviewerRequest reqForReassign =
        new ReassignReviewerRequest()
            .pullRequestId("pr1_reassign_noCandidates_400")
            .oldUserId("u2");

    mockMvc
        .perform(
            post(reassignPullRequestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqForReassign)))
        .andExpect(status().isBadRequest());
  }
}
