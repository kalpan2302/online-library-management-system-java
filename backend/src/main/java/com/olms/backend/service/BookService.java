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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.olms.backend.exception.ResourceNotFoundException;
import com.olms.backend.service.FineService;

import java.time.LocalDate;
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

  // @Transactional
  // public Book updateBook(Integer bookId, UpdateBookRequest req) {
  // Book book = bookRepo.findById(bookId)
  // .orElseThrow(() -> new RuntimeException("Book not found"));
  //
  // // TEMP: widen race window
  // try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
  //
  // if (req.getTitle() != null)
  // book.setTitle(req.getTitle().trim().toUpperCase());
  // if (req.getIsbn() != null)
  // book.setIsbn(req.getIsbn());
  // if (req.getTotalCopies() != null)
  // book.setTotalCopies(req.getTotalCopies());
  // if (req.getAvailableCopies() != null)
  // book.setAvailableCopies(req.getAvailableCopies());
  // if (req.getShelfLocation() != null)
  // book.setShelfLocation(req.getShelfLocation());
  // return bookRepo.save(book);
  // }

  @Transactional
  public Book updateBook(Integer bookId, UpdateBookRequest req) {
    Book book = bookRepo.findById(bookId)
        .orElseThrow(() -> new RuntimeException("Book not found"));

    // compute prospective values
    int newTotal = req.getTotalCopies() != null ? req.getTotalCopies() : nvl(book.getTotalCopies());
    int newAvail = req.getAvailableCopies() != null ? req.getAvailableCopies() : nvl(book.getAvailableCopies());

    if (newTotal < 0 || newAvail < 0)
      throw new IllegalArgumentException("Copies cannot be negative");
    if (newAvail > newTotal)
      throw new IllegalArgumentException("availableCopies cannot exceed totalCopies");

    if (req.getTitle() != null)
      book.setTitle(req.getTitle().trim().toUpperCase());
    if (req.getIsbn() != null)
      book.setIsbn(req.getIsbn().trim());
    if (req.getShelfLocation() != null)
      book.setShelfLocation(req.getShelfLocation().trim());
    book.setTotalCopies(newTotal);
    book.setAvailableCopies(newAvail);

    if (req.getAuthorsNames() != null && !req.getAuthorsNames().isEmpty()) {
      List<Author> authorsList = authorRepo.findAllByNameInIgnoreCase(req.getAuthorsNames());
      book.setAuthors(new HashSet<>(authorsList));
    }

    if (req.getCategoryName() != null) {
      Category category = catRepo.findByNameIgnoreCase(req.getCategoryName())
          .orElseThrow(() -> new RuntimeException("Category not found"));
      book.setCategory(category);
    }

    if (req.getPublicationName() != null) {
      Publication pub = pubRepo.findByNameIgnoreCase(req.getPublicationName())
          .orElseThrow(() -> new RuntimeException("Publication not found"));
      book.setPublication(pub);
    }

    return bookRepo.save(book);
  }

  private int nvl(Integer v) {
    return v == null ? 0 : v;
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

  // loan --> issue/return book ---------------------------------------------

  @Autowired
  private LoanRepository loanRepo;

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private FineService fineService;

  @Value("${olms.loans.max-active:3}")
  private int maxActiveLoans;

  @Value("${olms.loans.default-days:14}")
  private int defaultLoanDays;

  @Value("${olms.fines.block-on-unpaid:true}")
  private boolean blockOnUnpaid;

  @Autowired
  private FineRepository fineRepository;

  // @Transactional
  // public Loan issueBook(Long requestedUserId, Integer bookId,
  // java.time.LocalDate requestedDueDate, String callerEmail, boolean
  // callerIsAdmin) {
  // // determine user
  // com.olms.backend.entities.User user;
  // if (callerIsAdmin && requestedUserId != null) {
  // user = userRepo.findById(requestedUserId)
  // .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  // } else {
  // user = userRepo.findByEmail(callerEmail)
  // .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  // }
  //
  // Book book = bookRepo.findById(bookId)
  // .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
  //
  // if (!Boolean.TRUE.equals(book.getIsExist()))
  // throw new IllegalArgumentException("Book not available");
  //
  // if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0)
  // throw new IllegalArgumentException("No available copies to issue");
  //
  // // decrement available copies
  // book.setAvailableCopies(book.getAvailableCopies() - 1);
  // bookRepo.save(book);
  //
  // Loan loan = new Loan();
  // loan.setBook(book);
  // loan.setUser(user);
  // java.time.LocalDate issueDate = java.time.LocalDate.now();
  // loan.setIssueDate(issueDate);
  // java.time.LocalDate dueDate = (requestedDueDate == null) ?
  // issueDate.plusDays(DEFAULT_LOAN_DAYS) : requestedDueDate;
  // loan.setDueDate(dueDate);
  // loan.setStatus(LoanStatus.ISSUED);
  // loan.setFineAmount(0.0);
  //
  // return loanRepo.save(loan);
  // }

  // abhishek

  @Transactional
  public Loan issueBook(Long requestedUserId, Integer bookId,
      LocalDate requestedDueDate, String callerEmail, boolean callerIsAdmin) {

    // resolve borrower
    // User user = (callerIsAdmin && requestedUserId != null)
    // ? userRepo.findById(requestedUserId).orElseThrow(() -> new
    // ResourceNotFoundException("User not found"))
    // : userRepo.findByEmail(callerEmail).orElseThrow(() -> new
    // ResourceNotFoundException("User not found"));
    // User user = (callerIsAdmin && requestedUserId != null)
    // ? userRepo.findById(requestedUserId).orElseThrow(() -> new
    // ResourceNotFoundException("User not found"))
    // : userRepo.findByEmailIgnoreCase(callerEmail.trim())
    // .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    User user;
    if (callerIsAdmin && requestedUserId != null) {
      user = userRepo.findById(requestedUserId)
          .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    } else {
      if (callerEmail == null || callerEmail.isBlank())
        throw new IllegalArgumentException("Missing Authorization/Bearer token");
      user = userRepo.findByEmailIgnoreCase(callerEmail.trim())
          .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    // block if unpaid fines (configurable)
    if (blockOnUnpaid && fineRepository.countByUserAndStatus(user, FineStatus.UNPAID) > 0) {
      throw new IllegalArgumentException("User has unpaid fines");
    }

    // cap concurrent loans
    if (loanRepo.countByUserAndStatus(user, LoanStatus.ISSUED) >= maxActiveLoans) {
      throw new IllegalArgumentException("Max active loans reached (" + maxActiveLoans + ")");
    }

    // lock the book row to update copies atomically
    Book book = bookRepo.findByIdForUpdate(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    if (!Boolean.TRUE.equals(book.getIsExist()))
      throw new IllegalArgumentException("Book not available");
    if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0)
      throw new IllegalArgumentException("No available copies to issue");

    // no duplicate active loan for same user+book
    if (loanRepo.existsByBookAndUserAndStatus(book, user, LoanStatus.ISSUED)) {
      throw new IllegalArgumentException("User already has this book issued");
    }

    LocalDate issueDate = LocalDate.now();
    LocalDate dueDate = (requestedDueDate == null) ? issueDate.plusDays(defaultLoanDays) : requestedDueDate;
    if (dueDate.isBefore(issueDate))
      throw new IllegalArgumentException("Due date cannot be before issue date");
    long span = java.time.temporal.ChronoUnit.DAYS.between(issueDate, dueDate);
    if (span > defaultLoanDays)
      throw new IllegalArgumentException("Loan period exceeds " + defaultLoanDays + " days");

    // decrement under lock
    book.setAvailableCopies(book.getAvailableCopies() - 1);

    if (book.getTotalCopies() != null && book.getAvailableCopies() != null
        && book.getAvailableCopies() > book.getTotalCopies()) {
      throw new IllegalArgumentException("Available copies cannot exceed total copies");
    }

    bookRepo.save(book);

    Loan loan = new Loan();
    loan.setBook(book);
    loan.setUser(user);
    loan.setIssueDate(issueDate);
    loan.setDueDate(dueDate);
    loan.setStatus(LoanStatus.ISSUED);
    loan.setFineAmount(0.0);

    return loanRepo.save(loan);
  }

  // @Transactional
  // public Loan returnBook(Long loanId, Integer bookId, String callerEmail,
  // boolean callerIsAdmin) {
  // Loan loan = null;
  //
  // if (loanId != null) {
  // loan = loanRepo.findById(loanId)
  // .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
  // } else if (bookId != null) {
  // com.olms.backend.entities.User user = userRepo.findByEmail(callerEmail)
  // .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  // Book book = bookRepo.findById(bookId)
  // .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
  //
  // loan = loanRepo.findByBookAndUserAndStatus(book, user, LoanStatus.ISSUED)
  // .orElseThrow(() -> new ResourceNotFoundException("Active loan not found for
  // this user and book"));
  // } else {
  // throw new IllegalArgumentException("Provide loanId or bookId");
  // }
  //
  // if (loan.getStatus() == LoanStatus.RETURNED) {
  // throw new IllegalArgumentException("Book already returned");
  // }
  //
  // java.time.LocalDate returnDate = java.time.LocalDate.now();
  // loan.setReturnDate(returnDate);
  // loan.setStatus(LoanStatus.RETURNED);
  //
  // java.time.LocalDate due = loan.getDueDate();
  // double calculatedFine = 0.0;
  // if (due != null && returnDate.isAfter(due)) {
  // long daysLate = java.time.temporal.ChronoUnit.DAYS.between(due, returnDate);
  // Double rate = 0.0;
  // if (loan.getBook() != null && loan.getBook().getCategory() != null) {
  // rate = loan.getBook().getCategory().getFineRatePerDay();
  // }
  // rate = (rate == null) ? 0.0 : rate;
  // calculatedFine = daysLate * rate;
  // }
  // loan.setFineAmount(calculatedFine);
  //
  // Book book = loan.getBook();
  // book.setAvailableCopies((book.getAvailableCopies() == null ? 0 :
  // book.getAvailableCopies()) + 1);
  // bookRepo.save(book);
  //
  // // persist loan
  // Loan savedLoan = loanRepo.save(loan);
  //
  // // create or update a Fine record if any (fineService returns null if no fine
  // created)
  // Fine fineRecord = fineService.createOrUpdateFineForLoan(savedLoan);
  // // optional: log fine creation
  // // if (fineRecord != null) { logger.info("Created/updated fine id=" +
  // fineRecord.getFineId()); }
  //
  // return savedLoan;
  // }

  // Abhishek

  // @Transactional
  // public Loan returnBook(Long loanId, Integer bookId, String callerEmail,
  // boolean callerIsAdmin) {

  // Loan loan;
  // if (loanId != null) {
  // loan = loanRepo.findById(loanId).orElseThrow(() -> new
  // ResourceNotFoundException("Loan not found"));
  // } else if (bookId != null) {
  // // User user = userRepo.findByEmail(callerEmail)
  // // .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  // User user = userRepo.findByEmailIgnoreCase(callerEmail.trim())
  // .orElseThrow(() -> new ResourceNotFoundException("User not found"));

  // Book book = bookRepo.findById(bookId)
  // .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
  // loan = loanRepo.findByBookAndUserAndStatus(book, user, LoanStatus.ISSUED)
  // .orElseThrow(() -> new ResourceNotFoundException("Active loan not found for
  // this user and book"));
  // } else {
  // throw new IllegalArgumentException("Provide loanId or bookId");
  // }

  // // non-admins can only return their own loan
  // if (!callerIsAdmin) {
  // String owner =
  // Optional.ofNullable(loan.getUser()).map(User::getEmail).orElse(null);
  // if (owner == null || !owner.equalsIgnoreCase(callerEmail)) {
  // throw new IllegalArgumentException("You can only return your own loans");
  // }
  // }

  // if (loan.getStatus() == LoanStatus.RETURNED)
  // return loan; // idempotent

  // LocalDate returnDate = LocalDate.now();
  // loan.setReturnDate(returnDate);
  // loan.setStatus(LoanStatus.RETURNED);

  // // fine = days late * category rate
  // double fine = 0.0;
  // LocalDate due = loan.getDueDate();
  // if (due != null && returnDate.isAfter(due)) {
  // long daysLate = Math.max(0, (int)
  // java.time.temporal.ChronoUnit.DAYS.between(due, returnDate));
  // double rate = Optional.ofNullable(loan.getBook())
  // .map(Book::getCategory).map(Category::getFineRatePerDay).orElse(0.0);
  // fine = Math.max(0.0, daysLate * rate);
  // }
  // loan.setFineAmount(fine);

  // // lock book then increment copies
  // Book locked = bookRepo.findByIdForUpdate(loan.getBook().getBookId())
  // .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
  // locked.setAvailableCopies((locked.getAvailableCopies() == null ? 0 :
  // locked.getAvailableCopies()) + 1);
  // bookRepo.save(locked);

  // Loan saved = loanRepo.save(loan);
  // fineService.createOrUpdateFineForLoan(saved);
  // return saved;
  // }

  // snigdha
  @Transactional
  public Loan returnBook(Long userId, Integer bookId, LocalDate providedReturnDate) {

    if (userId == null || bookId == null) {
      throw new IllegalArgumentException("Provide userId and bookId");
    }

    // fetch user by ID
    User user = userRepo.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // fetch book by ID
    Book book = bookRepo.findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    // find active loan for this user and book
    Loan loan = loanRepo.findByBookAndUserAndStatus(book, user, LoanStatus.ISSUED)
        .orElseThrow(() -> new ResourceNotFoundException("Active loan not found for this user and book"));

    if (loan.getStatus() == LoanStatus.RETURNED)
      return loan; // already returned

    // set return date
    LocalDate returnDate = (providedReturnDate != null) ? providedReturnDate : LocalDate.now();
    if (returnDate.isBefore(loan.getIssueDate())) {
      throw new IllegalArgumentException("Return date cannot be before issue date");
    }
    loan.setReturnDate(returnDate);
    loan.setStatus(LoanStatus.RETURNED);

    // fine calculation
    LocalDate due = loan.getDueDate();
    double fine = 0.0;
    if (due != null && returnDate.isAfter(due)) {
      long daysLate = java.time.temporal.ChronoUnit.DAYS.between(due, returnDate);
      double rate = Optional.ofNullable(loan.getBook())
          .map(Book::getCategory)
          .map(Category::getFineRatePerDay)
          .orElse(0.0);
      fine = Math.max(0.0, daysLate * rate);
    }
    loan.setFineAmount(fine);

    // increment book copies
    Book locked = bookRepo.findByIdForUpdate(book.getBookId())
        .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    locked.setAvailableCopies((locked.getAvailableCopies() == null ? 0 : locked.getAvailableCopies()) + 1);
    bookRepo.save(locked);

    Loan saved = loanRepo.save(loan);
    fineService.createOrUpdateFineForLoan(saved);

    return saved;
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

  public List<Loan> getAllLoans() {
    return loanRepo.findAll();
  }

  // returns all loans of that user (past + current)
  public List<Loan> getAllLoansForUser(String email) {
    User user = userRepo.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return loanRepo.findByUser(user);
  }

  // all active issued books
  public List<Loan> getIssuedBooksForUser(String email) {
  User user = userRepo.findByEmailIgnoreCase(email)
  .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  return loanRepo.findByUserAndStatus(user, LoanStatus.ISSUED);
  }

  // get issued books for specific user
  public List<Book> getIssuedBooksForUser(Long userId) {
    User user = userRepo.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    List<Loan> loans = loanRepo.findByUserAndStatus(user, LoanStatus.ISSUED);

    return loans.stream()
        .map(Loan::getBook)
        .toList();
  }

  // all returned books
  public List<Loan> getReturnedBooksForUser(String email) {
    User user = userRepo.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return loanRepo.findByUserAndStatus(user, LoanStatus.RETURNED);
  }

  // overdue books
  public List<Loan> getOverdueBooksForUser(String email) {
    User user = userRepo.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return loanRepo.findByUserAndStatusAndDueDateBefore(user, LoanStatus.ISSUED, LocalDate.now());
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
