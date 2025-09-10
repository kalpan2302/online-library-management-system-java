import { Component, NgZone, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminBookService, Book } from '../../../services/admin-services/admin-book.service';
import { AuthorService } from '../../../services/admin-services/author.service';
import { CategoryService } from '../../../services/admin-services/category.service';
import { PublicationService } from '../../../services/admin-services/publication.service';

@Component({
  selector: 'app-admin-books-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './books-management.html'
})
export class BooksManagement implements OnInit {
  books: Book[] = [];
  error: string = '';

  authors: any[] = [];
  categories: any[] = [];
  publications: any[] = [];

  // Toast state
  toastMessage: string = '';
  showToast: boolean = false;

  private triggerToast(message: string) {
    this.toastMessage = message;
    this.showToast = true;
    setTimeout(() => (this.showToast = false), 3000);
  }

  // Dialog state
  showDialog = false;
  isEdit = false;

  dialogData: any = {
    bookId: null,
    title: '',
    isbn: '',
    totalCopies: null,
    availableCopies: null,
    shelfLocation: '',
    authorName: null,
    categoryName: null,
    publicationName: null,
  };


  // Delete confirmation state
  showDeleteDialog = false;
  deleteTarget: Book | null = null;

  constructor(
    private bookService: AdminBookService,
    private authorService: AuthorService,
    private categoryService: CategoryService,
    private publicationService: PublicationService,
    private ngZone: NgZone
  ) { }

  ngOnInit() {
    this.loadBooks();
    this.loadDropdowns();
  }

  loadBooks() {
    this.bookService.getAllBooks().subscribe({
      next: (data) => (this.books = data),
      error: () => (this.error = 'Failed to load books'),
    });
  }

  loadDropdowns() {
    this.authorService.getAllAuthors().subscribe({
      next: (data) => (this.authors = data),
      error: () => (this.error = 'Failed to load authors'),
    });

    this.categoryService.getAllCategories().subscribe({
      next: (data) => (this.categories = data),
      error: () => (this.error = 'Failed to load categories'),
    });

    this.publicationService.getAllPublications().subscribe({
      next: (data) => (this.publications = data),
      error: () => (this.error = 'Failed to load publications'),
    });
  }

  // ---- Add / Edit Dialog ----
  openAddDialog() {
    this.isEdit = false;
    this.dialogData = {
      bookId: null,
      title: '',
      isbn: '',
      // totalCopies: 0,
      // availableCopies: 0,
      totalCopies: null,
      availableCopies: null,
      shelfLocation: '',
      authorId: null,
      categoryId: null,
      publicationId: null,
    };
    this.showDialog = true;
  }

  openEditDialog(book: any) {
    console.log("Book data received for edit:", book);

    this.isEdit = true;
    this.dialogData = {
      bookId: book.bookId,
      title: book.title,
      isbn: book.isbn,
      totalCopies: book.totalCopies,
      availableCopies: book.availableCopies,
      shelfLocation: book.shelfLocation,
      authorName: book.authors?.[0]?.name || null,
      categoryName: book.category?.name || null,
      publicationName: book.publication?.name || null,
    };
    this.showDialog = true;
  }



  closeDialog() {
    this.showDialog = false;
  }

  confirmAdd() {
    if (!this.dialogData.title.trim()) return;

    const payload = {
      title: this.dialogData.title,
      isbn: this.dialogData.isbn,
      totalCopies: this.dialogData.totalCopies,
      availableCopies: this.dialogData.availableCopies,
      shelfLocation: this.dialogData.shelfLocation,
      authorNames: [this.dialogData.authorName], // backend expects a list
      categoryName: this.dialogData.categoryName,
      publicationName: this.dialogData.publicationName
    };

    console.log("Add Payload being sent to backend:", payload);


    this.bookService.addBook(payload).subscribe({
      next: () => {
        this.loadBooks();
        this.closeDialog();
        this.triggerToast('Book added successfully ✅');
      },
      error: () => (this.error = 'Failed to add book'),
    });
  }


  // ---- Confirm Update ----
  confirmUpdate() {
    if (!this.dialogData.bookId) return;

    this.bookService.editBook(this.dialogData.bookId, this.dialogData).subscribe({
      next: () => {
        this.loadBooks();
        this.closeDialog();
        this.triggerToast('Book updated successfully ✏️');
      },
      error: () => (this.error = 'Failed to update book'),
    });
  }

  // ---- Delete Methods ----
  openDeleteDialog(book: Book) {
    this.deleteTarget = book;
    this.showDeleteDialog = true;
  }

  closeDeleteDialog() {
    this.showDeleteDialog = false;
  }

  confirmDelete() {
    if (!this.deleteTarget?.bookId) return;

    console.log("Deleting book with ID:", this.deleteTarget.bookId);

    this.bookService.deleteBook(this.deleteTarget.bookId).subscribe({
      next: () => {
        this.books = this.books.filter(b => b.bookId !== this.deleteTarget?.bookId);
        this.closeDeleteDialog();
        this.triggerToast('Book deleted successfully ❌');
      },
      error: () => {
        this.error = 'Failed to delete book';
        this.closeDeleteDialog();
      },
    });
  }
}
