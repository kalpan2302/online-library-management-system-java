import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Sidemenu } from '../../layout/sidemenu/sidemenu';
import { UsersManagement } from './users-management/users-management';
import { BooksManagement } from './books-management/books-management';
import { AdminsManagement } from './admins-management/admins-management';
import { AuthorsManagement } from './authors-management/authors-management';
import { CategoryManagement } from './category-management/category-management';
import { PublicationManagement } from './publication-management/publication-management';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Sidemenu,
    AdminsManagement,
    UsersManagement,
    BooksManagement,
    AuthorsManagement,
    CategoryManagement,
    PublicationManagement
  ],
  templateUrl: './admin-dashboard.html',
})
export class AdminDashboard implements OnInit {
  username: string = '';
  currentSection: string = 'users'; // Default section
  sidebarOpen: boolean = true;

  constructor(private authService: AuthService) { }

  ngOnInit() {
    this.authService.getProfile().subscribe({
      next: (profile) => {
        this.username = profile.name;
        if (profile.role !== 'ADMIN') {
          console.error('Access denied! User is not an ADMIN.');
        }
        // Load last section from localStorage if exists
        const lastSection = localStorage.getItem('adminCurrentSection');
        this.currentSection = lastSection ? lastSection : 'users';
      },
      error: (err) => {
        console.error('Failed to load profile:', err);
        this.authService.logout();
      }
    });
  }

  onSectionSelected(section: string) {
    this.currentSection = section;
    localStorage.setItem('adminCurrentSection', section); // save selection
    console.log(`Admin Dashboard: Switched to section: ${this.currentSection}`);
  }
}