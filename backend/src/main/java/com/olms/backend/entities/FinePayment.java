package com.olms.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "fine_payments")
@Data
public class FinePayment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long paymentId;

  @ManyToOne
  @JoinColumn(name = "fine_id", nullable = false)
  private Fine fine;

  private Double amountPaid;

  private String paymentMethod; // CASH, UPI, CARD, ONLINE etc.

  private String transactionRef;

  @ManyToOne
  @JoinColumn(name = "paid_by", nullable = false)
  private User paidBy;

  @Column(name = "paid_at")
  private LocalDateTime paidAt = LocalDateTime.now();

  @Enumerated(EnumType.STRING)
  private FinePaymentStatus status = FinePaymentStatus.SUCCESS;

  private String note;

  @PrePersist
  public void prePersist() {
    if (paidAt == null) paidAt = LocalDateTime.now();
  }
}
