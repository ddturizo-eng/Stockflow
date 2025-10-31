/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Factura extends Entidad {
    
    private String numeroComprobante;
    private Venta venta;
    private LocalDateTime fechaEmision;
    private String estado; // "PAGADA", "PENDIENTE", "ANULADA"
    private double subtotal;
    private double iva;
    private double descuento;
    private double total;
    private String metodoPago;
    private double montoRecibido;
    private double cambio;
    
    // Constructor vacío
    public Factura() {
        super();
        this.fechaEmision = LocalDateTime.now();
        this.estado = "PAGADA";
    }
    
    // Constructor con parámetros
    public Factura(String numeroComprobante, Venta venta) {
        super(numeroComprobante);
        this.numeroComprobante = numeroComprobante;
        this.venta = venta;
        this.fechaEmision = LocalDateTime.now();
        this.estado = "PAGADA";
        
        // Copiar datos de la venta
        if (venta != null) {
            this.total = venta.getTotal();
        }
    }
    
    // Getters y Setters
    public String getNumeroComprobante() {
        return numeroComprobante;
    }
    
    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
        setId(numeroComprobante);
    }
    
    public Venta getVenta() {
        return venta;
    }
    
    public void setVenta(Venta venta) {
        this.venta = venta;
    }
    
    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }
    
    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    public double getIva() {
        return iva;
    }
    
    public void setIva(double iva) {
        this.iva = iva;
    }
    
    public double getDescuento() {
        return descuento;
    }
    
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public double getMontoRecibido() {
        return montoRecibido;
    }
    
    public void setMontoRecibido(double montoRecibido) {
        this.montoRecibido = montoRecibido;
    }
    
    public double getCambio() {
        return cambio;
    }
    
    public void setCambio(double cambio) {
        this.cambio = cambio;
    }
    
    // Métodos auxiliares
    public String getFechaFormateada() {
        if (fechaEmision == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fechaEmision.format(formatter);
    }
    
    public String getNombreCliente() {
        if (venta != null && venta.getCliente() != null) {
            return venta.getCliente().getNombre();
        }
        return "Sin cliente";
    }
    
    public String getCedulaCliente() {
        if (venta != null && venta.getCliente() != null) {
            return venta.getCliente().getCedula();
        }
        return "N/A";
    }
    
    public boolean isPagada() {
        return "PAGADA".equals(estado);
    }
    
    public boolean isPendiente() {
        return "PENDIENTE".equals(estado);
    }
    
    public boolean isAnulada() {
        return "ANULADA".equals(estado);
    }
    
    @Override
    public String toString() {
        return "Factura{" +
                "numeroComprobante='" + numeroComprobante + '\'' +
                ", fechaEmision=" + getFechaFormateada() +
                ", cliente=" + getNombreCliente() +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                '}';
    }
}