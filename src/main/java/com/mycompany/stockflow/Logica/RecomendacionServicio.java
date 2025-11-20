/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.*;
import com.mycompany.stockflow.excepciones.*;
import com.mycompany.stockflow.utils.*;
import java.util.*;

/**
 * Servicio para la generación de recomendaciones inteligentes de negocio.
 * 
 * <p>Este servicio utiliza inteligencia artificial y análisis de datos para generar
 * recomendaciones accionables en diferentes áreas del negocio, ayudando en la toma
 * de decisiones estratégicas sobre inventario, precios, promociones y gestión de productos.</p>
 * 
 * <p>Tipos de recomendaciones generadas:</p>
 * <ul>
 *   <li><b>RESTOCK:</b> Reabastecimiento de productos con stock bajo</li>
 *   <li><b>PRECIO:</b> Ajustes de precios para optimizar rentabilidad</li>
 *   <li><b>PROMOCION:</b> Oportunidades para promociones y descuentos</li>
 *   <li><b>DESCONTINUAR:</b> Productos candidatos para ser eliminados del catálogo</li>
 * </ul>
 * 
 * <p>Cada recomendación incluye:</p>
 * <ul>
 *   <li>Descripción del problema o oportunidad</li>
 *   <li>Acción recomendada específica</li>
 *   <li>Justificación basada en datos</li>
 *   <li>Nivel de prioridad (ALTA, MEDIA, BAJA)</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see Recomendacion
 * @see DeepSeekAPIClient
 */
public class RecomendacionServicio {

    /** Cliente API para comunicación con el servicio de IA */
    private final DeepSeekAPIClient apiClient;
    
    /** Lista interna de todas las recomendaciones generadas en la sesión */
    private final List<Recomendacion> recomendacionesGeneradas;

    /**
     * Constructor que inicializa el cliente de IA y la lista de recomendaciones.
     * 
     * <p>Prepara el servicio para generar y almacenar recomendaciones
     * durante el ciclo de vida de la aplicación.</p>
     */
    public RecomendacionServicio() {
        this.apiClient = new DeepSeekAPIClient();
        this.recomendacionesGeneradas = new ArrayList<>();
    }

    /**
     * Genera una recomendación inteligente de reabastecimiento para un producto.
     * 
     * <p>Analiza el stock actual versus el stock mínimo del producto y genera
     * una recomendación que incluye:</p>
     * <ul>
     *   <li>Cantidad sugerida de compra (calculada por IA)</li>
     *   <li>Justificación basada en patrones de consumo</li>
     *   <li>Prioridad según la urgencia del reabastecimiento</li>
     * </ul>
     * 
     * <p>Si la IA no está disponible, utiliza un cálculo de respaldo basado en
     * mantener el doble del stock mínimo.</p>
     * 
     * @param producto el producto que necesita reabastecimiento
     * @return recomendación completa con cantidad y justificación
     * @throws AIAPIException si hay problemas de comunicación con la API de IA
     * @throws ConfiguracionAIFaltanteException si la configuración de IA no está completa
     */
    public Recomendacion generarRecomendacionRestock(Producto producto)
            throws AIAPIException, ConfiguracionAIFaltanteException {

        Recomendacion recomendacion = new Recomendacion(
            "RESTOCK",
            producto.getCodigo(),
            "Reabastecimiento necesario: " + producto.getNombre(),
            "El producto tiene stock bajo y requiere reabastecimiento",
            "ALTA"
        );

        try {
            String prompt = String.format(
                "Producto '%s' tiene stock actual de %d unidades y stock mínimo de %d. " +
                "¿Cuántas unidades recomiendas comprar y por qué? Responde de forma concisa.",
                producto.getNombre(),
                producto.getStock(),
                producto.getStockMinimo()
            );

            String respuestaIA = apiClient.enviarPrompt(prompt, 0.5, 300);

            recomendacion.setAccionRecomendada("Realizar pedido de reabastecimiento");
            recomendacion.setJustificacion(respuestaIA);

        } catch (Exception e) {
            int cantidadRecomendada = (producto.getStockMinimo() * 2) - producto.getStock();
            recomendacion.setAccionRecomendada(
                "Comprar aproximadamente " + cantidadRecomendada + " unidades"
            );
            recomendacion.setJustificacion(
                "Cálculo basado en mantener el doble del stock mínimo"
            );
        }

        recomendacionesGeneradas.add(recomendacion);
        return recomendacion;
    }

