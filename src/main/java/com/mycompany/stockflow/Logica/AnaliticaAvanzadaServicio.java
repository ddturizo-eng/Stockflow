/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de Analítica Avanzada para Métricas de Negocio.
 * 
 * <p>Proporciona cálculos avanzados y métricas para análisis profundo 
 * del desempeño del negocio, incluyendo ROI, rotación de inventario, 
 * estacionalidad y predicción de demanda.</p>
 * 
 * <p>Capacidades principales:</p>
 * <ul>
 *   <li>Cálculo de ROI (Retorno de Inversión) por producto</li>
 *   <li>Identificación de productos más rentables</li>
 *   <li>Detección de productos con bajo movimiento</li>
 *   <li>Análisis de tasa de rotación de inventario</li>
 *   <li>Análisis de estacionalidad de ventas</li>
 *   <li>Predicción de demanda futura</li>
 *   <li>Cálculo de punto de reorden óptimo</li>
 *   <li>Análisis de concentración de clientes</li>
 *   <li>Cálculo de Customer Lifetime Value (CLV)</li>
 *   <li>Identificación de productos complementarios</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>{@code
 * AnaliticaAvanzadaServicio servicio = new AnaliticaAvanzadaServicio();
 * 
 * // Calcular ROI de productos
 * Map<String, Double> roi = servicio.calcularROIProductos(productos, ventas);
 * roi.forEach((codigo, porcentaje) -> 
 *     System.out.println("Producto: " + codigo + ", ROI: " + porcentaje + "%")
 * );
 * 
 * // Obtener productos más rentables
 * List<Producto> topRentables = servicio.obtenerProductosMasRentables(
 *     productos, ventas, 10
 * );
 * 
 * // Predecir demanda futura
 * int demandaProyectada = servicio.predecirDemanda(producto, ventas, 30);
 * System.out.println("Demanda estimada para 30 días: " + demandaProyectada);
 * }</pre>
 * 
 * @author Equipo StockFlow
 * @version 1.0
 * @since 2025
 * 
 * @see Producto
 * @see Venta
 * @see Cliente
 * @see DetalleVenta
 */
public class AnaliticaAvanzadaServicio {

    /**
     * Constructor por defecto.
     */
    public AnaliticaAvanzadaServicio() {
    }

    /**
     * Calcula el ROI (Retorno de Inversión) para cada producto.
     * 
     * <p>El ROI se calcula comparando el costo de tener el producto en 
     * inventario contra las ventas generadas. La fórmula utilizada es:</p>
     * 
     * <pre>
     * ROI (%) = ((Ventas Totales - Costo Inventario) / Costo Inventario) × 100
     * 
     * Donde:
     * - Ventas Totales = Suma de todos los subtotales de ventas del producto
     * - Costo Inventario = Precio del producto × Stock actual
     * </pre>
     * 
     * <p><strong>Interpretación del ROI:</strong></p>
     * <ul>
     *   <li><strong>ROI &gt; 0:</strong> El producto genera ganancias</li>
     *   <li><strong>ROI = 0:</strong> El producto está en punto de equilibrio</li>
     *   <li><strong>ROI &lt; 0:</strong> El producto genera pérdidas</li>
     * </ul>
     * 
     * <p><strong>Nota:</strong> Solo se calculan productos con costo de inventario mayor a 0.</p>
     * 
     * @param productos Lista de productos a analizar
     * @param ventas Lista de ventas históricas para calcular ingresos
     * @return Mapa con código de producto como clave y ROI (%) como valor
     * 
     * @see #calcularVentasProducto(Producto, List)
     */
    public Map<String, Double> calcularROIProductos(List<Producto> productos, List<Venta> ventas) {
        Map<String, Double> roiPorProducto = new HashMap<>();

        for (Producto p : productos) {
            double costoInventario = p.getPrecio() * p.getStock();
            double ventasProducto = calcularVentasProducto(p, ventas);

            if (costoInventario > 0) {
                double roi = ((ventasProducto - costoInventario) / costoInventario) * 100;
                roiPorProducto.put(p.getCodigo(), roi);
            }
        }

        return roiPorProducto;
    }

