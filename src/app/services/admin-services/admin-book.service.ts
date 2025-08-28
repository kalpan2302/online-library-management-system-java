import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminBookService {
  private baseUrl = 'http://localhost:3000/api/books'; // Your backend URL

  constructor(private http: HttpClient) {}

  getAllBooks(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl);
  }

  addBook(book: any): Observable<any> {
    return this.http.post<any>(this.baseUrl, book);
  }

  deleteBook(bookId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${bookId}`);
  }
}
