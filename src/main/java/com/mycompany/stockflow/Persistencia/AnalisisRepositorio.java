/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Persistencia;

import com.mycompany.stockflow.Modelo.ResultadoAnalisisIA;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio único para persistencia de análisis de IA en memoria.
 * 
 * <p>Implementa el patrón Singleton para mantener los análisis durante toda la sesión
 * de la aplicación y evitar que se pierdan al cambiar de vista. Proporciona almacenamiento
 * en memoria con gestión automática de límites mediante política LRU (Least Recently Used).</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Singleton: Una única instancia durante toda la aplicación</li>
 *   <li>Almacenamiento en memoria con límite de 100 análisis en caché</li>
 *   <li>Historial observable para interfaces reactivas (JavaFX)</li>
 *   <li>Gestión automática de memoria mediante política LRU</li>
 *   <li>Búsqueda y filtrado por tipo, fecha y período</li>
 *   <li>Estadísticas agregadas del repositorio</li>
 * </ul>
 * 
 * <p>Límites del sistema:</p>
 * <ul>
 *   <li><b>Caché:</b> 100 análisis (los más antiguos se eliminan automáticamente)</li>
 *   <li><b>Historial observable:</b> 50 análisis más recientes</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see ResultadoAnalisisIA
 */
public class AnalisisRepositorio {
    
    /** Instancia única del repositorio (patrón Singleton) */
    private static AnalisisRepositorio instancia;
    
    /** Mapa de análisis indexados por ID con política LRU */
    private final Map<String, ResultadoAnalisisIA> analisisPorId;
    
    /** Lista ordenada de todos los análisis de la sesión */
    private final List<ResultadoAnalisisIA> analisisEnSesion;
    
    /** Lista observable para interfaces JavaFX reactivas */
    private final ObservableList<ResultadoAnalisisIA> historialObservable;
    
    /** Referencia al análisis actualmente seleccionado */
    private ResultadoAnalisisIA analisisActual;
    
    /** Límite máximo de análisis en caché */
    private static final int LIMITE_CACHE = 100;
    
    /** Límite máximo de análisis en historial observable */
    private static final int LIMITE_HISTORIAL = 50;
    
