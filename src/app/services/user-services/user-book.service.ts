import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})

// for user dashboard:
// to view all the available books

export class BookServiceForUser {
  private baseUrl = 'http://localhost:8080/books'; // keep your real API

  constructor(private http: HttpClient) { }

  getAllBooks(): Observable<any[]> {
    if (environment.useDummy) {
      return of([
        {
          title: 'The Alchemist',
          author: 'Paulo Coelho',
          category: 'Fiction',
          totalCopies: 10,
          availableCopies: 6
        },
        {
          title: '1984',
          author: 'George Orwell',
          category: 'Dystopian',
          totalCopies: 8,
          availableCopies: 3
        },
        {
          title: 'To Kill a Mockingbird',
          author: 'Harper Lee',
          category: 'Classic',
          totalCopies: 12,
          availableCopies: 7
        },
      ]);
    }
    return this.http.get<any[]>(`${this.baseUrl}/all`);
  }
}
// Note: Replace the dummy data with actual API calls when backend is ready
// The environment variable `useDummy` can be set to true in development mode to use dummy data
// and false in production to use real API endpoints.