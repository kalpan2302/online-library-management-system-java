package com.olms.backend.dto;

import lombok.Data;

@Data
public class UpdatePublicationRequest {
    private String name;
    private String address;
}