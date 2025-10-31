/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Persistencia;

import com.mycompany.stockflow.Modelo.Venta;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VentaRepositorio {
    
    private static final String ARCHIVO = "data/ventas.dat";
    private List<Venta> ventas;
    
    public VentaRepositorio() {
        this.ventas = cargarVentas();
    }
    
    public void guardar(Venta venta) throws IOException {
        ventas.add(venta);
        guardarArchivo();
    }
    
    public Venta buscar(String codigo) throws Exception {
        return ventas.stream()
                .filter(v -> v.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new Exception("Venta no encontrada"));
    }
    
    public List<Venta> listarTodos() {
        return new ArrayList<>(ventas);
    }
    
    private void guardarArchivo() throws IOException {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(ventas);
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Venta> cargarVentas() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<Venta>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
