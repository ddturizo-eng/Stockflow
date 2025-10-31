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
 * Servicio para transformar datos de análisis en estructuras de gráficas JavaFX
 * ADAPTADO CON ANÁLISIS DE RENTABILIDAD (precioCompra vs precioVenta)
 */
public class DatosGraficaServicio {
    
    private final VentaServicio ventaServicio;
    private final ProductoServicio productoServicio;
    private final InventarioServicio inventarioServicio;
    
    public DatosGraficaServicio() {
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
        this.inventarioServicio = new InventarioServicio();
    }
    
    // ============ GRÁFICAS DE VENTAS ============
    
    /**
     * Tendencia de ventas (ingresos) por periodo
     */
    public DatosGrafica generarGraficaTendenciaVentas(LocalDate inicio, LocalDate fin) {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.LINEA);
        grafica.setTitulo("Tendencia de Ventas e Ingresos");
        
        List<Venta> todasVentas = ventaServicio.listarVentas();
        
        List<Venta> ventasFiltradas = todasVentas.stream()
            .filter(v -> {
                LocalDate fechaVenta = v.getFecha().toLocalDate();
                return !fechaVenta.isBefore(inicio) && !fechaVenta.isAfter(fin);
            })
            .collect(Collectors.toList());
        
        // Agrupar ventas totales por fecha
        Map<LocalDate, Double> ventasPorFecha = ventasFiltradas.stream()
            .collect(Collectors.groupingBy(
                v -> v.getFecha().toLocalDate(),
                Collectors.summingDouble(Venta::getTotal)
            ));
        
        // Agrupar ganancias por fecha
        Map<LocalDate, Double> gananciasPorFecha = ventasFiltradas.stream()
            .collect(Collectors.groupingBy(
                v -> v.getFecha().toLocalDate(),
                Collectors.summingDouble(Venta::getGananciaNeta)
            ));
        
        // Serie de ventas totales
        SerieGrafica serieVentas = new SerieGrafica("Ventas Totales");
        serieVentas.setColor("#4A90E2");
        
