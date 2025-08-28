import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './layout/header/header';
import { Footer } from './layout/footer/footer';
import { AuthService } from './services/auth.service';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, Header, Footer, HttpClientModule],
  templateUrl: './app.html',
})
export class App {
  isMenuOpen = false;

  constructor(private authService: AuthService) {}

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isLoginPage(): boolean {
    const currentUrl = window.location.pathname;
    return currentUrl === '/' || currentUrl === '/login' || currentUrl === '/register';
  }

  logout(): void {
    this.authService.logout();
    this.isMenuOpen = false;
    window.location.href = '/login';
  }
}
