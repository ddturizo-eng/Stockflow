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
 * Singleton para gestionar la configuración de la API de DeepSeek
 * Lee y escribe configuración en archivo properties
 */
public class ConfiguracionAI {
    
    private static final String CONFIG_FILE = "ai-config.properties";
    private static ConfiguracionAI instance;
    private Properties properties;
    
    private ConfiguracionAI() {
        properties = new Properties();
        cargarConfiguracion();
    }
    
    public static ConfiguracionAI getInstance() {
        if (instance == null) {
            instance = new ConfiguracionAI();
        }
        return instance;
    }
    
    private void cargarConfiguracion() {
        try {
            InputStream input = new FileInputStream(CONFIG_FILE);
            properties.load(input);
            input.close();
        } catch (IOException e) {
            establecerValoresPorDefecto();
        }
    }
    
    private void establecerValoresPorDefecto() {
        properties.setProperty("deepseek.api.key", "");
        properties.setProperty("deepseek.api.url", "https://api.deepseek.com/v1/chat/completions");
        properties.setProperty("deepseek.model", "deepseek-chat");
        properties.setProperty("deepseek.temperature", "0.7");
        properties.setProperty("deepseek.max_tokens", "2000");
        properties.setProperty("deepseek.timeout", "30000");
    }
    
    public void guardarConfiguracion() throws IOException {
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Configuracion IA - StockFlow");
        }
    }
    
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
    
    public String getApiUrl() {
        return properties.getProperty("deepseek.api.url", "https://api.deepseek.com/v1/chat/completions");
    }
    
    public String getModel() {
        return properties.getProperty("deepseek.model", "deepseek-chat");
    }
    
    public double getTemperature() {
        return Double.parseDouble(properties.getProperty("deepseek.temperature", "0.7"));
    }
    
    public int getMaxTokens() {
        return Integer.parseInt(properties.getProperty("deepseek.max_tokens", "2000"));
    }
    
    public int getTimeout() {
        return Integer.parseInt(properties.getProperty("deepseek.timeout", "30000"));
    }
    
    public void setApiKey(String apiKey) {
        properties.setProperty("deepseek.api.key", apiKey);
    }
    
    public void setApiUrl(String apiUrl) {
        properties.setProperty("deepseek.api.url", apiUrl);
    }
    
    public void setModel(String model) {
        properties.setProperty("deepseek.model", model);
    }
    
    public void setTemperature(double temperature) {
        properties.setProperty("deepseek.temperature", String.valueOf(temperature));
    }
    
    public void setMaxTokens(int maxTokens) {
        properties.setProperty("deepseek.max_tokens", String.valueOf(maxTokens));
    }
    
    public boolean isConfigured() {
        String apiKey = properties.getProperty("deepseek.api.key");
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}