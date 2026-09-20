package com.api.ecommerce.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.ecommerce.dto.LoginRequest;
import com.api.ecommerce.dto.RegisterRequest;
import com.api.ecommerce.model.Role;
import com.api.ecommerce.model.Usuario;
import com.api.ecommerce.repository.UsuarioRepository;
import com.api.ecommerce.security.JwtUtil;

import lombok.RequiredArgsConstructor;

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
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();

        usuarioRepository.save(user);

        // Genera el token JWT incluyendo el rol del usuario
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }

    public Map<String, String> authenticate(LoginRequest request) {
        // Verifica credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // Genera el JWT incluyendo el rol del usuario autenticado
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}