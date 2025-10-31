/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.excepciones;

/**
 * Excepción lanzada cuando las credenciales de login son inválidas
 */
public class CredencialesInvalidasExcepcion extends StockFlowExcepcion {
    
    public CredencialesInvalidasExcepcion(String mensaje) {
        super(mensaje);
    }
    
    public CredencialesInvalidasExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    /**
     * Constructor por defecto
     */
    public CredencialesInvalidasExcepcion() {
        super("Usuario o contraseña incorrectos");
    }
}
