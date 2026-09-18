package com.api.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.ecommerce.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
