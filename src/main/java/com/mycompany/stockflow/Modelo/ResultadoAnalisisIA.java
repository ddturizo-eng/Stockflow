package com.mycompany.stockflow.Modelo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Modelo que une análisis IA con datos para gráficas
 * Generado automáticamente para integrar IA + Gráficas
 */
public class ResultadoAnalisisIA {
    private String id;
    private LocalDateTime fechaGeneracion;
    private String tipoAnalisis; // "VENTAS", "INVENTARIO", "COMPLETO"
    private String analisisTexto; // Respuesta textual de DeepSeek
    private DatosGrafica datosGrafica; // Datos estructurados para gráficas
    private Map<String, Object> metricas; // Métricas extraídas del análisis
    private Map<String, String> recomendaciones; // Recomendaciones accionables
    
    public ResultadoAnalisisIA() {
        this.id = UUID.randomUUID().toString();
        this.fechaGeneracion = LocalDateTime.now();
        this.metricas = new HashMap<>();
        this.recomendaciones = new HashMap<>();
    }
    
    public ResultadoAnalisisIA(String tipoAnalisis, String analisisTexto, DatosGrafica datosGrafica) {
        this();
        this.tipoAnalisis = tipoAnalisis;
        this.analisisTexto = analisisTexto;
        this.datosGrafica = datosGrafica;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    
    public String getTipoAnalisis() { return tipoAnalisis; }
    public void setTipoAnalisis(String tipoAnalisis) { this.tipoAnalisis = tipoAnalisis; }
    
    public String getAnalisisTexto() { return analisisTexto; }
    public void setAnalisisTexto(String analisisTexto) { this.analisisTexto = analisisTexto; }
    
    public DatosGrafica getDatosGrafica() { return datosGrafica; }
    public void setDatosGrafica(DatosGrafica datosGrafica) { this.datosGrafica = datosGrafica; }
    
    public Map<String, Object> getMetricas() { return metricas; }
    public void setMetricas(Map<String, Object> metricas) { this.metricas = metricas; }
    
    public Map<String, String> getRecomendaciones() { return recomendaciones; }
    public void setRecomendaciones(Map<String, String> recomendaciones) { this.recomendaciones = recomendaciones; }
    
    // Métodos utilitarios
    public void agregarMetrica(String clave, Object valor) {
        this.metricas.put(clave, valor);
    }
    
    public void agregarRecomendacion(String titulo, String descripcion) {
        this.recomendaciones.put(titulo, descripcion);
    }
    
    @Override
    public String toString() {
        return "ResultadoAnalisisIA{" +
                "id='" + id + '\'' +
                ", tipoAnalisis='" + tipoAnalisis + '\'' +
                ", fechaGeneracion=" + fechaGeneracion +
                ", metricas=" + metricas.size() +
                ", recomendaciones=" + recomendaciones.size() +
                '}';
    }
}