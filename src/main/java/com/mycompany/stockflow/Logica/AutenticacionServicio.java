/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Usuario;
import com.mycompany.stockflow.Persistencia.UsuarioRepositorio;
import com.mycompany.stockflow.utils.SesionUsuario;

/**
 * Servicio de autenticación de usuarios
 */
public class AutenticacionServicio {
    
    private final UsuarioRepositorio repositorio;
    private final SesionUsuario sesionUsuario;
    
    public AutenticacionServicio() {
        this.repositorio = new UsuarioRepositorio();
        this.sesionUsuario = SesionUsuario.getInstancia();
    }
    
    /**
     * Autentica un usuario y crea la sesión
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Usuario autenticado o null si falla
     */
    public Usuario autenticar(String username, String password) {
        // Autenticar en repositorio
        Usuario usuario = repositorio.autenticar(username, password);
        
        if (usuario != null) {
            // ⭐ INICIAR SESIÓN EN EL SISTEMA
            sesionUsuario.iniciarSesion(usuario);
            
            System.out.println("✓ Sesión iniciada para: " + usuario.getNombreCompleto());
            System.out.println("  Rol: " + usuario.getRol().getNombre());
            System.out.println("  Username: " + usuario.getUsername());
        }
        
        return usuario;
    }
    
    /**
     * Cierra la sesión actual
     */
    public void cerrarSesion() {
        sesionUsuario.cerrarSesion();
        System.out.println("✓ Sesión cerrada correctamente");
    }
}