import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/** Adjunta el JWT a cada petición y cierra la sesión si el servidor responde 401. */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.obtenerToken();

  const peticion = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((error: unknown) => {
      const esLogin = req.url.includes('/auth/login');
      if (error instanceof HttpErrorResponse && error.status === 401 && !esLogin) {
        authService.cerrarSesion();   // token vencido o inválido
      }
      return throwError(() => error);
    })
  );
};
