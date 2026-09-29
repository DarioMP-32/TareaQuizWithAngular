import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { mensajeDeError } from '../domain/problem-detail';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <section class="panel panel-angosto">
      <h1>Iniciar sesión</h1>
      <p class="ayuda">Ingrese con su usuario de LogiExpress para gestionar paquetes.</p>

      <form [formGroup]="form" (ngSubmit)="ingresar()" novalidate>
        <label for="username">Usuario</label>
        <input id="username" type="text" formControlName="username" autocomplete="username" />
        @if (form.controls.username.touched && form.controls.username.invalid) {
          <p class="campo-error">El usuario es obligatorio.</p>
        }

        <label for="password">Contraseña</label>
        <input id="password" type="password" formControlName="password" autocomplete="current-password" />
        @if (form.controls.password.touched && form.controls.password.invalid) {
          <p class="campo-error">La contraseña es obligatoria.</p>
        }

        @if (error()) {
          <p class="alerta" role="alert">{{ error() }}</p>
        }

        <button type="submit" class="btn btn-primario" [disabled]="enviando()">
          @if (enviando()) { Ingresando... } @else { Ingresar }
        </button>
      </form>
    </section>
  `
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  enviando = signal(false);
  error = signal<string | null>(null);

  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  ingresar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        const destino = this.route.snapshot.queryParamMap.get('returnUrl') ?? '/paquetes';
        this.router.navigateByUrl(destino);
      },
      error: err => {
        this.enviando.set(false);
        this.error.set(
          err?.status === 401 ? 'Usuario o contraseña incorrectos.' : mensajeDeError(err)
        );
      }
    });
  }
}
