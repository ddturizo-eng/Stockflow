package com.mycompany.stockflow;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controlador principal del Dashboard de StockFlow
 * Gestiona la navegación entre las diferentes vistas del sistema
 * 
 * @author 
 */
public class DashboardController implements Initializable {
    
    // Referencias a botones del menú
    @FXML
    private Button btnVentas;
    
    @FXML
    private Button btnProductos;
    
    @FXML
    private Button btnClientes;
    
    @FXML
    private Button btnInventario;
    
    @FXML
    private Button btnFacturacion;
    
    @FXML
    private Button btnEstadisticas;
    
    @FXML
    private Button btnUsuarios;
    
    @FXML
    private Button btnCerrarSesion;
    
    // Referencias a elementos de la interfaz
    @FXML
    private Label lblTituloSeccion;
    
    @FXML
    private Label lblUsuario;
    
    @FXML
    private Label lblFecha;
    
    @FXML
    private AnchorPane contenedorPrincipal;
    
    // Variable para rastrear el botón activo
    private Button botonActivo;
    
    // Estilos CSS
    private static final String ESTILO_BOTON_ACTIVO = 
        "-fx-background-color: rgba(255, 255, 255, 0.2); -fx-background-radius: 10; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: rgba(255, 255, 255, 0.3); -fx-border-radius: 10; -fx-border-width: 1;";
    
    private static final String ESTILO_BOTON_INACTIVO = 
        "-fx-background-color: transparent; -fx-background-radius: 10; -fx-cursor: hand; -fx-border-color: transparent; -fx-border-radius: 10;";
    
    private static final String ESTILO_BOTON_HOVER = 
        "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-cursor: hand; -fx-text-fill: white; -fx-border-color: transparent; -fx-border-radius: 10;";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar el usuario (puedes cambiar "Admin" por el usuario real de la sesión)
        setUsuario("Admin");
        
        // Inicializar la fecha actual
        actualizarFecha();
        
        // Configurar efectos hover para todos los botones del menú
        configurarEfectosHover();
        
