/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.ChatBotServicio;
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
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador de la interfaz de ChatBot conversacional.
 * 
 * <p>Gestiona la interfaz de usuario del asistente de IA, permitiendo
 * a los usuarios hacer consultas en lenguaje natural sobre su negocio
 * y recibir respuestas contextualizadas en tiempo real.</p>
 * 
 * <p>Características de la interfaz:</p>
 * <ul>
 *   <li>Chat en tiempo real con burbujas de mensajes</li>
 *   <li>Sugerencias rápidas de consultas comunes</li>
 *   <li>Indicador de carga durante procesamiento</li>
 *   <li>Scroll automático al último mensaje</li>
 *   <li>Historial de conversación persistente</li>
 *   <li>Función de limpieza de conversación</li>
 * </ul>
 * 
 * <p>La interfaz procesa las consultas de forma asíncrona para mantener
 * la UI responsiva durante el análisis de datos.</p>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see ChatBotServicio
 * @see ChatMensaje
 */
public class ChatBotController implements Initializable {
    
    /** Panel de scroll que contiene los mensajes */
    @FXML
    private ScrollPane scrollPane;
    
    /** Contenedor vertical de mensajes */
    @FXML
    private VBox mensajesContainer;
    
    /** Campo de texto para entrada del usuario */
    @FXML
    private TextField inputTextField;
    
    /** Botón para enviar mensajes */
    @FXML
    private Button enviarButton;
    
    /** Botón para limpiar el historial */
    @FXML
    private Button limpiarButton;
    
    /** Contenedor de sugerencias rápidas */
    @FXML
    private VBox sugerenciasBox;
    
    /** Servicio de ChatBot para procesar consultas */
    private ChatBotServicio chatBotServicio;
    
    /** Indica si se está esperando respuesta del sistema */
    private boolean esperandoRespuesta = false;
    
    /** Sugerencias predefinidas de consultas comunes */
    private final String[] SUGERENCIAS = {
        "¿Quien fue mi mejor cliente este mes?",
        "¿Como van las ventas esta semana?",
        "¿Que productos tienen stock critico?",
        "Muestrame un resumen general del negocio",
        "¿Cual es el margen de ganancia promedio?",
        "¿Tengo clientes inactivos?"
    };
    
    /**
     * Inicializa el controlador después de que se cargue el archivo FXML.
     * 
     * @param url la ubicación del FXML
     * @param rb los recursos localizados
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chatBotServicio = new ChatBotServicio();
        configurarUI();
        cargarMensajesIniciales();
        mostrarSugerencias();
    }
    
    /**
     * Configura los componentes de la interfaz de usuario.
     * 
     * <p>Establece los manejadores de eventos, configuraciones de scroll
     * y comportamientos interactivos de los elementos de la UI.</p>
     */
    private void configurarUI() {
        // Scroll automático al final
        scrollPane.vvalueProperty().bind(mensajesContainer.heightProperty());
        
        // Enter para enviar
        inputTextField.setOnAction(event -> enviarMensaje());
        
        // Configurar botones
        enviarButton.setOnAction(event -> enviarMensaje());
        limpiarButton.setOnAction(event -> limpiarChat());
        
        // Foco inicial en el campo de entrada
        Platform.runLater(() -> inputTextField.requestFocus());
    }
    
    /**
     * Carga los mensajes iniciales del historial de conversación.
     * 
     * <p>Muestra el mensaje de bienvenida del asistente al cargar la interfaz.</p>
     */
    private void cargarMensajesIniciales() {
        List<ChatMensaje> historial = chatBotServicio.getHistorialConversacion();
        for (ChatMensaje mensaje : historial) {
            agregarMensajeAUI(mensaje);
        }
    }
    
