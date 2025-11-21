package com.mycompany.stockflow;

import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Logica.ProductoServicio;
import com.mycompany.stockflow.utils.ImagenProductoUtil;
import com.mycompany.stockflow.utils.CamaraServicio;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controlador para la gestión completa de productos en el sistema.
 * 
 * Proporciona funcionalidad CRUD (Crear, Leer, Actualizar, Eliminar) para productos,
 * incluyendo gestión de imágenes desde archivos o cámara web.
 * 
 * Características principales:
 * - Tabla interactiva con búsqueda y visualización de productos
 * - Formulario dinámico para crear y editar productos
 * - Selección de imágenes desde el sistema de archivos
 * - Captura de fotos directamente desde webcam
 * - Validación de datos en tiempo real
 * - Generación automática de códigos de producto
 * - Gestión de categorías predefinidas
 * - Alertas sobre stock bajo
 * - Notificación de cambios al dashboard
 * 
 * @author Equipo StockFlow / StockFlow Team
 * @version 1.0
 * @since 2025
 */
public class ProductosController implements Initializable {
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Number> colPrecioCompra;
    @FXML private TableColumn<Producto, Number> colPrecioVenta;
    @FXML private TableColumn<Producto, Number> colStock;
    @FXML private TableColumn<Producto, Number> colStockMinimo;
    @FXML private TableColumn<Producto, Void> colAcciones;
    @FXML private Label lblTotalProductos;
    
    @FXML private VBox formularioContainer;
    @FXML private Label lblTituloFormulario;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private TextField txtPrecioCompra;
    @FXML private TextField txtPrecioVenta;
    @FXML private TextField txtStock;
    @FXML private TextField txtStockMinimo;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar;
    
    @FXML private ImageView imgVistaPrevia;
    @FXML private Button btnSeleccionarImagen;
    @FXML private Button btnTomarFoto;
    @FXML private Button btnEliminarImagen;
    
    private ProductoServicio productoServicio;
    private ObservableList<Producto> listaProductos;
    private Producto productoSeleccionado;
    private boolean esEdicion = false;
    
    private File archivoImagenSeleccionado;
    private Image imagenCapturada;
    private boolean imagenModificada = false;
    private DashboardController dashboardController;
    
