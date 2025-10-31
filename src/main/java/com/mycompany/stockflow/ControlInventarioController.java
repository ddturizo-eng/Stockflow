/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.stockflow;

import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Modelo.MovimientoInventario;
import com.mycompany.stockflow.Logica.InventarioServicio;
import com.mycompany.stockflow.Logica.ProductoServicio;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class ControlInventarioController {

    // FXML Components - Alertas
    @FXML private TableView<ProductoStockItem> tblProductosStockBajo;
    @FXML private TableColumn<ProductoStockItem, String> colAlertaCodigo;
    @FXML private TableColumn<ProductoStockItem, String> colAlertaProducto;
    @FXML private TableColumn<ProductoStockItem, Integer> colAlertaStock;
    @FXML private TableColumn<ProductoStockItem, Integer> colAlertaMinimo;
    @FXML private TableColumn<ProductoStockItem, String> colAlertaEstado;
    @FXML private Label lblCantidadAlertas;

    // FXML Components - Registro
    @FXML private ComboBox<String> cmbTipoMovimiento;
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtMotivo;
    @FXML private Label lblStockActual;
    @FXML private Label lblStockNuevo;

    // FXML Components - Historial
    @FXML private TextField txtBuscarProducto;
    @FXML private ComboBox<String> cmbFiltroTipo;
    @FXML private TableView<MovimientoItem> tblMovimientos;
    @FXML private TableColumn<MovimientoItem, String> colCodigo;
    @FXML private TableColumn<MovimientoItem, String> colFecha;
    @FXML private TableColumn<MovimientoItem, String> colTipo;
    @FXML private TableColumn<MovimientoItem, String> colProducto;
    @FXML private TableColumn<MovimientoItem, String> colCantidad;
    @FXML private TableColumn<MovimientoItem, Integer> colStockAnterior;
    @FXML private TableColumn<MovimientoItem, Integer> colStockNuevo;
    @FXML private TableColumn<MovimientoItem, String> colMotivo;
    @FXML private Label lblTotalMovimientos;

    // Servicios y datos
    private InventarioServicio inventarioServicio;
    private ProductoServicio productoServicio;
    private ObservableList<ProductoStockItem> productosStockBajo;
    private ObservableList<MovimientoItem> movimientoItems;
    private ObservableList<MovimientoItem> movimientosFiltrados;

    public ControlInventarioController() {
        this.inventarioServicio = new InventarioServicio();
        this.productoServicio = new ProductoServicio();
        this.productosStockBajo = FXCollections.observableArrayList();
        this.movimientoItems = FXCollections.observableArrayList();
        this.movimientosFiltrados = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        System.out.println("ControlInventarioController inicializado");
        
        configurarTablaAlertas();
        configurarTablaMovimientos();
        configurarComboBoxes();
        configurarEventos();
        
        cargarProductosStockBajo();
        cargarMovimientos();
    }

    /**
     * Configura la tabla de alertas de stock bajo
     */
    private void configurarTablaAlertas() {
        colAlertaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colAlertaProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAlertaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colAlertaMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colAlertaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        // Colorear el estado
        colAlertaEstado.setCellFactory(col -> new TableCell<ProductoStockItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("CRÍTICO")) {
                        setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #F57C00; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        tblProductosStockBajo.setItems(productosStockBajo);
    }

    /**
     * Configura la tabla de movimientos
     */
    private void configurarTablaMovimientos() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadConSigno"));
        colStockAnterior.setCellValueFactory(new PropertyValueFactory<>("stockAnterior"));
        colStockNuevo.setCellValueFactory(new PropertyValueFactory<>("stockNuevo"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        
        // Colorear el tipo
        colTipo.setCellFactory(col -> new TableCell<MovimientoItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "ENTRADA":
                            setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                            break;
                        case "SALIDA":
                            setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                            break;
                        case "AJUSTE":
                            setStyle("-fx-text-fill: #F39C12; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });
        
        // Colorear la cantidad
        colCantidad.setCellFactory(col -> new TableCell<MovimientoItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.startsWith("+")) {
                        setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                    } else if (item.startsWith("-")) {
                        setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        tblMovimientos.setItems(movimientosFiltrados);
    }

    /**
     * Configura los ComboBox
     */
    private void configurarComboBoxes() {
        // ComboBox Tipo de Movimiento
        cmbTipoMovimiento.setItems(FXCollections.observableArrayList("ENTRADA", "AJUSTE"));
        
        // ComboBox Productos
        cmbProducto.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto producto) {
                if (producto == null) return null;
                return String.format("%s - %s (Stock: %d)", 
                    producto.getCodigo(), producto.getNombre(), producto.getStock());
            }
            
            @Override
            public Producto fromString(String string) {
                return null;
            }
        });
        
        cargarProductos();
        
        // ComboBox Filtro Tipo
        cmbFiltroTipo.setItems(FXCollections.observableArrayList("Todos", "ENTRADA", "SALIDA", "AJUSTE"));
        cmbFiltroTipo.setValue("Todos");
    }

    /**
     * Configura eventos
     */
    private void configurarEventos() {
        // Actualizar vista previa al cambiar producto o cantidad
        cmbProducto.setOnAction(e -> actualizarVistaPrevia());
        txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaPrevia());
        cmbTipoMovimiento.setOnAction(e -> actualizarVistaPrevia());
    }

    /**
     * Carga productos en el ComboBox
     */
    private void cargarProductos() {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            cmbProducto.setItems(FXCollections.observableArrayList(productos));
        } catch (Exception e) {
            mostrarError("Error al cargar productos: " + e.getMessage());
        }
    }

    /**
     * Carga productos con stock bajo
     */
    private void cargarProductosStockBajo() {
        try {
            productosStockBajo.clear();
            List<Producto> productos = inventarioServicio.obtenerProductosStockBajo();
            
            for (Producto p : productos) {
                productosStockBajo.add(new ProductoStockItem(p));
            }
            
            lblCantidadAlertas.setText(String.valueOf(productos.size()));
            
        } catch (Exception e) {
            mostrarError("Error al cargar alertas: " + e.getMessage());
        }
    }

    /**
     * Carga todos los movimientos
     */
    private void cargarMovimientos() {
        try {
            movimientoItems.clear();
            List<MovimientoInventario> movimientos = inventarioServicio.listarMovimientos();
            
            // Ordenar por fecha descendente (más recientes primero)
            movimientos.sort((m1, m2) -> m2.getFecha().compareTo(m1.getFecha()));
            
            for (MovimientoInventario m : movimientos) {
                movimientoItems.add(new MovimientoItem(m));
            }
            
            movimientosFiltrados.setAll(movimientoItems);
            lblTotalMovimientos.setText(String.valueOf(movimientos.size()));
            
        } catch (Exception e) {
            mostrarError("Error al cargar movimientos: " + e.getMessage());
        }
    }

    /**
     * Actualiza la vista previa del nuevo stock
     */
    private void actualizarVistaPrevia() {
        Producto productoSeleccionado = cmbProducto.getValue();
        String tipoMovimiento = cmbTipoMovimiento.getValue();
        
        if (productoSeleccionado == null) {
            lblStockActual.setText("Stock actual: --");
            lblStockNuevo.setText("Nuevo stock: --");
            return;
        }
        
        int stockActual = productoSeleccionado.getStock();
        lblStockActual.setText("Stock actual: " + stockActual);
        
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            int nuevoStock = stockActual;
            
            if ("ENTRADA".equals(tipoMovimiento)) {
                nuevoStock = stockActual + cantidad;
            } else if ("AJUSTE".equals(tipoMovimiento)) {
                nuevoStock = stockActual + cantidad;
            }
            
            lblStockNuevo.setText("Nuevo stock: " + nuevoStock);
            
            if (nuevoStock < 0) {
                lblStockNuevo.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
            } else if (nuevoStock <= productoSeleccionado.getStockMinimo()) {
                lblStockNuevo.setStyle("-fx-text-fill: #F39C12; -fx-font-weight: bold;");
            } else {
                lblStockNuevo.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
            }
            
        } catch (NumberFormatException e) {
            lblStockNuevo.setText("Nuevo stock: --");
        }
    }

    /**
     * Registra un nuevo movimiento de inventario
     */
    @FXML
    private void registrarMovimiento() {
        // Validaciones
        if (cmbTipoMovimiento.getValue() == null) {
            mostrarAdvertencia("Debe seleccionar un tipo de movimiento");
            cmbTipoMovimiento.requestFocus();
            return;
        }
        
        Producto productoSeleccionado = cmbProducto.getValue();
        if (productoSeleccionado == null) {
            mostrarAdvertencia("Debe seleccionar un producto");
            cmbProducto.requestFocus();
            return;
        }
        
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad == 0) {
                mostrarAdvertencia("La cantidad debe ser diferente de 0");
                txtCantidad.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("La cantidad debe ser un número válido");
            txtCantidad.requestFocus();
            return;
        }
        
        String motivo = txtMotivo.getText().trim();
        if (motivo.isEmpty()) {
            mostrarAdvertencia("Debe ingresar un motivo");
            txtMotivo.requestFocus();
            return;
        }
        
        try {
            String tipoMovimiento = cmbTipoMovimiento.getValue();
            MovimientoInventario movimiento = inventarioServicio.registrarMovimiento(
                productoSeleccionado, tipoMovimiento, cantidad, motivo
            );
            
            String mensaje = String.format(
                "✓ Movimiento registrado exitosamente\n\n" +
                "Código: %s\n" +
                "Tipo: %s\n" +
                "Producto: %s\n" +
                "Cantidad: %s\n" +
                "Stock anterior: %d\n" +
                "Stock nuevo: %d",
                movimiento.getCodigo(),
                movimiento.getTipoMovimiento(),
                movimiento.getNombreProducto(),
                movimiento.getSignoCantidad(),
                movimiento.getStockAnterior(),
                movimiento.getStockNuevo()
            );
            
            mostrarInformacion(mensaje);
            
            // Actualizar vistas
            cargarMovimientos();
            cargarProductosStockBajo();
            cargarProductos();
            limpiarFormulario();
            
        } catch (Exception e) {
            mostrarError("Error al registrar movimiento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Limpia el formulario de registro
     */
    @FXML
    private void limpiarFormulario() {
        cmbTipoMovimiento.setValue(null);
        cmbProducto.setValue(null);
        txtCantidad.clear();
        txtMotivo.clear();
        lblStockActual.setText("Stock actual: --");
        lblStockNuevo.setText("Nuevo stock: --");
        lblStockNuevo.setStyle("");
    }

    /**
     * Busca movimientos según los filtros
     */
    @FXML
    private void buscarMovimientos() {
        String buscarProducto = txtBuscarProducto.getText().trim().toLowerCase();
        String filtroTipo = cmbFiltroTipo.getValue();
        
        List<MovimientoItem> resultados = movimientoItems.stream()
            .filter(item -> {
                boolean coincideProducto = buscarProducto.isEmpty() || 
                    item.getNombreProducto().toLowerCase().contains(buscarProducto) ||
                    item.getCodigoProducto().toLowerCase().contains(buscarProducto);
                
                boolean coincideTipo = "Todos".equals(filtroTipo) || 
                    item.getTipo().equals(filtroTipo);
                
                return coincideProducto && coincideTipo;
            })
            .collect(Collectors.toList());
        
        movimientosFiltrados.setAll(resultados);
        lblTotalMovimientos.setText(String.valueOf(resultados.size()));
        
        if (resultados.isEmpty()) {
            mostrarInformacion("No se encontraron movimientos con los criterios especificados");
        }
    }

    /**
     * Actualiza el historial de movimientos
     */
    @FXML
    private void actualizarHistorial() {
        txtBuscarProducto.clear();
        cmbFiltroTipo.setValue("Todos");
        cargarMovimientos();
        cargarProductosStockBajo();
        mostrarInformacion("Lista actualizada");
    }

    // Métodos de alertas
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Clase interna para items de productos con stock bajo
     */
    public static class ProductoStockItem {
        private final SimpleStringProperty codigo;
        private final SimpleStringProperty nombre;
        private final SimpleIntegerProperty stock;
        private final SimpleIntegerProperty stockMinimo;
        private final SimpleStringProperty estado;

        public ProductoStockItem(Producto producto) {
            this.codigo = new SimpleStringProperty(producto.getCodigo());
            this.nombre = new SimpleStringProperty(producto.getNombre());
            this.stock = new SimpleIntegerProperty(producto.getStock());
            this.stockMinimo = new SimpleIntegerProperty(producto.getStockMinimo());
            
            String estadoTexto;
            if (producto.getStock() == 0) {
                estadoTexto = "SIN STOCK";
            } else if (producto.getStock() < producto.getStockMinimo()) {
                estadoTexto = "CRÍTICO";
            } else {
                estadoTexto = "BAJO";
            }
            this.estado = new SimpleStringProperty(estadoTexto);
        }

        public String getCodigo() { return codigo.get(); }
        public SimpleStringProperty codigoProperty() { return codigo; }
        
        public String getNombre() { return nombre.get(); }
        public SimpleStringProperty nombreProperty() { return nombre; }
        
        public int getStock() { return stock.get(); }
        public SimpleIntegerProperty stockProperty() { return stock; }
        
        public int getStockMinimo() { return stockMinimo.get(); }
        public SimpleIntegerProperty stockMinimoProperty() { return stockMinimo; }
        
        public String getEstado() { return estado.get(); }
        public SimpleStringProperty estadoProperty() { return estado; }
    }

    /**
     * Clase interna para items de movimientos
     */
    public static class MovimientoItem {
        private final MovimientoInventario movimiento;
        private final SimpleStringProperty codigo;
        private final SimpleStringProperty fecha;
        private final SimpleStringProperty tipo;
        private final SimpleStringProperty nombreProducto;
        private final SimpleStringProperty codigoProducto;
        private final SimpleStringProperty cantidadConSigno;
        private final SimpleIntegerProperty stockAnterior;
        private final SimpleIntegerProperty stockNuevo;
        private final SimpleStringProperty motivo;

        public MovimientoItem(MovimientoInventario movimiento) {
            this.movimiento = movimiento;
            this.codigo = new SimpleStringProperty(movimiento.getCodigo());
            this.fecha = new SimpleStringProperty(movimiento.getFechaFormateada());
            this.tipo = new SimpleStringProperty(movimiento.getTipoMovimiento());
            this.nombreProducto = new SimpleStringProperty(movimiento.getNombreProducto());
            this.codigoProducto = new SimpleStringProperty(movimiento.getCodigoProducto());
            this.cantidadConSigno = new SimpleStringProperty(movimiento.getSignoCantidad());
            this.stockAnterior = new SimpleIntegerProperty(movimiento.getStockAnterior());
            this.stockNuevo = new SimpleIntegerProperty(movimiento.getStockNuevo());
            this.motivo = new SimpleStringProperty(movimiento.getMotivo() != null ? movimiento.getMotivo() : "");
        }

        public String getCodigo() { return codigo.get(); }
        public SimpleStringProperty codigoProperty() { return codigo; }
        
        public String getFecha() { return fecha.get(); }
        public SimpleStringProperty fechaProperty() { return fecha; }
        
        public String getTipo() { return tipo.get(); }
        public SimpleStringProperty tipoProperty() { return tipo; }
        
        public String getNombreProducto() { return nombreProducto.get(); }
        public SimpleStringProperty nombreProductoProperty() { return nombreProducto; }
        
        public String getCodigoProducto() { return codigoProducto.get(); }
        public SimpleStringProperty codigoProductoProperty() { return codigoProducto; }
        
        public String getCantidadConSigno() { return cantidadConSigno.get(); }
        public SimpleStringProperty cantidadConSignoProperty() { return cantidadConSigno; }
        
        public int getStockAnterior() { return stockAnterior.get(); }
        public SimpleIntegerProperty stockAnteriorProperty() { return stockAnterior; }
        
        public int getStockNuevo() { return stockNuevo.get(); }
        public SimpleIntegerProperty stockNuevoProperty() { return stockNuevo; }
        
        public String getMotivo() { return motivo.get(); }
        public SimpleStringProperty motivoProperty() { return motivo; }
        
        public MovimientoInventario getMovimiento() { return movimiento; }
    }
}
