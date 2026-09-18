package com.api.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.ecommerce.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Boolean existsByEmail(String email);
}

