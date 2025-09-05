import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

// for user/admin authentication and registration (only user 'registers' and, both user and admin can 'login')
export class AuthService {
  private baseUrl = 'http://localhost:8080'; // change as per backend
  private dummyUsers = [
    {
      email: 'admin@example.com',
      password: 'admin123',
      name: 'Admin 1',
      contact: '1234567890',
      token: 'mock-admin-token',
      role: 'ADMIN'
    },
    {
      email: 'user@example.com',
      password: 'user123',
      name: 'User 1',
      contact: '9876543210',
      token: 'mock-user-token',
      role: 'USER'
    }
  ];

  constructor(private http: HttpClient) { }


  login(email: string, password: string): Observable<{ token: string; role: string }> {
    return this.http.post<{ token: string; role: string }>(
      `${this.baseUrl}/auth/login`,
      { email, password }
    );
  }

  register(email: string, password: string, name: string, phone: string):
      Observable<{ message: string }> {
    return this.http.post<{ message: string }>(
      `${this.baseUrl}/auth/register`,
      { email, password, name, phone: phone } // phone maps to backend
    );
  }

  getProfile(): Observable<{ name: string, email: string, role: string }> {
    const token = localStorage.getItem('token');
    if (!token) return throwError(() => new Error('No token found'));

    return this.http.get<{ name: string, email: string, role: string }>(
      `${this.baseUrl}/auth/profile`,
    );
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }
}