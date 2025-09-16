package com.olms.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "fines")
@Data
public class Fine {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long fineId;

  @ManyToOne
  @JoinColumn(name = "loan_id", nullable = false)
  private Loan loan;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private Double amount = 0.0;

  @Enumerated(EnumType.STRING)
  private FineStatus status = FineStatus.UNPAID;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "updated_at")
  private LocalDateTime updatedAt = LocalDateTime.now();

  private String note;

  @PrePersist
  public void prePersist() {
    createdAt = LocalDateTime.now();
    updatedAt = createdAt;
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
