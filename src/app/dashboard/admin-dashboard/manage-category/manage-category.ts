import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Category {
  id: number;
  name: string;
}

@Component({
  selector: 'app-manage-category',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-category.html'
})
export class ManageCategory {
  categories: Category[] = [
    { id: 1, name: 'Ellen Lupton' },
    { id: 2, name: 'Ellen Lupton' },
    { id: 3, name: 'Ellen Lupton' },
  ];

  showAddModal = false;
  newCategoryName = '';
  error = '';

  openAddModal() {
    this.showAddModal = true;
    this.newCategoryName = '';
    this.error = '';
  }

  closeAddModal() {
    this.showAddModal = false;
    this.newCategoryName = '';
    this.error = '';
  }

  addCategory() {
    if (!this.newCategoryName.trim()) {
      this.error = 'Category name is required!';
      return;
    }
    this.categories.push({
      id: this.categories.length + 1,
      name: this.newCategoryName.trim()
    });
    this.closeAddModal();
  }
}
