package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.AutenticacionServicio;
import com.mycompany.stockflow.Modelo.Usuario;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Label lblOlvidePassword;
    @FXML
    private Label lblError;
    @FXML
    private StackPane rootPane;
    
    private AutenticacionServicio autenticacionServicio;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar el servicio de autenticación
        autenticacionServicio = new AutenticacionServicio();
        
        // Ocultar mensaje de error al inicio
        lblError.setVisible(false);
        
        // Limpiar error cuando el usuario escriba
        txtUsuario.textProperty().addListener((obs, oldVal, newVal) -> {
            lblError.setVisible(false);
            txtUsuario.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 8;");
        });
        
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            lblError.setVisible(false);
            txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 8;");
        });
        
        // Permitir login con Enter en el campo contraseña
        txtPassword.setOnAction(this::ini_sesion);
        
        // Animación de entrada suave
        if (rootPane != null) {
            rootPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), rootPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
    }    
    
    @FXML
    private void ini_sesion(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();
        
        // Validar campos vacíos
        if (usuario.isEmpty()) {
            mostrarError("El campo usuario es obligatorio");
            txtUsuario.setStyle("-fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8;");
            return;
        }
        
        if (password.isEmpty()) {
            mostrarError("El campo contraseña es obligatorio");
            txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8;");
            return;
        }
        
        // Deshabilitar botón mientras se procesa
        btnLogin.setDisable(true);
        btnLogin.setText("Iniciando sesión...");
        
        try {
            // Intentar autenticar
            Usuario usuarioAutenticado = autenticacionServicio.autenticar(usuario, password);
            
            if (usuarioAutenticado != null) {
                // Login exitoso
                System.out.println("✓ Login exitoso para: " + usuarioAutenticado.getNombre());
                cargarDashboard();
                
            } else {
                // Credenciales incorrectas
                mostrarError("Usuario o contraseña incorrectos");
                txtUsuario.setStyle("-fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8;");
                txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8;");
                btnLogin.setDisable(false);
                btnLogin.setText("INICIAR SESIÓN");
                animarError();
            }
            
        } catch (Exception e) {
            mostrarError("Error en el sistema: " + e.getMessage());
            System.err.println("Error durante autenticación:");
            e.printStackTrace();
            btnLogin.setDisable(false);
            btnLogin.setText("INICIAR SESIÓN");
        }
    }
    
    private void cargarDashboard() {
        // Animación de salida
        if (rootPane != null) {
            animarSalida(() -> cambiarADashboard());
        } else {
            cambiarADashboard();
        }
    }
    
    private void cambiarADashboard() {
        try {
            // Cargar Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            
            // Obtener la Scene y Stage actuales
            Scene currentScene = txtUsuario.getScene();
            Stage stage = (Stage) currentScene.getWindow();
            
            // Cambiar la raíz de la Scene actual
            currentScene.setRoot(root);
            stage.setTitle("StockFlow - Dashboard");
            
            // Mantener F11 para pantalla completa
            currentScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.F11) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });
            
            // Animación de entrada del Dashboard
            root.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            
            System.out.println("✓ Dashboard cargado correctamente");
            
        } catch (IOException e) {
            System.err.println("Error al cargar Dashboard: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar el dashboard");
            btnLogin.setDisable(false);
            btnLogin.setText("INICIAR SESIÓN");
        }
    }
    
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        
        // Animación para el mensaje de error
        lblError.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(200), lblError);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
    
    private void animarError() {
        // Animación de "shake" para los campos con error
        double distancia = 10;
        
        txtUsuario.setTranslateX(distancia);
        txtPassword.setTranslateX(distancia);
        
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.millis(50), e -> {
                txtUsuario.setTranslateX(-distancia);
                txtPassword.setTranslateX(-distancia);
            }),
            new javafx.animation.KeyFrame(Duration.millis(100), e -> {
                txtUsuario.setTranslateX(distancia);
                txtPassword.setTranslateX(distancia);
            }),
            new javafx.animation.KeyFrame(Duration.millis(150), e -> {
                txtUsuario.setTranslateX(-distancia/2);
                txtPassword.setTranslateX(-distancia/2);
            }),
            new javafx.animation.KeyFrame(Duration.millis(200), e -> {
                txtUsuario.setTranslateX(0);
                txtPassword.setTranslateX(0);
            })
        );
        timeline.play();
    }
    
    private void animarSalida(Runnable onFinished) {
        // Zoom y fade out
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), rootPane);
        scale.setToX(0.95);
        scale.setToY(0.95);
        
        FadeTransition fade = new FadeTransition(Duration.millis(300), rootPane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        
        ParallelTransition parallel = new ParallelTransition(scale, fade);
        parallel.setOnFinished(e -> onFinished.run());
        parallel.play();
    }
}