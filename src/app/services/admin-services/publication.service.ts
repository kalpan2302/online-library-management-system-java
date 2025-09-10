import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';

interface Publication {
  publicationId?: number;
  name: string;
  address: string;
  isExist: boolean
}

@Injectable({
  providedIn: 'root'
})
export class PublicationService {
  private baseUrl = 'http://localhost:8080/api/admin/publications';

  constructor(private http: HttpClient) { }

  getAllPublications(): Observable<Publication[]> {
    return this.http.get<Publication[]>(`${this.baseUrl}/all`).pipe(
      map(publications => publications.filter(a => a.isExist === true)) // only active publications
    );
  }


  addPublication(publication: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/add`, publication);
  }

  updatePublication(publicationId: number, publication: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/update/${publicationId}`, publication);
  }

  deletePublication(publicationId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${publicationId}`);
  }
}