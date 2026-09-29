import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => localStorage.clear());

  it('inicia sin sesión', () => {
    expect(service.estaAutenticado()).toBeFalse();
    expect(service.esAdmin()).toBeFalse();
  });

  it('login guarda el token y actualiza los signals', () => {
    service.login({ username: 'admin', password: 'Admin123!' }).subscribe();
    const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush({ token: 'abc', tipo: 'Bearer', expiraEnMs: 3600000, rol: 'ROLE_ADMIN' });

    expect(service.estaAutenticado()).toBeTrue();
    expect(service.esAdmin()).toBeTrue();
    expect(service.obtenerToken()).toBe('abc');
    expect(localStorage.getItem('jwt_token')).toBe('abc');
  });

  it('un operador no es administrador', () => {
    service.guardarSesion('t', 'ROLE_OPERADOR', 'operador');
    expect(service.estaAutenticado()).toBeTrue();
    expect(service.esAdmin()).toBeFalse();
  });

  it('cerrarSesion limpia el estado', () => {
    service.guardarSesion('t', 'ROLE_ADMIN', 'admin');
    service.cerrarSesion();
    expect(service.estaAutenticado()).toBeFalse();
    expect(localStorage.getItem('jwt_token')).toBeNull();
  });
});
