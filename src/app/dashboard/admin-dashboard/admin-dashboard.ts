import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminBookService } from '../../services/admin-book.service';
import { UserService } from '../../services/user.service';
import { AuthorService } from '../../services/author.service';
import { CategoryService } from '../../services/category.service';
import { AdminIssueBooksService } from '../../services/admin-issue-books.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-dashboard',
  imports: [FormsModule, CommonModule],
  templateUrl: './admin-dashboard.html',
})
export class AdminDashboard {
  username: string = '';

  constructor(
    private authService: AuthService,
    private bookService: AdminBookService,
    private adminIssueBooksService: AdminIssueBooksService,
    private userService: UserService,
    private authorService: AuthorService,
    private categoryService: CategoryService
  ) { }

  ngOnInit() {
    this.authService.getProfile().subscribe(profile => {
      this.username = profile.name;
    });
  }


  books: any[] = [];
  users: any[] = [];
  authors: any[] = [];
  categories: any[] = [];
  issuedBooks: any[] = [];
  overdueBooks: any[] = [];
  currentSection: string = '';
  error = '';

  // ---------------------- Manage Books ----------------------
  loadAllBooks() {
    this.currentSection = 'books';
    this.bookService.getAllBooks().subscribe({
      next: data => this.books = data,
      error: err => this.error = 'Failed to load books'
    });
  }

  addBook() {
    console.log('Add book clicked');
    // Add book logic goes here (e.g., open modal or navigate)
  }

  editBook(book: any) {
    console.log('Edit book clicked', book);
    // Edit book logic (e.g., show edit form or dialog)
    this.bookService.editBook(book.book_id, book).subscribe({
      next: response => console.log('Edited book', response),
      error: err => this.error = 'Failed to edit book'
    });
  }

  deleteBook(bookId: number) {
    if (confirm('Are you sure you want to delete this book?')) {
      this.bookService.deleteBook(bookId).subscribe({
        next: () => {
          this.books = this.books.filter(b => b.book_id !== bookId);
          console.log(`Book with ID ${bookId} deleted successfully.`);
        },
        error: err => this.error = 'Failed to delete book'
      });
    }
  }

  // ---------------------- Manage Users ----------------------
  loadUsers() {
    this.currentSection = 'users';
    this.userService.getAllUsers().subscribe({
      next: data => this.users = data,
      error: err => this.error = 'Failed to load users'
    });
  }

  // ---------------------- Manage Authors ----------------------
  loadAuthors() {
    this.currentSection = 'authors';
    this.authorService.getAllAuthors().subscribe({
      next: data => this.authors = data,
      error: err => this.error = 'Failed to load authors'
    });
  }

  addAuthor() {
    console.log('Add Author clicked');
    // Add author logic
  }

  // ---------------------- Manage Categories ----------------------
  loadCategories() {
    this.currentSection = 'categories';
    this.categoryService.getAllCategories().subscribe({
      next: data => this.categories = data,
      error: err => this.error = 'Failed to load categories'
    });
  }

  addCategory() {
    console.log('Add Category clicked');
    // Add category logic
  }

  // ---------------------- Issued Books ----------------------
  loadIssuedBooks() {
    this.currentSection = 'issued';
    this.adminIssueBooksService.getIssuedBooks().subscribe({
      next: data => this.issuedBooks = data,
      error: err => this.error = 'Failed to load issued books'
    });
  }

  // ---------------------- Overdue Books ----------------------
  loadOverdueBooks() {
    this.currentSection = 'overdue';
    this.adminIssueBooksService.getOverdueBooks().subscribe({
      next: data => this.overdueBooks = data,
      error: err => this.error = 'Failed to load overdue books'
    });
  }
}