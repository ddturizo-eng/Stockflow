/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.stockflow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Clase principal de la aplicación StockFlow.
 * <p>
 * Esta clase extiende {@link Application} de JavaFX y es el punto de entrada
 * del sistema de gestión de inventario. Inicializa la interfaz gráfica con
 * la pantalla de bienvenida y configura los atajos de teclado globales.
 * </p>
 * 
 * <h2>Características principales:</h2>
 * <ul>
 *   <li>Inicialización de la ventana principal con dimensiones 1100x700</li>
 *   <li>Carga de la pantalla de bienvenida como vista inicial</li>
 *   <li>Soporte para pantalla completa mediante tecla F11</li>
 *   <li>Navegación entre vistas mediante carga dinámica de FXML</li>
 * </ul>
 * 
 * <h2>Flujo de inicio:</h2>
 * <ol>
 *   <li>Usuario ejecuta la aplicación (método {@link #main(String[])})</li>
 *   <li>Se inicializa el {@link Stage} principal con bienvenida.fxml</li>
 *   <li>Usuario navega a Login desde la pantalla de bienvenida</li>
 *   <li>Tras autenticación exitosa, se carga el Dashboard</li>
 * </ol>
 * 
 * <h2>Estructura de navegación:</h2>
 * <pre>
 * App.java (Bienvenida)
 *    ↓
 * Login.fxml → Dashboard.fxml
 *    ↓
 * Módulos del sistema (Productos, Ventas, Usuarios, etc.)
 * </pre>
 * 
 * @author StockFlow Team
 * @version 3.0
 * @since 1.0
 * 
 * @see BienvenidaController
 * @see LoginController
 * @see DashboardController
 */
public class App extends Application {
    
    /**
     * Escena principal de la aplicación.
     * Contiene la vista activa que se muestra al usuario.
     */
    private static Scene scene;
    
    /**
     * Stage (ventana) principal de la aplicación.
     * Referencia global para acceder a la ventana desde cualquier controlador.
     */
    private static Stage primaryStage;
    
    /**
     * Método de inicio de la aplicación JavaFX.
     * <p>
     * Se ejecuta automáticamente cuando se lanza la aplicación.
     * Configura la ventana principal, carga la vista inicial (Bienvenida)
     * y establece las propiedades de visualización.
     * </p>
     * 
     * @param stage El stage principal proporcionado por JavaFX
     * @throws IOException Si hay error al cargar el archivo FXML de bienvenida
     * 
     * @see #loadFXML(String)
     * @see #configurarAtajosTeclado()
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        // Cargar pantalla de bienvenida como vista inicial
        scene = new Scene(loadFXML("bienvenida"), 1100, 750);
        
        // Configuración de la ventana principal
        primaryStage.setTitle("StockFlow - Sistema de Gestión de Inventario");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        
        // Configurar atajos de teclado (F11 para pantalla completa)
        configurarAtajosTeclado();
        
        // Centrar ventana en la pantalla
        primaryStage.centerOnScreen();
        
        // Mostrar ventana
        primaryStage.show();
    }
    
    /**
     * Configura los atajos de teclado globales de la aplicación.
     * <p>
     * Actualmente configura:
     * <ul>
     *   <li><b>F11:</b> Alterna entre modo pantalla completa y modo ventana</li>
     *   <li><b>ESC:</b> Sale del modo pantalla completa (comportamiento predeterminado)</li>
     * </ul>
     * </p>
     * 
     * <p><b>Nota:</b> Estos atajos funcionan en cualquier vista de la aplicación.</p>
     */
    private void configurarAtajosTeclado() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
        
        primaryStage.setFullScreenExitHint(
            "Presiona F11 o ESC para salir de pantalla completa"
        );
    }
    
    /**
     * Cambia la vista actual de la aplicación.
     * <p>
     * Método legacy mantenido por compatibilidad con versiones anteriores.
     * En el código nuevo, se recomienda usar navegación directa desde los controladores.
     * </p>
     * 
     * @param fxml Nombre del archivo FXML (sin extensión) a cargar
     * @throws IOException Si el archivo FXML no existe o hay error al cargarlo
     * 
     * @deprecated Usar navegación directa en controladores mediante FXMLLoader
     * @see FXMLLoader
     */
    @Deprecated
    public static void setRoot(String fxml) throws IOException {
        System.out.println(
            "⚠️ Advertencia: setRoot() está deprecado. " +
            "Use la navegación directa del controlador."
        );
        scene.setRoot(loadFXML(fxml));
        configurarAtajosTecladoStatic();
    }
    
    /**
     * Versión estática de configuración de atajos de teclado.
     * <p>
     * Se usa cuando se cambia la raíz de la escena mediante {@link #setRoot(String)}.
     * Reconfigura los listeners de teclado que se pierden al cambiar la raíz.
     * </p>
     * 
     * @see #configurarAtajosTeclado()
     */
    private static void configurarAtajosTecladoStatic() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
    }
    
    /**
     * Carga un archivo FXML y retorna su raíz como Parent.
     * <p>
     * Este método utiliza {@link FXMLLoader} para cargar archivos FXML
     * desde el paquete de resources de la aplicación.
     * </p>
     * 
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * Parent root = loadFXML("Dashboard");
     * // Carga: src/main/resources/com/mycompany/stockflow/Dashboard.fxml
     * </pre>
     * 
     * @param fxml Nombre del archivo FXML sin extensión (ej: "Login", "Dashboard")
     * @return Parent raíz del archivo FXML cargado
     * @throws IOException Si el archivo no existe o hay error en la estructura FXML
     * 
     * @see FXMLLoader#load()
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            App.class.getResource(fxml + ".fxml")
        );
        return fxmlLoader.load();
    }
    
    /**
     * Obtiene el Stage principal de la aplicación.
     * <p>
     * Útil para acceder a la ventana principal desde controladores,
     * por ejemplo para cerrar la aplicación o cambiar propiedades de la ventana.
     * </p>
     * 
     * @return El {@link Stage} principal de la aplicación
     * 
     * @see Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Obtiene la Scene principal de la aplicación.
     * <p>
     * Permite acceder a la escena actual desde cualquier parte del código,
     * útil para cambiar estilos CSS globales o manipular la raíz.
     * </p>
     * 
     * @return La {@link Scene} principal actual
     * 
     * @see Scene
     */
    public static Scene getScene() {
        return scene;
    }
    
    /**
     * Punto de entrada principal de la aplicación.
     * <p>
     * Este método es invocado por la JVM al ejecutar la aplicación.
     * Delega la inicialización a JavaFX mediante {@link Application#launch()}.
     * </p>
     * 
     * @param args Argumentos de línea de comandos (no utilizados actualmente)
     * 
     * @see Application#launch(String...)
     */
    public static void main(String[] args) {
        launch();
    }
}