import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminBookService } from '../../../services/admin-services/admin-book.service';

@Component({
  selector: 'app-admin-books-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './books-management.html'
})
export class BooksManagement implements OnInit {
  books: any[] = [];
  error: string = '';

  constructor(private bookService: AdminBookService) { }

  ngOnInit() {
    this.loadBooks();
  }

  loadBooks() {
    this.bookService.getAllBooks().subscribe({
      next: (data) => this.books = data,
      error: () => this.error = 'Failed to load books'
    });
  }

  addBook() {
    console.log('Add Book clicked');
  }

  editBook(book: any) {
    console.log('Edit book clicked', book);
  }

  deleteBook(bookId: number) {
    if (confirm('Are you sure you want to delete this book?')) {
      this.bookService.deleteBook(bookId).subscribe({
        next: () => {
          this.books = this.books.filter(b => b.book_id !== bookId);
          console.log(`Book with ID ${bookId} deleted.`);
        },
        error: () => this.error = 'Failed to delete book'
      });
    }
  }
}
