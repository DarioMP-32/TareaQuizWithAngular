import { HttpErrorResponse } from '@angular/common/http';

/** Estructura RFC 7807 que devuelve el back-end. */
export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  timestamp?: string;
  errors?: Record<string, string>;
}

/** Convierte cualquier error HTTP en un mensaje legible para el usuario. */
export function mensajeDeError(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) {
      return 'No se pudo conectar con el servidor. Verifique que la API esté en ejecución.';
    }
    const problema = err.error as ProblemDetail | null;
    if (problema?.errors) {
      return Object.values(problema.errors).join(' ');
    }
    if (problema?.detail) {
      return problema.detail;
    }
  }
  return 'Ocurrió un error inesperado. Intente nuevamente.';
}
