package com.mycompany.stockflow;

import com.mycompany.stockflow.Modelo.Cliente;
import com.mycompany.stockflow.Logica.ClienteServicio;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ClientesController implements Initializable {
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colCedula;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, Void> colAcciones;
    @FXML private Label lblTotalClientes;
    
    @FXML private VBox formularioContainer;
    @FXML private Label lblTituloFormulario;
    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    
    private ClienteServicio clienteServicio;
    private ObservableList<Cliente> listaClientes;
    private Cliente clienteSeleccionado;
    private boolean esEdicion = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clienteServicio = new ClienteServicio();
        listaClientes = FXCollections.observableArrayList();
        
        configurarTabla();
        cargarClientes();
        configurarBusqueda();
        configurarValidacionTiempoReal();
        
        System.out.println("ClientesController inicializado");
    }
    
    private void configurarTabla() {
        // Configurar columnas usando lambdas
        colCedula.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCedula()));
        
        colNombre.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombre()));
        
        colTelefono.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTelefono()));
        
        colEmail.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getEmail();
            return new SimpleStringProperty(email != null ? email : "");
        });
        
        colDireccion.setCellValueFactory(cellData -> {
            String direccion = cellData.getValue().getDireccion();
            return new SimpleStringProperty(direccion != null ? direccion : "");
        });
        
        // Columna de acciones
        colAcciones.setCellFactory(col -> new TableCell<Cliente, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnEditar, btnEliminar);
            
            {
                btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 10px; -fx-padding: 5 10;");
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 10px; -fx-padding: 5 10;");
                
                btnEditar.setOnAction(e -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    editarCliente(cliente);
                });
                
                btnEliminar.setOnAction(e -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    eliminarCliente(cliente);
                });
                
                hbox.setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
        
        tablaClientes.setItems(listaClientes);
    }
    
    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            buscarCliente();
        });
    }
    
    /**
     * Configura validación en tiempo real para mejorar la experiencia del usuario
     */
    private void configurarValidacionTiempoReal() {
        // Validar cédula mientras escribe (solo números, máximo 10 dígitos)
        txtCedula.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtCedula.setText(oldVal);
            }
            if (newVal.length() > 10) {
                txtCedula.setText(oldVal);
            }
        });
        
        // Validar teléfono mientras escribe (solo números, máximo 10 dígitos)
        txtTelefono.textProperty().addListener((obs, oldVal, newVal) -> {
            String limpio = newVal.replaceAll("[^\\d]", "");
            if (!newVal.equals(limpio)) {
                txtTelefono.setText(limpio);
            }
            if (limpio.length() > 10) {
                txtTelefono.setText(oldVal);
            }
        });
        
        // Validar nombre mientras escribe (solo letras, espacios, tildes y ñ)
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ']*")) {
                txtNombre.setText(oldVal);
            }
        });
    }
    
    @FXML
    private void cargarClientes() {
        try {
            List<Cliente> clientes = clienteServicio.listarClientes();
            listaClientes.clear();
            listaClientes.addAll(clientes);
            
            // Forzar refresh de la tabla
            tablaClientes.refresh();
            
            actualizarContador();
            System.out.println("Clientes cargados en tabla: " + clientes.size());
        } catch (Exception e) {
            mostrarError("Error al cargar clientes", e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void buscarCliente() {
        String termino = txtBuscar.getText().toLowerCase().trim();
        
        if (termino.isEmpty()) {
            cargarClientes();
            return;
        }
        
        try {
            List<Cliente> todosClientes = clienteServicio.listarClientes();
            List<Cliente> filtrados = new ArrayList<>();
            
            for (Cliente c : todosClientes) {
                if (c.getCedula().toLowerCase().contains(termino) ||
                    c.getNombre().toLowerCase().contains(termino) ||
                    (c.getTelefono() != null && c.getTelefono().toLowerCase().contains(termino))) {
                    filtrados.add(c);
                }
            }
            
            listaClientes.clear();
            listaClientes.addAll(filtrados);
            actualizarContador();
        } catch (Exception e) {
            mostrarError("Error al buscar", e.getMessage());
        }
    }
    
    @FXML
    private void mostrarFormularioAgregar() {
        esEdicion = false;
        clienteSeleccionado = null;
        lblTituloFormulario.setText("Agregar Nuevo Cliente");
        limpiarFormulario();
        txtCedula.setDisable(false);
        formularioContainer.setVisible(true);
    }
    
    private void editarCliente(Cliente cliente) {
        esEdicion = true;
        clienteSeleccionado = cliente;
        lblTituloFormulario.setText("Editar Cliente");
        
        txtCedula.setText(cliente.getCedula());
        txtCedula.setDisable(true);
        txtNombre.setText(cliente.getNombre());
        txtTelefono.setText(cliente.getTelefono());
        txtEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "");
        txtDireccion.setText(cliente.getDireccion() != null ? cliente.getDireccion() : "");
        
        formularioContainer.setVisible(true);
    }
    
    @FXML
    private void guardarCliente() {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            Cliente cliente;
            
            if (esEdicion) {
                cliente = clienteSeleccionado;
            } else {
                cliente = new Cliente(
                    txtCedula.getText().trim(),
                    txtNombre.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtDireccion.getText().trim(),
                    txtEmail.getText().trim()
                );
            }
            
            // Actualizar datos del cliente
            cliente.setNombre(txtNombre.getText().trim());
            cliente.setTelefono(txtTelefono.getText().trim());
            cliente.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
            cliente.setDireccion(txtDireccion.getText().trim().isEmpty() ? null : txtDireccion.getText().trim());
            
            if (esEdicion) {
                clienteServicio.actualizarCliente(cliente);
                mostrarInformacion("Éxito", "Cliente actualizado correctamente");
            } else {
                clienteServicio.crearCliente(cliente);
                mostrarInformacion("Éxito", "Cliente agregado correctamente");
            }
            
            cerrarFormulario();
            cargarClientes();
            
        } catch (Exception e) {
            mostrarError("Error al guardar", e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void eliminarCliente(Cliente cliente) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este cliente?");
        confirmacion.setContentText(cliente.getNombre() + " - " + cliente.getCedula());
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                clienteServicio.eliminarCliente(cliente.getCedula());
                mostrarInformacion("Éxito", "Cliente eliminado correctamente");
                cargarClientes();
            } catch (Exception e) {
                mostrarError("Error al eliminar", e.getMessage());
            }
        }
    }
    
    @FXML
    private void cerrarFormulario() {
        formularioContainer.setVisible(false);
        limpiarFormulario();
        txtCedula.setDisable(false);
    }
    
    private void limpiarFormulario() {
        txtCedula.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
    }
    
    // ========== MÉTODOS DE VALIDACIÓN ==========
    
    /**
     * Valida todos los campos del formulario según estándares colombianos
     */
    private boolean validarFormulario() {
        // Validar Cédula
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            mostrarAdvertencia("Campo requerido", "La cédula es obligatoria");
            txtCedula.requestFocus();
            return false;
        }
        
        if (!validarCedulaColombia(cedula)) {
            mostrarAdvertencia("Cédula inválida", 
                "La cédula debe contener solo números y tener entre 6 y 10 dígitos");
            txtCedula.requestFocus();
            return false;
        }
        
        // Validar que la cédula no exista (solo al crear)
        if (!esEdicion) {
            try {
                List<Cliente> clientes = clienteServicio.listarClientes();
                for (Cliente c : clientes) {
                    if (c.getCedula().equals(cedula)) {
                        mostrarAdvertencia("Cédula duplicada", 
                            "Ya existe un cliente con esta cédula");
                        txtCedula.requestFocus();
                        return false;
                    }
                }
            } catch (Exception e) {
                mostrarError("Error", "No se pudo verificar la cédula");
                return false;
            }
        }
        
        // Validar Nombre
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAdvertencia("Campo requerido", "El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }
        
        if (!validarNombre(nombre)) {
            mostrarAdvertencia("Nombre inválido", 
                "El nombre debe contener solo letras y espacios (mínimo 3 caracteres)");
            txtNombre.requestFocus();
            return false;
        }
        
        // Validar Teléfono
        String telefono = txtTelefono.getText().trim();
        if (telefono.isEmpty()) {
            mostrarAdvertencia("Campo requerido", "El teléfono es obligatorio");
            txtTelefono.requestFocus();
            return false;
        }
        
        if (!validarTelefonoColombia(telefono)) {
            mostrarAdvertencia("Teléfono inválido", 
                "Ingrese un teléfono válido:\n" +
                "• Celular: 10 dígitos (ej: 3001234567)\n" +
                "• Fijo: 7 dígitos (ej: 5551234) o 10 con indicativo (ej: 6015551234)");
            txtTelefono.requestFocus();
            return false;
        }
        
        // Validar Email (opcional pero debe ser válido si se ingresa)
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !validarEmail(email)) {
            mostrarAdvertencia("Email inválido", 
                "Ingrese un correo electrónico válido (ej: ejemplo@correo.com)");
            txtEmail.requestFocus();
            return false;
        }
        
        // Validar Dirección (opcional pero con formato mínimo)
        String direccion = txtDireccion.getText().trim();
        if (!direccion.isEmpty() && direccion.length() < 5) {
            mostrarAdvertencia("Dirección inválida", 
                "La dirección debe tener al menos 5 caracteres");
            txtDireccion.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida cédula de ciudadanía colombiana
     * Debe ser numérica y tener entre 6 y 10 dígitos
     */
    private boolean validarCedulaColombia(String cedula) {
        if (cedula == null || cedula.isEmpty()) {
            return false;
        }
        
        // Solo números
        if (!cedula.matches("\\d+")) {
            return false;
        }
        
        // Longitud entre 6 y 10 dígitos
        int longitud = cedula.length();
        return longitud >= 6 && longitud <= 10;
    }
    
    /**
     * Valida número de teléfono colombiano
     * Celular: 10 dígitos comenzando con 3
     * Fijo: 7 dígitos o 10 dígitos con indicativo
     */
    private boolean validarTelefonoColombia(String telefono) {
        if (telefono == null || telefono.isEmpty()) {
            return false;
        }
        
        // Eliminar espacios, guiones y paréntesis
        String telefonoLimpio = telefono.replaceAll("[\\s\\-()]", "");
        
        // Solo números
        if (!telefonoLimpio.matches("\\d+")) {
            return false;
        }
        
        int longitud = telefonoLimpio.length();
        
        // Celular: 10 dígitos comenzando con 3
        if (longitud == 10 && telefonoLimpio.startsWith("3")) {
            return true;
        }
        
        // Teléfono fijo: 7 dígitos (sin indicativo)
        if (longitud == 7) {
            return true;
        }
        
        // Teléfono fijo: 10 dígitos con indicativo (601-608 para ciudades principales)
        if (longitud == 10) {
            String indicativo = telefonoLimpio.substring(0, 3);
            // Indicativos válidos: 601 (Bogotá), 602 (Cali), 604 (Medellín), 605 (Barranquilla), etc.
            return indicativo.matches("60[1-8]");
        }
        
        return false;
    }
    
    /**
     * Valida que el nombre contenga solo letras y espacios
     * Mínimo 3 caracteres
     */
    private boolean validarNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return false;
        }
        
        // Mínimo 3 caracteres
        if (nombre.length() < 3) {
            return false;
        }
        
        // Solo letras (incluye ñ, tildes), espacios y apóstrofes
        // Permite nombres como "María José" o "O'Connor"
        return nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ']+");
    }
    
    /**
     * Valida formato de email
     */
    private boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return true; // Email es opcional
        }
        
        // Expresión regular para email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        
        return email.matches(emailRegex);
    }
    
    /**
     * Formatea el teléfono para mostrarlo de manera consistente
     * Opcional: Puedes llamar este método al guardar
     */
    private String formatearTelefono(String telefono) {
        if (telefono == null || telefono.isEmpty()) {
            return telefono;
        }
        
        String telefonoLimpio = telefono.replaceAll("[\\s\\-()]", "");
        
        // Celular: 300 123 4567
        if (telefonoLimpio.length() == 10 && telefonoLimpio.startsWith("3")) {
            return telefonoLimpio.substring(0, 3) + " " + 
                   telefonoLimpio.substring(3, 6) + " " + 
                   telefonoLimpio.substring(6);
        }
        
        // Fijo con indicativo: (601) 555 1234
        if (telefonoLimpio.length() == 10 && telefonoLimpio.startsWith("60")) {
            return "(" + telefonoLimpio.substring(0, 3) + ") " + 
                   telefonoLimpio.substring(3, 6) + " " + 
                   telefonoLimpio.substring(6);
        }
        
        // Fijo sin indicativo: 555 1234
        if (telefonoLimpio.length() == 7) {
            return telefonoLimpio.substring(0, 3) + " " + telefonoLimpio.substring(3);
        }
        
        return telefono;
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private void actualizarContador() {
        lblTotalClientes.setText("Total: " + listaClientes.size() + " clientes");
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
    
    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}