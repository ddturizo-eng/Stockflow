package com.mycompany.stockflow;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controlador principal del Dashboard de StockFlow
 * Gestiona la navegación entre las diferentes vistas del sistema
 * Incluye animaciones y efectos visuales mejorados
 * 
 * @author 
 */
public class DashboardController implements Initializable {
    
    // Referencias a botones del menú
    @FXML
    private Button btnInicio;
    
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
    
    // Referencias a la vista de inicio y sus elementos
    @FXML
    private VBox vistaInicio;
    
    @FXML
    private VBox cardProductos;
    
    @FXML
    private VBox cardVentas;
    
    @FXML
    private VBox cardInventario;
    
    @FXML
    private VBox btnNuevoCliente;
    
    @FXML
    private VBox btnNuevaFactura;
    
    @FXML
    private VBox btnVerReportes;
    
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
        // Inicializar el usuario
        setUsuario("Admin");
        
        // Inicializar la fecha actual
        actualizarFecha();
        
        // Configurar efectos hover para todos los botones del menú
        configurarEfectosHover();
        
        // Aplicar animaciones de entrada a la vista de bienvenida
        aplicarAnimacionesIniciales();
        
        // Configurar efectos hover en las tarjetas
        configurarEfectosTarjetas();
        
        // Configurar eventos click en las tarjetas principales
        configurarClicksTarjetasPrincipales();
        
        // Configurar eventos click en acciones rápidas
        configurarClicksAccionesRapidas();
        
        // Establecer botón Inicio como activo por defecto
        establecerBotonActivo(btnInicio);
        
