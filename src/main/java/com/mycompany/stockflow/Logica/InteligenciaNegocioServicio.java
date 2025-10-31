/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.*;
import com.mycompany.stockflow.excepciones.*;
import com.mycompany.stockflow.utils.*;
import java.util.*;

public class InteligenciaNegocioServicio {

    // Inicializo los servicios que voy a usar: el cliente de IA, el servicio de analítica y el de recomendaciones.
    private final DeepSeekAPIClient apiClient;
    private final AnaliticaAvanzadaServicio analiticaServicio;
    private final RecomendacionServicio recomendacionServicio;

    public InteligenciaNegocioServicio() {
        this.apiClient = new DeepSeekAPIClient();
        this.analiticaServicio = new AnaliticaAvanzadaServicio();
        this.recomendacionServicio = new RecomendacionServicio();
    }

    // Esta función genera un análisis completo del negocio combinando métricas locales y análisis de IA.
    public AnalisisEstadistico generarAnalisisCompleto(ContextoNegocio contexto)
            throws AIAPIException, ConfiguracionAIFaltanteException, AnalisisFallidoException {

        try {
            AnalisisEstadistico analisis = new AnalisisEstadistico("COMPLETO");

            // Obtengo un resumen del negocio y extraigo métricas clave.
            String resumenNegocio = AnalizadorContexto.generarResumenNegocio(contexto);
            Map<String, Object> metricas = AnalizadorContexto.extraerMetricas(contexto);
            analisis.setMetricas(metricas);

            // Construyo el prompt para enviar a la IA.
            String prompt = generarPromptAnalisisCompleto(resumenNegocio, contexto);
            String respuestaIA = apiClient.enviarPrompt(prompt);

            // Guardo la respuesta de la IA y extraigo conclusiones y tendencias.
            analisis.setResumenIA(respuestaIA);
            analisis.setConclusiones(extraerConclusiones(respuestaIA));
            analisis.setTendencias(extraerTendencias(respuestaIA));

            return analisis;

        } catch (AIAPIException | ConfiguracionAIFaltanteException e) {
            throw e;
        } catch (Exception e) {
            throw new AnalisisFallidoException(
                "Error al generar análisis completo: " + e.getMessage(),
                "COMPLETO",
                e
            );
        }
    }

    public AnalisisEstadistico analizarInventario(List<Producto> productos)
            throws AIAPIException, ConfiguracionAIFaltanteException, AnalisisFallidoException {

        try {
            AnalisisEstadistico analisis = new AnalisisEstadistico("INVENTARIO");

            // Genero el prompt y obtengo la respuesta de la IA.
            String prompt = PromptTemplates.generarPromptAnalisisInventario(productos);
            String respuestaIA = apiClient.enviarPrompt(prompt);
            analisis.setResumenIA(respuestaIA);

            // Agrego métricas locales al análisis.
            analisis.agregarMetrica("total_productos", productos.size());
            analisis.agregarMetrica("productos_stock_bajo",
                productos.stream().filter(Producto::tieneStockBajo).count());

            return analisis;

        } catch (AIAPIException | ConfiguracionAIFaltanteException e) {
            throw e;
        } catch (Exception e) {
            throw new AnalisisFallidoException(
                "Error al analizar inventario: " + e.getMessage(),
                "INVENTARIO",
                e
            );
        }
    }

    public AnalisisEstadistico analizarVentas(List<Venta> ventas, List<Producto> productos)
            throws AIAPIException, ConfiguracionAIFaltanteException, AnalisisFallidoException {

        try {
            AnalisisEstadistico analisis = new AnalisisEstadistico("VENTAS");

            String prompt = PromptTemplates.generarPromptAnalisisVentas(ventas, productos);
            String respuestaIA = apiClient.enviarPrompt(prompt);
            analisis.setResumenIA(respuestaIA);

            // Agrego métricas locales al análisis.
            analisis.agregarMetrica("total_ventas", ventas.size());
            analisis.agregarMetrica("ingresos_totales",
                ventas.stream().mapToDouble(Venta::getTotal).sum());

            return analisis;

        } catch (AIAPIException | ConfiguracionAIFaltanteException e) {
            throw e;
        } catch (Exception e) {
            throw new AnalisisFallidoException(
                "Error al analizar ventas: " + e.getMessage(),
                "VENTAS",
                e
            );
        }
    }

