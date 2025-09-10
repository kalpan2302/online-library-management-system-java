import { Component, NgZone, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthorService } from '../../../services/admin-services/author.service';

interface Author {
  authorId?: number;
  name: string;
  bio: string;
}

@Component({
  selector: 'app-authors-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './authors-management.html',
})
export class AuthorsManagement implements OnInit {
  authors: Author[] = [];
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
  dialogData: Author = { name: '', bio: '' };

  // delete confirmation state
  showDeleteDialog = false;
  deleteTarget: Author | null = null;

  constructor(private authorService: AuthorService, private ngZone: NgZone) { }

  ngOnInit(): void {
    this.loadAuthors();
  }

  loadAuthors() {
    this.authorService.getAllAuthors().subscribe({
      next: (data) => {
        this.authors = data;
      },
      error: () => (this.error = 'Failed to load authors'),
    });
  }


  // ---- Add / Edit Dialog ----
  openAddDialog() {
    this.isEdit = false;
    this.dialogData = { name: '', bio: '' }; // fresh object
    this.showDialog = true;
  }

  openEditDialog(author: Author) {
    this.isEdit = true;
    this.dialogData = { ...author }; // clone to avoid direct mutation
    this.showDialog = true;
  }

  closeDialog() {
    this.showDialog = false;
  }

  // Use inside API success handlers:
  confirmAdd() {
    if (!this.dialogData.name.trim()) return;

    const newAuthor = { name: this.dialogData.name, bio: this.dialogData.bio };
    this.authorService.addAuthor(newAuthor).subscribe({
      next: () => {
        this.loadAuthors();
        this.closeDialog();
        this.triggerToast('Author added successfully ✅');
      },
      error: () => (this.error = 'Failed to add author'),
    });
  }

  confirmUpdate() {
    if (!this.dialogData.authorId) return;

    this.authorService.updateAuthor(this.dialogData.authorId, this.dialogData).subscribe({
      next: () => {
        this.loadAuthors();
        this.closeDialog();
        this.triggerToast('Author updated successfully ✏️');
      },
      error: () => (this.error = 'Failed to update author'),
    });
  }

  // ---- Delete Methods ----
  openDeleteDialog(author: Author) {
    this.deleteTarget = author;
    this.showDeleteDialog = true;
  }

  closeDeleteDialog() {
    this.showDeleteDialog = false;
  }

  // this is what i needed exactly

  confirmDelete() {
    if (!this.deleteTarget?.authorId) return;

    this.authorService.deleteAuthor(this.deleteTarget.authorId).subscribe({
      next: () => {
        this.closeDeleteDialog();

        this.authors = this.authors.filter(a => a.authorId !== this.deleteTarget?.authorId);

        this.triggerToast('Author deleted successfully ❌');
      },
      error: (err) => {
        this.error = 'Failed to delete author';
        this.closeDeleteDialog();
      }
    });
  }

  // works

  // confirmDelete() {
  //   if (!this.deleteTarget?.authorId) return;

  //   this.authorService.deleteAuthor(this.deleteTarget.authorId).subscribe({
  //     next: () => {
  //       this.closeDeleteDialog();

  //       // Show toast before reload
  //       this.triggerToast('Author deleted successfully ❌');

  //       // Refresh the page after a small delay so toast is visible
  //       setTimeout(() => {
  //         window.location.reload();
  //       }, 500); // 0.5s delay to briefly show the toast
  //     },
  //     error: () => {
  //       this.error = 'Failed to delete author';
  //       this.closeDeleteDialog();
  //     }
  //   });
  // }
}