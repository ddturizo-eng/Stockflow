/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.time.LocalDate;

/**
 * Representa una prediccion de demanda futura para un producto.
 * 
 * <p>Esta clase encapsula los resultados de algoritmos de prediccion
 * que estiman la demanda futura de un producto basandose en datos
 * historicos, tendencias y patrones estacionales.</p>
 * 
 * <p>Componentes de la prediccion:</p>
 * <ul>
 *   <li><strong>Demanda estimada:</strong> Cantidad esperada de unidades</li>
 *   <li><strong>Intervalo de confianza:</strong> Rango de variabilidad esperada</li>
 *   <li><strong>Nivel de confianza:</strong> Certeza estadistica de la prediccion</li>
 *   <li><strong>Factores considerados:</strong> Variables que influyen en la prediccion</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 * PrediccionDemanda prediccion = new PrediccionDemanda(
 *     producto,
 *     LocalDate.now().plusDays(30),
 *     150,  // demanda estimada
 *     120,  // limite inferior
 *     180   // limite superior
 * );
 * prediccion.setNivelConfianza(0.85);
 * prediccion.setTendencia("CRECIENTE");
 * </pre>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 */
public class PrediccionDemanda {
    
    /** Producto al que corresponde la prediccion */
    private Producto producto;
    
    /** Fecha para la cual se realiza la prediccion */
    private LocalDate fechaPrediccion;
    
    /** Cantidad estimada de unidades que se venderan */
    private int demandaEstimada;
    
    /** Limite inferior del intervalo de confianza */
    private int limiteInferior;
    
    /** Limite superior del intervalo de confianza */
    private int limiteSuperior;
    
    /** Nivel de confianza de la prediccion (0.0 a 1.0) */
    private double nivelConfianza;
    
    /** Tendencia detectada (CRECIENTE, DECRECIENTE, ESTABLE) */
    private String tendencia;
    
    /** Factor estacional aplicado */
    private double factorEstacional;
    
    /** Descripcion de los factores considerados */
    private String factoresConsiderados;
    
    /** Fecha en que se genero la prediccion */
    private LocalDate fechaGeneracion;
    
    /**
     * Constructor completo para crear una prediccion de demanda.
     * 
     * @param producto el producto a predecir
     * @param fechaPrediccion fecha futura para la prediccion
     * @param demandaEstimada cantidad estimada de unidades
     * @param limiteInferior limite inferior del intervalo de confianza
     * @param limiteSuperior limite superior del intervalo de confianza
     */
    public PrediccionDemanda(Producto producto, LocalDate fechaPrediccion, 
                           int demandaEstimada, int limiteInferior, int limiteSuperior) {
        this.producto = producto;
        this.fechaPrediccion = fechaPrediccion;
        this.demandaEstimada = demandaEstimada;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        this.fechaGeneracion = LocalDate.now();
        this.nivelConfianza = 0.8;
        this.tendencia = "ESTABLE";
        this.factorEstacional = 1.0;
    }
    
    /**
     * Constructor simplificado sin intervalos de confianza.
     * Los intervalos se calculan automaticamente con un margen del 20%.
     * 
     * @param producto el producto a predecir
     * @param fechaPrediccion fecha futura para la prediccion
     * @param demandaEstimada cantidad estimada de unidades
     */
    public PrediccionDemanda(Producto producto, LocalDate fechaPrediccion, int demandaEstimada) {
        this(producto, fechaPrediccion, demandaEstimada,
             (int) (demandaEstimada * 0.8), (int) (demandaEstimada * 1.2));
    }
    
    /**
     * Obtiene el producto de la prediccion.
     * 
     * @return el producto
     */
    public Producto getProducto() {
        return producto;
    }
    
    /**
     * Establece el producto de la prediccion.
     * 
     * @param producto el nuevo producto
     */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    /**
     * Obtiene la fecha de prediccion.
     * 
     * @return la fecha futura predicha
     */
    public LocalDate getFechaPrediccion() {
        return fechaPrediccion;
    }
    
    /**
     * Establece la fecha de prediccion.
     * 
     * @param fechaPrediccion la nueva fecha
     */
    public void setFechaPrediccion(LocalDate fechaPrediccion) {
        this.fechaPrediccion = fechaPrediccion;
    }
    
    /**
     * Obtiene la demanda estimada.
     * 
     * @return cantidad estimada de unidades
     */
    public int getDemandaEstimada() {
        return demandaEstimada;
    }
    
    /**
     * Establece la demanda estimada.
     * 
     * @param demandaEstimada la nueva cantidad estimada
     */
    public void setDemandaEstimada(int demandaEstimada) {
        this.demandaEstimada = demandaEstimada;
    }
    
