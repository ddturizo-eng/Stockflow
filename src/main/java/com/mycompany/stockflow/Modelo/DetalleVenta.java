package com.mycompany.stockflow.Modelo;

import java.io.Serializable;

public class DetalleVenta implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Venta venta;
    private Producto producto;
    private int cantidad;
    private double precioCompraUnitario;
    private double precioVentaUnitario;
    private double subtotal;
    private double costoTotal;
    private double ganancia;
    
    public DetalleVenta() {
        this.cantidad = 0;
        this.precioCompraUnitario = 0.0;
        this.precioVentaUnitario = 0.0;
        this.subtotal = 0.0;
        this.costoTotal = 0.0;
        this.ganancia = 0.0;
    }
    
    public DetalleVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioCompraUnitario = producto.getPrecioCompra();
        this.precioVentaUnitario = producto.getPrecioVenta();
        calcularTotales();
    }
    
    private void calcularTotales() {
        this.subtotal = precioVentaUnitario * cantidad;
        this.costoTotal = precioCompraUnitario * cantidad;
        this.ganancia = subtotal - costoTotal;
    }
    
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { 
        this.producto = producto;
        if (producto != null) {
            this.precioCompraUnitario = producto.getPrecioCompra();
            this.precioVentaUnitario = producto.getPrecioVenta();
            calcularTotales();
        }
    }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad;
        calcularTotales();
    }
    
    public double getPrecioCompraUnitario() { return precioCompraUnitario; }
    public void setPrecioCompraUnitario(double precioCompraUnitario) { 
        this.precioCompraUnitario = precioCompraUnitario;
        calcularTotales();
    }
    
    public double getPrecioVentaUnitario() { return precioVentaUnitario; }
    public void setPrecioVentaUnitario(double precioVentaUnitario) { 
        this.precioVentaUnitario = precioVentaUnitario;
        calcularTotales();
    }
    
    @Deprecated
    public double getPrecioUnitario() { return precioVentaUnitario; }
    
    @Deprecated
    public void setPrecioUnitario(double precioUnitario) { 
        this.precioVentaUnitario = precioUnitario;
        calcularTotales();
    }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }
    
    public double getGanancia() { return ganancia; }
    public void setGanancia(double ganancia) { this.ganancia = ganancia; }
    
    public double getMargenGanancia() {
        if (costoTotal == 0) return 0.0;
        return (ganancia / costoTotal) * 100;
    }
    
    public double getROI() {
        return getMargenGanancia();
    }
    
    public boolean esRentable() {
        return ganancia > 0;
    }
    
    @Override
    public String toString() {
        return "DetalleVenta{" +
                "producto=" + (producto != null ? producto.getNombre() : "null") +
                ", cantidad=" + cantidad +
                ", precioVenta=" + precioVentaUnitario +
                ", subtotal=" + subtotal +
                ", ganancia=" + ganancia +
                '}';
    }
}