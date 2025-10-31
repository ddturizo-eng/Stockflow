/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AnalisisEstadistico extends Entidad {
    
    private String tipoAnalisis;
    private LocalDateTime fechaAnalisis;
    private Map<String, Object> metricas;
    private String resumenIA;
    private String conclusiones;
    private String tendencias;
    
    public AnalisisEstadistico() {
        super();
        this.fechaAnalisis = LocalDateTime.now();
        this.metricas = new HashMap<>();
    }
    
    public AnalisisEstadistico(String tipoAnalisis) {
        super(tipoAnalisis + "_" + System.currentTimeMillis());
        this.tipoAnalisis = tipoAnalisis;
        this.fechaAnalisis = LocalDateTime.now();
        this.metricas = new HashMap<>();
    }
    
    public String getTipoAnalisis() {
        return tipoAnalisis;
    }
    
    public void setTipoAnalisis(String tipoAnalisis) {
        this.tipoAnalisis = tipoAnalisis;
    }
    
    public LocalDateTime getFechaAnalisis() {
        return fechaAnalisis;
    }
    
    public void setFechaAnalisis(LocalDateTime fechaAnalisis) {
        this.fechaAnalisis = fechaAnalisis;
    }
    
    public Map<String, Object> getMetricas() {
        return metricas;
    }
    
    public void setMetricas(Map<String, Object> metricas) {
        this.metricas = metricas;
    }
    
    public void agregarMetrica(String nombre, Object valor) {
        this.metricas.put(nombre, valor);
    }
    
    public Object obtenerMetrica(String nombre) {
        return this.metricas.get(nombre);
    }
    
    public String getResumenIA() {
        return resumenIA;
    }
    
    public void setResumenIA(String resumenIA) {
        this.resumenIA = resumenIA;
    }
    
    public String getConclusiones() {
        return conclusiones;
    }
    
    public void setConclusiones(String conclusiones) {
        this.conclusiones = conclusiones;
    }
    
    public String getTendencias() {
        return tendencias;
    }
    
    public void setTendencias(String tendencias) {
        this.tendencias = tendencias;
    }
    
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaAnalisis.format(formatter);
    }
    
    @Override
    public String toString() {
        return "Analisis " + tipoAnalisis + " - " + getFechaFormateada();
    }
}