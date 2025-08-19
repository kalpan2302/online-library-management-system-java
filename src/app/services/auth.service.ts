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
    if (environment.useDummy) {
      const user = this.dummyUsers.find(
        u => u.email === email && u.password === password
      );
      if (user) {
        return of({ token: user.token, role: user.role });
      } else {
        return throwError(() => new Error('Invalid credentials'));
      }
    }

    return this.http.post<{ token: string; role: string }>(
      `${this.baseUrl}/login`,
      { email, password }
    );
  }


  register(
    email: string,
    password: string,
    name: string,
    contact: string
  ): Observable<{ token: string; role: string }> {
    if (environment.useDummy) {
      const exists = this.dummyUsers.find(user => user.email === email);
      if (exists) {
        return throwError(() => new Error('User already exists'));
      }

      const role = 'USER';
      const token = `mock-${role.toLowerCase()}-token`;

      this.dummyUsers.push({ email, password, name, contact, token, role });

      return of({ token, role });
    }

    return this.http.post<{ token: string; role: string }>(
      `${this.baseUrl}/register`,
      { email, password, name, contact, role: 'USER' }
    );
  }

  getProfile(): Observable<{ name: string, email: string, role: string }> {
    if (environment.useDummy) {
      const token = localStorage.getItem('token');
      const user = this.dummyUsers.find(u => u.token === token);

      if (user) {
        return of({
          name: user.name,
          email: user.email,
          role: user.role
        });
      } else {
        return throwError(() => new Error('User not found'));
      }
    }

    // Call real backend when connected to backend
    return this.http.get<{ name: string, email: string, role: string }>(
      `${this.baseUrl}/api/profile`
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