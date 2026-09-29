export interface LoginRequest {
  username: string;
  password: string;
}

/** Coincide con LoginResponseDto del back-end. */
export interface LoginResponse {
  token: string;
  tipo: string;
  expiraEnMs: number;
  rol: 'ROLE_ADMIN' | 'ROLE_OPERADOR';
}
