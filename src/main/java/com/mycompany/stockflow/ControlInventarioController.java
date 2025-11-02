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
import javafx.geometry.Pos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ControlInventarioController {

    // Dashboard Labels
    @FXML private Label lblTotalProductos;
    @FXML private Label lblStockTotal;
    @FXML private Label lblCantidadAlertas;
    @FXML private Label lblMovimientosHoy;
    @FXML private Label lblCantidadAlertasBadge;

    // Alertas Table
    @FXML private TableView<ProductoStockItem> tblProductosStockBajo;
    @FXML private TableColumn<ProductoStockItem, String> colAlertaCodigo;
    @FXML private TableColumn<ProductoStockItem, String> colAlertaProducto;
    @FXML private TableColumn<ProductoStockItem, Integer> colAlertaStock;
    @FXML private TableColumn<ProductoStockItem, Integer> colAlertaMinimo;
    @FXML private TableColumn<ProductoStockItem, String> colAlertaEstado;
    @FXML private TableColumn<ProductoStockItem, Void> colAlertaAccion;

    // Registro
    @FXML private ComboBox<String> cmbTipoMovimiento;
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtMotivo;
    @FXML private Label lblStockActualNum;
    @FXML private Label lblStockNuevoNum;

    // Historial
    @FXML private TextField txtBuscarProducto;
    @FXML private ComboBox<String> cmbFiltroTipo;
    @FXML private ComboBox<String> cmbFiltroPeriodo;
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

    // Servicios
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
        System.out.println("ControlInventarioController inicializado - Versión mejorada");
        
        configurarDashboard();
        configurarTablaAlertas();
        configurarTablaMovimientos();
        configurarComboBoxes();
        configurarEventos();
        
        cargarDatosIniciales();
    }

    /**
     * Configura el dashboard con estadísticas
     */
    private void configurarDashboard() {
        actualizarDashboard();
    }

    /**
     * Actualiza las estadísticas del dashboard
     */
    private void actualizarDashboard() {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            lblTotalProductos.setText(String.valueOf(productos.size()));
            
            int stockTotal = productos.stream().mapToInt(Producto::getStock).sum();
            lblStockTotal.setText(String.format("%,d", stockTotal));
            
            List<Producto> alertas = inventarioServicio.obtenerProductosStockBajo();
            lblCantidadAlertas.setText(String.valueOf(alertas.size()));
            lblCantidadAlertasBadge.setText(String.valueOf(alertas.size()));
            
            long movimientosHoy = contarMovimientosHoy();
            lblMovimientosHoy.setText(String.valueOf(movimientosHoy));
            
        } catch (Exception e) {
            System.err.println("Error actualizando dashboard: " + e.getMessage());
        }
    }

    /**
     * Cuenta los movimientos de hoy
     */
    private long contarMovimientosHoy() {
        try {
            LocalDateTime hoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            List<MovimientoInventario> movimientos = inventarioServicio.listarMovimientos();
            return movimientos.stream()
                .filter(m -> m.getFecha().isAfter(hoy))
                .count();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Configura la tabla de alertas
     */
    private void configurarTablaAlertas() {
        colAlertaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colAlertaProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAlertaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colAlertaMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colAlertaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        // Estilo personalizado para el estado
        colAlertaEstado.setCellFactory(col -> new TableCell<ProductoStockItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                    String style = "-fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6;";
                    
                    switch (item) {
                        case "SIN STOCK":
                            setStyle(style + "-fx-background-color: #fee; -fx-text-fill: #c62828;");
                            break;
                        case "CRÍTICO":
                            setStyle(style + "-fx-background-color: #ffe0b2; -fx-text-fill: #e65100;");
                            break;
                        default:
                            setStyle(style + "-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                    }
                }
            }
        });
        
        // Columna de acción con botón
        colAlertaAccion.setCellFactory(col -> new TableCell<ProductoStockItem, Void>() {
            private final Button btnReabastecer = new Button("Reabastecer");
            
            {
                btnReabastecer.setStyle(
                    "-fx-background-color: #2e7d32; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 11; " +
                    "-fx-font-weight: 600; " +
                    "-fx-padding: 6 12; " +
                    "-fx-background-radius: 6; " +
                    "-fx-cursor: hand;"
                );
                
                btnReabastecer.setOnAction(e -> {
                    ProductoStockItem item = getTableView().getItems().get(getIndex());
                    reabastecerRapido(item);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnReabastecer);
                setAlignment(Pos.CENTER);
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
        
        // Estilo para el tipo
        colTipo.setCellFactory(col -> new TableCell<MovimientoItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                    String baseStyle = "-fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6;";
                    
                    switch (item) {
                        case "ENTRADA":
                            setStyle(baseStyle + "-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32;");
                            break;
                        case "SALIDA":
                            setStyle(baseStyle + "-fx-background-color: #ffebee; -fx-text-fill: #c62828;");
                            break;
                        case "AJUSTE":
                            setStyle(baseStyle + "-fx-background-color: #fff3e0; -fx-text-fill: #e65100;");
                            break;
                    }
                }
            }
        });
        
        // Estilo para la cantidad
        colCantidad.setCellFactory(col -> new TableCell<MovimientoItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                    if (item.startsWith("+")) {
                        setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 13;");
                    } else if (item.startsWith("-")) {
                        setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold; -fx-font-size: 13;");
                    } else {
                        setStyle("-fx-font-weight: 600; -fx-font-size: 13;");
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
        cmbTipoMovimiento.setItems(FXCollections.observableArrayList("ENTRADA", "AJUSTE"));
        
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
        
        cmbFiltroTipo.setItems(FXCollections.observableArrayList("Todos", "ENTRADA", "SALIDA", "AJUSTE"));
        cmbFiltroTipo.setValue("Todos");
        
        cmbFiltroPeriodo.setItems(FXCollections.observableArrayList(
            "Todos", "Hoy", "Esta Semana", "Este Mes", "Últimos 3 Meses"
        ));
        cmbFiltroPeriodo.setValue("Todos");
    }

    /**
     * Configura eventos
     */
    private void configurarEventos() {
        cmbProducto.setOnAction(e -> actualizarVistaPrevia());
        txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaPrevia());
        cmbTipoMovimiento.setOnAction(e -> actualizarVistaPrevia());
        
        // Búsqueda en tiempo real
        txtBuscarProducto.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 2) {
                buscarMovimientos();
            } else if (newVal == null || newVal.isEmpty()) {
                movimientosFiltrados.setAll(movimientoItems);
            }
        });
    }

    /**
     * Carga datos iniciales
     */
    private void cargarDatosIniciales() {
        cargarProductos();
        cargarProductosStockBajo();
        cargarMovimientos();
        actualizarDashboard();
    }

    /**
     * Carga productos
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
            
        } catch (Exception e) {
            mostrarError("Error al cargar alertas: " + e.getMessage());
        }
    }

    /**
     * Carga movimientos
     */
    private void cargarMovimientos() {
        try {
            movimientoItems.clear();
            List<MovimientoInventario> movimientos = inventarioServicio.listarMovimientos();
            
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
     * Actualiza vista previa de stock
     */
    private void actualizarVistaPrevia() {
        Producto producto = cmbProducto.getValue();
        String tipo = cmbTipoMovimiento.getValue();
        
        if (producto == null) {
            lblStockActualNum.setText("--");
            lblStockNuevoNum.setText("--");
            lblStockNuevoNum.setStyle("-fx-text-fill: #64748b; -fx-font-size: 20; -fx-font-weight: bold;");
            return;
        }
        
        int stockActual = producto.getStock();
        lblStockActualNum.setText(String.format("%,d", stockActual));
        
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            int nuevoStock = stockActual;
            
            if ("ENTRADA".equals(tipo)) {
                nuevoStock = stockActual + cantidad;
            } else if ("AJUSTE".equals(tipo)) {
                nuevoStock = stockActual + cantidad;
            }
            
            lblStockNuevoNum.setText(String.format("%,d", nuevoStock));
            
            if (nuevoStock < 0) {
                lblStockNuevoNum.setStyle("-fx-text-fill: #c62828; -fx-font-size: 20; -fx-font-weight: bold;");
            } else if (nuevoStock <= producto.getStockMinimo()) {
                lblStockNuevoNum.setStyle("-fx-text-fill: #f57c00; -fx-font-size: 20; -fx-font-weight: bold;");
            } else {
                lblStockNuevoNum.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 20; -fx-font-weight: bold;");
            }
            
        } catch (NumberFormatException e) {
            lblStockNuevoNum.setText("--");
            lblStockNuevoNum.setStyle("-fx-text-fill: #64748b; -fx-font-size: 20; -fx-font-weight: bold;");
        }
    }

    /**
     * Registra movimiento
     */
    @FXML
    private void registrarMovimiento() {
        if (cmbTipoMovimiento.getValue() == null) {
            mostrarAdvertencia("Debe seleccionar un tipo de movimiento");
            return;
        }
        
        Producto producto = cmbProducto.getValue();
        if (producto == null) {
            mostrarAdvertencia("Debe seleccionar un producto");
            return;
        }
        
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad == 0) {
                mostrarAdvertencia("La cantidad debe ser diferente de 0");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("La cantidad debe ser un número válido");
            return;
        }
        
        String motivo = txtMotivo.getText().trim();
        if (motivo.isEmpty()) {
            mostrarAdvertencia("Debe ingresar un motivo");
            return;
        }
        
        try {
            MovimientoInventario movimiento = inventarioServicio.registrarMovimiento(
                producto, cmbTipoMovimiento.getValue(), cantidad, motivo
            );
            
            mostrarExito(String.format(
                "Movimiento registrado exitosamente\n\n" +
                "Código: %s\nProducto: %s\nCantidad: %s\nNuevo Stock: %d",
                movimiento.getCodigo(),
                movimiento.getNombreProducto(),
                movimiento.getSignoCantidad(),
                movimiento.getStockNuevo()
            ));
            
            refrescarTodo();
            limpiarFormulario();
            
        } catch (Exception e) {
            mostrarError("Error al registrar movimiento: " + e.getMessage());
        }
    }

    /**
     * Limpia el formulario
     */
    @FXML
    private void limpiarFormulario() {
        cmbTipoMovimiento.setValue(null);
        cmbProducto.setValue(null);
        txtCantidad.clear();
        txtMotivo.clear();
        lblStockActualNum.setText("--");
        lblStockNuevoNum.setText("--");
        lblStockNuevoNum.setStyle("-fx-text-fill: #64748b; -fx-font-size: 20; -fx-font-weight: bold;");
    }

    /**
     * Busca movimientos con filtros
     */
    @FXML
    private void buscarMovimientos() {
        String buscar = txtBuscarProducto.getText().trim().toLowerCase();
        String filtroTipo = cmbFiltroTipo.getValue();
        String filtroPeriodo = cmbFiltroPeriodo.getValue();
        
        LocalDateTime fechaLimite = calcularFechaLimite(filtroPeriodo);
        
        List<MovimientoItem> resultados = movimientoItems.stream()
            .filter(item -> {
                boolean coincideProducto = buscar.isEmpty() || 
                    item.getNombreProducto().toLowerCase().contains(buscar) ||
                    item.getCodigoProducto().toLowerCase().contains(buscar);
                
                boolean coincideTipo = "Todos".equals(filtroTipo) || 
                    item.getTipo().equals(filtroTipo);
                
                boolean coincidePeriodo = fechaLimite == null ||
                    item.getMovimiento().getFecha().isAfter(fechaLimite);
                
                return coincideProducto && coincideTipo && coincidePeriodo;
            })
            .collect(Collectors.toList());
        
        movimientosFiltrados.setAll(resultados);
        lblTotalMovimientos.setText(String.valueOf(resultados.size()));
    }

    /**
     * Calcula fecha límite según período
     */
    private LocalDateTime calcularFechaLimite(String periodo) {
        if (periodo == null || "Todos".equals(periodo)) return null;
        
        LocalDateTime ahora = LocalDateTime.now();
        switch (periodo) {
            case "Hoy":
                return ahora.withHour(0).withMinute(0).withSecond(0);
            case "Esta Semana":
                return ahora.minusWeeks(1);
            case "Este Mes":
                return ahora.minusMonths(1);
            case "Últimos 3 Meses":
                return ahora.minusMonths(3);
            default:
                return null;
        }
    }

    /**
     * Actualiza historial
     */
    @FXML
    private void actualizarHistorial() {
        txtBuscarProducto.clear();
        cmbFiltroTipo.setValue("Todos");
        cmbFiltroPeriodo.setValue("Todos");
        refrescarTodo();
        mostrarInformacion("Datos actualizados correctamente");
    }

    /**
     * Reabastece producto rápido
     */
    private void reabastecerRapido(ProductoStockItem item) {
        cmbTipoMovimiento.setValue("ENTRADA");
        
        // Buscar el producto en la lista
        for (Producto p : cmbProducto.getItems()) {
            if (p.getCodigo().equals(item.getCodigo())) {
                cmbProducto.setValue(p);
                break;
            }
        }
        
        int cantidadSugerida = Math.max(item.getStockMinimo() * 2 - item.getStock(), 1);
        txtCantidad.setText(String.valueOf(cantidadSugerida));
        txtMotivo.setText("Reabastecimiento de stock crítico");
        
        mostrarInformacion("Formulario prellenado. Verifique los datos y presione Registrar.");
    }

    /**
     * Exporta alertas
     */
    @FXML
    private void exportarAlertas() {
        if (productosStockBajo.isEmpty()) {
            mostrarInformacion("No hay alertas para exportar");
            return;
        }
        
        mostrarInformacion("Función de exportación no implementada aún.\n" +
            "Se exportarían " + productosStockBajo.size() + " alertas.");
    }

    /**
     * Notifica alertas
     */
    @FXML
    private void notificarAlertas() {
        if (productosStockBajo.isEmpty()) {
            mostrarInformacion("No hay alertas para notificar");
            return;
        }
        
        mostrarInformacion("Función de notificación no implementada aún.\n" +
            "Se notificarían " + productosStockBajo.size() + " alertas críticas.");
    }

    /**
     * Genera reporte de inventario
     */
    @FXML
    private void generarReporteInventario() {
        mostrarInformacion("Función de reporte no implementada aún.\n" +
            "Generará un reporte completo del estado del inventario.");
    }

    /**
     * Refresca todos los datos
     */
    private void refrescarTodo() {
        cargarMovimientos();
        cargarProductosStockBajo();
        cargarProductos();
        actualizarDashboard();
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

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("✓ Éxito");
        alert.setHeaderText("Operación Exitosa");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Clases internas
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
        public String getNombre() { return nombre.get(); }
        public int getStock() { return stock.get(); }
        public int getStockMinimo() { return stockMinimo.get(); }
        public String getEstado() { return estado.get(); }
    }

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
        public String getFecha() { return fecha.get(); }
        public String getTipo() { return tipo.get(); }
        public String getNombreProducto() { return nombreProducto.get(); }
        public String getCodigoProducto() { return codigoProducto.get(); }
        public String getCantidadConSigno() { return cantidadConSigno.get(); }
        public int getStockAnterior() { return stockAnterior.get(); }
        public int getStockNuevo() { return stockNuevo.get(); }
        public String getMotivo() { return motivo.get(); }
        public MovimientoInventario getMovimiento() { return movimiento; }
    }
}