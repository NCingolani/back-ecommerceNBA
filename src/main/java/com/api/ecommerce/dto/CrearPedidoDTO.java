
package com.api.ecommerce.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearPedidoDTO {

    private List<ItemPedidoDTO> productos;
}
