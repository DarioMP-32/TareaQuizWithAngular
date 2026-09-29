import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { EstadoPaquete, Paquete, PaqueteRequest, Pagina } from '../domain/paquete.model';

@Injectable({ providedIn: 'root' })
export class PaqueteService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_URL}/paquetes`;

  obtenerPaquetes(estado?: EstadoPaquete | null, page = 0, size = 10): Observable<Pagina<Paquete>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (estado) {
      params = params.set('estado', estado);
    }
    return this.http.get<Pagina<Paquete>>(this.apiUrl, { params });
  }

  registrarPaquete(paquete: PaqueteRequest): Observable<Paquete> {
    return this.http.post<Paquete>(this.apiUrl, paquete);
  }

  eliminarPaquete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
