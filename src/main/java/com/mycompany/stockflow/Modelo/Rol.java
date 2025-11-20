/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

/**
 * Enumeración que define los diferentes roles de usuario en el sistema StockFlow.
 * <p>
 * Sistema de roles jerárquico:
 * - ADMIN: Acceso total incluyendo gestión de usuarios
 * - DUEÑO: Acceso completo excepto gestión de usuarios
 * - CAJERO: Acceso a operaciones diarias (ventas, productos, clientes)
 * </p>
 * 
 * @author StockFlow Team
 * @version 2.0
 */
public enum Rol {

    /**
     * Administrador del sistema - Acceso total
     * Puede gestionar usuarios, configuración y todas las funcionalidades
     */
    ADMIN("Administrador", "Acceso total al sistema incluyendo gestión de usuarios"),

    /**
     * Dueño del negocio - Acceso completo excepto gestión de usuarios
     * Puede ver estadísticas, reportes, gestionar inventario y realizar operaciones
     */
    DUEÑO("Dueño", "Acceso completo excepto gestión de usuarios"),

    /**
     * Cajero/Personal de caja - Acceso operacional
     * Puede realizar ventas, consultar productos y gestionar clientes
     */
    CAJERO("Cajero", "Acceso a ventas, productos y clientes");

    private final String nombre;
    private final String descripcion;

    /**
     * Constructor privado del enum
     */
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

    // ============================================
    // MÉTODOS DE VERIFICACIÓN DE PERMISOS
    // ============================================

    /**
     * Verifica si el rol es Administrador
     */
    public boolean esAdmin() {
        return this == ADMIN;
    }

    /**
     * Verifica si el rol es Dueño
     */
    public boolean esDueño() {
        return this == DUEÑO;
    }

    /**
     * Verifica si el rol es Cajero
     */
    public boolean esCajero() {
        return this == CAJERO;
    }

    /**
     * ⭐ CRÍTICO: Solo ADMIN puede gestionar usuarios
     */
    public boolean puedeGestionarUsuarios() {
        return this == ADMIN;
    }

    /**
     * Admin y Dueño pueden ver estadísticas completas
     */
    public boolean puedeVerEstadisticas() {
        return this == ADMIN || this == DUEÑO;
    }

    /**
     * Admin y Dueño pueden acceder a inteligencia de negocio
     */
    public boolean puedeVerInteligenciaNegocio() {
        return this == ADMIN || this == DUEÑO;
    }

    /**
     * Todos pueden gestionar inventario
     */
    public boolean puedeGestionarInventario() {
        return true; // Todos los roles tienen acceso
    }

    /**
     * Todos pueden gestionar clientes
     */
    public boolean puedeGestionarClientes() {
        return true;
    }

    /**
     * Todos pueden realizar ventas
     */
    public boolean puedeRealizarVentas() {
        return true;
    }

    /**
     * Todos pueden ver productos
     */
    public boolean puedeVerProductos() {
        return true;
    }

    /**
     * Admin y Dueño pueden ver reportes financieros
     */
    public boolean puedeVerReportesFinancieros() {
        return this == ADMIN || this == DUEÑO;
    }

    /**
     * Solo Admin puede modificar configuraciones críticas
     */
    public boolean puedeModificarConfiguracion() {
        return this == ADMIN;
    }

    @Override
    public String toString() {
        return nombre;
    }
}