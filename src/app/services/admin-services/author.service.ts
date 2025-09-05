import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';

interface Author {
  authorId?: number;
  name: string;
  bio: string;
  isExist: boolean
}

@Injectable({
  providedIn: 'root'
})
export class AuthorService {
  private baseUrl = 'http://localhost:8080/api/admin/authors';

  constructor(private http: HttpClient) { }

  getAllAuthors(): Observable<Author[]> {
    return this.http.get<Author[]>(`${this.baseUrl}/all`).pipe(
      map(authors => authors.filter(a => a.isExist === true)) // only active authors
    );
  }


  addAuthor(author: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/add`, author);
  }

  updateAuthor(authorId: number, author: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/update/${authorId}`, author);
  }

  deleteAuthor(authorId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${authorId}`);
  }
}
