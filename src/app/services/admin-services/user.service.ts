import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })

// for admin dashboard: to view all the registered users

export class UserService {
  constructor(private http: HttpClient) { }

  getAllUsers(): Observable<any[]> {
    if (environment.useDummy) {
      return of([
        { id: 1, name: 'User One', email: 'user1@example.com', contact: '9999999999' },
        { id: 2, name: 'User Two', email: 'user2@example.com', contact: '888888888' },
        { id: 3, name: 'User 3', email: 'user3@example.com', contact: '8111111111' }
      ]);
    }
    return this.http.get<any[]>('http://localhost:8080/users');
  }
}