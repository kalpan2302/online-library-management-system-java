package com.olms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olms.backend.dto.FinePaymentRequest;
import com.olms.backend.entities.Fine;
import com.olms.backend.entities.User;
import com.olms.backend.repository.FineRepository;
import com.olms.backend.repository.UserRepository;
import com.olms.backend.security.SecurityUtil;
import com.olms.backend.service.FineService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FineController.class)
@DisplayName("FineController Tests")
class FineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FineService fineService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private FineRepository fineRepository;

    @Nested
    @DisplayName("GET /api/fines/my")
    class GetMyFinesTests {
        @Test
        @WithMockUser(username = "user@test.com")
        @DisplayName("✅ Should return fines for the authenticated user")
        void getMyFines_asAuthenticatedUser_shouldSucceed() throws Exception {
            User currentUser = new User();
            currentUser.setId(1L);
            currentUser.setEmail("user@test.com");
            Fine userFine = new Fine();

            try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                mockedSecurityUtil.when(SecurityUtil::currentEmail).thenReturn("user@test.com");
                when(userRepository.findByEmailIgnoreCase("user@test.com")).thenReturn(Optional.of(currentUser));
                when(fineService.getFinesForUser(1L)).thenReturn(List.of(userFine));

                mockMvc.perform(get("/api/fines/my"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$").isArray());
            }
        }
    }

    @Nested
    @DisplayName("POST /api/fines/pay")
    class PayFineTests {
        @Test
        @WithMockUser(username = "admin@test.com", roles = "ADMIN")
        @DisplayName("✅ Should allow ADMIN to pay a fine")
        void payFine_asAdmin_shouldSucceed() throws Exception {
            FinePaymentRequest request = new FinePaymentRequest(1L, new BigDecimal("10.0"), "CASH", null, "Paid by admin");
            
            try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                mockedSecurityUtil.when(SecurityUtil::isAdmin).thenReturn(true);
                // Assume SecurityUtil.currentEmail() is not needed in the admin pay path, but mock it just in case.
                mockedSecurityUtil.when(SecurityUtil::currentEmail).thenReturn("admin@test.com");
                when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(new User()));


                mockMvc.perform(post("/api/fines/pay")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk());

                verify(fineService).payFine(anyLong(), any(), any(), any(), any(), any());
            }
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ Should return 403 Forbidden for non-ADMIN user")
        void payFine_asUser_shouldBeForbidden() throws Exception {
            FinePaymentRequest request = new FinePaymentRequest(1L, new BigDecimal("10.0"), "CASH", null, "Paid by user");
            
            try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                mockedSecurityUtil.when(SecurityUtil::isAdmin).thenReturn(false);
                
                mockMvc.perform(post("/api/fines/pay")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isForbidden());

                verify(fineService, never()).payFine(anyLong(), any(), any(), any(), any(), any());
            }
        }
    }
}