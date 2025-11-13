package ru.noleg.prreviewerservice.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "teams")
public class TeamEntity {

    @Id
    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<UserEntity> members = new HashSet<>();

    public void addMember(UserEntity user) {
        members.add(user);
        user.setTeam(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMembers(Set<UserEntity> members) {
        this.members = members;
    }

    public Set<UserEntity> getMembers() {
        return members;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TeamEntity teamEntity = (TeamEntity) o;
        return Objects.equals(title, teamEntity.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
