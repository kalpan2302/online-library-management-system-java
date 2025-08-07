import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
})
export class Register {
  email = '';
  password = '';
  name = '';
  contact = '';
  errorMessage = '';

  constructor(private router: Router, private authService: AuthService) { }

  register() {
    this.authService.register(this.email, this.password, this.name, this.contact).subscribe({
      next: (result: { token: string, role: string }) => {
        localStorage.setItem('token', result.token);
        localStorage.setItem('role', result.role);

        //  Clear form inputs
        this.email = '';
        this.password = '';
        this.name = '';
        this.contact = '';

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
}