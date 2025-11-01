/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Usuario extends Entidad {
    
    private String username;
    private String password;
    private String nombreCompleto;
    private String rol;
    private LocalDateTime ultimoAcceso;
    
    // Propiedades JavaFX (para TableView, ListView, etc.)
    private StringProperty usernameProperty;
    private StringProperty nombreCompletoProperty;
    private StringProperty rolProperty;
    private StringProperty ultimoAccesoProperty;
    
    // Constructor
    public Usuario(String username, String password, String nombreCompleto, String rol) {
        super(username);
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.ultimoAcceso = LocalDateTime.now();
        this.usernameProperty = new SimpleStringProperty(username);
        this.nombreCompletoProperty = new SimpleStringProperty(nombreCompleto);
        this.rolProperty = new SimpleStringProperty(rol);
        this.ultimoAccesoProperty = new SimpleStringProperty(formatearFecha(ultimoAcceso));
    }
    
    // Getters y Setters 
    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username;
        if (usernameProperty != null) {
            usernameProperty.set(username);
        }
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
        if (nombreCompletoProperty != null) {
            nombreCompletoProperty.set(nombreCompleto);
        }
    }
    
    public String getRol() { 
        return rol; 
    }
    
    public void setRol(String rol) { 
        this.rol = rol;
        if (rolProperty != null) {
            rolProperty.set(rol);
        }
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
    
    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
        if (ultimoAccesoProperty != null) {
            ultimoAccesoProperty.set(formatearFecha(this.ultimoAcceso));
        }
    }
    
    public String getNombre() {
        return this.nombreCompleto;
    }
    
    // JavaFX Properties (para binding con TableView)
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
    
    public StringProperty rolProperty() {
        if (rolProperty == null) {
            rolProperty = new SimpleStringProperty(rol);
        }
        return rolProperty;
    }
    
    public StringProperty ultimoAccesoProperty() {
        if (ultimoAccesoProperty == null) {
            ultimoAccesoProperty = new SimpleStringProperty(formatearFecha(ultimoAcceso));
        }
        return ultimoAccesoProperty;
    }
    
    // Método auxiliar para formatear fechas
    private String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }
    
    @Override
    public String toString() {
        return nombreCompleto + " (" + rol + ")";
    }

   

    
}

