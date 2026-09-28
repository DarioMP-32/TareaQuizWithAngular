package com.logiexpress.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logiexpress.domain.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCedula(String cedula);
}