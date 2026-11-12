
package com.api.ecommerce.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResumenDTO(
        Long id,
        LocalDateTime fecha,
        String estado,
        Double total,
        List<DetalleDTO> detalles
) {

    public record DetalleDTO(
            Long productoId,
            String nombre,
            Integer cantidad,
            Double precioUnitario,
            Double subtotal
    ) {
    }
}
