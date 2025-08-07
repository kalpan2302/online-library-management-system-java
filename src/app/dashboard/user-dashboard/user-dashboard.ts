import { Component } from '@angular/core';
import { BookServiceForUser } from '../../services/user-book.service';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-dashboard.html',
})
export class UserDashboard {
  username : string = '';;

  constructor(private bookService: BookServiceForUser, private authService: AuthService) { }

  ngOnInit() {
    this.authService.getProfile().subscribe(profile => {
      this.username = profile.name;
    });
  }


  books: any[] = [];
  error = '';

  loadAllBooks() {
    this.bookService.getAllBooks().subscribe({
      next: data => this.books = data,
      error: err => this.error = 'Failed to load books'
    });
  }

  loadIssuedBooks() {
    this.bookService.getIssuedBooks().subscribe({
      next: data => this.books = data,
      error: err => this.error = 'Failed to load issued books'
    });
  }

  loadReturnedBooks() {
    this.bookService.getReturnedBooks().subscribe({
      next: data => this.books = data,
      error: err => this.error = 'Failed to load returned books'
    });
  }

  loadOverdueBooks() {
    this.bookService.getOverdueBooks().subscribe({
      next: data => this.books = data,
      error: err => this.error = 'Failed to load overdue books'
    });
  }
}