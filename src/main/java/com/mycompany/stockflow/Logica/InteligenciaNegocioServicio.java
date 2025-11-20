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
 * Servicio de Inteligencia de Negocio que integra análisis de datos con IA.
 * 
 * <p>Esta clase proporciona funcionalidades avanzadas de análisis empresarial
 * combinando métricas locales con capacidades de inteligencia artificial para
 * generar insights, predicciones y recomendaciones estratégicas.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Análisis completo del estado del negocio</li>
 *   <li>Evaluación de inventario y detección de problemas</li>
 *   <li>Análisis de ventas y tendencias</li>
 *   <li>Generación automática de recomendaciones</li>
 *   <li>Consultas personalizadas mediante IA</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 */
public class InteligenciaNegocioServicio {

    /** Cliente API para comunicación con el servicio de IA DeepSeek */
    private final DeepSeekAPIClient apiClient;
    
    /** Servicio de analítica avanzada para cálculos y métricas complejas */
    private final AnaliticaAvanzadaServicio analiticaServicio;
    
    /** Servicio de recomendaciones para generar sugerencias automatizadas */
    private final RecomendacionServicio recomendacionServicio;

    /**
     * Constructor que inicializa todos los servicios necesarios.
     * 
     * <p>Crea instancias de los servicios de IA, analítica y recomendaciones
     * que serán utilizados para realizar análisis integrales del negocio.</p>
     */
    public InteligenciaNegocioServicio() {
        this.apiClient = new DeepSeekAPIClient();
        this.analiticaServicio = new AnaliticaAvanzadaServicio();
        this.recomendacionServicio = new RecomendacionServicio();
    }

