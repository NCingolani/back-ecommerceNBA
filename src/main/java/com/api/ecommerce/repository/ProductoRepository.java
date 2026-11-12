
package com.api.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.api.ecommerce.model.Producto;

public interface ProductoRepository
        extends JpaRepository<Producto, Long> {

    // =====================================
    // BUSCAR PRODUCTOS POR NOMBRE
    // =====================================

    // Busca productos que contengan el texto ingresado,
    // sin distinguir mayúsculas y minúsculas.

    List<Producto> findByNombreContainingIgnoreCase(
            String nombre
    );

    // =====================================
    // BUSCAR PRODUCTO PARA UNA COMPRA
    // =====================================

    // Bloquea el registro mientras se procesa la compra
    // para evitar descuentos simultáneos incorrectos.

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> buscarPorIdParaCompra(
            @Param("id") Long id
    );

}
