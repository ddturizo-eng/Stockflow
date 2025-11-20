package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.UsuarioServicio;
import com.mycompany.stockflow.Modelo.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller para el wizard de configuración inicial
 * Permite crear el primer usuario administrador del sistema
 */
public class SetupWizardController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtEmail;
    @FXML private Button btnCrear;
    @FXML private Button btnCancelar;
    @FXML private Label lblTitulo;
    @FXML private Label lblDescripcion;

    private UsuarioServicio usuarioServicio;
    private boolean setupCompletado = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioServicio = new UsuarioServicio();
        configurarValidaciones();
        
        System.out.println("✓ SetupWizardController inicializado");
    }

    /**
     * Configura validaciones en tiempo real
     */
    private void configurarValidaciones() {
        // Validar que las contraseñas coincidan en tiempo real
        txtConfirmarPassword.textProperty().addListener((obs, old, nuevo) -> {
            validarPasswords();
        });

        txtPassword.textProperty().addListener((obs, old, nuevo) -> {
            validarPasswords();
        });
        
        // Limpiar estilos al escribir
        txtUsername.textProperty().addListener((obs, old, nuevo) -> {
            txtUsername.setStyle("-fx-border-color: #bdc3c7;");
        });
        
        txtNombreCompleto.textProperty().addListener((obs, old, nuevo) -> {
            txtNombreCompleto.setStyle("-fx-border-color: #bdc3c7;");
        });
        
        txtEmail.textProperty().addListener((obs, old, nuevo) -> {
            txtEmail.setStyle("-fx-border-color: #bdc3c7;");
        });
    }

    /**
     * Valida que las contraseñas coincidan
     */
    private void validarPasswords() {
        String pass1 = txtPassword.getText();
        String pass2 = txtConfirmarPassword.getText();

        if (!pass2.isEmpty() && !pass1.equals(pass2)) {
            txtConfirmarPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
        } else {
            txtConfirmarPassword.setStyle("-fx-border-color: #27ae60; -fx-border-width: 2;");
        }
    }

    /**
     * Maneja el evento de crear el primer admin
     */
    @FXML
    private void handleCrear() {
        if (!validarCampos()) {
            return;
        }

        try {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText();
            String nombreCompleto = txtNombreCompleto.getText().trim();
            String email = txtEmail.getText().trim();

            System.out.println("📝 Creando primer administrador...");
            System.out.println("   Username: " + username);
            System.out.println("   Nombre: " + nombreCompleto);

            // Crear primer admin
            Usuario admin = usuarioServicio.crearPrimerAdmin(
                username, password, nombreCompleto, email
            );

            setupCompletado = true;

            // Alerta de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("¡Bienvenido a StockFlow!");
            alert.setHeaderText("✅ Administrador creado exitosamente");
            alert.setContentText(
                "Usuario: " + admin.getUsername() + "\n" +
                "Nombre: " + admin.getNombreCompleto() + "\n" +
                "Rol: " + admin.getRol().getNombre() + "\n\n" +
                "Ahora puedes iniciar sesión con tus credenciales."
            );
            alert.showAndWait();

            System.out.println("✓ Primer administrador creado exitosamente");
            cerrarVentana();

        } catch (IllegalStateException e) {
            System.err.println("❌ Error: " + e.getMessage());
            mostrarError(
                "Error de Sistema",
                "Ya existen usuarios en el sistema",
                "Este asistente solo funciona en la primera ejecución."
            );
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error de validación: " + e.getMessage());
            mostrarAdvertencia(
                "Datos Inválidos",
                "Por favor revisa los datos ingresados",
                e.getMessage()
            );
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            mostrarError(
                "Error",
                "No se pudo crear el administrador",
                e.getMessage()
            );
        }
    }

    /**
     * Valida todos los campos del formulario
     */
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        boolean valido = true;

        // Username
        if (txtUsername.getText().trim().isEmpty()) {
            errores.append("• El nombre de usuario es obligatorio\n");
            txtUsername.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        } else if (txtUsername.getText().trim().length() < 3) {
            errores.append("• El nombre de usuario debe tener al menos 3 caracteres\n");
            txtUsername.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        }

        // Password
        if (txtPassword.getText().isEmpty()) {
            errores.append("• La contraseña es obligatoria\n");
            txtPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        } else if (txtPassword.getText().length() < 4) {
            errores.append("• La contraseña debe tener al menos 4 caracteres\n");
            txtPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        }

        // Confirmar password
        if (!txtPassword.getText().equals(txtConfirmarPassword.getText())) {
            errores.append("• Las contraseñas no coinciden\n");
            txtConfirmarPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        }

        // Nombre completo
        if (txtNombreCompleto.getText().trim().isEmpty()) {
            errores.append("• El nombre completo es obligatorio\n");
            txtNombreCompleto.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        }

        // Email (opcional pero debe ser válido)
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !esEmailValido(email)) {
            errores.append("• El formato del email es inválido\n");
            txtEmail.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        }

        if (!valido) {
            mostrarAdvertencia(
                "Campos Inválidos",
                "Por favor completa correctamente el formulario",
                errores.toString()
            );
        }

        return valido;
    }

    /**
     * Validación simple de email
     */
    private boolean esEmailValido(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Maneja el evento de cancelar
     */
    @FXML
    private void handleCancelar() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Estás seguro de cancelar?");
        confirmacion.setContentText(
            "Si cancelas, no podrás usar el sistema hasta crear un administrador.\n\n" +
            "¿Deseas continuar?"
        );

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("⚠️ Setup cancelado por el usuario");
                cerrarVentana();
            }
        });
    }

    /**
     * Cierra la ventana del wizard
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCrear.getScene().getWindow();
        stage.close();
    }

    /**
     * Verifica si el setup fue completado
     */
    public boolean isSetupCompletado() {
        return setupCompletado;
    }

    // ========== MÉTODOS DE ALERTAS ==========

    private void mostrarError(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarExito(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}