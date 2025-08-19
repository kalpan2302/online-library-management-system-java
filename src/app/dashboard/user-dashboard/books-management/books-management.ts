import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookServiceForUser } from '../../../services/user-services/user-book.service';

@Component({
  selector: 'app-user-books-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './books-management.html',
})
export class BooksManagement implements OnInit {
  books: any[] = [];
  error: string = '';
  isLoggedIn = false;
  message = '';

  constructor(private bookService: BookServiceForUser) {
    this.isLoggedIn = !!localStorage.getItem('token');
  }

  ngOnInit() {
    this.loadAllBooks();
  }

  loadAllBooks() {
    this.bookService.getAllBooks().subscribe({
      next: data => this.books = data,
      error: () => this.error = 'Failed to load books'
    });
  }

  saveForLater(book: any) {
    if (!this.isLoggedIn) {
      this.message = `
      ⚠️ Please <a href="/login" class="text-blue-600 underline">login</a> 
      or <a href="/register" class="text-blue-600 underline">register</a> to save this book
    `;
      setTimeout(() => this.message = '', 5000);
      return;
    }
    console.log('Saved book:', book);
  }
}