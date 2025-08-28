import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface User {
  id: number;
  fullName: string;
  email: string;
  phoneNumber: string;
}

@Component({
  selector: 'app-users-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users-management.html'
})
export class UsersManagement {
  // All users in your system
  allUsers: User[] = [
    { id: 1, fullName: 'Thinking with type', email: 'Ellen Lupton', phoneNumber: 'Arts' },
    { id: 2, fullName: 'Thinking with type', email: 'Ellen Lupton', phoneNumber: 'Arts' },
    { id: 3, fullName: 'Thinking with type', email: 'Ellen Lupton', phoneNumber: 'Arts' }
    // Add more users as needed
  ];

  perPage = 3;
  currentPage = 1;

  get totalPages(): number {
    return Math.ceil(this.allUsers.length / this.perPage);
  }

  get shownUsers(): User[] {
    const start = (this.currentPage - 1) * this.perPage;
    return this.allUsers.slice(start, start + this.perPage);
  }

  get activePages(): number[] {
    // Only include pages that would actually show users
    const pages: number[] = [];
    for (let i = 1; i <= this.totalPages; i++) {
      const start = (i - 1) * this.perPage;
      const pageUsers = this.allUsers.slice(start, start + this.perPage);
      if (pageUsers.length > 0) {
        pages.push(i);
      }
    }
    return pages;
  }

  goToPage(page: number) {
    this.currentPage = page;
  }
}