        ventasPorFecha.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                PuntoGrafica punto = new PuntoGrafica();
                punto.setEtiqueta(entry.getKey().toString());
                punto.setValor(entry.getValue());
                punto.setTimestamp(entry.getKey().atStartOfDay());
                serieVentas.agregarPunto(punto);
            });
        
        // Serie de ganancias netas
        SerieGrafica serieGanancias = new SerieGrafica("Ganancias Netas");
        serieGanancias.setColor("#7ED321");
        
        gananciasPorFecha.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                PuntoGrafica punto = new PuntoGrafica();
                punto.setEtiqueta(entry.getKey().toString());
                punto.setValor(entry.getValue());
                serieGanancias.agregarPunto(punto);
            });
        
        grafica.agregarSerie(serieVentas);
        grafica.agregarSerie(serieGanancias);
        
        // Metadata con análisis de rentabilidad
        double totalVentas = ventasPorFecha.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalGanancias = gananciasPorFecha.values().stream().mapToDouble(Double::doubleValue).sum();
        double margenPromedio = totalVentas > 0 ? (totalGanancias / totalVentas) * 100 : 0;
        
        grafica.getMetadata().put("totalVentas", totalVentas);
        grafica.getMetadata().put("totalGanancias", totalGanancias);
        grafica.getMetadata().put("margenPromedio", margenPromedio);
        
        return grafica;
    }
    
    /**
     * Gráfica de comparativa mes actual vs anterior
     */
    public DatosGrafica generarGraficaComparativaVentas() {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.LINEA);
        grafica.setTitulo("Comparativa Mes Anterior");
        
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMesActual = hoy.withDayOfMonth(1);
        LocalDate inicioMesAnterior = inicioMesActual.minusMonths(1);
        LocalDate finMesAnterior = inicioMesActual.minusDays(1);
        
        List<Venta> todasVentas = ventaServicio.listarVentas();
        
        List<Venta> ventasActuales = todasVentas.stream()
            .filter(v -> !v.getFecha().toLocalDate().isBefore(inicioMesActual))
            .collect(Collectors.toList());
        
        List<Venta> ventasAnteriores = todasVentas.stream()
            .filter(v -> {
                LocalDate fecha = v.getFecha().toLocalDate();
                return !fecha.isBefore(inicioMesAnterior) && !fecha.isAfter(finMesAnterior);
            })
            .collect(Collectors.toList());
        
        Map<Integer, Double> ventasActualesPorDia = agruparVentasPorDiaDelMes(ventasActuales);
        Map<Integer, Double> ventasAnterioresPorDia = agruparVentasPorDiaDelMes(ventasAnteriores);
        
        SerieGrafica serieActual = new SerieGrafica("Mes Actual");
        serieActual.setColor("#FF9500");
        ventasActualesPorDia.forEach((dia, total) -> {
            serieActual.agregarPunto(new PuntoGrafica("Día " + dia, total));
        });
        
        SerieGrafica serieAnterior = new SerieGrafica("Mes Anterior");
        serieAnterior.setColor("#5AC8FA");
        ventasAnterioresPorDia.forEach((dia, total) -> {
            serieAnterior.agregarPunto(new PuntoGrafica("Día " + dia, total));
        });
        
        grafica.agregarSerie(serieAnterior);
        grafica.agregarSerie(serieActual);
        
        double totalActual = ventasActualesPorDia.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalAnterior = ventasAnterioresPorDia.values().stream().mapToDouble(Double::doubleValue).sum();
        double crecimiento = totalAnterior > 0 ? ((totalActual - totalAnterior) / totalAnterior) * 100 : 0;
        
        grafica.getMetadata().put("totalActual", totalActual);
        grafica.getMetadata().put("totalAnterior", totalAnterior);
        grafica.getMetadata().put("crecimiento", crecimiento);
        
        return grafica;
    }
    
    // ============ GRÁFICAS DE PRODUCTOS (CON RENTABILIDAD) ============
    
    /**
     * Top productos MÁS VENDIDOS (por ingresos)
     */
    public DatosGrafica generarGraficaTopProductos(int limite) {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.BARRAS);
        grafica.setTitulo("Top " + limite + " Productos Más Vendidos");
        
        List<Venta> ventas = ventaServicio.listarVentas();
        
        Map<String, Double> ventasPorProducto = new HashMap<>();
        Map<String, Integer> cantidadPorProducto = new HashMap<>();
        
        for (Venta venta : ventas) {
            if (venta.getDetalles() != null) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    if (detalle.getProducto() != null) {
                        String nombreProducto = detalle.getProducto().getNombre();
                        ventasPorProducto.merge(nombreProducto, detalle.getSubtotal(), Double::sum);
                        cantidadPorProducto.merge(nombreProducto, detalle.getCantidad(), Integer::sum);
                    }
                }
            }
        }
        
        List<Map.Entry<String, Double>> topProductos = ventasPorProducto.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(limite)
            .collect(Collectors.toList());
        
        SerieGrafica serie = new SerieGrafica("Ventas ($)");
        String[] colores = {"#4A90E2", "#7ED321", "#F5A623", "#FF6B6B", "#9C27B0"};
        
        for (int i = 0; i < topProductos.size(); i++) {
            Map.Entry<String, Double> entry = topProductos.get(i);
            PuntoGrafica punto = new PuntoGrafica();
            punto.setEtiqueta(entry.getKey());
            punto.setValor(entry.getValue());
            punto.setColor(colores[i % colores.length]);
            serie.agregarPunto(punto);
        }
        
        grafica.agregarSerie(serie);
        return grafica;
    }
    
    /**
     * Top productos MÁS RENTABLES (por ganancias)
     */
    public DatosGrafica generarGraficaProductosMasRentables(int limite) {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.BARRAS);
        grafica.setTitulo("Top " + limite + " Productos Más Rentables");
        
        List<Venta> ventas = ventaServicio.listarVentas();
        
        Map<String, Double> gananciasPorProducto = new HashMap<>();
        
        for (Venta venta : ventas) {
            if (venta.getDetalles() != null) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    if (detalle.getProducto() != null) {
                        String nombreProducto = detalle.getProducto().getNombre();
                        gananciasPorProducto.merge(nombreProducto, detalle.getGanancia(), Double::sum);
                    }
                }
            }
        }
        
        List<Map.Entry<String, Double>> topRentables = gananciasPorProducto.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(limite)
            .collect(Collectors.toList());
        
        SerieGrafica serie = new SerieGrafica("Ganancia ($)");
        String[] colores = {"#7ED321", "#4A90E2", "#F5A623", "#FF6B6B", "#9C27B0"};
        
        for (int i = 0; i < topRentables.size(); i++) {
            Map.Entry<String, Double> entry = topRentables.get(i);
            PuntoGrafica punto = new PuntoGrafica();
            punto.setEtiqueta(entry.getKey());
            punto.setValor(entry.getValue());
            punto.setColor(colores[i % colores.length]);
            serie.agregarPunto(punto);
        }
        
        grafica.agregarSerie(serie);
        
        double totalGanancias = topRentables.stream().mapToDouble(Map.Entry::getValue).sum();
        grafica.getMetadata().put("totalGanancias", totalGanancias);
        
        return grafica;
    }
    
    /**
     * Análisis de margen de ganancia por producto (productos actuales)
     */
    public DatosGrafica generarGraficaMargenesGanancia(int limite) {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.BARRAS);
        grafica.setTitulo("Margen de Ganancia por Producto (%)");
        
        List<Producto> productos = productoServicio.listarProductos();
        
        // Ordenar por margen de ganancia
        List<Producto> productosPorMargen = productos.stream()
            .filter(Producto::esRentable)
            .sorted(Comparator.comparingDouble(Producto::getMargenGanancia).reversed())
            .limit(limite)
            .collect(Collectors.toList());
        
        SerieGrafica serie = new SerieGrafica("Margen (%)");
        
        for (Producto producto : productosPorMargen) {
            PuntoGrafica punto = new PuntoGrafica();
            punto.setEtiqueta(producto.getNombre());
            punto.setValor(producto.getMargenGanancia());
            
            // Color según margen
            if (producto.tieneMargenAlto()) {
                punto.setColor("#7ED321"); // Verde - excelente
            } else if (producto.tieneMargenBajo()) {
                punto.setColor("#FF6B6B"); // Rojo - bajo
            } else {
                punto.setColor("#F5A623"); // Naranja - medio
            }
            
            serie.agregarPunto(punto);
        }
        
        grafica.agregarSerie(serie);
        
        double margenPromedio = productosPorMargen.stream()
            .mapToDouble(Producto::getMargenGanancia)
            .average()
            .orElse(0);
        
        grafica.getMetadata().put("margenPromedio", margenPromedio);
        
        return grafica;
    }
    
    /**
     * Distribución de productos por categoría
     */
    public DatosGrafica generarGraficaDistribucionCategorias() {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.PASTEL);
        grafica.setTitulo("Productos por Categoría");
        
        List<Producto> productos = productoServicio.listarProductos();
        
        Map<String, Integer> productosPorCategoria = productos.stream()
            .collect(Collectors.groupingBy(
                Producto::getCategoria,
                Collectors.summingInt(p -> 1)
            ));
        
        SerieGrafica serie = new SerieGrafica("Categorías");
        String[] colores = {"#4A90E2", "#7ED321", "#F5A623", "#FF6B6B", "#9C27B0", "#50E3C2"};
        int idx = 0;
        
        for (Map.Entry<String, Integer> entry : productosPorCategoria.entrySet()) {
            PuntoGrafica punto = new PuntoGrafica();
            punto.setEtiqueta(entry.getKey());
            punto.setValor(entry.getValue().doubleValue());
            punto.setColor(colores[idx % colores.length]);
            serie.agregarPunto(punto);
            idx++;
        }
        
        grafica.agregarSerie(serie);
        return grafica;
    }
    
    /**
     * Valor de inventario por categoría (precio de venta)
     */
    public DatosGrafica generarGraficaValorInventarioPorCategoria() {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.PASTEL);
        grafica.setTitulo("Valor de Inventario por Categoría");
        
        List<Producto> productos = productoServicio.listarProductos();
        
        Map<String, Double> valorPorCategoria = productos.stream()
            .collect(Collectors.groupingBy(
                Producto::getCategoria,
                Collectors.summingDouble(Producto::getValorInventarioVenta)
            ));
        
        SerieGrafica serie = new SerieGrafica("Valor ($)");
        String[] colores = {"#4A90E2", "#7ED321", "#F5A623", "#FF6B6B", "#9C27B0"};
        int idx = 0;
        
        for (Map.Entry<String, Double> entry : valorPorCategoria.entrySet()) {
            PuntoGrafica punto = new PuntoGrafica();
            punto.setEtiqueta(entry.getKey());
            punto.setValor(entry.getValue());
            punto.setColor(colores[idx % colores.length]);
            serie.agregarPunto(punto);
            idx++;
        }
        
        grafica.agregarSerie(serie);
        
        double valorTotal = valorPorCategoria.values().stream().mapToDouble(Double::doubleValue).sum();
        grafica.getMetadata().put("valorTotal", valorTotal);
        
        return grafica;
    }
    
    // ============ GRÁFICAS DE INVENTARIO ============
    
    /**
     * Inventario crítico (stock bajo)
     */
    public DatosGrafica generarGraficaInventarioCritico() {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.BARRAS_HORIZONTAL);
        grafica.setTitulo("Productos con Stock Crítico");
        
        List<Producto> productosCriticos = inventarioServicio.obtenerProductosStockBajo();
        
        // Limitar a 10 productos más críticos
        List<Producto> top10Criticos = productosCriticos.stream()
            .sorted(Comparator.comparingInt(Producto::getStock))
            .limit(10)
            .collect(Collectors.toList());
        
        SerieGrafica serie = new SerieGrafica("Stock Actual");
        
        for (Producto producto : top10Criticos) {
            PuntoGrafica punto = new PuntoGrafica();
            punto.setEtiqueta(producto.getNombre());
            punto.setValor((double) producto.getStock());
            
            if (producto.getStock() == 0) {
                punto.setColor("#D0021B"); // Rojo - sin stock
            } else if (producto.getStock() <= producto.getStockMinimo() / 2) {
                punto.setColor("#F5A623"); // Naranja - muy bajo
            } else {
                punto.setColor("#FFCC00"); // Amarillo - bajo
            }
            
            serie.agregarPunto(punto);
        }
        
        grafica.agregarSerie(serie);
        grafica.getMetadata().put("cantidadCriticos", productosCriticos.size());
        
        return grafica;
    }
    
    /**
     * ROI del inventario (productos actuales)
     */
    public DatosGrafica generarGraficaROIInventario(int limite) {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.BARRAS);
        grafica.setTitulo("ROI del Inventario Actual");
        
        List<Producto> productos = productoServicio.listarProductos();
        
        List<Producto> productosPorROI = productos.stream()
            .filter(p -> p.getStock() > 0 && p.esRentable())
            .sorted(Comparator.comparingDouble(Producto::getMargenGanancia).reversed())
            .limit(limite)
            .collect(Collectors.toList());
        
        SerieGrafica serie = new SerieGrafica("Margen (%)");
        
        for (Producto producto : productosPorROI) {
            PuntoGrafica punto = new PuntoGrafica();
            punto.setEtiqueta(producto.getNombre());
            punto.setValor(producto.getMargenGanancia());
            punto.setColor("#7ED321");
            serie.agregarPunto(punto);
        }
        
        grafica.agregarSerie(serie);
        
        return grafica;
    }
    
    // ============ GRÁFICAS DE PREDICCIONES ============
    
    /**
     * Proyección de ventas basada en tendencia
     */
    public DatosGrafica generarGraficaPredicciones(AnalisisEstadistico analisis) {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.AREA);
        grafica.setTitulo("Proyección de Ventas (Próximos 7 días)");
        
        LocalDate hoy = LocalDate.now();
        List<Venta> ventas = ventaServicio.listarVentas();
        
        // Histórico últimos 7 días
        SerieGrafica historico = new SerieGrafica("Histórico");
        historico.setColor("#5AC8FA");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            double ventasDia = calcularVentasDia(fecha, ventas);
            historico.agregarPunto(new PuntoGrafica(fecha.toString(), ventasDia));
        }
        
        // Calcular tendencia simple (promedio últimos 7 días)
        double promedioVentas = historico.getValores().stream()
            .mapToDouble(PuntoGrafica::getValor)
            .average()
            .orElse(0);
        
        // Predicción próximos 7 días
        SerieGrafica prediccion = new SerieGrafica("Predicción");
        prediccion.setColor("#FF9500");
        
        for (int i = 1; i <= 7; i++) {
            LocalDate fecha = hoy.plusDays(i);
            // Predicción simple: promedio con variación del 10%
            double ventaPredicha = promedioVentas * (0.95 + (Math.random() * 0.1));
            prediccion.agregarPunto(new PuntoGrafica(fecha.toString(), ventaPredicha));
        }
        
        grafica.agregarSerie(historico);
        grafica.agregarSerie(prediccion);
        
        grafica.getMetadata().put("promedioVentas", promedioVentas);
        
        return grafica;
    }
    
    // ============ MÉTODOS AUXILIARES ============
    
    private Map<Integer, Double> agruparVentasPorDiaDelMes(List<Venta> ventas) {
        return ventas.stream()
            .collect(Collectors.groupingBy(
                v -> v.getFecha().getDayOfMonth(),
                Collectors.summingDouble(Venta::getTotal)
            ));
    }
    
    private double calcularVentasDia(LocalDate fecha, List<Venta> ventas) {
        return ventas.stream()
            .filter(v -> v.getFecha().toLocalDate().equals(fecha))
            .mapToDouble(Venta::getTotal)
            .sum();
    }
}

// ============ CLASES DE APOYO ============



