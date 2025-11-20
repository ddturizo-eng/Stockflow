/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    /**
     * Controlador de la interfaz gráfica para la gestión de clientes.
     * 
     * Esta clase implementa la lógica de presentación para el módulo de clientes, proporcionando
     * funcionalidades para visualizar, buscar, crear, editar y eliminar clientes. Utiliza JavaFX
     * para la interfaz gráfica y se integra con ClienteServicio para las operaciones de negocio.
     * 
     * Características principales:
     * - Gestión completa de CRUD (Create, Read, Update, Delete) de clientes
     * - Búsqueda en tiempo real de clientes por cédula, nombre o teléfono
     * - Validación comprensiva de datos según estándares colombianos
     * - Validación en tiempo real mientras el usuario escribe
     * - Tabla interactiva con botones de edición y eliminación
     * - Formulario dinámico para agregar o editar clientes
     * - Contador de clientes en la interfaz
     * 
     * @author StockFlow Team
     * @version 1.0
     * @see ClienteServicio
     * @see Cliente
     */
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

        /**
         * Inicializa el controlador con los valores necesarios para su funcionamiento.
         * 
         * Este método se ejecuta automáticamente cuando el archivo FXML se carga. Realiza
         * las siguientes operaciones:
         * - Inicializa el servicio de clientes
         * - Crea la lista observable para la tabla
         * - Configura la tabla con las columnas y datos
         * - Carga los clientes existentes desde la base de datos
         * - Configura los listeners para búsqueda y validación en tiempo real
         * 
         * @param url URL del archivo FXML (no utilizado actualmente)
         * @param rb ResourceBundle para internacionalización (no utilizado actualmente)
         */
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

        /**
         * Configura la tabla de clientes con sus columnas y comportamiento.
         * 
         * Este método establece:
         * - Los valores de cada columna usando SimpleStringProperty
         * - El comportamiento de las celdas para mostrar datos del cliente
         * - Los botones de edición y eliminación en la última columna
         * - Los estilos CSS para los botones de acción
         * - Los listeners para los eventos de click en los botones
         * 
         * La columna de acciones contiene dos botones: Editar y Eliminar, que permiten
         * modificar o eliminar un cliente de la tabla.
         */
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

        /**
         * Configura el listener para la búsqueda de clientes en tiempo real.
         * 
         * Añade un listener al campo de búsqueda que se dispara cada vez que el usuario
         * modifica el texto, ejecutando la búsqueda automáticamente sin necesidad de
         * presionar un botón.
         */
        private void configurarBusqueda() {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
                buscarCliente();
            });
        }

        /**
         * Configura la validación en tiempo real de los campos del formulario.
         * 
         * Implementa listeners para cada campo de entrada que validan el contenido
         * mientras el usuario escribe, proporcionando retroalimentación inmediata:
         * - Cédula: Solo números, máximo 10 dígitos
         * - Teléfono: Solo números, máximo 10 dígitos
         * - Nombre: Solo letras, espacios, ñ y tildes
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

        /**
         * Carga todos los clientes desde la base de datos y los muestra en la tabla.
         * 
         * Obtiene la lista completa de clientes del servicio, limpia la tabla actual,
         * añade los nuevos registros y actualiza el contador de clientes. En caso de
         * error, muestra un diálogo con el mensaje de error.
         * 
         * @throws Exception Si ocurre un error al acceder a la base de datos
         */
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

        /**
         * Busca clientes según el término ingresado en el campo de búsqueda.
         * 
         * Realiza una búsqueda insensible a mayúsculas/minúsculas en los campos
         * de cédula, nombre y teléfono. Si el campo de búsqueda está vacío,
         * recarga la lista completa de clientes.
         * 
         * Campos de búsqueda:
         * - Cédula del cliente
         * - Nombre del cliente
         * - Teléfono del cliente
         */
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

        /**
         * Prepara la interfaz para agregar un nuevo cliente.
         * 
         * Limpia el formulario, establece el modo de edición como falso,
         * actualiza el título del formulario y lo hace visible al usuario.
         * La cédula queda habilitada para que pueda ingresar un nuevo valor.
         */
        @FXML
        private void mostrarFormularioAgregar() {
            esEdicion = false;
            clienteSeleccionado = null;
            lblTituloFormulario.setText("Agregar Nuevo Cliente");
            limpiarFormulario();
            txtCedula.setDisable(false);
            formularioContainer.setVisible(true);
        }

        /**
         * Prepara la interfaz para editar un cliente existente.
         * 
         * Carga los datos del cliente en los campos del formulario,
         * establece el modo de edición como verdadero y muestra el formulario.
         * La cédula se habilita para permitir su modificación.
         * 
         * @param cliente El cliente a editar
         */
        private void editarCliente(Cliente cliente) {
            esEdicion = true;
            clienteSeleccionado = cliente;
            lblTituloFormulario.setText("Editar Cliente");

            txtCedula.setText(cliente.getCedula());
            txtCedula.setDisable(false);
            txtNombre.setText(cliente.getNombre());
            txtTelefono.setText(cliente.getTelefono());
            txtEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "");
            txtDireccion.setText(cliente.getDireccion() != null ? cliente.getDireccion() : "");

            formularioContainer.setVisible(true);
        }

        /**
         * Guarda un nuevo cliente o actualiza uno existente.
         * 
         * Valida el formulario, crea o actualiza un objeto Cliente con los datos
         * ingresados, lo persiste en la base de datos mediante el servicio y
         * actualiza la tabla. El comportamiento depende del modo de edición.
         * 
         * Flujo:
         * 1. Valida todos los campos del formulario
         * 2. Si es edición, actualiza el cliente seleccionado; si no, crea uno nuevo
         * 3. Persiste los cambios en la base de datos
         * 4. Muestra un mensaje de confirmación
         * 5. Cierra el formulario y recarga la tabla
         * 
         * @throws Exception Si ocurre un error al guardar en la base de datos
         */
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
                cliente.setCedula(txtCedula.getText().trim());
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

        /**
         * Elimina un cliente después de confirmar con el usuario.
         * 
         * Muestra un diálogo de confirmación con los datos del cliente.
         * Si el usuario confirma, procede a eliminar el cliente de la base de datos
         * y actualiza la tabla. Si cancela, no realiza ninguna acción.
         * 
         * @param cliente El cliente a eliminar
         */
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

        /**
         * Cierra el formulario y limpia todos sus campos.
         * 
         * Oculta el contenedor del formulario y realiza una limpieza de todos
         * los campos de texto para prepararlo para la siguiente operación.
         */
        @FXML
        private void cerrarFormulario() {
            formularioContainer.setVisible(false);
            limpiarFormulario();
            txtCedula.setDisable(false);
        }

        /**
         * Limpia todos los campos del formulario.
         * 
         * Vacía los campos de texto para dejar el formulario listo para
         * nuevas entradas de datos.
         */
        private void limpiarFormulario() {
            txtCedula.clear();
            txtNombre.clear();
            txtTelefono.clear();
            txtEmail.clear();
            txtDireccion.clear();
        }


        /**
         * Valida todos los campos del formulario según estándares colombianos.
         * 
         * Realiza validaciones completas de todos los campos:
         * - Verifica que los campos obligatorios no estén vacíos
         * - Valida el formato de cédula, teléfono y email
         * - Comprueba que la cédula no sea duplicada (solo al crear)
         * - Muestra mensajes de error específicos para cada campo inválido
         * 
         * @return true si todos los campos son válidos; false en caso contrario
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
         * Valida que la cédula sea una cédula de ciudadanía colombiana válida.
         * 
         * Verifica que:
         * - No sea nula o vacía
         * - Contenga solo dígitos numéricos
         * - Tenga entre 6 y 10 dígitos de longitud
         * 
         * @param cedula La cédula a validar
         * @return true si la cédula es válida; false en caso contrario
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
         * Valida que el número de teléfono sea un número colombiano válido.
         * 
         * Acepta los siguientes formatos:
         * - Celular: 10 dígitos comenzando con 3
         * - Teléfono fijo: 7 dígitos (sin indicativo)
         * - Teléfono fijo: 10 dígitos con indicativo válido (601-608)
         * 
         * @param telefono El número de teléfono a validar
         * @return true si el teléfono es válido; false en caso contrario
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
         * Valida que el nombre contenga solo caracteres permitidos.
         * 
         * Verifica que:
         * - No sea nulo o vacío
         * - Tenga una longitud mínima de 3 caracteres
         * - Contenga solo letras (incluyendo ñ y tildes), espacios y apóstrofes
         * 
         * @param nombre El nombre a validar
         * @return true si el nombre es válido; false en caso contrario
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
         * Valida que el formato de email sea correcto.
         * 
         * Utiliza una expresión regular para verificar que el email tenga
         * un formato válido. El campo de email es opcional, por lo que
         * una cadena vacía se considera válida.
         * 
         * Formato aceptado: usuario@dominio.extensión
         * 
         * @param email El email a validar
         * @return true si el email es válido o está vacío; false en caso contrario
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
         * Formatea un número de teléfono para mostrarlo de manera estándar y consistente.
         * 
         * Convierte el teléfono al siguiente formato según su tipo:
         * - Celular (10 dígitos, comienza con 3): 300 123 4567
         * - Fijo con indicativo (10 dígitos, comienza con 60): (601) 555 1234
         * - Fijo sin indicativo (7 dígitos): 555 1234
         * 
         * Nota: Este método es opcional y puede ser usado al guardar para normalizar
         * los números de teléfono en la base de datos.
         * 
         * @param telefono El número de teléfono sin formato
         * @return El teléfono formateado según su tipo, o el original si no coincide
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


        /**
         * Actualiza el label que muestra el total de clientes en la tabla.
         * 
         * Se ejecuta cada vez que la lista de clientes cambia para mantener
         * actualizado el contador visible para el usuario.
         */
        private void actualizarContador() {
            lblTotalClientes.setText("Total: " + listaClientes.size() + " clientes");
        }

        /**
         * Muestra un diálogo de error al usuario.
         * 
         * Crea y muestra un cuadro de alerta de tipo ERROR con el título
         * y mensaje especificados. El usuario debe cerrar el diálogo para
         * continuar con la aplicación.
         * 
         * @param titulo El título del diálogo de error
         * @param mensaje El mensaje de error a mostrar
         */
        private void mostrarError(String titulo, String mensaje) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }

        /**
         * Muestra un diálogo de información al usuario.
         * 
         * Crea y muestra un cuadro de alerta de tipo INFORMATION con el título
         * y mensaje especificados. Se usa para confirmar operaciones exitosas.
         * 
         * @param titulo El título del diálogo de información
         * @param mensaje El mensaje informativo a mostrar
         */
        private void mostrarInformacion(String titulo, String mensaje) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }

        /**
         * Muestra un diálogo de advertencia al usuario.
         * 
         * Crea y muestra un cuadro de alerta de tipo WARNING con el título
         * y mensaje especificados. Se usa para advertir sobre datos inválidos
         * o situaciones que requieren atención antes de continuar.
         * 
         * @param titulo El título del diálogo de advertencia
         * @param mensaje El mensaje de advertencia a mostrar
         */
        private void mostrarAdvertencia(String titulo, String mensaje) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }
    }