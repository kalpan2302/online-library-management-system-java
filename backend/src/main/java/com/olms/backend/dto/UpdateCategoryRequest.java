package com.olms.backend.dto;

import lombok.Data;

@Data
public class UpdateCategoryRequest {
    private String name;
    private Double fineRatePerDay;
}