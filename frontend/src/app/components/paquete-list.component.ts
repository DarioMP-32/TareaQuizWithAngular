import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PaqueteService } from '../services/paquete.service';
import { AuthService } from '../services/auth.service';
import { ESTADOS, EstadoPaquete, Paquete } from '../domain/paquete.model';
import { mensajeDeError } from '../domain/problem-detail';

@Component({
  selector: 'app-paquete-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './paquete-list.component.html',
  styleUrl: './paquete-list.component.css'
})
export class PaqueteListComponent implements OnInit {
  private readonly paqueteService = inject(PaqueteService);
  protected readonly auth = inject(AuthService);

  readonly estados = ESTADOS;

  paquetes = signal<Paquete[]>([]);
  cargando = signal<boolean>(true);
  error = signal<string | null>(null);
  filtroEstado = signal<EstadoPaquete | null>(null);
  pagina = signal<number>(0);
  totalPaginas = signal<number>(0);
  totalElementos = signal<number>(0);

  // Estado derivado
  hayAnterior = computed(() => this.pagina() > 0);
  haySiguiente = computed(() => this.pagina() + 1 < this.totalPaginas());

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.paqueteService.obtenerPaquetes(this.filtroEstado(), this.pagina()).subscribe({
      next: data => {
        this.paquetes.set(data.contenido);
        this.totalPaginas.set(data.totalPaginas);
        this.totalElementos.set(data.totalElementos);
        this.cargando.set(false);
      },
      error: err => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      }
    });
  }

  cambiarEstado(valor: string): void {
    this.filtroEstado.set(valor === '' ? null : (valor as EstadoPaquete));
    this.pagina.set(0);
    this.cargar();
  }

  irAPagina(delta: number): void {
    this.pagina.update(p => p + delta);
    this.cargar();
  }

  eliminar(paquete: Paquete): void {
    if (paquete.id === undefined) {
      return;
    }
    if (!confirm(`¿Eliminar el paquete ${paquete.codigoRastreo}? Esta acción no se puede deshacer.`)) {
      return;
    }
    this.paqueteService.eliminarPaquete(paquete.id).subscribe({
      next: () => {
        // Si se vació la última página, retroceder una
        if (this.paquetes().length === 1 && this.pagina() > 0) {
          this.pagina.update(p => p - 1);
        }
        this.cargar();
      },
      error: err => this.error.set(mensajeDeError(err))
    });
  }
}
