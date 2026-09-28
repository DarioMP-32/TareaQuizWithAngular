package com.logiexpress.data;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logiexpress.domain.EstadoPaquete;
import com.logiexpress.domain.Paquete;

public interface PaqueteRepository extends JpaRepository<Paquete, Long> {

    /** Consulta JPQL paginada por estado (JOIN FETCH evita LazyInitializationException al mapear a DTO). */
    @Query(value = "SELECT p FROM Paquete p JOIN FETCH p.cliente WHERE p.estado = :estado",
           countQuery = "SELECT COUNT(p) FROM Paquete p WHERE p.estado = :estado")
    Page<Paquete> findByEstado(@Param("estado") EstadoPaquete estado, Pageable pageable);

    @Query(value = "SELECT p FROM Paquete p JOIN FETCH p.cliente",
           countQuery = "SELECT COUNT(p) FROM Paquete p")
    Page<Paquete> findAllConCliente(Pageable pageable);

    @Query("SELECT p FROM Paquete p JOIN FETCH p.cliente WHERE p.id = :id")
    Optional<Paquete> findByIdConCliente(@Param("id") Long id);

    boolean existsByCodigoRastreo(String codigoRastreo);
}
