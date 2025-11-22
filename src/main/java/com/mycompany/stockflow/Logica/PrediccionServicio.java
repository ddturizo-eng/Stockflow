/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de predicciones mejorado con algoritmos avanzados.
 * 
 * <p>Este servicio implementa algoritmos de prediccion que consideran
 * tendencias, estacionalidad y variabilidad para generar predicciones
 * mas precisas que los promedios simples.</p>
 * 
 * <p>Algoritmos implementados:</p>
 * <ul>
 *   <li><strong>Regresion lineal simple:</strong> Calculo de tendencias</li>
 *   <li><strong>Ajuste estacional multiplicativo:</strong> Factores mensuales</li>
 *   <li><strong>Intervalos de confianza:</strong> Basados en desviacion estandar</li>
 *   <li><strong>Deteccion de anomalias:</strong> Valores atipicos</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 */
public class PrediccionServicio {
    
    private final VentaServicio ventaServicio;
    private final ProductoServicio productoServicio;
    
    /**
     * Constructor por defecto.
     */
    public PrediccionServicio() {
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
    }
    
    /**
     * Predice la demanda futura de un producto con algoritmo mejorado.
     * 
     * <p>Proceso de prediccion:</p>
     * <ol>
     *   <li>Obtener serie temporal de ventas historicas</li>
     *   <li>Calcular tendencia mediante regresion lineal</li>
     *   <li>Detectar y aplicar factor estacional</li>
     *   <li>Proyectar demanda futura</li>
     *   <li>Calcular intervalos de confianza</li>
     * </ol>
     * 
     * @param producto producto a predecir
     * @param diasProyeccion dias hacia el futuro
     * @return objeto PrediccionDemanda con todos los detalles
     */
    public PrediccionDemanda predecirDemandaMejorada(Producto producto, int diasProyeccion) {
        List<Venta> ventas = ventaServicio.listarVentas();
        LocalDate fechaPrediccion = LocalDate.now().plusDays(diasProyeccion);
        
        Map<LocalDate, Integer> serieTemporal = construirSerieTemporal(producto, ventas, 90);
        
        if (serieTemporal.isEmpty()) {
            return new PrediccionDemanda(producto, fechaPrediccion, 0, 0, 0);
        }
        
        DatosTendencia tendencia = calcularTendencia(serieTemporal);
        
        double factorEstacional = calcularFactorEstacional(producto, ventas, fechaPrediccion);
        
        double demandaBase = tendencia.predecir(diasProyeccion);
        int demandaAjustada = (int) Math.round(demandaBase * factorEstacional);
        
        double desviacion = calcularDesviacionEstandar(serieTemporal);
        int margen = (int) Math.ceil(desviacion * 1.5);
        
        int limiteInferior = Math.max(0, demandaAjustada - margen);
        int limiteSuperior = demandaAjustada + margen;
        
        PrediccionDemanda prediccion = new PrediccionDemanda(
            producto, fechaPrediccion, demandaAjustada, limiteInferior, limiteSuperior
        );
        
        prediccion.setTendencia(tendencia.getTipo());
        prediccion.setFactorEstacional(factorEstacional);
        prediccion.setNivelConfianza(calcularNivelConfianza(serieTemporal.size(), desviacion));
        prediccion.setFactoresConsiderados(
            String.format("Tendencia: %s, Factor estacional: %.2f, Dias historicos: %d",
                tendencia.getTipo(), factorEstacional, serieTemporal.size())
        );
        
        return prediccion;
    }
    
