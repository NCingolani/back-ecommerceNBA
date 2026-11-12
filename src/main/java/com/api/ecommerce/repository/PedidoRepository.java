
package com.api.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.ecommerce.model.Pedido;

@Repository
public interface PedidoRepository
        extends JpaRepository<Pedido, Long> {

    // Consultar compras de un usuario,
    // ordenadas desde la más reciente.

    List<Pedido> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
