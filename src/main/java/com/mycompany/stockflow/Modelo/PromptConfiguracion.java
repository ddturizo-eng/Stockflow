/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;


public class PromptConfiguracion extends Entidad {
    
    private String nombre;
    private String tipoAnalisis;
    private String templatePrompt;
    private String contextoAdicional;
    private boolean activo;
    private double temperatura;
    private int maxTokens;
    
    public PromptConfiguracion() {
        super();
        this.activo = true;
        this.temperatura = 0.7;
        this.maxTokens = 2000;
    }
    
    public PromptConfiguracion(String nombre, String tipoAnalisis, String templatePrompt) {
        super(nombre);
        this.nombre = nombre;
        this.tipoAnalisis = tipoAnalisis;
        this.templatePrompt = templatePrompt;
        this.activo = true;
        this.temperatura = 0.7;
        this.maxTokens = 2000;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
        setId(nombre);
    }
    
    public String getTipoAnalisis() {
        return tipoAnalisis;
    }
    
    public void setTipoAnalisis(String tipoAnalisis) {
        this.tipoAnalisis = tipoAnalisis;
    }
    
    public String getTemplatePrompt() {
        return templatePrompt;
    }
    
    public void setTemplatePrompt(String templatePrompt) {
        this.templatePrompt = templatePrompt;
    }
    
    public String getContextoAdicional() {
        return contextoAdicional;
    }
    
    public void setContextoAdicional(String contextoAdicional) {
        this.contextoAdicional = contextoAdicional;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public double getTemperatura() {
        return temperatura;
    }
    
    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }
    
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    @Override
    public String toString() {
        return nombre + " (" + tipoAnalisis + ")";
    }
}
