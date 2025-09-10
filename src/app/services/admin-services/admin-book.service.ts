// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable, map } from 'rxjs';

// export interface Book {
//   bookId: number;
//   title: string;
//   isbn: string;
//   totalCopies: number;
//   availableCopies: number;
//   shelfLocation: string;
//   publicationName: string;
//   categoryName: string;
//   authorsNames: string;
// }

// @Injectable({
//   providedIn: 'root'
// })
// export class AdminBookService {
//   private baseUrl = 'http://localhost:8080/api/books';

//   constructor(private http: HttpClient) { }

//   // Fetch all active books from backend
//   getAllBooks(): Observable<Book[]> {
//     return this.http.get<any[]>(`${this.baseUrl}/all`).pipe(
//       map(books => books.map(b => ({
//         bookId: b.bookId,
//         title: b.title,
//         isbn: b.isbn,
//         totalCopies: b.totalCopies,
//         availableCopies: b.availableCopies,
//         shelfLocation: b.shelfLocation,
//         publicationName: b.publication?.name || '',
//         categoryName: b.category?.name || '',
//         authorsNames: b.authors?.map((a: any) => a.name).join(', ') || ''
//       })))
//     );
//   }

//   deleteBook(id: number): Observable<any> {
//     return this.http.delete(`${this.baseUrl}/${id}`);
//   }

//   editBook(id: number, updatedBook: any): Observable<any> {
//     return this.http.put(`${this.baseUrl}/${id}`, updatedBook);
//   }
// }




import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface Book {
  bookId: number;
  title: string;
  isbn: string;
  totalCopies: number;
  availableCopies: number;
  shelfLocation: string;
  publicationName: string;
  categoryName: string;
  authorsNames: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminBookService {
  private baseUrl = 'http://localhost:8080/api/admin/books';

  constructor(private http: HttpClient) { }

  // Fetch all active books
  getAllBooks(): Observable<Book[]> {
    return this.http.get<any[]>(`${this.baseUrl}/all`).pipe(
      map(books =>
        books
          .filter(b => b.isExist === true)
          .map(b => ({
            bookId: b.bookId,
            title: b.title,
            isbn: b.isbn,
            totalCopies: b.totalCopies,
            availableCopies: b.availableCopies,
            shelfLocation: b.shelfLocation,
            publicationName: b.publication?.name || '',
            categoryName: b.category?.name || '',
            authorsNames: b.authors?.map((a: any) => a.name).join(', ') || ''
          }))
      )
    );
  }

  // Add a new book
  addBook(newBook: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/add`, newBook);
  }

  // Update a book
  editBook(id: number, updatedBook: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/update/${id}`, updatedBook);
  }

  // Soft-delete a book
  deleteBook(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete/${id}`);
  }
}