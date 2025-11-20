/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Clase utilitaria para mostrar alertas en JavaFX
 */
public class Alertas {

    /**
     * Muestra una alerta de error
     */
    public static void mostrarError(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de advertencia
     */
    public static void mostrarAdvertencia(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de éxito/información
     */
    public static void mostrarExito(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de información
     */
    public static void mostrarInformacion(String titulo, String header, String contenido) {
        mostrarExito(titulo, header, contenido);
    }

    /**
     * Muestra un diálogo de confirmación
     * @return true si el usuario presionó OK, false si canceló
     */
    public static boolean mostrarConfirmacion(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    /**
     * Muestra un error simple con solo título y mensaje
     */
    public static void error(String titulo, String mensaje) {
        mostrarError(titulo, null, mensaje);
    }

    /**
     * Muestra una advertencia simple
     */
    public static void advertencia(String titulo, String mensaje) {
        mostrarAdvertencia(titulo, null, mensaje);
    }

    /**
     * Muestra un mensaje de éxito simple
     */
    public static void exito(String titulo, String mensaje) {
        mostrarExito(titulo, null, mensaje);
    }

    /**
     * Muestra información simple
     */
    public static void info(String titulo, String mensaje) {
        mostrarInformacion(titulo, null, mensaje);
    }
}