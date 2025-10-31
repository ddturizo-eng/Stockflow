/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.util.ArrayList;
import java.util.List;

public class SerieGrafica {
    private String nombre;
    private List<PuntoGrafica> valores;
    private String color;
    
    public SerieGrafica(String nombre) {
        this.nombre = nombre;
        this.valores = new ArrayList<>();
    }
    
    public void agregarPunto(PuntoGrafica punto) {
        this.valores.add(punto);
    }
    
    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public List<PuntoGrafica> getValores() { return valores; }
    public void setValores(List<PuntoGrafica> valores) { this.valores = valores; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}