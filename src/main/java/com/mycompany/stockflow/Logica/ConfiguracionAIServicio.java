/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.PromptConfiguracion;
import com.mycompany.stockflow.excepciones.ConfiguracionAIFaltanteException;
import com.mycompany.stockflow.utils.ConfiguracionAI;
import java.io.IOException;
import java.util.*;


public class ConfiguracionAIServicio {

    private final ConfiguracionAI config;
    private final Map<String, PromptConfiguracion> promptsPersonalizados;

    // En el constructor, obtengo la instancia única de configuración y cargo los prompts por defecto.
    public ConfiguracionAIServicio() {
        this.config = ConfiguracionAI.getInstance();
        this.promptsPersonalizados = new HashMap<>();
        inicializarPromptsDefault();
    }

    // Aquí defino los prompts por defecto que estarán disponibles al iniciar el sistema.
    private void inicializarPromptsDefault() {
        PromptConfiguracion promptInventario = new PromptConfiguracion(
            "analisis_inventario",
            "INVENTARIO",
            "Analiza el inventario y proporciona recomendaciones de reabastecimiento"
        );
        promptInventario.setTemperatura(0.5);
        promptInventario.setMaxTokens(1500);
        promptsPersonalizados.put("analisis_inventario", promptInventario);

        PromptConfiguracion promptVentas = new PromptConfiguracion(
            "analisis_ventas",
            "VENTAS",
            "Analiza las tendencias de ventas e identifica oportunidades"
        );
        promptVentas.setTemperatura(0.6);
        promptVentas.setMaxTokens(2000);
        promptsPersonalizados.put("analisis_ventas", promptVentas);

        PromptConfiguracion promptRecomendaciones = new PromptConfiguracion(
            "recomendaciones",
            "RECOMENDACIONES",
            "Genera recomendaciones accionables para mejorar el negocio"
        );
        promptRecomendaciones.setTemperatura(0.7);
        promptRecomendaciones.setMaxTokens(1000);
        promptsPersonalizados.put("recomendaciones", promptRecomendaciones);
    }

    
    public void configurarApiKey(String apiKey) throws IOException {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("La API Key no puede estar vacía");
        }

        config.setApiKey(apiKey.trim());
        config.guardarConfiguracion();
    }

    
    public void configurarApiUrl(String apiUrl) throws IOException {
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL de API no puede estar vacía");
        }

        config.setApiUrl(apiUrl.trim());
        config.guardarConfiguracion();
    }

   
    public void configurarModelo(String modelo) throws IOException {
        config.setModel(modelo);
        config.guardarConfiguracion();
    }

    // Aquí configuro la temperatura del modelo, que afecta la creatividad de las respuestas.
    public void configurarTemperatura(double temperatura) throws IOException {
        if (temperatura < 0.0 || temperatura > 2.0) {
            throw new IllegalArgumentException("La temperatura debe estar entre 0.0 y 2.0");
        }

        config.setTemperature(temperatura);
        config.guardarConfiguracion();
    }

    
    public void configurarMaxTokens(int maxTokens) throws IOException {
        if (maxTokens < 100 || maxTokens > 4000) {
            throw new IllegalArgumentException("Max tokens debe estar entre 100 y 4000");
        }

        config.setMaxTokens(maxTokens);
        config.guardarConfiguracion();
    }

    
    public Map<String, String> obtenerConfiguracionActual() {
        Map<String, String> configuracion = new HashMap<>();

        try {
            configuracion.put("apiKey", config.getApiKey().substring(0, 10) + "...");
        } catch (ConfiguracionAIFaltanteException e) {
            configuracion.put("apiKey", "NO CONFIGURADA");
        }

        configuracion.put("apiUrl", config.getApiUrl());
        configuracion.put("model", config.getModel());
        configuracion.put("temperature", String.valueOf(config.getTemperature()));
        configuracion.put("maxTokens", String.valueOf(config.getMaxTokens()));
        configuracion.put("timeout", String.valueOf(config.getTimeout()));
        configuracion.put("configured", String.valueOf(config.isConfigured()));

        return configuracion;
    }

    public boolean verificarConfiguracion() {
        return config.isConfigured();
    }

    // Aquí valido cada parte de la configuración y devuelvo mensajes que indican si está bien o falta algo.
    public List<String> validarConfiguracion() {
        List<String> mensajes = new ArrayList<>();

        try {
            String apiKey = config.getApiKey();
            if (apiKey.length() < 20) {
                mensajes.add("La API Key parece muy corta");
            } else {
                mensajes.add("API Key configurada");
            }
        } catch (ConfiguracionAIFaltanteException e) {
            mensajes.add("API Key no configurada");
        }

        if (config.getApiUrl() != null && !config.getApiUrl().isEmpty()) {
            mensajes.add("URL de API configurada");
        } else {
            mensajes.add("URL de API no configurada");
        }

        if (config.getTemperature() >= 0.0 && config.getTemperature() <= 2.0) {
            mensajes.add("Temperatura válida: " + config.getTemperature());
        } else {
            mensajes.add("Temperatura fuera de rango");
        }

        return mensajes;
    }

    // Esta función me permite guardar un nuevo prompt personalizado en el sistema.
    public void guardarPromptPersonalizado(PromptConfiguracion prompt) {
        promptsPersonalizados.put(prompt.getNombre(), prompt);
    }

    // Aquí obtengo un prompt personalizado por su nombre.
    public PromptConfiguracion obtenerPromptPersonalizado(String nombre) {
        return promptsPersonalizados.get(nombre);
    }

    // Esta función me devuelve todos los prompts que están disponibles actualmente.
    public List<PromptConfiguracion> listarPromptsDisponibles() {
        return new ArrayList<>(promptsPersonalizados.values());
    }
    
    public boolean eliminarPromptPersonalizado(String nombre) {
        if (promptsPersonalizados.containsKey(nombre)) {
            promptsPersonalizados.remove(nombre);
            return true;
        }
        return false;
    }

    // Esta función restaura la configuración a valores predeterminados recomendados.
    public void restaurarConfiguracionDefault() throws IOException {
        config.setApiUrl("https://api.deepseek.com/v1/chat/completions");
        config.setModel("deepseek-chat");
        config.setTemperature(0.7);
        config.setMaxTokens(2000);
        config.guardarConfiguracion();
    }

    public Map<String, Object> exportarConfiguracion() {
        Map<String, Object> exportacion = new HashMap<>();

        exportacion.put("apiUrl", config.getApiUrl());
        exportacion.put("model", config.getModel());
        exportacion.put("temperature", config.getTemperature());
        exportacion.put("maxTokens", config.getMaxTokens());
        exportacion.put("timeout", config.getTimeout());
        exportacion.put("prompts", new ArrayList<>(promptsPersonalizados.values()));

        return exportacion;
    }

    public Map<String, Object> obtenerEstadisticasUso() {
        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("totalConsultas", 0);
        estadisticas.put("tokensUsados", 0);
        estadisticas.put("ultimaConsulta", "N/A");
        estadisticas.put("estado", config.isConfigured() ? "Activo" : "Inactivo");

        return estadisticas;
    }
}