import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PaqueteService } from '../services/paquete.service';
import { mensajeDeError } from '../domain/problem-detail';

@Component({
  selector: 'app-paquete-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <section class="panel panel-angosto">
      <h1>Registrar paquete</h1>
      <p class="ayuda">El peso máximo permitido es 30 kg. El cliente demo tiene el id 1.</p>

      <form [formGroup]="form" (ngSubmit)="guardar()" novalidate>
        <label for="clienteId">Id del cliente</label>
        <input id="clienteId" type="number" formControlName="clienteId" min="1" />
        @if (invalido('clienteId')) { <p class="campo-error">Indique un id de cliente válido (mayor que 0).</p> }

        <label for="codigoRastreo">Código de rastreo</label>
        <input id="codigoRastreo" type="text" formControlName="codigoRastreo" placeholder="LX-2026-0001" />
        @if (invalido('codigoRastreo')) { <p class="campo-error">El código de rastreo es obligatorio.</p> }

        <label for="descripcion">Descripción</label>
        <input id="descripcion" type="text" formControlName="descripcion" />
        @if (invalido('descripcion')) { <p class="campo-error">La descripción es obligatoria.</p> }

        <label for="pesoKg">Peso (kg)</label>
        <input id="pesoKg" type="number" step="0.1" formControlName="pesoKg" />
        @if (invalido('pesoKg')) { <p class="campo-error">El peso debe ser mayor que 0.</p> }

        @if (error()) {
          <p class="alerta" role="alert">{{ error() }}</p>
        }

        <div class="fila-botones">
          <button type="submit" class="btn btn-primario" [disabled]="enviando()">
            @if (enviando()) { Guardando... } @else { Registrar paquete }
          </button>
          <a class="btn btn-secundario" routerLink="/paquetes">Cancelar</a>
        </div>
      </form>
    </section>
  `
})
export class PaqueteFormComponent {
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly paqueteService = inject(PaqueteService);
  private readonly router = inject(Router);

  enviando = signal(false);
  error = signal<string | null>(null);

  form = this.fb.group({
    clienteId: [1, [Validators.required, Validators.min(1)]],
    codigoRastreo: ['', Validators.required],
    descripcion: ['', Validators.required],
    pesoKg: [0, [Validators.required, Validators.min(0.01)]]
  });

  invalido(campo: 'clienteId' | 'codigoRastreo' | 'descripcion' | 'pesoKg'): boolean {
    const c = this.form.controls[campo];
    return c.touched && c.invalid;
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set(null);

    this.paqueteService.registrarPaquete(this.form.getRawValue()).subscribe({
      next: () => this.router.navigate(['/paquetes']),
      error: err => {
        // Muestra el "detail" del RFC 7807 (p. ej. peso excedido, cliente no encontrado)
        this.error.set(mensajeDeError(err));
        this.enviando.set(false);
      }
    });
  }
}
