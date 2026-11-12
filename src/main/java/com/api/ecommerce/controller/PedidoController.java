
package com.api.ecommerce.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.ecommerce.dto.CrearPedidoDTO;
import com.api.ecommerce.dto.PedidoResumenDTO;
import com.api.ecommerce.model.Pedido;
import com.api.ecommerce.service.PedidoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // =====================================
    // CREATE - REGISTRAR UNA COMPRA
    // =====================================

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPedido(
            @RequestBody CrearPedidoDTO solicitud) {

        Pedido pedido = pedidoService.crearPedido(solicitud);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", pedido.getId(),
                        "estado", pedido.getEstado(),
                        "total", pedido.getTotal(),
                        "mensaje", "Pedido simulado registrado correctamente"
                ));
    }

    // =====================================
    // READ - MIS PEDIDOS
    // =====================================

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoResumenDTO>> listarMisPedidos() {

        return ResponseEntity.ok(
                pedidoService.listarMisPedidos()
        );
    }
}
