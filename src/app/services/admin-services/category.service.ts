import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';

interface Category {
  categoryId?: number;
  name: string;
  fineRatePerDay: string;
  isExist: boolean
}

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private baseUrl = 'http://localhost:8080/api/admin/categories';

  constructor(private http: HttpClient) { }

  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.baseUrl}/all`).pipe(
      map(categories => categories.filter(a => a.isExist === true)) // only active categories
    );
  }


  addCategory(category: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/add`, category);
  }

  updateCategory(categoryId: number, category: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/update/${categoryId}`, category);
  }

  deleteCategory(categoryId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${categoryId}`);
  }
}