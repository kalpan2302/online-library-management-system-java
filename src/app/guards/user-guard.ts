import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

export const userGuard: CanActivateFn = () => {
  const router = inject(Router);
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  if (!token) {
    window.alert('Access denied: Please log in');
    router.navigate(['/login']);
    return false;
  }

  if (token && role === 'USER') {
    return true;
  } else {
    window.alert('Access denied: Users only');
    router.navigate(['/login']);
    return false;
  }
};
// This guard checks if the user is logged in and has the 'USER' role.
// If not, it redirects to the login page and shows an alert message.