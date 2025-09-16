package com.olms.backend.controller;

import com.olms.backend.service.DueNotificationJob;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminNotificationController.class)
@DisplayName("AdminNotificationController Tests")
class AdminNotificationControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform simulated HTTP requests

    @MockBean
    private DueNotificationJob dueNotificationJob; // A mock of the service dependency

    @Test
    @WithMockUser(roles = "ADMIN") // Simulate the request is made by a user with the ADMIN role
    @DisplayName("✅ Should trigger notification job successfully for ADMIN user")
    void runNow_asAdmin_shouldTriggerJobAndReturnOk() throws Exception {
        // Arrange: Since runDaily() returns void, we can use doNothing() for clarity.
        // Mockito does this by default for void methods, but being explicit is good practice.
        doNothing().when(dueNotificationJob).runDaily();

        // Act & Assert
        mockMvc.perform(post("/api/admin/notifications/run")
                        .with(csrf())) // Include a CSRF token for POST requests in Spring Security
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ok")))
                .andExpect(jsonPath("$.ranAt", notNullValue()));

        // Verify that the runDaily() method on our mock job was called exactly once.
        verify(dueNotificationJob, times(1)).runDaily();
    }

    @Test
    @WithMockUser(roles = "USER") // Simulate the request is made by a user with a non-admin role
    @DisplayName("❌ Should return 403 Forbidden for non-ADMIN user")
    void runNow_asNonAdminUser_shouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/admin/notifications/run")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        // Verify that the job was never triggered
        verify(dueNotificationJob, never()).runDaily();
    }

    @Test
    @DisplayName("❌ Should return 401 Unauthorized for anonymous user")
    void runNow_asAnonymousUser_shouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/admin/notifications/run")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        // Verify that the job was never triggered
        verify(dueNotificationJob, never()).runDaily();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("🚨 Should handle exceptions from the job service gracefully")
    void runNow_whenJobThrowsException_shouldReturnInternalServerError() throws Exception {
        // Arrange: Configure the mock to throw an exception when runDaily() is called.
        doThrow(new RuntimeException("Database connection failed"))
                .when(dueNotificationJob)
                .runDaily();

        // Act & Assert
        mockMvc.perform(post("/api/admin/notifications/run")
                        .with(csrf()))
                .andExpect(status().isInternalServerError());

        // Verify that the controller still attempted to run the job
        verify(dueNotification-job, times(1)).runDaily();
    }
}