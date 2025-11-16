package ru.noleg.prreviewerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "pull_requests")
public class PullRequestEntity {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private PullRequestStatus status = PullRequestStatus.OPEN;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id")
  private UserEntity author;

  @ManyToMany
  @JoinTable(
      name = "pull_requests_reviewers",
      joinColumns = @JoinColumn(name = "pull_request_id"),
      inverseJoinColumns = @JoinColumn(name = "reviewer_id"))
  private Set<UserEntity> reviewers = new HashSet<>();

  @Column(name = "need_more_reviewers", nullable = false)
  private boolean needMoreReviewers;

  @Column(name = "merged_at")
  private LocalDateTime mergedAt;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public PullRequestStatus getStatus() {
    return status;
  }

  public void setStatus(PullRequestStatus status) {
    this.status = status;
  }

  public UserEntity getAuthor() {
    return author;
  }

  public void setAuthor(UserEntity author) {
    this.author = author;
  }

  public Set<UserEntity> getReviewers() {
    return reviewers;
  }

  public void setReviewers(Set<UserEntity> reviewers) {
    this.reviewers = reviewers;
  }

  public void setNeedMoreReviewers(boolean needMoreReviewers) {
    this.needMoreReviewers = needMoreReviewers;
  }

  public boolean isNeedMoreReviewers() {
    return needMoreReviewers;
  }

  public void setMergedAt(LocalDateTime mergedAt) {
    this.mergedAt = mergedAt;
  }

  public LocalDateTime getMergedAt() {
    return mergedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PullRequestEntity that = (PullRequestEntity) o;
    return needMoreReviewers == that.needMoreReviewers
        && Objects.equals(id, that.id)
        && Objects.equals(title, that.title)
        && status == that.status
        && Objects.equals(mergedAt, that.mergedAt)
        && Objects.equals(createdAt, that.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, status, needMoreReviewers, mergedAt, createdAt);
  }

  @Override
  public String toString() {
    return "PullRequestEntity{"
        + "id='"
        + id
        + '\''
        + ", title='"
        + title
        + '\''
        + ", status="
        + status
        + '}';
  }
}
