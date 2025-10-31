/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.io.Serializable;

public abstract class Entidad implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String id;
    
    // Constructor sin parámetros 
    public Entidad() {
        this.id = null;
    }
    
    public Entidad(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "Entidad{id='" + id + "'}";
    }
}