package com.grupocordillera.bff.controller;

import com.grupocordillera.bff.dto.LoginRequest;
import com.grupocordillera.bff.dto.LoginResponse;
import com.grupocordillera.bff.dto.UsuarioRequest;
import com.grupocordillera.bff.entity.Usuario;
import com.grupocordillera.bff.repository.UsuarioRepository;
import com.grupocordillera.bff.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Operaciones de autenticacion y usuarios")
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
    @Operation(summary = "Iniciar sesion", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inicio de sesion exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales invalidas o usuario inactivo")
    })
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
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "El usuario ya existe o datos invalidos")
    })
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
    @Operation(summary = "Listar usuarios", description = "Retorna una lista de todos los usuarios registrados (solo ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN")
    })
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }
}
