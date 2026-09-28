package com.logiexpress.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logiexpress.data.ClienteRepository;
import com.logiexpress.data.PaqueteRepository;
import com.logiexpress.domain.Cliente;
import com.logiexpress.domain.EstadoPaquete;
import com.logiexpress.domain.Paquete;
import com.logiexpress.exception.ClienteNoEncontradoException;
import com.logiexpress.exception.PesoExcedidoException;

@ExtendWith(MockitoExtension.class)
class PaqueteServiceTest {

    @Mock
    private PaqueteRepository paqueteRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private PaqueteService paqueteService;

    @Test
    @DisplayName("Registra el paquete cuando el peso es válido")
    void registrarPaquete_pesoValido_guardaPaquete() {
        Cliente cliente = new Cliente("1-1111-1111", "Ana Mora", "ana@correo.com");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(paqueteRepository.save(any(Paquete.class))).thenAnswer(inv -> inv.getArgument(0));

        Paquete resultado = paqueteService.registrarPaquete(1L, "LX-001", "Caja pequeña", 12.5);

        assertNotNull(resultado);
        assertEquals(EstadoPaquete.REGISTRADO, resultado.getEstado());
        assertEquals("LX-001", resultado.getCodigoRastreo());
        verify(paqueteRepository).save(any(Paquete.class));
    }

    @Test
    @DisplayName("Lanza PesoExcedidoException cuando el peso supera 30 kg")
    void registrarPaquete_pesoMayorA30_lanzaPesoExcedidoException() {
        assertThrows(PesoExcedidoException.class,
                () -> paqueteService.registrarPaquete(1L, "LX-002", "Caja pesada", 30.1));

        verify(paqueteRepository, never()).save(any(Paquete.class));
    }

    @Test
    @DisplayName("Un peso de exactamente 30 kg es válido")
    void registrarPaquete_pesoExacto30_esValido() {
        Cliente cliente = new Cliente("1-1111-1111", "Ana Mora", "ana@correo.com");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(paqueteRepository.save(any(Paquete.class))).thenAnswer(inv -> inv.getArgument(0));

        Paquete resultado = paqueteService.registrarPaquete(1L, "LX-003", "Caja límite", 30.0);

        assertNotNull(resultado);
        verify(paqueteRepository).save(any(Paquete.class));
    }

    @Test
    @DisplayName("Lanza ClienteNoEncontradoException cuando el cliente no existe")
    void registrarPaquete_clienteInexistente_lanzaClienteNoEncontradoException() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class,
                () -> paqueteService.registrarPaquete(99L, "LX-004", "Sobre", 1.0));

        verify(paqueteRepository, never()).save(any(Paquete.class));
    }
}
