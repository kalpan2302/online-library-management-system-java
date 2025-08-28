import { Routes } from '@angular/router';

import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { UserDashboard } from './dashboard/user-dashboard/user-dashboard';
import { AdminDashboard } from './dashboard/admin-dashboard/admin-dashboard';
import { Home } from './home/home';

import { userGuard } from './guards/user-guard';
import { adminGuard } from './guards/admin-guard';

import { BooksManagement } from './dashboard/admin-dashboard/books-management/books-management';
import { IssueBookManagement } from './dashboard/admin-dashboard/issue-book-management/issue-book-management';
import { ManageAuthor } from './dashboard/admin-dashboard/manage-author/manage-author';
import { ManageCategory } from './dashboard/admin-dashboard/manage-category/manage-category';
import { ManageAdmins } from './dashboard/admin-dashboard/manage-admins/manage-admins'; // Added import

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'books', component: BooksManagement, canActivate: [adminGuard] },
  { path: 'issue-book', component: IssueBookManagement, canActivate: [adminGuard] },
  { path: 'manage-authors', component: ManageAuthor, canActivate: [adminGuard] },
  { path: 'manage-category', component: ManageCategory, canActivate: [adminGuard] },
  { path: 'manage-admins', component: ManageAdmins, canActivate: [adminGuard] }, // Added route
  { path: 'user-dashboard', component: UserDashboard, canActivate: [userGuard] },
  { path: 'admin-dashboard', component: AdminDashboard, canActivate: [adminGuard] },

  { path: '**', redirectTo: '', pathMatch: 'full' } // Fixed wildcard route syntax
];
