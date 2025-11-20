/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.excepciones.ConfiguracionAIFaltanteException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Gestion de configuracion para la API de DeepSeek mediante patron Singleton.
 * 
 * <p>Esta clase maneja la persistencia y recuperacion de parametros de configuracion
 * necesarios para la integracion con la API de inteligencia artificial DeepSeek,
 * incluyendo:</p>
 * <ul>
 *   <li>API Key de autenticacion</li>
 *   <li>URL del endpoint de la API</li>
 *   <li>Modelo de IA a utilizar</li>
 *   <li>Parametros de generacion (temperatura, max_tokens)</li>
 *   <li>Timeout de conexion</li>
 * </ul>
 * 
 * <p>La configuracion se almacena en un archivo properties local
 * (ai-config.properties) y se carga automaticamente al obtener la instancia.</p>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 * ConfiguracionAI config = ConfiguracionAI.getInstance();
 * config.setApiKey("sk-xxxxx");
 * config.guardarConfiguracion();
 * String apiKey = config.getApiKey();
 * </pre>
 * 
 * @author StockFlow Team
 * @version 2.0
 * @since 1.0
 */
public class ConfiguracionAI {
    
    /** Nombre del archivo de configuracion */
    private static final String CONFIG_FILE = "ai-config.properties";
    
    /** Instancia unica del Singleton */
    private static ConfiguracionAI instance;
    
    /** Objeto Properties que almacena la configuracion */
    private Properties properties;
    
    /**
     * Constructor privado para implementar el patron Singleton.
     * Carga automaticamente la configuracion desde el archivo.
     */
    private ConfiguracionAI() {
        properties = new Properties();
        cargarConfiguracion();
    }
    
    /**
     * Obtiene la instancia unica de ConfiguracionAI (Singleton).
     * 
     * @return la instancia unica de ConfiguracionAI
     */
    public static ConfiguracionAI getInstance() {
        if (instance == null) {
            instance = new ConfiguracionAI();
        }
        return instance;
    }
    
    /**
     * Carga la configuracion desde el archivo properties.
     * Si el archivo no existe o hay error al leerlo, establece valores por defecto.
     */
    private void cargarConfiguracion() {
        try {
            InputStream input = new FileInputStream(CONFIG_FILE);
            properties.load(input);
            input.close();
        } catch (IOException e) {
            establecerValoresPorDefecto();
        }
    }
    
    /**
     * Establece los valores por defecto de la configuracion.
     * Se utiliza cuando no existe el archivo de configuracion o no puede leerse.
     */
    private void establecerValoresPorDefecto() {
        properties.setProperty("deepseek.api.key", "");
        properties.setProperty("deepseek.api.url", "https://api.deepseek.com/v1/chat/completions");
        properties.setProperty("deepseek.model", "deepseek-chat");
        properties.setProperty("deepseek.temperature", "0.7");
        properties.setProperty("deepseek.max_tokens", "2000");
        properties.setProperty("deepseek.timeout", "30000");
    }
    
    /**
     * Guarda la configuracion actual en el archivo properties.
     * 
     * @throws IOException si hay un error al escribir el archivo
     */
    public void guardarConfiguracion() throws IOException {
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Configuracion IA - StockFlow");
        }
    }
    
    /**
     * Obtiene el API Key configurado.
     * 
     * @return el API Key de DeepSeek
     * @throws ConfiguracionAIFaltanteException si el API Key no esta configurado o esta vacio
     */
    public String getApiKey() throws ConfiguracionAIFaltanteException {
        String apiKey = properties.getProperty("deepseek.api.key");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ConfiguracionAIFaltanteException(
                "API Key de DeepSeek no configurada", 
                "deepseek.api.key"
            );
        }
        return apiKey;
    }
    
    /**
     * Obtiene la URL del endpoint de la API.
     * 
     * @return la URL de la API, por defecto "https://api.deepseek.com/v1/chat/completions"
     */
    public String getApiUrl() {
        return properties.getProperty("deepseek.api.url", "https://api.deepseek.com/v1/chat/completions");
    }
    
    /**
     * Obtiene el modelo de IA configurado.
     * 
     * @return el nombre del modelo, por defecto "deepseek-chat"
     */
    public String getModel() {
        return properties.getProperty("deepseek.model", "deepseek-chat");
    }
    
    /**
     * Obtiene el parametro de temperatura para la generacion.
     * 
     * <p>La temperatura controla la aleatoriedad de las respuestas:
     * valores bajos (0.1-0.5) generan respuestas mas predecibles,
     * valores altos (0.8-1.0) generan respuestas mas creativas.</p>
     * 
     * @return el valor de temperatura, por defecto 0.7
     */
    public double getTemperature() {
        return Double.parseDouble(properties.getProperty("deepseek.temperature", "0.7"));
    }
    
    /**
     * Obtiene el numero maximo de tokens para la respuesta.
     * 
     * @return el maximo de tokens, por defecto 2000
     */
    public int getMaxTokens() {
        return Integer.parseInt(properties.getProperty("deepseek.max_tokens", "2000"));
    }
    
    /**
     * Obtiene el timeout de conexion en milisegundos.
     * 
     * @return el timeout en ms, por defecto 30000 (30 segundos)
     */
    public int getTimeout() {
        return Integer.parseInt(properties.getProperty("deepseek.timeout", "30000"));
    }
    
    /**
     * Establece el API Key.
     * 
     * @param apiKey el nuevo API Key de DeepSeek
     */
    public void setApiKey(String apiKey) {
        properties.setProperty("deepseek.api.key", apiKey);
    }
    
    /**
     * Establece la URL del endpoint de la API.
     * 
     * @param apiUrl la nueva URL del endpoint
     */
    public void setApiUrl(String apiUrl) {
        properties.setProperty("deepseek.api.url", apiUrl);
    }
    
    /**
     * Establece el modelo de IA a utilizar.
     * 
     * @param model el nombre del modelo
     */
    public void setModel(String model) {
        properties.setProperty("deepseek.model", model);
    }
    
    /**
     * Establece el parametro de temperatura.
     * 
     * @param temperature el nuevo valor de temperatura (0.0 - 1.0)
     */
    public void setTemperature(double temperature) {
        properties.setProperty("deepseek.temperature", String.valueOf(temperature));
    }
    
    /**
     * Establece el numero maximo de tokens.
     * 
     * @param maxTokens el nuevo maximo de tokens
     */
    public void setMaxTokens(int maxTokens) {
        properties.setProperty("deepseek.max_tokens", String.valueOf(maxTokens));
    }
    
    /**
     * Verifica si la configuracion esta completa y valida.
     * 
     * @return true si el API Key esta configurado, false en caso contrario
     */
    public boolean isConfigured() {
        String apiKey = properties.getProperty("deepseek.api.key");
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}