
package com.api.ecommerce.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.api.ecommerce.dto.CrearPedidoDTO;
import com.api.ecommerce.dto.ItemPedidoDTO;
import com.api.ecommerce.dto.PedidoResumenDTO;
import com.api.ecommerce.model.DetallePedido;
import com.api.ecommerce.model.Pedido;
import com.api.ecommerce.model.Producto;
import com.api.ecommerce.model.Usuario;
import com.api.ecommerce.repository.PedidoRepository;
import com.api.ecommerce.repository.ProductoRepository;
import com.api.ecommerce.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

        private final PedidoRepository pedidoRepository;
        private final ProductoRepository productoRepository;
        private final UsuarioRepository usuarioRepository;

        private static final double COSTO_ENVIO = 1500.0;

        // =====================================
        // OBTENER USUARIO AUTENTICADO
        // =====================================

        private Usuario obtenerUsuarioAutenticado() {

                Authentication authentication = SecurityContextHolder.getContext()
                                .getAuthentication();

                if (authentication == null ||
                                !authentication.isAuthenticated() ||
                                "anonymousUser".equals(authentication.getPrincipal())) {

                        throw new AuthenticationCredentialsNotFoundException(
                                        "Debés iniciar sesión para continuar");
                }

                String email = authentication.getName();

                return usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "Usuario autenticado no encontrado"));
        }

        // =====================================
        // CREATE - REGISTRAR PEDIDO
        // =====================================

        @Transactional
        public Pedido crearPedido(CrearPedidoDTO solicitud) {

                // Identificar al cliente desde el JWT.
                Usuario usuario = obtenerUsuarioAutenticado();

                if (solicitud == null ||
                                solicitud.getProductos() == null ||
                                solicitud.getProductos().isEmpty()) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El carrito está vacío");
                }

                // Agrupar productos repetidos.
                // Usar orden fijo para bloquear filas.
                Map<Long, Integer> cantidades = new TreeMap<>();

                for (ItemPedidoDTO item : solicitud.getProductos()) {

                        if (item == null ||
                                        item.getProductoId() == null ||
                                        item.getCantidad() == null ||
                                        item.getCantidad() <= 0) {

                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Hay productos o cantidades inválidas");
                        }

                        try {
                                cantidades.merge(
                                                item.getProductoId(),
                                                item.getCantidad(),
                                                Math::addExact);

                        } catch (ArithmeticException e) {

                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Cantidad de productos demasiado grande");
                        }
                }

                // Crear pedido asociado al usuario.
                Pedido pedido = new Pedido();
                pedido.setUsuario(usuario);

                double subtotalPedido = 0.0;

                for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {

                        Long productoId = entrada.getKey();
                        int cantidad = entrada.getValue();

                        // Buscar producto y bloquear su fila.
                        Producto producto = productoRepository
                                        .buscarPorIdParaCompra(productoId)
                                        .orElseThrow(() -> new ResponseStatusException(
                                                        HttpStatus.NOT_FOUND,
                                                        "Producto no encontrado: " + productoId));

                        // Validar stock disponible.
                        if (producto.getStock() == null ||
                                        producto.getStock() < cantidad) {

                                throw new ResponseStatusException(
                                                HttpStatus.CONFLICT,
                                                "Stock insuficiente para: " +
                                                                producto.getNombre());
                        }

                        // Validar precio.
                        if (producto.getPrecio() == null ||
                                        !Double.isFinite(producto.getPrecio()) ||
                                        producto.getPrecio() < 0) {

                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Precio inválido para: " +
                                                                producto.getNombre());
                        }

                        // Descontar stock.
                        producto.setStock(
                                        producto.getStock() - cantidad);

                        productoRepository.save(producto);

                        // Crear detalle.
                        DetallePedido detalle = new DetallePedido();

                        detalle.setProducto(producto);
                        detalle.setCantidad(cantidad);
                        detalle.setPrecioUnitario(producto.getPrecio());

                        double subtotal = producto.getPrecio() * cantidad;

                        if (!Double.isFinite(subtotal)) {

                                throw new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Subtotal inválido");
                        }

                        detalle.setSubtotal(subtotal);
                        pedido.agregarDetalle(detalle);

                        subtotalPedido += subtotal;
                }

                double total = subtotalPedido + COSTO_ENVIO;

                if (!Double.isFinite(total)) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Total del pedido inválido");
                }

                pedido.setTotal(total);
                pedido.setEstado("CONFIRMADO_SIMULADO");

                // Guarda el pedido, sus detalles y la asociación
                // con el usuario dentro de la misma transacción.
                return pedidoRepository.save(pedido);
        }

        // =====================================
        // READ - MIS PEDIDOS
        // =====================================

        @Transactional(readOnly = true)
        public List<PedidoResumenDTO> listarMisPedidos() {

                Usuario usuario = obtenerUsuarioAutenticado();

                return pedidoRepository.findByUsuarioIdOrderByFechaDesc(
                                usuario.getId())
                                .stream()
                                .map(this::convertirAResumen)
                                .toList();
        }

        // =====================================
        // READ - TODOS LOS PEDIDOS (ADMIN)
        // =====================================

        @Transactional(readOnly = true)
        public List<PedidoResumenDTO> listarPedidos() {

                return pedidoRepository.findAll()
                                .stream()
                                .map(this::convertirAResumen)
                                .toList();
        }

        // =====================================
        // CONVERTIR PEDIDO A DTO
        // =====================================

        private PedidoResumenDTO convertirAResumen(Pedido pedido) {

                List<PedidoResumenDTO.DetalleDTO> detalles = pedido.getDetalles()
                                .stream()
                                .map(detalle -> new PedidoResumenDTO.DetalleDTO(
                                                detalle.getProducto().getId(),
                                                detalle.getProducto().getNombre(),
                                                detalle.getCantidad(),
                                                detalle.getPrecioUnitario(),
                                                detalle.getSubtotal()))
                                .toList();

                return new PedidoResumenDTO(
                                pedido.getId(),
                                pedido.getFecha(),
                                pedido.getEstado(),
                                pedido.getTotal(),
                                detalles);
        }
}
