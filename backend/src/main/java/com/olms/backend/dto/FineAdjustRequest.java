package com.olms.backend.dto;

import lombok.Data;

@Data
public class FineAdjustRequest {
  private Double newAmount;
  private String note;
}
