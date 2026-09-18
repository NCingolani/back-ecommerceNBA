package com.api.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.ecommerce.model.Etiqueta;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
}
