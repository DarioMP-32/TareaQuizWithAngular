package com.logiexpress.exception;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Manejador global de errores. Todas las respuestas de error siguen RFC 7807 (Problem Details):
 * type, title, status, detail, instance y timestamp. Nunca se expone el stacktrace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    static final String TYPE_BASE = "https://logiexpress.com/errors/";

    @ExceptionHandler(PesoExcedidoException.class)
    public ResponseEntity<ProblemDetail> handlePesoExcedido(PesoExcedidoException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Peso excedido", "peso-excedido", ex.getMessage(), req);
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleClienteNoEncontrado(ClienteNoEncontradoException ex,
            HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Cliente no encontrado", "cliente-no-encontrado", ex.getMessage(), req);
    }

    @ExceptionHandler(PaqueteNoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handlePaqueteNoEncontrado(PaqueteNoEncontradoException ex,
            HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Paquete no encontrado", "paquete-no-encontrado", ex.getMessage(), req);
    }

    @ExceptionHandler(CodigoRastreoDuplicadoException.class)
    public ResponseEntity<ProblemDetail> handleDuplicado(CodigoRastreoDuplicadoException ex,
            HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Código de rastreo duplicado", "codigo-duplicado", ex.getMessage(), req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAutenticacion(AuthenticationException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "No autenticado", "no-autenticado",
                "Credenciales inválidas o token ausente.", req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccesoDenegado(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Acceso denegado", "acceso-denegado",
                "Su rol no tiene permiso para realizar esta operación.", req);
    }

    /** Red de seguridad: cualquier error inesperado responde 500 genérico, sin detalles internos. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleInesperado(Exception ex, HttpServletRequest req) {
        log.error("Error inesperado en {}", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", "error-interno",
                "Ocurrió un error inesperado. Intente nuevamente más tarde.", req);
    }

    /** Errores de @Valid: 400 con el detalle de cada campo inválido. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errores.putIfAbsent(fe.getField(), fe.getDefaultMessage()));

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "La solicitud contiene campos inválidos.");
        pd.setTitle("Validación fallida");
        pd.setProperty("errors", errores);
        return handleExceptionInternal(ex, pd, headers, HttpStatus.BAD_REQUEST, request);
    }

    /** Completa con type, instance y timestamp los ProblemDetail que genera Spring MVC (405, 415, 400...). */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {
        ResponseEntity<Object> respuesta = super.handleExceptionInternal(ex, body, headers, statusCode, request);
        if (respuesta != null && respuesta.getBody() instanceof ProblemDetail pd) {
            if (pd.getType() == null || "about:blank".equals(pd.getType().toString())) {
                pd.setType(URI.create(TYPE_BASE + "solicitud-invalida"));
            }
            if (pd.getInstance() == null && request instanceof ServletWebRequest swr) {
                pd.setInstance(URI.create(swr.getRequest().getRequestURI()));
            }
            if (pd.getProperties() == null || !pd.getProperties().containsKey("timestamp")) {
                pd.setProperty("timestamp", Instant.now().toString());
            }
        }
        return respuesta;
    }

    private ResponseEntity<ProblemDetail> build(HttpStatus status, String title, String slug, String detail,
            HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setType(URI.create(TYPE_BASE + slug));
        pd.setTitle(title);
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(pd);
    }
}
