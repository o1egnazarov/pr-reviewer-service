package ru.noleg.prreviewerservice.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamEntity teamEntity;

    @OneToMany(mappedBy = "author")
    private Set<PullRequestEntity> authoredPullRequestEntities = new HashSet<>();

    @ManyToMany(mappedBy = "reviewers")
    private Set<PullRequestEntity> reviewingPullRequestEntities = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public TeamEntity getTeam() {
        return teamEntity;
    }

    public void setTeam(TeamEntity teamEntity) {
        this.teamEntity = teamEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity userEntity = (UserEntity) o;
        return isActive == userEntity.isActive &&
                Objects.equals(id, userEntity.id) &&
                Objects.equals(username, userEntity.username) &&
                Objects.equals(teamEntity, userEntity.teamEntity) &&
                Objects.equals(authoredPullRequestEntities, userEntity.authoredPullRequestEntities) &&
                Objects.equals(reviewingPullRequestEntities, userEntity.reviewingPullRequestEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, isActive, teamEntity, authoredPullRequestEntities, reviewingPullRequestEntities);
    }
}