    /**
     * Selecciona los productos que más ingresos han generado.
     * 
     * <p>Ordena los productos por total de ventas generadas en orden 
     * descendente y retorna los primeros N productos. Útil para:</p>
     * <ul>
     *   <li>Identificar productos estrella</li>
     *   <li>Enfocar estrategias de marketing</li>
     *   <li>Priorizar reabastecimiento</li>
     *   <li>Análisis ABC de inventario</li>
     * </ul>
     * 
     * <p>El cálculo se basa en la suma de todos los subtotales de ventas 
     * donde aparece cada producto.</p>
     * 
     * @param productos Lista completa de productos
     * @param ventas Lista de ventas históricas
     * @param top Cantidad de productos a retornar (ej: top 10)
     * @return Lista ordenada de los productos más rentables (limitada a 'top')
     * 
     * @see #calcularVentasProducto(Producto, List)
     */
    public List<Producto> obtenerProductosMasRentables(List<Producto> productos, List<Venta> ventas, int top) {
        Map<String, Double> ventasPorProducto = new HashMap<>();

        // Calcular ventas totales por producto
        for (Producto p : productos) {
            double ventasProducto = calcularVentasProducto(p, ventas);
            ventasPorProducto.put(p.getCodigo(), ventasProducto);
        }

        // Ordenar y limitar
        return productos.stream()
            .sorted((p1, p2) -> Double.compare(
                ventasPorProducto.getOrDefault(p2.getCodigo(), 0.0),
                ventasPorProducto.getOrDefault(p1.getCodigo(), 0.0)
            ))
            .limit(top)
            .collect(Collectors.toList());
    }

    /**
     * Identifica productos con stock pero sin ventas (bajo movimiento).
     * 
     * <p>Un producto se considera de bajo movimiento cuando:</p>
     * <ul>
     *   <li>Tiene stock disponible (cantidad &gt; 0)</li>
     *   <li>No ha registrado ventas en el período analizado</li>
     * </ul>
     * 
     * <p><strong>Implicaciones:</strong></p>
     * <ul>
     *   <li>Capital inmovilizado en inventario</li>
     *   <li>Posibles costos de almacenamiento innecesarios</li>
     *   <li>Riesgo de obsolescencia del producto</li>
     * </ul>
     * 
     * <p><strong>Acciones sugeridas:</strong></p>
     * <ul>
     *   <li>Aplicar descuentos o promociones</li>
     *   <li>Considerar descontinuar el producto</li>
     *   <li>Revisar estrategia de marketing</li>
     *   <li>Analizar si hay productos sustitutos</li>
     * </ul>
     * 
     * @param productos Lista de productos a analizar
     * @param ventas Lista de ventas históricas
     * @return Lista de productos sin movimiento
     * 
     * @see #calcularVentasProducto(Producto, List)
     */
    public List<Producto> obtenerProductosBajoMovimiento(List<Producto> productos, List<Venta> ventas) {
        List<Producto> bajoMovimiento = new ArrayList<>();

        for (Producto p : productos) {
            double ventasProducto = calcularVentasProducto(p, ventas);
            if (ventasProducto == 0 && p.getStock() > 0) {
                bajoMovimiento.add(p);
            }
        }

        return bajoMovimiento;
    }

