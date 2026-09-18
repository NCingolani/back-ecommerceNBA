package com.api.ecommerce.controller;
 
import com.api.ecommerce.model.Wishlist;
import com.api.ecommerce.service.WishlistService;
 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
 
    private final WishlistService wishlistService;
 
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }
 
 
    @GetMapping("/{usuarioId}")
    public ResponseEntity<Wishlist> obtenerWishlist(
            @PathVariable Long usuarioId
    ) {
 
        return ResponseEntity.ok(
                wishlistService.obtenerWishlist(usuarioId)
        );
    }
 
 
    @PostMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<Wishlist> agregarProducto(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId
    ) {
 
        return ResponseEntity.ok(
                wishlistService.agregarProducto(
                        usuarioId,
                        productoId
                )
        );
    }
 
 
    @DeleteMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<Wishlist> eliminarProducto(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId
    ) {
 
        return ResponseEntity.ok(
                wishlistService.eliminarProducto(
                        usuarioId,
                        productoId
                )
        );
    }
}
