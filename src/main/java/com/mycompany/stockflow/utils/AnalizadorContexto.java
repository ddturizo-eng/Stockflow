/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.Modelo.*;
import java.util.*;
import java.util.stream.Collectors;

public class AnalizadorContexto {

    // Esta función genera un resumen textual del negocio, combinando datos generales, productos y ventas.
    public static String generarResumenNegocio(ContextoNegocio contexto) {
        StringBuilder resumen = new StringBuilder();

        resumen.append("RESUMEN DEL NEGOCIO\n\n");

        // Agrego información general del periodo y totales.
        resumen.append(String.format("Periodo de análisis: %s\n",
            contexto.getPeriodoAnalisis() != null ? contexto.getPeriodoAnalisis() : "No especificado"));
        resumen.append(String.format("Total de productos: %d\n", contexto.getTotalProductos()));
        resumen.append(String.format("Total de clientes: %d\n", contexto.getTotalClientes()));
        resumen.append(String.format("Total de ventas: %d\n\n", contexto.getTotalVentas()));

        // Si hay productos, los analizo y agrego al resumen.
        if (contexto.getProductos() != null && !contexto.getProductos().isEmpty()) {
            resumen.append("ANÁLISIS DE PRODUCTOS\n");
            resumen.append(analizarProductos(contexto.getProductos()));
            resumen.append("\n");
        }

        // Si hay ventas, las analizo y agrego al resumen.
        if (contexto.getVentas() != null && !contexto.getVentas().isEmpty()) {
            resumen.append("ANÁLISIS DE VENTAS\n");
            resumen.append(analizarVentas(contexto.getVentas()));
            resumen.append("\n");
        }

        return resumen.toString();
    }

    // Esta función analiza los productos y genera estadísticas clave.
    private static String analizarProductos(List<Producto> productos) {
        StringBuilder analisis = new StringBuilder();

        // Identifico productos con stock bajo.
        List<Producto> stockBajo = productos.stream()
            .filter(Producto::tieneStockBajo)
            .collect(Collectors.toList());
        analisis.append(String.format("- Productos con stock bajo: %d\n", stockBajo.size()));

        // Calculo el valor total del inventario.
        double valorInventario = productos.stream()
            .mapToDouble(p -> p.getPrecio() * p.getStock())
            .sum();
        analisis.append(String.format("- Valor total del inventario: $%.2f\n", valorInventario));

        // Agrupo productos por categoría.
        Map<String, Long> categorias = productos.stream()
            .collect(Collectors.groupingBy(Producto::getCategoria, Collectors.counting()));
        analisis.append(String.format("- Categorías diferentes: %d\n", categorias.size()));

        // Calculo el precio promedio de los productos.
        double precioPromedio = productos.stream()
            .mapToDouble(Producto::getPrecio)
            .average()
            .orElse(0.0);
        analisis.append(String.format("- Precio promedio: $%.2f\n", precioPromedio));

        return analisis.toString();
    }

    // Esta función analiza las ventas y genera estadísticas clave.
    private static String analizarVentas(List<Venta> ventas) {
        StringBuilder analisis = new StringBuilder();

        // Calculo los ingresos totales.
        double ingresosTotales = ventas.stream()
            .mapToDouble(Venta::getTotal)
            .sum();
        analisis.append(String.format("- Ingresos totales: $%.2f\n", ingresosTotales));

        // Calculo el ticket promedio.
        double ticketPromedio = ingresosTotales / ventas.size();
        analisis.append(String.format("- Ticket promedio: $%.2f\n", ticketPromedio));

        // Identifico clientes únicos.
        Set<String> clientesUnicos = ventas.stream()
            .map(v -> v.getCliente() != null ? v.getCliente().getCedula() : "ANÓNIMO")
            .collect(Collectors.toSet());
        analisis.append(String.format("- Clientes únicos: %d\n", clientesUnicos.size()));

        return analisis.toString();
    }

    // Esta función extrae métricas clave del contexto para análisis estadístico.
    public static Map<String, Object> extraerMetricas(ContextoNegocio contexto) {
        Map<String, Object> metricas = new HashMap<>();

        if (contexto.getProductos() != null) {
            List<Producto> productos = contexto.getProductos();
            metricas.put("total_productos", productos.size());
            metricas.put("productos_stock_bajo",
                productos.stream().filter(Producto::tieneStockBajo).count());
            metricas.put("valor_inventario",
                productos.stream().mapToDouble(p -> p.getPrecio() * p.getStock()).sum());
        }

        if (contexto.getVentas() != null) {
            List<Venta> ventas = contexto.getVentas();
            metricas.put("total_ventas", ventas.size());
            metricas.put("ingresos_totales",
                ventas.stream().mapToDouble(Venta::getTotal).sum());
        }

        if (contexto.getClientes() != null) {
            metricas.put("total_clientes", contexto.getClientes().size());
        }

        return metricas;
    }

    // Esta función identifica problemas críticos en el negocio, como falta de stock o pocas ventas.
    public static List<String> identificarProblemas(ContextoNegocio contexto) {
        List<String> problemas = new ArrayList<>();

        if (contexto.getProductos() != null) {
            long sinStock = contexto.getProductos().stream()
                .filter(p -> p.getStock() == 0)
                .count();
            if (sinStock > 0) {
                problemas.add(String.format("%d productos sin stock", sinStock));
            }

            long stockCritico = contexto.getProductos().stream()
                .filter(Producto::tieneStockBajo)
                .count();
            if (stockCritico > 0) {
                problemas.add(String.format("%d productos con stock bajo", stockCritico));
            }
        }

        if (contexto.getVentas() != null && contexto.getVentas().size() < 5) {
            problemas.add("Volumen de ventas bajo");
        }

        return problemas;
    }

    // Esta función identifica oportunidades de mejora o crecimiento en el negocio.
    public static List<String> identificarOportunidades(ContextoNegocio contexto) {
        List<String> oportunidades = new ArrayList<>();

        if (contexto.getProductos() != null && contexto.getVentas() != null) {
            oportunidades.add("Análisis de productos más rentables disponible");

            if (contexto.getClientes() != null && contexto.getClientes().size() > 10) {
                oportunidades.add("Base de clientes suficiente para programas de fidelización");
            }
        }

        return oportunidades;
    }
}