    /**
     * Obtiene el limite inferior del intervalo de confianza.
     * 
     * @return limite inferior
     */
    public int getLimiteInferior() {
        return limiteInferior;
    }
    
    /**
     * Establece el limite inferior del intervalo de confianza.
     * 
     * @param limiteInferior el nuevo limite inferior
     */
    public void setLimiteInferior(int limiteInferior) {
        this.limiteInferior = limiteInferior;
    }
    
    /**
     * Obtiene el limite superior del intervalo de confianza.
     * 
     * @return limite superior
     */
    public int getLimiteSuperior() {
        return limiteSuperior;
    }
    
    /**
     * Establece el limite superior del intervalo de confianza.
     * 
     * @param limiteSuperior el nuevo limite superior
     */
    public void setLimiteSuperior(int limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }
    
    /**
     * Obtiene el nivel de confianza de la prediccion.
     * 
     * @return nivel de confianza entre 0.0 y 1.0
     */
    public double getNivelConfianza() {
        return nivelConfianza;
    }
    
    /**
     * Establece el nivel de confianza de la prediccion.
     * 
     * @param nivelConfianza el nuevo nivel de confianza (0.0 a 1.0)
     */
    public void setNivelConfianza(double nivelConfianza) {
        this.nivelConfianza = Math.max(0.0, Math.min(1.0, nivelConfianza));
    }
    
    /**
     * Obtiene la tendencia detectada.
     * 
     * @return tendencia (CRECIENTE, DECRECIENTE, ESTABLE)
     */
    public String getTendencia() {
        return tendencia;
    }
    
    /**
     * Establece la tendencia detectada.
     * 
     * @param tendencia la nueva tendencia
     */
    public void setTendencia(String tendencia) {
        this.tendencia = tendencia;
    }
    
    /**
     * Obtiene el factor estacional aplicado.
     * 
     * @return factor estacional (1.0 = sin ajuste)
     */
    public double getFactorEstacional() {
        return factorEstacional;
    }
    
    /**
     * Establece el factor estacional.
     * 
     * @param factorEstacional el nuevo factor
     */
    public void setFactorEstacional(double factorEstacional) {
        this.factorEstacional = factorEstacional;
    }
    
    /**
     * Obtiene la descripcion de factores considerados.
     * 
     * @return descripcion de factores
     */
    public String getFactoresConsiderados() {
        return factoresConsiderados;
    }
    
    /**
     * Establece la descripcion de factores considerados.
     * 
     * @param factoresConsiderados la nueva descripcion
     */
    public void setFactoresConsiderados(String factoresConsiderados) {
        this.factoresConsiderados = factoresConsiderados;
    }
    
    /**
     * Obtiene la fecha de generacion de la prediccion.
     * 
     * @return fecha de generacion
     */
    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }
    
    /**
     * Establece la fecha de generacion.
     * 
     * @param fechaGeneracion la nueva fecha
     */
    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
    
    /**
     * Calcula el rango del intervalo de confianza.
     * 
     * @return diferencia entre limite superior e inferior
     */
    public int getRangoConfianza() {
        return limiteSuperior - limiteInferior;
    }
    
    /**
     * Calcula el porcentaje de variabilidad.
     * 
     * @return porcentaje de variabilidad respecto a la demanda estimada
     */
    public double getPorcentajeVariabilidad() {
        if (demandaEstimada == 0) return 0.0;
        return ((double) getRangoConfianza() / demandaEstimada) * 100;
    }
    
    /**
     * Verifica si la prediccion indica necesidad de reabastecimiento.
     * Compara la demanda estimada con el stock actual del producto.
     * 
     * @return true si se requiere reabastecimiento
     */
    public boolean requiereReabastecimiento() {
        if (producto == null) return false;
        return producto.getStock() < demandaEstimada;
    }
    
    /**
     * Calcula la cantidad sugerida para reabastecer.
     * Considera el limite superior del intervalo mas un margen de seguridad.
     * 
     * @return cantidad sugerida de reabastecimiento
     */
    public int getCantidadSugeridaReabastecimiento() {
        if (producto == null) return 0;
        int stockActual = producto.getStock();
        int necesario = limiteSuperior - stockActual + producto.getStockMinimo();
        return Math.max(0, necesario);
    }
    
    /**
     * Obtiene el nivel de confianza como porcentaje.
     * 
     * @return nivel de confianza en porcentaje (0-100)
     */
    public int getNivelConfianzaPorcentaje() {
        return (int) (nivelConfianza * 100);
    }
    
    @Override
    public String toString() {
        return String.format(
            "Prediccion[%s, fecha=%s, demanda=%d, confianza=%.0f%%]",
            producto != null ? producto.getNombre() : "null",
            fechaPrediccion,
            demandaEstimada,
            nivelConfianza * 100
        );
    }
}