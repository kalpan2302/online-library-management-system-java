import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

// for admin dashboard: to manage all the books: view, delete, and edit books

export class AdminBookService {
  private baseUrl = 'http://localhost:8080/books';

  constructor(private http: HttpClient) { }

  getAllBooks(): Observable<any[]> {
    if (environment.useDummy) {
      return of([
        {
          book_id: 1,
          title: 'The Alchemist',
          author: 'Paulo Coelho',
          category: 'Fiction',
          totalCopies: 10,
          availableCopies: 6
        },
        {
          book_id: 2,
          title: 'Clean Code',
          author: 'Robert C. Martin',
          category: 'Programming',
          totalCopies: 5,
          availableCopies: 2
        }
      ]);
    }
    return this.http.get<any[]>(`${this.baseUrl}/all`);
  }

  deleteBook(id: number): Observable<any> {
    if (environment.useDummy) {
      return of(`Deleted book ${id}`);
    }
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  editBook(id: number, updatedBook: any): Observable<any> {
    if (environment.useDummy) {
      return of(`Edited book ${id}`);
    }
    return this.http.put(`${this.baseUrl}/${id}`, updatedBook);
  }
}