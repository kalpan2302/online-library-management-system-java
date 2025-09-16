package com.olms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olms.backend.dto.LoginRequest;
import com.olms.backend.dto.RegisterRequest;
import com.olms.backend.entities.AccountStatus;
import com.olms.backend.entities.Role;
import com.olms.backend.entities.User;
import com.olms.backend.repository.UserRepository;
import com.olms.backend.security.JwtUtil;
import com.olms.backend.security.SecurityUtil;
import com.olms.backend.service.VerificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private VerificationService verificationService;
    @MockBean
    private JwtUtil jwtUtil;

    @Nested
    @DisplayName("POST /auth/register")
    class RegisterTests {
        @Test
        @DisplayName("✅ Should register a new user successfully")
        void whenRegister_withNewEmail_shouldReturnOk() throws Exception {
            RegisterRequest request = new RegisterRequest("Test User", "new@test.com", "password123", "1234567890");
            when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Registered. Check your email to verify your account."));

            verify(userRepository).save(any(User.class));
            verify(verificationService).issueTokenAndEmail(any(User.class));
        }

        @Test
        @DisplayName("❌ Should return 409 Conflict if email is already registered and active")
        void whenRegister_withActiveEmail_shouldReturnConflict() throws Exception {
            RegisterRequest request = new RegisterRequest("Test User", "active@test.com", "password123", "1234567890");
            User existingUser = User.builder().status(AccountStatus.ACTIVE).build();
            when(userRepository.findByEmailIgnoreCase(request.getEmail())).thenReturn(Optional.of(existingUser));

            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /auth/verify")
    class VerifyTests {
        @Test
        @DisplayName("✅ Should verify account with a valid token")
        void whenVerify_withValidToken_shouldReturnOk() throws Exception {
            doNothing().when(verificationService).verifyByPlainToken("valid-token");

            mockMvc.perform(get("/auth/verify").param("token", "valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Account verified. You can log in now."));
        }
    }
    
    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {
        @Test
        @DisplayName("✅ Should login successfully with valid credentials for an active user")
        void whenLogin_withValidCredentialsAndActiveUser_shouldReturnToken() throws Exception {
            LoginRequest request = new LoginRequest("user@test.com", "password123");
            User activeUser = User.builder()
                .id(1L)
                .email("user@test.com")
                .password("hashedPassword")
                .role(Role.USER)
                .status(AccountStatus.ACTIVE)
                .build();
            
            when(userRepository.findByEmailIgnoreCase(request.getEmail())).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches(request.getPassword(), activeUser.getPassword())).thenReturn(true);
            when(jwtUtil.generateToken(anyLong(), anyString(), anyString())).thenReturn("test-jwt-token");

            mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        @DisplayName("❌ Should return 401 Unauthorized for a pending user")
        void whenLogin_withPendingUser_shouldReturnUnauthorized() throws Exception {
            LoginRequest request = new LoginRequest("pending@test.com", "password123");
            User pendingUser = User.builder().status(AccountStatus.PENDING).build();
            
            when(userRepository.findByEmailIgnoreCase(request.getEmail())).thenReturn(Optional.of(pendingUser));
            
            mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /auth/profile")
    class ProfileTests {
        @Test
        @WithMockUser(username = "profile@test.com")
        @DisplayName("✅ Should return profile for authenticated user")
        void whenGetProfile_asAuthenticatedUser_shouldReturnProfile() throws Exception {
            User profileUser = User.builder()
                .name("Profile User")
                .email("profile@test.com")
                .phone("9876543210")
                .role(Role.USER)
                .build();
            
            // Mock the static SecurityUtil.currentEmail() method
            try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                mockedSecurityUtil.when(SecurityUtil::currentEmail).thenReturn("profile@test.com");
                when(userRepository.findByEmailIgnoreCase("profile@test.com")).thenReturn(Optional.of(profileUser));

                mockMvc.perform(get("/auth/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Profile User"))
                    .andExpect(jsonPath("$.email").value("profile@test.com"));
            }
        }
    }
}