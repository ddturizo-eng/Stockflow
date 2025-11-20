/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.Modelo.*;
import java.util.List;

/**
 * Plantillas de prompts para interaccion con la API de inteligencia artificial.
 * 
 * <p>Esta clase proporciona metodos para generar prompts estructurados y detallados
 * que seran enviados a la API de DeepSeek para obtener analisis inteligentes sobre
 * diferentes aspectos del negocio.</p>
 * 
 * <p>Los prompts generados incluyen:</p>
 * <ul>
 *   <li>Analisis de inventario y stock</li>
 *   <li>Analisis de ventas y tendencias</li>
 *   <li>Recomendaciones especificas por producto</li>
 *   <li>Analisis de comportamiento de clientes</li>
 *   <li>Consultas personalizadas con contexto</li>
 * </ul>
 * 
 * <p>Cada metodo estructura los datos del negocio de forma optima para que la IA
 * pueda generar respuestas precisas y accionables.</p>
 * 
 * @author StockFlow Team
 * @version 2.0
 * @since 1.0
 */
public class PromptTemplates {

    /**
     * Constructor privado para evitar instanciacion.
     * Esta clase solo contiene metodos estaticos.
     */
    private PromptTemplates() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable");
    }

    /**
     * Genera un prompt detallado para analisis de inventario.
     * 
     * <p>El prompt incluye para cada producto:</p>
     * <ul>
     *   <li>Nombre y codigo del producto</li>
     *   <li>Categoria</li>
     *   <li>Precio de venta</li>
     *   <li>Stock actual y minimo</li>
     *   <li>Estado de stock (bajo o normal)</li>
     * </ul>
     * 
     * <p>Solicita a la IA:</p>
     * <ol>
     *   <li>Productos que requieren reabastecimiento urgente</li>
     *   <li>Recomendaciones de cantidad de compra</li>
     *   <li>Productos con exceso de inventario</li>
     *   <li>Sugerencias de optimizacion</li>
     * </ol>
     * 
     * @param productos lista de productos a analizar
     * @return String con el prompt formateado para enviar a la IA
     */
    public static String generarPromptAnalisisInventario(List<Producto> productos) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un experto en gestion de inventario. Analiza los siguientes productos:\n\n");

        for (Producto p : productos) {
            prompt.append(String.format(
                "- Producto: %s (Codigo: %s)\n" +
                "  Categoria: %s\n" +
                "  Precio: $%.2f\n" +
                "  Stock actual: %d\n" +
                "  Stock minimo: %d\n" +
                "  Estado: %s\n\n",
                p.getNombre(),
                p.getCodigo(),
                p.getCategoria(),
                p.getPrecio(),
                p.getStock(),
                p.getStockMinimo(),
                p.tieneStockBajo() ? "STOCK BAJO" : "Stock normal"
            ));
        }

        prompt.append("\nProporciona:\n");
        prompt.append("1. Productos que requieren reabastecimiento urgente\n");
        prompt.append("2. Recomendaciones de cantidad de compra\n");
        prompt.append("3. Productos con exceso de inventario\n");
        prompt.append("4. Sugerencias de optimizacion\n");

        return prompt.toString();
    }

    /**
     * Genera un prompt para analisis de ventas y productos.
     * 
     * <p>El prompt incluye:</p>
     * <ul>
     *   <li>Resumen general de ventas (total y ingresos)</li>
     *   <li>Lista de productos disponibles con precios y stock</li>
     * </ul>
     * 
     * <p>Solicita a la IA:</p>
     * <ol>
     *   <li>Identificar productos mas vendidos</li>
     *   <li>Productos con bajo movimiento</li>
     *   <li>Tendencias de ventas</li>
     *   <li>Recomendaciones para aumentar ventas</li>
     *   <li>Sugerencias de promociones</li>
     * </ol>
     * 
     * @param ventas lista de ventas registradas
     * @param productos lista de productos disponibles
     * @return String con el prompt formateado
     */
    public static String generarPromptAnalisisVentas(List<Venta> ventas, List<Producto> productos) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un experto en analisis de ventas. Analiza los siguientes datos:\n\n");

        prompt.append("RESUMEN DE VENTAS\n");
        prompt.append(String.format("Total de ventas registradas: %d\n", ventas.size()));

        double totalIngresos = ventas.stream().mapToDouble(Venta::getTotal).sum();
        prompt.append(String.format("Ingresos totales: $%.2f\n\n", totalIngresos));

        prompt.append("PRODUCTOS DISPONIBLES\n");
        for (Producto p : productos) {
            prompt.append(String.format("- %s: $%.2f (Stock: %d)\n",
                p.getNombre(), p.getPrecio(), p.getStock()));
        }

        prompt.append("\nANALISIS REQUERIDO\n");
        prompt.append("1. Identifica productos mas vendidos\n");
        prompt.append("2. Productos con bajo movimiento\n");
        prompt.append("3. Tendencias de ventas\n");
        prompt.append("4. Recomendaciones para aumentar ventas\n");
        prompt.append("5. Sugerencias de promociones\n");

        return prompt.toString();
    }

    /**
     * Genera un prompt para recomendaciones especificas sobre un producto.
     * 
     * <p>Analiza un producto individual considerando:</p>
     * <ul>
     *   <li>Informacion basica del producto</li>
     *   <li>Stock actual vs minimo</li>
     *   <li>Ventas recientes (ultimos 30 dias)</li>
     * </ul>
     * 
     * <p>Solicita a la IA:</p>
     * <ol>
     *   <li>Evaluacion del precio (competitividad)</li>
     *   <li>Recomendacion de nivel de stock</li>
     *   <li>Necesidad de promocion</li>
     *   <li>Sugerencias de mejora generales</li>
     * </ol>
     * 
     * @param producto el producto a analizar
     * @param ventasRecientes numero de unidades vendidas en los ultimos 30 dias
     * @return String con el prompt formateado
     */
    public static String generarPromptRecomendacionProducto(Producto producto, int ventasRecientes) {
        return String.format(
            "Analiza este producto y dame recomendaciones especificas:\n\n" +
            "Producto: %s\n" +
            "Categoria: %s\n" +
            "Precio actual: $%.2f\n" +
            "Stock actual: %d unidades\n" +
            "Stock minimo: %d unidades\n" +
            "Ventas ultimos 30 dias: %d unidades\n\n" +
            "Proporciona:\n" +
            "1. Es el precio competitivo?\n" +
            "2. Cuanto stock mantener?\n" +
            "3. Necesita promocion?\n" +
            "4. Sugerencias de mejora\n",
            producto.getNombre(),
            producto.getCategoria(),
            producto.getPrecio(),
            producto.getStock(),
            producto.getStockMinimo(),
            ventasRecientes
        );
    }

    /**
     * Genera un prompt para analisis del comportamiento de clientes.
     * 
     * <p>Proporciona datos agregados sobre:</p>
     * <ul>
     *   <li>Numero total de clientes</li>
     *   <li>Numero total de ventas</li>
     * </ul>
     * 
     * <p>Solicita a la IA:</p>
     * <ol>
     *   <li>Identificar patrones de compra</li>
     *   <li>Estimar clientes mas frecuentes</li>
     *   <li>Estrategias de fidelizacion</li>
     *   <li>Oportunidades de venta cruzada</li>
     * </ol>
     * 
     * @param clientes lista de clientes registrados
     * @param ventas lista de ventas realizadas
     * @return String con el prompt formateado
     */
    public static String generarPromptAnalisisClientes(List<Cliente> clientes, List<Venta> ventas) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un experto en analisis de comportamiento de clientes. Analiza:\n\n");

        prompt.append(String.format("Total de clientes: %d\n", clientes.size()));
        prompt.append(String.format("Total de ventas: %d\n\n", ventas.size()));

        prompt.append("Proporciona:\n");
        prompt.append("1. Patrones de compra identificados\n");
        prompt.append("2. Clientes mas frecuentes (estimado)\n");
        prompt.append("3. Estrategias de fidelizacion\n");
        prompt.append("4. Oportunidades de venta cruzada\n");

        return prompt.toString();
    }

    /**
     * Genera un prompt personalizado combinando contexto del negocio y una pregunta especifica.
     * 
     * <p>Este metodo permite crear consultas flexibles donde:</p>
     * <ul>
     *   <li>El contexto puede ser cualquier informacion relevante del negocio</li>
     *   <li>La pregunta es la consulta especifica del usuario</li>
     * </ul>
     * 
     * <p>Es util para consultas que no encajan en las plantillas predefinidas.</p>
     * 
     * @param contexto informacion de contexto sobre el negocio o situacion
     * @param pregunta la pregunta o solicitud especifica del usuario
     * @return String con el prompt formateado
     */
    public static String generarPromptPersonalizado(String contexto, String pregunta) {
        return String.format(
            "Contexto del negocio:\n%s\n\n" +
            "Pregunta/Solicitud:\n%s\n\n" +
            "Por favor proporciona una respuesta detallada y accionable.",
            contexto,
            pregunta
        );
    }
}