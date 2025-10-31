/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.Modelo.Usuario;
import com.mycompany.stockflow.Modelo.Rol;
import com.mycompany.stockflow.excepciones.AccesoDenegadoExcepcion;
import java.time.LocalDateTime;


public class SesionUsuario {

    private static SesionUsuario instancia; // Singleton
    private Usuario usuarioActual;
    private LocalDateTime horaLogin;
    private LocalDateTime ultimaActividad;

    // Constructor privado para Singleton
    private SesionUsuario() { }

    // Método para obtener la instancia única
    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    // Inicia sesión con el usuario dado
    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
        this.horaLogin = LocalDateTime.now();
        this.ultimaActividad = horaLogin;
    }

    // Cierra la sesión actual
    public void cerrarSesion() {
        this.usuarioActual = null;
        this.horaLogin = null;
        this.ultimaActividad = null;
    }

    // Verifica si hay sesión activa
    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    // Obtiene el usuario actual
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // Obtiene el rol del usuario actual
    public String getRolActual() {
        return (usuarioActual != null) ? usuarioActual.getRol() : null;
    }

    // Obtiene el nombre del usuario actual
    public String getNombreUsuario() {
        return (usuarioActual != null) ? usuarioActual.getNombreCompleto() : "invitado";
    }

    // Actualiza la última actividad
    public void actualizarUltimaActividad() {
        this.ultimaActividad = LocalDateTime.now();
    }

    // Obtiene la hora de login
    public LocalDateTime getHoraLogin() {
        return horaLogin;
    }

    // Obtiene la última actividad
    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }
}