    /**
     * Calcula la tasa de rotación de inventario de un producto.
     * 
     * <p>La tasa de rotación indica cuántas veces se ha vendido y 
     * reemplazado el inventario en un período determinado:</p>
     * 
     * <pre>
     * Tasa de Rotación = Unidades Vendidas / Stock Promedio
     * </pre>
     * 
     * <p><strong>Interpretación:</strong></p>
     * <ul>
     *   <li><strong>Tasa alta (&gt; 5):</strong> Rotación rápida, buen desempeño</li>
     *   <li><strong>Tasa media (2-5):</strong> Rotación normal, aceptable</li>
     *   <li><strong>Tasa baja (&lt; 2):</strong> Rotación lenta, inventario estancado</li>
     * </ul>
     * 
     * <p><strong>Nota:</strong> En esta implementación se usa el stock actual 
     * como stock promedio. Para análisis más precisos, considere calcular el 
     * promedio real del período.</p>
     * 
     * @param producto Producto a analizar
     * @param ventas Lista de ventas históricas
     * @param diasPeriodo Días del período de análisis (no utilizado actualmente)
     * @return Tasa de rotación (número de veces que rotó el inventario)
     * 
     * @see #contarUnidadesVendidas(Producto, List)
     */
    public double calcularTasaRotacion(Producto producto, List<Venta> ventas, int diasPeriodo) {
        int unidadesVendidas = contarUnidadesVendidas(producto, ventas);
        double stockPromedio = producto.getStock();

        if (stockPromedio > 0) {
            return (double) unidadesVendidas / stockPromedio;
        }
        return 0.0;
    }

    /**
     * Analiza patrones estacionales de ventas por mes.
     * 
     * <p>Agrupa las ventas por mes del año para identificar períodos de 
     * alta y baja demanda. Esta información es crucial para:</p>
     * <ul>
     *   <li><strong>Planificación de inventario:</strong> Aumentar stock antes de temporadas altas</li>
     *   <li><strong>Estrategias de marketing:</strong> Campañas en meses de baja demanda</li>
     *   <li><strong>Previsión de flujo de caja:</strong> Anticipar ingresos estacionales</li>
     *   <li><strong>Gestión de personal:</strong> Ajustar dotación según demanda</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de salida:</strong></p>
     * <pre>
     * JANUARY  -> $15,240.50
     * FEBRUARY -> $12,890.75
     * MARCH    -> $18,450.00
     * ...
     * </pre>
     * 
     * @param ventas Lista completa de ventas históricas
     * @return Mapa con nombre del mes (en inglés) y total de ventas en ese mes
     */
    public Map<String, Double> analizarEstacionalidad(List<Venta> ventas) {
        Map<String, Double> ventasPorMes = new HashMap<>();

        for (Venta v : ventas) {
            if (v.getFecha() != null) {
                String mes = v.getFecha().getMonth().toString();
                ventasPorMes.put(mes,
                    ventasPorMes.getOrDefault(mes, 0.0) + v.getTotal());
            }
        }

        return ventasPorMes;
    }

    /**
     * Predice la demanda futura de un producto basándose en historial de ventas.
     * 
     * <p>Utiliza el promedio de ventas históricas para proyectar 
     * ventas futuras mediante una extrapolación lineal simple:</p>
     * 
     * <pre>
     * Ventas Diarias Promedio = Unidades Vendidas / Días Históricos
     * Demanda Proyectada = Ventas Diarias Promedio × Días de Proyección
     * </pre>
     * 
     * <p><strong>Nota importante:</strong> Este es un método de predicción simple 
     * basado en promedios. Para análisis más sofisticados, considere:</p>
     * <ul>
     *   <li>Modelos de series temporales (ARIMA, SARIMA)</li>
     *   <li>Ajuste por estacionalidad</li>
     *   <li>Análisis de tendencias</li>
     *   <li>Machine learning (regresión, redes neuronales)</li>
     * </ul>
     * 
     * <p><strong>Limitaciones:</strong></p>
     * <ul>
     *   <li>No considera tendencias de crecimiento/decrecimiento</li>
     *   <li>No ajusta por estacionalidad</li>
     *   <li>Asume período histórico fijo de 30 días</li>
     *   <li>No considera eventos externos (promociones, competencia)</li>
     * </ul>
     * 
     * @param producto Producto a proyectar
     * @param ventas Historial de ventas
     * @param diasProyeccion Días hacia adelante a proyectar
     * @return Cantidad estimada de unidades que se venderán
     * 
     * @see #contarUnidadesVendidas(Producto, List)
     */
    public int predecirDemanda(Producto producto, List<Venta> ventas, int diasProyeccion) {
        int unidadesVendidas = contarUnidadesVendidas(producto, ventas);
        int diasHistorico = 30; // Período histórico fijo

        if (diasHistorico > 0) {
            double ventasDiarias = (double) unidadesVendidas / diasHistorico;
            return (int) Math.ceil(ventasDiarias * diasProyeccion);
        }

        return 0;
    }

