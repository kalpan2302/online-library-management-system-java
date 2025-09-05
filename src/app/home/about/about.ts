import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AdminBookService } from '../../services/admin-services/admin-book.service';

@Component({
  selector: 'app-about',
  imports: [CommonModule, RouterModule],
  templateUrl: './about.html',
})
export class About {
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
}
