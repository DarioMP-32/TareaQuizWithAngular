import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <header class="barra">
      <a class="marca" routerLink="/paquetes">LogiExpress</a>

      @if (auth.estaAutenticado()) {
        <nav class="menu" aria-label="Principal">
          <a routerLink="/paquetes" routerLinkActive="activo" [routerLinkActiveOptions]="{ exact: true }">Paquetes</a>
          @if (auth.esAdmin()) {
            <a routerLink="/paquetes/nuevo" routerLinkActive="activo">Registrar paquete</a>
          }
        </nav>
        <div class="sesion">
          <span class="usuario">{{ auth.usuario() }} <small>{{ auth.rolEtiqueta() }}</small></span>
          <button type="button" class="btn btn-secundario" (click)="auth.cerrarSesion()">Cerrar sesión</button>
        </div>
      }
    </header>

    <main class="contenido">
      <router-outlet />
    </main>
  `
})
export class AppComponent {
  protected readonly auth = inject(AuthService);
}
