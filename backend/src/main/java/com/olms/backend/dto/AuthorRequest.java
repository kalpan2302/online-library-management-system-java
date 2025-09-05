package com.olms.backend.dto;

import lombok.Data;

@Data
public class AuthorRequest {
    private String name;
    private String bio;
}