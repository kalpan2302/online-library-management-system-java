import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { UserDashboard } from './dashboard/user-dashboard/user-dashboard';
import { AdminDashboard } from './dashboard/admin-dashboard/admin-dashboard';
import { Home } from './home/home';
import { userGuard } from './guards/user-guard';
import { adminGuard } from './guards/admin-guard';
import { BooksManagement } from './dashboard/user-dashboard/books-management/books-management';

export const routes: Routes = [
  { path: '', component:Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'books', component: BooksManagement },
  { path: 'user-dashboard', component: UserDashboard, canActivate: [userGuard] },
  { path: 'admin-dashboard', component: AdminDashboard, canActivate: [adminGuard] },
];