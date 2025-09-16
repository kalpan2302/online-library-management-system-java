package com.olms.backend.dto;

//import lombok.Data;
//
//@Data
//public class LoginRequest {
//    private String email;
//    private String password;
//}

// Abhishek
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
  @NotBlank @Email
  private String email;

  @NotBlank
  private String password;
}
