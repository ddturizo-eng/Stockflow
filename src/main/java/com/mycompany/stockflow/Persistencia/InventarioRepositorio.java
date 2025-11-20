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

/**
 * Repositorio para la persistencia de movimientos de inventario.
 * 
 * <p>Gestiona el almacenamiento y consulta de todos los movimientos de inventario
 * del sistema (entradas, salidas y ajustes). Proporciona trazabilidad completa
 * del historial de cambios en el stock de productos.</p>
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Registro persistente de todos los movimientos</li>
 *   <li>Búsqueda por código de movimiento</li>
 *   <li>Filtrado por producto para ver su historial</li>
 *   <li>Filtrado por tipo de movimiento (ENTRADA, SALIDA, AJUSTE)</li>
 *   <li>Consulta de últimos N movimientos</li>
 *   <li>Generación de códigos secuenciales</li>
 * </ul>
 * 
 * <p>Formato de código: MOV-XXXX (ej: MOV-0001, MOV-0002)</p>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see MovimientoInventario
 */
public class InventarioRepositorio {
    
    /** Ruta del archivo de persistencia */
    private static final String ARCHIVO = "data/movimientos_inventario.dat";
    
    /** Lista en memoria de todos los movimientos */
    private List<MovimientoInventario> movimientos;
    
    /**
     * Constructor que inicializa el repositorio.
     * Carga automáticamente los movimientos desde el archivo.
     */
    public InventarioRepositorio() {
        this.movimientos = cargarMovimientos();
    }
    
    /**
     * Guarda un nuevo movimiento de inventario.
     * 
     * @param movimiento el movimiento a guardar
     * @throws IOException si ocurre un error al escribir
     */
    public void guardar(MovimientoInventario movimiento) throws IOException {
        movimientos.add(movimiento);
        guardarArchivo();
    }
    
    /**
     * Busca un movimiento por su código único.
     * 
     * @param codigo código del movimiento (formato: MOV-XXXX)
     * @return el movimiento encontrado
     * @throws Exception si el movimiento no existe
     */
    public MovimientoInventario buscar(String codigo) throws Exception {
        return movimientos.stream()
                .filter(m -> m.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new Exception("Movimiento no encontrado: " + codigo));
    }
    
    /**
     * Lista todos los movimientos registrados.
     * 
     * @return copia de la lista de movimientos
     */
    public List<MovimientoInventario> listarTodos() {
        return new ArrayList<>(movimientos);
    }
    
    /**
     * Busca todos los movimientos de un producto específico.
     * 
     * <p>Útil para ver el historial completo de entradas, salidas
     * y ajustes de un producto determinado.</p>
     * 
     * @param codigoProducto código del producto
     * @return lista de movimientos del producto
     */
    public List<MovimientoInventario> buscarPorProducto(String codigoProducto) {
        return movimientos.stream()
                .filter(m -> m.getCodigoProducto().equals(codigoProducto))
                .collect(Collectors.toList());
    }
    
    /**
     * Busca movimientos por tipo específico.
     * 
     * <p>Permite filtrar solo entradas, solo salidas o solo ajustes.</p>
     * 
     * @param tipoMovimiento el tipo: "ENTRADA", "SALIDA" o "AJUSTE"
     * @return lista de movimientos del tipo especificado
     */
    public List<MovimientoInventario> buscarPorTipo(String tipoMovimiento) {
        return movimientos.stream()
                .filter(m -> m.getTipoMovimiento().equals(tipoMovimiento))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los últimos N movimientos registrados.
     * 
     * <p>Útil para mostrar actividad reciente del inventario
     * en dashboards o reportes.</p>
     * 
     * @param cantidad número de movimientos a obtener
     * @return lista de los últimos movimientos
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
     * Obtiene el último número de movimiento para generar el siguiente código.
     * 
     * <p>Utilizado para generar códigos secuenciales automáticamente.</p>
     * 
     * @return el último número utilizado, o 0 si no hay movimientos
     */
    public int obtenerUltimoNumero() {
        if (movimientos.isEmpty()) {
            return 0;
        }
        return movimientos.size();
    }
    
    /**
     * Persiste la lista de movimientos en el archivo.
     * 
     * @throws IOException si ocurre un error al escribir
     */
    private void guardarArchivo() throws IOException {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(movimientos);
        }
    }
    
    /**
     * Carga los movimientos desde el archivo de persistencia.
     * 
     * <p>Si el archivo no existe o hay error, retorna lista vacía.</p>
     * 
     * @return lista de movimientos cargados
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