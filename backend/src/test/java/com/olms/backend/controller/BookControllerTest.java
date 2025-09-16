package com.olms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olms.backend.dto.BookRequest;
import com.olms.backend.dto.IssueRequest;
import com.olms.backend.entities.Book;
import com.olms.backend.entities.Loan;
import com.olms.backend.entities.User;
import com.olms.backend.repository.LoanRepository;
import com.olms.backend.repository.UserRepository;
import com.olms.backend.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@WithMockUser(roles = "ADMIN") // Apply ADMIN role to all tests in this class
@DisplayName("BookController (Admin) Tests")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private LoanRepository loanRepository;

    @Test
    @DisplayName("✅ [POST /add] Should add a new book")
    void addBook_shouldSucceed() throws Exception {
        BookRequest request = new BookRequest("New Title", "1234567890", 1, 1, 10);
        Book savedBook =