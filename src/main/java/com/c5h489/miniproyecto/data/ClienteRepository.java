package com.c5h489.miniproyecto.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c5h489.miniproyecto.domain.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCedula(String cedula);
}