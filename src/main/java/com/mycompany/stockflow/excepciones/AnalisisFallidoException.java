/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.excepciones;

/**
 * Excepción lanzada cuando el análisis de IA falla
 */
public class AnalisisFallidoException extends Exception {
    
    private String tipoAnalisis;
    
    public AnalisisFallidoException(String message) {
        super(message);
    }
    
    public AnalisisFallidoException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AnalisisFallidoException(String message, String tipoAnalisis) {
        super(message);
        this.tipoAnalisis = tipoAnalisis;
    }
    
    public AnalisisFallidoException(String message, String tipoAnalisis, Throwable cause) {
        super(message, cause);
        this.tipoAnalisis = tipoAnalisis;
    }
    
    public String getTipoAnalisis() {
        return tipoAnalisis;
    }
    
    @Override
    public String toString() {
        if (tipoAnalisis != null) {
            return "AnalisisFallidoException: " + getMessage() + 
                   " [Tipo: " + tipoAnalisis + "]";
        }
        return super.toString();
    }
}