package com.api.ecommerce.service;

import com.api.ecommerce.dto.LoginRequest;
import com.api.ecommerce.dto.RegisterRequest;
import com.api.ecommerce.model.Role;
import com.api.ecommerce.model.Usuario;
import com.api.ecommerce.repository.UsuarioRepository;
import com.api.ecommerce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public Map<String, String> register(RegisterRequest request) {
        // Encripta la contraseña antes de guardarla y asigna rol USER por defecto
        Usuario user = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fechaNacimiento(request.getFechaNacimiento())
                .sexo(request.getSexo())
                .role(Role.USER)
                .build();

        usuarioRepository.save(user);

        // Genera el token JWT para el usuario recién registrado
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }

    public Map<String, String> authenticate(LoginRequest request) {
        // Verifica si el email y la contraseña son correctos mediante el AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Si la validación es correcta, se busca el usuario en la BD
        Usuario user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // Se obtienen los roles y se genera el JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}