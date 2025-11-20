   /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

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
    
    // Formulario
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
    
    // Nuevos controles para la imagen
    @FXML private ImageView imgVistaPrevia;
    @FXML private Button btnSeleccionarImagen;
    @FXML private Button btnTomarFoto;
    @FXML private Button btnEliminarImagen;
    
    private ProductoServicio productoServicio;
    private ObservableList<Producto> listaProductos;
    private Producto productoSeleccionado;
    private boolean esEdicion = false;
    
    // Variables para manejo de imagen
    private File archivoImagenSeleccionado;
    private Image imagenCapturada;
    private boolean imagenModificada = false;
    private DashboardController dashboardController;
    
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
    
    private void configurarComboBoxCategorias() {
        cbCategoria.setItems(FXCollections.observableArrayList(
            "Electrónica", "Ropa y Accesorios", "Alimentos y Bebidas",
            "Hogar y Jardín", "Deportes", "Juguetes", "Libros",
            "Salud y Belleza", "Automotriz", "Otros"
        ));
    }
    
    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarProducto());
    }
    
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
    
    // NUEVOS MÉTODOS PARA MANEJO DE IMÁGENES
    
    /**
     * Configura la vista previa de imagen con imagen por defecto
     */
    private void configurarVistaPrevia() {
        cargarImagenPorDefecto();
    }
    
    /**
     * Carga la imagen por defecto en la vista previa
     */
    private void cargarImagenPorDefecto() {
        Image imagenDefault = ImagenProductoUtil.obtenerImagenPorDefecto();
        if (imagenDefault != null) {
            imgVistaPrevia.setImage(imagenDefault);
        }
    }
    
    /**
     * Permite al usuario seleccionar una imagen desde el sistema de archivos
     */
    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen del Producto");
        
        // Configurar filtros de extensión
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.bmp"),
            new FileChooser.ExtensionFilter("PNG", "*.png"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );
        
        // Abrir diálogo de selección
        Stage stage = (Stage) btnSeleccionarImagen.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);
        
        if (archivo != null) {
            try {
                // Cargar y mostrar la imagen seleccionada
                Image imagen = new Image(archivo.toURI().toString());
                imgVistaPrevia.setImage(imagen);
                
                // Guardar referencia al archivo
                archivoImagenSeleccionado = archivo;
                imagenCapturada = null;
                imagenModificada = true;
                
                mostrarInformacion("Imagen seleccionada", "La imagen se guardará al crear/actualizar el producto");
                
            } catch (Exception e) {
                mostrarError("Error al cargar imagen", "No se pudo cargar la imagen seleccionada: " + e.getMessage());
            }
        }
    }
    
    /**
     * Permite tomar una foto con la cámara (si está disponible)
     */
    @FXML
    private void tomarFoto() {
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
            // Capturar foto con la cámara
            Image fotoCapturada = CamaraServicio.capturarFoto();
            
            if (fotoCapturada != null) {
                imgVistaPrevia.setImage(fotoCapturada);
                imagenCapturada = fotoCapturada;
                archivoImagenSeleccionado = null;
                imagenModificada = true;
                
                mostrarInformacion("Foto capturada", 
                    "La foto se guardará al crear/actualizar el producto");
            } else {
                System.out.println("Captura de foto cancelada por el usuario");
            }
            
        } catch (Exception e) {
            System.err.println("Error al capturar foto: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al capturar foto", 
                "No se pudo capturar la foto: " + e.getMessage());
        }
    }
    
    /**
     * Elimina la imagen seleccionada y restaura la imagen por defecto
     */
    @FXML
    private void eliminarImagenPrevia() {
        cargarImagenPorDefecto();
        archivoImagenSeleccionado = null;
        imagenCapturada = null;
        imagenModificada = true;
    }
    
    /**
     * Carga la imagen de un producto en la vista previa
     */
    private void cargarImagenProducto(Producto producto) {
        if (producto.tieneImagen()) {
            Image imagen = ImagenProductoUtil.cargarImagen(producto.getRutaImagen());
            if (imagen != null) {
                imgVistaPrevia.setImage(imagen);
            } else {
                cargarImagenPorDefecto();
            }
        } else {
            cargarImagenPorDefecto();
        }
        
        archivoImagenSeleccionado = null;
        imagenCapturada = null;
        imagenModificada = false;
    }
    
    // FIN DE MÉTODOS DE MANEJO DE IMÁGENES
    
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
        
        // Cargar imagen del producto
        cargarImagenProducto(producto);
        
        formularioContainer.setVisible(true);
    }
    
    @FXML
 
            private void guardarProducto() {
                if (!validarFormulario()) return;

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

                    // GUARDAR IMAGEN SI FUE MODIFICADA
                    if (imagenModificada) {
                        try {
                            String rutaImagen = null;

                            if (archivoImagenSeleccionado != null) {
                                rutaImagen = ImagenProductoUtil.copiarImagen(
                                    archivoImagenSeleccionado, 
                                    producto.getCodigo()
                                );
                            } else if (imagenCapturada != null) {
                                rutaImagen = ImagenProductoUtil.guardarImagen(
                                    imagenCapturada, 
                                    producto.getCodigo()
                                );
                            }

                            if (esEdicion && producto.tieneImagen() && rutaImagen != null) {
                                ImagenProductoUtil.eliminarImagen(producto.getRutaImagen());
                            }

                            producto.setRutaImagen(rutaImagen);

                        } catch (Exception e) {
                            System.err.println("Error al guardar imagen: " + e.getMessage());
                            mostrarAdvertencia("Advertencia", "El producto se guardará sin imagen");
                        }
                    }

                    if (esEdicion) {
                        productoServicio.actualizarProducto(producto);
                        mostrarInformacion("Éxito", "Producto actualizado correctamente");
                    } else {
                        productoServicio.crearProducto(producto);
                        mostrarInformacion("Éxito", "Producto agregado correctamente");
                    }

                    cerrarFormulario();
                    cargarProductos();
                    verificarStockBajo();

                    // ✅ NUEVO: Notificar cambios
                    if (dashboardController != null) {
                        dashboardController.notificarCambioEnProductos();
                    }

                } catch (Exception e) {
                    mostrarError("Error al guardar", e.getMessage());
                }
            }
    
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

                        // ✅ NUEVO: Actualizar gráficas del dashboard
                        notificarCambiosAlDashboard();

                    } catch (Exception e) {
                        mostrarError("Error al eliminar", e.getMessage());
                    }
                }
            }
            /**
            * Notifica al controlador de Inteligencia de Negocios que hay cambios en los productos
            */
        private void notificarCambiosAlDashboard() {
            try {
                // Obtener la ventana principal (Scene)
                javafx.scene.Scene scene = tablaProductos.getScene();
                if (scene != null) {
                    javafx.stage.Window window = scene.getWindow();

                    // Intentar acceder al controlador principal de la app
                    // Si tienes un MainController o principal, aquí es donde notificarías
                    System.out.println("📊 Cambios en productos detectados - Dashboard se actualizará");
                }
            } catch (Exception e) {
                System.err.println("No se pudo notificar cambios: " + e.getMessage());
            }
        }

    @FXML
    private void cerrarFormulario() {
        formularioContainer.setVisible(false);
        limpiarFormulario();
        txtCodigo.setDisable(false);
        archivoImagenSeleccionado = null;
        imagenCapturada = null;
        imagenModificada = false;
    }
    
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
                    mensaje.append("• ").append(prod).append("\n");
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
    
    private boolean validarCodigo(String codigo) {
        return codigo != null && codigo.length() >= 3 && codigo.length() <= 20 && codigo.matches("[a-zA-Z0-9_-]+");
    }
    
    private boolean validarNombreProducto(String nombre) {
        return nombre != null && nombre.length() >= 3 && nombre.length() <= 100;
    }
    
    private boolean validarPrecio(double precio) {
        return precio > 0 && precio < 1000000000;
    }
    
    private boolean validarStock(int stock) {
        return stock >= 0 && stock < 100000000;
    }
    
    private double formatearPrecio(String precioTexto) {
        try {
            double precio = Double.parseDouble(precioTexto);
            return Math.round(precio * 100.0) / 100.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private void actualizarContador() {
        lblTotalProductos.setText("Total: " + listaProductos.size() + " productos");
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

            /**
         * El Dashboard inyecta su referencia para que podamos notificar cambios
         */
        public void setDashboardController(DashboardController dashboard) {
            this.dashboardController = dashboard;
            System.out.println("✓ ProductosController: Dashboard controller asignado");
        }
}