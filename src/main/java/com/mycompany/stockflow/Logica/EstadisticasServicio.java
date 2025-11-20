/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Venta;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Modelo.DetalleVenta;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de Estadísticas y Métricas de Negocio.
 * 
 * <p>Proporciona cálculos estadísticos básicos y métricas de desempeño 
 * del negocio. Este servicio ofrece datos agregados y resumidos que 
 * son útiles para dashboards, reportes y toma de decisiones rápida.</p>
 * 
 * <p><strong>Métricas disponibles:</strong></p>
 * <ul>
 *   <li>Ventas totales acumuladas</li>
 *   <li>Ventas por mes específico</li>
 *   <li>Productos más vendidos (por cantidad)</li>
 *   <li>Cantidad de ventas del día actual</li>
 *   <li>Distribución de ventas por mes del año</li>
 * </ul>
 * 
 * <p><strong>Diferencia con otros servicios:</strong></p>
 * <ul>
 *   <li><strong>EstadisticasServicio:</strong> Métricas básicas y agregadas</li>
 *   <li><strong>AnaliticaAvanzadaServicio:</strong> Análisis complejos (ROI, CLV, predicciones)</li>
 *   <li><strong>DatosGraficaServicio:</strong> Transformación de datos para visualización</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de uso en dashboard:</strong></p>
 * <pre>{@code
 * EstadisticasServicio servicio = new EstadisticasServicio();
 * 
 * // Métricas principales del dashboard
 * double ventasTotales = servicio.calcularVentasTotales();
 * int ventasHoy = servicio.contarVentasDelDia();
 * double ventasMesActual = servicio.calcularVentasMes(
 *     LocalDateTime.now().getMonthValue(), 
 *     LocalDateTime.now().getYear()
 * );
 * 
 * // Top productos
 * List<Producto> top5 = servicio.obtenerProductosMasVendidos(5);
 * 
 * // Mostrar en UI
 * lblVentasTotales.setText(String.format("$%.2f", ventasTotales));
 * lblVentasHoy.setText(String.valueOf(ventasHoy));
 * lblVentasMes.setText(String.format("$%.2f", ventasMesActual));
 * 
 * top5.forEach(p -> 
 *     System.out.println(p.getNombre() + " - " + p.getStock() + " vendidos")
 * );
 * }</pre>
 * 
 * @author Equipo StockFlow
 * @version 1.0
 * @since 2025
 * 
 * @see VentaServicio
 * @see ProductoServicio
 * @see AnaliticaAvanzadaServicio
 */
public class EstadisticasServicio {
    
    /**
     * Servicio de gestión de ventas.
     */
    private final VentaServicio ventaServicio;
    
    /**
     * Servicio de gestión de productos.
     */
    private final ProductoServicio productoServicio;
    
    /**
     * Constructor por defecto.
     * <p>Inicializa los servicios necesarios para acceder a datos 
     * de ventas y productos.</p>
     */
    public EstadisticasServicio() {
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
    }
    
    /**
     * Calcula el total acumulado de todas las ventas del sistema.
     * 
     * <p>Suma el valor total de todas las ventas registradas, 
     * sin filtros de fecha o período. Útil para:</p>
     * <ul>
     *   <li>Mostrar ingresos históricos totales</li>
     *   <li>KPI principal de dashboard</li>
     *   <li>Calcular crecimiento desde inicio de operaciones</li>
     * </ul>
     * 
     * <p><strong>Nota de rendimiento:</strong> Esta operación suma todas 
     * las ventas en memoria. Para sistemas con millones de registros, 
     * considere implementar caché o agregación en base de datos.</p>
     * 
     * @return Total acumulado de ventas en moneda del sistema
     * 
     * @see Venta#getTotal()
     */
    public double calcularVentasTotales() {
        return ventaServicio.listarVentas().stream()
                .mapToDouble(Venta::getTotal)
                .sum();
    }
    
