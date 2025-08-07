import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { Header } from './layout/header/header';
import { Footer } from './layout/footer/footer';
import { Sidemenu } from './layout/sidemenu/sidemenu';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, Header, Footer, Sidemenu, RouterModule],
  templateUrl: './app.html',
})
export class App {
  isMenuOpen = false;

  constructor(private router: Router) {}

  // Toggle side menu
  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  // Check if user is logged in
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  // Check if current page is login page
  isLoginPage(): boolean {
    return this.router.url === '/' || this.router.url === '/login';
  }

  // Logout and close the menu
  logout(): void {
    localStorage.clear();
    this.isMenuOpen = false;
    this.router.navigate(['/login']);
  }
}
