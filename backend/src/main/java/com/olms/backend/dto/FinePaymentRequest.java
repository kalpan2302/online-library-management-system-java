package com.olms.backend.dto;

import lombok.Data;

@Data
public class FinePaymentRequest {
  private Long fineId;
  private Double amountPaid;
  private String paymentMethod;
  private String transactionRef;
  private String note;
}
