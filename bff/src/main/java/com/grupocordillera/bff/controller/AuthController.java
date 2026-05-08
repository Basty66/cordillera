package com.grupocordillera.bff.controller;

import com.grupocordillera.bff.dto.LoginRequest;
import com.grupocordillera.bff.dto.LoginResponse;
import com.grupocordillera.bff.dto.UsuarioRequest;
import com.grupocordillera.bff.entity.Usuario;
import com.grupocordillera.bff.repository.UsuarioRepository;
import com.grupocordillera.bff.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var optUser = usuarioRepository.findByUsername(request.getUsername());
        if (optUser.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no encontrado"));
        }
        Usuario user = optUser.get();
        if (!user.isActivo()) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario inactivo"));
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Contrasena incorrecta"));
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRol(), user.getNombre());
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRol(), user.getNombre()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El usuario ya existe"));
        }
        Usuario user = new Usuario(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getRol(),
            request.getNombre(),
            request.getEmail()
        );
        usuarioRepository.save(user);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario creado exitosamente"));
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }
}
