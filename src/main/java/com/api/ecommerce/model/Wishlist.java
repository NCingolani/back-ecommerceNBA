package com.api.ecommerce.model;
 
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
import java.util.ArrayList;
import java.util.List;
 
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Wishlist {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    private Long usuarioId;
 
    @ManyToMany
    @JoinTable(
            name = "wishlist_producto",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos = new ArrayList<>();
 
}
