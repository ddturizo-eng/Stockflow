/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.time.LocalDateTime;

public class PuntoGrafica {
    private String etiqueta;
    private Double valor;
    private LocalDateTime timestamp;
    private String color;
    
    public PuntoGrafica() {}
    
    public PuntoGrafica(String etiqueta, Double valor) {
        this.etiqueta = etiqueta;
        this.valor = valor;
    }
    
    // Getters y Setters
    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }
    
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
