package ru.noleg.prreviewerservice.entity;

import jakarta.persistence.*;

import java.util.*;

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
    private TeamEntity team;

    @OneToMany(mappedBy = "author")
    private List<PullRequestEntity> authoredPullRequestEntities = new ArrayList<>();

    @ManyToMany(mappedBy = "reviewers")
    private List<PullRequestEntity> reviewingPullRequestEntities = new ArrayList<>();

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
        return team;
    }

    public void setTeam(TeamEntity teamEntity) {
        this.team = teamEntity;
    }

    public List<PullRequestEntity> getReviewingPullRequestEntities() {
        return reviewingPullRequestEntities;
    }

    public void setReviewingPullRequestEntities(List<PullRequestEntity> reviewingPullRequestEntities) {
        this.reviewingPullRequestEntities = reviewingPullRequestEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity userEntity = (UserEntity) o;
        return isActive == userEntity.isActive &&
                Objects.equals(id, userEntity.id) &&
                Objects.equals(username, userEntity.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, isActive);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
