/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.*;
import java.util.*;
import java.util.stream.Collectors;

public class AnaliticaAvanzadaServicio {

    // Aquí calculo el ROI (retorno de inversión) para cada producto, comparando lo que costó tenerlo en inventario con lo que se ha vendido.
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

    // En esta función selecciono los productos que más ingresos han generado, ordenándolos y devolviendo los 'top' que se indiquen.
    public List<Producto> obtenerProductosMasRentables(List<Producto> productos, List<Venta> ventas, int top) {
        Map<String, Double> ventasPorProducto = new HashMap<>();

        for (Producto p : productos) {
            double ventasProducto = calcularVentasProducto(p, ventas);
            ventasPorProducto.put(p.getCodigo(), ventasProducto);
        }

        return productos.stream()
            .sorted((p1, p2) -> Double.compare(
                ventasPorProducto.getOrDefault(p2.getCodigo(), 0.0),
                ventasPorProducto.getOrDefault(p1.getCodigo(), 0.0)
            ))
            .limit(top)
            .collect(Collectors.toList());
    }

    // Aquí identifico los productos que tienen stock pero no se han vendido, lo que indica bajo movimiento.
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

    // Esta función calcula cuántas veces se ha rotado el inventario de un producto en un periodo determinado.
    public double calcularTasaRotacion(Producto producto, List<Venta> ventas, int diasPeriodo) {
        int unidadesVendidas = contarUnidadesVendidas(producto, ventas);
        double stockPromedio = producto.getStock();

        if (stockPromedio > 0) {
            return (double) unidadesVendidas / stockPromedio;
        }
        return 0.0;
    }

    // Aquí analizo en qué meses se han vendido más productos, para detectar patrones estacionales.
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

    // Esta función estima cuántas unidades se podrían vender en el futuro, basándose en el historial de ventas.
    public int predecirDemanda(Producto producto, List<Venta> ventas, int diasProyeccion) {
        int unidadesVendidas = contarUnidadesVendidas(producto, ventas);
        int diasHistorico = 30;

        if (diasHistorico > 0) {
            double ventasDiarias = (double) unidadesVendidas / diasHistorico;
            return (int) Math.ceil(ventasDiarias * diasProyeccion);
        }

        return 0;
    }

    // Aquí calculo el punto óptimo para reordenar un producto, considerando el tiempo de entrega y el stock mínimo.
    public int calcularPuntoReorden(Producto producto, List<Venta> ventas, int diasEntrega) {
        int demandaDiaria = predecirDemanda(producto, ventas, 1);
        int stockSeguridad = producto.getStockMinimo();

        return (demandaDiaria * diasEntrega) + stockSeguridad;
    }

    // En esta función analizo qué clientes concentran más ventas, útil para identificar clientes clave.
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

    // Aquí calculo el valor del ciclo de vida del cliente (CLV), estimando cuánto podría gastar en el futuro.
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
            return ticketPromedio * (numeroCompras * 2);
        }

        return 0.0;
    }

    // Esta función detecta productos que suelen comprarse juntos, útil para estrategias de venta cruzada.
    public Map<String, List<String>> identificarProductosComplementarios(List<Venta> ventas) {
        Map<String, List<String>> complementarios = new HashMap<>();

        for (Venta v : ventas) {
            if (v.getDetalles() != null && v.getDetalles().size() > 1) {
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

    // Esta función me permite calcular cuánto se ha vendido de un producto específico.
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

    // Aquí cuento cuántas unidades se han vendido de un producto específico.
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