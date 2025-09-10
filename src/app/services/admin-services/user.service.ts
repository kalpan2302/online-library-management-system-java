import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: string;
  created_at?: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private baseUrl = 'http://localhost:8080';
  constructor(private http: HttpClient) { }

  getAllUsers(): Observable<User[]> {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('No token found');
    }

    return this.http.get<User[]>(`${this.baseUrl}/auth/users/all`, {
    }).pipe(
      map(users => users
        .filter(u => u.role === 'USER')
        .map(u => ({
          ...u,
          created_at: u.created_at
            ? new Date(u.created_at).toLocaleDateString('en-GB')
            : ''
        }))
      )
    );
  }

  getAllAdmins(): Observable<User[]> {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('No token found');
    }

    return this.http.get<User[]>(`${this.baseUrl}/auth/users/all`, {
      headers: { Authorization: `Bearer ${token}` }
    }).pipe(
      map(users => users
        .filter(u => u.role === 'ADMIN')
        .map(u => ({
          ...u,
          created_at: u.created_at
            ? new Date(u.created_at).toLocaleDateString('en-GB')
            : ''
        }))
      )
    );
  }

  // to add another admin
  addUser(user: any): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.post(`${this.baseUrl}/auth/admin/add-user`, user, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
}