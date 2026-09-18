package com.api.ecommerce.service;
 
import com.api.ecommerce.exception.ResourceNotFoundException;
import com.api.ecommerce.model.Producto;
import com.api.ecommerce.model.Wishlist;
import com.api.ecommerce.repository.ProductoRepository;
import com.api.ecommerce.repository.WishlistRepository;
 
import org.springframework.stereotype.Service;
 
@Service
public class WishlistService {
 
    private final WishlistRepository wishlistRepository;
    private final ProductoRepository productoRepository;
 
    public WishlistService(
            WishlistRepository wishlistRepository,
            ProductoRepository productoRepository
    ) {
        this.wishlistRepository = wishlistRepository;
        this.productoRepository = productoRepository;
    }
 
    public Wishlist obtenerWishlist(Long usuarioId) {
 
        return wishlistRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(() -> {
 
                    Wishlist wishlist = new Wishlist();
 
                    wishlist.setUsuarioId(usuarioId);
 
                    return wishlistRepository.save(wishlist);
                });
    }
 
 
    public Wishlist agregarProducto(
            Long usuarioId,
            Long productoId
    ) {
 
        Wishlist wishlist = obtenerWishlist(usuarioId);
 
        Producto producto = productoRepository
                .findById(productoId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Producto no encontrado"
                        )
                );
 
        // Evitamos duplicados: si ya está, no lo agregamos de nuevo
        boolean yaExiste = wishlist.getProductos().stream()
                .anyMatch(p -> p.getId().equals(productoId));
 
        if (!yaExiste) {
            wishlist.getProductos().add(producto);
        }
 
        return wishlistRepository.save(wishlist);
    }
 
 
    public Wishlist eliminarProducto(
            Long usuarioId,
            Long productoId
    ) {
 
        Wishlist wishlist = obtenerWishlist(usuarioId);
 
        wishlist.getProductos().removeIf(
                producto ->
                        producto.getId()
                                .equals(productoId)
        );
 
        return wishlistRepository.save(wishlist);
    }
}
