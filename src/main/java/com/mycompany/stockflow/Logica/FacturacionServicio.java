/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Factura;
import com.mycompany.stockflow.Modelo.Venta;
import com.mycompany.stockflow.Persistencia.FacturacionRepositorio;
import java.io.IOException;
import java.util.List;

public class FacturacionServicio {
    
    private final FacturacionRepositorio repositorio;
    
    public FacturacionServicio() {
        this.repositorio = new FacturacionRepositorio();
    }
    
    
    public Factura generarFacturaDesdeVenta(Venta venta, String metodoPago, double montoRecibido, double cambio, 
                                           double subtotal, double iva, double descuento) throws IOException {
       
        int ultimoNumero = repositorio.obtenerUltimoNumero();
        String numeroComprobante = String.format("COMP-%03d", ultimoNumero + 1);
        
        // Crear factura
        Factura factura = new Factura(numeroComprobante, venta);
        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setDescuento(descuento);
        factura.setTotal(venta.getTotal());
        factura.setMetodoPago(metodoPago);
        factura.setMontoRecibido(montoRecibido);
        factura.setCambio(cambio);
        factura.setEstado("PAGADA");
        
        repositorio.guardar(factura);
        
        return factura;
    }
    
    
    public void crearFactura(Factura factura) throws IOException {
        validarFactura(factura);
        repositorio.guardar(factura);
    }
    
    
    public Factura buscarFactura(String numeroComprobante) throws Exception {
        return repositorio.buscar(numeroComprobante);
    }
    
   
    public List<Factura> listarFacturas() {
        return repositorio.listarTodos();
    }
    
    public List<Factura> buscarPorCliente(String nombreCliente) {
        return repositorio.buscarPorCliente(nombreCliente);
    }
    
    
    public List<Factura> buscarPorEstado(String estado) {
        return repositorio.buscarPorEstado(estado);
    }
    
    public void anularFactura(String numeroComprobante) throws Exception, IOException {
        Factura factura = repositorio.buscar(numeroComprobante);
        
        if ("ANULADA".equals(factura.getEstado())) {
            throw new Exception("La factura ya está anulada");
        }
        
        repositorio.actualizarEstado(numeroComprobante, "ANULADA");
    }
    
    
    public double calcularTotalFacturado() {
        return repositorio.listarTodos().stream()
                .filter(Factura::isPagada)
                .mapToDouble(Factura::getTotal)
                .sum();
    }
    
    
    public int obtenerUltimoNumero() {
        return repositorio.obtenerUltimoNumero();
    }
    
    
    private void validarFactura(Factura factura) {
        if (factura.getVenta() == null) {
            throw new IllegalArgumentException("La factura debe tener una venta asociada");
        }
        if (factura.getNumeroComprobante() == null || factura.getNumeroComprobante().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de comprobante es obligatorio");
        }
        if (factura.getTotal() <= 0) {
            throw new IllegalArgumentException("El total debe ser mayor a 0");
        }
    }
}