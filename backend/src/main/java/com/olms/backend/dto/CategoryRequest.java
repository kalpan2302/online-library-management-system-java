package com.olms.backend.dto;

import lombok.Data;

@Data
public class CategoryRequest {
    private String name;
    private Double fineRatePerDay; // optional - if null defaults to 0.0 in service
}