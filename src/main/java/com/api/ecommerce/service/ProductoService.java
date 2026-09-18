package com.api.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.ecommerce.dto.ProductoDTO;
import com.api.ecommerce.exception.ResourceNotFoundException;
import com.api.ecommerce.model.Categoria;
import com.api.ecommerce.model.Etiqueta;
import com.api.ecommerce.model.Producto;
import com.api.ecommerce.repository.CategoriaRepository;
import com.api.ecommerce.repository.EtiquetaRepository;
import com.api.ecommerce.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EtiquetaRepository etiquetaRepository;

    // Se inyectan los repositorios adicionales para manejar las relaciones
    public ProductoService(ProductoRepository productoRepository, 
                           CategoriaRepository categoriaRepository, 
                           EtiquetaRepository etiquetaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.etiquetaRepository = etiquetaRepository;
    }

    @Transactional
    public ProductoDTO crearProducto(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());

        // 1. Buscar o crear la categoría
        if (dto.getNombreCategoria() != null) {
            Categoria categoria = categoriaRepository.findAll().stream()
                    .filter(c -> c.getNombre().equalsIgnoreCase(dto.getNombreCategoria()))
                    .findFirst()
                    .orElseGet(() -> categoriaRepository.save(new Categoria(dto.getNombreCategoria())));
            producto.setCategoria(categoria);
        }

        // 2. Buscar o crear las etiquetas
        if (dto.getEtiquetas() != null) {
            List<Etiqueta> etiquetas = dto.getEtiquetas().stream()
                    .map(nombre -> etiquetaRepository.findAll().stream()
                            .filter(e -> e.getNombre().equalsIgnoreCase(nombre))
                            .findFirst()
                            .orElseGet(() -> etiquetaRepository.save(new Etiqueta(nombre))))
                    .collect(Collectors.toList());
            producto.setEtiquetas(etiquetas);
        }

        // 3. Guardar el producto en la BD
        Producto guardado = productoRepository.save(producto);
        
        // 4. Devolverlo como DTO
        return convertirADTO(guardado);
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
                    .map(Etiqueta::getNombre)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}