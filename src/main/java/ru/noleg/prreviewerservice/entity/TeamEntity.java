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

    public void addMember(UserEntity userEntity) {
        members.add(userEntity);
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TeamEntity teamEntity = (TeamEntity) o;
        return Objects.equals(title, teamEntity.title) &&
                Objects.equals(members, teamEntity.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, members);
    }
}
