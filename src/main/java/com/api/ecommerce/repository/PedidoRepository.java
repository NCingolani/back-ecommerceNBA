
package com.api.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.ecommerce.model.Pedido;

public interface PedidoRepository
        extends JpaRepository<Pedido, Long> {

    // Consultar compras de un usuario,
    // ordenadas desde la más reciente.

    List<Pedido> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
