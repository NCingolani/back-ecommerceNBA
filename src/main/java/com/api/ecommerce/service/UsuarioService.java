package com.api.ecommerce.service;

import com.api.ecommerce.dto.UsuarioDTO;
import com.api.ecommerce.exception.ArgumentInvalidException;
import com.api.ecommerce.exception.ResourceNotFoundException;
import com.api.ecommerce.model.Usuario;
import com.api.ecommerce.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO.getNombre() == null || usuarioDTO.getNombre().trim().isEmpty()) {
            throw new ArgumentInvalidException("El nombre del usuario es obligatorio.");
        }
        if (usuarioDTO.getFechaNacimiento() == null) {
            throw new ArgumentInvalidException("La fecha de nacimiento es obligatoria.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setFechaNacimiento(usuarioDTO.getFechaNacimiento());
        usuario.setSexo(usuarioDTO.getSexo());

        Usuario guardado = usuarioRepository.save(usuario);
        return convertirADTO(guardado);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con ID: " + id));
        return convertirADTO(usuario);
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setSexo(usuario.getSexo());
        return dto;
    }
}
