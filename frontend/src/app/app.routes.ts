import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'paquetes' },
  {
    path: 'login',
    loadComponent: () => import('./components/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'paquetes',
    loadComponent: () => import('./components/paquete-list.component').then(m => m.PaqueteListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'paquetes/nuevo',
    loadComponent: () => import('./components/paquete-form.component').then(m => m.PaqueteFormComponent),
    canActivate: [authGuard, adminGuard]
  },
  { path: '**', redirectTo: 'paquetes' }
];
