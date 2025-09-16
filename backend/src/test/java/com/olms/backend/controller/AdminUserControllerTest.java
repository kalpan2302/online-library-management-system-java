package com.olms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olms.backend.dto.AddUserRequest;
import com.olms.backend.entities.Role;
import com.olms.backend.entities.User;
import com.olms.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class)
@DisplayName("AdminUserController Tests")
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("POST /api/admin/users")
    class AddUserTests {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ Should add a new user successfully")
        void whenAddUser_withValidData_shouldReturn201Created() throws Exception {
            // Arrange
            AddUserRequest request = new AddUserRequest("New User", "new@test.com", "password123", "1234567890", "USER");
            User savedUser = new User();
            savedUser.setId(1L);
            savedUser.setName(request.getName());
            savedUser.setEmail(request.getEmail());

            when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            // Act & Assert
            mockMvc.perform(post("/api/admin/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/admin/users/1"))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.email", is(request.getEmail())));

            verify(userRepository).save(any(User.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("❌ Should return 400 Bad Request if email already exists")
        void whenAddUser_withExistingEmail_shouldReturnBadRequest() throws Exception {
            // Arrange
            AddUserRequest request = new AddUserRequest("New User", "existing@test.com", "password123", "1234567890", "USER");
            when(userRepository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(true);

            // Act & Assert
            mockMvc.perform(post("/api/admin/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Email already exists"));

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ Should return 403 Forbidden for non-ADMIN user")
        void whenAddUser_byNonAdmin_shouldReturnForbidden() throws Exception {
            AddUserRequest request = new AddUserRequest("New User", "new@test.com", "password123", "1234567890", "USER");
            mockMvc.perform(post("/api/admin/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/users")
    class GetAllUsersTests {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ Should return a list of all users")
        void whenGetAllUsers_asAdmin_shouldReturnUserList() throws Exception {
            // Arrange
            User user1 = new User();
            user1.setId(1L);
            user1.setName("User One");
            user1.setEmail("one@test.com");
            user1.setRole(Role.USER);
            when(userRepository.findAll()).thenReturn(List.of(user1));

            // Act & Assert
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].name", is("User One")));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ Should return 403 Forbidden for non-ADMIN user")
        void whenGetAllUsers_byNonAdmin_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isForbidden());
        }
    }
}