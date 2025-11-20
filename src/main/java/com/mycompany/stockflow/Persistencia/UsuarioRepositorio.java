/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Persistencia;

import com.mycompany.stockflow.Modelo.Usuario;
import com.mycompany.stockflow.Modelo.Rol;
import com.mycompany.stockflow.utils.EncriptadorPassword;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestión y persistencia de usuarios en archivo .dat
 * 
 * @author StockFlow Team
 * @version 2.0
 */
public class UsuarioRepositorio {
    
    private static final String ARCHIVO_USUARIOS = "data/usuarios.dat";
    private List<Usuario> usuarios;
    
    /**
     * Constructor - Carga usuarios desde archivo o crea archivo vacío
     */
    public UsuarioRepositorio() {
        this.usuarios = new ArrayList<>();
        cargarDesdeArchivo();
    }
    
    // ========== OPERACIONES CRUD ==========
    
    /**
     * Guarda un nuevo usuario
     */
    public boolean guardar(Usuario usuario) {
        if (existeUsername(usuario.getUsername())) {
            return false; // Username ya existe
        }
        usuarios.add(usuario);
        return guardarEnArchivo();
    }
    
    /**
     * Actualiza un usuario existente
     */
    public boolean actualizar(Usuario usuario) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId() == usuario.getId()) {
                usuarios.set(i, usuario);
                return guardarEnArchivo();
            }
        }
        return false;
    }
    
    /**
     * Elimina un usuario por username (ID)
     */
    public boolean eliminar(String usuarioId) {
        boolean eliminado = usuarios.removeIf(u -> 
            u.getUsername().equals(usuarioId)
        );
        if (eliminado) {
            guardarEnArchivo();
        }
        return eliminado;
    }
    
    /**
     * Obtiene un usuario por ID (username)
     */
    public Optional<Usuario> obtenerPorId(String id) {
        return usuarios.stream()
                .filter(u -> u.getUsername().equals(id))
                .findFirst();
    }
    
    /**
     * Obtiene un usuario por username
     */
    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarios.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }
    
    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios);
    }
    
    /**
     * Obtiene solo usuarios activos
     */
    public List<Usuario> obtenerActivos() {
        List<Usuario> activos = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u.isActivo()) {
                activos.add(u);
            }
        }
        return activos;
    }
    
    /**
     * Obtiene usuarios por rol
     */
    public List<Usuario> obtenerPorRol(Rol rol) {
        List<Usuario> usuariosPorRol = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u.getRol() == rol) {
                usuariosPorRol.add(u);
            }
        }
        return usuariosPorRol;
    }
    
    // ========== AUTENTICACIÓN ==========
    
    /**
     * Autentica un usuario con username y password
     */
    public Usuario autenticar(String username, String password) {
        Optional<Usuario> usuarioOpt = obtenerPorUsername(username);
        
        if (!usuarioOpt.isPresent()) {
            return null; // Usuario no existe
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar si está activo
        if (!usuario.isActivo()) {
            return null; // Usuario desactivado
        }
        
        // Verificar contraseña
        if (EncriptadorPassword.verificar(password, usuario.getPassword())) {
            usuario.reiniciarIntentosFallidos();
            usuario.actualizarUltimoAcceso();
            guardarEnArchivo();
            return usuario;
        } else {
            usuario.incrementarIntentosFallidos();
            guardarEnArchivo();
            return null;
        }
    }
    
    // ========== VALIDACIONES ==========
    
    /**
     * Verifica si existe un username
     */
    public boolean existeUsername(String username) {
        return usuarios.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }
    
    /**
     * Verifica si existe un email
     */
    public boolean existeEmail(String email) {
        return usuarios.stream()
                .anyMatch(u -> u.getEmail() != null && 
                               u.getEmail().equalsIgnoreCase(email));
    }
    
    /**
     * Cuenta total de usuarios
     */
    public int contarUsuarios() {
        return usuarios.size();
    }
    
    /**
     * Cuenta usuarios activos
     */
    public int contarUsuariosActivos() {
        return (int) usuarios.stream()
                .filter(Usuario::isActivo)
                .count();
    }
    
    /**
     * Verifica si hay al menos un administrador activo
     */
    public boolean hayAdminActivo() {
        return usuarios.stream()
                .anyMatch(u -> u.getRol() == Rol.ADMIN && u.isActivo());
    }
    
    // ========== BÚSQUEDA Y FILTRADO ==========
    
    /**
     * Busca usuarios por criterio (nombre, username, email)
     */
    public List<Usuario> buscar(String criterio) {
        List<Usuario> resultados = new ArrayList<>();
        String criterioBajo = criterio.toLowerCase();
        
        for (Usuario u : usuarios) {
            if (u.getUsername().toLowerCase().contains(criterioBajo) ||
                u.getNombreCompleto().toLowerCase().contains(criterioBajo) ||
                (u.getEmail() != null && u.getEmail().toLowerCase().contains(criterioBajo))) {
                resultados.add(u);
            }
        }
        return resultados;
    }
    
    // ========== PERSISTENCIA EN ARCHIVO .DAT ==========
    
    /**
     * Carga usuarios desde archivo .dat
     */
    @SuppressWarnings("unchecked")
    private void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_USUARIOS);
        
        // Crear directorio si no existe
        archivo.getParentFile().mkdirs();
        
        if (!archivo.exists()) {
            // Archivo no existe, lista vacía
            usuarios = new ArrayList<>();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(archivo))) {
            usuarios = (List<Usuario>) ois.readObject();
            System.out.println("✅ Usuarios cargados: " + usuarios.size());
        } catch (FileNotFoundException e) {
            usuarios = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al cargar usuarios: " + e.getMessage());
            usuarios = new ArrayList<>();
        }
    }
    
    /**
     * Guarda usuarios en archivo .dat
     */
    private boolean guardarEnArchivo() {
        File archivo = new File(ARCHIVO_USUARIOS);
        archivo.getParentFile().mkdirs();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(archivo))) {
            oos.writeObject(usuarios);
            System.out.println("✅ Usuarios guardados: " + usuarios.size());
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error al guardar usuarios: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reinicia el repositorio (elimina todos los usuarios)
     * ⚠️ USAR CON PRECAUCIÓN - Solo para desarrollo/testing
     */
    public void reiniciar() {
        usuarios.clear();
        guardarEnArchivo();
    }
}