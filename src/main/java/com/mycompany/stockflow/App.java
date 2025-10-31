package com.mycompany.stockflow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App con soporte para pantalla completa y gestión de escenas
 */
public class App extends Application {
    
    private static Scene scene;
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        // OPCIÓN 1: Iniciar con Bienvenida (800x600, centrada, no maximizada)
        scene = new Scene(loadFXML("bienvenida"), 800, 600);
        primaryStage.setTitle("StockFlow - Sistema de Gestión de Inventario");
        primaryStage.setResizable(true); // Permitir F11, pero no maximizar automáticamente
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // OPCIÓN 2: Iniciar directo con Login (comenta lo de arriba y descomenta esto)
        /*
        scene = new Scene(loadFXML("Login"), 1000, 600);
        primaryStage.setTitle("StockFlow - Sistema de Gestión");
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        */
        
        primaryStage.setScene(scene);
        
        // Configurar F11 para pantalla completa
        configurarAtajosTeclado();
        
        // Centrar en pantalla (NO maximizar)
        primaryStage.centerOnScreen();
        
        primaryStage.show();
    }
    
    private void configurarAtajosTeclado() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
        
        primaryStage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");
    }
    
    /**
     * Método para cambiar solo el contenido (usado por versiones antiguas del código)
     * Mantiene compatibilidad pero ya no es necesario
     */
    public static void setRoot(String fxml) throws IOException {
        System.out.println("️ Advertencia: setRoot() está deprecado. Use la navegación directa del controlador.");
        scene.setRoot(loadFXML(fxml));
        configurarAtajosTecladoStatic();
    }
    
    private static void configurarAtajosTecladoStatic() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
    }
    
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static Scene getScene() {
        return scene;
    }
    
    public static void main(String[] args) {
        launch();
    }
}