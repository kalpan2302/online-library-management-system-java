package com.olms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olms.backend.dto.FineAdjustRequest;
import com.olms.backend.entities.Fine;
import com.olms.backend.service.FineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminFineController.class)
@DisplayName("AdminFineController Tests")
class AdminFineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FineService fineService;

    @Autowired
    private ObjectMapper objectMapper;

    private Fine fine1;
    private Fine fine2;

    @BeforeEach
    void setUp() {
        // Sample data for tests
        fine1 = new Fine();
        fine1.setId(1L);
        fine1.setAmount(new BigDecimal("10.50"));
        fine1.setReason("Late book return");

        fine2 = new Fine();
        fine2.setId(2L);
        fine2.setAmount(new BigDecimal("25.00"));
        fine2.setReason("Damaged book cover");
    }

    @Nested
    @DisplayName("GET /api/admin/fines/all")
    class GetAllFinesTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ Should return all fines for ADMIN user")
        void whenCalledByAdmin_shouldReturnListOfFines() throws Exception {
            // Arrange
            when(fineService.getAllFines()).thenReturn(List.of(fine1, fine2));

            // Act & Assert
            mockMvc.perform(get("/api/admin/fines/all"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].reason", is("Late book return")))
                    .andExpect(jsonPath("$[1].id", is(2)));

            verify(fineService, times(1)).getAllFines();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ Should return empty list when no fines exist")
        void whenNoFinesExist_shouldReturnEmptyList() throws Exception {
            // Arrange
            when(fineService.getAllFines()).thenReturn(Collections.emptyList());

            // Act & Assert
            mockMvc.perform(get("/api/admin/fines/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(fineService, times(1)).getAllFines();
        }

        @Test
        @WithMockUser(roles = "USER") // Using a non-admin role
        @DisplayName("❌ Should return 403 Forbidden for non-ADMIN user")
        void whenCalledByUser_shouldReturnForbidden() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/api/admin/fines/all"))
                    .andExpect(status().isForbidden());

            verify(fineService, never()).getAllFines();
        }
    }

    @Nested
    @DisplayName("POST /api/admin/fines/{fineId}/adjust")
    class AdjustFineTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ Should adjust fine successfully for ADMIN user")
        void whenAdjustFine_withValidData_shouldReturnAdjustedFine() throws Exception {
            // Arrange
            long fineId = 1L;
            BigDecimal newAmount = new BigDecimal("5.00");
            String note = "Reduced for good record";
            FineAdjustRequest request = new FineAdjustRequest(newAmount, note);

            Fine adjustedFine = new Fine();
            adjustedFine.setId(fineId);
            adjustedFine.setAmount(newAmount);
            adjustedFine.setReason(note);

            when(fineService.adjustFine(fineId, newAmount, note)).thenReturn(adjustedFine);

            // Act & Assert
            mockMvc.perform(post("/api/admin/fines/{fineId}/adjust", fineId)
                            .with(csrf()) // Include CSRF token for POST requests
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is((int) fineId)))
                    .andExpect(jsonPath("$.amount", is(5.00)))
                    .andExpect(jsonPath("$.reason", is(note)));

            verify(fineService, times(1)).adjustFine(fineId, newAmount, note);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("❌ Should return 400 Bad Request if note is blank")
        void whenAdjustFine_withBlankNote_shouldReturnBadRequest() throws Exception {
            // Arrange
            FineAdjustRequest request = new FineAdjustRequest(new BigDecimal("5.00"), " "); // Invalid note

            // Act & Assert
            mockMvc.perform(post("/api/admin/fines/1/adjust")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(fineService, never()).adjustFine(anyLong(), any(), anyString());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ Should return 403 Forbidden for non-ADMIN user")
        void whenCalledByUser_shouldReturnForbidden() throws Exception {
            // Arrange
            FineAdjustRequest request = new FineAdjustRequest(new BigDecimal("5.00"), "A note");

            // Act & Assert
            mockMvc.perform(post("/api/admin/fines/1/adjust")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(fineService, never()).adjustFine(anyLong(), any(), anyString());
        }
    }

    @Nested
    @DisplayName("POST /api/admin/fines/{fineId}/waive")
    class WaiveFineTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("✅ Should waive fine successfully for ADMIN user")
        void whenWaiveFine_withValidNote_shouldReturnWaivedFine() throws Exception {
            // Arrange
            long fineId = 1L;
            String note = "Waived as a one-time courtesy.";
            FineAdjustRequest request = new FineAdjustRequest(null, note); // Amount is not needed

            Fine waivedFine = new Fine();
            waivedFine.setId(fineId);
            waivedFine.setAmount(BigDecimal.ZERO); // A waived fine should have a zero amount
            waivedFine.setReason(note);

            when(fineService.waiveFine(fineId, note)).thenReturn(waivedFine);

            // Act & Assert
            mockMvc.perform(post("/api/admin/fines/{fineId}/waive", fineId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is((int) fineId)))
                    .andExpect(jsonPath("$.amount", is(0.0)))
                    .andExpect(jsonPath("$.reason", is(note)));

            verify(fineService, times(1)).waiveFine(fineId, note);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("❌ Should return 400 Bad Request if note is blank")
        void whenWaiveFine_withBlankNote_shouldReturnBadRequest() throws Exception {
            // Arrange
            FineAdjustRequest request = new FineAdjustRequest(null, ""); // Invalid blank note

            // Act & Assert
            mockMvc.perform(post("/api/admin/fines/1/waive")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(fineService, never()).waiveFine(anyLong(), anyString());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("❌ Should return 403 Forbidden for non-ADMIN user")
        void whenCalledByUser_shouldReturnForbidden() throws Exception {
            // Arrange
            FineAdjustRequest request = new FineAdjustRequest(null, "A note");

            // Act & Assert
            mockMvc.perform(post("/api/admin/fines/1/waive")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(fineService, never()).waiveFine(anyLong(), anyString());
        }
    }
}