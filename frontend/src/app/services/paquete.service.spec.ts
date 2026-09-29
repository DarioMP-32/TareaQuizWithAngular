import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PaqueteService } from './paquete.service';

describe('PaqueteService', () => {
  let service: PaqueteService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PaqueteService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(PaqueteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('debe solicitar la lista de paquetes via GET', () => {
    service.obtenerPaquetes().subscribe();
    const req = httpMock.expectOne(r => r.url === 'http://localhost:8080/api/v1/paquetes');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('page')).toBe('0');
    req.flush({ contenido: [], pagina: 0, tamanio: 10, totalElementos: 0, totalPaginas: 0 });
  });

  it('debe enviar el filtro de estado como parámetro', () => {
    service.obtenerPaquetes('ENTREGADO', 1, 5).subscribe();
    const req = httpMock.expectOne(r => r.url === 'http://localhost:8080/api/v1/paquetes');
    expect(req.request.params.get('estado')).toBe('ENTREGADO');
    expect(req.request.params.get('page')).toBe('1');
    expect(req.request.params.get('size')).toBe('5');
    req.flush({ contenido: [], pagina: 1, tamanio: 5, totalElementos: 0, totalPaginas: 0 });
  });

  it('debe registrar un paquete via POST', () => {
    const nuevo = { clienteId: 1, codigoRastreo: 'LX-1', descripcion: 'Caja', pesoKg: 5 };
    service.registrarPaquete(nuevo).subscribe();
    const req = httpMock.expectOne('http://localhost:8080/api/v1/paquetes');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(nuevo);
    req.flush({ ...nuevo, id: 1, estado: 'REGISTRADO' });
  });

  it('debe eliminar un paquete via DELETE', () => {
    service.eliminarPaquete(7).subscribe();
    const req = httpMock.expectOne('http://localhost:8080/api/v1/paquetes/7');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
