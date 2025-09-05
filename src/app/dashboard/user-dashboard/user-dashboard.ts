import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Sidemenu } from '../../layout/sidemenu/sidemenu';
import { BooksManagement } from './books-management/books-management';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Sidemenu,
    BooksManagement,
  ],
  templateUrl: './user-dashboard.html',
})
export class UserDashboard implements OnInit {
  username: string = '';
  currentSection: string = 'books'; // Default section

  constructor(private authService: AuthService) { }

  ngOnInit() {
    this.authService.getProfile().subscribe({
      next: (profile) => {
        this.username = profile.name;
        if (profile.role !== 'USER') {
          console.error('Access denied! User is not an USER.');
        }

        // Load last section from localStorage if exists
        const lastSection = localStorage.getItem('userCurrentSection');
        this.currentSection = lastSection ? lastSection : 'books';
      },
      error: (err) => {
        console.error('Failed to load profile:', err);
        this.authService.logout();
      }
    });
  }

  onSectionSelected(section: string) {
    this.currentSection = section;
    localStorage.setItem('userCurrentSection', section); // save selection
    console.log(`User Dashboard: Switched to section: ${this.currentSection}`);
  }
}