import { Component, NgZone, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../../services/admin-services/category.service';

interface Category {
  categoryId?: number;
  name: string;
  fineRatePerDay: string;
}

@Component({
  selector: 'app-category-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './category-management.html',
})
export class CategoryManagement implements OnInit {
  categories: Category[] = [];
  error: string = '';

  toastMessage: string = '';
  showToast: boolean = false;

  private triggerToast(message: string) {
    this.toastMessage = message;
    this.showToast = true;
    setTimeout(() => (this.showToast = false), 3000); // hide after 3s
  }

  // dialog state
  showDialog = false;
  isEdit = false;
  dialogData: Category = { name: '', fineRatePerDay: '' };

  // delete confirmation state
  showDeleteDialog = false;
  deleteTarget: Category | null = null;

  constructor(private categoryService: CategoryService, private ngZone: NgZone) { }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories() {
    this.categoryService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: () => (this.error = 'Failed to load categories'),
    });
  }


  // ---- Add / Edit Dialog ----
  openAddDialog() {
    this.isEdit = false;
    this.dialogData = { name: '', fineRatePerDay: '' }; // fresh object
    this.showDialog = true;
  }

  openEditDialog(category: Category) {
    this.isEdit = true;
    this.dialogData = { ...category }; // clone to avoid direct mutation
    this.showDialog = true;
  }

  closeDialog() {
    this.showDialog = false;
  }

  // Use inside API success handlers:
  confirmAdd() {
    if (!this.dialogData.name.trim()) return;

    const newCategory = { name: this.dialogData.name, fineRatePerDay: this.dialogData.fineRatePerDay };
    this.categoryService.addCategory(newCategory).subscribe({
      next: () => {
        this.loadCategories();
        this.closeDialog();
        this.triggerToast('Category added successfully ✅');
      },
      error: () => (this.error = 'Failed to add category'),
    });
  }

  confirmUpdate() {
    if (!this.dialogData.categoryId) return;

    this.categoryService.updateCategory(this.dialogData.categoryId, this.dialogData).subscribe({
      next: () => {
        this.loadCategories();
        this.closeDialog();
        this.triggerToast('Category updated successfully ✏️');
      },
      error: () => (this.error = 'Failed to update category'),
    });
  }

  // ---- Delete Methods ----
  openDeleteDialog(category: Category) {
    this.deleteTarget = category;
    this.showDeleteDialog = true;
  }

  closeDeleteDialog() {
    this.showDeleteDialog = false;
  }

  confirmDelete() {
    if (!this.deleteTarget?.categoryId) return;

    this.categoryService.deleteCategory(this.deleteTarget.categoryId).subscribe({
      next: () => {
        this.closeDeleteDialog();

        this.categories = this.categories.filter(a => a.categoryId !== this.deleteTarget?.categoryId);

        this.triggerToast('Category deleted successfully ❌');
      },
      error: (err) => {
        this.error = 'Failed to delete category';
        this.closeDeleteDialog();
      }
    });
  }
}