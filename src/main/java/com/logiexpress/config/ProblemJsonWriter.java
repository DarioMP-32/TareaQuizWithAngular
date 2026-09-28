package com.logiexpress.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Los errores 401/403 ocurren en los filtros de seguridad, antes de llegar al @RestControllerAdvice.
 * Este utilitario escribe la misma estructura RFC 7807 para mantener consistencia.
 */
final class ProblemJsonWriter {

    private ProblemJsonWriter() {
    }

    static void escribir(HttpServletRequest request, HttpServletResponse response,
            HttpStatus status, String title, String slug, String detail) throws IOException {
        String json = "{"
                + "\"type\":\"https://logiexpress.com/errors/" + slug + "\","
                + "\"title\":\"" + esc(title) + "\","
                + "\"status\":" + status.value() + ","
                + "\"detail\":\"" + esc(detail) + "\","
                + "\"instance\":\"" + esc(request.getRequestURI()) + "\","
                + "\"timestamp\":\"" + Instant.now() + "\""
                + "}";
        response.setStatus(status.value());
        response.setContentType("application/problem+json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(json);
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
