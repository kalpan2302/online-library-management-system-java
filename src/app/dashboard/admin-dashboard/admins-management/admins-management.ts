import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../services/admin-services/user.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admins-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admins-management.html'
})
export class AdminsManagement implements OnInit {
  users: any[] = [];
  error: string = '';

  constructor(private userService: UserService) { }

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getAllAdmins().subscribe({
      next: (data) => this.users = data,
      error: () => this.error = 'Failed to load users'
    });
  }

  showDialog = false;
  newAdmin = {
    name: '',
    email: '',
    phone: '',
    password: ''
  };

  showToast = false;
  toastMessage = '';


  openAddDialog() {
    this.showDialog = true;
  }

  closeDialog() {
    this.showDialog = false;
    this.newAdmin = { name: '', email: '', phone: '', password: '' };
  }

  saveAdmin() {
    const payload = { ...this.newAdmin, role: 'ADMIN' };

    this.userService.addUser(payload).subscribe({
      next: () => {
        this.closeDialog();
        this.loadUsers(); // refresh admins list
        // show toast
        this.toastMessage = 'Admin added successfully';
        this.showToast = true;

        // hide after 3 seconds
        setTimeout(() => this.showToast = false, 3000);
      },
      error: () => this.error = 'Failed to add admin'
    });
  }

  showPassword = false;
  togglePassword() {
    this.showPassword = !this.showPassword;
  }

}