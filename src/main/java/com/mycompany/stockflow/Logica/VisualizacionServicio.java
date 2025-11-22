/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.*;
import javafx.scene.chart.XYChart;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de visualizacion de datos para graficas JavaFX.
 * 
 * <p>Este servicio transforma datos del negocio en series de datos
 * compatibles con los componentes Chart de JavaFX. Prepara datos
 * para LineChart, BarChart, AreaChart y otros tipos de graficas.</p>
 * 
 * <p>Tipos de visualizaciones soportadas:</p>
 * <ul>
 *   <li>Tendencias de ventas historicas y predicciones</li>
 *   <li>Comparativas de productos</li>
 *   <li>Niveles de inventario vs demanda</li>
 *   <li>Analisis de estacionalidad</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 */
public class VisualizacionServicio {
    
    private final VentaServicio ventaServicio;
    private final ProductoServicio productoServicio;
    private final PrediccionServicio prediccionServicio;
    private final DateTimeFormatter formatterDia = DateTimeFormatter.ofPattern("dd/MM");
    private final DateTimeFormatter formatterMes = DateTimeFormatter.ofPattern("MMM");
    
    /**
     * Constructor por defecto.
     */
    public VisualizacionServicio() {
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
        this.prediccionServicio = new PrediccionServicio();
    }
    
    /**
     * Genera serie de ventas historicas de un producto.
     * 
     * @param producto producto a visualizar
     * @param diasHistorico dias hacia atras
     * @return serie de datos para LineChart
     */
    public XYChart.Series<String, Number> generarSerieVentasHistoricas(Producto producto, 
                                                                       int diasHistorico) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ventas Historicas");
        
        try {
            List<Venta> ventas = ventaServicio.listarVentas();
            LocalDate fechaInicio = LocalDate.now().minusDays(diasHistorico);
            
            Map<LocalDate, Integer> ventasPorDia = new TreeMap<>();
            
            for (Venta venta : ventas) {
                LocalDate fechaVenta = venta.getFecha().toLocalDate();
                if (fechaVenta.isBefore(fechaInicio)) continue;
                
                if (venta.getDetalles() != null) {
                    for (DetalleVenta detalle : venta.getDetalles()) {
                        if (detalle.getProducto() != null && 
                            detalle.getProducto().getCodigo().equals(producto.getCodigo())) {
                            ventasPorDia.merge(fechaVenta, detalle.getCantidad(), Integer::sum);
                        }
                    }
                }
            }
            
            for (Map.Entry<LocalDate, Integer> entry : ventasPorDia.entrySet()) {
                serie.getData().add(new XYChart.Data<>(
                    entry.getKey().format(formatterDia),
                    entry.getValue()
                ));
            }
            
        } catch (Exception e) {
            System.err.println("Error generando serie historica: " + e.getMessage());
        }
        
