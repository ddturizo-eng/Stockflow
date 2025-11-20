/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Persistencia;

import com.mycompany.stockflow.Modelo.Venta;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la persistencia de ventas del sistema.
 * 
 * <p>Gestiona el almacenamiento y recuperación de todas las ventas realizadas
 * mediante serialización de objetos. Cada venta incluye información del cliente,
 * fecha, productos vendidos (detalles) y totales calculados.</p>
 * 
 * <p>Características de persistencia:</p>
 * <ul>
 *   <li>Almacenamiento en archivo binario (data/ventas.dat)</li>
 *   <li>Carga automática al inicializar el repositorio</li>
 *   <li>Guardado inmediato después de cada venta</li>
 *   <li>Búsqueda por código único de venta</li>
 *   <li>Acceso completo al historial de ventas</li>
 * </ul>
 * 
 * <p>Cada venta almacenada mantiene relaciones con:</p>
 * <ul>
 *   <li>Cliente que realizó la compra</li>
 *   <li>Lista de detalles de venta (productos y cantidades)</li>
 *   <li>Totales y subtotales calculados</li>
 *   <li>Fecha y hora de la transacción</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see Venta
 */
public class VentaRepositorio {
    
    /** Ruta del archivo de persistencia de ventas */
    private static final String ARCHIVO = "data/ventas.dat";
    
    /** Lista en memoria de todas las ventas */
    private List<Venta> ventas;
    
    /**
     * Constructor que inicializa el repositorio.
     * 
     * <p>Carga automáticamente las ventas desde el archivo de persistencia.
     * Si el archivo no existe o hay error al leer, inicializa con lista vacía.</p>
     */
    public VentaRepositorio() {
        this.ventas = cargarVentas();
    }
    
    /**
     * Guarda una nueva venta en el repositorio.
     * 
     * <p>La venta se añade a la lista en memoria y se persiste inmediatamente
     * en el archivo. Antes de guardar, asegúrese de que:</p>
     * <ul>
     *   <li>El código de venta sea único</li>
     *   <li>La venta tenga un cliente asignado</li>
     *   <li>Contenga al menos un detalle de venta</li>
     *   <li>El stock de productos haya sido actualizado</li>
     * </ul>
     * 
     * @param venta la venta a guardar con todos sus detalles
     * @throws IOException si ocurre un error al escribir en el archivo
     */
    public void guardar(Venta venta) throws IOException {
        ventas.add(venta);
        guardarArchivo();
    }
    
    /**
     * Busca una venta por su código único.
     * 
     * <p>El código de venta es el identificador principal de cada
     * transacción en el sistema.</p>
     * 
     * @param codigo código único de la venta a buscar
     * @return la venta encontrada con todos sus detalles
     * @throws Exception si la venta no existe
     */
    public Venta buscar(String codigo) throws Exception {
        return ventas.stream()
                .filter(v -> v.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new Exception("Venta no encontrada"));
    }
    
    /**
     * Lista todas las ventas registradas en el sistema.
     * 
     * <p>Retorna una copia de la lista completa de ventas. Útil para:</p>
     * <ul>
     *   <li>Generar reportes de ventas</li>
     *   <li>Análisis de datos y estadísticas</li>
     *   <li>Mostrar historial completo</li>
     *   <li>Calcular totales y promedios</li>
     * </ul>
     * 
     * @return lista completa de todas las ventas
     */
    public List<Venta> listarTodos() {
        return new ArrayList<>(ventas);
    }
    
    /**
     * Persiste la lista de ventas en el archivo.
     * 
     * <p>Crea el directorio data si no existe y serializa la lista completa
     * de ventas con todos sus detalles asociados.</p>
     * 
     * @throws IOException si ocurre un error al escribir en el archivo
     */
    private void guardarArchivo() throws IOException {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(ventas);
        }
    }
    
    /**
     * Carga las ventas desde el archivo de persistencia.
     * 
     * <p>Deserializa la lista completa de ventas con todas sus relaciones
     * (cliente, detalles, productos). Si el archivo no existe, está corrupto
     * o hay error al leer, retorna una lista vacía para permitir que la
     * aplicación funcione normalmente.</p>
     * 
     * @return lista de ventas cargadas o lista vacía si hay error
     */
    @SuppressWarnings("unchecked")
    private List<Venta> cargarVentas() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<Venta>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}