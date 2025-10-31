/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.*;
import com.mycompany.stockflow.excepciones.*;
import com.mycompany.stockflow.utils.*;
import java.util.*;

public class RecomendacionServicio {

    private final DeepSeekAPIClient apiClient;
    private final List<Recomendacion> recomendacionesGeneradas;

    public RecomendacionServicio() {
        this.apiClient = new DeepSeekAPIClient();
        this.recomendacionesGeneradas = new ArrayList<>();
    }

    
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

        // Agrego la recomendación a la lista y la retorno.
        recomendacionesGeneradas.add(recomendacion);
        return recomendacion;
    }

   
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

            // Genero el prompt usando una plantilla.
            String prompt = PromptTemplates.generarPromptRecomendacionProducto(producto, ventasRecientes);

            // Envío el prompt a la IA y obtengo la respuesta.
            String respuestaIA = apiClient.enviarPrompt(prompt, 0.6, 500);

            // Guardo la recomendación generada por la IA.
            recomendacion.setJustificacion(respuestaIA);
            recomendacion.setAccionRecomendada("Revisar estrategia de precios");

        } catch (Exception e) {
            // Si ocurre un error, doy una recomendación básica basada en el precio actual.
            recomendacion.setJustificacion(
                "Precio actual: $" + producto.getPrecio() + ". Revisar competencia."
            );
        }

        recomendacionesGeneradas.add(recomendacion);
        return recomendacion;
    }

    // Aquí genero una recomendación para aplicar una promoción al producto.
    public Recomendacion generarRecomendacionPromocion(Producto producto) {
        Recomendacion recomendacion = new Recomendacion(
            "PROMOCION",
            producto.getCodigo(),
            "Oportunidad de promoción: " + producto.getNombre(),
            "El producto podría beneficiarse de una estrategia promocional",
            "BAJA"
        );

        // Si el stock es muy alto, recomiendo una promoción urgente.
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
            // Si no hay exceso, sugiero una promoción cruzada.
            recomendacion.setAccionRecomendada("Considerar promoción cruzada");
            recomendacion.setJustificacion(
                "Producto con potencial para ventas cruzadas"
            );
        }

        recomendacionesGeneradas.add(recomendacion);
        return recomendacion;
    }

    // Esta función genera una recomendación para descontinuar un producto con bajo movimiento.
    public Recomendacion generarRecomendacionDescontinuar(Producto producto, List<Venta> ventas) {
        Recomendacion recomendacion = new Recomendacion(
            "DESCONTINUAR",
            producto.getCodigo(),
            "Considerar descontinuar: " + producto.getNombre(),
            "Producto con bajo o nulo movimiento",
            "MEDIA"
        );

        int ventasRecientes = contarVentasProducto(producto, ventas);

        // Si no se ha vendido nada y hay stock, recomiendo liquidar y descontinuar.
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
            // Si hay pocas ventas, sugiero monitorear el desempeño.
            recomendacion.setAccionRecomendada("Monitorear desempeño");
            recomendacion.setJustificacion(
                "Ventas bajas (" + ventasRecientes + "). Evaluar en próximo periodo."
            );
        }

        recomendacionesGeneradas.add(recomendacion);
        return recomendacion;
    }

    // Esta función devuelve todas las recomendaciones generadas hasta el momento.
    public List<Recomendacion> obtenerTodasRecomendaciones() {
        return new ArrayList<>(recomendacionesGeneradas);
    }

    // Aquí filtro las recomendaciones por prioridad (ALTA, MEDIA, BAJA).
    public List<Recomendacion> obtenerRecomendacionesPorPrioridad(String prioridad) {
        List<Recomendacion> filtradas = new ArrayList<>();

        for (Recomendacion r : recomendacionesGeneradas) {
            if (r.getPrioridad().equals(prioridad)) {
                filtradas.add(r);
            }
        }

        return filtradas;
    }

    // Esta función marca una recomendación como aplicada, usando su ID.
    public void marcarRecomendacionAplicada(String recomendacionId) {
        for (Recomendacion r : recomendacionesGeneradas) {
            if (r.getId().equals(recomendacionId)) {
                r.setAplicada(true);
                break;
            }
        }
    }

    // Aquí elimino todas las recomendaciones que ya fueron aplicadas.
    public void limpiarRecomendacionesAntiguas() {
        recomendacionesGeneradas.removeIf(Recomendacion::isAplicada);
    }

    // Esta función cuenta cuántas unidades se han vendido de un producto específico.
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
