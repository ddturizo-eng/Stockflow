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

public class EstadisticasServicio {
    
    private final VentaServicio ventaServicio;
    private final ProductoServicio productoServicio;
    
    public EstadisticasServicio() {
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
    }
    
    public double calcularVentasTotales() {
        return ventaServicio.listarVentas().stream()
                .mapToDouble(Venta::getTotal)
                .sum();
    }
    
    public double calcularVentasMes(int mes, int año) {
        return ventaServicio.listarVentas().stream()
                .filter(v -> v.getFecha().getMonthValue() == mes && v.getFecha().getYear() == año)
                .mapToDouble(Venta::getTotal)
                .sum();
    }
    
    public List<Producto> obtenerProductosMasVendidos(int limite) {
        Map<String, Integer> conteoVentas = new HashMap<>();
        
        for (Venta venta : ventaServicio.listarVentas()) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                String codigo = detalle.getProducto().getCodigo();
                conteoVentas.put(codigo, conteoVentas.getOrDefault(codigo, 0) + detalle.getCantidad());
            }
        }
        
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
    
    public int contarVentasDelDia() {
        LocalDateTime hoy = LocalDateTime.now();
        return (int) ventaServicio.listarVentas().stream()
                .filter(v -> v.getFecha().toLocalDate().equals(hoy.toLocalDate()))
                .count();
    }
    
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
