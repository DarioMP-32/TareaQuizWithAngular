package com.logiexpress.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para registrar un paquete")
public record PaqueteRequestDto(

        @Schema(example = "1", description = "Id del cliente dueño del paquete")
        @NotNull(message = "El clienteId es obligatorio")
        @Positive(message = "El clienteId debe ser positivo")
        Long clienteId,

        @Schema(example = "LX-2026-0001")
        @NotBlank(message = "El código de rastreo es obligatorio")
        @Size(max = 50, message = "El código de rastreo no puede superar 50 caracteres")
        String codigoRastreo,

        @Schema(example = "Caja con documentos")
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion,

        @Schema(example = "12.5", description = "Peso en kg (máximo permitido: 30.0)")
        @NotNull(message = "El peso es obligatorio")
        @Positive(message = "El peso debe ser mayor que cero")
        Double pesoKg
) {
}
