package com.logiexpress.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.logiexpress.data.ClienteRepository;
import com.logiexpress.data.UsuarioRepository;
import com.logiexpress.domain.Cliente;
import com.logiexpress.domain.Rol;
import com.logiexpress.domain.Usuario;

/** Datos iniciales de demostración (solo se crean si no existen). Las contraseñas se guardan con BCrypt. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarDatosIniciales(UsuarioRepository usuarios, ClienteRepository clientes,
            PasswordEncoder encoder) {
        return args -> {
            if (usuarios.findByUsername("admin").isEmpty()) {
                usuarios.save(new Usuario("admin", encoder.encode("Admin123!"), Rol.ROLE_ADMIN));
            }
            if (usuarios.findByUsername("operador").isEmpty()) {
                usuarios.save(new Usuario("operador", encoder.encode("Operador123!"), Rol.ROLE_OPERADOR));
            }
            if (clientes.findByCedula("1-1111-1111").isEmpty()) {
                clientes.save(new Cliente("1-1111-1111", "Cliente Demo", "demo@logiexpress.com"));
            }
        };
    }
}
