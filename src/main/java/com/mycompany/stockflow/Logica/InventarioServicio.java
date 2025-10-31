/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.MovimientoInventario;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Persistencia.InventarioRepositorio;
import java.io.IOException;
import java.util.List;

public class InventarioServicio {
    
    private final InventarioRepositorio repositorio;
    private final ProductoServicio productoServicio;
    
    public InventarioServicio() {
        this.repositorio = new InventarioRepositorio();
        this.productoServicio = new ProductoServicio();
    }
    
    
    public MovimientoInventario registrarMovimiento(Producto producto, String tipoMovimiento, 
                                                   int cantidad, String motivo) throws Exception {
        validarMovimiento(producto, tipoMovimiento, cantidad);
        
        // Generar código de movimiento
        int ultimoNumero = repositorio.obtenerUltimoNumero();
        String codigo = String.format("MOV-%04d", ultimoNumero + 1);
        
        int stockActual = producto.getStock();
        
        // Crear movimiento
        MovimientoInventario movimiento = new MovimientoInventario(
            codigo, producto, tipoMovimiento, cantidad, stockActual, motivo
        );
        
        int nuevoStock = calcularNuevoStock(stockActual, tipoMovimiento, cantidad);
        movimiento.setStockNuevo(nuevoStock);
        
        // Actualizar stock en producto
        productoServicio.actualizarStock(producto.getCodigo(), nuevoStock);
        
        repositorio.guardar(movimiento);
        
        return movimiento;
    }
    
   
    public MovimientoInventario registrarEntrada(Producto producto, int cantidad, String motivo) throws Exception {
        return registrarMovimiento(producto, "ENTRADA", cantidad, motivo);
    }
    
    
    public MovimientoInventario registrarAjuste(Producto producto, int cantidad, String motivo) throws Exception {
        return registrarMovimiento(producto, "AJUSTE", cantidad, motivo);
    }
    
    
    public MovimientoInventario registrarSalida(Producto producto, int cantidad, String motivo) throws Exception {
        return registrarMovimiento(producto, "SALIDA", cantidad, motivo);
    }
    
    
    public MovimientoInventario buscarMovimiento(String codigo) throws Exception {
        return repositorio.buscar(codigo);
    }
    
    
    public List<MovimientoInventario> listarMovimientos() {
        return repositorio.listarTodos();
    }
    
    
    public List<MovimientoInventario> buscarPorProducto(String codigoProducto) {
        return repositorio.buscarPorProducto(codigoProducto);
    }
    
    
    public List<MovimientoInventario> buscarPorTipo(String tipoMovimiento) {
        return repositorio.buscarPorTipo(tipoMovimiento);
    }
    
    
    public List<Producto> obtenerProductosStockBajo() {
        List<Producto> todosLosProductos = productoServicio.listarProductos();
        return todosLosProductos.stream()
                .filter(Producto::tieneStockBajo)
                .collect(java.util.stream.Collectors.toList());
    }
    
   
    public List<MovimientoInventario> obtenerUltimosMovimientos(int cantidad) {
        return repositorio.obtenerUltimosMovimientos(cantidad);
    }
    
   
    private int calcularNuevoStock(int stockActual, String tipoMovimiento, int cantidad) {
        switch (tipoMovimiento) {
            case "ENTRADA":
                return stockActual + cantidad;
            case "SALIDA":
                return stockActual - cantidad;
            case "AJUSTE":
                return stockActual + cantidad; // Puede ser negativo si es ajuste a la baja
            default:
                return stockActual;
        }
    }
    
    
    private void validarMovimiento(Producto producto, String tipoMovimiento, int cantidad) throws Exception {
        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        
        if (tipoMovimiento == null || tipoMovimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio");
        }
        
        if (!tipoMovimiento.equals("ENTRADA") && !tipoMovimiento.equals("SALIDA") && !tipoMovimiento.equals("AJUSTE")) {
            throw new IllegalArgumentException("Tipo de movimiento inválido");
        }
        
        if (cantidad == 0) {
            throw new IllegalArgumentException("La cantidad debe ser diferente de 0");
        }
        
        // Validar que no quede stock negativo en salidas
        if ("SALIDA".equals(tipoMovimiento) && cantidad > producto.getStock()) {
            throw new Exception("Stock insuficiente. Stock actual: " + producto.getStock());
        }
        
        // Validar que no quede stock negativo en ajustes negativos
        if ("AJUSTE".equals(tipoMovimiento) && cantidad < 0) {
            int nuevoStock = producto.getStock() + cantidad;
            if (nuevoStock < 0) {
                throw new Exception("El ajuste dejaría el stock en negativo. Stock actual: " + producto.getStock());
            }
        }
    }
}