    /**
     * Calcula el punto óptimo para reordenar un producto.
     * 
     * <p>El punto de reorden (ROP - Reorder Point) indica el nivel de stock 
     * en el cual se debe realizar un nuevo pedido para evitar quiebres de 
     * inventario. La fórmula utilizada es:</p>
     * 
     * <pre>
     * Punto de Reorden = (Demanda Diaria × Días de Entrega) + Stock de Seguridad
     * 
     * Donde:
     * - Demanda Diaria = Predicción basada en historial de ventas
     * - Días de Entrega = Lead time del proveedor
     * - Stock de Seguridad = Stock mínimo configurado del producto
     * </pre>
     * 
     * <p><strong>Componentes del cálculo:</strong></p>
     * <ul>
     *   <li><strong>Lead Time Stock:</strong> Inventario necesario durante el tiempo de entrega</li>
     *   <li><strong>Stock de Seguridad:</strong> Colchón para variabilidad de demanda</li>
     * </ul>
     * 
     * <p><strong>Ejemplo práctico:</strong></p>
     * <pre>{@code
     * // Producto que vende 10 unidades/día
     * // Proveedor entrega en 5 días
     * // Stock mínimo de seguridad: 20 unidades
     * 
     * Punto Reorden = (10 × 5) + 20 = 70 unidades
     * 
     * Interpretación: Cuando el stock llegue a 70 unidades, 
     * hacer pedido para evitar quiebre de stock.
     * }</pre>
     * 
     * @param producto Producto a calcular
     * @param ventas Historial de ventas para calcular demanda
     * @param diasEntrega Tiempo de entrega del proveedor en días
     * @return Nivel de stock en el cual se debe reordenar
     * 
     * @see #predecirDemanda(Producto, List, int)
     */
    public int calcularPuntoReorden(Producto producto, List<Venta> ventas, int diasEntrega) {
        int demandaDiaria = predecirDemanda(producto, ventas, 1);
        int stockSeguridad = producto.getStockMinimo();

        return (demandaDiaria * diasEntrega) + stockSeguridad;
    }

    /**
     * Analiza la concentración de ventas por cliente.
     * 
     * <p>Identifica qué clientes generan más ingresos para el negocio. 
     * Este análisis es fundamental para:</p>
     * <ul>
     *   <li><strong>Regla 80/20 (Principio de Pareto):</strong> 
     *       Identificar si el 80% de ingresos viene del 20% de clientes</li>
     *   <li><strong>Programas de lealtad:</strong> Recompensar clientes valiosos</li>
     *   <li><strong>Estrategias de retención:</strong> Evitar pérdida de clientes clave</li>
     *   <li><strong>Segmentación:</strong> Crear estrategias por segmento de valor</li>
     *   <li><strong>Gestión de riesgo:</strong> Evitar dependencia excesiva de pocos clientes</li>
     * </ul>
     * 
     * <p><strong>Categorización sugerida de clientes:</strong></p>
     * <ul>
     *   <li><strong>VIP:</strong> Top 10% de clientes por volumen</li>
     *   <li><strong>Premium:</strong> 10%-30% siguientes</li>
     *   <li><strong>Regular:</strong> 30%-70%</li>
     *   <li><strong>Ocasional:</strong> Resto</li>
     * </ul>
     * 
     * @param ventas Lista de ventas históricas
     * @return Mapa con cédula de cliente como clave y total de ventas como valor
     */
    public Map<String, Double> analizarConcentracionClientes(List<Venta> ventas) {
        Map<String, Double> ventasPorCliente = new HashMap<>();

        for (Venta v : ventas) {
            if (v.getCliente() != null) {
                String clienteId = v.getCliente().getCedula();
                ventasPorCliente.put(clienteId,
                    ventasPorCliente.getOrDefault(clienteId, 0.0) + v.getTotal());
            }
        }

        return ventasPorCliente;
    }

