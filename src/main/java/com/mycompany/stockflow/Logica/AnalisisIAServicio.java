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
 * Servicio que integra analisis de IA con generacion de graficas.
 * Coordino la consulta a DeepSeek, procesamiento de datos y persistencia.
 */
public class AnalisisIAServicio {
    
    private final DeepSeekAPIClient deepSeekClient;
    private final DatosGraficaServicio graficaServicio;
    private final AnalisisRepositorio analisisRepositorio;
    private final VentaServicio ventaServicio;
    private final ProductoServicio productoServicio;
    private final InventarioServicio inventarioServicio;
    
    public AnalisisIAServicio() {
        this.deepSeekClient = new DeepSeekAPIClient();
        this.graficaServicio = new DatosGraficaServicio();
        this.analisisRepositorio = AnalisisRepositorio.getInstance();
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
        this.inventarioServicio = new InventarioServicio();
    }
    
    /**
     * Genero analisis completo combinando IA y graficas.
     */
    public ResultadoAnalisisIA generarAnalisisCompleto(String tipoAnalisis, 
                                                     LocalDateTime fechaInicio, 
                                                     LocalDateTime fechaFin) {
        try {
            String contexto = prepararContextoParaIA(tipoAnalisis, fechaInicio, fechaFin);
            
            String analisisTexto = deepSeekClient.enviarPrompt(contexto);
            
            DatosGrafica datosGrafica = generarGraficasPorTipo(
                tipoAnalisis, 
                fechaInicio.toLocalDate(), 
                fechaFin.toLocalDate()
            );
            
            Map<String, Object> metricas = extraerMetricasDeAnalisis(analisisTexto);
            
            Map<String, String> recomendaciones = extraerRecomendaciones(analisisTexto);
            
            ResultadoAnalisisIA resultado = new ResultadoAnalisisIA(
                tipoAnalisis, 
                analisisTexto, 
                datosGrafica
            );
            resultado.setMetricas(metricas);
            resultado.setRecomendaciones(recomendaciones);
            
            analisisRepositorio.guardar(resultado);
            
            return resultado;
            
        } catch (Exception e) {
            throw new RuntimeException("Error generando analisis completo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Preparo contexto estructurado para la IA usando datos reales del negocio.
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
     * Genero contexto de ventas con datos reales del periodo especificado.
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
     * Genero contexto de inventario con datos actuales del stock.
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
     * Genero graficas especificas segun el tipo de analisis solicitado.
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
     * Extraigo metricas numericas del texto de analisis de la IA.
     */
    private Map<String, Object> extraerMetricasDeAnalisis(String analisisTexto) {
        Map<String, Object> metricas = new HashMap<>();
        
        Pattern porcentajePattern = Pattern.compile("(\\d+(?:\\.\\d+)?)%");
        Matcher porcentajeMatcher = porcentajePattern.matcher(analisisTexto);
        
        int countPorcentajes = 0;
        while (porcentajeMatcher.find() && countPorcentajes < 5) {
            String clave = "porcentaje_" + (countPorcentajes + 1);
            metricas.put(clave, Double.parseDouble(porcentajeMatcher.group(1)));
            countPorcentajes++;
        }
        
        Pattern dineroPattern = Pattern.compile("\\$([\\d,]+(?:\\.\\d{2})?)");
        Matcher dineroMatcher = dineroPattern.matcher(analisisTexto);
        
        int countDinero = 0;
        while (dineroMatcher.find() && countDinero < 5) {
            String clave = "monto_" + (countDinero + 1);
            String valor = dineroMatcher.group(1).replace(",", "");
            metricas.put(clave, Double.parseDouble(valor));
            countDinero++;
        }
        
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
        
        String[] lineas = analisisTexto.split("\n");
        long countRecomendaciones = java.util.Arrays.stream(lineas)
            .filter(linea -> linea.trim().matches("^\\d+\\.|^-|^•|^\\*|recomendacion|sugerencia|accion"))
            .count();
        metricas.put("total_recomendaciones", countRecomendaciones);
        
        return metricas;
    }
    
    /**
     * Extraigo recomendaciones estructuradas del analisis de IA.
     */
    private Map<String, String> extraerRecomendaciones(String analisisTexto) {
        Map<String, String> recomendaciones = new HashMap<>();
        
        String[] lineas = analisisTexto.split("\n");
        int numeroRecomendacion = 1;
        
        for (String linea : lineas) {
            linea = linea.trim();
            
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
                
                if (descripcion.length() > 10) {
                    recomendaciones.put(titulo, descripcion);
                    numeroRecomendacion++;
                }
            }
        }
        
        if (recomendaciones.isEmpty()) {
            recomendaciones.put("Analisis General", 
                "Revise el analisis completo para recomendaciones especificas");
        }
        
        return recomendaciones;
    }
    
    /**
     * Metodo simplificado para analisis rapido del ultimo mes.
     */
    public ResultadoAnalisisIA generarAnalisisRapido(String tipoAnalisis) {
        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime inicio = fin.minusMonths(1);
        
        return generarAnalisisCompleto(tipoAnalisis, inicio, fin);
    }
}