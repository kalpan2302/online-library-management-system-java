import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { Header } from './layout/header/header';
import { Footer } from './layout/footer/footer';
import { Sidemenu } from './layout/sidemenu/sidemenu';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, Header, Footer, Sidemenu, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  isMenuOpen = false;

  constructor(private router: Router) { }

  isLoggedIn() {
    return !!localStorage.getItem('token');
  }
  
  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
    this.isMenuOpen = false;
  }
  
  isLoginPage() {
    return this.router.url === '/' || this.router.url === '/login';
  }
  
  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

}