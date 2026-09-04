package com.api.ecommerce.service;

import com.api.ecommerce.dto.ProductoDTO;
import com.api.ecommerce.exception.ResourceNotFoundException;
import com.api.ecommerce.model.Producto;
import com.api.ecommerce.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return convertirADTO(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        if (producto.getCategoria() != null) {
            dto.setNombreCategoria(producto.getCategoria().getNombre());
        }
        if (producto.getEtiquetas() != null) {
            dto.setEtiquetas(producto.getEtiquetas().stream()
                    .map(e -> e.getNombre())
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}