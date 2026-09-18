package com.api.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.ecommerce.model.Carrito;
import com.api.ecommerce.service.CarritoService;

@RestController
@RequestMapping("/api/carrito/{usuarioId}")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }


    @GetMapping
    public ResponseEntity<Carrito> obtenerCarrito(
            @PathVariable Long usuarioId
    ) {

        return ResponseEntity.ok(
                carritoService.obtenerCarrito(usuarioId)
        );
    }


    @PostMapping("/producto/{productoId}")
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


    @DeleteMapping("/producto/{productoId}")
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