import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface MenuItem {
  label: string;
  icon: string;
  section: string;
}

@Component({
  selector: 'app-sidemenu',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidemenu.html',
})
export class Sidemenu implements OnInit {
  @Output() sectionSelected = new EventEmitter<string>();
  username: string = '';
  isOpen: boolean = true;
  menuItems: MenuItem[] = [];
  activeSection: string = '';

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.authService.getProfile().subscribe({
      next: (profile) => {
        this.username = profile.name;
        this.setMenuItems(profile.role);
      },
      error: () => {
        this.username = 'User';
        this.setMenuItems('USER');
      },
    });
  }

  setMenuItems(role: string) {
    if (role === 'ADMIN') {
      this.menuItems = [
        { label: 'Admins', icon: 'person', section: 'admins' },
        { label: 'Users', icon: 'person', section: 'users' },
        { label: 'Manage Books', icon: 'menu_book', section: 'books' },
        { label: 'Manage Authors', icon: 'badge', section: 'authors' },
        { label: 'Manage Category', icon: 'category', section: 'categories' },
        { label: 'Manage Publications', icon: 'collections_bookmark', section: 'publications' },
        { label: 'Issue Book', icon: 'assignment', section: 'issueBook' },
        { label: 'Return Book', icon: 'assignment_return', section: 'returnBook' },
        { label: 'Issue History', icon: 'history', section: 'issueHistory' },
        { label: 'Logout', icon: 'logout', section: 'logout' },
      ];
      // this.activeSection = 'users';
      // this.sectionSelected.emit(this.activeSection);

      const savedSection = localStorage.getItem('adminCurrentSection');
      this.activeSection = savedSection ? savedSection : 'admins';
      this.sectionSelected.emit(this.activeSection);
    }

    else {
      this.menuItems = [
        { label: 'Book List', icon: 'menu_book', section: 'books' },
        { label: 'Issued Books', icon: 'assignment', section: 'issued' },
        { label: 'Returned Books', icon: 'assignment_return', section: 'returned' },
        { label: 'Overdue Books', icon: 'warning', section: 'overdue' },
        { label: 'Saved Books', icon: 'bookmark', section: 'saved' },
        { label: 'Notifications', icon: 'notifications', section: 'notifications' },
        { label: 'Recommended Books', icon: 'star', section: 'recommended' },
        { label: 'Logout', icon: 'logout', section: 'logout' },
      ];
      // this.activeSection = 'books';
      // this.sectionSelected.emit(this.activeSection);

      const savedSection = localStorage.getItem('userCurrentSection');
      this.activeSection = savedSection ? savedSection : 'books';
      this.sectionSelected.emit(this.activeSection);
    }
  }

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  onItemClick(item: MenuItem) {
    if (item.section === 'logout') {
      this.logout();
    } else {
      this.activeSection = item.section;
      this.sectionSelected.emit(item.section);
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
