/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Usuario;
import com.mycompany.stockflow.Modelo.Rol;
import com.mycompany.stockflow.Persistencia.UsuarioRepositorio;
import com.mycompany.stockflow.utils.EncriptadorPassword;
import com.mycompany.stockflow.utils.Validador;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de usuarios con validaciones y reglas de negocio
 * 
 * @author StockFlow Team
 * @version 1.0
 */
public class UsuarioServicio {
    
    private final UsuarioRepositorio repositorio;
    
    public UsuarioServicio() {
        this.repositorio = new UsuarioRepositorio();
    }
    
    /**
     * Crea un nuevo usuario con validaciones completas
     * 
     * @return Usuario creado o null si hay error
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public Usuario crearUsuario(String username, String password, 
                               String nombreCompleto, String email, Rol rol) {
        
        // Validaciones
        validarDatosUsuario(username, password, nombreCompleto, email);
        
        // Verificar que no exista el username
        if (repositorio.existeUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        
        // Verificar que no exista el email
        if (email != null && !email.isEmpty() && repositorio.existeEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Encriptar contraseña
        String passwordEncriptada = EncriptadorPassword.encriptar(password);
        
        // Crear usuario
        Usuario usuario = new Usuario(username, passwordEncriptada, 
                                     nombreCompleto, email, rol);
        
        // Guardar
        if (repositorio.guardar(usuario)) {
            return usuario;
        } else {
            throw new RuntimeException("Error al guardar el usuario");
        }
    }
    
    /**
     * Crea el primer usuario administrador (para setup inicial)
     */
    public Usuario crearPrimerAdmin(String username, String password, 
                                   String nombreCompleto, String email) {
        
        // Verificar que no haya usuarios en el sistema
        if (repositorio.contarUsuarios() > 0) {
            throw new IllegalStateException("Ya existen usuarios en el sistema");
        }
        
        return crearUsuario(username, password, nombreCompleto, email, Rol.ADMIN);
    }
   
    
    /**
     * Actualiza los datos de un usuario (excepto password)
     */
    public boolean actualizarUsuario(String usuarioId, String nombreCompleto, 
                                    String email, Rol rol) {
        
        Optional<Usuario> usuarioOpt = repositorio.obtenerPorId(usuarioId);
        if (!usuarioOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Validar email si se proporciona
        if (email != null && !email.isEmpty()) {
            if (!Validador.esEmailValido(email)) {
                throw new IllegalArgumentException("Email inválido");
            }
            
            // Verificar que el email no esté en uso por otro usuario
            if (repositorio.existeEmail(email) && 
                !email.equalsIgnoreCase(usuario.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
        }
        
        // Actualizar datos
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setEmail(email);
        usuario.setRol(rol);
        
        return repositorio.actualizar(usuario);
    }
    
    /**
     * Cambia la contraseña de un usuario
     */
    public boolean cambiarPassword(String usuarioId, String nuevaPassword) {
        
        // Validar contraseña
        if (nuevaPassword == null || nuevaPassword.length() < 4) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos 4 caracteres");
        }
        
        Optional<Usuario> usuarioOpt = repositorio.obtenerPorId(usuarioId);
        if (!usuarioOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Encriptar y guardar
        String passwordEncriptada = EncriptadorPassword.encriptar(nuevaPassword);
        usuario.setPassword(passwordEncriptada);
        
        return repositorio.actualizar(usuario);
    }
    
    /**
     * Cambia la contraseña validando la anterior
     */
    public boolean cambiarPasswordConValidacion(String usuarioId, 
                                               String passwordActual, 
                                               String nuevaPassword) {
        
        Optional<Usuario> usuarioOpt = repositorio.obtenerPorId(usuarioId);
        if (!usuarioOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar contraseña actual
        if (!EncriptadorPassword.verificar(passwordActual, usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        
        return cambiarPassword(usuarioId, nuevaPassword);
    }
    
    
    /**
     * Activa un usuario
     */
    public boolean activarUsuario(String usuarioId) {
        Optional<Usuario> usuarioOpt = repositorio.obtenerPorId(usuarioId);
        if (!usuarioOpt.isPresent()) {
            return false;
        }
        
        Usuario usuario = usuarioOpt.get();
        usuario.activar();
        return repositorio.actualizar(usuario);
    }
    
    /**
     * Desactiva un usuario con validación de seguridad
     */
    public boolean desactivarUsuario(String usuarioId) {
        Optional<Usuario> usuarioOpt = repositorio.obtenerPorId(usuarioId);
        if (!usuarioOpt.isPresent()) {
            return false;
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // No permitir desactivar al último admin activo
        if (usuario.getRol() == Rol.ADMIN) {
            List<Usuario> admins = repositorio.obtenerPorRol(Rol.ADMIN);
            long adminsActivos = admins.stream()
                    .filter(Usuario::isActivo)
                    .count();
            
            if (adminsActivos <= 1) {
                throw new IllegalStateException(
                    "No se puede desactivar al único administrador activo");
            }
        }
        
        usuario.desactivar();
        return repositorio.actualizar(usuario);
    }
    
    /**
     * Alterna el estado activo/inactivo
     */
    public boolean toggleEstado(String usuarioId) {
        Optional<Usuario> usuarioOpt = repositorio.obtenerPorId(usuarioId);
        if (!usuarioOpt.isPresent()) {
            return false;
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (usuario.isActivo()) {
            return desactivarUsuario(usuarioId);
        } else {
            return activarUsuario(usuarioId);
        }
    }
    
  
    
    /**
     * Elimina un usuario permanentemente con validaciones
     */
    public boolean eliminarUsuario(String usuarioId) {
        Optional<Usuario> usuarioOpt = repositorio.obtenerPorId(usuarioId);
        if (!usuarioOpt.isPresent()) {
            return false;
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // No permitir eliminar al último admin
        if (usuario.getRol() == Rol.ADMIN) {
            List<Usuario> admins = repositorio.obtenerPorRol(Rol.ADMIN);
            if (admins.size() <= 1) {
                throw new IllegalStateException(
                    "No se puede eliminar al único administrador");
            }
        }
        
        return repositorio.eliminar(usuarioId);
    }
    
    
    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return repositorio.obtenerTodos();
    }
    
    /**
     * Obtiene solo usuarios activos
     */
    public List<Usuario> obtenerActivos() {
        return repositorio.obtenerActivos();
    }
    
    /**
     * Obtiene usuarios por rol
     */
    public List<Usuario> obtenerPorRol(Rol rol) {
        return repositorio.obtenerPorRol(rol);
    }
    
    /**
     * Busca usuarios por criterio
     */
    public List<Usuario> buscarUsuarios(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return obtenerTodos();
        }
        return repositorio.buscar(criterio.trim());
    }
    
    /**
     * Obtiene un usuario por username
     */
    public Optional<Usuario> obtenerPorId(String username) {
        return repositorio.obtenerPorId(username);
    }
    
    /**
     * Verifica si el sistema está vacío (sin usuarios)
     */
    public boolean sistemaVacio() {
        return repositorio.contarUsuarios() == 0;
    }
    
    /**
     * Cuenta total de usuarios
     */
    public int contarUsuarios() {
        return repositorio.contarUsuarios();
    }
    
    /**
     * Autentica un usuario
     */
    public Usuario autenticar(String username, String password) {
        return repositorio.autenticar(username, password);
    }
    
    private void validarDatosUsuario(String username, String password, 
                                    String nombreCompleto, String email) {
        
        // Username
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException(
                "El nombre de usuario debe tener al menos 3 caracteres");
        }
        
        // Password
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos 4 caracteres");
        }
        
        // Nombre completo
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }
        
        // Email (opcional pero debe ser válido si se proporciona)
        if (email != null && !email.isEmpty() && !Validador.esEmailValido(email)) {
            throw new IllegalArgumentException("El formato del email es inválido");
        }
    }
        /**
     * Obtiene un usuario por su nombre de usuario (username)
     * 
     * @param username El nombre de usuario a buscar
     * @return El usuario encontrado o null si no existe
     */
    public Usuario obtenerPorUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        try {
            Optional<Usuario> usuarioOpt = repositorio.obtenerPorId(username);
            return usuarioOpt.orElse(null);
        } catch (Exception e) {
            System.err.println("Error al obtener usuario por username: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    
    /**
     * Obtiene estadísticas del sistema de usuarios
     */
    public EstadisticasUsuarios obtenerEstadisticas() {
        return new EstadisticasUsuarios(
            repositorio.contarUsuarios(),
            repositorio.contarUsuariosActivos(),
            repositorio.obtenerPorRol(Rol.ADMIN).size(),
            repositorio.obtenerPorRol(Rol.DUEÑO).size(),
            repositorio.obtenerPorRol(Rol.CAJERO).size()
        );
    }
    
    /**
     * Clase interna para estadísticas
     */
    public static class EstadisticasUsuarios {
        public final int total;
        public final int activos;
        public final int admins;
        public final int dueños;
        public final int cajeros;
        
        public EstadisticasUsuarios(int total, int activos, int admins, 
                                   int dueños, int cajeros) {
            this.total = total;
            this.activos = activos;
            this.admins = admins;
            this.dueños = dueños;
            this.cajeros = cajeros;
        }
    }
}