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
 * Controlador para el asistente de configuración inicial del sistema.
 * 
 * Este wizard se ejecuta únicamente en la primera ejecución de StockFlow
 * para crear el primer usuario administrador del sistema. Proporciona validación
 * en tiempo real y confirmación de contraseña.
 * 
 * Características principales:
 * - Creación del primer administrador del sistema
 * - Validación de campos en tiempo real
 * - Confirmación de contraseña con indicador visual
 * - Validación de formato de correo electrónico
 * - Confirmación antes de cancelar
 * - Mensajes informativos de éxito o error
 * 
 * El asistente únicamente funciona cuando no existen usuarios en el sistema.
 * Si ya existen usuarios, se mostrará un mensaje de error.
 * 
 * @author Equipo StockFlow / StockFlow Team
 * @version 1.0
 * @since 2025
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

    /**
     * Inicializa el controlador del asistente de configuración.
     * 
     * Se invoca automáticamente después de cargar el archivo FXML.
     * Configura las validaciones en tiempo real de los campos.
     * 
     * @param url URL del archivo FXML
     * @param rb Bundle de recursos
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioServicio = new UsuarioServicio();
        configurarValidaciones();
        
        System.out.println("SetupWizardController inicializado");
    }

    /**
     * Configura validaciones en tiempo real para los campos del formulario.
     * 
     * Valida:
     * - Coincidencia de contraseñas en tiempo real
     * - Limpia estilos de error cuando el usuario comienza a escribir
     * - Proporciona feedback visual sobre el estado de validación
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
     * Valida que las contraseñas ingresadas coincidan.
     * 
     * Cambia el borde del campo de confirmación a rojo si las contraseñas
     * no coinciden, o a verde si coinciden correctamente.
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
     * Maneja el evento de crear el primer administrador del sistema.
     * 
     * Valida todos los campos del formulario antes de proceder.
     * Crea el usuario administrador en la base de datos.
     * Muestra un mensaje de éxito y cierra la ventana.
     * Captura excepciones específicas y muestra errores adecuados.
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

            System.out.println("Creando primer administrador...");
            System.out.println("   Username: " + username);
            System.out.println("   Nombre: " + nombreCompleto);

            // Crear primer admin
            Usuario admin = usuarioServicio.crearPrimerAdmin(
                username, password, nombreCompleto, email
            );

            setupCompletado = true;

            // Alerta de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bienvenido a StockFlow!");
            alert.setHeaderText("Administrador creado exitosamente");
            alert.setContentText(
                "Usuario: " + admin.getUsername() + "\n" +
                "Nombre: " + admin.getNombreCompleto() + "\n" +
                "Rol: " + admin.getRol().getNombre() + "\n\n" +
                "Ahora puedes iniciar sesión con tus credenciales."
            );
            alert.showAndWait();

            System.out.println("Primer administrador creado exitosamente");
            cerrarVentana();

        } catch (IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
            mostrarError(
                "Error de Sistema",
                "Ya existen usuarios en el sistema",
                "Este asistente solo funciona en la primera ejecución."
            );
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            mostrarAdvertencia(
                "Datos Inválidos",
                "Por favor revisa los datos ingresados",
                e.getMessage()
            );
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            mostrarError(
                "Error",
                "No se pudo crear el administrador",
                e.getMessage()
            );
        }
    }

    /**
     * Valida todos los campos del formulario antes de crear el administrador.
     * 
     * Valida:
     * - Username: mínimo 3 caracteres
     * - Password: mínimo 4 caracteres
     * - Confirmación de password: debe coincidir con la contraseña
     * - Nombre completo: requerido
     * - Email: opcional pero debe ser válido si se proporciona
     * 
     * Muestra estilos de error en campos inválidos y muestra una alerta
     * resumiendo todos los errores encontrados.
     * 
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        boolean valido = true;

        // Username
        if (txtUsername.getText().trim().isEmpty()) {
            errores.append("- El nombre de usuario es obligatorio\n");
            txtUsername.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        } else if (txtUsername.getText().trim().length() < 3) {
            errores.append("- El nombre de usuario debe tener al menos 3 caracteres\n");
            txtUsername.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        }

        // Password
        if (txtPassword.getText().isEmpty()) {
            errores.append("- La contraseña es obligatoria\n");
            txtPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        } else if (txtPassword.getText().length() < 4) {
            errores.append("- La contraseña debe tener al menos 4 caracteres\n");
            txtPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        }

        // Confirmar password
        if (!txtPassword.getText().equals(txtConfirmarPassword.getText())) {
            errores.append("- Las contraseñas no coinciden\n");
            txtConfirmarPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        }

        // Nombre completo
        if (txtNombreCompleto.getText().trim().isEmpty()) {
            errores.append("- El nombre completo es obligatorio\n");
            txtNombreCompleto.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
            valido = false;
        }

        // Email (opcional pero debe ser válido)
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !esEmailValido(email)) {
            errores.append("- El formato del email es inválido\n");
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
     * Valida que un correo electrónico tenga el formato correcto.
     * 
     * Utiliza una expresión regular para validar el formato estándar de email.
     * 
     * @param email El correo electrónico a validar
     * @return true si el email tiene formato válido, false en caso contrario
     */
    private boolean esEmailValido(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Maneja el evento de cancelar el asistente de configuración.
     * 
     * Solicita confirmación antes de cancelar, ya que sin crear un administrador
     * no será posible usar el sistema.
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
                System.out.println("Setup cancelado por el usuario");
                cerrarVentana();
            }
        });
    }

    /**
     * Cierra la ventana del asistente de configuración.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCrear.getScene().getWindow();
        stage.close();
    }

    /**
     * Verifica si el asistente de configuración fue completado exitosamente.
     * 
     * @return true si se creó el administrador exitosamente, false en caso contrario
     */
    public boolean isSetupCompletado() {
        return setupCompletado;
    }

    /**
     * Muestra un diálogo de error.
     * 
     * @param titulo El título del diálogo
     * @param header El encabezado del diálogo
     * @param contenido El contenido del mensaje de error
     */
    private void mostrarError(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de advertencia.
     * 
     * @param titulo El título del diálogo
     * @param header El encabezado del diálogo
     * @param contenido El contenido del mensaje de advertencia
     */
    private void mostrarAdvertencia(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo informativo de éxito.
     * 
     * @param titulo El título del diálogo
     * @param header El encabezado del diálogo
     * @param contenido El contenido del mensaje
     */
    private void mostrarExito(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}