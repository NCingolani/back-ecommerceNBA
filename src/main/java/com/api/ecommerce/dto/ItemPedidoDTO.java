
package com.api.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemPedidoDTO {

    private Long productoId;
    private Integer cantidad;
}
