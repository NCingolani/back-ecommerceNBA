
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

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            EtiquetaRepository etiquetaRepository) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.etiquetaRepository = etiquetaRepository;
    }

    // CREAR PRODUCTO
    @Transactional
    public ProductoDTO crearProducto(ProductoDTO dto) {

        Producto producto = new Producto();

        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagen(dto.getImagen());
        producto.setTalle(dto.getTalle());
        producto.setStock(dto.getStock());

        // BUSCAR O CREAR CATEGORÍA
        if (dto.getNombreCategoria() != null) {
            producto.setCategoria(
                    buscarOCrearCategoria(dto.getNombreCategoria())
            );
        }

        // BUSCAR O CREAR ETIQUETAS
        if (dto.getEtiquetas() != null) {
            producto.setEtiquetas(
                    buscarOCrearEtiquetas(dto.getEtiquetas())
            );
        }

        // GUARDAR PRODUCTO
        Producto guardado = productoRepository.save(producto);

        return convertirADTO(guardado);
    }

    // LISTAR TODOS LOS PRODUCTOS
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {

        return productoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // BUSCAR PRODUCTO POR ID
    @Transactional(readOnly = true)
    public ProductoDTO buscarPorId(Long id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Producto no encontrado con ID: " + id
                        )
                );

        return convertirADTO(producto);
    }

    // BUSCAR PRODUCTOS POR NOMBRE
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String nombre) {

        return productoRepository
                .findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // MODIFICAR PRODUCTO EXISTENTE
    @Transactional
    public ProductoDTO actualizarProducto(
            Long id,
            ProductoDTO dto) {

        // BUSCAR EL PRODUCTO EXISTENTE
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Producto no encontrado con ID: " + id
                        )
                );

        // ACTUALIZAR DATOS GENERALES
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagen(dto.getImagen());
        producto.setTalle(dto.getTalle());
        producto.setStock(dto.getStock());

        // ACTUALIZAR CATEGORÍA
        if (dto.getNombreCategoria() != null) {
            Categoria categoria = buscarOCrearCategoria(
                    dto.getNombreCategoria()
            );

            producto.setCategoria(categoria);
        } else {
            producto.setCategoria(null);
        }

        // ACTUALIZAR ETIQUETAS
        if (dto.getEtiquetas() != null) {
            List<Etiqueta> etiquetas = buscarOCrearEtiquetas(
                    dto.getEtiquetas()
            );

            producto.setEtiquetas(etiquetas);
        } else {
            producto.setEtiquetas(null);
        }

        // GUARDAR MODIFICACIONES
        Producto actualizado = productoRepository.save(producto);

        // DEVOLVER PRODUCTO ACTUALIZADO
        return convertirADTO(actualizado);
    }

    // ELIMINAR PRODUCTO
    @Transactional
    public void eliminar(Long id) {

        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Producto no encontrado con ID: " + id
            );
        }

        productoRepository.deleteById(id);
    }

    // BUSCAR O CREAR CATEGORÍA
    private Categoria buscarOCrearCategoria(String nombre) {

        String nombreNormalizado = nombre.trim();

        if (nombreNormalizado.equalsIgnoreCase("Camisetas") ||
            nombreNormalizado.equalsIgnoreCase("Camiseta") ||
            nombreNormalizado.equalsIgnoreCase("Jerseys")) {

            nombreNormalizado = "Jersey";
        }

        final String categoriaBuscada = nombreNormalizado;

        return categoriaRepository.findAll()
                .stream()
                .filter(c ->
                        c.getNombre().equalsIgnoreCase(
                                categoriaBuscada
                        )
                )
                .findFirst()
                .orElseGet(() ->
                        categoriaRepository.save(
                                new Categoria(categoriaBuscada)
                        )
                );
    }

    // BUSCAR O CREAR ETIQUETAS
    private List<Etiqueta> buscarOCrearEtiquetas(
            List<String> nombres) {

        return nombres.stream()
                .map(String::trim)
                .filter(nombre -> !nombre.isEmpty())
                .distinct()
                .map(nombre ->
                        etiquetaRepository.findAll()
                                .stream()
                                .filter(e ->
                                        e.getNombre().equalsIgnoreCase(
                                                nombre
                                        )
                                )
                                .findFirst()
                                .orElseGet(() ->
                                        etiquetaRepository.save(
                                                new Etiqueta(nombre)
                                        )
                                )
                )
                .collect(Collectors.toList());
    }

    // CONVERTIR ENTIDAD A DTO
    private ProductoDTO convertirADTO(Producto producto) {

        ProductoDTO dto = new ProductoDTO();

        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setDescripcion(producto.getDescripcion());
        dto.setImagen(producto.getImagen());
        dto.setTalle(producto.getTalle());
        dto.setStock(producto.getStock());

        // CATEGORÍA
        if (producto.getCategoria() != null) {
            dto.setNombreCategoria(
                    producto.getCategoria().getNombre()
            );
        }

        // ETIQUETAS
        if (producto.getEtiquetas() != null) {
            dto.setEtiquetas(
                    producto.getEtiquetas()
                            .stream()
                            .map(Etiqueta::getNombre)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}
