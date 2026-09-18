package com.api.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.ecommerce.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
