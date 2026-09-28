package com.logiexpress.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)                       // API stateless con JWT
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/error").permitAll()
                // RBAC: consultar -> ADMIN y OPERADOR; registrar/eliminar -> solo ADMIN
                .requestMatchers(HttpMethod.GET, "/api/v1/paquetes/**").hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers(HttpMethod.POST, "/api/v1/paquetes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/paquetes/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> ProblemJsonWriter.escribir(req, res,
                        HttpStatus.UNAUTHORIZED, "No autenticado", "no-autenticado",
                        "Debe enviar un token JWT válido en el encabezado Authorization: Bearer <token>."))
                .accessDeniedHandler((req, res, e) -> ProblemJsonWriter.escribir(req, res,
                        HttpStatus.FORBIDDEN, "Acceso denegado", "acceso-denegado",
                        "Su rol no tiene permiso para realizar esta operación.")))
            .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
