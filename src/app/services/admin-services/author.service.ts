import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })

// for admin dashboard: to manage authors
export class AuthorService {
  constructor(private http: HttpClient) { }

  getAllAuthors(): Observable<string[]> {
    if (environment.useDummy) {
      return of(['Paulo Coelho', 'Robert C. Martin', 'George Orwell']);
    }
    return this.http.get<string[]>('http://localhost:8080/authors');
  }

  addAuthor(name: string): Observable<string> {
    if (environment.useDummy) {
      return of(`Author ${name} added.`);
    }
    return this.http.post<string>('http://localhost:8080/authors', { name });
  }
}