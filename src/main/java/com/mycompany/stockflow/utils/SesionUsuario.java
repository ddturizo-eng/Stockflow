package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.Modelo.Usuario;
import com.mycompany.stockflow.Modelo.Rol;
import com.mycompany.stockflow.excepciones.AccesoDenegadoExcepcion;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Gestión de sesión de usuario (Singleton)
 * Maneja el usuario actual y control de acceso
 * 
 * @version 2.0
 */
public class SesionUsuario {

    private static SesionUsuario instancia; // Singleton
    private Usuario usuarioActual;
    private LocalDateTime horaLogin;
    private LocalDateTime ultimaActividad;

    // Constructor privado para Singleton
    private SesionUsuario() { }

    /**
     * Obtiene la instancia única (Singleton)
     */
    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    // ========== GESTIÓN DE SESIÓN ==========

    /**
     * Inicia sesión con el usuario dado
     */
    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
        this.horaLogin = LocalDateTime.now();
        this.ultimaActividad = horaLogin;
    }

    /**
     * Cierra la sesión actual
     */
    public void cerrarSesion() {
        this.usuarioActual = null;
        this.horaLogin = null;
        this.ultimaActividad = null;
    }

    /**
     * Verifica si hay sesión activa
     */
    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    /**
     * Actualiza la última actividad
     */
    public void actualizarUltimaActividad() {
        this.ultimaActividad = LocalDateTime.now();
    }

    // ========== OBTENER DATOS DEL USUARIO ==========

    /**
     * Obtiene el usuario actual
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Obtiene el rol del usuario actual
     */
    public Rol getRolActual() {
        return (usuarioActual != null) ? usuarioActual.getRol() : null;
    }

    /**
     * Obtiene el nombre del usuario actual
     */
    public String getNombreUsuario() {
        return (usuarioActual != null) ? usuarioActual.getNombreCompleto() : "Invitado";
    }

    /**
     * Obtiene el username del usuario actual
     */
    public String getUsername() {
        return (usuarioActual != null) ? usuarioActual.getUsername() : null;
    }

    // ========== VERIFICACIÓN DE PERMISOS ==========

    /**
     * Verifica si el usuario actual es Admin
     */
    public boolean esAdmin() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.ADMIN;
    }

    /**
     * Verifica si el usuario actual es Dueño
     */
    public boolean esDueño() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.DUEÑO;
    }

    /**
     * Verifica si el usuario actual es Cajero
     */
    public boolean esCajero() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.CAJERO;
    }

    /**
     * Verifica si puede gestionar usuarios (solo Admin)
     */
    public boolean puedeGestionarUsuarios() {
        return usuarioActual != null && 
               usuarioActual.getRol().puedeGestionarUsuarios();
    }

    /**
     * Verifica si puede ver estadísticas
     */
    public boolean puedeVerEstadisticas() {
        return usuarioActual != null && 
               usuarioActual.getRol().puedeVerEstadisticas();
    }

    /**
     * Verifica si puede ver inteligencia de negocio
     */
    public boolean puedeVerInteligenciaNegocio() {
        return usuarioActual != null && 
               usuarioActual.getRol().puedeVerInteligenciaNegocio();
    }

    /**
     * Lanza excepción si no es Admin
     */
    public void verificarEsAdmin() throws AccesoDenegadoExcepcion {
        if (!esAdmin()) {
            throw new AccesoDenegadoExcepcion(
                "Esta operación requiere permisos de Administrador");
        }
    }

    /**
     * Lanza excepción si no tiene el permiso especificado
     */
    public void verificarPermiso(boolean tienePermiso, String mensaje) 
            throws AccesoDenegadoExcepcion {
        if (!tienePermiso) {
            throw new AccesoDenegadoExcepcion(mensaje);
        }
    }

    // ========== INFORMACIÓN DE SESIÓN ==========

    /**
     * Obtiene la hora de login
     */
    public LocalDateTime getHoraLogin() {
        return horaLogin;
    }

    /**
     * Obtiene la última actividad
     */
    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    /**
     * Obtiene el tiempo de sesión en minutos
     */
    public long getTiempoSesionMinutos() {
        if (horaLogin == null) return 0;
        return Duration.between(horaLogin, LocalDateTime.now()).toMinutes();
    }

    /**
     * Verifica si la sesión ha expirado (30 minutos de inactividad)
     */
    public boolean sesionExpirada() {
        if (ultimaActividad == null) return false;
        long minutosInactivo = Duration.between(
            ultimaActividad, LocalDateTime.now()
        ).toMinutes();
        return minutosInactivo > 30;
    }

    // ========== MÉTODOS ESTÁTICOS DE CONVENIENCIA ==========

    /**
     * Método estático para obtener el usuario actual directamente
     */
    public static Usuario getUsuario() {
        return getInstancia().getUsuarioActual();
    }

    /**
     * Método estático para verificar si hay sesión activa
     */
    public static boolean hayUsuarioLogueado() {
        return getInstancia().haySesionActiva();
    }
}