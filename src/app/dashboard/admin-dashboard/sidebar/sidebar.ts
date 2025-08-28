import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.html',
})
export class Sidebar {
  @Input() currentSection = 'users';
  @Output() sectionSelected = new EventEmitter<string>();
  @Output() logoutClicked = new EventEmitter<void>();

  sections = [
    { key: 'users', label: 'Users', icon: 'person' },
    { key: 'books', label: 'Manage Books', icon: 'menu_book' },
    { key: 'issue-book', label: 'Issue Book', icon: 'library_books' },
    { key: 'return-book', label: 'Return Book', icon: 'assignment_return' },
    { key: 'issue-history', label: 'Issue History', icon: 'history' },
    { key: 'manage-author', label: 'Manage Authors', icon: 'group' },
    { key: 'manage-category', label: 'Manage Category', icon: 'category' },
    { key: 'renew-requests', label: 'Renew Requests', icon: 'autorenew' },
    { key: 'manage-admins', label: 'Manage Admins', icon: 'admin_panel_settings' },
  ];

  selectSection(key: string) {
    if (key === 'logout') {
      this.logoutClicked.emit();
    } else {
      this.sectionSelected.emit(key);
    }
  }
}
