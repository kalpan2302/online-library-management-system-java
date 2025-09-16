package com.olms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olms.backend.dto.AuthorRequest;
import com.olms.backend.entities.Author;
import com.olms.backend.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@DisplayName("AuthorController Tests")
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Nested
    @DisplayName("Admin Author Management")
    @WithMockUser(roles = "ADMIN")
    class AdminAuthorTests {

        @Test
        @DisplayName("✅ Should add a new author")
        void addAuthor() throws Exception {
            AuthorRequest request = new AuthorRequest("New Author");
            Author createdAuthor = new Author(1, "New Author", null);
            when(bookService.addAuthor(any(AuthorRequest.class))).thenReturn(createdAuthor);

            mockMvc.perform(post("/api/admin/authors/add")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("New Author"));
        }

        @Test
        @DisplayName("✅ Should delete an author")
        void deleteAuthor() throws Exception {
            mockMvc.perform(delete("/api/admin/authors/delete/1")
                            .with(csrf()))
                    .andExpect(status().isNoContent());
            verify(bookService).deleteAuthor(1);
        }

        @Test
        @DisplayName("✅ Should list all authors")
        void listAuthors() throws Exception {
            Author author = new Author(1, "Test Author", null);
            when(bookService.listAuthors()).thenReturn(List.of(author));
            mockMvc.perform(get("/api/admin/authors/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Test Author"));
        }
    }

    @Test
    @DisplayName("❌ Should return 403 Forbidden for non-ADMIN user")
    @WithMockUser(roles = "USER")
    void nonAdminAccess_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/authors/all")).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/admin/authors/add").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/admin/authors/delete/1").with(csrf())).andExpect(status().isForbidden());
    }
}