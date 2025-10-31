/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatosGrafica {
    private TipoGrafica tipo;
    private String titulo;
    private List<SerieGrafica> series;
    private Map<String, Object> metadata;
    
    public DatosGrafica() {
        this.series = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    public void agregarSerie(SerieGrafica serie) {
        this.series.add(serie);
    }
    
    // Getters y Setters
    public TipoGrafica getTipo() { return tipo; }
    public void setTipo(TipoGrafica tipo) { this.tipo = tipo; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public List<SerieGrafica> getSeries() { return series; }
    public void setSeries(List<SerieGrafica> series) { this.series = series; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}