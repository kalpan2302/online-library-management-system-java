package com.olms.backend.dto;

import lombok.Data;

@Data
public class UpdateAuthorRequest {
    private String name;
    private String bio;
}