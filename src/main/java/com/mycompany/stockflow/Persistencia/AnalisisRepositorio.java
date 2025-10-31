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
 * Implemento Singleton para mantener los análisis durante toda la sesión
 * y evitar que se pierdan al cambiar de vista.
 */
public class AnalisisRepositorio {
    
    private static AnalisisRepositorio instancia;
    
    private final Map<String, ResultadoAnalisisIA> analisisPorId;
    private final List<ResultadoAnalisisIA> analisisEnSesion;
    private final ObservableList<ResultadoAnalisisIA> historialObservable;
    private ResultadoAnalisisIA analisisActual;
    
    private static final int LIMITE_CACHE = 100;
    private static final int LIMITE_HISTORIAL = 50;
    
    public AnalisisRepositorio() {
        // Uso LinkedHashMap con política LRU para gestionar el límite de memoria
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
    
    public static synchronized AnalisisRepositorio getInstance() {
        if (instancia == null) {
            instancia = new AnalisisRepositorio();
        }
        return instancia;
    }
    
    /**
     * Guardo un nuevo análisis en el repositorio.
     * Lo registro en todas las estructuras de datos necesarias.
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
    
    public synchronized ResultadoAnalisisIA obtenerPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID no puede ser nulo o vacio");
        }
        return analisisPorId.get(id);
    }
    
    public synchronized List<ResultadoAnalisisIA> obtenerTodos() {
        return new ArrayList<>(analisisEnSesion);
    }
    
    public ObservableList<ResultadoAnalisisIA> obtenerHistorialObservable() {
        return historialObservable;
    }
    
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
    
    public synchronized ResultadoAnalisisIA obtenerAnalisisActual() {
        return analisisActual;
    }
    
    public synchronized void establecerAnalisisActual(String id) {
        ResultadoAnalisisIA analisis = obtenerPorId(id);
        if (analisis != null) {
            this.analisisActual = analisis;
        }
    }
    
    public synchronized void establecerAnalisisActual(ResultadoAnalisisIA analisis) {
        this.analisisActual = analisis;
    }
    
    public synchronized boolean tieneAnalisisActual() {
        return analisisActual != null;
    }
    
    /**
     * Filtro análisis por tipo específico.
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
     * Obtengo análisis dentro de un rango de fechas.
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
    
    public synchronized List<ResultadoAnalisisIA> obtenerMasRecientes(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("El limite debe ser mayor a 0");
        }
        
        return analisisEnSesion.stream()
            .sorted((a1, a2) -> a2.getFechaGeneracion().compareTo(a1.getFechaGeneracion()))
            .limit(limite)
            .collect(Collectors.toList());
    }
    
    public synchronized ResultadoAnalisisIA obtenerUltimo() {
        return analisisEnSesion.stream()
            .max(Comparator.comparing(ResultadoAnalisisIA::getFechaGeneracion))
            .orElse(null);
    }
    
    /**
     * Cuento el total de análisis almacenados.
     */
    public synchronized int contarTotal() {
        return analisisEnSesion.size();
    }
    
    /**
     * Genero estadísticas agrupadas por tipo de análisis.
     */
    public synchronized Map<String, Long> contarPorTipo() {
        return analisisEnSesion.stream()
            .collect(Collectors.groupingBy(
                ResultadoAnalisisIA::getTipoAnalisis,
                Collectors.counting()
            ));
    }
    
    /**
     * Limpio análisis más antiguos que los días especificados.
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
     * Limpio completamente el repositorio.
     * Útil al cerrar sesión o reiniciar la aplicación.
     */
    public synchronized void limpiarTodo() {
        analisisPorId.clear();
        analisisEnSesion.clear();
        historialObservable.clear();
        analisisActual = null;
    }
    
    /**
     * Verifico si existe algún análisis de un tipo específico.
     */
    public synchronized boolean existeTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return false;
        }
        
        return analisisEnSesion.stream()
            .anyMatch(a -> tipo.equalsIgnoreCase(a.getTipoAnalisis()));
    }
    
    /**
     * Obtengo el análisis más reciente de un tipo específico.
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
     * Genero un resumen completo del estado del repositorio.
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
     * Exporto una copia del historial completo.
     */
    public synchronized List<ResultadoAnalisisIA> exportarHistorial() {
        return new ArrayList<>(analisisEnSesion);
    }
    
    /**
     * Verifico si un análisis específico existe en el repositorio.
     */
    public synchronized boolean existe(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return analisisPorId.containsKey(id);
    }
}