package com.api.ecommerce.service;

import com.api.ecommerce.model.Carrito;
import com.api.ecommerce.model.ItemCarrito;
import com.api.ecommerce.model.Producto;
import com.api.ecommerce.repository.CarritoRepository;
import com.api.ecommerce.repository.ProductoRepository;

import org.springframework.stereotype.Service;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(
            CarritoRepository carritoRepository,
            ProductoRepository productoRepository
    ) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    public Carrito obtenerCarrito(Long usuarioId) {

        return carritoRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(() -> {

                    Carrito carrito = new Carrito();

                    carrito.setUsuarioId(usuarioId);

                    return carritoRepository.save(carrito);
                });
    }


    public Carrito agregarProducto(
            Long usuarioId,
            Long productoId,
            Integer cantidad
    ) {

        Carrito carrito = obtenerCarrito(usuarioId);

        Producto producto = productoRepository
                .findById(productoId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Producto no encontrado"
                        )
                );

        // Buscamos si el producto ya está en el carrito
        for (ItemCarrito item : carrito.getItems()) {

            if (item.getProducto().getId().equals(productoId)) {

                item.setCantidad(
                        item.getCantidad() + cantidad
                );

                return carritoRepository.save(carrito);
            }
        }

        // Si no estaba, creamos un item nuevo
        ItemCarrito nuevoItem = new ItemCarrito();

        nuevoItem.setProducto(producto);
        nuevoItem.setCantidad(cantidad);

        carrito.getItems().add(nuevoItem);

        return carritoRepository.save(carrito);
    }


    public Carrito eliminarProducto(
            Long usuarioId,
            Long productoId
    ) {

        Carrito carrito = obtenerCarrito(usuarioId);

        carrito.getItems().removeIf(
                item ->
                        item.getProducto()
                                .getId()
                                .equals(productoId)
        );

        return carritoRepository.save(carrito);
    }
}