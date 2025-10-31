/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.excepciones;

public class ProductoNoEncontradoExcepcion extends StockFlowExcepcion {
    
    public ProductoNoEncontradoExcepcion(String codigo) {
        super("Producto con código " + codigo + " no encontrado");
    }
}
