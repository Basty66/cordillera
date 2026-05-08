package com.grupocordillera.bff.dto;

public class LoginResponse {
    private String token;
    private String username;
    private String rol;
    private String nombre;

    public LoginResponse(String token, String username, String rol, String nombre) {
        this.token = token;
        this.username = username;
        this.rol = rol;
        this.nombre = nombre;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
