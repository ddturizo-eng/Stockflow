/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Persistencia;

import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.excepciones.ProductoNoEncontradoExcepcion;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositorio {
    
    private static final String ARCHIVO = "data/productos.dat";
    private List<Producto> productos;
    
    public ProductoRepositorio() {
        this.productos = cargarProductos();
    }
    
    public void guardar(Producto producto) throws IOException {
        productos.add(producto);
        guardarArchivo();
    }
    
    public Producto buscar(String codigo) throws ProductoNoEncontradoExcepcion {
        return productos.stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new ProductoNoEncontradoExcepcion(codigo));
    }
    
    public List<Producto> listarTodos() {
        return new ArrayList<>(productos);
    }
    
    public void actualizar(Producto producto) throws IOException, ProductoNoEncontradoExcepcion {
        Producto existente = buscar(producto.getCodigo());
        int index = productos.indexOf(existente);
        productos.set(index, producto);
        guardarArchivo();
    }
    
    public void eliminar(String codigo) throws IOException, ProductoNoEncontradoExcepcion {
        Producto producto = buscar(codigo);
        productos.remove(producto);
        guardarArchivo();
    }
    
    private void guardarArchivo() throws IOException {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(productos);
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Producto> cargarProductos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<Producto>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
