/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Persistencia.ProductoRepositorio;
import com.mycompany.stockflow.excepciones.ProductoNoEncontradoExcepcion;
import java.io.IOException;
import java.util.List;

public class ProductoServicio {
    
    private final ProductoRepositorio repositorio;
    
    public ProductoServicio() {
        this.repositorio = new ProductoRepositorio();
    }
    
    public void crearProducto(Producto producto) throws IOException {
        validarProducto(producto);
        repositorio.guardar(producto);
    }
    
    public Producto buscarProducto(String codigo) throws ProductoNoEncontradoExcepcion {
        return repositorio.buscar(codigo);
    }
    
    public List<Producto> listarProductos() {
        return repositorio.listarTodos();
    }
    
    public void actualizarProducto(Producto producto) throws IOException, ProductoNoEncontradoExcepcion {
        validarProducto(producto);
        repositorio.actualizar(producto);
    }
    
    public void eliminarProducto(String codigo) throws IOException, ProductoNoEncontradoExcepcion {
        repositorio.eliminar(codigo);
    }
    
    public void actualizarStock(String codigo, int nuevaCantidad) throws IOException, ProductoNoEncontradoExcepcion {
        Producto producto = buscarProducto(codigo);
        producto.setStock(nuevaCantidad);
        repositorio.actualizar(producto);
    }
    
    private void validarProducto(Producto producto) {
        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }
}