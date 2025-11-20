package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.AutenticacionServicio;
import com.mycompany.stockflow.Logica.UsuarioServicio;
import com.mycompany.stockflow.Modelo.Usuario;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la pantalla de inicio de sesión de StockFlow.
 * Gestiona la autenticación de usuarios, validación de credenciales,
 * y la navegación entre pantallas. También incluye funcionalidades de
 * recuperación de contraseña y configuración inicial del sistema.
 * 
 * @author Equipo StockFlow/StockFlow Team
 * @version 1.0
 * @since 2025
 */
public class LoginController implements Initializable {
    
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button btnLogin;
    @FXML private Button btnVolver;
    @FXML private Button btnTogglePassword;
    @FXML private Label lblEyeIcon;
    @FXML private Label lblOlvidePassword;
    @FXML private Label lblError;
    @FXML private StackPane rootPane;
    
    private AutenticacionServicio autenticacionServicio;
    private UsuarioServicio usuarioServicio;
    private boolean passwordVisible = false;
    
    /**
     * Inicializa el controlador de login.
     * Configura los servicios, listeners, validaciones y efectos visuales.
     * Verifica si es la primera ejecución del sistema para mostrar el wizard de configuración.
     * 
     * @param url la ubicación utilizada para resolver rutas relativas del objeto raíz
     * @param rb los recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        autenticacionServicio = new AutenticacionServicio();
        usuarioServicio = new UsuarioServicio();
        
        lblError.setVisible(false);
        
        configurarListenersInput();
        sincronizarCamposPassword();
        configurarHoverButtons();
        
        // Configurar label de recuperación de contraseña
        if (lblOlvidePassword != null) {
            lblOlvidePassword.setStyle("-fx-cursor: hand; -fx-underline: true; -fx-text-fill: #2196f3;");
            lblOlvidePassword.setOnMouseClicked(event -> manejarOlvidePassword());
            
            // Agregar efecto hover
            lblOlvidePassword.setOnMouseEntered(event -> 
                lblOlvidePassword.setStyle("-fx-cursor: hand; -fx-underline: true; -fx-text-fill: #1565c0; -fx-font-weight: bold;")
            );
            lblOlvidePassword.setOnMouseExited(event -> 
                lblOlvidePassword.setStyle("-fx-cursor: hand; -fx-underline: true; -fx-text-fill: #2196f3;")
            );
        }
        
        // Permitir login con Enter
        txtPassword.setOnAction(this::ini_sesion);
        txtPasswordVisible.setOnAction(this::ini_sesion);
        
        // Animación de entrada
        if (rootPane != null) {
            rootPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), rootPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
        
        // Verificar si el sistema está vacío
        verificarSistemaVacio();
        
        System.out.println("LoginController inicializado correctamente");
    }
    
    /**
     * Verifica si el sistema está vacío y muestra el wizard de setup.
     * Se ejecuta en el primer inicio del sistema cuando no existen usuarios.
     */
    private void verificarSistemaVacio() {
        if (usuarioServicio.sistemaVacio()) {
            System.out.println("Sistema vacío - Mostrando wizard de configuración inicial");
            
            // Mostrar alerta informativa
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bienvenido a StockFlow");
            alert.setHeaderText("Configuración Inicial");
            alert.setContentText(
                "Parece que es la primera vez que usas StockFlow.\n\n" +
                "A continuación crearemos el primer usuario administrador."
            );
            alert.showAndWait();
            
            // Abrir wizard de setup
            mostrarWizardSetup();
        }
    }
    
    /**
     * Muestra el wizard de configuración inicial del sistema.
     * Permite crear el primer usuario administrador.
     */
    private void mostrarWizardSetup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SetupWizard.fxml"));
            Parent root = loader.load();
            
            Stage setupStage = new Stage();
            setupStage.setTitle("Configuración Inicial - StockFlow");
            setupStage.setScene(new Scene(root));
            setupStage.initModality(Modality.APPLICATION_MODAL);
            setupStage.initStyle(StageStyle.DECORATED);
            setupStage.setResizable(false);
            
            // Obtener el controller
            SetupWizardController controller = loader.getController();
            
            // Mostrar y esperar
            setupStage.showAndWait();
            
            // Verificar si se completó el setup
            if (controller.isSetupCompletado()) {
                mostrarExito("Setup completado", "Ahora puedes iniciar sesión con tus credenciales.");
            } else {
                // Si no completó, verificar nuevamente al volver
                if (usuarioServicio.sistemaVacio()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Configuración Requerida");
                    alert.setHeaderText("No se puede usar el sistema");
                    alert.setContentText("Debes crear al menos un administrador para usar StockFlow.");
                    alert.showAndWait();
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error al cargar SetupWizard: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar el asistente de configuración");
        }
    }
    
