import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent),
  },
  {
    path: 'comics',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/comics/comics.component').then(m => m.ComicsComponent),
  },
  {
    path: 'inventory',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/inventory/inventory.component').then(m => m.InventoryComponent),
  },
  {
    path: 'sales',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/sales/sales.component').then(m => m.SalesComponent),
  },
  {
    path: 'publishers',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/publishers/publishers.component').then(m => m.PublishersComponent),
  },
  {
    path: 'authors',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/authors/authors.component').then(m => m.AuthorsComponent),
  },
];
