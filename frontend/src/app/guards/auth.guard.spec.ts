import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';
import { adminGuard, authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('Guards', () => {
  let auth: AuthService;
  const ruta = {} as ActivatedRouteSnapshot;
  const estado = { url: '/paquetes' } as RouterStateSnapshot;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    });
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => localStorage.clear());

  it('authGuard redirige a /login si no hay sesión', () => {
    const resultado = TestBed.runInInjectionContext(() => authGuard(ruta, estado));
    expect(resultado instanceof UrlTree).toBeTrue();
    expect(TestBed.inject(Router).serializeUrl(resultado as UrlTree)).toContain('/login');
  });

  it('authGuard permite el paso con sesión', () => {
    auth.guardarSesion('t', 'ROLE_OPERADOR', 'operador');
    expect(TestBed.runInInjectionContext(() => authGuard(ruta, estado))).toBeTrue();
  });

  it('adminGuard bloquea a un operador', () => {
    auth.guardarSesion('t', 'ROLE_OPERADOR', 'operador');
    const resultado = TestBed.runInInjectionContext(() => adminGuard(ruta, estado));
    expect(resultado instanceof UrlTree).toBeTrue();
  });

  it('adminGuard permite a un administrador', () => {
    auth.guardarSesion('t', 'ROLE_ADMIN', 'admin');
    expect(TestBed.runInInjectionContext(() => adminGuard(ruta, estado))).toBeTrue();
  });
});
