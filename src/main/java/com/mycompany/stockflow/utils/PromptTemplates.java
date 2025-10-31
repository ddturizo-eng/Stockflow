/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.Modelo.*;
import java.util.List;

public class PromptTemplates {

    // Esta función genera un prompt detallado para que la IA analice el inventario de productos.
    public static String generarPromptAnalisisInventario(List<Producto> productos) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un experto en gestión de inventario. Analiza los siguientes productos:\n\n");

        // Recorro cada producto y agrego su información estructurada al prompt.
        for (Producto p : productos) {
            prompt.append(String.format(
                "- Producto: %s (Código: %s)\n" +
                "  Categoría: %s\n" +
                "  Precio: $%.2f\n" +
                "  Stock actual: %d\n" +
                "  Stock mínimo: %d\n" +
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

        // Solicito a la IA que realice un análisis específico sobre el inventario.
        prompt.append("\nProporciona:\n");
        prompt.append("1. Productos que requieren reabastecimiento urgente\n");
        prompt.append("2. Recomendaciones de cantidad de compra\n");
        prompt.append("3. Productos con exceso de inventario\n");
        prompt.append("4. Sugerencias de optimización\n");

        return prompt.toString();
    }

    // Esta función genera un prompt para que la IA analice las ventas y los productos disponibles.
    public static String generarPromptAnalisisVentas(List<Venta> ventas, List<Producto> productos) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un experto en análisis de ventas. Analiza los siguientes datos:\n\n");

        // Agrego un resumen general de las ventas.
        prompt.append("RESUMEN DE VENTAS\n");
        prompt.append(String.format("Total de ventas registradas: %d\n", ventas.size()));

        double totalIngresos = ventas.stream().mapToDouble(Venta::getTotal).sum();
        prompt.append(String.format("Ingresos totales: $%.2f\n\n", totalIngresos));

        // Listo los productos disponibles con su precio y stock.
        prompt.append("PRODUCTOS DISPONIBLES\n");
        for (Producto p : productos) {
            prompt.append(String.format("- %s: $%.2f (Stock: %d)\n",
                p.getNombre(), p.getPrecio(), p.getStock()));
        }

        // Solicito a la IA que realice un análisis detallado de ventas.
        prompt.append("\nANÁLISIS REQUERIDO\n");
        prompt.append("1. Identifica productos más vendidos\n");
        prompt.append("2. Productos con bajo movimiento\n");
        prompt.append("3. Tendencias de ventas\n");
        prompt.append("4. Recomendaciones para aumentar ventas\n");
        prompt.append("5. Sugerencias de promociones\n");

        return prompt.toString();
    }

    // Esta función genera un prompt para obtener recomendaciones específicas sobre un producto.
    public static String generarPromptRecomendacionProducto(Producto producto, int ventasRecientes) {
        return String.format(
            "Analiza este producto y dame recomendaciones específicas:\n\n" +
            "Producto: %s\n" +
            "Categoría: %s\n" +
            "Precio actual: $%.2f\n" +
            "Stock actual: %d unidades\n" +
            "Stock mínimo: %d unidades\n" +
            "Ventas últimos 30 días: %d unidades\n\n" +
            "Proporciona:\n" +
            "1. ¿Es el precio competitivo?\n" +
            "2. ¿Cuánto stock mantener?\n" +
            "3. ¿Necesita promoción?\n" +
            "4. Sugerencias de mejora\n",
            producto.getNombre(),
            producto.getCategoria(),
            producto.getPrecio(),
            producto.getStock(),
            producto.getStockMinimo(),
            ventasRecientes
        );
    }

    // Esta función genera un prompt para que la IA analice el comportamiento de los clientes.
    public static String generarPromptAnalisisClientes(List<Cliente> clientes, List<Venta> ventas) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un experto en análisis de comportamiento de clientes. Analiza:\n\n");

        // Agrego datos generales sobre clientes y ventas.
        prompt.append(String.format("Total de clientes: %d\n", clientes.size()));
        prompt.append(String.format("Total de ventas: %d\n\n", ventas.size()));

        // Solicito a la IA que identifique patrones y oportunidades.
        prompt.append("Proporciona:\n");
        prompt.append("1. Patrones de compra identificados\n");
        prompt.append("2. Clientes más frecuentes (estimado)\n");
        prompt.append("3. Estrategias de fidelización\n");
        prompt.append("4. Oportunidades de venta cruzada\n");

        return prompt.toString();
    }

    // Esta función permite generar un prompt personalizado combinando el contexto del negocio con una pregunta específica.
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