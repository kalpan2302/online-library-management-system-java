package com.olms.backend.dto;

import lombok.Data;

@Data
public class UpdateBookRequest {
    private String title;
    private String isbn;
    private Integer totalCopies;
    private Integer availableCopies;
    private String shelfLocation;
}