        System.out.println("Dashboard inicializado correctamente con animaciones");
    }
    
    /**
     * Configura los clicks en las tarjetas principales para navegar
     */
    private void configurarClicksTarjetasPrincipales() {
        if (cardProductos != null) {
            cardProductos.setOnMouseClicked(e -> mostrarProductos());
        }
        
        if (cardVentas != null) {
            cardVentas.setOnMouseClicked(e -> mostrarVentas());
        }
        
        if (cardInventario != null) {
            cardInventario.setOnMouseClicked(e -> mostrarInventario());
        }
    }
    
    /**
     * Configura los clicks en las acciones rápidas
     */
    private void configurarClicksAccionesRapidas() {
        if (btnNuevoCliente != null) {
            btnNuevoCliente.setOnMouseClicked(e -> mostrarClientes());
        }
        
        if (btnNuevaFactura != null) {
            btnNuevaFactura.setOnMouseClicked(e -> mostrarProductos());
        }
        
        if (btnVerReportes != null) {
            btnVerReportes.setOnMouseClicked(e -> mostrarEstadisticas());
        }
    }
    
    /**
     * Muestra la vista de inicio (Dashboard con tarjetas)
     */
    @FXML
    private void mostrarInicio() {
        // Limpiar contenedor
        contenedorPrincipal.getChildren().clear();
        
        // Actualizar título
        animarCambioTexto(lblTituloSeccion, "Dashboard Principal");
        
        // Establecer botón activo
        establecerBotonActivo(btnInicio);
        
        // Verificar que vistaInicio existe
        if (vistaInicio != null) {
            // Agregar vista de inicio
            AnchorPane.setTopAnchor(vistaInicio, 0.0);
            AnchorPane.setBottomAnchor(vistaInicio, 0.0);
            AnchorPane.setLeftAnchor(vistaInicio, 0.0);
            AnchorPane.setRightAnchor(vistaInicio, 0.0);
            
            contenedorPrincipal.getChildren().add(vistaInicio);
            
            // Aplicar animación de entrada
            aplicarAnimacionFadeIn(vistaInicio);
            
            // Re-configurar eventos después de volver a agregar
            configurarClicksTarjetasPrincipales();
            configurarClicksAccionesRapidas();
            configurarEfectosTarjetas();
            
            System.out.println("Vista de inicio cargada");
        } else {
            System.err.println("Error: vistaInicio es null");
        }
    }
    
    /**
     * Aplica animaciones de entrada a los elementos del dashboard
     */
    private void aplicarAnimacionesIniciales() {
        // Obtener todos los VBox hijos del contenedor principal
        if (contenedorPrincipal.getChildren().isEmpty()) {
            return;
        }
        
        Node contenido = contenedorPrincipal.getChildren().get(0);
        if (contenido instanceof VBox) {
            VBox vbox = (VBox) contenido;
            
            // Animar cada hijo del VBox principal
            for (int i = 0; i < vbox.getChildren().size(); i++) {
                Node nodo = vbox.getChildren().get(i);
                
                // Configurar estado inicial
                nodo.setOpacity(0);
                nodo.setTranslateY(30);
                
                // Crear animación de entrada con delay progresivo
                FadeTransition fade = new FadeTransition(Duration.millis(800), nodo);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.setDelay(Duration.millis(i * 150));
                
                TranslateTransition translate = new TranslateTransition(Duration.millis(800), nodo);
                translate.setFromY(30);
                translate.setToY(0);
                translate.setDelay(Duration.millis(i * 150));
                translate.setInterpolator(Interpolator.EASE_OUT);
                
                // Ejecutar animaciones en paralelo
                ParallelTransition parallel = new ParallelTransition(fade, translate);
                parallel.play();
            }
        }
    }
    
    /**
     * Configura efectos hover interactivos en las tarjetas
     */
    private void configurarEfectosTarjetas() {
        if (contenedorPrincipal.getChildren().isEmpty()) {
            return;
        }
        
        Node contenido = contenedorPrincipal.getChildren().get(0);
        if (contenido instanceof VBox) {
            VBox vbox = (VBox) contenido;
            buscarYAnimarTarjetas(vbox);
        }
    }
    
    /**
     * Busca VBox que representen tarjetas y agrega efectos hover
     */
    private void buscarYAnimarTarjetas(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof VBox) {
                VBox vboxCard = (VBox) node;
                String style = vboxCard.getStyle();
                
                // Detectar si es una tarjeta por su estilo
                if (style != null && (style.contains("linear-gradient") || 
                    (style.contains("#FFFFFF") && style.contains("-fx-cursor: hand")))) {
                    
                    configurarEfectoHoverTarjeta(vboxCard);
                }
            }
            
            // Buscar recursivamente en los hijos
            if (node instanceof Parent) {
                buscarYAnimarTarjetas((Parent) node);
            }
        }
    }
    
    /**
     * Configura efecto hover para una tarjeta específica
     */
    private void configurarEfectoHoverTarjeta(VBox tarjeta) {
        // Guardar escala original
        final double scaleOriginal = 1.0;
        final double scaleHover = 1.05;
        
        tarjeta.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), tarjeta);
            scale.setToX(scaleHover);
            scale.setToY(scaleHover);
            scale.setInterpolator(Interpolator.EASE_OUT);
            scale.play();
        });
        
        tarjeta.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), tarjeta);
            scale.setToX(scaleOriginal);
            scale.setToY(scaleOriginal);
            scale.setInterpolator(Interpolator.EASE_OUT);
            scale.play();
        });
        
        // Efecto de click
        tarjeta.setOnMousePressed(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), tarjeta);
            scale.setToX(0.98);
            scale.setToY(0.98);
            scale.play();
        });
        
        tarjeta.setOnMouseReleased(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), tarjeta);
            scale.setToX(scaleHover);
            scale.setToY(scaleHover);
            scale.play();
        });
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
        configurarHoverBoton(btnInicio);
        configurarHoverBoton(btnVentas);
        configurarHoverBoton(btnProductos);
        configurarHoverBoton(btnClientes);
        configurarHoverBoton(btnInventario);
        configurarHoverBoton(btnFacturacion);
        configurarHoverBoton(btnEstadisticas);
        configurarHoverBoton(btnUsuarios);
    }
    
    /**
     * Configura el efecto hover para un botón específico con animación
     */
    private void configurarHoverBoton(Button boton) {
        boton.setOnMouseEntered(e -> {
            if (boton != botonActivo) {
                boton.setStyle(ESTILO_BOTON_HOVER);
                
                // Animación de deslizamiento suave
                TranslateTransition slide = new TranslateTransition(Duration.millis(200), boton);
                slide.setToX(5);
                slide.setInterpolator(Interpolator.EASE_OUT);
                slide.play();
            }
        });
        
        boton.setOnMouseExited(e -> {
            if (boton != botonActivo) {
                boton.setStyle(ESTILO_BOTON_INACTIVO);
                
                // Regresar a posición original
                TranslateTransition slide = new TranslateTransition(Duration.millis(200), boton);
                slide.setToX(0);
                slide.setInterpolator(Interpolator.EASE_OUT);
                slide.play();
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
            botonActivo.setTranslateX(0); // Resetear posición
        }
        
        // Establecer nuevo botón activo
        boton.setStyle(ESTILO_BOTON_ACTIVO);
        boton.setTranslateX(5); // Mantener desplazado
        botonActivo = boton;
    }
    
    /**
     * Carga una vista FXML en el contenedor principal con animación
     * 
     * @param rutaFxml Ruta del archivo FXML a cargar
     * @param titulo Título de la sección
     * @param boton Botón del menú que se activó
     */
    private void cargarVista(String rutaFxml, String titulo, Button boton) {
        try {
            // Actualizar título con animación
            animarCambioTexto(lblTituloSeccion, titulo);
            
            // Establecer botón activo
            establecerBotonActivo(boton);
            
            // Cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent vista = loader.load();
            
            // Animación de salida del contenido actual
            if (!contenedorPrincipal.getChildren().isEmpty()) {
                Node contenidoActual = contenedorPrincipal.getChildren().get(0);
                
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), contenidoActual);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                
                fadeOut.setOnFinished(e -> {
                    // Limpiar contenedor
                    contenedorPrincipal.getChildren().clear();
                    
                    // Configurar anclajes para que ocupe todo el espacio
                    AnchorPane.setTopAnchor(vista, 0.0);
                    AnchorPane.setBottomAnchor(vista, 0.0);
                    AnchorPane.setLeftAnchor(vista, 0.0);
                    AnchorPane.setRightAnchor(vista, 0.0);
                    
                    // Agregar nueva vista al contenedor
                    contenedorPrincipal.getChildren().add(vista);
                    
                    // Aplicar animación de entrada
                    aplicarAnimacionFadeIn(vista);
                });
                
                fadeOut.play();
            } else {
                // No hay contenido previo, cargar directamente
                AnchorPane.setTopAnchor(vista, 0.0);
                AnchorPane.setBottomAnchor(vista, 0.0);
                AnchorPane.setLeftAnchor(vista, 0.0);
                AnchorPane.setRightAnchor(vista, 0.0);
                
                contenedorPrincipal.getChildren().add(vista);
                aplicarAnimacionFadeIn(vista);
            }
            
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
     * Aplica una animación de fade-in a un nodo
     */
    private void aplicarAnimacionFadeIn(Parent nodo) {
        nodo.setOpacity(0);
        
        FadeTransition fade = new FadeTransition(Duration.millis(400), nodo);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setInterpolator(Interpolator.EASE_IN);
        fade.play();
    }
    
    /**
     * Anima el cambio de texto en un Label
     */
    private void animarCambioTexto(Label label, String nuevoTexto) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), label);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(e -> {
            label.setText(nuevoTexto);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), label);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        
        fadeOut.play();
    }
    
    // ========== MÉTODOS PARA CADA OPCIÓN DEL MENÚ ==========
    
    @FXML
    private void mostrarVentas() {
        cargarVista("Venta.fxml", "Registro de Ventas", btnVentas);
    }
    
    @FXML
    private void mostrarProductos() {
        cargarVista("Productos.fxml", "Gestión de Productos", btnProductos);
    }
    
    @FXML
    private void mostrarClientes() {
        cargarVista("clientes.fxml", "Gestión de Clientes", btnClientes);
    }
    
    @FXML
    private void mostrarInventario() {
        cargarVista("ControlInventario.fxml", "Control de Inventario", btnInventario);
    }
    
    @FXML
    private void mostrarFacturacion() {
        cargarVista("facturacion.fxml", "Facturación", btnFacturacion);
    }
    
    @FXML
    private void mostrarEstadisticas() {
        cargarVista("inteligenciaDeNegocio.fxml", "", btnEstadisticas);
    }
    
    @FXML
    private void mostrarUsuarios() {
        cargarVista("usuarios.fxml", "Gestión de Usuarios", btnUsuarios);
    }
    
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
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("bienvenida.fxml"));
                    Parent root = loader.load();
                    
                    Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
                    Scene scene = new Scene(root, 800, 600);
                    
                    stage.setScene(scene);
                    stage.setTitle("StockFlow - Sistema de Gestión de Inventario");
                    stage.setResizable(true);
                    stage.setMinWidth(800);
                    stage.setMinHeight(600);
                    
                    if (stage.isMaximized()) {
                        stage.setMaximized(false);
                    }
                    
                    stage.centerOnScreen();
                    
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
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public void setUsuario(String nombreUsuario) {
        if (lblUsuario != null) {
            lblUsuario.setText("Usuario: " + nombreUsuario);
        }
    }
}