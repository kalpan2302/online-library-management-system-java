package com.olms.backend.service;

import com.olms.backend.dto.SavedBookResponse;
import com.olms.backend.entities.SavedBook;
import com.olms.backend.entities.Book;
import com.olms.backend.entities.User;
import com.olms.backend.repository.BookRepository;
import com.olms.backend.repository.SavedBookRepository;
import com.olms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedBookService {

    @Autowired
    private SavedBookRepository savedBookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public SavedBook saveBookForUser(String email, Integer bookId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        SavedBook savedBook = new SavedBook();
        savedBook.setUser(user);
        savedBook.setBook(book);
        return savedBookRepository.save(savedBook);
    }

    public List<SavedBookResponse> getSavedBooksForUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<SavedBook> savedBooks = savedBookRepository.findAllByUser(user);

        return savedBooks.stream().map(sb -> {
            Book book = sb.getBook();
            SavedBookResponse dto = new SavedBookResponse();
            dto.setBookId(book.getBookId());
            dto.setTitle(book.getTitle());
            dto.setIsbn(book.getIsbn());
            dto.setShelfLocation(book.getShelfLocation());
            dto.setCategoryName(book.getCategory() != null ? book.getCategory().getName() : null);
            dto.setPublicationName(book.getPublication() != null ? book.getPublication().getName() : null);
            dto.setAuthorNames(book.getAuthors().stream().map(a -> a.getName()).collect(Collectors.toList()));
            dto.setTotalCopies(book.getTotalCopies()); // added total copies
            dto.setAvailableCopies(book.getAvailableCopies()); // added available copies
            dto.setSaved(true);
            dto.setIsExist(book.getIsExist());
            return dto;
        }).collect(Collectors.toList());
    }
}