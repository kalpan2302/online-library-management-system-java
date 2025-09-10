package com.olms.backend.dto;

import lombok.Data;

@Data
public class AddUserRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role; // "USER" or "ADMIN"
}