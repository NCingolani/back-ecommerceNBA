package com.api.ecommerce.controller;
 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.ecommerce.model.Wishlist;
import com.api.ecommerce.service.WishlistService;
 
@RestController
@RequestMapping("/api/wishlist/{usuarioId}")
public class WishlistController {
 
    private final WishlistService wishlistService;
 
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }
 
 
    @GetMapping
    public ResponseEntity<Wishlist> obtenerWishlist(
            @PathVariable Long usuarioId
    ) {
 
        return ResponseEntity.ok(
                wishlistService.obtenerWishlist(usuarioId)
        );
    }
 
 
    @PostMapping("/producto/{productoId}")
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
 
 
    @DeleteMapping("/producto/{productoId}")
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
