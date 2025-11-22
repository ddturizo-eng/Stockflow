/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de generacion de notificaciones y alertas inteligentes.
 * 
 * <p>Este servicio analiza el estado del negocio y genera notificaciones
 * automaticas basadas en reglas de negocio y analisis de datos. Las
 * notificaciones son priorizadas y clasificadas por tipo e impacto.</p>
 * 
 * <p>Tipos de notificaciones generadas:</p>
 * <ul>
 *   <li><strong>Alertas criticas:</strong> Stock agotado, anomalias graves</li>
 *   <li><strong>Oportunidades:</strong> Productos para promocion, cross-selling</li>
 *   <li><strong>Advertencias:</strong> Stock bajo, tendencias negativas</li>
 *   <li><strong>Recomendaciones:</strong> Optimizaciones y mejoras</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 */
public class NotificacionIAServicio {
    
    private final ProductoServicio productoServicio;
    private final VentaServicio ventaServicio;
    private final PrediccionServicio prediccionServicio;
    private final AnaliticaAvanzadaServicio analiticaServicio;
    
    /**
     * Constructor por defecto.
     */
    public NotificacionIAServicio() {
        this.productoServicio = new ProductoServicio();
        this.ventaServicio = new VentaServicio();
        this.prediccionServicio = new PrediccionServicio();
        this.analiticaServicio = new AnaliticaAvanzadaServicio();
    }
    
