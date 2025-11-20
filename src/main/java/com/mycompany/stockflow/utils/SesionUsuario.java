/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.Modelo.Usuario;
import com.mycompany.stockflow.Modelo.Rol;
import com.mycompany.stockflow.excepciones.AccesoDenegadoExcepcion;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Gestion de sesion de usuario mediante patron Singleton.
 * 
 * <p>Esta clase centraliza toda la informacion y control de la sesion activa del usuario,
 * proporcionando funcionalidades para:</p>
 * <ul>
 *   <li>Iniciar y cerrar sesion de usuario</li>
 *   <li>Verificar permisos y roles</li>
 *   <li>Controlar tiempo de sesion e inactividad</li>
 *   <li>Gestionar el acceso a funcionalidades segun el rol</li>
 * </ul>
 * 
 * <p>El patron Singleton asegura que solo existe una instancia de la sesion
 * en toda la aplicacion, accesible globalmente.</p>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 * SesionUsuario sesion = SesionUsuario.getInstancia();
 * sesion.iniciarSesion(usuario);
 * 
 * if (sesion.esAdmin()) {
 *     // Permitir acceso a funcionalidades de administrador
 * }
 * 
 * sesion.cerrarSesion();
 * </pre>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 */
public class SesionUsuario {

    /** Instancia unica del Singleton */
    private static SesionUsuario instancia;
    
    /** Usuario actualmente autenticado */
    private Usuario usuarioActual;
    
    /** Momento en que se inicio la sesion */
    private LocalDateTime horaLogin;
    
    /** Momento de la ultima actividad del usuario */
    private LocalDateTime ultimaActividad;

    /**
     * Constructor privado para implementar el patron Singleton.
     */
    private SesionUsuario() { }

