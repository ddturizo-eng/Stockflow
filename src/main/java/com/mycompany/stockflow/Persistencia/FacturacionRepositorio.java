/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Persistencia;

import com.mycompany.stockflow.Modelo.Factura;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FacturacionRepositorio {
    
    private static final String ARCHIVO = "data/facturas.dat";
    private List<Factura> facturas;
    
    public FacturacionRepositorio() {
        this.facturas = cargarFacturas();
    }
    
    /**
     * Guarda una nueva factura
     */
    public void guardar(Factura factura) throws IOException {
        facturas.add(factura);
        guardarArchivo();
    }
    
    /**
     * Busca una factura por su número de comprobante
     */
    public Factura buscar(String numeroComprobante) throws Exception {
        return facturas.stream()
                .filter(f -> f.getNumeroComprobante().equals(numeroComprobante))
                .findFirst()
                .orElseThrow(() -> new Exception("Factura no encontrada: " + numeroComprobante));
    }
    
    /**
     * Lista todas las facturas
     */
    public List<Factura> listarTodos() {
        return new ArrayList<>(facturas);
    }
    
    /**
     * Busca facturas por nombre de cliente
     */
    public List<Factura> buscarPorCliente(String nombreCliente) {
        return facturas.stream()
                .filter(f -> f.getNombreCliente().toLowerCase().contains(nombreCliente.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Busca facturas por estado
     */
    public List<Factura> buscarPorEstado(String estado) {
        return facturas.stream()
                .filter(f -> f.getEstado().equals(estado))
                .collect(Collectors.toList());
    }
    
    /**
     * Actualiza el estado de una factura
     */
    public void actualizarEstado(String numeroComprobante, String nuevoEstado) throws Exception, IOException {
        Factura factura = buscar(numeroComprobante);
        factura.setEstado(nuevoEstado);
        guardarArchivo();
    }
    
    /**
     * Obtiene el último número de comprobante para generar el siguiente
     */
    public int obtenerUltimoNumero() {
        if (facturas.isEmpty()) {
            return 0;
        }
        return facturas.size();
    }
    
    /**
     * Guarda la lista de facturas en el archivo
     */
    private void guardarArchivo() throws IOException {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(facturas);
        }
    }
    
    /**
     * Carga las facturas desde el archivo
     */
    @SuppressWarnings("unchecked")
    private List<Factura> cargarFacturas() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<Factura>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
