import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IssueBookService, IssueRecord } from '../../../services/admin-services/issue-book.service';

@Component({
  selector: 'app-issue-book-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './issue-book-management.html',
})
export class IssueBookManagement {
  users = [
    { email: 'user1@gmail.com', name: 'User One' },
    { email: 'user2@gmail.com', name: 'User Two' },
  ];

  books = [
    { title: 'Thinking with Type' },
    { title: 'Clean Code' },
  ];

  form = {
    userEmail: '',
    userName: '',
    bookTitle: '',
    issueDate: '',
    returnDate: '',
  };

  error = '';
  success = '';

  constructor(private issueBookService: IssueBookService) {}

  issueBook() {
    if (!this.form.userEmail || !this.form.bookTitle || !this.form.issueDate || !this.form.returnDate) {
      this.error = 'Please fill all required fields';
      this.success = '';
      return;
    }

    const newIssue: IssueRecord = {
      userEmail: this.form.userEmail,
      issuedBook: this.form.bookTitle,
      issuedBy: this.form.userName || 'Admin',
      issueDate: this.form.issueDate,
      scheduledReturnDate: this.form.returnDate,
      returnStatus: '',
      returnedByUserOn: '',
      fine: 0,
    };

    this.issueBookService.addIssue(newIssue);
    this.success = 'Book issued successfully!';
    this.error = '';
    this.form = { userEmail: '', userName: '', bookTitle: '', issueDate: '', returnDate: '' };
  }
}
