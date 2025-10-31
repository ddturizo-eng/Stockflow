/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.excepciones;


public class AccesoDenegadoExcepcion extends StockFlowExcepcion {
    
    public AccesoDenegadoExcepcion(String mensaje) {
        super(mensaje);
    }
    
    public AccesoDenegadoExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    /**
     * Constructor por defecto
     */
    public AccesoDenegadoExcepcion() {
        super("No tienes permisos para realizar esta acción");
    }
    
    /**
     * Constructor con el nombre de la acción denegada
     */
    public AccesoDenegadoExcepcion(String accion, String rolRequerido) {
        super(String.format("No tienes permisos para %s. Se requiere rol: %s", accion, rolRequerido));
    }
}
