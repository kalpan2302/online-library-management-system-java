import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IssueBookService, IssueRecord } from '../../../services/admin-services/issue-book.service';

@Component({
  selector: 'app-return-book-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './return-book-management.html',
})
export class ReturnBookManagement {
  issues: IssueRecord[] = [];

  form = {
    userEmail: '',
    bookTitle: '',
    returnedByUserOn: '',
    fine: 0,
  };

  error = '';
  success = '';

  constructor(private issueBookService: IssueBookService) {
    this.issueBookService.issues$.subscribe((data: IssueRecord[]) => (this.issues = data));
  }

  returnBook() {
    if (!this.form.userEmail || !this.form.bookTitle || !this.form.returnedByUserOn) {
      this.error = 'Please fill all required fields';
      this.success = '';
      return;
    }

    const found = this.issues.find(
      (issue) => issue.userEmail === this.form.userEmail && issue.issuedBook === this.form.bookTitle
    );

    if (!found) {
      this.error = 'No matching issue record found';
      this.success = '';
      return;
    }

    const updatedIssue: IssueRecord = {
      ...found,
      returnStatus: 'Returned',
      returnedByUserOn: this.form.returnedByUserOn,
      fine: this.form.fine,
    };

    this.issueBookService.updateIssue(updatedIssue);

    this.success = 'Book returned successfully!';
    this.error = '';
    this.form = { userEmail: '', bookTitle: '', returnedByUserOn: '', fine: 0 };
  }
}
