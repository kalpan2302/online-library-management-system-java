import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

// for user dashboard:
// to view all the available books, issued books, returned books, and overdue books of particular logged in user

export class BookServiceForUser {
  private baseUrl = 'http://localhost:8080/books'; // keep your real API

  constructor(private http: HttpClient) {}

  getAllBooks(): Observable<any[]> {
    if (environment.useDummy) {
      return of([
        { title: 'The Alchemist', author: 'Paulo Coelho' },
        { title: '1984', author: 'George Orwell' }
      ]);
    }
    return this.http.get<any[]>(`${this.baseUrl}/all`);
  }

  getIssuedBooks(): Observable<any[]> {
    if (environment.useDummy) {
      return of([
        { title: 'Clean Code', author: 'Robert C. Martin' }
      ]);
    }
    return this.http.get<any[]>(`${this.baseUrl}/issued`);
  }

  getReturnedBooks(): Observable<any[]> {
    if (environment.useDummy) {
      return of([
        { title: 'Design Patterns', author: 'GoF' }
      ]);
    }
    return this.http.get<any[]>(`${this.baseUrl}/returned`);
  }

  getOverdueBooks(): Observable<any[]> {
    if (environment.useDummy) {
      return of([
        { title: 'OS Concepts', author: 'Galvin' }
      ]);
    }
    return this.http.get<any[]>(`${this.baseUrl}/overdue`);
  }
}
// Note: Replace the dummy data with actual API calls when backend is ready
// The environment variable `useDummy` can be set to true in development mode to use dummy data
// and false in production to use real API endpoints.