package com.api.ecommerce.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private String nombreCategoria;
    private List<String> etiquetas;
}
