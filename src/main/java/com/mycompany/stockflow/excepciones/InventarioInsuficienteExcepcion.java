/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.excepciones;

public class InventarioInsuficienteExcepcion extends StockFlowExcepcion {
    
    public InventarioInsuficienteExcepcion(String producto, int disponible, int solicitado) {
        super("Stock insuficiente para " + producto + ". Disponible: " + disponible + ", Solicitado: " + solicitado);
    }
}
