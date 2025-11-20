    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Representa un usuario del sistema StockFlow con gestión completa de permisos.
 * 
 * @author StockFlow Team
 * @version 2.0
 */
public class Usuario extends Entidad implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String username;
    private String password; // Encriptada con EncriptadorPassword
    private String nombreCompleto;
    private String email;
    private Rol rol; //
    
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;
    private int intentosFallidos; 
    
    private transient StringProperty usernameProperty;
    private transient StringProperty nombreCompletoProperty;
    private transient StringProperty emailProperty;
    private transient StringProperty rolProperty;
    private transient StringProperty estadoProperty;
    private transient StringProperty ultimoAccesoProperty;
    
    /**
     * Constructor completo para crear un usuario
     */
    public Usuario(String username, String password, String nombreCompleto, 
                   String email, Rol rol) {
        super(username);
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
        this.ultimoAcceso = LocalDateTime.now();
        this.intentosFallidos = 0;
        inicializarProperties();
    }
    
    /**
     * Constructor compatible con versión anterior (String rol)
     */
    public Usuario(String username, String password, String nombreCompleto, String rolString) {
        this(username, password, nombreCompleto, "", convertirStringARol(rolString));
    }
    
    /**
     * Convierte String a Rol para compatibilidad hacia atrás
     */
    private static Rol convertirStringARol(String rolString) {
        if (rolString == null) return Rol.CAJERO;
        
        switch (rolString.toUpperCase()) {
            case "ADMIN":
            case "ADMINISTRADOR":
                return Rol.ADMIN;
            case "DUEÑO":
            case "DUENO":
            case "DUEÑO DE NEGOCIO":
                return Rol.DUEÑO;
            case "CAJERO":
            case "USUARIO":
            default:
                return Rol.CAJERO;
        }
    }

   
    
    private void inicializarProperties() {
        this.usernameProperty = new SimpleStringProperty(username);
        this.nombreCompletoProperty = new SimpleStringProperty(nombreCompleto);
        this.emailProperty = new SimpleStringProperty(email);
        this.rolProperty = new SimpleStringProperty(rol.getNombre());
        this.estadoProperty = new SimpleStringProperty(activo ? "Activo" : "Inactivo");
        this.ultimoAccesoProperty = new SimpleStringProperty(formatearFecha(ultimoAcceso));
    }


    
    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username;
        if (usernameProperty != null) usernameProperty.set(username);
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
    
    public String getNombreCompleto() { 
        return nombreCompleto; 
    }
    
    public void setNombreCompleto(String nombreCompleto) { 
        this.nombreCompleto = nombreCompleto;
        if (nombreCompletoProperty != null) nombreCompletoProperty.set(nombreCompleto);
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        if (emailProperty != null) emailProperty.set(email);
    }
    
    public Rol getRol() { 
        return rol; 
    }
    
    public void setRol(Rol rol) { 
        this.rol = rol;
        if (rolProperty != null) rolProperty.set(rol.getNombre());
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
        if (estadoProperty != null) {
            estadoProperty.set(activo ? "Activo" : "Inactivo");
        }
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }
    
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
        if (ultimoAccesoProperty != null) {
            ultimoAccesoProperty.set(formatearFecha(ultimoAcceso));
        }
    }
    
    public int getIntentosFallidos() {
        return intentosFallidos;
    }
    
    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

 
    
    /**
     * Actualiza la fecha de último acceso a ahora
     */
    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
        if (ultimoAccesoProperty != null) {
            ultimoAccesoProperty.set(formatearFecha(this.ultimoAcceso));
        }
    }
    
    /**
     * Reinicia los intentos fallidos (después de login exitoso)
     */
    public void reiniciarIntentosFallidos() {
        this.intentosFallidos = 0;
    }
    
    /**
     * Incrementa los intentos fallidos
     */
    public void incrementarIntentosFallidos() {
        this.intentosFallidos++;
    }
    
    /**
     * Activa el usuario
     */
    public void activar() {
        setActivo(true);
    }
    
    /**
     * Desactiva el usuario
     */
    public void desactivar() {
        setActivo(false);
    }

    
    
    public StringProperty usernameProperty() {
        if (usernameProperty == null) {
            usernameProperty = new SimpleStringProperty(username);
        }
        return usernameProperty;
    }
    
    public StringProperty nombreCompletoProperty() {
        if (nombreCompletoProperty == null) {
            nombreCompletoProperty = new SimpleStringProperty(nombreCompleto);
        }
        return nombreCompletoProperty;
    }
    
    public StringProperty emailProperty() {
        if (emailProperty == null) {
            emailProperty = new SimpleStringProperty(email);
        }
        return emailProperty;
    }
    
    public StringProperty rolProperty() {
        if (rolProperty == null) {
            rolProperty = new SimpleStringProperty(rol.getNombre());
        }
        return rolProperty;
    }
    
    public StringProperty estadoProperty() {
        if (estadoProperty == null) {
            estadoProperty = new SimpleStringProperty(activo ? "Activo" : "Inactivo");
        }
        return estadoProperty;
    }
    
    public StringProperty ultimoAccesoProperty() {
        if (ultimoAccesoProperty == null) {
            ultimoAccesoProperty = new SimpleStringProperty(formatearFecha(ultimoAcceso));
        }
        return ultimoAccesoProperty;
    }

    // ========== MÉTODOS DE FORMATO ==========
    
    private String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }
    
    /**
     * Alias para compatibilidad
     */
    public String getNombre() {
        return this.nombreCompleto;
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + rol.getNombre() + ")";
    }
}
