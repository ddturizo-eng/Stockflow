/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.excepciones;

public class VentaInvalidaExcepcion extends StockFlowExcepcion {
    
    public VentaInvalidaExcepcion(String mensaje) {
        super("Venta inválida: " + mensaje);
    }
}