    /**
     * Calcula el total de ventas de un mes específico.
     * 
     * <p>Filtra y suma las ventas que ocurrieron en el mes y año indicados. 
     * Los meses se numeran de 1 (enero) a 12 (diciembre).</p>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Reportes mensuales de ventas</li>
     *   <li>Comparaciones mes a mes</li>
     *   <li>Evaluación de cumplimiento de metas mensuales</li>
     *   <li>Análisis de estacionalidad</li>
     * </ul>
     * 
     * <p><strong>Ejemplo:</strong></p>
     * <pre>{@code
     * // Ventas de marzo 2025
     * double ventasMarzo = servicio.calcularVentasMes(3, 2025);
     * 
     * // Ventas del mes actual
     * LocalDateTime ahora = LocalDateTime.now();
     * double ventasMesActual = servicio.calcularVentasMes(
     *     ahora.getMonthValue(), 
     *     ahora.getYear()
     * );
     * }</pre>
     * 
     * @param mes Número del mes (1-12): 1=Enero, 2=Febrero, ..., 12=Diciembre
     * @param año Año a consultar (ej: 2025)
     * @return Total de ventas del mes especificado
     * 
     * @see LocalDateTime#getMonthValue()
     * @see LocalDateTime#getYear()
     */
    public double calcularVentasMes(int mes, int año) {
        return ventaServicio.listarVentas().stream()
                .filter(v -> v.getFecha().getMonthValue() == mes && v.getFecha().getYear() == año)
                .mapToDouble(Venta::getTotal)
                .sum();
    }
    
