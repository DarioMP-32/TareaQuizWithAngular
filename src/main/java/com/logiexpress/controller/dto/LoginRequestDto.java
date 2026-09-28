package com.logiexpress.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @Schema(example = "admin") @NotBlank(message = "El usuario es obligatorio") String username,
        @Schema(example = "Admin123!") @NotBlank(message = "La contraseña es obligatoria") String password
) {
}
