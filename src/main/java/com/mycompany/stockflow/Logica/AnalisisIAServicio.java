/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.*;
import com.mycompany.stockflow.Persistencia.AnalisisRepositorio;
import com.mycompany.stockflow.utils.DeepSeekAPIClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servicio de Análisis con Inteligencia Artificial.
 * 
 * <p>Integra capacidades de análisis de IA (DeepSeek API) con generación 
 * de gráficas y persistencia de resultados. Coordina el flujo completo desde 
 * la consulta a la IA hasta el almacenamiento del análisis generado.</p>
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Generación de análisis completos combinando IA y datos reales</li>
 *   <li>Análisis específicos de ventas e inventario</li>
 *   <li>Extracción automática de métricas y recomendaciones</li>
 *   <li>Generación de gráficas personalizadas según tipo de análisis</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>{@code
 * AnalisisIAServicio servicio = new AnalisisIAServicio();
 * LocalDateTime inicio = LocalDateTime.now().minusMonths(1);
 * LocalDateTime fin = LocalDateTime.now();
 * 
 * ResultadoAnalisisIA resultado = servicio.generarAnalisisCompleto(
 *     "VENTAS", inicio, fin
 * );
 * 
 * System.out.println(resultado.getAnalisisTexto());
 * System.out.println("Métricas: " + resultado.getMetricas());
 * }</pre>
 * 
 * @author Equipo StockFlow
 * @version 1.0
 * @since 2025
 * 
 * @see DeepSeekAPIClient
 * @see DatosGraficaServicio
 * @see ResultadoAnalisisIA
 */
public class AnalisisIAServicio {
    
    /**
     * Cliente para comunicación con la API de DeepSeek.
     */
    private final DeepSeekAPIClient deepSeekClient;
    
    /**
     * Servicio para generación de datos de gráficas.
     */
    private final DatosGraficaServicio graficaServicio;
    
    /**
     * Repositorio para persistencia de análisis.
     */
    private final AnalisisRepositorio analisisRepositorio;
    
    /**
     * Servicio de gestión de ventas.
     */
    private final VentaServicio ventaServicio;
    
    /**
     * Servicio de gestión de productos.
     */
    private final ProductoServicio productoServicio;
    
    /**
     * Servicio de gestión de inventario.
     */
    private final InventarioServicio inventarioServicio;
    
    /**
     * Constructor por defecto.
     * <p>Inicializa todas las dependencias necesarias para el servicio.</p>
     */
    public AnalisisIAServicio() {
        this.deepSeekClient = new DeepSeekAPIClient();
        this.graficaServicio = new DatosGraficaServicio();
        this.analisisRepositorio = AnalisisRepositorio.getInstance();
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
        this.inventarioServicio = new InventarioServicio();
    }
    
