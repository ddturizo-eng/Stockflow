/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

public class Validador {

    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public static boolean esNumeroValido(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(numero);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esEnteroValido(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(numero);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esEmailValido(String email) {
        if (email == null) return false;
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    public static boolean esCedulaValida(String cedula) {
        if (cedula == null) return false;
        return cedula.matches("\\d{7,10}");
    }

    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null) return false;
        return telefono.matches("\\d{7,10}");
    }

    public static boolean esPrecioValido(double precio) {
        return precio > 0;
    }

    public static boolean esStockValido(int stock) {
        return stock >= 0;
    }
}