    /**
     * Calcula el Customer Lifetime Value (CLV) de un cliente.
     * 
     * <p>El CLV estima el valor total que un cliente aportará durante 
     * toda su relación con el negocio. Es una métrica crítica para:</p>
     * <ul>
     *   <li>Determinar cuánto invertir en adquisición de clientes</li>
     *   <li>Identificar clientes más valiosos</li>
     *   <li>Justificar programas de retención</li>
     *   <li>Tomar decisiones estratégicas de marketing</li>
     * </ul>
     * 
     * <p><strong>Fórmula simplificada utilizada:</strong></p>
     * <pre>
     * Ticket Promedio = Total Compras / Número de Compras
     * CLV = Ticket Promedio × (Número de Compras × 2)
     * </pre>
     * 
     * <p><strong>Nota:</strong> Este es un cálculo simplificado. 
     * Para CLV más preciso, considere:</p>
     * <ul>
     *   <li><strong>Tasa de retención:</strong> Probabilidad de que el cliente vuelva</li>
     *   <li><strong>Frecuencia de compra:</strong> Cada cuánto compra</li>
     *   <li><strong>Margen de ganancia:</strong> No solo ingresos, sino ganancias</li>
     *   <li><strong>Valor presente neto:</strong> Descontar valor futuro</li>
     *   <li><strong>Ciclo de vida esperado:</strong> Cuánto tiempo será cliente</li>
     * </ul>
     * 
     * <p><strong>Fórmula avanzada de CLV:</strong></p>
     * <pre>
     * CLV = (Valor Promedio Pedido × Frecuencia Compra × Margen) 
     *       × (1 / (1 + Tasa Descuento - Tasa Retención))
     * </pre>
     * 
     * @param cliente Cliente a analizar
     * @param ventas Historial completo de ventas
     * @return Valor estimado del ciclo de vida del cliente
     */
    public double calcularCLV(Cliente cliente, List<Venta> ventas) {
        double totalCompras = ventas.stream()
            .filter(v -> v.getCliente() != null &&
                        v.getCliente().getCedula().equals(cliente.getCedula()))
            .mapToDouble(Venta::getTotal)
            .sum();

        long numeroCompras = ventas.stream()
            .filter(v -> v.getCliente() != null &&
                        v.getCliente().getCedula().equals(cliente.getCedula()))
            .count();

        if (numeroCompras > 0) {
            double ticketPromedio = totalCompras / numeroCompras;
            // Proyección simple: multiplicar por 2 para estimar valor futuro
            return ticketPromedio * (numeroCompras * 2);
        }

        return 0.0;
    }