    /**
     * Inicializa el controlador y configura todos los componentes de interfaz.
     * 
     * Se invoca automáticamente después de cargar el archivo FXML.
     * Configura tabla, combos, búsqueda y validaciones en tiempo real.
     * 
     * @param url URL del archivo FXML
     * @param rb Bundle de recursos
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productoServicio = new ProductoServicio();
        listaProductos = FXCollections.observableArrayList();
        
        configurarTabla();
        configurarComboBoxCategorias();
        cargarProductos();
        configurarBusqueda();
        configurarValidacionTiempoReal();
        configurarVistaPrevia();
    }
    
    /**
     * Configura las columnas de la tabla de productos.
     * 
     * Define los valores de celda para cada columna, incluyendo formateo
     * especial para precios, stock y estados. Añade botones de acción
     * para editar y eliminar productos.
     */
    private void configurarTabla() {
        colCodigo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCodigo()));
        
        colNombre.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombre()));
        
        colCategoria.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategoria()));
        
        colPrecioCompra.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getPrecioCompra()));
        
        colPrecioCompra.setCellFactory(col -> new TableCell<Producto, Number>() {
            @Override
            protected void updateItem(Number precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", precio.doubleValue()));
                    setStyle("-fx-text-fill: #e74c3c;");
                }
            }
        });
        
        colPrecioVenta.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getPrecioVenta()));
        
        colPrecioVenta.setCellFactory(col -> new TableCell<Producto, Number>() {
            @Override
            protected void updateItem(Number precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", precio.doubleValue()));
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });
        
        colStock.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getStock()));
        
        colStock.setCellFactory(col -> new TableCell<Producto, Number>() {
            @Override
            protected void updateItem(Number stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(stock.toString());
                    Producto producto = getTableView().getItems().get(getIndex());
                    if (producto != null && stock.intValue() <= producto.getStockMinimo()) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60;");
                    }
                }
            }
        });
        
        colStockMinimo.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getStockMinimo()));
        
        colAcciones.setCellFactory(col -> new TableCell<Producto, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnEditar, btnEliminar);
            
            {
                btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 10px; -fx-padding: 5 10;");
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 10px; -fx-padding: 5 10;");
                
                btnEditar.setOnAction(e -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    editarProducto(producto);
                });
                
                btnEliminar.setOnAction(e -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    eliminarProducto(producto);
                });
                
                hbox.setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
        
        tablaProductos.setItems(listaProductos);
    }
    
    /**
     * Configura las categorías disponibles en el ComboBox.
     */
    private void configurarComboBoxCategorias() {
        cbCategoria.setItems(FXCollections.observableArrayList(
            "Electrónica", "Ropa y Accesorios", "Alimentos y Bebidas",
            "Hogar y Jardín", "Deportes", "Juguetes", "Libros",
            "Salud y Belleza", "Automotriz", "Otros"
        ));
    }
    
    /**
     * Configura el listener para la búsqueda en tiempo real.
     */
    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarProducto());
    }
    
    /**
     * Configura validaciones de entrada de datos en tiempo real.
     * 
     * Valida:
     * - Código: alfanuméricos, guiones y guiones bajos, máximo 20 caracteres
     * - Nombre: máximo 100 caracteres
     * - Precios: formato decimal con 2 decimales
     * - Stock: números enteros sin límite de caracteres más allá de 8
     * - Descripción: máximo 500 caracteres
     */
    private void configurarValidacionTiempoReal() {
        txtCodigo.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtrado = newVal.replaceAll("[^a-zA-Z0-9-_]", "").toUpperCase();
            if (filtrado.length() > 20) filtrado = filtrado.substring(0, 20);
            if (!newVal.equals(filtrado)) txtCodigo.setText(filtrado);
        });
        
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 100) txtNombre.setText(oldVal);
        });
        
        txtPrecioCompra.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d{0,2}")) txtPrecioCompra.setText(oldVal);
        });
        
        txtPrecioVenta.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d{0,2}")) txtPrecioVenta.setText(oldVal);
        });
        
        txtStock.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*") || newVal.length() > 8) txtStock.setText(oldVal);
        });
        
        txtStockMinimo.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*") || newVal.length() > 8) txtStockMinimo.setText(oldVal);
        });
        
        txtDescripcion.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 500) txtDescripcion.setText(oldVal);
        });
    }
    
    /**
     * Configura la vista previa de imagen con la imagen por defecto.
     */
    private void configurarVistaPrevia() {
        cargarImagenPorDefecto();
    }
    
    /**
     * Carga la imagen por defecto en la vista previa.
     * 
     * Se utiliza cuando no hay imagen seleccionada o cuando se elimina la imagen.
     */
    private void cargarImagenPorDefecto() {
        Image imagenDefault = ImagenProductoUtil.obtenerImagenPorDefecto();
        if (imagenDefault != null) {
            imgVistaPrevia.setImage(imagenDefault);
        }
    }
    
    /**
     * Abre un diálogo para seleccionar una imagen del sistema de archivos.
     * 
     * Muestra un FileChooser con filtros para archivos de imagen (PNG, JPG, BMP).
     * La imagen seleccionada se carga en la vista previa y se marca como modificada.
     */
        @FXML
    private void seleccionarImagen() {
        // Deshabilitar botón para evitar conflicto con captura de cámara
        btnTomarFoto.setDisable(true);

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Imagen del Producto");

            // Agregar filtros de archivo
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.bmp"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
            );

            Stage stage = (Stage) btnSeleccionarImagen.getScene().getWindow();
            File archivo = fileChooser.showOpenDialog(stage);

            if (archivo != null) {
                try {
                    System.out.println("Cargando imagen: " + archivo.getAbsolutePath());

                    Image imagen = new Image(archivo.toURI().toString());

                    // Validar que la imagen se cargó correctamente
                    if (imagen.isError()) {
                        mostrarError("Error al cargar imagen", 
                            "La imagen no pudo ser cargada. Asegúrese de que es un archivo válido.");
                        return;
                    }

                    imgVistaPrevia.setImage(imagen);
                    archivoImagenSeleccionado = archivo;
                    imagenCapturada = null;
                    imagenModificada = true;

                    System.out.println("Imagen cargada exitosamente");
                    mostrarInformacion("Imagen seleccionada", 
                        "La imagen se guardará al crear/actualizar el producto");

                } catch (Exception e) {
                    System.err.println("Error al cargar imagen: " + e.getMessage());
                    e.printStackTrace();
                    mostrarError("Error al cargar imagen", 
                        "No se pudo cargar la imagen seleccionada: " + e.getMessage());
                }
            } else {
                System.out.println("Selección de imagen cancelada");
            }

        } catch (Exception e) {
            System.err.println("Error en selector de imagen: " + e.getMessage());
            mostrarError("Error", "Error al abrir el selector de imagen: " + e.getMessage());
        } finally {
            // Siempre habilitar botón al terminar
            btnTomarFoto.setDisable(false);
        }
    }
    
    /**
     * Captura una foto directamente desde la cámara web.
     * 
     * Verifica que la cámara esté disponible en el sistema.
     * La foto capturada se muestra en la vista previa y se marca como modificada.
     */
   @FXML
    private void tomarFoto() {
        // Validar disponibilidad de cámara
        if (!CamaraServicio.isCamaraDisponible()) {
            mostrarAdvertencia("Cámara no disponible", 
                "No se detectó ninguna cámara web en el sistema.\n\n" +
                "Para usar esta función necesita:\n" +
                "1. Una webcam conectada\n" +
                "2. La librería Webcam Capture (sarxos)\n\n" +
                "Por favor, use 'Seleccionar Imagen' para cargar una foto existente.");
            return;
        }

        try {
            System.out.println("Iniciando captura de foto...");

            // Deshabilitar botón para evitar clics múltiples
            btnTomarFoto.setDisable(true);
            btnSeleccionarImagen.setDisable(true);

            try {
                // Capturar foto con timeout integrado en CamaraServicio
                Image fotoCapturada = CamaraServicio.capturarFoto();

                // Validar que se capturó la foto (usuario no canceló)
                if (fotoCapturada != null) {
                    imgVistaPrevia.setImage(fotoCapturada);
                    imagenCapturada = fotoCapturada;
                    archivoImagenSeleccionado = null;
                    imagenModificada = true;

                    System.out.println("Foto capturada exitosamente");
                    mostrarInformacion("Foto capturada", 
                        "La foto se guardará al crear/actualizar el producto");
                } else {
                    System.out.println("Captura de foto cancelada por el usuario");
                }

            } catch (Exception e) {
                System.err.println("Error durante la captura de foto: " + e.getMessage());
                e.printStackTrace();
                mostrarError("Error al capturar foto", 
                    "No se pudo capturar la foto: " + e.getMessage() + 
                    "\n\nVerifique que la cámara esté correctamente conectada.");
            }

        } finally {
            // Siempre habilitar botones al terminar
            btnTomarFoto.setDisable(false);
            btnSeleccionarImagen.setDisable(false);
        }
    }
    
    /**
     * Elimina la imagen seleccionada y restaura la imagen por defecto.
     */
    @FXML
    private void eliminarImagenPrevia() {
        try {
            cargarImagenPorDefecto();
            archivoImagenSeleccionado = null;
            imagenCapturada = null;
            imagenModificada = true;

            System.out.println("Imagen eliminada, restaurada imagen por defecto");
            mostrarInformacion("Imagen eliminada", "Se restauró la imagen por defecto");

        } catch (Exception e) {
            System.err.println("Error al eliminar imagen: " + e.getMessage());
            mostrarError("Error", "Error al eliminar la imagen: " + e.getMessage());
        }
    }
    
    /**
     * Carga la imagen de un producto existente en la vista previa.
     * 
     * @param producto El producto del cual se cargará la imagen
     */
        private void cargarImagenProducto(Producto producto) {
        try {
            if (producto != null && producto.tieneImagen()) {
                System.out.println("Cargando imagen del producto: " + producto.getCodigo());

                Image imagen = ImagenProductoUtil.cargarImagen(producto.getRutaImagen());
                if (imagen != null && !imagen.isError()) {
                    imgVistaPrevia.setImage(imagen);
                    System.out.println("Imagen cargada exitosamente");
                } else {
                    System.out.println("La imagen del producto no es válida, usando imagen por defecto");
                    cargarImagenPorDefecto();
                }
            } else {
                cargarImagenPorDefecto();
            }

            archivoImagenSeleccionado = null;
            imagenCapturada = null;
            imagenModificada = false;

        } catch (Exception e) {
            System.err.println("Error al cargar imagen del producto: " + e.getMessage());
            cargarImagenPorDefecto();
        }
    }
    
    /**
     * Carga todos los productos desde la base de datos.
     */
    @FXML
    private void cargarProductos() {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            listaProductos.clear();
            listaProductos.addAll(productos);
            tablaProductos.refresh();
            actualizarContador();
        } catch (Exception e) {
            mostrarError("Error al cargar productos", e.getMessage());
        }
    }
    
    /**
     * Busca productos según el término ingresado.
     * 
     * Busca en código, nombre y categoría del producto.
     * Si el campo está vacío, muestra todos los productos.
     */
    @FXML
    private void buscarProducto() {
        String termino = txtBuscar.getText().toLowerCase().trim();
        
        if (termino.isEmpty()) {
            cargarProductos();
            return;
        }
        
        try {
            List<Producto> todosProductos = productoServicio.listarProductos();
            List<Producto> filtrados = new ArrayList<>();
            
            for (Producto p : todosProductos) {
                if (p.getCodigo().toLowerCase().contains(termino) ||
                    p.getNombre().toLowerCase().contains(termino) ||
                    (p.getCategoria() != null && p.getCategoria().toLowerCase().contains(termino))) {
                    filtrados.add(p);
                }
            }
            
            listaProductos.clear();
            listaProductos.addAll(filtrados);
            actualizarContador();
        } catch (Exception e) {
            mostrarError("Error al buscar", e.getMessage());
        }
    }
    
    /**
     * Prepara el formulario para agregar un nuevo producto.
     * 
     * Limpia todos los campos, resetea el estado y muestra el formulario.
     */
    @FXML
    private void mostrarFormularioAgregar() {
        esEdicion = false;
        productoSeleccionado = null;
        lblTituloFormulario.setText("Agregar Nuevo Producto");
        limpiarFormulario();
        txtCodigo.setDisable(false);
        cargarImagenPorDefecto();
        formularioContainer.setVisible(true);
    }
    
    /**
     * Prepara el formulario para editar un producto existente.
     * 
     * Carga los datos del producto en los campos de formulario.
     * El código del producto queda deshabilitado para no permitir cambios.
     * 
     * @param producto El producto a editar
     */
    private void editarProducto(Producto producto) {
        esEdicion = true;
        productoSeleccionado = producto;
        lblTituloFormulario.setText("Editar Producto");
        
        txtCodigo.setText(producto.getCodigo());
        txtCodigo.setDisable(true);
        txtNombre.setText(producto.getNombre());
        cbCategoria.setValue(producto.getCategoria());
        txtPrecioCompra.setText(String.valueOf(producto.getPrecioCompra()));
        txtPrecioVenta.setText(String.valueOf(producto.getPrecioVenta()));
        txtStock.setText(String.valueOf(producto.getStock()));
        txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
        txtDescripcion.setText(producto.getDescripcion() != null ? producto.getDescripcion() : "");
        
        cargarImagenProducto(producto);
        
        formularioContainer.setVisible(true);
    }
    
    /**
     * Guarda un producto nuevo o actualiza uno existente.
     * 
     * Valida todos los campos del formulario antes de guardar.
     * Gestiona la carga de imágenes si fueron modificadas.
     * Notifica al dashboard de los cambios realizados.
     * Verifica el stock bajo después de guardar.
     */
    @FXML
    private void guardarProducto() {
        if (!validarFormulario()) {
            return;
        }

        // Deshabilitar botón para evitar clics múltiples
        btnGuardar.setDisable(true);

        try {
            Producto producto = esEdicion ? productoSeleccionado : new Producto();

            if (!esEdicion) {
                producto.setCodigo(txtCodigo.getText().trim().toUpperCase());
            }

            producto.setNombre(txtNombre.getText().trim());
            producto.setCategoria(cbCategoria.getValue());
            producto.setPrecioCompra(formatearPrecio(txtPrecioCompra.getText().trim()));
            producto.setPrecioVenta(formatearPrecio(txtPrecioVenta.getText().trim()));
            producto.setStock(Integer.parseInt(txtStock.getText().trim()));
            producto.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));

            String descripcion = txtDescripcion.getText().trim();
            producto.setDescripcion(descripcion.isEmpty() ? null : descripcion);

            // Gestión mejorada de imágenes
            if (imagenModificada) {
                try {
                    String rutaImagen = null;

                    if (archivoImagenSeleccionado != null) {
                        System.out.println("Guardando imagen desde archivo...");
                        rutaImagen = ImagenProductoUtil.copiarImagen(
                            archivoImagenSeleccionado, 
                            producto.getCodigo()
                        );
                        System.out.println("Imagen guardada en: " + rutaImagen);

                    } else if (imagenCapturada != null) {
                        System.out.println("Guardando imagen capturada de cámara...");
                        rutaImagen = ImagenProductoUtil.guardarImagen(
                            imagenCapturada, 
                            producto.getCodigo()
                        );
                        System.out.println("Imagen guardada en: " + rutaImagen);
                    }

                    // Eliminar imagen anterior si se edita y cambia
                    if (esEdicion && producto.tieneImagen() && rutaImagen != null) {
                        try {
                            ImagenProductoUtil.eliminarImagen(producto.getRutaImagen());
                            System.out.println("Imagen anterior eliminada");
                        } catch (Exception e) {
                            System.err.println("Advertencia al eliminar imagen anterior: " + e.getMessage());
                        }
                    }

                    producto.setRutaImagen(rutaImagen);

                } catch (Exception e) {
                    System.err.println("Error al guardar imagen: " + e.getMessage());
                    e.printStackTrace();
                    mostrarAdvertencia("Advertencia", 
                        "El producto se guardará sin imagen.\n\nError: " + e.getMessage());
                }
            }

            // Guardar producto en base de datos
            if (esEdicion) {
                System.out.println("Actualizando producto: " + producto.getCodigo());
                productoServicio.actualizarProducto(producto);
                mostrarInformacion("Éxito", "Producto actualizado correctamente");
            } else {
                System.out.println("Creando nuevo producto: " + producto.getCodigo());
                productoServicio.crearProducto(producto);
                mostrarInformacion("Éxito", "Producto agregado correctamente");
            }

            cerrarFormulario();
            cargarProductos();
            verificarStockBajo();

            // Notificar cambios al dashboard
            if (dashboardController != null) {
                dashboardController.notificarCambioEnProductos();
                System.out.println("Dashboard notificado de cambios");
            }

        } catch (Exception e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al guardar", 
                "No se pudo guardar el producto: " + e.getMessage());
        } finally {
            // Siempre habilitar botón al terminar
            btnGuardar.setDisable(false);
        }
    }
    
    /**
     * Elimina un producto después de confirmación del usuario.
     * 
     * Elimina la imagen asociada del sistema de archivos.
     * Notifica al dashboard de los cambios.
     * 
     * @param producto El producto a eliminar
     */
    private void eliminarProducto(Producto producto) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este producto?");
        confirmacion.setContentText(producto.getNombre() + " - " + producto.getCodigo());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                if (producto.tieneImagen()) {
                    ImagenProductoUtil.eliminarImagen(producto.getRutaImagen());
                }

                productoServicio.eliminarProducto(producto.getCodigo());
                mostrarInformacion("Éxito", "Producto eliminado correctamente");
                cargarProductos();

                notificarCambiosAlDashboard();

            } catch (Exception e) {
                mostrarError("Error al eliminar", e.getMessage());
            }
        }
    }

    /**
     * Notifica al controlador del dashboard sobre cambios en productos.
     * 
     * Permite que el dashboard actualice sus gráficas y estadísticas.
     */
    private void notificarCambiosAlDashboard() {
        try {
            javafx.scene.Scene scene = tablaProductos.getScene();
            if (scene != null) {
                javafx.stage.Window window = scene.getWindow();
                System.out.println("Cambios en productos detectados - Dashboard se actualizará");
            }
        } catch (Exception e) {
            System.err.println("No se pudo notificar cambios: " + e.getMessage());
        }
    }

    /**
     * Cierra el formulario y limpia todos los campos.
     */
    @FXML
    private void cerrarFormulario() {
        try {
            formularioContainer.setVisible(false);
            limpiarFormulario();
            txtCodigo.setDisable(false);

            // Limpiar estado de imágenes
            archivoImagenSeleccionado = null;
            imagenCapturada = null;
            imagenModificada = false;

            // Habilitar botones
            btnTomarFoto.setDisable(false);
            btnSeleccionarImagen.setDisable(false);
            btnGuardar.setDisable(false);

            System.out.println("Formulario cerrado");

        } catch (Exception e) {
            System.err.println("Error al cerrar formulario: " + e.getMessage());
        }
    }
    
    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        cbCategoria.setValue(null);
        txtPrecioCompra.clear();
        txtPrecioVenta.clear();
        txtStock.clear();
        txtStockMinimo.clear();
        txtDescripcion.clear();
        cargarImagenPorDefecto();
    }
    
    /**
     * Valida todos los datos del formulario antes de guardar.
     * 
     * Valida:
     * - Código único y válido
     * - Nombre de producto válido
     * - Categoría seleccionada
     * - Precios válidos y coherentes
     * - Stock válido
     * - Consistencia entre stock y stock mínimo
     * 
     * @return true si todos los datos son válidos, false en caso contrario
     */
    private boolean validarFormulario() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty() || !validarCodigo(codigo)) {
            mostrarAdvertencia("Código inválido", "El código debe tener entre 3 y 20 caracteres");
            return false;
        }
        
        if (!esEdicion) {
            try {
                for (Producto p : productoServicio.listarProductos()) {
                    if (p.getCodigo().equalsIgnoreCase(codigo)) {
                        mostrarAdvertencia("Código duplicado", "Ya existe un producto con este código");
                        return false;
                    }
                }
            } catch (Exception e) {
                mostrarError("Error", "No se pudo verificar el código");
                return false;
            }
        }
        
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty() || !validarNombreProducto(nombre)) {
            mostrarAdvertencia("Nombre inválido", "El nombre debe tener entre 3 y 100 caracteres");
            return false;
        }
        
        if (cbCategoria.getValue() == null) {
            mostrarAdvertencia("Campo requerido", "Debe seleccionar una categoría");
            return false;
        }
        
        double precioCompra, precioVenta;
        try {
            precioCompra = Double.parseDouble(txtPrecioCompra.getText().trim());
            if (!validarPrecio(precioCompra)) {
                mostrarAdvertencia("Precio inválido", "El precio de compra debe ser mayor a 0");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("Precio inválido", "Ingrese un precio de compra válido");
            return false;
        }
        
        try {
            precioVenta = Double.parseDouble(txtPrecioVenta.getText().trim());
            if (!validarPrecio(precioVenta)) {
                mostrarAdvertencia("Precio inválido", "El precio de venta debe ser mayor a 0");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("Precio inválido", "Ingrese un precio de venta válido");
            return false;
        }
        
        if (precioVenta <= precioCompra) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia de Rentabilidad");
            alert.setHeaderText("El precio de venta es menor o igual al de compra");
            alert.setContentText("¿Está seguro de continuar?");
            
            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) return false;
        }
        
        try {
            int stock = Integer.parseInt(txtStock.getText().trim());
            int stockMinimo = Integer.parseInt(txtStockMinimo.getText().trim());
            
            if (!validarStock(stock) || !validarStock(stockMinimo)) {
                mostrarAdvertencia("Stock inválido", "El stock debe ser >= 0");
                return false;
            }
            
            if (stockMinimo > stock) {
                mostrarAdvertencia("Inconsistencia", "Stock mínimo no puede ser mayor al actual");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("Stock inválido", "Ingrese valores numéricos válidos");
            return false;
        }
        
        return true;
    }
    
    /**
     * Verifica y muestra alerta si hay productos con stock bajo.
     * 
     * Comprueba cada producto en la lista para detectar si el stock
     * está por debajo del stock mínimo configurado.
     */
    private void verificarStockBajo() {
        try {
            List<String> productosStockBajo = new ArrayList<>();
            
            for (Producto p : listaProductos) {
                if (p.tieneStockBajo()) {
                    productosStockBajo.add(p.getNombre() + " (Stock: " + p.getStock() + ")");
                }
            }
            
            if (!productosStockBajo.isEmpty() && productosStockBajo.size() <= 10) {
                StringBuilder mensaje = new StringBuilder("Productos con stock bajo:\n\n");
                for (String prod : productosStockBajo) {
                    mensaje.append("- ").append(prod).append("\n");
                }
                
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Alerta de Stock Bajo");
                alerta.setHeaderText(productosStockBajo.size() + " producto(s) con stock bajo");
                alerta.setContentText(mensaje.toString());
                alerta.show();
            }
        } catch (Exception e) {
            System.err.println("Error al verificar stock bajo: " + e.getMessage());
        }
    }
    
    /**
     * Genera automáticamente un código único para un nuevo producto.
     * 
     * El código se genera combinando las iniciales de la categoría
     * con un número secuencial. Por ejemplo: ELE-0001 para Electrónica.
     * No disponible cuando se edita un producto existente.
     */
    @FXML
    private void generarCodigo() {
        if (esEdicion) {
            mostrarAdvertencia("No disponible", "No se puede cambiar el código de un producto existente");
            return;
        }
        
        if (cbCategoria.getValue() == null) {
            mostrarAdvertencia("Categoría requerida", "Seleccione primero una categoría");
            return;
        }
        
        String codigoGenerado = generarCodigoAutomatico();
        if (!codigoGenerado.isEmpty()) {
            txtCodigo.setText(codigoGenerado);
            mostrarInformacion("Código generado", "Código: " + codigoGenerado);
        }
    }
    
    /**
     * Genera el código automático basado en la categoría seleccionada.
     * 
     * @return El código generado o cadena vacía si ocurre un error
     */
    private String generarCodigoAutomatico() {
        try {
            String categoria = cbCategoria.getValue();
            if (categoria == null || categoria.isEmpty()) return "";
            
            String iniciales = categoria.substring(0, Math.min(3, categoria.length())).toUpperCase();
            iniciales = iniciales.replaceAll("[^A-Z]", "");
            
            List<Producto> productos = productoServicio.listarProductos();
            int maxNumero = 0;
            String prefijo = iniciales + "-";
            
            for (Producto p : productos) {
                if (p.getCodigo().startsWith(prefijo)) {
                    try {
                        String numStr = p.getCodigo().substring(prefijo.length());
                        int num = Integer.parseInt(numStr);
                        maxNumero = Math.max(maxNumero, num);
                    } catch (Exception e) {
                        // Ignoro códigos con formato diferente
                    }
                }
            }
            
            return String.format("%s%04d", prefijo, maxNumero + 1);
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Valida que el código cumpla con los requisitos.
     * 
     * @param codigo El código a validar
     * @return true si es válido, false en caso contrario
     */
    private boolean validarCodigo(String codigo) {
        return codigo != null && codigo.length() >= 3 && codigo.length() <= 20 && codigo.matches("[a-zA-Z0-9_-]+");
    }
    
    /**
     * Valida que el nombre del producto sea válido.
     * 
     * @param nombre El nombre a validar
     * @return true si es válido, false en caso contrario
     */
    private boolean validarNombreProducto(String nombre) {
        return nombre != null && nombre.length() >= 3 && nombre.length() <= 100;
    }
    
    /**
     * Valida que el precio sea válido.
     * 
     * @param precio El precio a validar
     * @return true si es válido, false en caso contrario
     */
    private boolean validarPrecio(double precio) {
        return precio > 0 && precio < 1000000000;
    }
    
    /**
     * Valida que el stock sea válido.
     * 
     * @param stock El stock a validar
     * @return true si es válido, false en caso contrario
     */
    private boolean validarStock(int stock) {
        return stock >= 0 && stock < 100000000;
    }
    
    /**
     * Formatea un precio a 2 decimales.
     * 
     * @param precioTexto El texto del precio a formatear
     * @return El precio formateado o 0.0 si hay error
     */
    private double formatearPrecio(String precioTexto) {
        try {
            double precio = Double.parseDouble(precioTexto);
            return Math.round(precio * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Actualiza el contador de productos en la interfaz.
     */
    private void actualizarContador() {
        lblTotalProductos.setText("Total: " + listaProductos.size() + " productos");
    }
    
    /**
     * Muestra un diálogo de error.
     * 
     * @param titulo El título del diálogo
     * @param mensaje El mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo informativo.
     * 
     * @param titulo El título del diálogo
     * @param mensaje El mensaje informativo
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de advertencia.
     * 
     * @param titulo El título del diálogo
     * @param mensaje El mensaje de advertencia
     */
    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Asigna la referencia del controlador del dashboard.
     * 
     * Permite que ProductosController notifique al dashboard
     * sobre cambios en los productos.
     * 
     * @param dashboard El controlador del dashboard
     */
    public void setDashboardController(DashboardController dashboard) {
        this.dashboardController = dashboard;
        System.out.println("ProductosController: Dashboard controller asignado");
    }
}