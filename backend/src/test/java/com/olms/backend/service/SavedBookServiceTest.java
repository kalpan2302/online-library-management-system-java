package com.olms.backend.service;

import com.olms.backend.dto.SavedBookResponse;
import com.olms.backend.entities.Book;
import com.olms.backend.entities.SavedBook;
import com.olms.backend.entities.User;
import com.olms.backend.repository.BookRepository;
import com.olms.backend.repository.SavedBookRepository;
import com.olms.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SavedBookService Tests")
class SavedBookServiceTest {

    @Mock
    private SavedBookRepository savedBookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private SavedBookService savedBookService;

    @Test
    @DisplayName("✅ Should save a book for a user")
    void saveBookForUser_shouldSucceed() {
        // Arrange
        User user = new User();
        user.setEmail("test@user.com");
        Book book = new Book();
        book.setBookId(1);
        
        when(userRepository.findByEmail("test@user.com")).thenReturn(Optional.of(user));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(savedBookRepository.save(any(SavedBook.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        SavedBook result = savedBookService.saveBookForUser("test@user.com", 1);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(book, result.getBook());
        verify(savedBookRepository).save(any(SavedBook.class));
    }

    @Test
    @DisplayName("✅ Should retrieve and map saved books for a user")
    void getSavedBooksForUser_shouldReturnMappedDtoList() {
        // Arrange
        User user = new User();
        user.setEmail("test@user.com");
        
        Book book = new Book();
        book.setBookId(1);
        book.setTitle("Test Book");
        book.setAuthors(Set.of()); // Initialize to avoid NPE

        SavedBook savedBook = new SavedBook();
        savedBook.setUser(user);
        savedBook.setBook(book);
        
        when(userRepository.findByEmail("test@user.com")).thenReturn(Optional.of(user));
        when(savedBookRepository.findAllByUser(user)).thenReturn(List.of(savedBook));

        // Act
        List<SavedBookResponse> result = savedBookService.getSavedBooksForUser("test@user.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        assertTrue(result.get(0).isSaved());
    }

    @Test
    @DisplayName("❌ Should throw RuntimeException if user is not found")
    void whenUserNotFound_shouldThrowException() {
        when(userRepository.findByEmail("notfound@user.com")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> savedBookService.saveBookForUser("notfound@user.com", 1));
        assertThrows(RuntimeException.class, () -> savedBookService.getSavedBooksForUser("notfound@user.com"));
    }
}