    /**
     * Genera una recomendación sobre la estrategia de precios de un producto.
     * 
     * <p>Analiza el precio actual del producto en relación con su desempeño
     * de ventas y genera sugerencias para:</p>
     * <ul>
     *   <li>Optimizar la rentabilidad</li>
     *   <li>Mejorar la competitividad</li>
     *   <li>Aumentar el volumen de ventas</li>
     *   <li>Ajustar márgenes según la rotación</li>
     * </ul>
     * 
     * @param producto el producto a analizar
     * @param ventas historial de ventas para análisis de desempeño
     * @return recomendación sobre estrategia de precios
     * @throws AIAPIException si hay problemas de comunicación con la API de IA
     * @throws ConfiguracionAIFaltanteException si la configuración de IA no está completa
     */
    public Recomendacion generarRecomendacionPrecio(Producto producto, List<Venta> ventas)
            throws AIAPIException, ConfiguracionAIFaltanteException {

        Recomendacion recomendacion = new Recomendacion(
            "PRECIO",
            producto.getCodigo(),
            "Revisión de precio: " + producto.getNombre(),
            "Análisis de competitividad y rentabilidad del precio actual",
            "MEDIA"
        );

        try {
            int ventasRecientes = contarVentasProducto(producto, ventas);

            String prompt = PromptTemplates.generarPromptRecomendacionProducto(producto, ventasRecientes);

            String respuestaIA = apiClient.enviarPrompt(prompt, 0.6, 500);

            recomendacion.setJustificacion(respuestaIA);
            recomendacion.setAccionRecomendada("Revisar estrategia de precios");

        } catch (Exception e) {
            recomendacion.setJustificacion(
                "Precio actual: $" + producto.getPrecio() + ". Revisar competencia."
            );
        }

        recomendacionesGeneradas.add(recomendacion);
        return recomendacion;
    }

    /**
     * Genera una recomendación para crear una promoción sobre un producto.
     * 
     * <p>Identifica oportunidades promocionales basándose en:</p>
     * <ul>
     *   <li>Exceso de inventario (stock alto)</li>
     *   <li>Rotación lenta del producto</li>
     *   <li>Oportunidades de ventas cruzadas</li>
     *   <li>Estacionalidad y tendencias</li>
     * </ul>
     * 
     * <p>La prioridad de la recomendación se ajusta automáticamente según
     * la urgencia (productos con mucho exceso tienen prioridad ALTA).</p>
     * 
     * @param producto el producto candidato para promoción
     * @return recomendación de estrategia promocional
     */
    public Recomendacion generarRecomendacionPromocion(Producto producto) {
        Recomendacion recomendacion = new Recomendacion(
            "PROMOCION",
            producto.getCodigo(),
            "Oportunidad de promoción: " + producto.getNombre(),
            "El producto podría beneficiarse de una estrategia promocional",
            "BAJA"
        );

        if (producto.getStock() > producto.getStockMinimo() * 3) {
            recomendacion.setPrioridad("ALTA");
            recomendacion.setAccionRecomendada(
                "Crear promoción para reducir exceso de inventario"
            );
            recomendacion.setJustificacion(
                "Stock actual (" + producto.getStock() + ") es " +
                (producto.getStock() / producto.getStockMinimo()) +
                " veces el stock mínimo. Promoción recomendada."
            );
        } else {
            recomendacion.setAccionRecomendada("Considerar promoción cruzada");
            recomendacion.setJustificacion(
                "Producto con potencial para ventas cruzadas"
            );
        }

        recomendacionesGeneradas.add(recomendacion);
        return recomendacion;
    }