    /**
     * Genera un análisis completo combinando IA y gráficas.
     * 
     * <p>Este método coordina todo el proceso de análisis:</p>
     * <ol>
     *   <li>Prepara el contexto con datos reales del negocio</li>
     *   <li>Consulta a la API de DeepSeek</li>
     *   <li>Genera gráficas correspondientes</li>
     *   <li>Extrae métricas y recomendaciones</li>
     *   <li>Persiste el resultado</li>
     * </ol>
     * 
     * @param tipoAnalisis Tipo de análisis a realizar: "VENTAS", "INVENTARIO" o "COMPLETO"
     * @param fechaInicio Fecha de inicio del período a analizar
     * @param fechaFin Fecha de fin del período a analizar
     * @return Resultado completo del análisis con texto, gráficas, métricas y recomendaciones
     * @throws RuntimeException Si ocurre algún error durante la generación del análisis
     * 
     * @see #prepararContextoParaIA(String, LocalDateTime, LocalDateTime)
     * @see #generarGraficasPorTipo(String, LocalDate, LocalDate)
     */
    public ResultadoAnalisisIA generarAnalisisCompleto(String tipoAnalisis, 
                                                     LocalDateTime fechaInicio, 
                                                     LocalDateTime fechaFin) {
        try {
            // Preparar contexto con datos reales
            String contexto = prepararContextoParaIA(tipoAnalisis, fechaInicio, fechaFin);
            
            // Consultar a DeepSeek
            String analisisTexto = deepSeekClient.enviarPrompt(contexto);
            
            // Generar gráficas correspondientes
            DatosGrafica datosGrafica = generarGraficasPorTipo(
                tipoAnalisis, 
                fechaInicio.toLocalDate(), 
                fechaFin.toLocalDate()
            );
            
            // Extraer métricas del análisis
            Map<String, Object> metricas = extraerMetricasDeAnalisis(analisisTexto);
            
            // Extraer recomendaciones
            Map<String, String> recomendaciones = extraerRecomendaciones(analisisTexto);
            
            // Crear resultado
            ResultadoAnalisisIA resultado = new ResultadoAnalisisIA(
                tipoAnalisis, 
                analisisTexto, 
                datosGrafica
            );
            resultado.setMetricas(metricas);
            resultado.setRecomendaciones(recomendaciones);
            
            // Persistir resultado
            analisisRepositorio.guardar(resultado);
            
            return resultado;
            
        } catch (Exception e) {
            throw new RuntimeException("Error generando analisis completo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Prepara el contexto estructurado para enviar a la IA.
     * 
     * <p>Construye un prompt detallado que incluye:</p>
     * <ul>
     *   <li>Datos reales del negocio (ventas, inventario)</li>
     *   <li>Instrucciones específicas según el tipo de análisis</li>
     *   <li>Formato esperado de respuesta</li>
     *   <li>Áreas de enfoque particulares</li>
     * </ul>
     * 
     * @param tipoAnalisis Tipo de análisis: "VENTAS", "INVENTARIO" o "COMPLETO"
     * @param inicio Fecha de inicio del período
     * @param fin Fecha de fin del período
     * @return Contexto estructurado como String para enviar a la IA
     * 
     * @see #generarContextoVentas(LocalDateTime, LocalDateTime)
     * @see #generarContextoInventario()
     */
    private String prepararContextoParaIA(String tipoAnalisis, LocalDateTime inicio, LocalDateTime fin) {
        StringBuilder contexto = new StringBuilder();
        
        contexto.append("Eres un analista empresarial experto en retail y gestion de inventarios. ");
        contexto.append("Analiza los siguientes datos y proporciona un analisis profesional:\n\n");
        
        switch (tipoAnalisis.toUpperCase()) {
            case "VENTAS":
                contexto.append(generarContextoVentas(inicio, fin));
                contexto.append("\n\nEnfocate en:\n");
                contexto.append("- Tendencias de ventas y crecimiento\n");
                contexto.append("- Estacionalidad y patrones\n");
                contexto.append("- Efectividad de estrategias de precio\n");
                contexto.append("- Proyecciones de ingresos\n");
                break;
                
            case "INVENTARIO":
                contexto.append(generarContextoInventario());
                contexto.append("\n\nEnfocate en:\n");
                contexto.append("- Niveles de stock y rotacion\n");
                contexto.append("- Productos con exceso o falta de inventario\n");
                contexto.append("- Optimizacion de costos de almacenamiento\n");
                contexto.append("- Riesgos de ruptura de stock\n");
                break;
                
            case "COMPLETO":
                contexto.append(generarContextoVentas(inicio, fin));
                contexto.append("\n");
                contexto.append(generarContextoInventario());
                contexto.append("\n\nEnfocate en:\n");
                contexto.append("- Relacion entre ventas e inventario\n");
                contexto.append("- Rentabilidad general del negocio\n");
                contexto.append("- Oportunidades de crecimiento\n");
                contexto.append("- Estrategias integradas de mejora\n");
                break;
                
            default:
                contexto.append("Analisis general del negocio.\n");
        }
        
        contexto.append("\n\nFormato de respuesta esperado:\n");
        contexto.append("1. RESUMEN EJECUTIVO: 2-3 lineas con lo mas importante\n");
        contexto.append("2. ANALISIS DETALLADO: Puntos especificos con datos\n");
        contexto.append("3. RECOMENDACIONES ACCIONABLES: Lista numerada con acciones concretas\n");
        contexto.append("4. METRICAS CLAVE: Destacar numeros importantes\n");
        contexto.append("5. ALERTAS: Posibles problemas u oportunidades\n");
        
        contexto.append("\nSe especifico, cuantitativo y orientado a la accion.");
        
        return contexto.toString();
    }
    
    /**
     * Genera contexto de ventas con datos reales del período especificado.
     * 
     * <p>Incluye:</p>
     * <ul>
     *   <li>Total de ventas del período</li>
     *   <li>Cantidad de transacciones</li>
     *   <li>Ticket promedio</li>
     *   <li>Top 3 productos más vendidos con sus montos</li>
     * </ul>
     * 
     * @param inicio Fecha de inicio del período
     * @param fin Fecha de fin del período
     * @return Contexto formateado con estadísticas de ventas
     */
    private String generarContextoVentas(LocalDateTime inicio, LocalDateTime fin) {
        StringBuilder contexto = new StringBuilder();
        contexto.append("=== DATOS DE VENTAS ===\n");
        contexto.append("Periodo analizado: ").append(inicio.toLocalDate()).append(" a ").append(fin.toLocalDate()).append("\n");
        
        try {
            java.util.List<Venta> ventas = ventaServicio.listarVentas();
            java.util.List<Venta> ventasFiltradas = ventas.stream()
                .filter(v -> !v.getFecha().isBefore(inicio) && !v.getFecha().isAfter(fin))
                .collect(java.util.stream.Collectors.toList());
            
            double totalVentas = ventasFiltradas.stream().mapToDouble(Venta::getTotal).sum();
            int cantidadVentas = ventasFiltradas.size();
            
            contexto.append("Total ventas periodo: $").append(String.format("%,.2f", totalVentas)).append("\n");
            contexto.append("Cantidad de transacciones: ").append(cantidadVentas).append("\n");
            contexto.append("Ticket promedio: $").append(cantidadVentas > 0 ? String.format("%,.2f", totalVentas / cantidadVentas) : "0.00").append("\n");
            
            Map<String, Double> ventasPorProducto = new HashMap<>();
            for (Venta venta : ventasFiltradas) {
                if (venta.getDetalles() != null) {
                    for (DetalleVenta detalle : venta.getDetalles()) {
                        if (detalle.getProducto() != null) {
                            String nombre = detalle.getProducto().getNombre();
                            ventasPorProducto.merge(nombre, detalle.getSubtotal(), Double::sum);
                        }
                    }
                }
            }
            
            if (!ventasPorProducto.isEmpty()) {
                contexto.append("\nTop 3 productos mas vendidos:\n");
                ventasPorProducto.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(3)
                    .forEach(entry -> contexto.append("- ")
                        .append(entry.getKey()).append(": $")
                        .append(String.format("%,.2f", entry.getValue())).append("\n"));
            }
            
        } catch (Exception e) {
            contexto.append("Error obteniendo datos de ventas: ").append(e.getMessage()).append("\n");
        }
        
        return contexto.toString();
    }
    
    /**
     * Genera contexto de inventario con datos actuales del stock.
     * 
     * <p>Incluye:</p>
     * <ul>
     *   <li>Total de productos en inventario</li>
     *   <li>Productos con stock crítico</li>
     *   <li>Valor total del inventario (precio de venta y costo)</li>
     *   <li>Margen potencial</li>
     *   <li>Lista de productos con stock crítico (hasta 5)</li>
     *   <li>Lista de productos con posible exceso de stock (hasta 3)</li>
     * </ul>
     * 
     * @return Contexto formateado con estadísticas de inventario
     */
    private String generarContextoInventario() {
        StringBuilder contexto = new StringBuilder();
        contexto.append("=== DATOS DE INVENTARIO ===\n");
        
        try {
            java.util.List<Producto> productos = productoServicio.listarProductos();
            java.util.List<Producto> productosCriticos = inventarioServicio.obtenerProductosStockBajo();
            
            contexto.append("Total productos en inventario: ").append(productos.size()).append("\n");
            contexto.append("Productos con stock critico: ").append(productosCriticos.size()).append("\n");
            
            double valorTotalVenta = productos.stream()
                .mapToDouble(p -> p.getPrecioVenta() * p.getStock())
                .sum();
            
            double valorTotalCosto = productos.stream()
                .mapToDouble(p -> p.getPrecioCompra() * p.getStock())
                .sum();
            
            contexto.append("Valor total inventario (venta): $").append(String.format("%,.2f", valorTotalVenta)).append("\n");
            contexto.append("Valor total inventario (costo): $").append(String.format("%,.2f", valorTotalCosto)).append("\n");
            contexto.append("Margen potencial: $").append(String.format("%,.2f", valorTotalVenta - valorTotalCosto)).append("\n");
            
            if (!productosCriticos.isEmpty()) {
                contexto.append("\nProductos con stock critico:\n");
                productosCriticos.stream()
                    .limit(5)
                    .forEach(p -> contexto.append("- ")
                        .append(p.getNombre()).append(": ")
                        .append(p.getStock()).append(" unidades (minimo: ")
                        .append(p.getStockMinimo()).append(")\n"));
            }
            
            java.util.List<Producto> excesoStock = productos.stream()
                .filter(p -> p.getStock() > p.getStockMinimo() * 3)
                .collect(java.util.stream.Collectors.toList());
            
            if (!excesoStock.isEmpty()) {
                contexto.append("\nProductos con posible exceso de stock:\n");
                excesoStock.stream()
                    .limit(3)
                    .forEach(p -> contexto.append("- ")
                        .append(p.getNombre()).append(": ")
                        .append(p.getStock()).append(" unidades\n"));
            }
            
        } catch (Exception e) {
            contexto.append("Error obteniendo datos de inventario: ").append(e.getMessage()).append("\n");
        }
        
        return contexto.toString();
    }
    
    /**
     * Genera gráficas específicas según el tipo de análisis solicitado.
     * 
     * <p>Mapeo de tipos de análisis a gráficas:</p>
     * <ul>
     *   <li><strong>VENTAS:</strong> Gráfica de tendencia de ventas</li>
     *   <li><strong>INVENTARIO:</strong> Gráfica de inventario crítico</li>
     *   <li><strong>COMPLETO:</strong> Gráfica de productos más rentables</li>
     *   <li><strong>Otro:</strong> Top 5 productos más vendidos</li>
     * </ul>
     * 
     * @param tipoAnalisis Tipo de análisis: "VENTAS", "INVENTARIO", "COMPLETO"
     * @param inicio Fecha de inicio para la gráfica
     * @param fin Fecha de fin para la gráfica
     * @return Objeto DatosGrafica con la información para visualización
     * 
     * @see DatosGraficaServicio
     */
    private DatosGrafica generarGraficasPorTipo(String tipoAnalisis, LocalDate inicio, LocalDate fin) {
        switch (tipoAnalisis.toUpperCase()) {
            case "VENTAS":
                return graficaServicio.generarGraficaTendenciaVentas(inicio, fin);
                
            case "INVENTARIO":
                return graficaServicio.generarGraficaInventarioCritico();
                
            case "COMPLETO":
                return graficaServicio.generarGraficaProductosMasRentables(10);
                
            default:
                return graficaServicio.generarGraficaTopProductos(5);
        }
    }
    
    /**
     * Extrae métricas numéricas del texto de análisis generado por la IA.
     * 
     * <p>Identifica y extrae automáticamente:</p>
     * <ul>
     *   <li><strong>Porcentajes:</strong> Busca patrones como "25%", "15.5%" (hasta 5)</li>
     *   <li><strong>Montos:</strong> Busca patrones como "$1,250.00", "$50" (hasta 5)</li>
     *   <li><strong>Tendencias:</strong> Identifica palabras clave para clasificar como 
     *       POSITIVA, NEGATIVA o ESTABLE</li>
     *   <li><strong>Total recomendaciones:</strong> Cuenta líneas con formato de lista</li>
     * </ul>
     * 
     * <p>Las métricas se almacenan con claves como:</p>
     * <ul>
     *   <li>{@code porcentaje_1}, {@code porcentaje_2}, ...</li>
     *   <li>{@code monto_1}, {@code monto_2}, ...</li>
     *   <li>{@code tendencia}</li>
     *   <li>{@code total_recomendaciones}</li>
     * </ul>
     * 
     * @param analisisTexto Texto completo del análisis de la IA
     * @return Mapa con métricas extraídas (clave-valor)
     */
    private Map<String, Object> extraerMetricasDeAnalisis(String analisisTexto) {
        Map<String, Object> metricas = new HashMap<>();
        
        // Extraer porcentajes
        Pattern porcentajePattern = Pattern.compile("(\\d+(?:\\.\\d+)?)%");
        Matcher porcentajeMatcher = porcentajePattern.matcher(analisisTexto);
        
        int countPorcentajes = 0;
        while (porcentajeMatcher.find() && countPorcentajes < 5) {
            String clave = "porcentaje_" + (countPorcentajes + 1);
            metricas.put(clave, Double.parseDouble(porcentajeMatcher.group(1)));
            countPorcentajes++;
        }
        
        // Extraer montos monetarios
        Pattern dineroPattern = Pattern.compile("\\$([\\d,]+(?:\\.\\d{2})?)");
        Matcher dineroMatcher = dineroPattern.matcher(analisisTexto);
        
        int countDinero = 0;
        while (dineroMatcher.find() && countDinero < 5) {
            String clave = "monto_" + (countDinero + 1);
            String valor = dineroMatcher.group(1).replace(",", "");
            metricas.put(clave, Double.parseDouble(valor));
            countDinero++;
        }
        
        // Detectar tendencia general
        if (analisisTexto.toLowerCase().contains("crecimiento") || 
            analisisTexto.toLowerCase().contains("aumento") || 
            analisisTexto.toLowerCase().contains("incremento")) {
            metricas.put("tendencia", "POSITIVA");
        } else if (analisisTexto.toLowerCase().contains("disminucion") || 
                   analisisTexto.toLowerCase().contains("decrecimiento") || 
                   analisisTexto.toLowerCase().contains("baja")) {
            metricas.put("tendencia", "NEGATIVA");
        } else {
            metricas.put("tendencia", "ESTABLE");
        }
        
        // Contar recomendaciones
        String[] lineas = analisisTexto.split("\n");
        long countRecomendaciones = java.util.Arrays.stream(lineas)
            .filter(linea -> linea.trim().matches("^\\d+\\.|^-|^•|^\\*|recomendacion|sugerencia|accion"))
            .count();
        metricas.put("total_recomendaciones", countRecomendaciones);
        
        return metricas;
    }
    
    /**
     * Extrae recomendaciones estructuradas del análisis de IA.
     * 
     * <p>Identifica líneas que contienen recomendaciones basándose en:</p>
     * <ul>
     *   <li>Numeración: {@code 1., 2., 3.}</li>
     *   <li>Viñetas: {@code -, •, *}</li>
     *   <li>Palabras clave: "recomendación", "sugerencia", "acción"</li>
     * </ul>
     * 
     * <p>Las recomendaciones extraídas se formatean limpiando los prefijos 
     * y se almacenan con títulos como "Recomendacion 1", "Recomendacion 2", etc.</p>
     * 
     * <p>Si no se encuentran recomendaciones, retorna un mensaje por defecto 
     * indicando que se revise el análisis completo.</p>
     * 
     * @param analisisTexto Texto completo del análisis de la IA
     * @return Mapa de recomendaciones con título como clave y descripción como valor
     */
    private Map<String, String> extraerRecomendaciones(String analisisTexto) {
        Map<String, String> recomendaciones = new HashMap<>();
        
        String[] lineas = analisisTexto.split("\n");
        int numeroRecomendacion = 1;
        
        for (String linea : lineas) {
            linea = linea.trim();
            
            // Detectar líneas que parecen recomendaciones
            if (linea.matches("^\\d+\\.\\s+.+") || 
                linea.matches("^-\\s+.+") || 
                linea.matches("^•\\s+.+") ||
                linea.matches("^\\*\\s+.+") ||
                linea.toLowerCase().contains("recomendacion") ||
                linea.toLowerCase().contains("sugerencia") ||
                linea.toLowerCase().contains("accion")) {
                
                String titulo = "Recomendacion " + numeroRecomendacion;
                String descripcion = linea.replaceAll("^\\d+\\.\\s*", "")
                                         .replaceAll("^[-•*]\\s*", "")
                                         .trim();
                
                // Solo agregar si tiene contenido significativo
                if (descripcion.length() > 10) {
                    recomendaciones.put(titulo, descripcion);
                    numeroRecomendacion++;
                }
            }
        }
        
        // Si no se encontraron recomendaciones, agregar mensaje por defecto
        if (recomendaciones.isEmpty()) {
            recomendaciones.put("Analisis General", 
                "Revise el analisis completo para recomendaciones especificas");
        }
        
        return recomendaciones;
    }
    
    /**
     * Método simplificado para análisis rápido del último mes.
     * 
     * <p>Genera un análisis del período de los últimos 30 días 
     * sin necesidad de especificar fechas manualmente.</p>
     * 
     * <p>Este método es útil para:</p>
     * <ul>
     *   <li>Dashboard con análisis actual</li>
     *   <li>Reportes rápidos mensuales</li>
     *   <li>Consultas ad-hoc del usuario</li>
     * </ul>
     * 
     * @param tipoAnalisis Tipo de análisis: "VENTAS", "INVENTARIO" o "COMPLETO"
     * @return Resultado completo del análisis del último mes
     * 
     * @see #generarAnalisisCompleto(String, LocalDateTime, LocalDateTime)
     */
    public ResultadoAnalisisIA generarAnalisisRapido(String tipoAnalisis) {
        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime inicio = fin.minusMonths(1);
        
        return generarAnalisisCompleto(tipoAnalisis, inicio, fin);
    }
}