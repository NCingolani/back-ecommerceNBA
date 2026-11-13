package com.api.ecommerce.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.api.ecommerce.security.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    // =====================================
    // CONFIGURACIÓN DE SPRING SECURITY
    // =====================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // CONFIGURACIÓN CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // API REST CON JWT
                .csrf(csrf -> csrf.disable())

                // PERMISOS DE ACCESO
                .authorizeHttpRequests(auth -> auth

                        // =====================================
                        // AUTENTICACIÓN PÚBLICA
                        // =====================================

                        .requestMatchers(
                                "/api/auth/**")
                        .permitAll()

                        // =====================================
                        // CONSULTAR PRODUCTOS
                        // =====================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/productos",
                                "/api/productos/**")
                        .permitAll()

                        // =====================================
                        // VISUALIZAR IMÁGENES
                        // =====================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/uploads/**")
                        .permitAll()

                        // =====================================
                        // ADMINISTRACIÓN
                        // =====================================

                        .requestMatchers(
                                "/api/admin/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        // CREAR PRODUCTOS
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/productos",
                                "/api/productos/")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        // SUBIR IMÁGENES (CON Y SIN BARRA FINAL)
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/productos/imagen",
                                "/api/productos/imagen/")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        // MODIFICAR PRODUCTOS
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/productos/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        // ELIMINAR PRODUCTOS
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/productos/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        // =====================================
                        // COMPRAS DE LOS USUARIOS
                        // =====================================

                        // USER Y ADMIN AUTENTICADOS
                        // PUEDEN REGISTRAR UNA COMPRA
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/pedidos",
                                "/api/pedidos/")
                        .authenticated()

                        // =====================================
                        // HISTORIAL DE COMPRAS DEL USUARIO
                        // =====================================

                        // CADA USUARIO PUEDE CONSULTAR
                        // ÚNICAMENTE SUS PROPIOS PEDIDOS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/pedidos/mis-pedidos",
                                "/api/pedidos/mis-pedidos/")
                        .authenticated()

                        // =====================================
                        // HISTORIAL GENERAL: SOLO ADMIN
                        // =====================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/pedidos",
                                "/api/pedidos/")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        // =====================================
                        // OTRAS PETICIONES
                        // =====================================

                        .anyRequest().authenticated())

                // SESIONES SIN ESTADO
                .sessionManagement(sess -> sess.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))

                // AUTENTICACIÓN
                .authenticationProvider(authenticationProvider)

                // VALIDACIÓN DEL JWT
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // =====================================
    // CONFIGURACIÓN CORS
    // =====================================

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // FRONTENDS PERMITIDOS
        configuration.setAllowedOrigins(
                Arrays.asList(
                        "http://localhost:5173",
                        "http://localhost:3000",
                        "http://localhost:4200"));

        // MÉTODOS HTTP
        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"));

        // CABECERAS
        configuration.setAllowedHeaders(
                Arrays.asList(
                        "Authorization",
                        "Content-Type"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration);

        return source;
    }
}
