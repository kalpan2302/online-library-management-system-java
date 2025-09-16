package com.olms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olms.backend.dto.IssueRequest;
import com.olms.backend.entities.Loan;
import com.olms.backend.security.SecurityUtil;
import com.olms.backend.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserBookController.class)
@WithMockUser(username = "user@test.com")
@DisplayName("UserBookController Tests")
class UserBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("✅ [POST /issue] Should issue a book for the current user")
    void issueBook() throws Exception {
        IssueRequest request = new IssueRequest(null, 1, "2025-12-31");
        
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::currentEmail).thenReturn("user@test.com");
            when(bookService.issueBook(isNull(), anyInt(), any(), eq("user@test.com"), eq(false))).thenReturn(new Loan());

            mockMvc.perform(post("/api/books/issue")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
            
            verify(bookService).issueBook(isNull(), eq(1), any(), eq("user@test.com"), eq(false));
        }
    }
}