    // Esta función genera recomendaciones automáticas de reabastecimiento para productos con stock bajo.
    public List<Recomendacion> generarRecomendacionesAutomaticas(ContextoNegocio contexto)
            throws AIAPIException, ConfiguracionAIFaltanteException {

        List<Recomendacion> recomendaciones = new ArrayList<>();

        if (contexto.getProductos() != null) {
            for (Producto p : contexto.getProductos()) {
                if (p.tieneStockBajo()) {
                    try {
                        Recomendacion rec = recomendacionServicio.generarRecomendacionRestock(p);
                        recomendaciones.add(rec);
                    } catch (Exception e) {
                        System.err.println("Error generando recomendación para " + p.getNombre());
                    }
                }
            }
        }

        return recomendaciones;
    }

    // Esta función permite hacer una consulta personalizada a la IA, usando el contexto del negocio.
    public String consultarIA(String pregunta, ContextoNegocio contexto)
            throws AIAPIException, ConfiguracionAIFaltanteException {

        String resumenContexto = AnalizadorContexto.generarResumenNegocio(contexto);
        String prompt = PromptTemplates.generarPromptPersonalizado(resumenContexto, pregunta);

        return apiClient.enviarPrompt(prompt);
    }

    public boolean verificarConfiguracion() {
        try {
            ConfiguracionAI config = ConfiguracionAI.getInstance();
            return config.isConfigured();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean probarConexionAPI() {
        return apiClient.probarConexion();
    }

    private String generarPromptAnalisisCompleto(String resumenNegocio, ContextoNegocio contexto) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Eres un consultor experto en gestión de negocios y análisis de datos. ");
        prompt.append("Analiza la siguiente información de un negocio de gestión de inventario:\n\n");
        prompt.append(resumenNegocio);
        prompt.append("\n\nANÁLISIS REQUERIDO\n");
        prompt.append("Proporciona un análisis completo que incluya:\n");
        prompt.append("1. Salud general del negocio\n");
        prompt.append("2. Puntos fuertes identificados\n");
        prompt.append("3. Áreas de mejora críticas\n");
        prompt.append("4. Tendencias observadas\n");
        prompt.append("5. Recomendaciones estratégicas accionables\n");
        prompt.append("6. Predicciones y alertas\n");

        List<String> problemas = AnalizadorContexto.identificarProblemas(contexto);
        if (!problemas.isEmpty()) {
            prompt.append("\nPROBLEMAS DETECTADOS\n");
            for (String problema : problemas) {
                prompt.append("- ").append(problema).append("\n");
            }
        }

        return prompt.toString();
    }

    // Extraigo las primeras líneas como conclusiones del análisis generado por la IA.
    private String extraerConclusiones(String respuestaIA) {
        String[] lineas = respuestaIA.split("\n");
        StringBuilder conclusiones = new StringBuilder();

        int contador = 0;
        for (String linea : lineas) {
            if (contador >= 5) break;
            if (!linea.trim().isEmpty()) {
                conclusiones.append(linea).append("\n");
                contador++;
            }
        }

        return conclusiones.toString();
    }

    // Busco la sección de tendencias dentro del texto generado por la IA.
    private String extraerTendencias(String respuestaIA) {
        if (respuestaIA.toLowerCase().contains("tendencia")) {
            int inicio = respuestaIA.toLowerCase().indexOf("tendencia");
            int fin = Math.min(inicio + 500, respuestaIA.length());
            return respuestaIA.substring(inicio, fin);
        }
        return "Tendencias incluidas en el análisis general";
    }
}

