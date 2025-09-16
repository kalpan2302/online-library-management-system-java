package com.olms.backend.service;

import com.olms.backend.dto.AuthorRequest;
import com.olms.backend.dto.BookRequest;
import com.olms.backend.entities.*;
import com.olms.backend.exception.DuplicateResourceException;
import com.olms.backend.exception.ResourceNotFoundException;
import com.olms.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepo;
    @Mock
    private AuthorRepository authorRepo;
    @Mock
    private PublicationRepository pubRepo;
    @Mock
    private CategoryRepository catRepo;
    @Mock
    private LoanRepository loanRepo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private FineRepository fineRepository;
    @Mock
    private FineService fineService;

    @InjectMocks
    private BookService bookService;
    
    private User testUser;
    private Book testBook;
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        // Set configured values for testing
        ReflectionTestUtils.setField(bookService, "maxActiveLoans", 3);
        ReflectionTestUtils.setField(bookService, "defaultLoanDays", 14);
        ReflectionTestUtils.setField(bookService, "blockOnUnpaid", true);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@user.com");

        testBook = new Book();
        testBook.setBookId(1);
        testBook.setAvailableCopies(1);
        testBook.setIsExist(true);

        testLoan = new Loan();
        testLoan.setUser(testUser);
        testLoan.setBook(testBook);
        testLoan.setStatus(LoanStatus.ISSUED);
        testLoan.setIssueDate(LocalDate.now().minusDays(5));
        testLoan.setDueDate(LocalDate.now().plusDays(9));
    }

    @Nested
    @DisplayName("addBook Method")
    class AddBookTests {
        @Test
        @DisplayName("✅ Should add a new book successfully")
        void whenAddBook_withValidRequest_shouldSaveAndReturnBook() {
            BookRequest request = new BookRequest("New Book", "12345", 1, 1, 10, List.of("Author"), "Pub", "Cat", "A1");
            when(bookRepo.findByIsbn(anyString())).thenReturn(Optional.empty());
            when(authorRepo.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
            when(pubRepo.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
            when(catRepo.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
            when(bookRepo.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

            Book result = bookService.addBook(request);

            assertNotNull(result);
            assertEquals("NEW BOOK", result.getTitle());
            verify(bookRepo).save(any(Book.class));
            verify(authorRepo, times(1)).save(any(Author.class)); // Verifies getOrCreate logic
        }

        @Test
        @DisplayName("❌ Should throw DuplicateResourceException if ISBN exists")
        void whenAddBook_withExistingIsbn_shouldThrowException() {
            BookRequest request = new BookRequest("New Book", "12345", 1, 1, 10, List.of("Author"), "Pub", "Cat", "A1");
            when(bookRepo.findByIsbn("12345")).thenReturn(Optional.of(new Book()));
            
            assertThrows(DuplicateResourceException.class, () -> bookService.addBook(request));
        }
    }

    @Nested
    @DisplayName("issueBook Method")
    class IssueBookTests {
        @Test
        @DisplayName("✅ Should issue book successfully for a valid user")
        void whenIssueBook_withValidConditions_shouldSucceed() {
            when(userRepo.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testUser));
            when(fineRepository.countByUserAndStatus(testUser, FineStatus.UNPAID)).thenReturn(0L);
            when(loanRepo.countByUserAndStatus(testUser, LoanStatus.ISSUED)).thenReturn(0L);
            when(bookRepo.findByIdForUpdate(1)).thenReturn(Optional.of(testBook));
            when(loanRepo.existsByBookAndUserAndStatus(testBook, testUser, LoanStatus.ISSUED)).thenReturn(false);
            when(loanRepo.save(any(Loan.class))).thenAnswer(i -> i.getArgument(0));

            Loan result = bookService.issueBook(null, 1, null, "test@user.com", false);

            assertNotNull(result);
            assertEquals(LoanStatus.ISSUED, result.getStatus());
            assertEquals(0, testBook.getAvailableCopies()); // verify copy decrement
            verify(bookRepo).save(testBook);
            verify(loanRepo).save(any(Loan.class));
        }

        @Test
        @DisplayName("❌ Should throw exception if user has unpaid fines")
        void whenIssueBook_withUnpaidFines_shouldThrowException() {
            when(userRepo.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testUser));
            when(fineRepository.countByUserAndStatus(testUser, FineStatus.UNPAID)).thenReturn(1L);
            
            Exception e = assertThrows(IllegalArgumentException.class, () -> bookService.issueBook(null, 1, null, "test@user.com", false));
            assertEquals("User has unpaid fines", e.getMessage());
        }

        @Test
        @DisplayName("❌ Should throw exception if book has no available copies")
        void whenIssueBook_withNoAvailableCopies_shouldThrowException() {
            testBook.setAvailableCopies(0);
            when(userRepo.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testUser));
            when(fineRepository.countByUserAndStatus(testUser, FineStatus.UNPAID)).thenReturn(0L);
            when(loanRepo.countByUserAndStatus(testUser, LoanStatus.ISSUED)).thenReturn(0L);
            when(bookRepo.findByIdForUpdate(1)).thenReturn(Optional.of(testBook));

            Exception e = assertThrows(IllegalArgumentException.class, () -> bookService.issueBook(null, 1, null, "test@user.com", false));
            assertEquals("No available copies to issue", e.getMessage());
        }
    }
    
    @Nested
    @DisplayName("returnBook Method")
    class ReturnBookTests {
        @Test
        @DisplayName("✅ Should return a book successfully with no fine")
        void whenReturnBook_onTime_shouldSucceed() {
            when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
            when(bookRepo.findById(1)).thenReturn(Optional.of(testBook));
            when(loanRepo.findByBookAndUserAndStatus(testBook, testUser, LoanStatus.ISSUED)).thenReturn(Optional.of(testLoan));
            when(bookRepo.findByIdForUpdate(1)).thenReturn(Optional.of(testBook));

            int initialCopies = testBook.getAvailableCopies();
            bookService.returnBook(1L, 1, LocalDate.now());
            
            assertEquals(LoanStatus.RETURNED, testLoan.getStatus());
            assertEquals(0.0, testLoan.getFineAmount());
            assertEquals(initialCopies + 1, testBook.getAvailableCopies());
            verify(loanRepo).save(testLoan);
            verify(bookRepo).save(testBook);
            verify(fineService).createOrUpdateFineForLoan(testLoan);
        }

        @Test
        @DisplayName("✅ Should calculate fine correctly for an overdue book")
        void whenReturnBook_overdue_shouldCalculateFine() {
            Category category = new Category();
            category.setFineRatePerDay(2.0); // 2.0 per day
            testBook.setCategory(category);
            testLoan.setDueDate(LocalDate.now().minusDays(5)); // 5 days late
            
            when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
            when(bookRepo.findById(1)).thenReturn(Optional.of(testBook));
            when(loanRepo.findByBookAndUserAndStatus(testBook, testUser, LoanStatus.ISSUED)).thenReturn(Optional.of(testLoan));
            when(bookRepo.findByIdForUpdate(1)).thenReturn(Optional.of(testBook));
            
            bookService.returnBook(1L, 1, LocalDate.now());

            assertEquals(10.0, testLoan.getFineAmount()); // 5 days * 2.0/day
            verify(fineService).createOrUpdateFineForLoan(testLoan);
        }
    }
}