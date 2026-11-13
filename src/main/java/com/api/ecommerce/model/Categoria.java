package com.api.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // <- NUEVO IMPORT
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude // <- EL FIX CLAVE PARA FRENAR A LOMBOK
    private List<Producto> productos;

    public Categoria(String nombre) {
        this.nombre = nombre;
    }
}