    /**
     * Construye una serie temporal de ventas diarias.
     * 
     * @param producto producto a analizar
     * @param ventas lista de ventas
     * @param diasHistorico dias hacia atras a considerar
     * @return mapa de fecha a cantidad vendida
     */
    private Map<LocalDate, Integer> construirSerieTemporal(Producto producto, 
                                                           List<Venta> ventas, 
                                                           int diasHistorico) {
        Map<LocalDate, Integer> serie = new TreeMap<>();
        LocalDate fechaInicio = LocalDate.now().minusDays(diasHistorico);
        
        for (Venta venta : ventas) {
            LocalDate fechaVenta = venta.getFecha().toLocalDate();
            if (fechaVenta.isBefore(fechaInicio)) continue;
            
            if (venta.getDetalles() != null) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    if (detalle.getProducto() != null && 
                        detalle.getProducto().getCodigo().equals(producto.getCodigo())) {
                        serie.merge(fechaVenta, detalle.getCantidad(), Integer::sum);
                    }
                }
            }
        }
        
        return serie;
    }
    
    /**
     * Calcula la tendencia mediante regresion lineal simple.
     * 
     * <p>Formula: y = a + bx, donde:</p>
     * <ul>
     *   <li>b = pendiente (tasa de cambio)</li>
     *   <li>a = intercepto (valor inicial)</li>
     * </ul>
     * 
     * @param serieTemporal serie temporal de ventas
     * @return datos de tendencia con pendiente e intercepto
     */
    private DatosTendencia calcularTendencia(Map<LocalDate, Integer> serieTemporal) {
        if (serieTemporal.size() < 2) {
            return new DatosTendencia(0, 0, "ESTABLE");
        }
        
        List<LocalDate> fechas = new ArrayList<>(serieTemporal.keySet());
        int n = fechas.size();
        
        double sumaX = 0, sumaY = 0, sumaXY = 0, sumaX2 = 0;
        
        for (int i = 0; i < n; i++) {
            double x = i;
            double y = serieTemporal.get(fechas.get(i));
            
            sumaX += x;
            sumaY += y;
            sumaXY += x * y;
            sumaX2 += x * x;
        }
        
        double pendiente = (n * sumaXY - sumaX * sumaY) / (n * sumaX2 - sumaX * sumaX);
        double intercepto = (sumaY - pendiente * sumaX) / n;
        
        String tipo = "ESTABLE";
        if (pendiente > 0.1) tipo = "CRECIENTE";
        else if (pendiente < -0.1) tipo = "DECRECIENTE";
        
        return new DatosTendencia(pendiente, intercepto, tipo);
    }
    
    /**
     * Calcula el factor estacional para un mes especifico.
     * 
     * <p>El factor estacional indica si en ese mes las ventas
     * tienden a ser mayores o menores que el promedio.</p>
     * 
     * @param producto producto a analizar
     * @param ventas lista de ventas
     * @param fecha fecha futura para la prediccion
     * @return factor estacional (1.0 = promedio, mayor 1.0 = temporada alta)
     */
    private double calcularFactorEstacional(Producto producto, 
                                           List<Venta> ventas, 
                                           LocalDate fecha) {
        Map<Integer, List<Integer>> ventasPorMes = new HashMap<>();
        
        for (Venta venta : ventas) {
            int mes = venta.getFecha().getMonthValue();
            
            if (venta.getDetalles() != null) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    if (detalle.getProducto() != null && 
                        detalle.getProducto().getCodigo().equals(producto.getCodigo())) {
                        
                        ventasPorMes.computeIfAbsent(mes, k -> new ArrayList<>())
                                   .add(detalle.getCantidad());
                    }
                }
            }
        }
        
        if (ventasPorMes.isEmpty()) return 1.0;
        
        double promedioGeneral = ventasPorMes.values().stream()
            .flatMap(List::stream)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(1.0);
        
        int mesFuturo = fecha.getMonthValue();
        double promedioMes = ventasPorMes.getOrDefault(mesFuturo, new ArrayList<>())
            .stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(promedioGeneral);
        
        if (promedioGeneral == 0) return 1.0;
        
        return promedioMes / promedioGeneral;
    }
    
    /**
     * Calcula la desviacion estandar de la serie temporal.
     * 
     * @param serieTemporal serie temporal de ventas
     * @return desviacion estandar
     */
    private double calcularDesviacionEstandar(Map<LocalDate, Integer> serieTemporal) {
        if (serieTemporal.isEmpty()) return 0;
        
        double media = serieTemporal.values().stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0);
        
        double sumaCuadrados = serieTemporal.values().stream()
            .mapToDouble(v -> Math.pow(v - media, 2))
            .sum();
        
        return Math.sqrt(sumaCuadrados / serieTemporal.size());
    }
    
    /**
     * Calcula el nivel de confianza basado en cantidad de datos.
     * 
     * @param cantidadDatos numero de puntos de datos
     * @param desviacion desviacion estandar
     * @return nivel de confianza entre 0.0 y 1.0
     */
    private double calcularNivelConfianza(int cantidadDatos, double desviacion) {
        double confianzaDatos = Math.min(1.0, cantidadDatos / 60.0);
        
        double confianzaEstabilidad = desviacion < 5 ? 0.9 : 
                                     desviacion < 10 ? 0.75 : 
                                     desviacion < 20 ? 0.6 : 0.4;
        
        return (confianzaDatos * 0.6 + confianzaEstabilidad * 0.4);
    }
    
    /**
     * Genera predicciones para multiples productos.
     * 
     * @param diasProyeccion dias hacia el futuro
     * @param limite numero maximo de productos a analizar
     * @return lista de predicciones ordenadas por demanda
     */
    public List<PrediccionDemanda> generarPrediccionesMultiples(int diasProyeccion, int limite) {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            
            return productos.stream()
                .limit(limite)
                .map(p -> predecirDemandaMejorada(p, diasProyeccion))
                .filter(pred -> pred.getDemandaEstimada() > 0)
                .sorted(Comparator.comparingInt(PrediccionDemanda::getDemandaEstimada).reversed())
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Identifica productos con alto riesgo de quiebre de stock.
     * 
     * @param diasProyeccion dias hacia el futuro a considerar
     * @return lista de productos en riesgo
     */
    public List<Producto> identificarProductosEnRiesgo(int diasProyeccion) {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            List<Producto> enRiesgo = new ArrayList<>();
            
            for (Producto producto : productos) {
                PrediccionDemanda prediccion = predecirDemandaMejorada(producto, diasProyeccion);
                
                if (prediccion.requiereReabastecimiento() || 
                    producto.getStock() < prediccion.getLimiteInferior()) {
                    enRiesgo.add(producto);
                }
            }
            
            return enRiesgo;
            
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Detecta anomalias en patrones de ventas.
     * 
     * @param producto producto a analizar
     * @param ventas lista de ventas
     * @return lista de fechas con anomalias detectadas
     */
    public List<LocalDate> detectarAnomalias(Producto producto, List<Venta> ventas) {
        Map<LocalDate, Integer> serieTemporal = construirSerieTemporal(producto, ventas, 90);
        
        if (serieTemporal.size() < 10) return new ArrayList<>();
        
        double media = serieTemporal.values().stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0);
        
        double desviacion = calcularDesviacionEstandar(serieTemporal);
        double umbral = media + (desviacion * 2);
        
        return serieTemporal.entrySet().stream()
            .filter(entry -> entry.getValue() > umbral)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    /**
     * Clase interna para almacenar datos de tendencia.
     */
    private static class DatosTendencia {
        private final double pendiente;
        private final double intercepto;
        private final String tipo;
        
        public DatosTendencia(double pendiente, double intercepto, String tipo) {
            this.pendiente = pendiente;
            this.intercepto = intercepto;
            this.tipo = tipo;
        }
        
        public double predecir(int diasFuturos) {
            return Math.max(0, intercepto + pendiente * diasFuturos);
        }
        
        public String getTipo() {
            return tipo;
        }
    }
}