
package com.api.ecommerce.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.api.ecommerce.dto.ProductoDTO;
import com.api.ecommerce.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final Path directorioImagenes;

    // CONSTRUCTOR
    public ProductoController(
            ProductoService productoService,
            @Value("${app.upload-dir:uploads}") String uploadDir) {

        this.productoService = productoService;

        this.directorioImagenes = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    // =====================================
    // READ - LISTAR TODOS LOS PRODUCTOS
    // =====================================

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodos() {

        List<ProductoDTO> productos =
                productoService.listarTodos();

        return ResponseEntity.ok(productos);
    }

    // =====================================
    // READ - BUSCAR PRODUCTOS POR NOMBRE
    // =====================================

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombre(
            @RequestParam String nombre) {

        List<ProductoDTO> productos =
                productoService.buscarPorNombre(nombre);

        return ResponseEntity.ok(productos);
    }

    // =====================================
    // READ - BUSCAR PRODUCTO POR ID
    // =====================================

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(
            @PathVariable Long id) {

        ProductoDTO producto =
                productoService.buscarPorId(id);

        return ResponseEntity.ok(producto);
    }

    // =====================================
    // CREATE - CREAR PRODUCTO
    // =====================================

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(
            @RequestBody ProductoDTO productoDTO) {

        ProductoDTO nuevoProducto =
                productoService.crearProducto(productoDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoProducto);
    }

    // =====================================
    // UPDATE - MODIFICAR PRODUCTO
    // =====================================

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable Long id,
            @RequestBody ProductoDTO productoDTO) {

        ProductoDTO productoActualizado =
                productoService.actualizarProducto(
                        id,
                        productoDTO
                );

        return ResponseEntity.ok(productoActualizado);
    }

    // =====================================
    // DELETE - ELIMINAR PRODUCTO
    // =====================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Long id) {

        productoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    // =====================================
    // SUBIR IMAGEN DE PRODUCTO
    // =====================================

    @PostMapping(
            value = "/imagen",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> subirImagen(
            @RequestParam("archivo") MultipartFile archivo) {

        // VALIDAR QUE EXISTA EL ARCHIVO
        if (archivo.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Seleccioná una imagen"
            );
        }

        // VALIDAR TAMAÑO MÁXIMO
        if (archivo.getSize() > 5 * 1024 * 1024) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La imagen supera los 5 MB"
            );
        }

        // VALIDAR FORMATO REAL DEL ARCHIVO
        String extension;

        try (InputStream entrada = archivo.getInputStream()) {

            byte[] cabecera = entrada.readNBytes(12);

            boolean jpg =
                    cabecera.length >= 3 &&
                    (cabecera[0] & 0xff) == 0xff &&
                    (cabecera[1] & 0xff) == 0xd8 &&
                    (cabecera[2] & 0xff) == 0xff;

            boolean png =
                    cabecera.length >= 8 &&
                    (cabecera[0] & 0xff) == 0x89 &&
                    cabecera[1] == 'P' &&
                    cabecera[2] == 'N' &&
                    cabecera[3] == 'G';

            boolean webp =
                    cabecera.length >= 12 &&
                    new String(
                            cabecera,
                            0,
                            4,
                            java.nio.charset.StandardCharsets.US_ASCII
                    ).equals("RIFF") &&
                    new String(
                            cabecera,
                            8,
                            4,
                            java.nio.charset.StandardCharsets.US_ASCII
                    ).equals("WEBP");

            if (jpg) {
                extension = ".jpg";

            } else if (png) {
                extension = ".png";

            } else if (webp) {
                extension = ".webp";

            } else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Solo se permiten imágenes JPG, PNG o WebP"
                );
            }

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo leer la imagen",
                    e
            );
        }

        // GUARDAR IMAGEN
        try {

            Files.createDirectories(directorioImagenes);

            String nombreArchivo =
                    UUID.randomUUID() + extension;

            Path destino =
                    directorioImagenes.resolve(nombreArchivo);

            try (InputStream entrada = archivo.getInputStream()) {

                Files.copy(entrada, destino);
            }

            // URL ACCESIBLE DESDE REACT
            String urlImagen =
                    "/uploads/" + nombreArchivo;

            return ResponseEntity.ok(
                    Map.of("url", urlImagen)
            );

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar la imagen",
                    e
            );
        }
    }
}