        return serie;
    }
    
    /**
     * Genera serie de prediccion de ventas.
     * 
     * @param producto producto a predecir
     * @param diasFuturos dias hacia adelante
     * @return serie de datos para LineChart
     */
    public XYChart.Series<String, Number> generarSeriePrediccion(Producto producto, 
                                                                 int diasFuturos) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Prediccion IA");
        
        try {
            LocalDate hoy = LocalDate.now();
            
            for (int i = 1; i <= diasFuturos; i += Math.max(1, diasFuturos / 10)) {
                PrediccionDemanda prediccion = prediccionServicio.predecirDemandaMejorada(
                    producto, i
                );
                
                serie.getData().add(new XYChart.Data<>(
                    hoy.plusDays(i).format(formatterDia),
                    prediccion.getDemandaEstimada()
                ));
            }
            
        } catch (Exception e) {
            System.err.println("Error generando serie prediccion: " + e.getMessage());
        }
        
        return serie;
    }
    
    /**
     * Genera serie de stock actual.
     * 
     * @param producto producto a visualizar
     * @param diasProyeccion dias a mostrar
     * @return serie de datos para LineChart
     */
    public XYChart.Series<String, Number> generarSerieStockActual(Producto producto, 
                                                                  int diasProyeccion) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Stock Actual");
        
        try {
            LocalDate hoy = LocalDate.now();
            int stockActual = producto.getStock();
            
            for (int i = 0; i <= diasProyeccion; i += Math.max(1, diasProyeccion / 10)) {
                serie.getData().add(new XYChart.Data<>(
                    hoy.plusDays(i).format(formatterDia),
                    stockActual
                ));
            }
            
        } catch (Exception e) {
            System.err.println("Error generando serie stock: " + e.getMessage());
        }
        
        return serie;
    }
    
    /**
     * Genera serie de nivel de reabastecimiento recomendado.
     * 
     * @param producto producto a analizar
     * @param diasProyeccion dias a proyectar
     * @return serie de datos para BarChart
     */
    public XYChart.Series<String, Number> generarSerieReabastecimiento(Producto producto, 
                                                                       int diasProyeccion) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Nivel Recomendado IA");
        
        try {
            PrediccionDemanda prediccion = prediccionServicio.predecirDemandaMejorada(
                producto, diasProyeccion
            );
            
            int nivelRecomendado = prediccion.getCantidadSugeridaReabastecimiento();
            
            serie.getData().add(new XYChart.Data<>("Recomendado", nivelRecomendado));
            serie.getData().add(new XYChart.Data<>("Stock Minimo", producto.getStockMinimo()));
            serie.getData().add(new XYChart.Data<>("Stock Actual", producto.getStock()));
            
        } catch (Exception e) {
            System.err.println("Error generando serie reabastecimiento: " + e.getMessage());
        }
        
        return serie;
    }
    
    /**
     * Genera serie comparativa de productos por ventas.
     * 
     * @param limite numero de productos a incluir
     * @return serie de datos para BarChart
     */
    public XYChart.Series<String, Number> generarSerieTopProductos(int limite) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Top Productos por Ventas");
        
        try {
            List<Venta> ventas = ventaServicio.listarVentas();
            Map<String, Integer> ventasPorProducto = new HashMap<>();
            
            for (Venta venta : ventas) {
                if (venta.getDetalles() != null) {
                    for (DetalleVenta detalle : venta.getDetalles()) {
                        if (detalle.getProducto() != null) {
                            String nombre = detalle.getProducto().getNombre();
                            ventasPorProducto.merge(nombre, detalle.getCantidad(), Integer::sum);
                        }
                    }
                }
            }
            
            ventasPorProducto.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .forEach(entry -> {
                    String nombre = entry.getKey().length() > 15 ? 
                        entry.getKey().substring(0, 12) + "..." : entry.getKey();
                    serie.getData().add(new XYChart.Data<>(nombre, entry.getValue()));
                });
                
        } catch (Exception e) {
            System.err.println("Error generando top productos: " + e.getMessage());
        }
        
        return serie;
    }
    
    /**
     * Genera serie de ventas por mes del año.
     * 
     * @param año año a analizar
     * @return serie de datos para LineChart
     */
    public XYChart.Series<String, Number> generarSerieVentasMensuales(int año) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ventas " + año);
        
        try {
            List<Venta> ventas = ventaServicio.listarVentas();
            Map<Integer, Double> ventasPorMes = new TreeMap<>();
            
            for (int mes = 1; mes <= 12; mes++) {
                ventasPorMes.put(mes, 0.0);
            }
            
            for (Venta venta : ventas) {
                if (venta.getFecha().getYear() == año) {
                    int mes = venta.getFecha().getMonthValue();
                    ventasPorMes.merge(mes, venta.getTotal(), Double::sum);
                }
            }
            
            String[] nombresMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", 
                                    "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
            
            for (Map.Entry<Integer, Double> entry : ventasPorMes.entrySet()) {
                serie.getData().add(new XYChart.Data<>(
                    nombresMeses[entry.getKey() - 1],
                    entry.getValue()
                ));
            }
            
        } catch (Exception e) {
            System.err.println("Error generando ventas mensuales: " + e.getMessage());
        }
        
        return serie;
    }
    
    /**
     * Genera serie de productos en riesgo de stock.
     * 
     * @return serie de datos para BarChart
     */
    public XYChart.Series<String, Number> generarSerieProductosEnRiesgo() {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Productos en Riesgo");
        
        try {
            List<Producto> productos = productoServicio.listarProductos();
            
            productos.stream()
                .filter(Producto::tieneStockBajo)
                .limit(10)
                .forEach(p -> {
                    String nombre = p.getNombre().length() > 12 ? 
                        p.getNombre().substring(0, 9) + "..." : p.getNombre();
                    serie.getData().add(new XYChart.Data<>(nombre, p.getStock()));
                });
                
        } catch (Exception e) {
            System.err.println("Error generando productos en riesgo: " + e.getMessage());
        }
        
        return serie;
    }
    
    /**
     * Genera configuracion de colores para series.
     * 
     * @param nombreSerie nombre de la serie
     * @return codigo de color CSS
     */
    public String obtenerColorSerie(String nombreSerie) {
        if (nombreSerie.contains("Historica") || nombreSerie.contains("Actual")) {
            return "#10b981";
        } else if (nombreSerie.contains("Prediccion") || nombreSerie.contains("IA")) {
            return "#3b82f6";
        } else if (nombreSerie.contains("Recomendado") || nombreSerie.contains("Alerta")) {
            return "#f59e0b";
        } else if (nombreSerie.contains("Critico") || nombreSerie.contains("Riesgo")) {
            return "#ef4444";
        }
        return "#667eea";
    }
    
    /**
     * Genera lista completa de series para grafica combinada.
     * 
     * @param producto producto a analizar
     * @param diasHistorico dias historicos a mostrar
     * @param diasFuturos dias futuros a predecir
     * @return lista de series para grafica
     */
    public List<XYChart.Series<String, Number>> generarGraficaCompleta(Producto producto,
                                                                       int diasHistorico,
                                                                       int diasFuturos) {
        List<XYChart.Series<String, Number>> series = new ArrayList<>();
        
        series.add(generarSerieVentasHistoricas(producto, diasHistorico));
        series.add(generarSeriePrediccion(producto, diasFuturos));
        series.add(generarSerieStockActual(producto, diasFuturos));
        
        return series;
    }
    
    /**
     * Calcula estadisticas resumidas para un producto.
     * 
     * @param producto producto a analizar
     * @return mapa con estadisticas clave
     */
    public Map<String, String> calcularEstadisticasResumen(Producto producto) {
        Map<String, String> stats = new HashMap<>();
        
        try {
            PrediccionDemanda prediccion = prediccionServicio.predecirDemandaMejorada(
                producto, 30
            );
            
            stats.put("demandaEstimada", String.valueOf(prediccion.getDemandaEstimada()));
            stats.put("stockActual", String.valueOf(producto.getStock()));
            stats.put("nivelConfianza", prediccion.getNivelConfianzaPorcentaje() + "%");
            stats.put("tendencia", prediccion.getTendencia());
            stats.put("requiereReabastecimiento", 
                prediccion.requiereReabastecimiento() ? "SI" : "NO");
            stats.put("cantidadRecomendada", 
                String.valueOf(prediccion.getCantidadSugeridaReabastecimiento()));
            
        } catch (Exception e) {
            stats.put("error", "No se pudo calcular");
        }
        
        return stats;
    }

        /**
     * Genera serie de ventas históricas con muestreo para reducir puntos.
     * 
     * @param producto El producto
     * @param dias Días hacia atrás
     * @param intervalo Cada cuántos días tomar muestra (ej: 3 = cada 3 días)
     */
    public XYChart.Series<String, Number> generarSerieVentasHistoricasMuestreada(
            Producto producto, int dias, int intervalo) {

        XYChart.Series<String, Number> serie = generarSerieVentasHistoricas(producto, dias);

        // Crear nueva serie con muestreo
        XYChart.Series<String, Number> serieMuestreada = new XYChart.Series<>();
        serieMuestreada.setName(serie.getName());

        List<XYChart.Data<String, Number>> datos = serie.getData();
        for (int i = 0; i < datos.size(); i += intervalo) {
            serieMuestreada.getData().add(datos.get(i));
        }

        // Asegurar que el último punto siempre esté incluido
        if (!datos.isEmpty() && (datos.size() - 1) % intervalo != 0) {
            serieMuestreada.getData().add(datos.get(datos.size() - 1));
        }

        return serieMuestreada;
    }

    public XYChart.Series<String, Number> generarSeriePrediccionMuestreada(
            Producto producto, int dias, int intervalo) {

        XYChart.Series<String, Number> serie = generarSeriePrediccion(producto, dias);
        return muestrearSerie(serie, intervalo);
    }

    public XYChart.Series<String, Number> generarSerieStockActualMuestreada(
            Producto producto, int dias, int intervalo) {

        XYChart.Series<String, Number> serie = generarSerieStockActual(producto, dias);
        return muestrearSerie(serie, intervalo);
    }

    private XYChart.Series<String, Number> muestrearSerie(
            XYChart.Series<String, Number> serie, int intervalo) {

        XYChart.Series<String, Number> serieMuestreada = new XYChart.Series<>();
        serieMuestreada.setName(serie.getName());

        List<XYChart.Data<String, Number>> datos = serie.getData();
        for (int i = 0; i < datos.size(); i += intervalo) {
            serieMuestreada.getData().add(datos.get(i));
        }

        if (!datos.isEmpty() && (datos.size() - 1) % intervalo != 0) {
            serieMuestreada.getData().add(datos.get(datos.size() - 1));
        }

        return serieMuestreada;
    }
}