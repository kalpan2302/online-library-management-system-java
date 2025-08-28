import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Admin {
  id: number;
  fullName: string;
  email: string;
  phoneNumber: string;
}

@Component({
  selector: 'app-manage-admins',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './manage-admins.html'
})
export class ManageAdmins {
  admins: Admin[] = [
    { id: 1, fullName: 'Thinking with type', email: 'Ellen Lupton', phoneNumber: 'Arts' },
    { id: 2, fullName: 'Thinking with type', email: 'Ellen Lupton', phoneNumber: 'Arts' },
    { id: 3, fullName: 'Thinking with type', email: 'Ellen Lupton', phoneNumber: 'Arts' }
    // Add more as needed
  ];

  currentPage = 1;
  perPage = 10;
  totalAdmins = 30;

  get pages(): number[] {
    return Array.from({ length: Math.ceil(this.totalAdmins / this.perPage) }, (_, i) => i + 1);
  }

  goToPage(page: number) {
    this.currentPage = page;
    // Logic to fetch admins if paging from backend
  }
}