    /**
     * Muestra las sugerencias rápidas de consultas comunes.
     * 
     * <p>Crea botones interactivos para cada sugerencia que permiten
     * al usuario iniciar consultas con un solo clic.</p>
     */
    private void mostrarSugerencias() {
        sugerenciasBox.getChildren().clear();
        
        Label titulo = new Label("Preguntas frecuentes:");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #666;");
        sugerenciasBox.getChildren().add(titulo);
        
        for (String sugerencia : SUGERENCIAS) {
            Button btnSugerencia = new Button(sugerencia);
            btnSugerencia.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 8px; " +
                "-fx-background-radius: 8px; " +
                "-fx-padding: 8px 12px; " +
                "-fx-cursor: hand; " +
                "-fx-font-size: 11px; " +
                "-fx-text-fill: #333;"
            );
            btnSugerencia.setMaxWidth(Double.MAX_VALUE);
            btnSugerencia.setAlignment(Pos.CENTER_LEFT);
            
            btnSugerencia.setOnMouseEntered(e -> 
                btnSugerencia.setStyle(
                    "-fx-background-color: #f5f5f5; " +
                    "-fx-border-color: #2196F3; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-padding: 8px 12px; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-size: 11px; " +
                    "-fx-text-fill: #2196F3;"
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
                    "-fx-text-fill: #333;"
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
    
    /**
     * Procesa y envía el mensaje del usuario al servicio de ChatBot.
     * 
     * <p>Valida la entrada, muestra el mensaje en la UI, procesa la consulta
     * de forma asíncrona y muestra la respuesta del asistente.</p>
     */
    @FXML
    private void enviarMensaje() {
        String texto = inputTextField.getText().trim();
        
        if (texto.isEmpty() || esperandoRespuesta) {
            return;
        }
        
        // Ocultar sugerencias después del primer mensaje
        sugerenciasBox.setVisible(false);
        
        // Limpiar input
        inputTextField.clear();
        
        // Crear mensaje del usuario
        ChatMensaje mensajeUsuario = new ChatMensaje("user", texto);
        agregarMensajeAUI(mensajeUsuario);
        
        // Mostrar indicador de carga
        esperandoRespuesta = true;
        enviarButton.setDisable(true);
        HBox cargando = mostrarIndicadorCarga();
        
        // Procesar en segundo plano
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
    
    /**
     * Agrega un mensaje a la interfaz de usuario.
     * 
     * <p>Crea los elementos visuales necesarios para mostrar el mensaje
     * con el estilo apropiado según el rol (usuario o asistente).</p>
     * 
     * @param mensaje el mensaje a mostrar
     */
    private void agregarMensajeAUI(ChatMensaje mensaje) {
        HBox contenedorMensaje = new HBox(10);
        contenedorMensaje.setPadding(new Insets(10));
        contenedorMensaje.setMaxWidth(Double.MAX_VALUE);
        
        if (mensaje.esUsuario()) {
            // Mensaje del usuario (derecha, azul)
            contenedorMensaje.setAlignment(Pos.CENTER_RIGHT);
            VBox burbuja = crearBurbujaMensaje(mensaje, true, mensaje.isEsError());
            contenedorMensaje.getChildren().add(burbuja);
            
        } else {
            // Mensaje del asistente (izquierda, blanco)
            contenedorMensaje.setAlignment(Pos.CENTER_LEFT);
            
            // Ícono del bot
            Label iconoBot = new Label("🤖");
            iconoBot.setStyle(
                "-fx-font-size: 24px; " +
                "-fx-padding: 5px; " +
                "-fx-background-color: #2196F3; " +
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
        
        // Scroll automático al final
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
    
    /**
     * Crea la burbuja visual de un mensaje.
     * 
     * @param mensaje el mensaje a mostrar
     * @param esUsuario true si es mensaje del usuario
     * @param esError true si es un mensaje de error
     * @return el contenedor VBox con el mensaje formateado
     */
    private VBox crearBurbujaMensaje(ChatMensaje mensaje, boolean esUsuario, boolean esError) {
        VBox burbuja = new VBox(5);
        burbuja.setMaxWidth(600);
        burbuja.setPadding(new Insets(12, 15, 12, 15));
        
        String estilo;
        if (esError) {
            estilo = "-fx-background-color: #ffebee; " +
                    "-fx-background-radius: 15px; " +
                    "-fx-border-color: #ef5350; " +
                    "-fx-border-radius: 15px; " +
                    "-fx-border-width: 1px;";
        } else if (esUsuario) {
            estilo = "-fx-background-color: linear-gradient(to bottom right, #2196F3, #1976D2); " +
                    "-fx-background-radius: 15px 15px 5px 15px;";
        } else {
            estilo = "-fx-background-color: white; " +
                    "-fx-background-radius: 15px 15px 15px 5px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);";
        }
        
        burbuja.setStyle(estilo);
        
        // Contenido del mensaje
        TextFlow textFlow = new TextFlow();
        Text textoMensaje = new Text(mensaje.getContenido());
        textoMensaje.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-fill: " + (esUsuario ? "white" : "#333") + "; " +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        textFlow.getChildren().add(textoMensaje);
        textFlow.setMaxWidth(580);
        
        // Timestamp
        Label timestamp = new Label(mensaje.getHoraFormateada());
        timestamp.setStyle(
            "-fx-font-size: 10px; " +
            "-fx-text-fill: " + (esUsuario ? "rgba(255,255,255,0.7)" : "#999") + ";"
        );
        
        burbuja.getChildren().addAll(textFlow, timestamp);
        return burbuja;
    }
    
    /**
     * Muestra un indicador de carga mientras se procesa la consulta.
     * 
     * @return el contenedor HBox con el indicador de carga
     */
    private HBox mostrarIndicadorCarga() {
        HBox contenedor = new HBox(10);
        contenedor.setPadding(new Insets(10));
        contenedor.setAlignment(Pos.CENTER_LEFT);
        
        Label iconoBot = new Label("🤖");
        iconoBot.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-padding: 5px; " +
            "-fx-background-color: #2196F3; " +
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
        Label texto = new Label("Analizando...");
        texto.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        HBox contenidoCarga = new HBox(8, progress, texto);
        contenidoCarga.setAlignment(Pos.CENTER_LEFT);
        burbuja.getChildren().add(contenidoCarga);
        
        contenedor.getChildren().addAll(iconoBot, burbuja);
        mensajesContainer.getChildren().add(contenedor);
        
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
        
        return contenedor;
    }
    
    /**
     * Muestra un mensaje de error en la interfaz.
     * 
     * @param mensajeError el texto del error a mostrar
     */
    private void mostrarError(String mensajeError) {
        ChatMensaje error = new ChatMensaje("assistant", mensajeError, true);
        agregarMensajeAUI(error);
    }
    
    /**
     * Limpia el historial de conversación y reinicia el chat.
     */
    @FXML
    private void limpiarChat() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Limpiar Conversación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas limpiar el historial?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");
        
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
}