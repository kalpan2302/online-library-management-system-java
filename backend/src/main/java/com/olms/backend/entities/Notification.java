// com/olms/backend/entities/Notification.java
package com.olms.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name="notifications",
  indexes = {
    @Index(name="idx_notifications_user", columnList="user_id, sent_at"),
    @Index(name="idx_notifications_loan", columnList="loan_id, sent_at")
  }
)
public class Notification {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="user_id", nullable=false)
  private Long userId;

  @Column(name="loan_id")
  private Long loanId;

  @Enumerated(EnumType.STRING) @Column(nullable=false, length=32)
  private NotificationType type;

  @Enumerated(EnumType.STRING)
  @Column(name="delivery_method", nullable=false, length=16)
  @Builder.Default
  private DeliveryMethod deliveryMethod = DeliveryMethod.EMAIL;

  @Column(nullable=false, length=500)
  private String message;

  @Column(name="sent_at", nullable=false, updatable=false)
  @Builder.Default
  private Instant sentAt = Instant.now();

  @Column(name="read_at")
  private Instant readAt;
}
