/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.util.ArrayList;
import java.util.List;

public class ContextoNegocio {
    
    private List<Producto> productos;
    private List<Venta> ventas;
    private List<Cliente> clientes;
    private List<MovimientoInventario> movimientos;
    private String periodoAnalisis;
    
    public ContextoNegocio() {
        this.productos = new ArrayList<>();
        this.ventas = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.movimientos = new ArrayList<>();
    }
    
    public List<Producto> getProductos() {
        return productos;
    }
    
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
    
    public List<Venta> getVentas() {
        return ventas;
    }
    
    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }
    
    public List<Cliente> getClientes() {
        return clientes;
    }
    
    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
    
    public List<MovimientoInventario> getMovimientos() {
        return movimientos;
    }
    
    public void setMovimientos(List<MovimientoInventario> movimientos) {
        this.movimientos = movimientos;
    }
    
    public String getPeriodoAnalisis() {
        return periodoAnalisis;
    }
    
    public void setPeriodoAnalisis(String periodoAnalisis) {
        this.periodoAnalisis = periodoAnalisis;
    }
    
    public int getTotalProductos() {
        return productos != null ? productos.size() : 0;
    }
    
    public int getTotalVentas() {
        return ventas != null ? ventas.size() : 0;
    }
    
    public int getTotalClientes() {
        return clientes != null ? clientes.size() : 0;
    }
    
    @Override
    public String toString() {
        return String.format("Contexto: %d productos, %d ventas, %d clientes",
                getTotalProductos(), getTotalVentas(), getTotalClientes());
    }
}