    /**
     * Genera una recomendación para descontinuar un producto de baja rotación.
     * 
     * <p>Identifica productos que:</p>
     * <ul>
     *   <li>No han tenido ventas recientes</li>
     *   <li>Tienen stock inmovilizado</li>
     *   <li>Representan capital sin retorno</li>
     *   <li>Ocupan espacio sin generar valor</li>
     * </ul>
     * 
     * <p>La recomendación incluye el valor del inventario inmovilizado para
     * ayudar en la toma de decisiones financieras.</p>
     * 
     * @param producto el producto a evaluar para descontinuación
     * @param ventas historial de ventas para análisis de movimiento
     * @return recomendación sobre descontinuación del producto
     */
    public Recomendacion generarRecomendacionDescontinuar(Producto producto, List<Venta> ventas) {
        Recomendacion recomendacion = new Recomendacion(
            "DESCONTINUAR",
            producto.getCodigo(),
            "Considerar descontinuar: " + producto.getNombre(),
            "Producto con bajo o nulo movimiento",
            "MEDIA"
        );

        int ventasRecientes = contarVentasProducto(producto, ventas);

        if (ventasRecientes == 0 && producto.getStock() > 0) {
            recomendacion.setPrioridad("ALTA");
            recomendacion.setAccionRecomendada(
                "Liquidar inventario y descontinuar producto"
            );
            recomendacion.setJustificacion(
                "Sin ventas recientes. Stock actual: " + producto.getStock() +
                " unidades. Valor inmovilizado: $" +
                (producto.getPrecio() * producto.getStock())
            );
        } else {
            recomendacion.setAccionRecomendada("Monitorear desempeño");
            recomendacion.setJustificacion(
                "Ventas bajas (" + ventasRecientes + "). Evaluar en próximo periodo."
            );
        }

        recomendacionesGeneradas.add(recomendacion);
        return recomendacion;
    }

    /**
     * Obtiene todas las recomendaciones generadas en la sesión actual.
     * 
     * <p>Retorna una copia de la lista para evitar modificaciones externas.</p>
     * 
     * @return lista completa de recomendaciones generadas
     */
    public List<Recomendacion> obtenerTodasRecomendaciones() {
        return new ArrayList<>(recomendacionesGeneradas);
    }

    /**
     * Filtra y obtiene recomendaciones por nivel de prioridad.
     * 
     * <p>Útil para mostrar primero las recomendaciones más urgentes
     * o para generar reportes segmentados por prioridad.</p>
     * 
     * @param prioridad nivel de prioridad a filtrar: "ALTA", "MEDIA" o "BAJA"
     * @return lista de recomendaciones que coinciden con la prioridad especificada
     */
    public List<Recomendacion> obtenerRecomendacionesPorPrioridad(String prioridad) {
        List<Recomendacion> filtradas = new ArrayList<>();

        for (Recomendacion r : recomendacionesGeneradas) {
            if (r.getPrioridad().equals(prioridad)) {
                filtradas.add(r);
            }
        }

        return filtradas;
    }

    /**
     * Marca una recomendación como aplicada.
     * 
     * <p>Útil para hacer seguimiento de qué recomendaciones ya fueron
     * implementadas por el usuario y cuáles están pendientes.</p>
     * 
     * @param recomendacionId identificador único de la recomendación
     */
    public void marcarRecomendacionAplicada(String recomendacionId) {
        for (Recomendacion r : recomendacionesGeneradas) {
            if (r.getId().equals(recomendacionId)) {
                r.setAplicada(true);
                break;
            }
        }
    }

    /**
     * Elimina todas las recomendaciones que ya fueron aplicadas.
     * 
     * <p>Mantiene la lista limpia eliminando recomendaciones completadas,
     * útil para mostrar solo recomendaciones pendientes de acción.</p>
     */
    public void limpiarRecomendacionesAntiguas() {
        recomendacionesGeneradas.removeIf(Recomendacion::isAplicada);
    }

    /**
     * Cuenta el total de unidades vendidas de un producto específico.
     * 
     * <p>Recorre todas las ventas y sus detalles para sumar las cantidades
     * vendidas del producto en cuestión.</p>
     * 
     * @param producto el producto del cual contar las ventas
     * @param ventas lista de ventas a analizar
     * @return número total de unidades vendidas del producto
     */
    private int contarVentasProducto(Producto producto, List<Venta> ventas) {
        int contador = 0;

        if (ventas != null) {
            for (Venta v : ventas) {
                if (v.getDetalles() != null) {
                    for (DetalleVenta d : v.getDetalles()) {
                        if (d.getProducto() != null &&
                            d.getProducto().getCodigo().equals(producto.getCodigo())) {
                            contador += d.getCantidad();
                        }
                    }
                }
            }
        }

        return contador;
    }
}