    /**
     * Obtiene los productos más vendidos por cantidad de unidades.
     * 
     * <p>Identifica los productos con mayor volumen de ventas, 
     * ordenados por cantidad total de unidades vendidas (no por valor monetario).</p>
     * 
     * <p><strong>Proceso:</strong></p>
     * <ol>
     *   <li>Recorre todas las ventas y sus detalles</li>
     *   <li>Suma las cantidades vendidas por código de producto</li>
     *   <li>Ordena por cantidad descendente</li>
     *   <li>Limita a N productos según el parámetro</li>
     *   <li>Busca y retorna objetos Producto completos</li>
     * </ol>
     * 
     * <p><strong>Diferencias con otras métricas:</strong></p>
     * <ul>
     *   <li><strong>obtenerProductosMasVendidos:</strong> Por cantidad de unidades</li>
     *   <li><strong>obtenerProductosMasRentables:</strong> Por ingresos totales</li>
     *   <li><strong>Top rentables (ganancias):</strong> Por margen de ganancia</li>
     * </ul>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Identificar productos estrella</li>
     *   <li>Priorizar reabastecimiento</li>
     *   <li>Planificar promociones</li>
     *   <li>Análisis de popularidad</li>
     * </ul>
     * 
     * <p><strong>Nota:</strong> Los productos que ya no existen en el sistema 
     * (eliminados) son filtrados automáticamente (retorna null y se excluyen).</p>
     * 
     * @param limite Cantidad de productos a retornar (ej: 5 para top 5)
     * @return Lista ordenada de productos más vendidos (por cantidad), 
     *         limitada al número especificado
     * 
     * @see DetalleVenta#getCantidad()
     * @see ProductoServicio#buscarProducto(String)
     */
    public List<Producto> obtenerProductosMasVendidos(int limite) {
        Map<String, Integer> conteoVentas = new HashMap<>();
        
        // Contar unidades vendidas por producto
        for (Venta venta : ventaServicio.listarVentas()) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                String codigo = detalle.getProducto().getCodigo();
                conteoVentas.put(codigo, conteoVentas.getOrDefault(codigo, 0) + detalle.getCantidad());
            }
        }
        
        // Ordenar y limitar
        return conteoVentas.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .map(entry -> {
                    try {
                        return productoServicio.buscarProducto(entry.getKey());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    /**
     * Cuenta la cantidad de ventas realizadas en el día actual.
     * 
     * <p>Filtra las ventas cuya fecha coincide con el día de hoy 
     * (sin importar la hora). Útil para:</p>
     * <ul>
     *   <li>Indicador de actividad del día en dashboard</li>
     *   <li>Seguimiento de metas diarias</li>
     *   <li>Alertas de bajo desempeño</li>
     *   <li>Métricas de productividad del personal</li>
     * </ul>
     * 
     * <p><strong>Nota:</strong> Retorna el número de transacciones, 
     * no el valor monetario total.</p>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>{@code
     * int ventasHoy = servicio.contarVentasDelDia();
     * 
     * if (ventasHoy == 0) {
     *     System.out.println("¡Aún no hay ventas hoy!");
     * } else if (ventasHoy < 10) {
     *     System.out.println("Día lento: " + ventasHoy + " ventas");
     * } else {
     *     System.out.println("Buen día: " + ventasHoy + " ventas");
     * }
     * }</pre>
     * 
     * @return Cantidad de ventas realizadas hoy (número de transacciones)
     * 
     * @see LocalDateTime#now()
     * @see LocalDateTime#toLocalDate()
     */
    public int contarVentasDelDia() {
        LocalDateTime hoy = LocalDateTime.now();
        return (int) ventaServicio.listarVentas().stream()
                .filter(v -> v.getFecha().toLocalDate().equals(hoy.toLocalDate()))
                .count();
    }
    
    /**
     * Obtiene la distribución de ventas por mes del año.
     * 
     * <p>Calcula el total de ventas para cada mes de un año específico 
     * y retorna un mapa ordenado con nombres abreviados de meses.</p>
     * 
     * <p><strong>Formato de salida:</strong></p>
     * <pre>
     * Ene -> $12,450.00
     * Feb -> $15,320.50
     * Mar -> $18,750.25
     * ...
     * Dic -> $22,890.75
     * </pre>
     * 
     * <p><strong>Características:</strong></p>
     * <ul>
     *   <li>Meses en orden cronológico (LinkedHashMap)</li>
     *   <li>Nombres de meses en español abreviados (3 letras)</li>
     *   <li>Incluye todos los 12 meses (con 0 si no hay ventas)</li>
     *   <li>Valores en formato double para precisión monetaria</li>
     * </ul>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Gráficas de tendencia anual</li>
     *   <li>Reportes anuales de desempeño</li>
     *   <li>Análisis de estacionalidad</li>
     *   <li>Comparación año a año</li>
     *   <li>Identificación de temporadas altas/bajas</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>{@code
     * Map<String, Double> ventasPorMes = servicio.obtenerVentasPorMes(2025);
     * 
     * // Generar gráfica de barras
     * ventasPorMes.forEach((mes, total) -> {
     *     System.out.printf("%s: $%,.2f%n", mes, total);
     * });
     * 
     * // Identificar mejor mes
     * String mejorMes = ventasPorMes.entrySet().stream()
     *     .max(Map.Entry.comparingByValue())
     *     .map(Map.Entry::getKey)
     *     .orElse("N/A");
     * 
     * System.out.println("Mejor mes: " + mejorMes);
     * }</pre>
     * 
     * @param año Año a analizar (ej: 2025)
     * @return Mapa ordenado con nombre de mes abreviado como clave 
     *         y total de ventas como valor. Incluye todos los 12 meses.
     * 
     * @see #calcularVentasMes(int, int)
     * @see LinkedHashMap
     */
    public Map<String, Double> obtenerVentasPorMes(int año) {
        Map<String, Double> ventasPorMes = new LinkedHashMap<>();
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        
        for (int i = 1; i <= 12; i++) {
            double total = calcularVentasMes(i, año);
            ventasPorMes.put(meses[i-1], total);
        }
        
        return ventasPorMes;
    }
}