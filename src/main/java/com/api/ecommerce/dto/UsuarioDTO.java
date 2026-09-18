package com.api.ecommerce.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private LocalDate fechaNacimiento;
    private String sexo;
}