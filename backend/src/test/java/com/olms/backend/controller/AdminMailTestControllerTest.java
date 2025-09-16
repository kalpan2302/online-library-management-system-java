package com.olms.backend.controller;

import com.olms.backend.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminMailTestController.class)
@DisplayName("AdminMailTestController Tests")
class AdminMailTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Creates a mock of EmailService and injects it into the application context
    private EmailService emailService;

    private final String TEST_EMAIL = "test@example.com";

    @Test
    @WithMockUser(roles = "ADMIN") // Simulate a request from a user with the ADMIN role
    @DisplayName("✅ Should send test email successfully for ADMIN user")
    void send_asAdminWithValidEmail_shouldSucceed() throws Exception {
        // Arrange
        // The sendPlain method returns void, so we don't need to stub a return value.
        // Mockito will do nothing by default for void methods.
        // We can be explicit with doNothing() if preferred.
        doNothing().when(emailService).sendPlain(anyString(), anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/admin/test-mail")
                        .param("to", TEST_EMAIL)
                        .with(csrf())) // Include CSRF token for POST request with Spring Security
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sent", is(true)));

        // Verify that the service method was called exactly once with the expected arguments
        verify(emailService, times(1))
                .sendPlain(TEST_EMAIL, "OLMS mail test", "If you see this, SMTP is OK.");
    }

    @Test
    @WithMockUser(roles = "USER") // Simulate a request from a user with a non-ADMIN role
    @DisplayName("❌ Should return 403 Forbidden for non-ADMIN user")
    void send_asNonAdmin_shouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/admin/test-mail")
                        .param("to", TEST_EMAIL)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        // Verify that the email service was never contacted
        verify(emailService, never()).sendPlain(anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("❌ Should return 400 Bad Request if 'to' parameter is missing")
    void send_asAdminWithMissingParam_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/admin/test-mail") // No .param("to", ...)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        // Verify that the email service was never contacted
        verify(emailService, never()).sendPlain(anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("🚨 Should handle exceptions from EmailService gracefully")
    void send_whenEmailServiceFails_shouldReturnInternalServerError() throws Exception {
        // Arrange: Configure the mock to throw an exception when called
        doThrow(new MailSendException("Failed to connect to SMTP server"))
                .when(emailService)
                .sendPlain(anyString(), anyString(), anyString());

        // Act & Assert
        // We expect a 500 Internal Server Error because the unhandled service layer exception
        // will be caught by Spring's default exception handler.
        mockMvc.perform(post("/api/admin/test-mail")
                        .param("to", TEST_EMAIL)
                        .with(csrf()))
                .andExpect(status().isInternalServerError());

        // Verify that the service method was still attempted
        verify(emailService, times(1))
                .sendPlain(TEST_EMAIL, "OLMS mail test", "If you see this, SMTP is OK.");
    }
}