        // Mensaje de bienvenida en consola
        System.out.println("Dashboard inicializado correctamente");
    }
    
    /**
     * Actualiza la etiqueta de fecha con la fecha y hora actual
     */
    private void actualizarFecha() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", new Locale("es", "ES"));
        lblFecha.setText(ahora.format(formato));
    }
    
    /**
     * Configura los efectos hover para los botones del menú
     */
    private void configurarEfectosHover() {
        configurarHoverBoton(btnVentas);
        configurarHoverBoton(btnProductos);
        configurarHoverBoton(btnClientes);
        configurarHoverBoton(btnInventario);
        configurarHoverBoton(btnFacturacion);
        configurarHoverBoton(btnEstadisticas);
        configurarHoverBoton(btnUsuarios);
    }
    
    /**
     * Configura el efecto hover para un botón específico
     */
    private void configurarHoverBoton(Button boton) {
        boton.setOnMouseEntered(e -> {
            if (boton != botonActivo) {
                boton.setStyle(ESTILO_BOTON_HOVER);
            }
        });
        
        boton.setOnMouseExited(e -> {
            if (boton != botonActivo) {
                boton.setStyle(ESTILO_BOTON_INACTIVO);
            }
        });
    }
    
    /**
     * Establece el estilo del botón activo
     */
    private void establecerBotonActivo(Button boton) {
        // Remover estilo activo del botón anterior
        if (botonActivo != null) {
            botonActivo.setStyle(ESTILO_BOTON_INACTIVO);
        }
        
        // Establecer nuevo botón activo
        boton.setStyle(ESTILO_BOTON_ACTIVO);
        botonActivo = boton;
    }
    
    /**
     * Carga una vista FXML en el contenedor principal
     * 
     * @param rutaFxml Ruta del archivo FXML a cargar
     * @param titulo Título de la sección
     * @param boton Botón del menú que se activó
     */
    private void cargarVista(String rutaFxml, String titulo, Button boton) {
        try {
            // Actualizar título
            lblTituloSeccion.setText(titulo);
            
            // Establecer botón activo
            establecerBotonActivo(boton);
            
            // Cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent vista = loader.load();
            
            // Limpiar contenedor
            contenedorPrincipal.getChildren().clear();
            
            // Configurar anclajes para que ocupe todo el espacio
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);
            
            // Agregar vista al contenedor
            contenedorPrincipal.getChildren().add(vista);
            
            // Aplicar animación de transición
            aplicarAnimacionFade(vista);
            
            System.out.println("Vista cargada: " + titulo);
            
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + rutaFxml);
            e.printStackTrace();
            mostrarError("Error al cargar vista", 
                "No se pudo cargar la vista: " + titulo + "\n\nAsegúrese de que el archivo " + 
                rutaFxml + " existe en la carpeta de recursos.");
        }
    }
    
    /**
     * Aplica una animación de fade a un nodo
     */
    private void aplicarAnimacionFade(Parent nodo) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), nodo);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }
    
    // ========== MÉTODOS PARA CADA OPCIÓN DEL MENÚ ==========
    
    /**
     * Muestra la vista de Ventas
     */
    @FXML
    private void mostrarVentas() {
        cargarVista("Venta.fxml", "Registro de Ventas", btnVentas);
    }
    
    /**
     * Muestra la vista de Productos
     */
    @FXML
    private void mostrarProductos() {
        cargarVista("Productos.fxml", "Gestión de Productos", btnProductos);
    }
    
    /**
     * Muestra la vista de Clientes
     */
    @FXML
    private void mostrarClientes() {
        cargarVista("clientes.fxml", "Gestión de Clientes", btnClientes);
    }
    
    /**
     * Muestra la vista de Inventario
     */
    @FXML
    private void mostrarInventario() {
        cargarVista("ControlInventario.fxml", "Control de Inventario", btnInventario);
    }
    
    /**
     * Muestra la vista de Facturación
     */
    @FXML
    private void mostrarFacturacion() {
        cargarVista("facturacion.fxml", "Facturación", btnFacturacion);
    }
    
    /**
     * Muestra la vista de Estadísticas
     */
    @FXML
    private void mostrarEstadisticas() {
        cargarVista("inteligenciaDeNegocio.fxml", "Estadísticas y Reportes", btnEstadisticas);
    }
    
    /**
     * Muestra la vista de Usuarios
     */
    @FXML
    private void mostrarUsuarios() {
        cargarVista("usuarios.fxml", "Gestión de Usuarios", btnUsuarios);
    }
    
    /**
     * Cierra la sesión del usuario y regresa a la pantalla de Bienvenida
     */
    @FXML
    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText("¿Está seguro que desea cerrar sesión?");
        alert.setContentText("Será redirigido a la pantalla de bienvenida.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Cerrando sesión...");
                volverABienvenida();
            }
        });
    }
    
    /**
     * Regresa a la pantalla de Bienvenida con animación
     */
    private void volverABienvenida() {
        try {
            // Obtener el nodo raíz actual para animación
            Parent rootActual = btnCerrarSesion.getScene().getRoot();
            
            // Animación de salida
            ScaleTransition scale = new ScaleTransition(Duration.millis(300), rootActual);
            scale.setToX(0.95);
            scale.setToY(0.95);
            
            FadeTransition fade = new FadeTransition(Duration.millis(300), rootActual);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            
            ParallelTransition salida = new ParallelTransition(scale, fade);
            
            salida.setOnFinished(e -> {
                try {
                    // Cargar Bienvenida
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("bienvenida.fxml"));
                    Parent root = loader.load();
                    
                    // Obtener Stage actual
                    Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
                    
                    // Crear nueva Scene con tamaño de bienvenida
                    Scene scene = new Scene(root, 800, 600);
                    
                    // Cambiar a la Scene de bienvenida
                    stage.setScene(scene);
                    stage.setTitle("StockFlow - Sistema de Gestión de Inventario");
                    stage.setResizable(true);
                    stage.setMinWidth(800);
                    stage.setMinHeight(600);
                    
                    // Si estaba maximizado, restaurar tamaño normal
                    if (stage.isMaximized()) {
                        stage.setMaximized(false);
                    }
                    
                    // Centrar en pantalla
                    stage.centerOnScreen();
                    
                    // Configurar F11
                    scene.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.F11) {
                            stage.setFullScreen(!stage.isFullScreen());
                        }
                    });
                    
                    stage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");
                    
                    // Animación de entrada
                    root.setOpacity(0);
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(400), root);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                    
                    System.out.println("✓ Sesión cerrada - Regresando a Bienvenida");
                    
                } catch (IOException ex) {
                    System.err.println("Error al cargar Bienvenida: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            
            salida.play();
            
        } catch (Exception ex) {
            System.err.println("Error al cerrar sesión: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un mensaje de información
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Establece el nombre del usuario en la etiqueta
     * 
     * @param nombreUsuario Nombre del usuario a mostrar
     */
    public void setUsuario(String nombreUsuario) {
        if (lblUsuario != null) {
            lblUsuario.setText("Usuario: " + nombreUsuario);
        }
    }
}