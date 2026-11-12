
package com.api.ecommerce.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    private Double total;

    private String estado;

    // USUARIO QUE REALIZÓ LA COMPRA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // PRODUCTOS INCLUIDOS EN EL PEDIDO
    @OneToMany(
        mappedBy = "pedido",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<DetallePedido> detalles = new ArrayList<>();

    // CONSTRUCTOR
    public Pedido() {
        this.fecha = LocalDateTime.now();
        this.estado = "CONFIRMADO_SIMULADO";
    }

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // USUARIO
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // DETALLES
    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }
}
