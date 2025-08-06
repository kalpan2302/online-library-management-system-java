
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })

// for admin dashboard: to manage category of a book

export class CategoryService {
  constructor(private http: HttpClient) {}

  getAllCategories(): Observable<string[]> {
    if (environment.useDummy) {
      return of(['Fiction', 'Non-fiction', 'Programming', 'Philosophy']);
    }
    return this.http.get<string[]>('http://localhost:8080/categories');
  }

  addCategory(name: string): Observable<string> {
    if (environment.useDummy) {
      return of(`Category ${name} added.`);
    }
    return this.http.post<string>('http://localhost:8080/categories', { name });
  }
}