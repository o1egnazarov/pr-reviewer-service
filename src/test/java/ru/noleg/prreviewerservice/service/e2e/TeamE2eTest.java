package ru.noleg.prreviewerservice.service.e2e;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamE2eTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private static final String addTeamPath = "/team/add";
  private static final String getTeamPath = "/team/get";

  @Test
  void addTeam_shouldCreateTeamSuccessfully() throws Exception {
    Map<String, Object> requestBody =
        Map.of(
            "team_name",
            "backendAddTeam",
            "members",
            List.of(
                Map.of("user_id", "u1AddTeam", "username", "AliceAddTeam", "is_active", true),
                Map.of("user_id", "u2AddTeam", "username", "BobAddTeam", "is_active", true)));

    mockMvc
        .perform(
            post(addTeamPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.team.team_name").value("backendAddTeam"))
        .andExpect(jsonPath("$.team.members").isArray())
        .andExpect(jsonPath("$.team.members[?(@.username=='AliceAddTeam')]").exists())
        .andExpect(jsonPath("$.team.members[?(@.username=='BobAddTeam')]").exists());
  }

  @Test
  void addTeam_shouldThrownException_whenTeamAlreadyExists() throws Exception {
    Map<String, Object> requestBody =
        Map.of(
            "team_name",
            "backendAddTeamError",
            "members",
            List.of(
                Map.of(
                    "user_id",
                    "u1AddTeamError",
                    "username",
                    "AliceAddTeamError",
                    "is_active",
                    true)));

    mockMvc
        .perform(
            post(addTeamPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post(addTeamPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("TEAM_EXISTS"));
  }

  @Test
  void getTeam_shouldGetTeamSuccessfully() throws Exception {
    Map<String, Object> requestBody =
        Map.of(
            "team_name",
            "backendGetTeam",
            "members",
            List.of(Map.of("user_id", "u1GetTeam", "username", "AliceGetTeam", "is_active", true)));

    mockMvc
        .perform(
            post(addTeamPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get(getTeamPath).param("team_name", "backendGetTeam"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.team_name").value("backendGetTeam"))
        .andExpect(jsonPath("$.members[0].username").value("AliceGetTeam"));
  }

  @Test
  void getTeam_shouldReturnNotFound_whenTeamNonExists() throws Exception {
    mockMvc
        .perform(get(getTeamPath).param("team_name", "nonexistent"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
  }
}
