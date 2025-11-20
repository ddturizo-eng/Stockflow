/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.ChatBotInvitadoServicio;
import com.mycompany.stockflow.Modelo.ChatMensaje;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador del ChatBot para clientes invitados.
 * 
 * <p>Gestiona la interfaz de usuario del asistente virtual libre
 * que puede responder cualquier tipo de pregunta de los visitantes.</p>
 * 
 * @author StockFlow Team
 * @version 1.0
 */
public class ChatBotInvitadoController implements Initializable {
    
    @FXML private ScrollPane scrollPane;
    @FXML private VBox mensajesContainer;
    @FXML private TextField inputTextField;
    @FXML private Button enviarButton;
    @FXML private Button limpiarButton;
    @FXML private Button cerrarButton;
    @FXML private VBox sugerenciasBox;
    
    private ChatBotInvitadoServicio chatBotServicio;
    private boolean esperandoRespuesta = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chatBotServicio = new ChatBotInvitadoServicio();
        configurarUI();
        cargarMensajesIniciales();
        mostrarSugerencias();
    }
    
    private void configurarUI() {
        scrollPane.vvalueProperty().bind(mensajesContainer.heightProperty());
        inputTextField.setOnAction(event -> enviarMensaje());
        enviarButton.setOnAction(event -> enviarMensaje());
        limpiarButton.setOnAction(event -> limpiarChat());
        if (cerrarButton != null) {
            cerrarButton.setOnAction(event -> cerrarVentana());
        }
        Platform.runLater(() -> inputTextField.requestFocus());
    }
    
    private void cargarMensajesIniciales() {
        List<ChatMensaje> historial = chatBotServicio.getHistorialConversacion();
        for (ChatMensaje mensaje : historial) {
            agregarMensajeAUI(mensaje);
        }
    }
    
    private void mostrarSugerencias() {
        sugerenciasBox.getChildren().clear();
        
        Label titulo = new Label("Preguntas que puedes hacer:");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #666;");
        sugerenciasBox.getChildren().add(titulo);
        
        String[] sugerencias = chatBotServicio.obtenerSugerencias();
        
        for (String sugerencia : sugerencias) {
            Button btnSugerencia = new Button(sugerencia);
            btnSugerencia.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 8px; " +
                "-fx-background-radius: 8px; " +
                "-fx-padding: 8px 12px; " +
                "-fx-cursor: hand; " +
                "-fx-font-size: 11px; " +
                "-fx-text-fill: #333; " +
                "-fx-alignment: center-left;"
            );
            btnSugerencia.setMaxWidth(Double.MAX_VALUE);
            btnSugerencia.setWrapText(true);
            
            btnSugerencia.setOnMouseEntered(e -> 
                btnSugerencia.setStyle(
                    "-fx-background-color: #f5f5f5; " +
                    "-fx-border-color: #27ae60; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-padding: 8px 12px; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-size: 11px; " +
                    "-fx-text-fill: #27ae60; " +
                    "-fx-alignment: center-left;"
                )
            );
            
            btnSugerencia.setOnMouseExited(e -> 
                btnSugerencia.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-padding: 8px 12px; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-size: 11px; " +
                    "-fx-text-fill: #333; " +
                    "-fx-alignment: center-left;"
                )
            );
            
            btnSugerencia.setOnAction(e -> {
                inputTextField.setText(sugerencia);
                enviarMensaje();
                sugerenciasBox.setVisible(false);
            });
            
            sugerenciasBox.getChildren().add(btnSugerencia);
        }
    }
    
    @FXML
    private void enviarMensaje() {
        String texto = inputTextField.getText().trim();
        
        if (texto.isEmpty() || esperandoRespuesta) {
            return;
        }
        
        sugerenciasBox.setVisible(false);
        inputTextField.clear();
        
        ChatMensaje mensajeUsuario = new ChatMensaje("user", texto);
        agregarMensajeAUI(mensajeUsuario);
        
        esperandoRespuesta = true;
        enviarButton.setDisable(true);
        HBox cargando = mostrarIndicadorCarga();
        
        CompletableFuture.runAsync(() -> {
            try {
                ChatMensaje respuesta = chatBotServicio.procesarPregunta(texto);
                
                Platform.runLater(() -> {
                    mensajesContainer.getChildren().remove(cargando);
                    agregarMensajeAUI(respuesta);
                    esperandoRespuesta = false;
                    enviarButton.setDisable(false);
                    inputTextField.requestFocus();
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mensajesContainer.getChildren().remove(cargando);
                    mostrarError("Error al procesar: " + e.getMessage());
                    esperandoRespuesta = false;
                    enviarButton.setDisable(false);
                });
            }
        });
    }
    
    private void agregarMensajeAUI(ChatMensaje mensaje) {
        HBox contenedorMensaje = new HBox(10);
        contenedorMensaje.setPadding(new Insets(10));
        contenedorMensaje.setMaxWidth(Double.MAX_VALUE);
        
        if (mensaje.esUsuario()) {
            contenedorMensaje.setAlignment(Pos.CENTER_RIGHT);
            VBox burbuja = crearBurbujaMensaje(mensaje, true, mensaje.isEsError());
            contenedorMensaje.getChildren().add(burbuja);
            
        } else {
            contenedorMensaje.setAlignment(Pos.CENTER_LEFT);
            
            Label iconoBot = new Label("🤖");
            iconoBot.setStyle(
                "-fx-font-size: 24px; " +
                "-fx-padding: 5px; " +
                "-fx-background-color: #32718F; " +
                "-fx-background-radius: 50%; " +
                "-fx-min-width: 40px; " +
                "-fx-min-height: 40px; " +
                "-fx-max-width: 40px; " +
                "-fx-max-height: 40px; " +
                "-fx-alignment: center;"
            );
            
            VBox burbuja = crearBurbujaMensaje(mensaje, false, mensaje.isEsError());
            
            contenedorMensaje.getChildren().addAll(iconoBot, burbuja);
        }
        
        mensajesContainer.getChildren().add(contenedorMensaje);
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
    
    private VBox crearBurbujaMensaje(ChatMensaje mensaje, boolean esUsuario, boolean esError) {
        VBox burbuja = new VBox(5);
        burbuja.setMaxWidth(600);
        burbuja.setPadding(new Insets(12, 15, 12, 15));
        
        String estilo;
        if (esError) {
            estilo = "-fx-background-color: #32718F; " +
                    "-fx-background-radius: 15px; " +
                    "-fx-border-color: #32718F; " +
                    "-fx-border-radius: 15px; " +
                    "-fx-border-width: 1px;";
        } else if (esUsuario) {
            estilo = "-fx-background-color: linear-gradient(to bottom right, #32718F, #207BA8); " +
                    "-fx-background-radius: 15px 15px 5px 15px;";
        } else {
            estilo = "-fx-background-color: white; " +
                    "-fx-background-radius: 15px 15px 15px 5px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);";
        }
        
        burbuja.setStyle(estilo);
        
        TextFlow textFlow = new TextFlow();
        Text textoMensaje = new Text(mensaje.getContenido());
        textoMensaje.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-fill: " + (esUsuario ? "white" : "#333") + "; " +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        textFlow.getChildren().add(textoMensaje);
        textFlow.setMaxWidth(580);
        
        Label timestamp = new Label(mensaje.getHoraFormateada());
        timestamp.setStyle(
            "-fx-font-size: 10px; " +
            "-fx-text-fill: " + (esUsuario ? "rgba(255,255,255,0.7)" : "#999") + ";"
        );
        
        burbuja.getChildren().addAll(textFlow, timestamp);
        return burbuja;
    }
    
    private HBox mostrarIndicadorCarga() {
        HBox contenedor = new HBox(10);
        contenedor.setPadding(new Insets(10));
        contenedor.setAlignment(Pos.CENTER_LEFT);
        
        Label iconoBot = new Label("🤖");
        iconoBot.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-padding: 5px; " +
            "-fx-background-color: #32718F; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 40px; " +
            "-fx-min-height: 40px; " +
            "-fx-max-width: 40px; " +
            "-fx-max-height: 40px; " +
            "-fx-alignment: center;"
        );
        
        VBox burbuja = new VBox(5);
        burbuja.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15px; " +
            "-fx-padding: 12px 20px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        
        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(20, 20);
        Label texto = new Label("Pensando...");
        texto.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        HBox contenidoCarga = new HBox(8, progress, texto);
        contenidoCarga.setAlignment(Pos.CENTER_LEFT);
        burbuja.getChildren().add(contenidoCarga);
        
        contenedor.getChildren().addAll(iconoBot, burbuja);
        mensajesContainer.getChildren().add(contenedor);
        
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
        
        return contenedor;
    }
    
    private void mostrarError(String mensajeError) {
        ChatMensaje error = new ChatMensaje("assistant", mensajeError, true);
        agregarMensajeAUI(error);
    }
    
    @FXML
    private void limpiarChat() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Limpiar Conversación");
        confirmacion.setHeaderText("¿Deseas reiniciar la conversación?");
        confirmacion.setContentText("Se borrará todo el historial de mensajes.");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            mensajesContainer.getChildren().clear();
            chatBotServicio.limpiarHistorial();
            cargarMensajesIniciales();
            sugerenciasBox.setVisible(true);
            inputTextField.clear();
            inputTextField.requestFocus();
        }
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
        stage.close();
    }
}