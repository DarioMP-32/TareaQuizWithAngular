package com.c5h489.miniproyecto.business;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.c5h489.miniproyecto.data.ClienteRepository;
import com.c5h489.miniproyecto.data.PaqueteRepository;
import com.c5h489.miniproyecto.domain.Cliente;
import com.c5h489.miniproyecto.domain.EstadoPaquete;
import com.c5h489.miniproyecto.domain.Paquete;
import com.c5h489.miniproyecto.exception.ClienteNoEncontradoException;
import com.c5h489.miniproyecto.exception.PaqueteNoEncontradoException;
import com.c5h489.miniproyecto.exception.PesoExcedidoException;

@Service
public class PaqueteService {

    private static final double PESO_MAXIMO_KG = 30.0;

    private final PaqueteRepository paqueteRepository;
    private final ClienteRepository clienteRepository;

    public PaqueteService(PaqueteRepository paqueteRepository, ClienteRepository clienteRepository) {
        this.paqueteRepository = paqueteRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Paquete registrarPaquete(Long clienteId, String codigoRastreo, String descripcion, Double pesoKg) {
        if (pesoKg != null && pesoKg > PESO_MAXIMO_KG) {
            throw new PesoExcedidoException(
                    "El peso del paquete (" + pesoKg + " kg) supera el máximo permitido de " + PESO_MAXIMO_KG + " kg.");
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(
                        "No existe un cliente con id " + clienteId));

        Paquete paquete = new Paquete(codigoRastreo, descripcion, pesoKg, EstadoPaquete.REGISTRADO, cliente);
        return paqueteRepository.save(paquete);
    }

    @Transactional
    public Paquete actualizarEstado(Long paqueteId, EstadoPaquete nuevoEstado) {
        Paquete paquete = paqueteRepository.findById(paqueteId)
                .orElseThrow(() -> new PaqueteNoEncontradoException(
                        "No existe un paquete con id " + paqueteId));
        paquete.setEstado(nuevoEstado);
        return paqueteRepository.save(paquete);
    }

    @Transactional
    public void eliminarPaquete(Long paqueteId) {
        if (!paqueteRepository.existsById(paqueteId)) {
            throw new PaqueteNoEncontradoException("No existe un paquete con id " + paqueteId);
        }
        paqueteRepository.deleteById(paqueteId);
    }

    @Transactional(readOnly = true)
    public Paquete buscarPorId(Long paqueteId) {
        return paqueteRepository.findById(paqueteId)
                .orElseThrow(() -> new PaqueteNoEncontradoException(
                        "No existe un paquete con id " + paqueteId));
    }

    @Transactional(readOnly = true)
    public Page<Paquete> buscarPorEstado(EstadoPaquete estado, Pageable pageable) {
        return paqueteRepository.findByEstado(estado, pageable);
    }
}
