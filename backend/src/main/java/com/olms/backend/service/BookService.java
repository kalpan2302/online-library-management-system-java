package com.olms.backend.service;

import com.olms.backend.dto.BookRequest;
import com.olms.backend.dto.AuthorRequest;
import com.olms.backend.dto.PublicationRequest;
import com.olms.backend.dto.UpdateAuthorRequest;
import com.olms.backend.dto.UpdateBookRequest;
import com.olms.backend.dto.UpdateCategoryRequest;
import com.olms.backend.dto.UpdatePublicationRequest;
import com.olms.backend.dto.CategoryRequest;
import com.olms.backend.entities.*;
import com.olms.backend.exception.DuplicateResourceException;
import com.olms.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepo;
    @Autowired
    private AuthorRepository authorRepo;
    @Autowired
    private PublicationRepository pubRepo;
    @Autowired
    private CategoryRepository catRepo;

    // ---------- Book (existing) ----------
    public Book addBook(BookRequest req) {
        if (req == null)
            throw new IllegalArgumentException("Request body is missing");
        if (req.getIsbn() == null || req.getIsbn().trim().isEmpty())
            throw new IllegalArgumentException("ISBN required");

        String isbn = req.getIsbn().trim();
        if (bookRepo.findByIsbn(isbn).isPresent()) {
            throw new DuplicateResourceException("Book with ISBN already exists");
        }

        Publication publication = getOrCreatePublication(req.getPublicationName());
        Category category = getOrCreateCategory(req.getCategoryName());
        Set<Author> authors = req.getAuthorNames().stream().map(this::getOrCreateAuthor).collect(Collectors.toSet());

        Book book = new Book();
        book.setTitle(req.getTitle().trim().toUpperCase());
        book.setIsbn(isbn);
        book.setTotalCopies(req.getTotalCopies() == null ? 1 : req.getTotalCopies());
        book.setAvailableCopies(req.getAvailableCopies() == null ? book.getTotalCopies() : req.getAvailableCopies());
        book.setShelfLocation(req.getShelfLocation() == null ? null : req.getShelfLocation().trim().toUpperCase());
        book.setPublication(publication);
        book.setCategory(category);
        book.setAuthors(authors);

        return bookRepo.save(book);
    }

    @Transactional
    public Book updateBook(Integer bookId, UpdateBookRequest req) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (req.getTitle() != null)
            book.setTitle(req.getTitle().trim().toUpperCase());
        if (req.getIsbn() != null)
            book.setIsbn(req.getIsbn());
        if (req.getTotalCopies() != null)
            book.setTotalCopies(req.getTotalCopies());
        if (req.getAvailableCopies() != null)
            book.setAvailableCopies(req.getAvailableCopies());
        if (req.getShelfLocation() != null)
            book.setShelfLocation(req.getShelfLocation());
        return bookRepo.save(book);
    }

    @Transactional
    public void deleteBook(Integer bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setIsExist(false);
        bookRepo.save(book);
    }

    // ---------- Author / Publication / Category add + list (new) ----------

    // ---------------- Author ----------------

    public Author addAuthor(AuthorRequest req) {
        if (req == null || req.getName() == null || req.getName().trim().isEmpty())
            throw new IllegalArgumentException("Author name required");

        String name = req.getName().trim().toUpperCase();
        if (authorRepo.findByNameIgnoreCase(name).isPresent()) {
            throw new DuplicateResourceException("Author with this name already exists");
        }
        Author a = new Author();
        a.setName(name);
        a.setBio(req.getBio());
        return authorRepo.save(a);
    }

    @Transactional
    public Author updateAuthor(Integer authorId, UpdateAuthorRequest req) {
        Author author = authorRepo.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        if (req.getName() != null)
            author.setName(req.getName().trim().toUpperCase());
        if (req.getBio() != null)
            author.setBio(req.getBio());
        return authorRepo.save(author);
    }

    @Transactional
    public void deleteAuthor(Integer authorId) {
        Author author = authorRepo.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        author.setIsExist(false);
        authorRepo.save(author);

        List<Book> books = bookRepo.findByAuthors(author);
        books.forEach(b -> {
            b.setIsExist(false);
            bookRepo.save(b);
        });
    }

    // ---------------- Publication ----------------

    public Publication addPublication(PublicationRequest req) {
        if (req == null || req.getName() == null || req.getName().trim().isEmpty())
            throw new IllegalArgumentException("Publication name required");

        String name = req.getName().trim().toUpperCase();
        if (pubRepo.findByNameIgnoreCase(name).isPresent()) {
            throw new DuplicateResourceException("Publication with this name already exists");
        }
        Publication p = new Publication();
        p.setName(name);
        p.setAddress(req.getAddress());
        return pubRepo.save(p);
    }

    @Transactional
    public Publication updatePublication(Integer publicationId, UpdatePublicationRequest req) {
        Publication pub = pubRepo.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication not found"));
        if (req.getName() != null)
            pub.setName(req.getName().trim().toUpperCase());
        if (req.getAddress() != null)
            pub.setAddress(req.getAddress());
        return pubRepo.save(pub);
    }

    @Transactional
    public void deletePublication(Integer publicationId) {
        Publication pub = pubRepo.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication not found"));
        pub.setIsExist(false);
        pubRepo.save(pub);

        List<Book> books = bookRepo.findByPublication(pub);
        books.forEach(b -> {
            b.setIsExist(false);
            bookRepo.save(b);
        });
    }

    // ---------------- Category ----------------

    public Category addCategory(CategoryRequest req) {
        if (req == null || req.getName() == null || req.getName().trim().isEmpty())
            throw new IllegalArgumentException("Category name required");

        String name = req.getName().trim().toUpperCase();
        if (catRepo.findByNameIgnoreCase(name).isPresent()) {
            throw new DuplicateResourceException("Category with this name already exists");
        }
        Category c = new Category();
        c.setName(name);
        c.setFineRatePerDay(req.getFineRatePerDay() == null ? 0.0 : req.getFineRatePerDay());
        return catRepo.save(c);
    }

    @Transactional
    public Category updateCategory(Integer categoryId, UpdateCategoryRequest req) {
        Category cat = catRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (req.getName() != null)
            cat.setName(req.getName().trim().toUpperCase());
        if (req.getFineRatePerDay() != null)
            cat.setFineRatePerDay(req.getFineRatePerDay());
        return catRepo.save(cat);
    }

    @Transactional
    public void deleteCategory(Integer categoryId) {
        Category cat = catRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        cat.setIsExist(false);
        catRepo.save(cat);

        List<Book> books = bookRepo.findByCategory(cat);
        books.forEach(b -> {
            b.setIsExist(false);
            bookRepo.save(b);
        });
    }

    // ---------- list endpoints for dropdowns ----------
    public List<Author> listAuthors() {
        return authorRepo.findAll();
    }

    public List<Publication> listPublications() {
        return pubRepo.findAll();
    }

    public List<Category> listCategories() {
        return catRepo.findAll();
    }

    // all books even after delete
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    // only currently Existing books (is_exist = true)
    public List<Book> getAllActiveBooks() {
        return bookRepo.findAll().stream()
                .filter(Book::getIsExist)
                .collect(Collectors.toList());
    }

    // ---------- helpers: reuse/create (uppercase storage) ----------
    private Author getOrCreateAuthor(String raw) {
        if (raw == null || raw.trim().isEmpty())
            throw new IllegalArgumentException("Author name required");
        String name = raw.trim().toUpperCase();
        return authorRepo.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Author a = new Author();
                    a.setName(name);
                    return authorRepo.save(a);
                });
    }

    private Publication getOrCreatePublication(String raw) {
        if (raw == null || raw.trim().isEmpty())
            throw new IllegalArgumentException("Publication name required");
        String name = raw.trim().toUpperCase();
        return pubRepo.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Publication p = new Publication();
                    p.setName(name);
                    return pubRepo.save(p);
                });
    }

    private Category getOrCreateCategory(String raw) {
        if (raw == null || raw.trim().isEmpty())
            throw new IllegalArgumentException("Category name required");
        String name = raw.trim().toUpperCase();
        return catRepo.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(name);
                    c.setFineRatePerDay(0.0);
                    return catRepo.save(c);
                });
    }
}
