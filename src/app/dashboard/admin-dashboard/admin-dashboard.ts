import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';        // Import Router
import { AuthService } from '../../services/auth.service';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Sidebar } from './sidebar/sidebar';
import { UsersManagement } from './users-management/users-management';
import { BooksManagement } from './books-management/books-management';
import { IssueBookManagement } from './issue-book-management/issue-book-management';
import { ReturnBookManagement } from './return-book-management/return-book-management';
import { IssueHistoryManagement } from './issue-history-management/issue-history-management';
import { RenewRequests } from './renew-requests/renew-requests';
import { ManageAuthor } from './manage-author/manage-author';
import { ManageCategory } from './manage-category/manage-category';
import { ManageAdmins } from './manage-admins/manage-admins';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Sidebar,
    UsersManagement,
    BooksManagement,
    IssueBookManagement,
    ReturnBookManagement,
    IssueHistoryManagement,
    RenewRequests,
    ManageAuthor,
    ManageCategory,
    ManageAdmins,
  ],
  templateUrl: './admin-dashboard.html',
})
export class AdminDashboard implements OnInit {
  username = '';
  currentSection = 'users';

  constructor(private authService: AuthService, private router: Router) {} // Inject Router

  ngOnInit() {
    this.authService.getProfile().subscribe({
      next: (profile) => {
        this.username = profile.name;
        if (profile.role !== 'ADMIN') {
          console.error('Access denied! User is not an ADMIN.');
          // Optionally redirect or logout here
        }
      },
      error: (err) => {
        console.error('Failed to load profile:', err);
        this.authService.logout();
      },
    });
  }

  onSectionSelected(section: string) {
    this.currentSection = section;
  }

  // Add this method to handle logout event
  onLogout() {
    this.authService.logout();
    this.router.navigate(['/login']);   // Redirect user after logout
  }
}
