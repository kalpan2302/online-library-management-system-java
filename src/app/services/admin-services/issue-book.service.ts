import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface IssueRecord {
  userEmail: string;
  issuedBook: string;
  issuedBy: string;
  issueDate: string;
  scheduledReturnDate: string;
  returnStatus: string;
  returnedByUserOn: string;
  fine: number;
}

@Injectable({
  providedIn: 'root',
})
export class IssueBookService {
  private issuesSubject = new BehaviorSubject<IssueRecord[]>([]);
  issues$ = this.issuesSubject.asObservable();

  addIssue(issue: IssueRecord) {
    const current = this.issuesSubject.getValue();
    this.issuesSubject.next([...current, issue]);
  }

  updateIssue(updatedIssue: IssueRecord) {
    const current = this.issuesSubject.getValue();
    const updatedList = current.map(issue =>
      issue.userEmail === updatedIssue.userEmail && issue.issuedBook === updatedIssue.issuedBook
        ? updatedIssue
        : issue
    );
    this.issuesSubject.next(updatedList);
  }

  clearIssues() {
    this.issuesSubject.next([]);
  }
}