    /**
     * Sincroniza los campos de contraseña visible e invisible.
     * Asegura que ambos campos mantengan el mismo valor cuando se editan.
     */
    private void sincronizarCamposPassword() {
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
    
    /**
     * Alterna la visibilidad de la contraseña entre texto plano y oculto.
     * Cambia el icono del ojo y muestra/oculta el campo correspondiente.
     */
    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        
        if (passwordVisible) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPassword.setVisible(false);
            txtPasswordVisible.setVisible(true);
            lblEyeIcon.setText("👁‍🗨");
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPasswordVisible.setVisible(false);
            txtPassword.setVisible(true);
            lblEyeIcon.setText("👁");
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
    }
    
    /**
     * Obtiene el texto de la contraseña del campo visible actualmente.
     * 
     * @return el texto de la contraseña ingresada
     */
    private String getPasswordText() {
        return passwordVisible ? txtPasswordVisible.getText() : txtPassword.getText();
    }
    
    /**
     * Configura los listeners para los campos de entrada.
     * Limpia los mensajes de error y restablece los estilos al escribir.
     */
    private void configurarListenersInput() {
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
    
    /**
     * Configura los efectos hover para los botones de la interfaz.
     * Añade transiciones visuales al pasar el mouse sobre los botones.
     */
    private void configurarHoverButtons() {
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
        
        btnTogglePassword.setOnMouseEntered(e -> {
            lblEyeIcon.setStyle("-fx-font-size: 18; -fx-text-fill: #2196F3;");
        });
        
        btnTogglePassword.setOnMouseExited(e -> {
            lblEyeIcon.setStyle("-fx-font-size: 18; -fx-text-fill: #666666;");
        });
    }
    
    /**
     * Maneja el proceso de inicio de sesión.
     * Valida las credenciales, autentica al usuario y carga el dashboard.
     * 
     * @param event el evento de acción generado por el botón de login
     */
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
                System.out.println("Login exitoso para: " + usuarioAutenticado.getNombre());
                cargarDashboard();
            } else {
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
    
    /**
     * Carga el dashboard principal con animación de transición.
     */
    private void cargarDashboard() {
        if (rootPane != null) {
            animarSalida(() -> cambiarADashboard());
        } else {
            cambiarADashboard();
        }
    }
    
    /**
     * Cambia la escena actual al dashboard principal.
     * Configura las opciones de pantalla completa y animaciones de entrada.
     */
    private void cambiarADashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            
            Scene currentScene = txtUsuario.getScene();
            Stage stage = (Stage) currentScene.getWindow();
            
            currentScene.setRoot(root);
            stage.setTitle("StockFlow - Dashboard");
            
            currentScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.F11) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });
            
            stage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");
            
            root.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            
            System.out.println("Dashboard cargado correctamente");
            
        } catch (IOException e) {
            System.err.println("Error al cargar Dashboard: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar el dashboard");
            btnLogin.setDisable(false);
            btnLogin.setText("INICIAR SESIÓN");
        }
    }
    
    /**
     * Maneja el evento de volver a la pantalla de bienvenida.
     */
    @FXML
    private void volverABienvenida() {
        System.out.println("Volviendo a Bienvenida...");
        
        if (rootPane != null) {
            animarSalida(() -> cambiarABienvenida());
        } else {
            cambiarABienvenida();
        }
    }
    
    /**
     * Cambia la escena actual a la pantalla de bienvenida.
     */
    private void cambiarABienvenida() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Bienvenida.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 600);
            
            stage.setScene(scene);
            stage.setTitle("StockFlow - Bienvenida");
            stage.setResizable(true);
            stage.setMaximized(true);
            
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.F11) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });
            
            stage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");
            
            root.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            
            System.out.println("Volviendo a Bienvenida");
            
        } catch (IOException e) {
            System.err.println("Error al cargar Bienvenida: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al volver a bienvenida");
        }
    }
    
    /**
     * Muestra un mensaje de error en la etiqueta de errores con animación.
     * 
     * @param mensaje el mensaje de error a mostrar
     */
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        
        lblError.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(200), lblError);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
    
    /**
     * Muestra un diálogo de éxito con el título y mensaje especificados.
     * 
     * @param titulo el título del diálogo
     * @param mensaje el contenido del mensaje
     */
    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Anima los campos de entrada con un efecto de sacudida cuando hay error.
     */
    private void animarError() {
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
    
    /**
     * Anima la salida del panel con efecto fade out.
     * 
     * @param onFinished acción a ejecutar al finalizar la animación
     */
    private void animarSalida(Runnable onFinished) {
        FadeTransition fade = new FadeTransition(Duration.millis(250), rootPane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> onFinished.run());
        fade.play();
    }
    
    /**
     * Maneja el evento cuando el usuario hace clic en "Olvidé mi contraseña".
     */
    @FXML
    private void manejarOlvidePassword() {
        System.out.println("Usuario solicitó recuperación de contraseña");
        mostrarDialogoOlvidePassword();
    }

    /**
     * Muestra el diálogo de recuperación de contraseña.
     */
    private void mostrarDialogoOlvidePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DialogoOlvidePassword.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Recuperar Contraseña");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            
            stage.showAndWait();
            
            System.out.println("Diálogo de recuperación cerrado");
            
        } catch (IOException e) {
            System.err.println("Error al abrir diálogo de recuperación: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al abrir el diálogo de recuperación");
        }
    }
}