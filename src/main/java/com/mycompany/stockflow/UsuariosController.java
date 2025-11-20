package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.UsuarioServicio;
import com.mycompany.stockflow.Modelo.Rol;
import com.mycompany.stockflow.Modelo.Usuario;
import com.mycompany.stockflow.utils.SesionUsuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador para la gestión completa de usuarios del sistema.
 * 
 * Proporciona funcionalidad CRUD (Crear, Leer, Actualizar, Eliminar) para usuarios,
 * con búsqueda, filtrado por rol y estado, y estadísticas de usuarios.
 * 
 * Características principales:
 * - Tabla interactiva con lista de todos los usuarios
 * - Búsqueda en tiempo real por username, nombre o email
 * - Filtrado por rol (Administrador, Dueño, Cajero)
 * - Filtrado por estado (Activo, Inactivo)
 * - Botones de acción con iconos para editar, cambiar estado y eliminar
 * - Panel de estadísticas mostrando totales por rol
 * - Protección de usuarios críticos (no permitir desactivar el último admin)
 * - Diálogos modales para crear y editar usuarios
 * - Validaciones y confirmaciones de operaciones
 * 
 * @author Equipo StockFlow / StockFlow Team
 * @version 1.0
 * @since 2025
 */
public class UsuariosController implements Initializable {

    // ========== COMPONENTES FXML ==========
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroRol;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private Button btnNuevoUsuario;
    @FXML private Button btnLimpiarFiltros;
    
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colNombreCompleto;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colEstado;
    @FXML private TableColumn<Usuario, String> colUltimoAcceso;
    @FXML private TableColumn<Usuario, Void> colAcciones;
    
    @FXML private Label lblSubtitulo;
    @FXML private Label lblContador;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblUsuariosActivos;
    @FXML private Label lblAdmins;
    @FXML private Label lblDuenos;
    @FXML private Label lblCajeros;

    // ========== SERVICIOS Y DATOS ==========
    private UsuarioServicio usuarioServicio;
    private ObservableList<Usuario> listaUsuarios;
    private FilteredList<Usuario> listaFiltrada;

