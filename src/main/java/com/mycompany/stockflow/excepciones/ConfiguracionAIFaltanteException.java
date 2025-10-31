/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.excepciones;

/**
 * Excepción lanzada cuando falta configuración necesaria para la IA
 */
public class ConfiguracionAIFaltanteException extends Exception {
    
    private String parametroFaltante;
    
    public ConfiguracionAIFaltanteException(String message) {
        super(message);
    }
    
    public ConfiguracionAIFaltanteException(String message, String parametroFaltante) {
        super(message);
        this.parametroFaltante = parametroFaltante;
    }
    
    public String getParametroFaltante() {
        return parametroFaltante;
    }
    
    @Override
    public String toString() {
        if (parametroFaltante != null) {
            return "ConfiguracionAIFaltanteException: " + getMessage() + 
                   " [Parametro: " + parametroFaltante + "]";
        }
        return super.toString();
    }
}