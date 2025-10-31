/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;


public enum Rol {
    USUARIO("Usuario", "Acceso completo al sistema de gestión"),
    CLIENTE("Cliente", "Solo puede ver sus compras y productos");
    
    private final String nombre;
    private final String descripcion;
    
    Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    
    public boolean esUsuario() {
        return this == USUARIO;
    }
    
    public boolean esCliente() {
        return this == CLIENTE;
    }
   
    public boolean puedeGestionarInventario() {
        return this == USUARIO;
    }
   
    public boolean puedeVerEstadisticas() {
        return this == USUARIO;
    }
    
    
    public boolean puedeGestionarClientes() {
        return this == USUARIO;
    }
    
    
    public boolean puedeRealizarVentas() {
        return this == USUARIO;
    }
    
   
    public boolean puedeVerProductos() {
        return true; // Ambos pueden ver productos
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}