    /**
     * Obtiene la instancia unica de SesionUsuario (Singleton).
     * 
     * @return la instancia unica de SesionUsuario
     */
    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    /**
     * Inicia una nueva sesion con el usuario especificado.
     * Registra la hora de login y establece la ultima actividad.
     * 
     * @param usuario el usuario que inicia sesion
     */
    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
        this.horaLogin = LocalDateTime.now();
        this.ultimaActividad = horaLogin;
    }

    /**
     * Cierra la sesion actual, limpiando todos los datos del usuario.
     */
    public void cerrarSesion() {
        this.usuarioActual = null;
        this.horaLogin = null;
        this.ultimaActividad = null;
    }

    /**
     * Verifica si existe una sesion activa.
     * 
     * @return true si hay un usuario autenticado, false en caso contrario
     */
    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    /**
     * Actualiza el timestamp de la ultima actividad del usuario.
     * Debe ser llamado en cada interaccion significativa del usuario.
     */
    public void actualizarUltimaActividad() {
        this.ultimaActividad = LocalDateTime.now();
    }

    /**
     * Obtiene el usuario actualmente autenticado.
     * 
     * @return el usuario actual, o null si no hay sesion activa
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Obtiene el rol del usuario actual.
     * 
     * @return el rol del usuario, o null si no hay sesion activa
     */
    public Rol getRolActual() {
        return (usuarioActual != null) ? usuarioActual.getRol() : null;
    }

    /**
     * Obtiene el nombre completo del usuario actual.
     * 
     * @return el nombre del usuario, o "Invitado" si no hay sesion activa
     */
    public String getNombreUsuario() {
        return (usuarioActual != null) ? usuarioActual.getNombreCompleto() : "Invitado";
    }

    /**
     * Obtiene el username del usuario actual.
     * 
     * @return el username, o null si no hay sesion activa
     */
    public String getUsername() {
        return (usuarioActual != null) ? usuarioActual.getUsername() : null;
    }

    /**
     * Verifica si el usuario actual tiene rol de Administrador.
     * 
     * @return true si es ADMIN, false en caso contrario
     */
    public boolean esAdmin() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.ADMIN;
    }

    /**
     * Verifica si el usuario actual tiene rol de Dueño.
     * 
     * @return true si es DUEÑO, false en caso contrario
     */
    public boolean esDueño() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.DUEÑO;
    }

    /**
     * Verifica si el usuario actual tiene rol de Cajero.
     * 
     * @return true si es CAJERO, false en caso contrario
     */
    public boolean esCajero() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.CAJERO;
    }

    /**
     * Verifica si el usuario puede gestionar usuarios (crear, editar, eliminar).
     * Esta funcionalidad suele estar reservada para Administradores.
     * 
     * @return true si tiene permisos para gestionar usuarios
     */
    public boolean puedeGestionarUsuarios() {
        return usuarioActual != null && 
               usuarioActual.getRol().puedeGestionarUsuarios();
    }

    /**
     * Verifica si el usuario puede acceder a estadisticas y reportes.
     * 
     * @return true si tiene permisos para ver estadisticas
     */
    public boolean puedeVerEstadisticas() {
        return usuarioActual != null && 
               usuarioActual.getRol().puedeVerEstadisticas();
    }

    /**
     * Verifica si el usuario puede acceder a inteligencia de negocio y analisis con IA.
     * 
     * @return true si tiene permisos para inteligencia de negocio
     */
    public boolean puedeVerInteligenciaNegocio() {
        return usuarioActual != null && 
               usuarioActual.getRol().puedeVerInteligenciaNegocio();
    }

    /**
     * Verifica que el usuario actual sea Administrador, lanzando excepcion si no lo es.
     * Util para proteger operaciones criticas que requieren permisos administrativos.
     * 
     * @throws AccesoDenegadoExcepcion si el usuario no es Administrador
     */
    public void verificarEsAdmin() throws AccesoDenegadoExcepcion {
        if (!esAdmin()) {
            throw new AccesoDenegadoExcepcion(
                "Esta operacion requiere permisos de Administrador");
        }
    }

    /**
     * Verifica un permiso especifico, lanzando excepcion si no se cumple.
     * Metodo generico para validar cualquier tipo de permiso.
     * 
     * @param tienePermiso resultado de la verificacion del permiso
     * @param mensaje mensaje de error si el permiso es denegado
     * @throws AccesoDenegadoExcepcion si no tiene el permiso requerido
     */
    public void verificarPermiso(boolean tienePermiso, String mensaje) 
            throws AccesoDenegadoExcepcion {
        if (!tienePermiso) {
            throw new AccesoDenegadoExcepcion(mensaje);
        }
    }

    /**
     * Obtiene el momento en que se inicio la sesion.
     * 
     * @return LocalDateTime del login, o null si no hay sesion
     */
    public LocalDateTime getHoraLogin() {
        return horaLogin;
    }

    /**
     * Obtiene el momento de la ultima actividad registrada.
     * 
     * @return LocalDateTime de ultima actividad, o null si no hay sesion
     */
    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    /**
     * Calcula el tiempo total de la sesion en minutos.
     * 
     * @return duracion de la sesion en minutos, o 0 si no hay sesion
     */
    public long getTiempoSesionMinutos() {
        if (horaLogin == null) return 0;
        return Duration.between(horaLogin, LocalDateTime.now()).toMinutes();
    }

    /**
     * Verifica si la sesion ha expirado por inactividad.
     * Se considera expirada si han pasado mas de 30 minutos sin actividad.
     * 
     * @return true si la sesion ha expirado, false en caso contrario
     */
    public boolean sesionExpirada() {
        if (ultimaActividad == null) return false;
        long minutosInactivo = Duration.between(
            ultimaActividad, LocalDateTime.now()
        ).toMinutes();
        return minutosInactivo > 30;
    }

    /**
     * Metodo estatico de conveniencia para obtener el usuario actual directamente.
     * 
     * @return el usuario actual, o null si no hay sesion
     */
    public static Usuario getUsuario() {
        return getInstancia().getUsuarioActual();
    }

    /**
     * Metodo estatico de conveniencia para verificar si hay usuario autenticado.
     * 
     * @return true si hay sesion activa
     */
    public static boolean hayUsuarioLogueado() {
        return getInstancia().haySesionActiva();
    }
}