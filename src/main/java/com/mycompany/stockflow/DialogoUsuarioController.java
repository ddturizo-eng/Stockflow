/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.UsuarioServicio;
import com.mycompany.stockflow.Modelo.Rol;
import com.mycompany.stockflow.Modelo.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * Controlador para el diálogo de creación y edición de usuarios.
 * 
 * Este controlador gestiona la interfaz de usuario para crear nuevos usuarios
 * o editar usuarios existentes. Proporciona funcionalidades como:
 * - Validación de formularios en tiempo real
 * - Toggle de visibilidad de contraseña
 * - Sincronización de campos de contraseña
 * - Gestión de dos modos: creación y edición
 * - Visualización de errores de validación
 * 
 * En modo creación: requiere username, contraseña y confirmación de contraseña.
 * En modo edición: permite cambiar nombre, email, rol y opcionalmente contraseña.
 * 
 * @author Equipo StockFlow
 * @version 1.0
 * @since 1.0
 */

public class DialogoUsuarioController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    
    @FXML private TextField txtUsername;
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<Rol> cmbRol;
    
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private Button btnTogglePassword;
    @FXML private Label lblEyeIcon;
    
    @FXML private VBox vboxPassword;
    @FXML private VBox vboxConfirmarPassword;
    @FXML private CheckBox chkCambiarPassword;
    
    @FXML private Label lblErrorUsername;
    @FXML private Label lblErrorNombre;
    @FXML private Label lblErrorEmail;
    @FXML private Label lblErrorPassword;
    @FXML private Label lblErrorConfirmar;
    
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    
    private UsuarioServicio usuarioServicio;
    private Usuario usuarioEditando;
    private boolean modoEdicion = false;
    private boolean guardado = false;
    private boolean passwordVisible = false;

    /**
     * Inicializa el controlador de la vista.
     * 
     * Se ejecuta cuando el documento FXML es cargado. Realiza
     * las siguientes acciones:
     * - Inicializa el servicio de usuario
     * - Configura el ComboBox de roles
     * - Configura las validaciones en tiempo real
     * - Sincroniza los campos de contraseña
     * 
     * @param url La ubicación relativa del objeto FXML
     * @param rb El ResourceBundle específico de la localización
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioServicio = new UsuarioServicio();
        
        configurarComboRol();
        configurarValidaciones();
        sincronizarCamposPassword();
        
        System.out.println("✓ DialogoUsuarioController inicializado");
    }
    
    /**
     * Configura el ComboBox de roles con los valores disponibles.
     * 
     * Establece los roles disponibles del sistema en el ComboBox,
     * personaliza la visualización de los roles y establece CAJERO
     * como rol por defecto.
     */
    private void configurarComboRol() {
        cmbRol.setItems(FXCollections.observableArrayList(Rol.values()));
        cmbRol.setValue(Rol.CAJERO); // Por defecto
        
        // Personalizar cómo se muestran los roles
        cmbRol.setCellFactory(param -> new ListCell<Rol>() {
            @Override
            protected void updateItem(Rol item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre());
                }
            }
        });
        
        cmbRol.setButtonCell(new ListCell<Rol>() {
            @Override
            protected void updateItem(Rol item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre());
                }
            }
        });
    }
    
    /**
     * Configura los validadores en tiempo real para todos los campos.
     * 
     * Agrega listeners a los campos de texto para:
     * - Limpiar mensajes de error cuando el usuario modifica un campo
     * - Restablecer el estilo del campo a su estado normal
     * - Validar la coincidencia de contraseñas en tiempo real
     * - Mostrar/ocultar campos de contraseña según el checkbox
     */
    private void configurarValidaciones() {
        // Validación en tiempo real
        txtUsername.textProperty().addListener((obs, old, newVal) -> {
            lblErrorUsername.setVisible(false);
            txtUsername.setStyle("-fx-border-color: #dcdcdc;");
        });
        
        txtNombreCompleto.textProperty().addListener((obs, old, newVal) -> {
            lblErrorNombre.setVisible(false);
            txtNombreCompleto.setStyle("-fx-border-color: #dcdcdc;");
        });
        
        txtEmail.textProperty().addListener((obs, old, newVal) -> {
            lblErrorEmail.setVisible(false);
            txtEmail.setStyle("-fx-border-color: #dcdcdc;");
        });
        
        txtPassword.textProperty().addListener((obs, old, newVal) -> {
            lblErrorPassword.setVisible(false);
            txtPassword.setStyle("-fx-border-color: #dcdcdc;");
            validarCoincidenciaPasswords();
        });
        
        txtConfirmarPassword.textProperty().addListener((obs, old, newVal) -> {
            lblErrorConfirmar.setVisible(false);
            txtConfirmarPassword.setStyle("-fx-border-color: #dcdcdc;");
            validarCoincidenciaPasswords();
        });
        
        // CheckBox cambiar contraseña
        chkCambiarPassword.selectedProperty().addListener((obs, old, newVal) -> {
            vboxPassword.setVisible(newVal);
            vboxPassword.setManaged(newVal);
            vboxConfirmarPassword.setVisible(newVal);
            vboxConfirmarPassword.setManaged(newVal);
        });
    }
    
    /**
     * Sincroniza los campos de contraseña visible y oculto.
     * 
     * Mantiene ambos campos sincronizados para que el usuario pueda
     * ver o ocultar la contraseña indistintamente, siempre con el
     * mismo contenido.
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
     * Alterna la visibilidad de la contraseña.
     * 
     * Cambia entre mostrar la contraseña en texto plano o como puntos.
     * Actualiza el icono del ojo y mantiene el foco en el campo activo.
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
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPasswordVisible.setVisible(false);
            txtPassword.setVisible(true);
            lblEyeIcon.setText("👁");
            txtPassword.requestFocus();
        }
    }
    
    /**
     * Valida que las contraseñas ingresadas coincidan.
     * 
     * Compara la contraseña con su confirmación y aplica estilos
     * de error si no coinciden. Se ejecuta en tiempo real mientras
     * el usuario escribe.
     */
    private void validarCoincidenciaPasswords() {
        String pass1 = txtPassword.getText();
        String pass2 = txtConfirmarPassword.getText();
        
        if (!pass2.isEmpty() && !pass1.equals(pass2)) {
            txtConfirmarPassword.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
        } else {
            txtConfirmarPassword.setStyle("-fx-border-color: #dcdcdc;");
        }
    }
    
    // ========== CONFIGURACIÓN DE MODOS ==========
    
    /**
     * Configura el diálogo en modo creación de nuevo usuario.
     * 
     * Establece:
     * - Título y subtítulo del diálogo
     * - Campos habilitados para entrada
     * - Visibilidad de campos de contraseña (siempre visible en creación)
     * - Texto del botón de guardar
     */
    public void setModoCreacion() {
        modoEdicion = false;
        lblTitulo.setText("Nuevo Usuario");
        lblSubtitulo.setText("Complete los datos del nuevo usuario");
        
        txtUsername.setDisable(false);
        vboxPassword.setVisible(true);
        vboxPassword.setManaged(true);
        vboxConfirmarPassword.setVisible(true);
        vboxConfirmarPassword.setManaged(true);
        chkCambiarPassword.setVisible(false);
        chkCambiarPassword.setManaged(false);
        
        btnGuardar.setText("Crear Usuario");
    }
    
    /**
     * Configura el diálogo en modo edición de usuario existente.
     * 
     * Carga los datos del usuario especificado y establece:
     * - Título y subtítulo del diálogo
     * - Username deshabilitado (no se puede cambiar)
     * - Campos de contraseña ocultos (solo se muestran si se marca el checkbox)
     * - Checkbox para cambiar contraseña
     * - Texto del botón de guardar
     * 
     * @param usuario El usuario a editar
     */
    public void setModoEdicion(Usuario usuario) {
        modoEdicion = true;
        usuarioEditando = usuario;
        
        lblTitulo.setText("Editar Usuario");
        lblSubtitulo.setText("Modifique los datos del usuario");
        
        // Cargar datos
        txtUsername.setText(usuario.getUsername());
        txtUsername.setDisable(true); // No se puede cambiar el username
        txtNombreCompleto.setText(usuario.getNombreCompleto());
        txtEmail.setText(usuario.getEmail());
        cmbRol.setValue(usuario.getRol());
        
        // Ocultar campos de contraseña por defecto
        vboxPassword.setVisible(false);
        vboxPassword.setManaged(false);
        vboxConfirmarPassword.setVisible(false);
        vboxConfirmarPassword.setManaged(false);
        chkCambiarPassword.setVisible(true);
        chkCambiarPassword.setManaged(true);
        chkCambiarPassword.setSelected(false);
        
        btnGuardar.setText("Guardar Cambios");
    }
    
    // ========== ACCIONES ==========
    
    /**
     * Maneja el evento de click en el botón guardar.
     * 
     * Valida el formulario y, si es válido:
     * - En modo creación: crea un nuevo usuario
     * - En modo edición: actualiza el usuario existente
     * 
     * Luego cierra el diálogo. Si hay error, muestra un mensaje
     * al usuario sin cerrar el diálogo.
     */
    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            if (modoEdicion) {
                actualizarUsuario();
            } else {
                crearUsuario();
            }
            
            guardado = true;
            cerrarVentana();
            
        } catch (IllegalArgumentException e) {
            mostrarErrorValidacion(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al Guardar", "No se pudo guardar el usuario", e.getMessage());
        }
    }
    
    /**
     * Crea un nuevo usuario con los datos ingresados en el formulario.
     * 
     * Obtiene los valores de los campos, valida que sean correctos,
     * y los envía al servicio de usuario para crear el registro.
     */
    private void crearUsuario() {
        String username = txtUsername.getText().trim();
        String password = getPasswordText();
        String nombreCompleto = txtNombreCompleto.getText().trim();
        String email = txtEmail.getText().trim();
        Rol rol = cmbRol.getValue();
        
        Usuario usuario = usuarioServicio.crearUsuario(
            username, password, nombreCompleto, email, rol
        );
        
        System.out.println("✓ Usuario creado: " + usuario.getUsername());
    }
    
    /**
     * Actualiza los datos de un usuario existente.
     * 
     * Modifica el nombre completo, email y rol del usuario.
     * Si el checkbox de cambiar contraseña está marcado,
     * también actualiza la contraseña del usuario.
     */
    private void actualizarUsuario() {
        String nombreCompleto = txtNombreCompleto.getText().trim();
        String email = txtEmail.getText().trim();
        Rol rol = cmbRol.getValue();
        
        boolean exito = usuarioServicio.actualizarUsuario(
            usuarioEditando.getUsername(), nombreCompleto, email, rol
        );
        
        // Si se marcó cambiar contraseña
        if (chkCambiarPassword.isSelected()) {
            String nuevaPassword = getPasswordText();
            usuarioServicio.cambiarPassword(usuarioEditando.getUsername(), nuevaPassword);
        }
        
        if (exito) {
            System.out.println("✓ Usuario actualizado: " + usuarioEditando.getUsername());
        }
    }
    
    /**
     * Maneja el evento de click en el botón cancelar.
     * 
     * Cierra el diálogo sin guardar los cambios.
     */
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }
    
    // ========== VALIDACIONES ==========
    
    /**
     * Valida todos los campos del formulario.
     * 
     * Verifica:
     * - Username (solo en modo creación): no vacío y mínimo 3 caracteres
     * - Nombre completo: no puede estar vacío
     * - Email: formato válido (si está ingresado)
     * - Contraseña: no vacía, mínimo 4 caracteres y coincidencia confirmada
     * 
     * Si hay errores, muestra mensajes visuales en los campos correspondientes.
     * 
     * @return true si el formulario es válido, false en caso contrario
     */
    private boolean validarFormulario() {
        boolean valido = true;
        
        // Username (solo en creación)
        if (!modoEdicion) {
            String username = txtUsername.getText().trim();
            if (username.isEmpty()) {
                mostrarErrorCampo(txtUsername, lblErrorUsername, "El usuario es obligatorio");
                valido = false;
            } else if (username.length() < 3) {
                mostrarErrorCampo(txtUsername, lblErrorUsername, "Mínimo 3 caracteres");
                valido = false;
            }
        }
        
        // Nombre completo
        String nombreCompleto = txtNombreCompleto.getText().trim();
        if (nombreCompleto.isEmpty()) {
            mostrarErrorCampo(txtNombreCompleto, lblErrorNombre, "El nombre es obligatorio");
            valido = false;
        }
        
        // Email (opcional pero debe ser válido)
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !esEmailValido(email)) {
            mostrarErrorCampo(txtEmail, lblErrorEmail, "Formato de email inválido");
            valido = false;
        }
        
        // Contraseña (en creación o si se marcó cambiar)
        if (!modoEdicion || chkCambiarPassword.isSelected()) {
            String password = getPasswordText();
            String confirmar = txtConfirmarPassword.getText();
            
            if (password.isEmpty()) {
                mostrarErrorCampo(txtPassword, lblErrorPassword, "La contraseña es obligatoria");
                valido = false;
            } else if (password.length() < 4) {
                mostrarErrorCampo(txtPassword, lblErrorPassword, "Mínimo 4 caracteres");
                valido = false;
            }
            
            if (!password.equals(confirmar)) {
                mostrarErrorCampo(txtConfirmarPassword, lblErrorConfirmar, "Las contraseñas no coinciden");
                valido = false;
            }
        }
        
        return valido;
    }
    
    /**
     * Valida si una dirección de email tiene formato correcto.
     * 
     * @param email La dirección de email a validar
     * @return true si el email es válido, false en caso contrario
     */
    private boolean esEmailValido(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Obtiene el texto de la contraseña del campo activo.
     * 
     * Retorna el contenido del campo visible (ya sea el campo
     * de texto plano o el campo oculto según la visibilidad).
     * 
     * @return El texto de la contraseña
     */
    private String getPasswordText() {
        return passwordVisible ? txtPasswordVisible.getText() : txtPassword.getText();
    }
    
    // ========== UTILIDADES ==========
    
    /**
     * Muestra un mensaje de error en un campo específico.
     * 
     * Marca el campo con un borde rojo y muestra el mensaje de error
     * en su label correspondiente.
     * 
     * @param campo El campo de texto que tiene el error
     * @param labelError El label donde mostrar el mensaje
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarErrorCampo(TextField campo, Label labelError, String mensaje) {
        campo.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2;");
        labelError.setText(mensaje);
        labelError.setVisible(true);
    }
    
    /**
     * Muestra un diálogo de advertencia de validación.
     * 
     * Presenta un Alert tipo WARNING al usuario indicando que hay
     * datos inválidos en el formulario.
     * 
     * @param mensaje El mensaje de error detallado
     */
    private void mostrarErrorValidacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Datos Inválidos");
        alert.setHeaderText("Por favor corrija los siguientes errores");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de error general.
     * 
     * Presenta un Alert tipo ERROR al usuario con información
     * detallada sobre el error ocurrido.
     * 
     * @param titulo El título del diálogo de error
     * @param header El header del diálogo
     * @param contenido El contenido detallado del error
     */
    private void mostrarError(String titulo, String header, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
    
    /**
     * Cierra la ventana del diálogo.
     * 
     * Obtiene la ventana (Stage) actual y la cierra finalizando
     * el diálogo de creación o edición de usuario.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Verifica si el usuario fue guardado correctamente.
     * 
     * @return true si el usuario fue guardado, false en caso contrario
     */
    public boolean isGuardado() {
        return guardado;
    }
}