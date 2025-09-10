package com.olms.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookRequest {
    private String title;
    private String isbn;
    private Integer totalCopies;
    private Integer availableCopies;
    private String shelfLocation;
    private List<String> authorNames;
    private String publicationName;
    private String categoryName;
}