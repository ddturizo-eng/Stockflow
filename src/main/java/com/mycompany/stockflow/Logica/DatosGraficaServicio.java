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
 * Servicio para transformar datos de análisis en estructuras de gráficas JavaFX.
 * 
 * <p>Este servicio es responsable de generar todas las estructuras de datos 
 * necesarias para visualizar gráficas en la interfaz de usuario. Transforma 
 * datos crudos de ventas, productos e inventario en objetos {@link DatosGrafica} 
 * listos para ser renderizados.</p>
 * 
 * <p><strong>ADAPTADO CON ANÁLISIS DE RENTABILIDAD:</strong> 
 * Incluye cálculos diferenciados entre precioCompra y precioVenta para análisis 
 * de márgenes y rentabilidad real.</p>
 * 
 * <p><strong>Tipos de gráficas generadas:</strong></p>
 * <ul>
 *   <li><strong>Gráficas de Ventas:</strong>
 *     <ul>
 *       <li>Tendencia de ventas e ingresos por período</li>
 *       <li>Comparativa mes actual vs anterior</li>
 *       <li>Proyecciones y predicciones</li>
 *     </ul>
 *   </li>
 *   <li><strong>Gráficas de Productos:</strong>
 *     <ul>
 *       <li>Top productos más vendidos (por ingresos)</li>
 *       <li>Top productos más rentables (por ganancias)</li>
 *       <li>Análisis de márgenes de ganancia</li>
 *       <li>Distribución por categoría</li>
 *       <li>Valor de inventario por categoría</li>
 *     </ul>
 *   </li>
 *   <li><strong>Gráficas de Inventario:</strong>
 *     <ul>
 *       <li>Productos con stock crítico</li>
 *       <li>ROI del inventario actual</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>{@code
 * DatosGraficaServicio servicio = new DatosGraficaServicio();
 * 
 * // Generar gráfica de tendencia de ventas
 * LocalDate inicio = LocalDate.now().minusMonths(1);
 * LocalDate fin = LocalDate.now();
 * DatosGrafica grafica = servicio.generarGraficaTendenciaVentas(inicio, fin);
 * 
 * // Usar en componente JavaFX
 * for (SerieGrafica serie : grafica.getSeries()) {
 *     XYChart.Series<String, Number> chartSerie = new XYChart.Series<>();
 *     chartSerie.setName(serie.getNombre());
 *     
 *     for (PuntoGrafica punto : serie.getPuntos()) {
 *         chartSerie.getData().add(
 *             new XYChart.Data<>(punto.getEtiqueta(), punto.getValor())
 *         );
 *     }
 *     
 *     lineChart.getData().add(chartSerie);
 * }
 * }</pre>
 * 
 * @author Equipo StockFlow
 * @version 1.0
 * @since 2025
 * 
 * @see DatosGrafica
 * @see SerieGrafica
 * @see PuntoGrafica
 * @see TipoGrafica
 */
public class DatosGraficaServicio {
    
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
     * <p>Inicializa los servicios necesarios para obtener datos de 
     * ventas, productos e inventario.</p>
     */
    public DatosGraficaServicio() {
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
        this.inventarioServicio = new InventarioServicio();
    }
    
    // ============ GRÁFICAS DE VENTAS ============
    
