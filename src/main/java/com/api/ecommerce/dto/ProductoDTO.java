package com.api.ecommerce.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private String nombreCategoria;
    private List<String> etiquetas;
    private String talle;
private Integer stock;
}
