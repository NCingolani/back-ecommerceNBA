package com.api.ecommerce.controller;

import com.api.ecommerce.model.Carrito;
import com.api.ecommerce.service.CarritoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }


    @GetMapping("/{usuarioId}")
    public ResponseEntity<Carrito> obtenerCarrito(
            @PathVariable Long usuarioId
    ) {

        return ResponseEntity.ok(
                carritoService.obtenerCarrito(usuarioId)
        );
    }


    @PostMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<Carrito> agregarProducto(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId,
            @RequestParam Integer cantidad
    ) {

        return ResponseEntity.ok(
                carritoService.agregarProducto(
                        usuarioId,
                        productoId,
                        cantidad
                )
        );
    }


    @DeleteMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<Carrito> eliminarProducto(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId
    ) {

        return ResponseEntity.ok(
                carritoService.eliminarProducto(
                        usuarioId,
                        productoId
                )
        );
    }
}