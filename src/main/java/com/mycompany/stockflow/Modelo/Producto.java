/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

public class Producto extends Entidad {
    
    private String codigo;
    private String nombre;
    private String categoria;
    private double precioCompra;  // Costo de adquisición
    private double precioVenta;   // Precio al que se vende
    private int stock;
    private int stockMinimo;
    private String descripcion;
    
    public Producto(String codigo, String nombre, String categoria, double precioCompra, 
                    double precioVenta, int stock, int stockMinimo, String descripcion) {
        super(codigo);
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.descripcion = descripcion;
    }
    
    public Producto(String codigo, String nombre, double precioCompra, double precioVenta, 
                    int stock, String descripcion) {
        this(codigo, nombre, "Sin categoría", precioCompra, precioVenta, stock, 5, descripcion);
    }
    
    // Constructor compatible con versión anterior
    public Producto(String codigo, String nombre, String categoria, double precio, 
                    int stock, int stockMinimo, String descripcion) {
        this(codigo, nombre, categoria, precio * 0.6, precio, stock, stockMinimo, descripcion);
    }
    
    public Producto() {
        super("");
        this.codigo = "";
        this.nombre = "";
        this.categoria = "Sin categoría";
        this.precioCompra = 0.0;
        this.precioVenta = 0.0;
        this.stock = 0;
        this.stockMinimo = 5;
        this.descripcion = "";
    }
    
    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { 
        this.codigo = codigo;
        setId(codigo);
    }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { 
        if (precioCompra < 0) {
            throw new IllegalArgumentException("El precio de compra no puede ser negativo");
        }
        this.precioCompra = precioCompra;
    }
    
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { 
        if (precioVenta < 0) {
            throw new IllegalArgumentException("El precio de venta no puede ser negativo");
        }
        this.precioVenta = precioVenta;
    }
    
    
    @Deprecated
    public double getPrecio() { return precioVenta; }
    
    @Deprecated
    public void setPrecio(double precio) { this.precioVenta = precio; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { 
        if (stock < 0) {
            throw new IllegalArgumentException("Stock no puede ser negativo");
        }
        this.stock = stock; 
    }
    
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    // Métodos de cálculo de rentabilidad
    public double getUtilidadUnitaria() {
        return precioVenta - precioCompra;
    }
    
    public double getMargenGanancia() {
        if (precioCompra == 0) return 0.0;
        return ((precioVenta - precioCompra) / precioCompra) * 100;
    }
    
    public double getMarkenUp() {
        if (precioCompra == 0) return 0.0;
        return ((precioVenta - precioCompra) / precioVenta) * 100;
    }
    
    public double getUtilidadTotal() {
        return getUtilidadUnitaria() * stock;
    }
    
    public double getInversionTotal() {
        return precioCompra * stock;
    }
    
    public double getValorInventarioVenta() {
        return precioVenta * stock;
    }
    
    public boolean esRentable() {
        return precioVenta > precioCompra;
    }
    
    public boolean tieneMargenBajo() {
        return getMargenGanancia() < 10.0;
    }
    
    public boolean tieneMargenAlto() {
        return getMargenGanancia() > 50.0;
    }
    
    // Métodos auxiliares existentes
    public boolean tieneStockBajo() {
        return stock <= stockMinimo;
    }
    
    public boolean tieneStock(int cantidad) {
        return stock >= cantidad;
    }
    
    public void reducirStock(int cantidad) {
        if (cantidad > stock) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        this.stock -= cantidad;
    }
    
    public void aumentarStock(int cantidad) {
        this.stock += cantidad;
    }
    
    @Override
    public String toString() {
        return nombre + " - $" + precioVenta;
    }
}