package com.logiexpress.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.logiexpress.business.PaqueteService;
import com.logiexpress.controller.dto.PaginaResponseDto;
import com.logiexpress.controller.dto.PaqueteRequestDto;
import com.logiexpress.controller.dto.PaqueteResponseDto;
import com.logiexpress.domain.EstadoPaquete;
import com.logiexpress.domain.Paquete;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/paquetes")
@Tag(name = "Paquetes", description = "Gestión de paquetes de LogiExpress")
@SecurityRequirement(name = "bearerAuth")
public class PaqueteController {

    private final PaqueteService paqueteService;

    public PaqueteController(PaqueteService paqueteService) {
        this.paqueteService = paqueteService;
    }

    @Operation(summary = "Registrar un paquete", description = "Solo ROLE_ADMIN. Rechaza pesos mayores a 30 kg.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Paquete registrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o peso excedido (RFC 7807)"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "El rol no tiene permiso"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "409", description = "Código de rastreo duplicado")
    })
    @PostMapping
    public ResponseEntity<PaqueteResponseDto> registrar(@Valid @RequestBody PaqueteRequestDto request) {
        Paquete creado = paqueteService.registrarPaquete(
                request.clienteId(), request.codigoRastreo(), request.descripcion(), request.pesoKg());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(PaqueteResponseDto.desde(creado));
    }

    @Operation(summary = "Listar paquetes (paginado)",
               description = "ROLE_ADMIN y ROLE_OPERADOR. Filtro opcional por estado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado paginado"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "El rol no tiene permiso")
    })
    @GetMapping
    public PaginaResponseDto<PaqueteResponseDto> listar(
            @RequestParam(required = false) EstadoPaquete estado,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<Paquete> resultado = (estado == null)
                ? paqueteService.listarTodos(pageable)
                : paqueteService.buscarPorEstado(estado, pageable);
        return PaginaResponseDto.desde(resultado, PaqueteResponseDto::desde);
    }

    @Operation(summary = "Consultar un paquete por id", description = "ROLE_ADMIN y ROLE_OPERADOR.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paquete encontrado"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "El rol no tiene permiso"),
        @ApiResponse(responseCode = "404", description = "Paquete no encontrado")
    })
    @GetMapping("/{id}")
    public PaqueteResponseDto buscarPorId(@PathVariable Long id) {
        return PaqueteResponseDto.desde(paqueteService.buscarPorId(id));
    }

    @Operation(summary = "Eliminar un paquete", description = "Solo ROLE_ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Paquete eliminado"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "El rol no tiene permiso"),
        @ApiResponse(responseCode = "404", description = "Paquete no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        paqueteService.eliminarPaquete(id);
        return ResponseEntity.noContent().build();
    }
}