    /**
     * Genera un análisis completo del negocio combinando métricas locales y análisis de IA.
     * 
     * <p>Este método realiza un análisis exhaustivo que incluye:</p>
     * <ul>
     *   <li>Evaluación de la salud general del negocio</li>
     *   <li>Identificación de fortalezas y debilidades</li>
     *   <li>Detección de tendencias y patrones</li>
     *   <li>Generación de recomendaciones estratégicas</li>
     *   <li>Predicciones y alertas tempranas</li>
     * </ul>
     * 
     * @param contexto el contexto completo del negocio incluyendo productos, ventas y clientes
     * @return un objeto AnalisisEstadistico con métricas, conclusiones y recomendaciones
     * @throws AIAPIException si hay problemas de comunicación con la API de IA
     * @throws ConfiguracionAIFaltanteException si la configuración de IA no está completa
     * @throws AnalisisFallidoException si ocurre un error durante el proceso de análisis
     */
    public AnalisisEstadistico generarAnalisisCompleto(ContextoNegocio contexto)
            throws AIAPIException, ConfiguracionAIFaltanteException, AnalisisFallidoException {

        try {
            AnalisisEstadistico analisis = new AnalisisEstadistico("COMPLETO");

            String resumenNegocio = AnalizadorContexto.generarResumenNegocio(contexto);
            Map<String, Object> metricas = AnalizadorContexto.extraerMetricas(contexto);
            analisis.setMetricas(metricas);

            String prompt = generarPromptAnalisisCompleto(resumenNegocio, contexto);
            String respuestaIA = apiClient.enviarPrompt(prompt);

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

    /**
     * Analiza el inventario actual y genera recomendaciones.
     * 
     * <p>Evalúa el estado del inventario considerando:</p>
     * <ul>
     *   <li>Cantidad total de productos</li>
     *   <li>Productos con stock bajo</li>
     *   <li>Distribución y rotación de inventario</li>
     *   <li>Oportunidades de optimización</li>
     * </ul>
     * 
     * @param productos lista de productos a analizar
     * @return análisis estadístico del inventario con métricas y recomendaciones
     * @throws AIAPIException si hay problemas de comunicación con la API de IA
     * @throws ConfiguracionAIFaltanteException si la configuración de IA no está completa
     * @throws AnalisisFallidoException si ocurre un error durante el análisis
     */
    public AnalisisEstadistico analizarInventario(List<Producto> productos)
            throws AIAPIException, ConfiguracionAIFaltanteException, AnalisisFallidoException {

        try {
            AnalisisEstadistico analisis = new AnalisisEstadistico("INVENTARIO");

            String prompt = PromptTemplates.generarPromptAnalisisInventario(productos);
            String respuestaIA = apiClient.enviarPrompt(prompt);
            analisis.setResumenIA(respuestaIA);

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

    /**
     * Analiza las ventas realizadas y genera insights sobre rendimiento.
     * 
     * <p>Proporciona análisis detallado de:</p>
     * <ul>
     *   <li>Volumen total de ventas</li>
     *   <li>Ingresos generados</li>
     *   <li>Productos más vendidos</li>
     *   <li>Tendencias de ventas</li>
     *   <li>Oportunidades de mejora</li>
     * </ul>
     * 
     * @param ventas lista de ventas a analizar
     * @param productos lista de productos relacionados
     * @return análisis estadístico de ventas con métricas y tendencias
     * @throws AIAPIException si hay problemas de comunicación con la API de IA
     * @throws ConfiguracionAIFaltanteException si la configuración de IA no está completa
     * @throws AnalisisFallidoException si ocurre un error durante el análisis
     */
    public AnalisisEstadistico analizarVentas(List<Venta> ventas, List<Producto> productos)
            throws AIAPIException, ConfiguracionAIFaltanteException, AnalisisFallidoException {

        try {
            AnalisisEstadistico analisis = new AnalisisEstadistico("VENTAS");

            String prompt = PromptTemplates.generarPromptAnalisisVentas(ventas, productos);
            String respuestaIA = apiClient.enviarPrompt(prompt);
            analisis.setResumenIA(respuestaIA);

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

    /**
     * Genera recomendaciones automáticas de reabastecimiento para productos con stock bajo.
     * 
     * <p>Identifica productos que necesitan reabastecimiento y genera
     * recomendaciones específicas para cada uno, incluyendo cantidades
     * sugeridas y prioridad de acción.</p>
     * 
     * @param contexto contexto del negocio con información de productos
     * @return lista de recomendaciones de restock ordenadas por prioridad
     * @throws AIAPIException si hay problemas de comunicación con la API de IA
     * @throws ConfiguracionAIFaltanteException si la configuración de IA no está completa
     */
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

    /**
     * Permite realizar consultas personalizadas a la IA usando el contexto del negocio.
     * 
     * <p>Esta función permite al usuario hacer preguntas específicas sobre su negocio
     * y recibir respuestas contextualizadas basadas en los datos actuales de la empresa.</p>
     * 
     * @param pregunta la pregunta o consulta del usuario
     * @param contexto el contexto actual del negocio
     * @return respuesta de la IA a la consulta realizada
     * @throws AIAPIException si hay problemas de comunicación con la API de IA
     * @throws ConfiguracionAIFaltanteException si la configuración de IA no está completa
     */
    public String consultarIA(String pregunta, ContextoNegocio contexto)
            throws AIAPIException, ConfiguracionAIFaltanteException {

        String resumenContexto = AnalizadorContexto.generarResumenNegocio(contexto);
        String prompt = PromptTemplates.generarPromptPersonalizado(resumenContexto, pregunta);

        return apiClient.enviarPrompt(prompt);
    }

    /**
     * Verifica si la configuración de IA está completa y correcta.
     * 
     * @return {@code true} si la configuración es válida, {@code false} en caso contrario
     */
    public boolean verificarConfiguracion() {
        try {
            ConfiguracionAI config = ConfiguracionAI.getInstance();
            return config.isConfigured();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Prueba la conexión con la API de IA.
     * 
     * @return {@code true} si la conexión es exitosa, {@code false} en caso contrario
     */
    public boolean probarConexionAPI() {
        return apiClient.probarConexion();
    }

    /**
     * Genera el prompt completo para el análisis integral del negocio.
     * 
     * <p>Construye un prompt estructurado que incluye el resumen del negocio,
     * métricas clave y problemas detectados para obtener un análisis
     * completo y accionable de la IA.</p>
     * 
     * @param resumenNegocio resumen textual del estado actual del negocio
     * @param contexto contexto completo con todos los datos del negocio
     * @return prompt formateado listo para enviar a la IA
     */
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

    /**
     * Extrae las conclusiones principales del análisis generado por la IA.
     * 
     * <p>Toma las primeras líneas significativas del análisis para proporcionar
     * un resumen ejecutivo de las conclusiones más importantes.</p>
     * 
     * @param respuestaIA respuesta completa generada por la IA
     * @return texto con las conclusiones principales (máximo 5 líneas)
     */
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

    /**
     * Extrae la sección de tendencias del análisis generado por la IA.
     * 
     * <p>Busca y extrae la porción del texto que contiene información
     * sobre tendencias identificadas en el negocio.</p>
     * 
     * @param respuestaIA respuesta completa generada por la IA
     * @return texto con las tendencias identificadas o mensaje por defecto
     */
    private String extraerTendencias(String respuestaIA) {
        if (respuestaIA.toLowerCase().contains("tendencia")) {
            int inicio = respuestaIA.toLowerCase().indexOf("tendencia");
            int fin = Math.min(inicio + 500, respuestaIA.length());
            return respuestaIA.substring(inicio, fin);
        }
        return "Tendencias incluidas en el análisis general";
    }
}