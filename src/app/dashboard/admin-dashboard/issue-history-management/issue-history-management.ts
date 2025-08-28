import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IssueBookService, IssueRecord } from '../../../services/admin-services/issue-book.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-issue-history-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './issue-history-management.html',
})
export class IssueHistoryManagement implements OnInit {
  issueHistory$!: Observable<IssueRecord[]>;

  constructor(private issueBookService: IssueBookService) {}

  ngOnInit() {
    this.issueHistory$ = this.issueBookService.issues$;
  }

  clearTable() {
    this.issueBookService.clearIssues();
  }
}
