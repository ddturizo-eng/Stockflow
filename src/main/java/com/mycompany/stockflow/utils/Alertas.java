/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Clase utilitaria para la gestion y visualizacion de alertas en JavaFX.
 * Proporciona metodos estaticos para mostrar diferentes tipos de alertas
 * (error, advertencia, exito, informacion, confirmacion) de forma estandarizada.
 * 
 * <p>Esta clase simplifica la creacion de dialogos de alerta mediante metodos
 * estaticos que encapsulan la configuracion de Alert de JavaFX.</p>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 * Alertas.mostrarError("Error", "Operacion fallida", "No se pudo guardar el registro");
 * boolean confirmado = Alertas.mostrarConfirmacion("Confirmar", "¿Desea continuar?", "Esta accion no se puede deshacer");
 * </pre>
 * 
 * @author StockFlow Team
 * @version 2.0
 * @since 1.0
 */
public class Alertas {

    /**
     * Constructor privado para evitar instanciacion.
     * Esta clase solo contiene metodos estaticos.
     */
    private Alertas() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable");
    }

    /**
     * Muestra una alerta de error con titulo, encabezado y contenido personalizados.
     * 
     * @param titulo el titulo de la ventana de alerta
     * @param header el texto del encabezado de la alerta
     * @param contenido el mensaje detallado del error
     */
    public static void mostrarError(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de advertencia con titulo, encabezado y contenido personalizados.
     * 
     * @param titulo el titulo de la ventana de alerta
     * @param header el texto del encabezado de la alerta
     * @param contenido el mensaje detallado de la advertencia
     */
    public static void mostrarAdvertencia(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de exito/informacion con titulo, encabezado y contenido personalizados.
     * 
     * @param titulo el titulo de la ventana de alerta
     * @param header el texto del encabezado de la alerta
     * @param contenido el mensaje detallado de exito
     */
    public static void mostrarExito(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de informacion con titulo, encabezado y contenido personalizados.
     * Este metodo es un alias de mostrarExito.
     * 
     * @param titulo el titulo de la ventana de alerta
     * @param header el texto del encabezado de la alerta
     * @param contenido el mensaje informativo
     */
    public static void mostrarInformacion(String titulo, String header, String contenido) {
        mostrarExito(titulo, header, contenido);
    }

    /**
     * Muestra un dialogo de confirmacion y espera la respuesta del usuario.
     * 
     * @param titulo el titulo de la ventana de confirmacion
     * @param header el texto del encabezado
     * @param contenido el mensaje de confirmacion
     * @return true si el usuario presiono OK, false si cancelo
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
     * Muestra un error simple con solo titulo y mensaje (sin encabezado).
     * 
     * @param titulo el titulo de la ventana de error
     * @param mensaje el mensaje de error
     */
    public static void error(String titulo, String mensaje) {
        mostrarError(titulo, null, mensaje);
    }

    /**
     * Muestra una advertencia simple con solo titulo y mensaje (sin encabezado).
     * 
     * @param titulo el titulo de la ventana de advertencia
     * @param mensaje el mensaje de advertencia
     */
    public static void advertencia(String titulo, String mensaje) {
        mostrarAdvertencia(titulo, null, mensaje);
    }

    /**
     * Muestra un mensaje de exito simple con solo titulo y mensaje (sin encabezado).
     * 
     * @param titulo el titulo de la ventana de exito
     * @param mensaje el mensaje de exito
     */
    public static void exito(String titulo, String mensaje) {
        mostrarExito(titulo, null, mensaje);
    }

    /**
     * Muestra informacion simple con solo titulo y mensaje (sin encabezado).
     * 
     * @param titulo el titulo de la ventana informativa
     * @param mensaje el mensaje informativo
     */
    public static void info(String titulo, String mensaje) {
        mostrarInformacion(titulo, null, mensaje);
    }
}