    /**
     * Identifica productos que suelen comprarse juntos (Market Basket Analysis).
     * 
     * <p>Analiza las transacciones para detectar productos complementarios, 
     * es decir, aquellos que aparecen frecuentemente en la misma compra. 
     * Esta técnica es fundamental para:</p>
     * 
     * <p><strong>Estrategias de negocio:</strong></p>
     * <ul>
     *   <li><strong>Venta cruzada (Cross-selling):</strong> 
     *       "Los clientes que compraron X también compraron Y"</li>
     *   <li><strong>Organización de tienda:</strong> 
     *       Colocar productos complementarios cerca</li>
     *   <li><strong>Promociones combinadas:</strong> 
     *       Crear paquetes o descuentos por compra conjunta</li>
     *   <li><strong>Recomendaciones personalizadas:</strong> 
     *       Sugerir productos basados en compras actuales</li>
     *   <li><strong>Gestión de inventario:</strong> 
     *       Mantener stock coordinado de productos relacionados</li>
     * </ul>
     * 
     * <p><strong>Técnicas avanzadas relacionadas:</strong></p>
     * <ul>
     *   <li><strong>Reglas de asociación:</strong> Algoritmo Apriori, FP-Growth</li>
     *   <li><strong>Métricas:</strong> Support, Confidence, Lift</li>
     *   <li><strong>Machine Learning:</strong> Collaborative filtering</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de salida:</strong></p>
     * <pre>
     * "PROD-001" -> ["PROD-005", "PROD-012", "PROD-023"]
     * 
     * Interpretación: Los clientes que compran PROD-001 también 
     * suelen comprar PROD-005, PROD-012 y PROD-023
     * </pre>
     * 
     * <p><strong>Limitación actual:</strong> Esta implementación solo identifica 
     * co-ocurrencia, sin calcular métricas de fuerza de asociación. Para 
     * análisis más robusto, implemente cálculos de Support, Confidence y Lift.</p>
     * 
     * @param ventas Lista de ventas históricas
     * @return Mapa donde la clave es un código de producto y el valor 
     *         es una lista de códigos de productos que se compraron junto con él
     */
    public Map<String, List<String>> identificarProductosComplementarios(List<Venta> ventas) {
        Map<String, List<String>> complementarios = new HashMap<>();

        for (Venta v : ventas) {
            if (v.getDetalles() != null && v.getDetalles().size() > 1) {
                // Solo analizar ventas con múltiples productos
                for (DetalleVenta d1 : v.getDetalles()) {
                    String codigo1 = d1.getProducto().getCodigo();

                    complementarios.putIfAbsent(codigo1, new ArrayList<>());

                    for (DetalleVenta d2 : v.getDetalles()) {
                        if (!d1.equals(d2)) {
                            String codigo2 = d2.getProducto().getCodigo();
                            if (!complementarios.get(codigo1).contains(codigo2)) {
                                complementarios.get(codigo1).add(codigo2);
                            }
                        }
                    }
                }
            }
        }

        return complementarios;
    }

    /**
     * Calcula el total de ventas generado por un producto específico.
     * 
     * <p>Suma todos los subtotales de las ventas donde aparece el producto.
     * Este método es auxiliar para otros cálculos como ROI y productos rentables.</p>
     * 
     * @param producto Producto a analizar
     * @param ventas Lista de ventas históricas
     * @return Total monetario de ventas del producto
     */
    private double calcularVentasProducto(Producto producto, List<Venta> ventas) {
        double total = 0.0;

        for (Venta v : ventas) {
            if (v.getDetalles() != null) {
                for (DetalleVenta d : v.getDetalles()) {
                    if (d.getProducto() != null &&
                        d.getProducto().getCodigo().equals(producto.getCodigo())) {
                        total += d.getSubtotal();
                    }
                }
            }
        }

        return total;
    }

    /**
     * Cuenta el número total de unidades vendidas de un producto.
     * 
     * <p>Suma las cantidades de todas las ventas donde aparece el producto.
     * Este método es auxiliar para cálculos de rotación y predicción de demanda.</p>
     * 
     * @param producto Producto a analizar
     * @param ventas Lista de ventas históricas
     * @return Cantidad total de unidades vendidas
     */
    private int contarUnidadesVendidas(Producto producto, List<Venta> ventas) {
        int unidades = 0;

        for (Venta v : ventas) {
            if (v.getDetalles() != null) {
                for (DetalleVenta d : v.getDetalles()) {
                    if (d.getProducto() != null &&
                        d.getProducto().getCodigo().equals(producto.getCodigo())) {
                        unidades += d.getCantidad();
                    }
                }
            }
        }

        return unidades;
    }
}