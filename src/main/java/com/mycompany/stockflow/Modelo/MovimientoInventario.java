/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MovimientoInventario extends Entidad {
    
    private String codigo;
    private Producto producto;
    private String tipoMovimiento;
    private int cantidad;
    private int stockAnterior;
    private int stockNuevo;
    private double precioCompraUnitario;
    private double valorTotal;
    private LocalDateTime fecha;
    private String motivo;
    
    public MovimientoInventario() {
        super();
        this.fecha = LocalDateTime.now();
        this.precioCompraUnitario = 0.0;
        this.valorTotal = 0.0;
    }
    
    public MovimientoInventario(String codigo, Producto producto, String tipoMovimiento, 
                                int cantidad, int stockAnterior, String motivo) {
        super(codigo);
        this.codigo = codigo;
        this.producto = producto;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = calcularStockNuevo();
        this.precioCompraUnitario = producto != null ? producto.getPrecioCompra() : 0.0;
        this.valorTotal = precioCompraUnitario * Math.abs(cantidad);
        this.fecha = LocalDateTime.now();
        this.motivo = motivo;
    }
    
    public MovimientoInventario(String codigo, Producto producto, String tipoMovimiento, 
                                int cantidad, int stockAnterior, double precioCompraUnitario, String motivo) {
        super(codigo);
        this.codigo = codigo;
        this.producto = producto;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = calcularStockNuevo();
        this.precioCompraUnitario = precioCompraUnitario;
        this.valorTotal = precioCompraUnitario * Math.abs(cantidad);
        this.fecha = LocalDateTime.now();
        this.motivo = motivo;
    }
    
    private int calcularStockNuevo() {
        if ("ENTRADA".equals(tipoMovimiento)) {
            return stockAnterior + cantidad;
        } else if ("SALIDA".equals(tipoMovimiento)) {
            return stockAnterior - cantidad;
        } else if ("AJUSTE".equals(tipoMovimiento)) {
            return stockAnterior + cantidad;
        }
        return stockAnterior;
    }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { 
        this.codigo = codigo;
        setId(codigo);
    }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public int getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(int stockAnterior) { this.stockAnterior = stockAnterior; }
    
    public int getStockNuevo() { return stockNuevo; }
    public void setStockNuevo(int stockNuevo) { this.stockNuevo = stockNuevo; }
    
    public double getPrecioCompraUnitario() { return precioCompraUnitario; }
    public void setPrecioCompraUnitario(double precioCompraUnitario) { 
        this.precioCompraUnitario = precioCompraUnitario;
        this.valorTotal = precioCompraUnitario * Math.abs(cantidad);
    }
    
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public String getFechaFormateada() {
        if (fecha == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }
    
    public String getNombreProducto() {
        return producto != null ? producto.getNombre() : "N/A";
    }
    
    public String getCodigoProducto() {
        return producto != null ? producto.getCodigo() : "N/A";
    }
    
    public boolean esEntrada() {
        return "ENTRADA".equals(tipoMovimiento);
    }
    
    public boolean esSalida() {
        return "SALIDA".equals(tipoMovimiento);
    }
    
    public boolean esAjuste() {
        return "AJUSTE".equals(tipoMovimiento);
    }
    
    public String getSignoCantidad() {
        if (esEntrada() || (esAjuste() && cantidad > 0)) {
            return "+" + cantidad;
        } else if (esSalida() || (esAjuste() && cantidad < 0)) {
            return String.valueOf(cantidad);
        }
        return String.valueOf(cantidad);
    }
    
    public double getImpactoFinanciero() {
        if (esEntrada()) {
            return -valorTotal;
        } else if (esSalida()) {
            return 0;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "MovimientoInventario{" +
                "codigo='" + codigo + '\'' +
                ", producto=" + getNombreProducto() +
                ", tipo='" + tipoMovimiento + '\'' +
                ", cantidad=" + cantidad +
                ", precioCompra=" + precioCompraUnitario +
                ", valorTotal=" + valorTotal +
                ", fecha=" + getFechaFormateada() +
                '}';
    }
}