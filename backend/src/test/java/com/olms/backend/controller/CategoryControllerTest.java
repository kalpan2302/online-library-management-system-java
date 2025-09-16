package com.olms.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olms.backend.dto.CategoryRequest;
import com.olms.backend.entities.Category;
import com.olms.backend.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CategoryController.class)
@WithMockUser(roles = "ADMIN") // Apply ADMIN role to all tests
@DisplayName("CategoryController Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("✅ [POST /add] Should add a new category")
    void addCategory() throws Exception {
        CategoryRequest request = new CategoryRequest("Fiction");
        Category createdCategory = new Category(1, "Fiction", null);
        when(bookService.addCategory(any(CategoryRequest.class))).thenReturn(createdCategory);

        mockMvc.perform(post("/api/admin/categories/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Fiction"));
    }

    @Test
    @DisplayName("✅ [GET /all] Should list all categories")
    void listCategories() throws Exception {
        when(bookService.listCategories()).thenReturn(List.of(new Category(1, "Fiction", null)));

        mockMvc.perform(get("/api/admin/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fiction"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("❌ Should return 403 Forbidden for non-ADMIN user")
    void nonAdminAccess_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/categories/all")).andExpect(status().isForbidden());
    }
}