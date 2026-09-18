package com.api.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.ecommerce.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}