/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.EmailNotificacionServicio;
import com.mycompany.stockflow.Logica.UsuarioServicio;
import com.mycompany.stockflow.Modelo.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * Controlador para el diálogo de recuperación de contraseña.
 * 
 * Este controlador gestiona la interfaz de recuperación de contraseña,
 * permitiendo que los usuarios busquen su cuenta por nombre de usuario
 * y soliciten la recuperación de contraseña mediante notificación por email.
 * 
 * El flujo es el siguiente:
 * 1. El usuario ingresa su nombre de usuario
 * 2. Se busca el usuario en la base de datos
 * 3. Si se encuentra, se muestran los datos del usuario
 * 4. El usuario puede enviar una solicitud de recuperación por email
 * 5. Se notifica al administrador para que verifique la identidad
 * 
 * @author Equipo StockFlow
 * @version 1.0
 * @since 1.0
 */

public class DialogoOlvidePasswordController implements Initializable {
    
    @FXML
    private TextField txtUsuario;
    
    @FXML
    private Label lblEstado;
    
    @FXML
    private Button btnBuscar;
    
    @FXML
    private Button btnCancelar;
    
    @FXML
    private Button btnEnviarEmail;
    
    @FXML
    private VBox vboxDatos;
    
    @FXML
    private Label lblUsuarioEncontrado;
    
    @FXML
    private Label lblEmailUsuario;
    
    private UsuarioServicio usuarioServicio;
    private Usuario usuarioEncontrado;

    /**
     * Inicializa el controlador de la vista.
     * 
     * Se ejecuta cuando el documento FXML es cargado. Realiza
     * las siguientes acciones:
     * - Inicializa el servicio de usuario
     * - Oculta el contenedor de datos de usuario
     * - Establece el mensaje de estado inicial
     * 
     * @param url La ubicación relativa del objeto FXML
     * @param rb El ResourceBundle específico de la localización
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioServicio = new UsuarioServicio();
        vboxDatos.setVisible(false);
        lblEstado.setText("Ingresa tu nombre de usuario");
        System.out.println("DialogoOlvidePasswordController inicializado");
    }

    /**
     * Busca un usuario en la base de datos por nombre de usuario.
     * 
     * Realiza la búsqueda del usuario ingresado en el campo de texto.
     * Si se encuentra el usuario:
     * - Muestra sus datos (nombre completo y email)
     * - Habilita la opción de enviar solicitud por email
     * - Deshabilita los campos de búsqueda
     * 
     * Si no se encuentra o hay error:
     * - Muestra un mensaje de error al usuario
     * - Mantiene los campos habilitados para reintentar
     */
    @FXML
    private void handleBuscar() {
        String username = txtUsuario.getText().trim();
        if (username.isEmpty()) {
            mostrarError("Por favor ingresa un nombre de usuario");
            return;
        }

        try {
            usuarioEncontrado = usuarioServicio.obtenerPorUsername(username);
            if (usuarioEncontrado != null && usuarioEncontrado.getUsername() != null) {
                lblUsuarioEncontrado.setText(usuarioEncontrado.getNombreCompleto());
                lblEmailUsuario.setText(usuarioEncontrado.getEmail() != null && !usuarioEncontrado.getEmail().isEmpty() 
                    ? usuarioEncontrado.getEmail() 
                    : "No tiene email registrado");
                vboxDatos.setVisible(true);
                lblEstado.setText("Usuario encontrado - Haz clic en el boton para enviar la solicitud");
                lblEstado.setStyle("-fx-text-fill: #10b981;");
                btnBuscar.setDisable(true);
                txtUsuario.setDisable(true);
                System.out.println("Usuario encontrado: " + username);
            } else {
                mostrarError("No se encontro usuario con ese nombre");
                vboxDatos.setVisible(false);
                System.out.println("Usuario no encontrado: " + username);
            }
        } catch (Exception e) {
            mostrarError("Error al buscar usuario: " + e.getMessage());
            System.err.println("Error en handleBuscar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Envía una solicitud de recuperación de contraseña por email.
     * 
     * Valida que exista un usuario encontrado y una dirección de email válida.
     * Envía un correo de notificación al administrador para que verifique
     * la identidad del usuario y gestione la recuperación de contraseña.
     * 
     * Muestra un diálogo de progreso mientras se envía el correo y notifica
     * al usuario cuando se completa la operación.
     */
    @FXML
    private void handleEnviarEmail() {
        if (usuarioEncontrado == null) {
            mostrarError("Por favor busca un usuario primero");
            return;
        }

        String email = usuarioEncontrado.getEmail();
        if (email == null || email.isEmpty()) {
            mostrarError("Este usuario no tiene email registrado");
            return;
        }

        Alert progress = new Alert(Alert.AlertType.INFORMATION);
        progress.setTitle("Enviando");
        progress.setHeaderText(null);
        progress.setContentText("Enviando correo al administrador...");
        progress.show();

        try {
            boolean exito = EmailNotificacionServicio.enviarNotificacionEmail(
                usuarioEncontrado.getUsername(),
                email
            );
            progress.close();
            if (exito) {
                mostrarExito("Correo enviado", 
                    "Se ha enviado una solicitud al administrador.\n" +
                    "Te contactaremos pronto para verificar tu identidad.");
                cerrarDialogo();
            } else {
                mostrarError("No se pudo enviar el correo.\n" +
                    "Verifica las variables de entorno y tu conexion a Internet.\n\n" +
                    "Revisa la consola para mas detalles.");
            }
        } catch (Exception e) {
            progress.close();
            mostrarError("Error al enviar email: " + e.getMessage());
            System.err.println("Error en handleEnviarEmail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cancela el diálogo de recuperación de contraseña.
     * 
     * Se ejecuta cuando el usuario hace clic en el botón cancelar.
     * Cierra la ventana del diálogo.
     */
    @FXML
    private void handleCancelar() {
        cerrarDialogo();
    }

    /**
     * Cierra la ventana del diálogo.
     * 
     * Obtiene la ventana (Stage) actual y la cierra,
     * finalizando el proceso de recuperación de contraseña.
     */
    private void cerrarDialogo() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra un diálogo de error al usuario.
     * 
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de éxito al usuario.
     * 
     * @param titulo El título del diálogo de éxito
     * @param mensaje El mensaje de éxito a mostrar
     */
    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}