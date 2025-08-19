import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Welcome } from './welcome/welcome';
import { About } from './about/about';
import { Contact } from './contact/contact';

@Component({
  selector: 'app-home',
  imports: [CommonModule, Welcome, About, Contact],
  templateUrl: './home.html',
})
export class Home {
  constructor(private router: Router) {}

  goToLogin() {
    this.router.navigate(['/login']);
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}