    /**
     * Inicializa el controlador de usuarios.
     * 
     * Se invoca automáticamente después de cargar el archivo FXML.
     * Configura la tabla, filtros, búsqueda y carga los usuarios iniciales.
     * 
     * @param url URL del archivo FXML
     * @param rb Bundle de recursos
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioServicio = new UsuarioServicio();
        listaUsuarios = FXCollections.observableArrayList();
        
        configurarTabla();
        configurarFiltros();
        configurarBusqueda();
        cargarUsuarios();
        actualizarEstadisticas();
        
        System.out.println("UsuariosController inicializado");
    }

    // ========== CONFIGURACION DE TABLA ==========
    
    /**
     * Configura todas las columnas de la tabla de usuarios.
     * 
     * Incluye:
     * - Columnas de datos (username, nombre, email, rol, estado, último acceso)
     * - Columna de acciones con botones para editar, cambiar estado y eliminar
     * - Estilos condicionales para estado y rol
     * - Iconos en botones de acción
     */
    private void configurarTabla() {
        colUsername.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getUsername()));
        
        colNombreCompleto.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getNombreCompleto()));
        
        colEmail.setCellValueFactory(data -> {
            String email = data.getValue().getEmail();
            return new SimpleStringProperty(email != null && !email.isEmpty() ? email : "N/A");
        });
        
        colRol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getRol().getNombre()));
        
        colEstado.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().isActivo() ? "Activo" : "Inactivo"));
        
        colUltimoAcceso.setCellValueFactory(data -> 
            data.getValue().ultimoAccesoProperty());
        
        // Columna de acciones con botones
        colAcciones.setCellFactory(param -> new TableCell<Usuario, Void>() {
            private final HBox hbox = new HBox(8);
            private final Button btnEditar = new Button();
            private final Button btnToggleEstado = new Button();
            private final Button btnEliminar = new Button();
            
            {
                hbox.setAlignment(Pos.CENTER);
                hbox.setPrefWidth(Double.MAX_VALUE);
                
                // Botón Editar
                ImageView imgEditar = crearImageView("file:src/main/resources/com/mycompany/stockflow/IMG/editar_usuario.png");
                btnEditar.setGraphic(imgEditar);
                configurarBotonAccion(btnEditar, "#FFFFFF00", "Editar", (usuario) -> handleEditarUsuario(usuario));
                
                // Botón Toggle Estado
                ImageView imgEstado = crearImageView("file:src/main/resources/com/mycompany/stockflow/IMG/estado.png");
                btnToggleEstado.setGraphic(imgEstado);
                configurarBotonAccion(btnToggleEstado, "#FFFFFF00", "Cambiar Estado", (usuario) -> handleToggleEstado(usuario));
                
                // Botón Eliminar
                ImageView imgEliminar = crearImageView("file:src/main/resources/com/mycompany/stockflow/IMG/eliminar_us.png");
                btnEliminar.setGraphic(imgEliminar);
                configurarBotonAccion(btnEliminar, "#FFFFFF00", "Eliminar", (usuario) -> handleEliminarUsuario(usuario));
                
                hbox.getChildren().addAll(btnEditar, btnToggleEstado, btnEliminar);
            }
            
            /**
             * Crea un ImageView desde una ruta de imagen.
             * 
             * @param rutaImagen La ruta de la imagen
             * @return El ImageView creado o uno vacío si hay error
             */
            private ImageView crearImageView(String rutaImagen) {
                try {
                    Image imagen = new Image(rutaImagen);
                    ImageView imageView = new ImageView(imagen);
                    imageView.setFitWidth(18);
                    imageView.setFitHeight(18);
                    imageView.setPreserveRatio(true);
                    return imageView;
                } catch (Exception e) {
                    System.err.println("Error al cargar imagen: " + rutaImagen);
                    e.printStackTrace();
                    return new ImageView();
                }
            }
            
            /**
             * Configura los estilos y eventos de un botón de acción.
             * 
             * @param btn El botón a configurar
             * @param color El color de fondo del botón
             * @param tooltip El texto del tooltip
             * @param accion La acción a ejecutar al hacer clic
             */
            private void configurarBotonAccion(Button btn, String color, String tooltip, 
                                              java.util.function.Consumer<Usuario> accion) {
                btn.setStyle(
                    "-fx-background-color: " + color + ";" +
                    "-fx-padding: 8 12;" +
                    "-fx-background-radius: 6;" +
                    "-fx-cursor: hand;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 10;"
                );
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(e -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    accion.accept(usuario);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    setGraphic(hbox);
                }
            }
        });
        
        // Estilo condicional para columna Estado
        colEstado.setCellFactory(col -> new TableCell<Usuario, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Activo")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Estilo condicional para columna Rol
        colRol.setCellFactory(col -> new TableCell<Usuario, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Administrador":
                            setStyle("-fx-text-fill: #347B99; -fx-font-weight: bold;");
                            break;
                        case "Dueño":
                            setStyle("-fx-text-fill:#90A340; -fx-font-weight: bold;");
                            break;
                        case "Cajero":
                            setStyle("-fx-text-fill: #050505; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });
    }

    // ========== CONFIGURACION DE FILTROS ==========
    
    /**
     * Configura los ComboBox de filtros de rol y estado.
     * 
     * Define las opciones disponibles y configura los listeners
     * para aplicar filtros cuando cambia la selección.
     */
    private void configurarFiltros() {
        // Filtro de rol
        cmbFiltroRol.setItems(FXCollections.observableArrayList(
            "Todos los roles", "Administrador", "Dueño", "Cajero"
        ));
        cmbFiltroRol.setValue("Todos los roles");
        
        // Filtro de estado
        cmbFiltroEstado.setItems(FXCollections.observableArrayList(
            "Todos los estados", "Activo", "Inactivo"
        ));
        cmbFiltroEstado.setValue("Todos los estados");
        
        // Listeners para filtros
        cmbFiltroRol.setOnAction(e -> aplicarFiltros());
        cmbFiltroEstado.setOnAction(e -> aplicarFiltros());
    }
    
    /**
     * Configura el listener para búsqueda en tiempo real.
     */
    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }
    
    /**
     * Aplica todos los filtros activos (búsqueda, rol y estado).
     * 
     * Filtra la lista de usuarios según:
     * - Texto de búsqueda en username, nombre o email
     * - Rol seleccionado
     * - Estado (activo/inactivo)
     * 
     * Actualiza el contador de resultados después de filtrar.
     */
    private void aplicarFiltros() {
        if (listaFiltrada == null) return;
        
        listaFiltrada.setPredicate(usuario -> {
            // Filtro de búsqueda
            String busqueda = txtBuscar.getText().toLowerCase().trim();
            boolean coincideBusqueda = busqueda.isEmpty() || 
                usuario.getUsername().toLowerCase().contains(busqueda) ||
                usuario.getNombreCompleto().toLowerCase().contains(busqueda) ||
                (usuario.getEmail() != null && usuario.getEmail().toLowerCase().contains(busqueda));
            
            if (!coincideBusqueda) return false;
            
            // Filtro de rol
            String filtroRol = cmbFiltroRol.getValue();
            boolean coincideRol = filtroRol.equals("Todos los roles") || 
                usuario.getRol().getNombre().equals(filtroRol);
            
            if (!coincideRol) return false;
            
            // Filtro de estado
            String filtroEstado = cmbFiltroEstado.getValue();
            boolean coincideEstado = filtroEstado.equals("Todos los estados") ||
                (filtroEstado.equals("Activo") && usuario.isActivo()) ||
                (filtroEstado.equals("Inactivo") && !usuario.isActivo());
            
            return coincideEstado;
        });
        
        actualizarContador();
    }

    // ========== CARGA DE DATOS ==========
    
    /**
     * Carga todos los usuarios desde la base de datos.
     * 
     * Obtiene la lista completa de usuarios y la muestra en la tabla.
     * Actualiza el contador y las estadísticas.
     */
    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioServicio.obtenerTodos();
            listaUsuarios.setAll(usuarios);
            
            listaFiltrada = new FilteredList<>(listaUsuarios, p -> true);
            tblUsuarios.setItems(listaFiltrada);
            
            actualizarContador();
            actualizarEstadisticas();
            
            System.out.println("Usuarios cargados: " + usuarios.size());
            
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error", "No se pudieron cargar los usuarios", e.getMessage());
        }
    }
    
    /**
     * Actualiza el contador de usuarios mostrado en la interfaz.
     * 
     * Muestra el número total de usuarios que coinciden con los filtros aplicados.
     */
    private void actualizarContador() {
        int total = listaFiltrada != null ? listaFiltrada.size() : 0;
        lblContador.setText(total + (total == 1 ? " usuario" : " usuarios"));
    }
    
    /**
     * Actualiza el panel de estadísticas de usuarios.
     * 
     * Muestra:
     * - Total de usuarios
     * - Usuarios activos
     * - Administradores
     * - Dueños
     * - Cajeros
     */
    private void actualizarEstadisticas() {
        try {
            UsuarioServicio.EstadisticasUsuarios stats = usuarioServicio.obtenerEstadisticas();
            
            lblTotalUsuarios.setText(String.valueOf(stats.total));
            lblUsuariosActivos.setText(String.valueOf(stats.activos));
            lblAdmins.setText(String.valueOf(stats.admins));
            lblDuenos.setText(String.valueOf(stats.dueños));
            lblCajeros.setText(String.valueOf(stats.cajeros));
            
        } catch (Exception e) {
            System.err.println("Error al actualizar estadísticas: " + e.getMessage());
        }
    }

    // ========== ACCIONES ==========
    
    /**
     * Maneja el evento de crear un nuevo usuario.
     * 
     * Abre un diálogo modal para ingresar los datos del nuevo usuario.
     * Si se guarda exitosamente, recarga la lista de usuarios.
     */
    @FXML
    private void handleNuevoUsuario() {
        System.out.println("Abriendo diálogo de nuevo usuario...");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DialogoUsuario.fxml"));
            Parent root = loader.load();
            
            DialogoUsuarioController controller = loader.getController();
            controller.setModoCreacion();
            
            Stage stage = new Stage();
            stage.setTitle("Nuevo Usuario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            
            stage.showAndWait();
            
            if (controller.isGuardado()) {
                cargarUsuarios();
                mostrarExito("Usuario Creado", "El usuario se creó correctamente");
            }
            
        } catch (IOException e) {
            System.err.println("Error al abrir diálogo: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir el diálogo", e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de editar un usuario.
     * 
     * Abre un diálogo modal con los datos del usuario para editar.
     * Si se guarda exitosamente, recarga la lista de usuarios.
     * 
     * @param usuario El usuario a editar
     */
    private void handleEditarUsuario(Usuario usuario) {
        System.out.println("Editando usuario: " + usuario.getUsername());
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DialogoUsuario.fxml"));
            Parent root = loader.load();
            
            DialogoUsuarioController controller = loader.getController();
            controller.setModoEdicion(usuario);
            
            Stage stage = new Stage();
            stage.setTitle("Editar Usuario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            
            stage.showAndWait();
            
            if (controller.isGuardado()) {
                cargarUsuarios();
                mostrarExito("Usuario Actualizado", "Los cambios se guardaron correctamente");
            }
            
        } catch (IOException e) {
            System.err.println("Error al abrir diálogo: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir el diálogo", e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de cambiar el estado de un usuario.
     * 
     * Alterna entre activo e inactivo después de confirmación.
     * Impide desactivar usuarios críticos del sistema.
     * 
     * @param usuario El usuario cuyo estado se cambiará
     */
    private void handleToggleEstado(Usuario usuario) {
        String accion = usuario.isActivo() ? "desactivar" : "activar";
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Acción");
        confirmacion.setHeaderText("¿" + accion.substring(0, 1).toUpperCase() + 
                                   accion.substring(1) + " usuario?");
        confirmacion.setContentText(
            "¿Está seguro de " + accion + " a " + usuario.getNombreCompleto() + "?"
        );
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean exito = usuarioServicio.toggleEstado(usuario.getUsername());
                
                if (exito) {
                    cargarUsuarios();
                    mostrarExito("Estado Actualizado", 
                        "El usuario fue " + (usuario.isActivo() ? "desactivado" : "activado") + " correctamente");
                } else {
                    mostrarError("Error", "No se pudo cambiar el estado", 
                        "Ocurrió un error al intentar cambiar el estado del usuario");
                }
                
            } catch (IllegalStateException e) {
                mostrarAdvertencia("Acción No Permitida", 
                    "No se puede desactivar este usuario", e.getMessage());
            } catch (Exception e) {
                mostrarError("Error", "Error al cambiar estado", e.getMessage());
            }
        }
    }
    
    /**
     * Maneja el evento de eliminar un usuario.
     * 
     * Solicita confirmación antes de proceder con la eliminación.
     * Impide eliminar usuarios críticos del sistema.
     * 
     * @param usuario El usuario a eliminar
     */
    private void handleEliminarUsuario(Usuario usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar usuario permanentemente?");
        confirmacion.setContentText(
            "Está a punto de eliminar a: " + usuario.getNombreCompleto() + "\n\n" +
            "Esta acción NO se puede deshacer.\n" +
            "¿Está completamente seguro?"
        );
        
        ButtonType btnEliminar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnEliminar, btnCancelar);
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnEliminar) {
            try {
                boolean exito = usuarioServicio.eliminarUsuario(usuario.getUsername());
                
                if (exito) {
                    cargarUsuarios();
                    mostrarExito("Usuario Eliminado", 
                        usuario.getNombreCompleto() + " fue eliminado del sistema");
                } else {
                    mostrarError("Error", "No se pudo eliminar el usuario", 
                        "Ocurrió un error durante la eliminación");
                }
                
            } catch (IllegalStateException e) {
                mostrarAdvertencia("Acción No Permitida", 
                    "No se puede eliminar este usuario", e.getMessage());
            } catch (Exception e) {
                mostrarError("Error", "Error al eliminar", e.getMessage());
            }
        }
    }
    
    /**
     * Limpia todos los filtros aplicados y muestra la lista completa.
     */
    @FXML
    private void handleLimpiarFiltros() {
        txtBuscar.clear();
        cmbFiltroRol.setValue("Todos los roles");
        cmbFiltroEstado.setValue("Todos los estados");
        aplicarFiltros();
    }

    // ========== UTILIDADES ==========
    
    /**
     * Muestra un diálogo de éxito.
     * 
     * @param titulo El título del diálogo
     * @param mensaje El mensaje de éxito
     */
    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
}