package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.AutenticacionServicio;
import com.mycompany.stockflow.Modelo.Usuario;
import javafx.animation.*;
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

public class LoginController implements Initializable {
    
    @FXML
    private TextField txtUsuario;
    
    @FXML
    private PasswordField txtPassword;
    
    @FXML
    private TextField txtPasswordVisible;
    
    @FXML
    private Button btnLogin;
    
    @FXML
    private Button btnVolver;
    
    @FXML
    private Button btnTogglePassword;
    
    @FXML
    private Label lblEyeIcon;
    
    @FXML
    private Label lblOlvidePassword;
    
    @FXML
    private Label lblError;
    
    @FXML
    private StackPane rootPane;
    
    private AutenticacionServicio autenticacionServicio;
    private boolean passwordVisible = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        autenticacionServicio = new AutenticacionServicio();
        
        lblError.setVisible(false);
        
        configurarListenersInput();
        sincronizarCamposPassword();
        configurarHoverButtons();
        
        // Permitir login con Enter en ambos campos
        txtPassword.setOnAction(this::ini_sesion);
        txtPasswordVisible.setOnAction(this::ini_sesion);
        
        // Animación de entrada suave
        if (rootPane != null) {
            rootPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), rootPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
        
        System.out.println("Login inicializado correctamente");
    }
    
    private void sincronizarCamposPassword() {
        // Sincronizar texto entre PasswordField y TextField
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!passwordVisible) {
                txtPasswordVisible.setText(newVal);
            }
        });
        
        txtPasswordVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (passwordVisible) {
                txtPassword.setText(newVal);
            }
        });
    }
    
    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        
        if (passwordVisible) {
            // Mostrar contraseña
            txtPasswordVisible.setText(txtPassword.getText());
            txtPassword.setVisible(false);
            txtPasswordVisible.setVisible(true);
            lblEyeIcon.setText("👁‍🗨");
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            // Ocultar contraseña
            txtPassword.setText(txtPasswordVisible.getText());
            txtPasswordVisible.setVisible(false);
            txtPassword.setVisible(true);
            lblEyeIcon.setText("👁");
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
    }
    
    private String getPasswordText() {
        return passwordVisible ? txtPasswordVisible.getText() : txtPassword.getText();
    }
    
    private void configurarListenersInput() {
        // Limpiar error cuando el usuario escriba
        txtUsuario.textProperty().addListener((obs, oldVal, newVal) -> {
            lblError.setVisible(false);
            txtUsuario.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 8;");
        });
        
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            lblError.setVisible(false);
            txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 8 40 8 8;");
            txtPasswordVisible.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 8 40 8 8;");
        });
        
        txtPasswordVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            lblError.setVisible(false);
            txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 8 40 8 8;");
            txtPasswordVisible.setStyle("-fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 8 40 8 8;");
        });
    }
    
    private void configurarHoverButtons() {
        // Hover en botón Volver
        btnVolver.setOnMouseEntered(e -> {
            btnVolver.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                "-fx-background-radius: 25;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(33, 150, 243, 0.4), 12, 0.4, 0, 3);"
            );
        });
        
        btnVolver.setOnMouseExited(e -> {
            btnVolver.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                "-fx-background-radius: 25;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.3, 0, 2);"
            );
        });
        
        // Hover en botón ojo
        btnTogglePassword.setOnMouseEntered(e -> {
            lblEyeIcon.setStyle("-fx-font-size: 18; -fx-text-fill: #2196F3;");
        });
        
        btnTogglePassword.setOnMouseExited(e -> {
            lblEyeIcon.setStyle("-fx-font-size: 18; -fx-text-fill: #666666;");
        });
    }
    
    @FXML
    private void ini_sesion(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String password = getPasswordText();
        
        // Validar campos vacíos
        if (usuario.isEmpty()) {
            mostrarError("El campo usuario es obligatorio");
            txtUsuario.setStyle("-fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8;");
            return;
        }
        
        if (password.isEmpty()) {
            mostrarError("El campo contraseña es obligatorio");
            txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8 40 8 8;");
            txtPasswordVisible.setStyle("-fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8 40 8 8;");
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
                txtPassword.setStyle("-fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8 40 8 8;");
                txtPasswordVisible.setStyle("-fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8 40 8 8;");
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
        // Animación de salida suave
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
            
            // Cambiar la raíz de la Scene actual sin redimensionar
            currentScene.setRoot(root);
            stage.setTitle("StockFlow - Dashboard");
            
            // Mantener F11 para pantalla completa
            currentScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.F11) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });
            
            stage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");
            
            // Animación de entrada del Dashboard
            root.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
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
    
    @FXML
    private void volverABienvenida() {
        System.out.println("Volviendo a Bienvenida...");
        
        if (rootPane != null) {
            animarSalida(() -> cambiarABienvenida());
        } else {
            cambiarABienvenida();
        }
    }
    
    private void cambiarABienvenida() {
        try {
            // Cargar Bienvenida
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Bienvenida.fxml"));
            Parent root = loader.load();
            
            // Obtener Stage actual
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 600);
            
            stage.setScene(scene);
            stage.setTitle("StockFlow - Bienvenida");
            stage.setResizable(true);
            stage.setMaximized(true);
            
            // Configurar F11
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.F11) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });
            
            stage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");
            
            // Animación de entrada
            root.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            
            System.out.println("✓ Volviendo a Bienvenida");
            
        } catch (IOException e) {
            System.err.println("Error al cargar Bienvenida: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al volver a bienvenida");
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
        txtPasswordVisible.setTranslateX(distancia);
        
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(50), e -> {
                txtUsuario.setTranslateX(-distancia);
                txtPassword.setTranslateX(-distancia);
                txtPasswordVisible.setTranslateX(-distancia);
            }),
            new KeyFrame(Duration.millis(100), e -> {
                txtUsuario.setTranslateX(distancia);
                txtPassword.setTranslateX(distancia);
                txtPasswordVisible.setTranslateX(distancia);
            }),
            new KeyFrame(Duration.millis(150), e -> {
                txtUsuario.setTranslateX(-distancia/2);
                txtPassword.setTranslateX(-distancia/2);
                txtPasswordVisible.setTranslateX(-distancia/2);
            }),
            new KeyFrame(Duration.millis(200), e -> {
                txtUsuario.setTranslateX(0);
                txtPassword.setTranslateX(0);
                txtPasswordVisible.setTranslateX(0);
            })
        );
        timeline.play();
    }
    
    private void animarSalida(Runnable onFinished) {
        // Solo fade out suave, sin zoom
        FadeTransition fade = new FadeTransition(Duration.millis(250), rootPane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> onFinished.run());
        fade.play();
    }
}