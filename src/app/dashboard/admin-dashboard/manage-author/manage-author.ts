import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Author {
  id: number;
  name: string;
}

@Component({
  selector: 'app-manage-author',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-author.html'
})
export class ManageAuthor {
  authors: Author[] = [
    { id: 1, name: 'Ellen Lupton' },
    { id: 2, name: 'Ellen Lupton' },
    { id: 3, name: 'Ellen Lupton' }
  ];

  error = '';
  showAddModal = false;
  newAuthorName = '';

  openAddModal() {
    this.newAuthorName = '';
    this.showAddModal = true;
    this.error = '';
  }

  closeAddModal() {
    this.showAddModal = false;
    this.error = '';
  }

  addAuthor() {
    if (!this.newAuthorName.trim()) {
      this.error = 'Author name is required.';
      return;
    }
    this.authors.push({ id: this.authors.length + 1, name: this.newAuthorName });
    this.closeAddModal();
  }
}