    /**
     * Genera todas las notificaciones actuales del sistema.
     * 
     * <p>Analiza multiples aspectos del negocio y consolida
     * todas las notificaciones relevantes en una sola lista
     * ordenada por prioridad.</p>
     * 
     * @return lista de insights ordenados por importancia
     */
    public List<InsightIA> generarNotificacionesActuales() {
        List<InsightIA> notificaciones = new ArrayList<>();
        
        notificaciones.addAll(generarAlertasStockCritico());
        notificaciones.addAll(generarAlertasPrediccion());
        notificaciones.addAll(generarOportunidadesVenta());
        notificaciones.addAll(generarRecomendacionesInventario());
        notificaciones.addAll(detectarAnomalias());
        
        return notificaciones.stream()
            .sorted(Comparator.comparingInt(InsightIA::getPuntuacionImportancia).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Genera alertas de stock critico y agotado.
     * 
     * @return lista de alertas de stock
     */
    private List<InsightIA> generarAlertasStockCritico() {
        List<InsightIA> alertas = new ArrayList<>();
        
        try {
            List<Producto> productos = productoServicio.listarProductos();
            
            for (Producto producto : productos) {
                if (producto.getStock() == 0) {
                    InsightIA alerta = new InsightIA(
                        InsightIA.TipoInsight.ALERT,
                        InsightIA.NivelPrioridad.CRITICAL,
                        "Producto Agotado: " + producto.getNombre(),
                        "Stock completamente agotado. Posibles perdidas de venta."
                    );
                    alerta.setProductoId(producto.getCodigo());
                    alerta.setAccionRecomendada("Realizar pedido urgente al proveedor");
                    alerta.setImpacto("ALTO");
                    alerta.agregarMetrica("stockActual", 0);
                    alerta.agregarMetrica("stockMinimo", producto.getStockMinimo());
                    alertas.add(alerta);
                    
                } else if (producto.tieneStockBajo()) {
                    InsightIA.NivelPrioridad prioridad = 
                        producto.getStock() <= producto.getStockMinimo() / 2 ?
                        InsightIA.NivelPrioridad.HIGH : InsightIA.NivelPrioridad.MEDIUM;
                    
                    InsightIA alerta = new InsightIA(
                        InsightIA.TipoInsight.WARNING,
                        prioridad,
                        "Stock Bajo: " + producto.getNombre(),
                        String.format("Stock actual: %d unidades (minimo: %d)",
                            producto.getStock(), producto.getStockMinimo())
                    );
                    alerta.setProductoId(producto.getCodigo());
                    alerta.setAccionRecomendada("Planificar reabastecimiento");
                    alerta.setImpacto("MEDIO");
                    alerta.agregarMetrica("stockActual", producto.getStock());
                    alerta.agregarMetrica("stockMinimo", producto.getStockMinimo());
                    alertas.add(alerta);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error generando alertas stock: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Genera alertas basadas en predicciones de demanda.
     * 
     * @return lista de alertas predictivas
     */
    private List<InsightIA> generarAlertasPrediccion() {
        List<InsightIA> alertas = new ArrayList<>();
        
        try {
            List<Producto> productosEnRiesgo = prediccionServicio.identificarProductosEnRiesgo(30);
            
            for (Producto producto : productosEnRiesgo) {
                if (producto.tieneStockBajo()) continue;
                
                PrediccionDemanda prediccion = prediccionServicio.predecirDemandaMejorada(
                    producto, 30
                );
                
                InsightIA alerta = new InsightIA(
                    InsightIA.TipoInsight.WARNING,
                    InsightIA.NivelPrioridad.MEDIUM,
                    "Alerta Predictiva: " + producto.getNombre(),
                    String.format("La IA predice agotamiento en 30 dias. Demanda estimada: %d unidades",
                        prediccion.getDemandaEstimada())
                );
                alerta.setProductoId(producto.getCodigo());
                alerta.setAccionRecomendada(
                    String.format("Reabastecer con %d unidades", 
                        prediccion.getCantidadSugeridaReabastecimiento())
                );
                alerta.setImpacto("MEDIO");
                alerta.agregarMetrica("demandaEstimada", prediccion.getDemandaEstimada());
                alerta.agregarMetrica("stockActual", producto.getStock());
                alerta.agregarMetrica("confianza", prediccion.getNivelConfianzaPorcentaje());
                alertas.add(alerta);
            }
            
        } catch (Exception e) {
            System.err.println("Error generando alertas prediccion: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Identifica oportunidades de venta y promocion.
     * 
     * @return lista de oportunidades
     */
    private List<InsightIA> generarOportunidadesVenta() {
        List<InsightIA> oportunidades = new ArrayList<>();
        
        try {
            List<Producto> productos = productoServicio.listarProductos();
            List<Venta> ventas = ventaServicio.listarVentas();
            
            List<Producto> bajaDemanda = analiticaServicio.obtenerProductosBajoMovimiento(
                productos, ventas
            );
            
            for (Producto producto : bajaDemanda) {
                if (producto.getStock() > producto.getStockMinimo() * 2) {
                    double descuentoSugerido = 15 + (producto.getStock() / (double) producto.getStockMinimo()) * 5;
                    descuentoSugerido = Math.min(30, descuentoSugerido);
                    
                    InsightIA oportunidad = new InsightIA(
                        InsightIA.TipoInsight.OPPORTUNITY,
                        InsightIA.NivelPrioridad.LOW,
                        "Promocion Sugerida: " + producto.getNombre(),
                        String.format("Producto con exceso de stock. Aplicar descuento de %.0f%% para rotar inventario",
                            descuentoSugerido)
                    );
                    oportunidad.setProductoId(producto.getCodigo());
                    oportunidad.setAccionRecomendada("Crear promocion especial");
                    oportunidad.setImpacto("BAJO");
                    oportunidad.agregarMetrica("stockExceso", producto.getStock());
                    oportunidad.agregarMetrica("descuentoSugerido", descuentoSugerido);
                    oportunidades.add(oportunidad);
                }
            }
            
            Map<String, List<String>> complementarios = 
                analiticaServicio.identificarProductosComplementarios(ventas);
            
            if (!complementarios.isEmpty()) {
                Map.Entry<String, List<String>> mejorPar = complementarios.entrySet().stream()
                    .max(Comparator.comparingInt(e -> e.getValue().size()))
                    .orElse(null);
                
                if (mejorPar != null && mejorPar.getValue().size() >= 2) {
                    InsightIA oportunidad = new InsightIA(
                        InsightIA.TipoInsight.OPPORTUNITY,
                        InsightIA.NivelPrioridad.LOW,
                        "Cross-Selling Detectado",
                        "Productos frecuentemente comprados juntos identificados"
                    );
                    oportunidad.setAccionRecomendada("Crear paquete promocional");
                    oportunidad.setImpacto("MEDIO");
                    oportunidad.agregarMetrica("productosRelacionados", mejorPar.getValue().size());
                    oportunidades.add(oportunidad);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error generando oportunidades: " + e.getMessage());
        }
        
        return oportunidades;
    }
    
    /**
     * Genera recomendaciones de optimizacion de inventario.
     * 
     * @return lista de recomendaciones
     */
    private List<InsightIA> generarRecomendacionesInventario() {
        List<InsightIA> recomendaciones = new ArrayList<>();
        
        try {
            List<Producto> productos = productoServicio.listarProductos();
            List<Venta> ventas = ventaServicio.listarVentas();
            
            Map<String, Double> roi = analiticaServicio.calcularROIProductos(productos, ventas);
            
            List<Map.Entry<String, Double>> bajosROI = roi.entrySet().stream()
                .filter(e -> e.getValue() < 10)
                .sorted(Map.Entry.comparingByValue())
                .limit(3)
                .collect(Collectors.toList());
            
            for (Map.Entry<String, Double> entry : bajosROI) {
                try {
                    Producto producto = productoServicio.buscarProducto(entry.getKey());
                    
                    InsightIA recomendacion = new InsightIA(
                        InsightIA.TipoInsight.RECOMMENDATION,
                        InsightIA.NivelPrioridad.MEDIUM,
                        "Revisar Rentabilidad: " + producto.getNombre(),
                        String.format("ROI bajo detectado: %.1f%%. Considerar ajuste de precio o descontinuar",
                            entry.getValue())
                    );
                    recomendacion.setProductoId(producto.getCodigo());
                    recomendacion.setAccionRecomendada("Analizar estructura de costos y precios");
                    recomendacion.setImpacto("MEDIO");
                    recomendacion.agregarMetrica("roi", entry.getValue());
                    recomendaciones.add(recomendacion);
                    
                } catch (Exception e) {
                    continue;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error generando recomendaciones: " + e.getMessage());
        }
        
        return recomendaciones;
    }
    
    /**
     * Detecta anomalias en patrones de ventas.
     * 
     * @return lista de anomalias detectadas
     */
    private List<InsightIA> detectarAnomalias() {
        List<InsightIA> anomalias = new ArrayList<>();
        
        try {
            List<Producto> productos = productoServicio.listarProductos();
            List<Venta> ventas = ventaServicio.listarVentas();
            
            for (Producto producto : productos) {
                List<LocalDateTime> anomaliasDetectadas = prediccionServicio.detectarAnomalias(
                    producto, ventas
                ).stream()
                .map(fecha -> fecha.atStartOfDay())
                .collect(Collectors.toList());
                
                if (!anomaliasDetectadas.isEmpty()) {
                    InsightIA anomalia = new InsightIA(
                        InsightIA.TipoInsight.SPIKE,
                        InsightIA.NivelPrioridad.LOW,
                        "Pico de Ventas: " + producto.getNombre(),
                        String.format("%d picos de venta detectados en ultimos 90 dias",
                            anomaliasDetectadas.size())
                    );
                    anomalia.setProductoId(producto.getCodigo());
                    anomalia.setAccionRecomendada("Investigar causa del pico para replicar exito");
                    anomalia.setImpacto("BAJO");
                    anomalia.agregarMetrica("picosDetectados", anomaliasDetectadas.size());
                    anomalias.add(anomalia);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error detectando anomalias: " + e.getMessage());
        }
        
        return anomalias;
    }
    
    /**
     * Filtra notificaciones por tipo.
     * 
     * @param notificaciones lista completa
     * @param tipo tipo a filtrar
     * @return lista filtrada
     */
    public List<InsightIA> filtrarPorTipo(List<InsightIA> notificaciones, 
                                         InsightIA.TipoInsight tipo) {
        return notificaciones.stream()
            .filter(n -> n.getTipo() == tipo)
            .collect(Collectors.toList());
    }
    
    /**
     * Filtra notificaciones por prioridad.
     * 
     * @param notificaciones lista completa
     * @param prioridad prioridad minima
     * @return lista filtrada
     */
    public List<InsightIA> filtrarPorPrioridad(List<InsightIA> notificaciones,
                                              InsightIA.NivelPrioridad prioridad) {
        return notificaciones.stream()
            .filter(n -> n.getPrioridad().ordinal() <= prioridad.ordinal())
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene conteo de notificaciones por tipo.
     * 
     * @param notificaciones lista de notificaciones
     * @return mapa con conteos
     */
    public Map<String, Integer> obtenerConteosPorTipo(List<InsightIA> notificaciones) {
        Map<String, Integer> conteos = new HashMap<>();
        
        for (InsightIA notif : notificaciones) {
            String tipo = notif.getTipoString();
            conteos.merge(tipo, 1, Integer::sum);
        }
        
        return conteos;
    }
    
    /**
     * Genera sugerencias de acciones prioritarias.
     * 
     * @param limite numero maximo de acciones
     * @return lista de acciones sugeridas
     */
    public List<String> generarAccionesPrioritarias(int limite) {
        List<InsightIA> notificaciones = generarNotificacionesActuales();
        
        return notificaciones.stream()
            .filter(n -> n.getAccionRecomendada() != null && !n.getAccionRecomendada().isEmpty())
            .limit(limite)
            .map(InsightIA::getAccionRecomendada)
            .collect(Collectors.toList());
    }
}
