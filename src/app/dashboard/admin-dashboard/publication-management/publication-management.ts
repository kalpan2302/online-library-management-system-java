import { Component, NgZone, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PublicationService } from '../../../services/admin-services/publication.service';

interface Publication {
  publicationId?: number;
  name: string;
  address: string;
}

@Component({
  selector: 'app-publication-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './publication-management.html',
})
export class PublicationManagement implements OnInit {
  publications: Publication[] = [];
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
  dialogData: Publication = { name: '', address: '' };

  // delete confirmation state
  showDeleteDialog = false;
  deleteTarget: Publication | null = null;

  constructor(private publicationService: PublicationService, private ngZone: NgZone) { }

  ngOnInit(): void {
    this.loadPublications();
  }

  loadPublications() {
    this.publicationService.getAllPublications().subscribe({
      next: (data) => {
        this.publications = data;
      },
      error: () => (this.error = 'Failed to load publications'),
    });
  }


  // ---- Add / Edit Dialog ----
  openAddDialog() {
    this.isEdit = false;
    this.dialogData = { name: '', address: '' }; // fresh object
    this.showDialog = true;
  }

  openEditDialog(publication: Publication) {
    this.isEdit = true;
    this.dialogData = { ...publication }; // clone to avoid direct mutation
    this.showDialog = true;
  }

  closeDialog() {
    this.showDialog = false;
  }

  // Use inside API success handlers:
  confirmAdd() {
    if (!this.dialogData.name.trim()) return;

    const newPublication = { name: this.dialogData.name, address: this.dialogData.address };
    this.publicationService.addPublication(newPublication).subscribe({
      next: () => {
        this.loadPublications();
        this.closeDialog();
        this.triggerToast('Publication added successfully ✅');
      },
      error: () => (this.error = 'Failed to add publication'),
    });
  }

  confirmUpdate() {
    if (!this.dialogData.publicationId) return;

    this.publicationService.updatePublication(this.dialogData.publicationId, this.dialogData).subscribe({
      next: () => {
        this.loadPublications();
        this.closeDialog();
        this.triggerToast('Publication updated successfully ✏️');
      },
      error: () => (this.error = 'Failed to update publication'),
    });
  }

  // ---- Delete Methods ----
  openDeleteDialog(publication: Publication) {
    this.deleteTarget = publication;
    this.showDeleteDialog = true;
  }

  closeDeleteDialog() {
    this.showDeleteDialog = false;
  }

  confirmDelete() {
    if (!this.deleteTarget?.publicationId) return;

    this.publicationService.deletePublication(this.deleteTarget.publicationId).subscribe({
      next: () => {
        this.closeDeleteDialog();

        this.publications = this.publications.filter(a => a.publicationId !== this.deleteTarget?.publicationId);

        this.triggerToast('Publication deleted successfully ❌');
      },
      error: (err) => {
        this.error = 'Failed to delete publication';
        this.closeDeleteDialog();
      }
    });
  }
}