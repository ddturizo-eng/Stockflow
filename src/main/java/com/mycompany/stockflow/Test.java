/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow;

import com.mycompany.stockflow.utils.DeepSeekAPIClient;

public class Test {
    public static void main(String[] args) {
        try {
            DeepSeekAPIClient client = new DeepSeekAPIClient();
            String respuesta = client.enviarPrompt("Di solamente 'HOLA STOCKFLOW'");
            System.out.println("RESPUESTA: " + respuesta);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}