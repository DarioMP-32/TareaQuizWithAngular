export type EstadoPaquete = 'REGISTRADO' | 'EN_TRANSITO' | 'ENTREGADO';

export const ESTADOS: EstadoPaquete[] = ['REGISTRADO', 'EN_TRANSITO', 'ENTREGADO'];

/** Coincide con PaqueteResponseDto del back-end. */
export interface Paquete {
  id?: number;
  codigoRastreo: string;
  descripcion: string;
  pesoKg: number;
  estado: EstadoPaquete;
  clienteId: number;
  clienteNombre?: string;
}

/** Coincide con PaqueteRequestDto del back-end. */
export interface PaqueteRequest {
  clienteId: number;
  codigoRastreo: string;
  descripcion: string;
  pesoKg: number;
}

/** Coincide con PaginaResponseDto del back-end. */
export interface Pagina<T> {
  contenido: T[];
  pagina: number;
  tamanio: number;
  totalElementos: number;
  totalPaginas: number;
}
