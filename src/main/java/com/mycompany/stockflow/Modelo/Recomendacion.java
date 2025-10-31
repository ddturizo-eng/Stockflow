/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Recomendacion extends Entidad {
    
    private String tipo;
    private String productoId;
    private String titulo;
    private String descripcion;
    private String prioridad;
    private LocalDateTime fechaGeneracion;
    private String accionRecomendada;
    private String justificacion;
    private boolean aplicada;
    
    public Recomendacion() {
        super();
        this.fechaGeneracion = LocalDateTime.now();
        this.aplicada = false;
    }
    
    public Recomendacion(String tipo, String productoId, String titulo, String descripcion, String prioridad) {
        super(tipo + "_" + productoId + "_" + System.currentTimeMillis());
        this.tipo = tipo;
        this.productoId = productoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.fechaGeneracion = LocalDateTime.now();
        this.aplicada = false;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getProductoId() {
        return productoId;
    }
    
    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getPrioridad() {
        return prioridad;
    }
    
    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
    
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }
    
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
    
    public String getAccionRecomendada() {
        return accionRecomendada;
    }
    
    public void setAccionRecomendada(String accionRecomendada) {
        this.accionRecomendada = accionRecomendada;
    }
    
    public String getJustificacion() {
        return justificacion;
    }
    
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
    
    public boolean isAplicada() {
        return aplicada;
    }
    
    public void setAplicada(boolean aplicada) {
        this.aplicada = aplicada;
    }
    
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaGeneracion.format(formatter);
    }
    
    @Override
    public String toString() {
        return "Recomendacion: " + titulo + " [" + prioridad + "]";
    }
}
