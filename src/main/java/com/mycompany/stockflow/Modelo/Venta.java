package com.mycompany.stockflow.Modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta extends Entidad implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String codigo;
    private Cliente cliente;
    private LocalDateTime fecha;
    private List<DetalleVenta> detalles;
    private double subtotal;
    private double iva;
    private double total;
    private double costoTotal;
    private double gananciaBruta;
    private double gananciaNeta;
    private String metodoPago;
    
    public Venta() {
        super();
        this.detalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
        this.subtotal = 0.0;
        this.iva = 0.0;
        this.total = 0.0;
        this.costoTotal = 0.0;
        this.gananciaBruta = 0.0;
        this.gananciaNeta = 0.0;
    }
    
    public Venta(String codigo, Cliente cliente) {
        super(codigo);
        this.codigo = codigo;
        this.cliente = cliente;
        this.fecha = LocalDateTime.now();
        this.detalles = new ArrayList<>();
        this.subtotal = 0.0;
        this.iva = 0.0;
        this.total = 0.0;
        this.costoTotal = 0.0;
        this.gananciaBruta = 0.0;
        this.gananciaNeta = 0.0;
    }
    
    public void agregarDetalle(DetalleVenta detalle) {
        if (detalles == null) {
            detalles = new ArrayList<>();
        }
        detalles.add(detalle);
        calcularTotales();
    }
    
    private void calcularTotales() {
        if (detalles != null && !detalles.isEmpty()) {
            this.subtotal = detalles.stream()
                    .mapToDouble(DetalleVenta::getSubtotal)
                    .sum();
            
            this.costoTotal = detalles.stream()
                    .mapToDouble(DetalleVenta::getCostoTotal)
                    .sum();
            
            this.gananciaBruta = subtotal - costoTotal;
            this.gananciaNeta = gananciaBruta;
            
        } else {
            this.subtotal = 0.0;
            this.costoTotal = 0.0;
            this.gananciaBruta = 0.0;
            this.gananciaNeta = 0.0;
        }
    }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    
    public List<DetalleVenta> getDetalles() { 
        if (detalles == null) {
            detalles = new ArrayList<>();
        }
        return detalles; 
    }
    
    public void setDetalles(List<DetalleVenta> detalles) { 
        this.detalles = detalles;
        calcularTotales();
    }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }
    
    public double getGananciaBruta() { return gananciaBruta; }
    public void setGananciaBruta(double gananciaBruta) { this.gananciaBruta = gananciaBruta; }
    
    public double getGananciaNeta() { return gananciaNeta; }
    public void setGananciaNeta(double gananciaNeta) { this.gananciaNeta = gananciaNeta; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public double getMargenGanancia() {
        if (costoTotal == 0) return 0.0;
        return (gananciaBruta / costoTotal) * 100;
    }
    
    public double getROI() {
        return getMargenGanancia();
    }
    
    public double getPorcentajeGananciaDelTotal() {
        if (total == 0) return 0.0;
        return (gananciaNeta / total) * 100;
    }
    
    public boolean esRentable() {
        return gananciaNeta > 0;
    }
    
    @Override
    public String toString() {
        return "Venta{" +
                "codigo='" + codigo + '\'' +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "null") +
                ", fecha=" + fecha +
                ", subtotal=" + subtotal +
                ", total=" + total +
                ", costoTotal=" + costoTotal +
                ", ganancia=" + gananciaNeta +
                ", ROI=" + String.format("%.2f", getROI()) + "%" +
                ", detalles=" + (detalles != null ? detalles.size() : 0) + " items" +
                '}';
    }
}