    /**
     * Genera gráfica de tendencia de ventas e ingresos por período.
     * 
     * <p>Crea una gráfica de líneas con dos series de datos:</p>
     * <ol>
     *   <li><strong>Ventas Totales (azul):</strong> Ingresos brutos por día</li>
     *   <li><strong>Ganancias Netas (verde):</strong> Beneficio real por día 
     *       (ventas - costos)</li>
     * </ol>
     * 
     * <p><strong>Cálculos realizados:</strong></p>
     * <ul>
     *   <li>Agrupa ventas por fecha dentro del período especificado</li>
     *   <li>Calcula total de ventas por día (suma de totales)</li>
     *   <li>Calcula ganancias netas por día (suma de gananciaNeta)</li>
     *   <li>Ordena cronológicamente los resultados</li>
     * </ul>
     * 
     * <p><strong>Metadata incluida:</strong></p>
     * <ul>
     *   <li>{@code totalVentas}: Suma total de ventas del período</li>
     *   <li>{@code totalGanancias}: Suma total de ganancias del período</li>
     *   <li>{@code margenPromedio}: Porcentaje promedio de ganancia</li>
     * </ul>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Dashboard principal para visualizar desempeño diario</li>
     *   <li>Análisis de rentabilidad del negocio</li>
     *   <li>Identificación de días con mejor/peor desempeño</li>
     *   <li>Comparación entre ingresos y ganancias reales</li>
     * </ul>
     * 
     * @param inicio Fecha de inicio del período a analizar (inclusive)
     * @param fin Fecha de fin del período a analizar (inclusive)
     * @return DatosGrafica configurada como gráfica de líneas con dos series
     * 
     * @see Venta#getTotal()
     * @see Venta#getGananciaNeta()
     */
    public DatosGrafica generarGraficaTendenciaVentas(LocalDate inicio, LocalDate fin) {
        DatosGrafica grafica = new DatosGrafica();
        grafica.setTipo(TipoGrafica.LINEA);
        grafica.setTitulo("Tendencia de Ventas e Ingresos");
        
        List<Venta> todasVentas = ventaServicio.listarVentas();
        
        // Filtrar ventas por período
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
     * Genera gráfica comparativa entre mes actual y anterior.
     * 
     * <p>Compara el desempeño de ventas día por día entre el mes en curso 
     * y el mes anterior. Útil para identificar tendencias y evaluar crecimiento.</p>
     * 
     * <p><strong>Lógica de comparación:</strong></p>
     * <ul>
     *   <li>Agrupa ventas del mes actual por día del mes (1-31)</li>
     *   <li>Agrupa ventas del mes anterior por día del mes (1-31)</li>
     *   <li>Permite comparación día a día independientemente de la fecha absoluta</li>
     * </ul>
     * 
     * <p><strong>Series generadas:</strong></p>
     * <ul>
     *   <li><strong>Mes Anterior (azul):</strong> Ventas del mes pasado</li>
     *   <li><strong>Mes Actual (naranja):</strong> Ventas del mes en curso</li>
     * </ul>
     * 
     * <p><strong>Metadata incluida:</strong></p>
     * <ul>
     *   <li>{@code totalActual}: Total de ventas del mes actual</li>
     *   <li>{@code totalAnterior}: Total de ventas del mes anterior</li>
     *   <li>{@code crecimiento}: Porcentaje de crecimiento (positivo o negativo)</li>
     * </ul>
     * 
     * @return DatosGrafica configurada como gráfica de líneas comparativa
     * 
     * @see #agruparVentasPorDiaDelMes(List)
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
     * Genera gráfica de top productos más vendidos (por ingresos).
     * 
     * <p>Identifica los productos que han generado más ingresos totales 
     * (no necesariamente los más rentables). Ordena por subtotal de ventas.</p>
     * 
     * <p><strong>Criterio de ordenamiento:</strong> Suma de subtotales de ventas</p>
     * 
     * <p><strong>Visualización:</strong></p>
     * <ul>
     *   <li>Tipo: Gráfica de barras</li>
     *   <li>Colores: Paleta de 5 colores rotativos</li>
     *   <li>Serie única: "Ventas ($)"</li>
     * </ul>
     * 
     * <p><strong>Diferencia con productos rentables:</strong></p>
     * <ul>
     *   <li><strong>Más vendidos:</strong> Mayor volumen de ingresos</li>
     *   <li><strong>Más rentables:</strong> Mayor margen de ganancia</li>
     * </ul>
     * 
     * @param limite Cantidad de productos a mostrar (ej: top 5, top 10)
     * @return DatosGrafica configurada como gráfica de barras
     * 
     * @see #generarGraficaProductosMasRentables(int)
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
     * Genera gráfica de top productos más rentables (por ganancias).
     * 
     * <p>Identifica los productos que han generado más ganancias netas, 
     * considerando la diferencia entre precio de venta y precio de compra.</p>
     * 
     * <p><strong>Cálculo de ganancia:</strong></p>
     * <pre>
     * Ganancia por Detalle = (Precio Venta - Precio Compra) × Cantidad
     * Ganancia Total Producto = Suma de ganancias de todos los detalles
     * </pre>
     * 
     * <p><strong>Importancia:</strong></p>
     * <ul>
     *   <li>Productos con alto volumen de ventas pero bajo margen pueden no aparecer aquí</li>
     *   <li>Productos con ventas moderadas pero alto margen sí aparecerán</li>
     *   <li>Permite identificar productos más valiosos para el negocio</li>
     * </ul>
     * 
     * <p><strong>Metadata incluida:</strong></p>
     * <ul>
     *   <li>{@code totalGanancias}: Suma total de ganancias de los productos mostrados</li>
     * </ul>
     * 
     * @param limite Cantidad de productos a mostrar
     * @return DatosGrafica configurada como gráfica de barras con colores verdes
     * 
     * @see DetalleVenta#getGanancia()
     * @see #generarGraficaTopProductos(int)
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
     * Genera gráfica de análisis de márgenes de ganancia por producto.
     * 
     * <p>Muestra el porcentaje de margen de ganancia de productos actuales 
     * en inventario (no histórico de ventas).</p>
     * 
     * <p><strong>Cálculo del margen:</strong></p>
     * <pre>
     * Margen (%) = ((Precio Venta - Precio Compra) / Precio Venta) × 100
     * </pre>
     * 
     * <p><strong>Clasificación por colores:</strong></p>
     * <ul>
     *   <li><strong>Verde:</strong> Margen alto (&gt; umbral alto) - Excelente rentabilidad</li>
     *   <li><strong>Naranja:</strong> Margen medio - Rentabilidad aceptable</li>
     *   <li><strong>Rojo:</strong> Margen bajo (&lt; umbral bajo) - Requiere revisión</li>
     * </ul>
     * 
     * <p><strong>Filtros aplicados:</strong></p>
     * <ul>
     *   <li>Solo productos rentables (margen &gt; 0)</li>
     *   <li>Ordenados por margen descendente</li>
     *   <li>Limitados a cantidad especificada</li>
     * </ul>
     * 
     * <p><strong>Metadata incluida:</strong></p>
     * <ul>
     *   <li>{@code margenPromedio}: Promedio de márgenes de los productos mostrados</li>
     * </ul>
     * 
     * @param limite Cantidad de productos a mostrar
     * @return DatosGrafica configurada como gráfica de barras con colores semaforizados
     * 
     * @see Producto#getMargenGanancia()
     * @see Producto#tieneMargenAlto()
     * @see Producto#tieneMargenBajo()
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
     * Genera gráfica de distribución de productos por categoría.
     * 
     * <p>Muestra la cantidad de productos en cada categoría mediante 
     * una gráfica de pastel (pie chart).</p>
     * 
     * <p><strong>Uso:</strong> Visualizar diversificación del catálogo</p>
     * 
     * @return DatosGrafica configurada como gráfica de pastel
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
     * Genera gráfica de valor de inventario por categoría.
     * 
     * <p>Muestra el valor monetario total del inventario en cada categoría, 
     * calculado a precio de venta.</p>
     * 
     * <p><strong>Cálculo:</strong></p>
     * <pre>
     * Valor Categoría = Suma(Precio Venta × Stock) para cada producto
     * </pre>
     * 
     * <p><strong>Metadata incluida:</strong></p>
     * <ul>
     *   <li>{@code valorTotal}: Valor total de todo el inventario</li>
     * </ul>
     * 
     * @return DatosGrafica configurada como gráfica de pastel
     * 
     * @see Producto#getValorInventarioVenta()
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
     * Genera gráfica de inventario crítico (productos con stock bajo).
     * 
     * <p>Visualiza los productos que tienen stock por debajo del nivel mínimo 
     * configurado, ordenados por urgencia (menor stock primero).</p>
     * 
     * <p><strong>Clasificación por colores:</strong></p>
     * <ul>
     *   <li><strong>Rojo (#D0021B):</strong> Sin stock (0 unidades) - Crítico</li>
     *   <li><strong>Naranja (#F5A623):</strong> Stock muy bajo (≤ 50% del mínimo) - Urgente</li>
     *   <li><strong>Amarillo (#FFCC00):</strong> Stock bajo (por debajo del mínimo) - Atención</li>
     * </ul>
     * 
     * <p><strong>Límite de visualización:</strong> Muestra máximo 10 productos más críticos</p>
     * 
     * <p><strong>Metadata incluida:</strong></p>
     * <ul>
     *   <li>{@code cantidadCriticos}: Total de productos con stock crítico en el sistema</li>
     * </ul>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Dashboard de alertas de inventario</li>
     *   <li>Priorización de órdenes de compra</li>
     *   <li>Prevención de quiebres de stock</li>
     * </ul>
     * 
     * @return DatosGrafica configurada como gráfica de barras horizontales con colores semaforizados
     * 
     * @see InventarioServicio#obtenerProductosStockBajo()
     * @see Producto#getStockMinimo()
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
     * Genera gráfica de ROI del inventario actual.
     * 
     * <p>Muestra el margen de ganancia (como indicador de ROI) de los productos 
     * actualmente en inventario. Solo incluye productos rentables con stock disponible.</p>
     * 
     * <p><strong>Filtros aplicados:</strong></p>
     * <ul>
     *   <li>Stock mayor a 0</li>
     *   <li>Productos rentables (margen &gt; 0)</li>
     *   <li>Ordenados por margen descendente</li>
     * </ul>
     * 
     * <p><strong>Interpretación:</strong></p>
     * <p>Productos con mayor margen representan mejor retorno sobre la inversión 
     * del capital inmovilizado en inventario.</p>
     * 
     * @param limite Cantidad de productos a mostrar
     * @return DatosGrafica configurada como gráfica de barras en color verde
     * 
     * @see Producto#getMargenGanancia()
     * @see Producto#esRentable()
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
     * Genera gráfica de proyección de ventas basada en tendencia histórica.
     * 
     * <p>Crea una visualización con dos series:</p>
     * <ol>
     *   <li><strong>Histórico (azul):</strong> Ventas reales de los últimos 7 días</li>
     *   <li><strong>Predicción (naranja):</strong> Proyección para los próximos 7 días</li>
     * </ol>
     * 
     * <p><strong>Algoritmo de predicción (simplificado):</strong></p>
     * <pre>
     * 1. Calcular promedio de ventas de últimos 7 días
     * 2. Para cada día futuro:
     *    Venta Predicha = Promedio × Factor Aleatorio (0.95 - 1.05)
     * </pre>
     * 
     * <p><strong>Limitaciones actuales:</strong></p>
     * <ul>
     *   <li>Predicción muy simple basada en promedio</li>
     *   <li>No considera estacionalidad</li>
     *   <li>No considera tendencias de crecimiento/decrecimiento</li>
     *   <li>Variación aleatoria del 10% simulada</li>
     * </ul>
     * 
     * <p><strong>Mejoras recomendadas:</strong></p>
     * <ul>
     *   <li>Implementar regresión lineal para detectar tendencias</li>
     *   <li>Ajustar por estacionalidad (día de semana, mes)</li>
     *   <li>Usar modelos ARIMA o Prophet para series temporales</li>
     *   <li>Considerar eventos especiales (promociones, festivos)</li>
     * </ul>
     * 
     * <p><strong>Metadata incluida:</strong></p>
     * <ul>
     *   <li>{@code promedioVentas}: Promedio diario de ventas del período histórico</li>
     * </ul>
     * 
     * @param analisis Objeto de análisis estadístico (actualmente no utilizado)
     * @return DatosGrafica configurada como gráfica de área con dos series
     * 
     * @see #calcularVentasDia(LocalDate, List)
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
    
    /**
     * Agrupa ventas por día del mes (1-31).
     * 
     * <p>Útil para comparaciones mes a mes donde queremos comparar 
     * día 1 con día 1, día 2 con día 2, etc., independientemente 
     * del mes o año absoluto.</p>
     * 
     * @param ventas Lista de ventas a agrupar
     * @return Mapa con día del mes (1-31) como clave y total de ventas como valor
     */
    private Map<Integer, Double> agruparVentasPorDiaDelMes(List<Venta> ventas) {
        return ventas.stream()
            .collect(Collectors.groupingBy(
                v -> v.getFecha().getDayOfMonth(),
                Collectors.summingDouble(Venta::getTotal)
            ));
    }
    
    /**
     * Calcula el total de ventas de un día específico.
     * 
     * <p>Suma todas las ventas que ocurrieron exactamente en la fecha indicada.</p>
     * 
     * @param fecha Fecha específica a consultar
     * @param ventas Lista completa de ventas
     * @return Total de ventas del día, 0 si no hubo ventas
     */
    private double calcularVentasDia(LocalDate fecha, List<Venta> ventas) {
        return ventas.stream()
            .filter(v -> v.getFecha().toLocalDate().equals(fecha))
            .mapToDouble(Venta::getTotal)
            .sum();
    }
}