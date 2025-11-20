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

/**
 * Repositorio para la persistencia de facturas del sistema.
 * 
 * <p>Gestiona el almacenamiento, consulta y actualización de facturas mediante
 * serialización de objetos. Proporciona funcionalidades de búsqueda avanzada
 * por cliente, estado y número de comprobante.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Almacenamiento persistente en archivo binario</li>
 *   <li>Búsqueda por número de comprobante (identificador único)</li>
 *   <li>Filtrado por cliente y estado de factura</li>
 *   <li>Actualización de estados de factura</li>
 *   <li>Generación automática de números de comprobante</li>
 * </ul>
 * 
 * <p>Estados de factura típicos: PAGADA, PENDIENTE, ANULADA, etc.</p>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see Factura
 */
public class FacturacionRepositorio {
    
    /** Ruta del archivo de persistencia de facturas */
    private static final String ARCHIVO = "data/facturas.dat";
    
    /** Lista en memoria de todas las facturas */
    private List<Factura> facturas;
    
    /**
     * Constructor que inicializa el repositorio.
     * Carga automáticamente las facturas desde el archivo.
     */
    public FacturacionRepositorio() {
        this.facturas = cargarFacturas();
    }
    
    /**
     * Guarda una nueva factura en el repositorio.
     * 
     * @param factura la factura a guardar
     * @throws IOException si ocurre un error al escribir en el archivo
     */
    public void guardar(Factura factura) throws IOException {
        facturas.add(factura);
        guardarArchivo();
    }
    
    /**
     * Busca una factura por su número de comprobante.
     * 
     * <p>El número de comprobante es el identificador único de cada factura.</p>
     * 
     * @param numeroComprobante el número de comprobante a buscar
     * @return la factura encontrada
     * @throws Exception si la factura no existe
     */
    public Factura buscar(String numeroComprobante) throws Exception {
        return facturas.stream()
                .filter(f -> f.getNumeroComprobante().equals(numeroComprobante))
                .findFirst()
                .orElseThrow(() -> new Exception("Factura no encontrada: " + numeroComprobante));
    }
    
    /**
     * Lista todas las facturas registradas en el sistema.
     * 
     * @return copia de la lista completa de facturas
     */
    public List<Factura> listarTodos() {
        return new ArrayList<>(facturas);
    }
    
    /**
     * Busca facturas por nombre de cliente.
     * 
     * <p>Realiza una búsqueda case-insensitive que coincida con parte
     * del nombre del cliente.</p>
     * 
     * @param nombreCliente nombre o parte del nombre del cliente
     * @return lista de facturas que coinciden con el criterio
     */
    public List<Factura> buscarPorCliente(String nombreCliente) {
        return facturas.stream()
                .filter(f -> f.getNombreCliente().toLowerCase().contains(nombreCliente.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Busca facturas por estado específico.
     * 
     * <p>Útil para filtrar facturas pagadas, pendientes, anuladas, etc.</p>
     * 
     * @param estado el estado de las facturas a buscar
     * @return lista de facturas con el estado especificado
     */
    public List<Factura> buscarPorEstado(String estado) {
        return facturas.stream()
                .filter(f -> f.getEstado().equals(estado))
                .collect(Collectors.toList());
    }
    
    /**
     * Actualiza el estado de una factura existente.
     * 
     * <p>Permite cambiar el estado de una factura (ej: de PENDIENTE a PAGADA)
     * sin modificar otros datos de la factura.</p>
     * 
     * @param numeroComprobante número de comprobante de la factura
     * @param nuevoEstado el nuevo estado a establecer
     * @throws Exception si la factura no existe
     * @throws IOException si hay error al guardar los cambios
     */
    public void actualizarEstado(String numeroComprobante, String nuevoEstado) throws Exception, IOException {
        Factura factura = buscar(numeroComprobante);
        factura.setEstado(nuevoEstado);
        guardarArchivo();
    }
    
    /**
     * Obtiene el último número de comprobante para generar el siguiente.
     * 
     * <p>Utilizado para generar números de comprobante consecutivos
     * de forma automática.</p>
     * 
     * @return el último número utilizado, o 0 si no hay facturas
     */
    public int obtenerUltimoNumero() {
        if (facturas.isEmpty()) {
            return 0;
        }
        return facturas.size();
    }
    
    /**
     * Persiste la lista de facturas en el archivo.
     * 
     * <p>Crea el directorio data si no existe.</p>
     * 
     * @throws IOException si ocurre un error al escribir
     */
    private void guardarArchivo() throws IOException {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(facturas);
        }
    }
    
    /**
     * Carga las facturas desde el archivo de persistencia.
     * 
     * <p>Si el archivo no existe o hay error al leer,
     * retorna una lista vacía.</p>
     * 
     * @return lista de facturas cargadas
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