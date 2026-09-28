package com.logiexpress.controller.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/** Envoltorio estable para respuestas paginadas (evita serializar PageImpl directamente). */
public record PaginaResponseDto<T>(
        List<T> contenido,
        int pagina,
        int tamanio,
        long totalElementos,
        int totalPaginas
) {

    public static <E, T> PaginaResponseDto<T> desde(Page<E> page, Function<E, T> mapper) {
        return new PaginaResponseDto<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
