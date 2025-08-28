import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminBookService } from '../../../services/admin-services/admin-book.service';

interface Book {
  id?: number;
  title: string;
  author: string;
  category: string;
  totalCopies: number;
  availableCopies: number;
}

@Component({
  selector: 'app-books-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './books-management.html',
})
export class BooksManagement implements OnInit {
  books: Book[] = [];
  error = '';

  authors: string[] = ['Paulo Coelho', 'Robert C. Martin'];
  categories: string[] = ['Fiction', 'Programming'];
  showBookDialog = false;

  bookForm: Book = {
    title: '',
    author: '',
    category: '',
    totalCopies: 1,
    availableCopies: 1,
  };

  constructor(private bookService: AdminBookService) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.bookService.getAllBooks().subscribe({
      next: (data: Book[]) => {
        this.books = data;
      },
      error: () => {
        this.error = 'Failed to load books';
      },
    });
  }

  openAddBookDialog(): void {
    this.bookForm = {
      title: '',
      author: '',
      category: '',
      totalCopies: 1,
      availableCopies: 1,
    };
    this.showBookDialog = true;
    this.error = '';
  }

  closeBookDialog(): void {
    this.showBookDialog = false;
    this.error = '';
  }

  saveBook(): void {
    if (!this.bookForm.title || !this.bookForm.author || !this.bookForm.category) {
      this.error = 'Failed to add book';
      return;
    }
    this.bookForm.availableCopies = this.bookForm.totalCopies;
    this.bookService.addBook(this.bookForm).subscribe({
      next: () => {
        this.loadBooks();
        this.closeBookDialog();
      },
      error: () => {
        this.error = 'Failed to add book';
      },
    });
  }

  deleteBook(bookId?: number): void {
    if (bookId === undefined) {
      console.error('Book ID is undefined.');
      return;
    }
    if (confirm('Are you sure you want to delete this book?')) {
      this.bookService.deleteBook(bookId).subscribe({
        next: () => {
          this.books = this.books.filter((b) => b.id !== bookId);
        },
        error: () => {
          this.error = 'Failed to delete book';
        },
      });
    }
  }
}
