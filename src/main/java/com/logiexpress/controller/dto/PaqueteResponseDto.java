package com.logiexpress.controller.dto;

import com.logiexpress.domain.EstadoPaquete;
import com.logiexpress.domain.Paquete;

public record PaqueteResponseDto(
        Long id,
        String codigoRastreo,
        String descripcion,
        Double pesoKg,
        EstadoPaquete estado,
        Long clienteId,
        String clienteNombre
) {

    /** Convierte la entidad JPA en DTO para no exponer el modelo de persistencia por HTTP. */
    public static PaqueteResponseDto desde(Paquete p) {
        return new PaqueteResponseDto(
                p.getId(),
                p.getCodigoRastreo(),
                p.getDescripcion(),
                p.getPesoKg(),
                p.getEstado(),
                p.getCliente().getId(),
                p.getCliente().getNombre());
    }
}