    /**
     * Constructor privado para implementar Singleton.
     * 
     * <p>Inicializa las estructuras de datos:</p>
     * <ul>
     *   <li>LinkedHashMap con política LRU para el caché principal</li>
     *   <li>ArrayList para almacenamiento en sesión</li>
     *   <li>ObservableList para interfaces reactivas</li>
     * </ul>
     */
    public AnalisisRepositorio() {
        this.analisisPorId = new LinkedHashMap<String, ResultadoAnalisisIA>(
            LIMITE_CACHE, 0.75f, true
        ) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ResultadoAnalisisIA> eldest) {
                return size() > LIMITE_CACHE;
            }
        };
        
        this.analisisEnSesion = new ArrayList<>();
        this.historialObservable = FXCollections.observableArrayList();
    }
    
    /**
     * Obtiene la instancia única del repositorio (patrón Singleton).
     * 
     * <p>Si no existe instancia, la crea. Este método es thread-safe.</p>
     * 
     * @return la única instancia del repositorio
     */
    public static synchronized AnalisisRepositorio getInstance() {
        if (instancia == null) {
            instancia = new AnalisisRepositorio();
        }
        return instancia;
    }
    
    /**
     * Guarda un nuevo análisis en el repositorio.
     * 
     * <p>El análisis se registra en todas las estructuras de datos:</p>
     * <ol>
     *   <li>Se añade al mapa indexado por ID</li>
     *   <li>Se añade a la lista de sesión si no existe</li>
     *   <li>Se añade al historial observable (al inicio)</li>
     *   <li>Se establece como análisis actual</li>
     * </ol>
     * 
     * <p>El historial observable mantiene solo los 50 análisis más recientes.</p>
     * 
     * @param analisis el análisis a guardar (no puede ser null)
     * @throws IllegalArgumentException si el análisis es null o no tiene ID válido
     */
    public synchronized void guardar(ResultadoAnalisisIA analisis) {
        if (analisis == null) {
            throw new IllegalArgumentException("El analisis no puede ser nulo");
        }
        
        if (analisis.getId() == null || analisis.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("El analisis debe tener un ID valido");
        }
        
        String id = analisis.getId();
        
        analisisPorId.put(id, analisis);
        
        if (!analisisEnSesion.contains(analisis)) {
            analisisEnSesion.add(analisis);
        }
        
        historialObservable.remove(analisis);
        historialObservable.add(0, analisis);
        
        while (historialObservable.size() > LIMITE_HISTORIAL) {
            historialObservable.remove(historialObservable.size() - 1);
        }
        
        this.analisisActual = analisis;
    }
    
    /**
     * Obtiene un análisis específico por su ID.
     * 
     * @param id identificador único del análisis
     * @return el análisis encontrado o null si no existe
     * @throws IllegalArgumentException si el ID es null o vacío
     */
    public synchronized ResultadoAnalisisIA obtenerPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID no puede ser nulo o vacio");
        }
        return analisisPorId.get(id);
    }
    
    /**
     * Obtiene todos los análisis almacenados en la sesión.
     * 
     * <p>Retorna una copia de la lista para evitar modificaciones externas.</p>
     * 
     * @return lista completa de análisis
     */
    public synchronized List<ResultadoAnalisisIA> obtenerTodos() {
        return new ArrayList<>(analisisEnSesion);
    }
    
    /**
     * Obtiene la lista observable de análisis para interfaces JavaFX.
     * 
     * <p>Esta lista se actualiza automáticamente y permite binding con
     * componentes de UI como TableView o ListView.</p>
     * 
     * @return lista observable con los análisis más recientes
     */
    public ObservableList<ResultadoAnalisisIA> obtenerHistorialObservable() {
        return historialObservable;
    }
    
    /**
     * Elimina un análisis del repositorio.
     * 
     * <p>El análisis se elimina de todas las estructuras de datos.
     * Si el análisis eliminado era el actual, se limpia la referencia.</p>
     * 
     * @param id identificador del análisis a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    public synchronized boolean eliminar(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        
        ResultadoAnalisisIA analisis = analisisPorId.remove(id);
        if (analisis != null) {
            analisisEnSesion.remove(analisis);
            historialObservable.remove(analisis);
            
            if (analisisActual != null && analisisActual.getId().equals(id)) {
                analisisActual = null;
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Obtiene el análisis actualmente seleccionado.
     * 
     * @return el análisis actual o null si no hay ninguno seleccionado
     */
    public synchronized ResultadoAnalisisIA obtenerAnalisisActual() {
        return analisisActual;
    }
    
    /**
     * Establece un análisis como actual mediante su ID.
     * 
     * @param id identificador del análisis a establecer como actual
     */
    public synchronized void establecerAnalisisActual(String id) {
        ResultadoAnalisisIA analisis = obtenerPorId(id);
        if (analisis != null) {
            this.analisisActual = analisis;
        }
    }
    
    /**
     * Establece un análisis como actual directamente.
     * 
     * @param analisis el análisis a establecer como actual
     */
    public synchronized void establecerAnalisisActual(ResultadoAnalisisIA analisis) {
        this.analisisActual = analisis;
    }
    
    /**
     * Verifica si existe un análisis seleccionado actualmente.
     * 
     * @return true si hay un análisis actual, false en caso contrario
     */
    public synchronized boolean tieneAnalisisActual() {
        return analisisActual != null;
    }
    
    /**
     * Filtra análisis por tipo específico.
     * 
     * <p>Los resultados se ordenan por fecha de generación (más recientes primero).</p>
     * 
     * @param tipo el tipo de análisis a filtrar (ej: "INVENTARIO", "VENTAS", "COMPLETO")
     * @return lista de análisis del tipo especificado, ordenados por fecha descendente
     */
    public synchronized List<ResultadoAnalisisIA> obtenerPorTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return obtenerTodos();
        }
        
        return analisisEnSesion.stream()
            .filter(a -> tipo.equalsIgnoreCase(a.getTipoAnalisis()))
            .sorted(Comparator.comparing(ResultadoAnalisisIA::getFechaGeneracion).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene análisis dentro de un rango de fechas específico.
     * 
     * <p>Incluye análisis cuya fecha esté entre inicio y fin (ambos inclusivos).</p>
     * 
     * @param inicio fecha inicial del período (inclusiva)
     * @param fin fecha final del período (inclusiva)
     * @return lista de análisis en el período, ordenados por fecha descendente
     * @throws IllegalArgumentException si alguna fecha es null
     */
    public synchronized List<ResultadoAnalisisIA> obtenerPorPeriodo(
        LocalDateTime inicio, 
        LocalDateTime fin
    ) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        
        return analisisEnSesion.stream()
            .filter(a -> {
                LocalDateTime fecha = a.getFechaGeneracion();
                return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
            })
            .sorted(Comparator.comparing(ResultadoAnalisisIA::getFechaGeneracion).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los análisis más recientes hasta un límite especificado.
     * 
     * @param limite número máximo de análisis a retornar
     * @return lista con los N análisis más recientes
     * @throws IllegalArgumentException si el límite es menor o igual a 0
     */
    public synchronized List<ResultadoAnalisisIA> obtenerMasRecientes(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("El limite debe ser mayor a 0");
        }
        
        return analisisEnSesion.stream()
            .sorted((a1, a2) -> a2.getFechaGeneracion().compareTo(a1.getFechaGeneracion()))
            .limit(limite)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene el análisis más reciente del repositorio.
     * 
     * @return el análisis con la fecha más reciente o null si no hay análisis
     */
    public synchronized ResultadoAnalisisIA obtenerUltimo() {
        return analisisEnSesion.stream()
            .max(Comparator.comparing(ResultadoAnalisisIA::getFechaGeneracion))
            .orElse(null);
    }
    
    /**
     * Cuenta el total de análisis almacenados.
     * 
     * @return número total de análisis en el repositorio
     */
    public synchronized int contarTotal() {
        return analisisEnSesion.size();
    }
    
    /**
     * Genera estadísticas de distribución de análisis por tipo.
     * 
     * <p>Agrupa los análisis por tipo y cuenta cuántos hay de cada uno.</p>
     * 
     * @return mapa con tipo de análisis como clave y cantidad como valor
     */
    public synchronized Map<String, Long> contarPorTipo() {
        return analisisEnSesion.stream()
            .collect(Collectors.groupingBy(
                ResultadoAnalisisIA::getTipoAnalisis,
                Collectors.counting()
            ));
    }
    
    /**
     * Limpia análisis más antiguos que el número de días especificado.
     * 
     * <p>Elimina automáticamente todos los análisis cuya fecha de generación
     * sea anterior al límite calculado.</p>
     * 
     * @param diasAntiguedad número de días de antigüedad límite
     * @return cantidad de análisis eliminados
     */
    public synchronized int limpiarAnalisisAntiguos(int diasAntiguedad) {
        LocalDateTime limite = LocalDateTime.now().minusDays(diasAntiguedad);
        
        List<String> idsAEliminar = analisisEnSesion.stream()
            .filter(a -> a.getFechaGeneracion().isBefore(limite))
            .map(ResultadoAnalisisIA::getId)
            .collect(Collectors.toList());
        
        idsAEliminar.forEach(this::eliminar);
        
        return idsAEliminar.size();
    }
    
    /**
     * Limpia completamente el repositorio.
     * 
     * <p>Elimina todos los análisis de todas las estructuras de datos y
     * resetea el análisis actual. Útil al cerrar sesión o reiniciar la aplicación.</p>
     */
    public synchronized void limpiarTodo() {
        analisisPorId.clear();
        analisisEnSesion.clear();
        historialObservable.clear();
        analisisActual = null;
    }
    
    /**
     * Verifica si existe algún análisis de un tipo específico.
     * 
     * @param tipo el tipo de análisis a verificar
     * @return true si existe al menos un análisis de ese tipo, false en caso contrario
     */
    public synchronized boolean existeTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return false;
        }
        
        return analisisEnSesion.stream()
            .anyMatch(a -> tipo.equalsIgnoreCase(a.getTipoAnalisis()));
    }
    
    /**
     * Obtiene el análisis más reciente de un tipo específico.
     * 
     * @param tipo el tipo de análisis buscado
     * @return el análisis más reciente de ese tipo o null si no existe
     */
    public synchronized ResultadoAnalisisIA obtenerUltimoPorTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return null;
        }
        
        return analisisEnSesion.stream()
            .filter(a -> tipo.equalsIgnoreCase(a.getTipoAnalisis()))
            .max(Comparator.comparing(ResultadoAnalisisIA::getFechaGeneracion))
            .orElse(null);
    }
    
    /**
     * Genera un resumen completo con estadísticas del repositorio.
     * 
     * <p>Incluye información agregada como:</p>
     * <ul>
     *   <li>Total de análisis almacenados</li>
     *   <li>Si existe análisis actual seleccionado</li>
     *   <li>Distribución por tipo de análisis</li>
     *   <li>Fecha del análisis más reciente</li>
     * </ul>
     * 
     * @return mapa con estadísticas del repositorio
     */
    public synchronized Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        estadisticas.put("totalAnalisis", contarTotal());
        estadisticas.put("tieneAnalisisActual", tieneAnalisisActual());
        estadisticas.put("distribucionPorTipo", contarPorTipo());
        
        Optional<ResultadoAnalisisIA> masReciente = analisisEnSesion.stream()
            .max(Comparator.comparing(ResultadoAnalisisIA::getFechaGeneracion));
        
        masReciente.ifPresent(a -> 
            estadisticas.put("fechaMasReciente", a.getFechaGeneracion())
        );
        
        return estadisticas;
    }
    
    /**
     * Exporta una copia completa del historial de análisis.
     * 
     * <p>Útil para respaldos o exportación de datos.</p>
     * 
     * @return copia de la lista completa de análisis
     */
    public synchronized List<ResultadoAnalisisIA> exportarHistorial() {
        return new ArrayList<>(analisisEnSesion);
    }
    
    /**
     * Verifica si un análisis específico existe en el repositorio.
     * 
     * @param id identificador del análisis a verificar
     * @return true si el análisis existe, false en caso contrario
     */
    public synchronized boolean existe(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return analisisPorId.containsKey(id);
    }
}