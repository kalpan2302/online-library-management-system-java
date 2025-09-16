package com.olms.backend.controller;

import com.olms.backend.entities.Book;
import com.olms.backend.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicBookController.class)
@DisplayName("PublicBookController Tests")
class PublicBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("✅ Should return a list of active books to any user")
    void getAllActiveBooks() throws Exception {
        Book activeBook = new Book();
        activeBook.setId(1);
        activeBook.setTitle("Available Book");
        
        when(bookService.getAllActiveBooks()).thenReturn(List.of(activeBook));

        mockMvc.perform(get("/api/books/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Available Book"));
    }
}