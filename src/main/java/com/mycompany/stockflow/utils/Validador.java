/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

    /**
     * Clase utilitaria para validación de datos comunes en el sistema StockFlow.
     * Provee métodos estáticos para validar textos, números, enteros, emails,
     * cédulas, teléfonos, precios y stock.
     *
     * @author Stockflow Team
     * @version 1.0
     * @since 2025
     */
    public class Validador {

        /**
         * Verifica si el texto es válido (no nulo ni vacío).
         *
         * @param texto Texto a validar.
         * @return true si el texto no es nulo ni está vacío.
         */
        public static boolean esTextoValido(String texto) {
            return texto != null && !texto.trim().isEmpty();
        }

        /**
         * Verifica si el valor es un número válido (double).
         *
         * @param numero Cadena a validar.
         * @return true si la cadena representa un número double.
         */
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

        /**
         * Verifica si la cadena es un entero válido.
         *
         * @param numero Cadena a validar.
         * @return true si la cadena representa un entero.
         */
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

        /**
         * Valida si el correo electrónico cumple con un patrón básico de email.
         *
         * @param email Cadena de email.
         * @return true si el formato es válido.
         */
        public static boolean esEmailValido(String email) {
            if (email == null) return false;
            String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            return email.matches(regex);
        }

        /**
         * Valida si la cédula contiene entre 7 y 10 dígitos.
         *
         * @param cedula Cadena de cédula.
         * @return true si la cédula es válida.
         */
        public static boolean esCedulaValida(String cedula) {
            if (cedula == null) return false;
            return cedula.matches("\\d{7,10}");
        }

        /**
         * Valida si el teléfono contiene entre 7 y 10 dígitos.
         *
         * @param telefono Cadena de teléfono.
         * @return true si el teléfono es válido.
         */
        public static boolean esTelefonoValido(String telefono) {
            if (telefono == null) return false;
            return telefono.matches("\\d{7,10}");
        }

        /**
         * Valida que el precio sea mayor a cero.
         *
         * @param precio Valor numérico del precio.
         * @return true si el precio es válido.
         */
        public static boolean esPrecioValido(double precio) {
            return precio > 0;
        }

        /**
         * Valida que el stock sea cero o positivo.
         *
         * @param stock Cantidad de stock (entero).
         * @return true si el stock es válido.
         */
        public static boolean esStockValido(int stock) {
            return stock >= 0;
        }
    }
