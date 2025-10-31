/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Persistencia;

import com.mycompany.stockflow.Modelo.MovimientoInventario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventarioRepositorio {
    
    private static final String ARCHIVO = "data/movimientos_inventario.dat";
    private List<MovimientoInventario> movimientos;
    
    public InventarioRepositorio() {
        this.movimientos = cargarMovimientos();
    }
    
    /**
     * Guarda un nuevo movimiento de inventario
     */
    public void guardar(MovimientoInventario movimiento) throws IOException {
        movimientos.add(movimiento);
        guardarArchivo();
    }
    
    /**
     * Busca un movimiento por código
     */
    public MovimientoInventario buscar(String codigo) throws Exception {
        return movimientos.stream()
                .filter(m -> m.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new Exception("Movimiento no encontrado: " + codigo));
    }
    
    /**
     * Lista todos los movimientos
     */
    public List<MovimientoInventario> listarTodos() {
        return new ArrayList<>(movimientos);
    }
    
    /**
     * Busca movimientos por código de producto
     */
    public List<MovimientoInventario> buscarPorProducto(String codigoProducto) {
        return movimientos.stream()
                .filter(m -> m.getCodigoProducto().equals(codigoProducto))
                .collect(Collectors.toList());
    }
    
    /**
     * Busca movimientos por tipo
     */
    public List<MovimientoInventario> buscarPorTipo(String tipoMovimiento) {
        return movimientos.stream()
                .filter(m -> m.getTipoMovimiento().equals(tipoMovimiento))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los últimos N movimientos
     */
    public List<MovimientoInventario> obtenerUltimosMovimientos(int cantidad) {
        int size = movimientos.size();
        if (size == 0) {
            return new ArrayList<>();
        }
        
        int inicio = Math.max(0, size - cantidad);
        return new ArrayList<>(movimientos.subList(inicio, size));
    }
    
    /**
     * Obtiene el último número de movimiento para generar el siguiente código
     */
    public int obtenerUltimoNumero() {
        if (movimientos.isEmpty()) {
            return 0;
        }
        return movimientos.size();
    }
    
    /**
     * Guarda la lista de movimientos en el archivo
     */
    private void guardarArchivo() throws IOException {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(movimientos);
        }
    }
    
    /**
     * Carga los movimientos desde el archivo
     */
    @SuppressWarnings("unchecked")
    private List<MovimientoInventario> cargarMovimientos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<MovimientoInventario>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
