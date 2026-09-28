package com.logiexpress.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logiexpress.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
}
