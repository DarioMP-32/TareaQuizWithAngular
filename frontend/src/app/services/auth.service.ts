import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../config/api.config';
import { LoginRequest, LoginResponse } from '../domain/auth.model';

const CLAVE_TOKEN = 'jwt_token';
const CLAVE_ROL = 'jwt_rol';
const CLAVE_USUARIO = 'jwt_usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  // Estado reactivo con Signals
  private tokenSignal = signal<string | null>(null);
  private rolSignal = signal<string | null>(null);
  private usuarioSignal = signal<string | null>(null);

  // Estado derivado (solo lectura)
  readonly estaAutenticado = computed(() => this.tokenSignal() !== null);
  readonly esAdmin = computed(() => this.rolSignal() === 'ROLE_ADMIN');
  readonly usuario = computed(() => this.usuarioSignal());
  readonly rolEtiqueta = computed(() =>
    this.rolSignal() === 'ROLE_ADMIN' ? 'Administrador' : this.rolSignal() === 'ROLE_OPERADOR' ? 'Operador' : ''
  );

  constructor() {
    this.restaurarSesion();
  }

  login(credenciales: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${API_URL}/auth/login`, credenciales)
      .pipe(tap(resp => this.guardarSesion(resp.token, resp.rol, credenciales.username)));
  }

  obtenerToken(): string | null {
    return this.tokenSignal();
  }

  guardarSesion(token: string, rol: string, usuario: string): void {
    this.escribir(CLAVE_TOKEN, token);
    this.escribir(CLAVE_ROL, rol);
    this.escribir(CLAVE_USUARIO, usuario);
    this.tokenSignal.set(token);
    this.rolSignal.set(rol);
    this.usuarioSignal.set(usuario);
  }

  cerrarSesion(): void {
    [CLAVE_TOKEN, CLAVE_ROL, CLAVE_USUARIO].forEach(k => this.borrar(k));
    this.tokenSignal.set(null);
    this.rolSignal.set(null);
    this.usuarioSignal.set(null);
    this.router.navigate(['/login']);
  }

  /** Recupera la sesión guardada, descartándola si el JWT ya expiró. */
  private restaurarSesion(): void {
    const token = this.leer(CLAVE_TOKEN);
    if (!token || this.estaExpirado(token)) {
      [CLAVE_TOKEN, CLAVE_ROL, CLAVE_USUARIO].forEach(k => this.borrar(k));
      return;
    }
    this.tokenSignal.set(token);
    this.rolSignal.set(this.leer(CLAVE_ROL));
    this.usuarioSignal.set(this.leer(CLAVE_USUARIO));
  }

  private estaExpirado(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      return typeof payload.exp === 'number' && payload.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }

  private leer(clave: string): string | null {
    try { return localStorage.getItem(clave); } catch { return null; }
  }
  private escribir(clave: string, valor: string): void {
    try { localStorage.setItem(clave, valor); } catch { /* almacenamiento no disponible */ }
  }
  private borrar(clave: string): void {
    try { localStorage.removeItem(clave); } catch { /* almacenamiento no disponible */ }
  }
}
