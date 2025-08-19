import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })

// for admin dashboard: to view issued and overdue books
export class AdminIssueBooksService {
  constructor(private http: HttpClient) { }

  getIssuedBooks(): Observable<any[]> {
    if (environment.useDummy) {
      return of([
        { userId: 101, username: 'User One', bookName: 'The Alchemist' },
        { userId: 102, username: 'User Two', bookName: '1984' }
      ]);
    }
    return this.http.get<any[]>('http://localhost:8080/admin/issued-books');
  }

  getOverdueBooks(): Observable<any[]> {
    if (environment.useDummy) {
      return of([
        { userId: 101, username: 'User One', contact: '9998887770', bookName: 'Clean Code' }
      ]);
    }
    return this.http.get<any[]>('http://localhost:8080/admin/overdue-books');
  }
}