package com.logiexpress.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logiexpress.domain.EstadoPaquete;
import com.logiexpress.domain.Paquete;

public interface PaqueteRepository extends JpaRepository<Paquete, Long> {

    @Query("SELECT p FROM Paquete p WHERE p.estado = :estado")
    Page<Paquete> findByEstado(@Param("estado") EstadoPaquete estado, Pageable pageable);
}
