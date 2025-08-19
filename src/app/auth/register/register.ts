import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
})
export class Register {
  email = '';
  password = '';
  name = '';
  contact = '';
  errorMessage = '';
  confirmPassword: string = '';
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(private router: Router, private authService: AuthService) { }

  register() {
    if (this.password !== this.confirmPassword) {
      this.errorMessage = "Passwords do not match!";
      return;
    }

    if (!this.email || !this.password || !this.name || !this.contact) {
      this.errorMessage = 'All fields are required.';
      return;
    }

    this.authService.register(this.email, this.password, this.name, this.contact).subscribe({
      next: (result: { token: string, role: string }) => {
        localStorage.setItem('token', result.token);
        localStorage.setItem('role', result.role);

        //  Clear form inputs
        this.name = '';
        this.email = '';
        this.contact = '';
        this.password = '';
        this.confirmPassword = '';

        // Navigate based on role
        if (result.role === 'ADMIN') {
          this.router.navigate(['/admin-dashboard']);
        } else {
          this.router.navigate(['/user-dashboard']);
        }
      },
      error: () => {
        this.errorMessage = 'Registration failed. Email may already be in use.';
      }
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }
}