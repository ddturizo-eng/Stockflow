package com.mycompany.stockflow;

import com.mycompany.stockflow.Modelo.Cliente;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Modelo.Venta;
import com.mycompany.stockflow.Modelo.DetalleVenta;
import com.mycompany.stockflow.Modelo.Factura;
import com.mycompany.stockflow.Logica.ClienteServicio;
import com.mycompany.stockflow.Logica.VentaServicio;
import com.mycompany.stockflow.Logica.ProductoServicio;
import com.mycompany.stockflow.Logica.FacturacionServicio;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VentaController {

    @FXML private TextField txtCodigoVenta;
    @FXML private TextField txtFecha;
    @FXML private TextField txtBuscarCliente;
    @FXML private Button btnBuscarCliente;
    @FXML private Button btnNuevoCliente;
    
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colClienteCedula;
    @FXML private TableColumn<Cliente, String> colClienteNombre;
    @FXML private TableColumn<Cliente, String> colClienteTelefono;
    @FXML private TableColumn<Cliente, String> colClienteEmail;
    @FXML private TableColumn<Cliente, Void> colClienteSeleccionar;
    @FXML private Label lblClienteSeleccionado;
    
    @FXML private TextField txtBuscarProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtDescuento;
    @FXML private Button btnBuscarProducto;
    
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colProductoCodigo;
    @FXML private TableColumn<Producto, String> colProductoNombre;
    @FXML private TableColumn<Producto, String> colProductoCategoria;
    @FXML private TableColumn<Producto, Double> colProductoPrecio;
    @FXML private TableColumn<Producto, Integer> colProductoStock;
    @FXML private TableColumn<Producto, Void> colProductoAgregar;

    @FXML private TableView<DetalleVentaItem> tblDetalleVenta;
    @FXML private TableColumn<DetalleVentaItem, Integer> colNumero;
    @FXML private TableColumn<DetalleVentaItem, String> colProducto;
    @FXML private TableColumn<DetalleVentaItem, Integer> colCantidad;
    @FXML private TableColumn<DetalleVentaItem, Double> colDescuento;
    @FXML private TableColumn<DetalleVentaItem, Double> colSubtotal;

    @FXML private TextField txtIVA;
    @FXML private Label lblTotalPagar;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private TextField txtMontoRecibido;
    @FXML private TextField txtCambio;
    @FXML private Button btnCancelarVenta;
    @FXML private Button btnGuardarVenta;

    private ClienteServicio clienteServicio;
    private VentaServicio ventaServicio;
    private ProductoServicio productoServicio;
    private FacturacionServicio facturacionServicio;
    
    private ObservableList<DetalleVentaItem> detalleVentaItems;
    private ObservableList<Cliente> clientesEncontrados;
    private ObservableList<Producto> productosEncontrados;
    private DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");
    
    private Cliente clienteSeleccionado = null;
    private double subtotalNeto = 0.0;
    private double descuentoGlobalTotal = 0.0;
    private double porcentajeIVA = 0.16;
    private double montoIVA = 0.0;
    private double totalPagar = 0.0;
    private int contadorVentas = 1;

    public VentaController() {
        this.clienteServicio = new ClienteServicio();
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
        this.facturacionServicio = new FacturacionServicio();
        this.detalleVentaItems = FXCollections.observableArrayList();
        this.clientesEncontrados = FXCollections.observableArrayList();
        this.productosEncontrados = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        System.out.println("VentaController inicializado");
        
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtFecha.setText(fechaActual.format(formatter));

        generarCodigoVenta();
        configurarTablaClientes();
        configurarTablaProductos();
        configurarTablaDetalleVenta();
        configurarComboBoxes();
        configurarEventos();
        actualizarLabelsResumen();
    }

    private void configurarTablaClientes() {
        if (tablaClientes == null) return;
        
        // CORRECCIÓN: Usar lambdas con SimpleStringProperty como en ClientesController
        colClienteCedula.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCedula()));
        
        colClienteNombre.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombre()));
        
        colClienteTelefono.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTelefono()));
        
        colClienteEmail.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getEmail();
            return new SimpleStringProperty(email != null ? email : "");
        });
        
        colClienteSeleccionar.setCellFactory(param -> new TableCell<>() {
            private final Button btnSeleccionar = new Button("Seleccionar");
            
            {
                btnSeleccionar.setStyle(
                    "-fx-background-color: #27ae60; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 5; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-weight: bold;"
                );
                
                btnSeleccionar.setOnAction(event -> {
                    Cliente cliente = getTableView().getItems().get(getIndex());
                    seleccionarCliente(cliente);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnSeleccionar);
            }
        });
        
        tablaClientes.setItems(clientesEncontrados);
    }
    
    private void configurarTablaProductos() {
        if (tablaProductos == null) return;
        
        // Usar lambdas con SimpleStringProperty
        colProductoCodigo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCodigo()));
        
        colProductoNombre.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombre()));
        
        colProductoCategoria.setCellValueFactory(cellData -> {
            String categoria = cellData.getValue().getCategoria();
            return new SimpleStringProperty(categoria != null ? categoria : "");
        });
        
        colProductoPrecio.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());
        
        colProductoStock.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
        
        colProductoPrecio.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty || precio == null ? null : formatoMoneda.format(precio));
            }
        });
        
        colProductoAgregar.setCellFactory(param -> new TableCell<>() {
            private final Button btnAgregar = new Button("+ Agregar");
            
            {
                btnAgregar.setStyle(
                    "-fx-background-color: #3498db; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 5; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-weight: bold;"
                );
                
                btnAgregar.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    agregarProductoDesdeTabla(producto);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnAgregar);
            }
        });
        
        tablaProductos.setItems(productosEncontrados);
    }

    private void configurarTablaDetalleVenta() {
        if (tblDetalleVenta == null) return;
        
        colNumero.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getNumero()).asObject());
        
        colProducto.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombreProducto()));
        
        colCantidad.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
        
        colDescuento.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getDescuento()).asObject());
        
        colSubtotal.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getSubtotal()).asObject());
        
        colDescuento.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatoMoneda.format(item));
            }
        });
        
        colSubtotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatoMoneda.format(item));
            }
        });
        
        tblDetalleVenta.setItems(detalleVentaItems);
    }

    @FXML
    private void buscarCliente() {
        String criterio = txtBuscarCliente.getText().trim().toLowerCase();
        
        if (criterio.isEmpty()) {
            mostrarAdvertencia("Ingrese un criterio de búsqueda (cédula, nombre o teléfono)");
            txtBuscarCliente.requestFocus();
            return;
        }
        
        try {
            List<Cliente> todosClientes = clienteServicio.listarClientes();
            
            List<Cliente> clientesFiltrados = todosClientes.stream()
                .filter(c -> 
                    c.getCedula().toLowerCase().contains(criterio) ||
                    c.getNombre().toLowerCase().contains(criterio) ||
                    (c.getTelefono() != null && c.getTelefono().toLowerCase().contains(criterio))
                )
                .collect(Collectors.toList());
            
            clientesEncontrados.clear();
            clientesEncontrados.addAll(clientesFiltrados);
            
            // Forzar refresco de la tabla
            tablaClientes.refresh();
            
            if (clientesFiltrados.isEmpty()) {
                mostrarInformacion("No se encontraron clientes con el criterio: " + criterio);
            } else {
                System.out.println("Clientes encontrados: " + clientesFiltrados.size());
            }
            
        } catch (Exception e) {
            mostrarError("Error al buscar clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void seleccionarCliente(Cliente cliente) {
        if (cliente == null) return;
        
        clienteSeleccionado = cliente;
        lblClienteSeleccionado.setText(
            String.format("%s - %s", cliente.getNombre(), cliente.getCedula())
        );
        lblClienteSeleccionado.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        mostrarInformacion("Cliente seleccionado: " + cliente.getNombre());
    }
    
    @FXML
    private void mostrarFormularioCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Clientes.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestión de Clientes");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            
            stage.setOnHidden(e -> {
                if (!txtBuscarCliente.getText().trim().isEmpty()) {
                    buscarCliente();
                }
            });
            
            stage.showAndWait();

        } catch (IOException e) {
            mostrarError("Error al abrir el formulario de clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarProducto() {
        String criterio = txtBuscarProducto.getText().trim().toLowerCase();
        
        if (criterio.isEmpty()) {
            mostrarAdvertencia("Ingrese un criterio de búsqueda (código, nombre o categoría)");
            txtBuscarProducto.requestFocus();
            return;
        }
        
        try {
            List<Producto> todosProductos = productoServicio.listarProductos();
            
            List<Producto> productosFiltrados = todosProductos.stream()
                .filter(p -> 
                    p.getCodigo().toLowerCase().contains(criterio) ||
                    p.getNombre().toLowerCase().contains(criterio) ||
                    (p.getCategoria() != null && p.getCategoria().toLowerCase().contains(criterio))
                )
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
            
            productosEncontrados.clear();
            productosEncontrados.addAll(productosFiltrados);
            
            tablaProductos.refresh();
            
            if (productosFiltrados.isEmpty()) {
                mostrarInformacion("No se encontraron productos con stock disponible para: " + criterio);
            } else {
                System.out.println("Productos encontrados: " + productosFiltrados.size());
            }
            
        } catch (Exception e) {
            mostrarError("Error al buscar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void agregarProductoDesdeTabla(Producto producto) {
        if (producto == null) return;
        
        int cantidad;
        try {
            String cantidadTexto = txtCantidad.getText().trim();
            cantidad = cantidadTexto.isEmpty() ? 1 : Integer.parseInt(cantidadTexto);
            
            if (cantidad <= 0) {
                mostrarAdvertencia("La cantidad debe ser mayor a 0");
                txtCantidad.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("La cantidad debe ser un número válido");
            txtCantidad.requestFocus();
            return;
        }
        
        double descuento;
        try {
            String descuentoTexto = txtDescuento.getText().trim();
            descuento = descuentoTexto.isEmpty() ? 0.0 : Double.parseDouble(descuentoTexto);
            
            if (descuento < 0) {
                mostrarAdvertencia("El descuento no puede ser negativo");
                txtDescuento.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("El descuento debe ser un número válido");
            txtDescuento.requestFocus();
            return;
        }
        
        agregarProducto(producto, cantidad, descuento);
        
        txtBuscarProducto.clear();
        txtCantidad.setText("1");
        txtDescuento.setText("0");
    }

    private void agregarProducto(Producto producto, int cantidad, double descuento) {
        if (producto == null || cantidad <= 0) {
            mostrarError("Producto o cantidad inválidos");
            return;
        }
        
        if (!producto.tieneStock(cantidad)) {
            mostrarAdvertencia(
                String.format("Stock insuficiente para %s.\nDisponible: %d unidades", 
                    producto.getNombre(), producto.getStock())
            );
            return;
        }
        
        double precioUnitario = producto.getPrecioVenta();
        double subtotal = (precioUnitario * cantidad) - descuento;
        
        boolean productoExiste = false;
        for (DetalleVentaItem item : detalleVentaItems) {
            if (item.getProducto().getCodigo().equals(producto.getCodigo())) {
                int nuevaCantidad = item.getCantidad() + cantidad;
                if (!producto.tieneStock(nuevaCantidad)) {
                    mostrarAdvertencia(
                        String.format("No puede agregar más unidades de %s.\nStock disponible: %d", 
                            producto.getNombre(), producto.getStock())
                    );
                    return;
                }
                
                item.setCantidad(nuevaCantidad);
                double nuevoDescuento = item.getDescuento() + descuento;
                item.setDescuento(nuevoDescuento);
                double nuevoSubtotal = (precioUnitario * nuevaCantidad) - nuevoDescuento;
                item.setSubtotal(nuevoSubtotal);
                
                productoExiste = true;
                tblDetalleVenta.refresh();
                break;
            }
        }
        
        if (!productoExiste) {
            int numero = detalleVentaItems.size() + 1;
            DetalleVentaItem item = new DetalleVentaItem(
                numero, producto, cantidad, precioUnitario, descuento, subtotal
            );
            detalleVentaItems.add(item);
        }
        
        calcularTotales();
        mostrarInformacion(
            String.format("✓ Producto agregado: %s x%d", producto.getNombre(), cantidad)
        );
    }

    @FXML
    public void eliminarProductoSeleccionado() {
        DetalleVentaItem itemSeleccionado = tblDetalleVenta.getSelectionModel().getSelectedItem();
        if (itemSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Eliminar Producto");
            confirmacion.setHeaderText("¿Desea eliminar este producto de la venta?");
            confirmacion.setContentText(
                String.format("%s x%d - %s", 
                    itemSeleccionado.getNombreProducto(), 
                    itemSeleccionado.getCantidad(),
                    formatoMoneda.format(itemSeleccionado.getSubtotal()))
            );
            
            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    detalleVentaItems.remove(itemSeleccionado);
                    renumerarItems();
                    calcularTotales();
                    mostrarInformacion("Producto eliminado del detalle");
                }
            });
        } else {
            mostrarAdvertencia("Seleccione un producto de la tabla para eliminar");
        }
    }

    private void renumerarItems() {
        for (int i = 0; i < detalleVentaItems.size(); i++) {
            detalleVentaItems.get(i).setNumero(i + 1);
        }
    }

    private void calcularTotales() {
        subtotalNeto = 0.0;
        descuentoGlobalTotal = 0.0;
        
        for (DetalleVentaItem item : detalleVentaItems) {
            subtotalNeto += item.getSubtotal();
            descuentoGlobalTotal += item.getDescuento();
        }
        
        try {
            String ivaTexto = txtIVA.getText().trim();
            if (!ivaTexto.isEmpty()) {
                porcentajeIVA = Double.parseDouble(ivaTexto) / 100.0;
            }
        } catch (NumberFormatException e) {
            porcentajeIVA = 0.16;
        }
        
        montoIVA = subtotalNeto * porcentajeIVA;
        totalPagar = subtotalNeto + montoIVA;
        
        actualizarLabelsResumen();
        calcularCambio();
    }

    private void actualizarLabelsResumen() {
        if (lblTotalPagar != null) {
            lblTotalPagar.setText(
                String.format("TOTAL A PAGAR: %s", formatoMoneda.format(totalPagar))
            );
            lblTotalPagar.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #1e3c72;"
            );
        }
    }

    private void calcularCambio() {
        if (txtMontoRecibido == null || txtCambio == null) return;
        
        try {
            String montoTexto = txtMontoRecibido.getText().trim();
            if (montoTexto.isEmpty()) {
                txtCambio.clear();
                return;
            }
            
            double montoRecibido = Double.parseDouble(montoTexto);
            double cambio = montoRecibido - totalPagar;
            
            if (cambio < 0) {
                txtCambio.setText("Falta: " + formatoMoneda.format(Math.abs(cambio)));
                txtCambio.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                txtCambio.setText(formatoMoneda.format(cambio));
                txtCambio.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            }
        } catch (NumberFormatException e) {
            txtCambio.clear();
        }
    }

    private void generarCodigoVenta() {
        try {
            List<Venta> ventas = ventaServicio.listarVentas();
            contadorVentas = ventas.size() + 1;
        } catch (Exception e) {
            System.out.println("No se pudieron cargar ventas, usando contador por defecto");
        }
        
        String codigo = String.format("V-%03d", contadorVentas);
        txtCodigoVenta.setText(codigo);
    }

    private void configurarComboBoxes() {
        if (cmbMetodoPago != null) {
            cmbMetodoPago.setItems(FXCollections.observableArrayList(
                "Efectivo", "Tarjeta", "Transferencia"
            ));
            cmbMetodoPago.setValue("Efectivo");
            
            cmbMetodoPago.setOnAction(e -> {
                if (txtMontoRecibido != null && txtCambio != null) {
                    String metodoPago = cmbMetodoPago.getValue();
                    
                    txtMontoRecibido.setDisable(false);
                    txtMontoRecibido.setEditable(true);
                    
                    if ("Efectivo".equals(metodoPago)) {
                        txtCambio.setDisable(false);
                        txtMontoRecibido.clear();
                        txtCambio.clear();
                        txtMontoRecibido.requestFocus();
                    } else if ("Tarjeta".equals(metodoPago) || "Transferencia".equals(metodoPago)) {
                        txtMontoRecibido.setText(String.format("%.2f", totalPagar));
                        txtCambio.setText("0.00");
                        txtCambio.setDisable(true);
                    }
                }
            });
        }
        
        if (txtIVA != null) {
            txtIVA.textProperty().addListener((obs, oldVal, newVal) -> {
                calcularTotales();
            });
        }
    }

    private void configurarEventos() {
        if (txtMontoRecibido != null) {
            txtMontoRecibido.textProperty().addListener((obs, oldVal, newVal) -> {
                calcularCambio();
            });
        }
        
        if (txtBuscarCliente != null) {
            txtBuscarCliente.setOnAction(e -> buscarCliente());
        }
        
        if (txtBuscarProducto != null) {
            txtBuscarProducto.setOnAction(e -> buscarProducto());
        }
        
        if (btnCancelarVenta != null) {
            btnCancelarVenta.setOnAction(e -> cancelarVenta());
        }
    }

    @FXML
    private void guardarVenta() {
        if (!validarVenta()) {
            return;
        }
        
        try {
            Venta venta = new Venta();
            venta.setCodigo(txtCodigoVenta.getText());
            venta.setFecha(LocalDateTime.now());
            venta.setCliente(clienteSeleccionado);
            venta.setTotal(totalPagar);
            
            List<DetalleVenta> detalles = new ArrayList<>();
            for (DetalleVentaItem item : detalleVentaItems) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setProducto(item.getProducto());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getPrecioUnitario());
                
                double subtotalConDescuento = (item.getPrecioUnitario() * item.getCantidad()) - item.getDescuento();
                detalle.setSubtotal(subtotalConDescuento);
                
                detalles.add(detalle);
            }
            
            ventaServicio.guardarVenta(venta, detalles);
            
            String metodoPago = cmbMetodoPago.getValue();
            double montoRecibido = 0.0;
            double cambio = 0.0;
            
            if ("Efectivo".equals(metodoPago)) {
                montoRecibido = Double.parseDouble(txtMontoRecibido.getText().trim());
                cambio = montoRecibido - totalPagar;
            } else {
                montoRecibido = totalPagar;
            }
            
            Factura factura = facturacionServicio.generarFacturaDesdeVenta(
                venta, 
                metodoPago, 
                montoRecibido, 
                cambio,
                subtotalNeto,
                montoIVA,
                descuentoGlobalTotal
            );
            
            String mensaje = String.format(
                " VENTA GUARDADA EXITOSAMENTE\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "📄 Código de Venta: %s\n" +
                " Comprobante: %s\n\n" +
                "👤 Cliente: %s\n" +
                "📦 Productos: %d items\n" +
                "💰 Total: %s\n" +
                "💳 Método de Pago: %s\n" +
                "%s",
                txtCodigoVenta.getText(),
                factura.getNumeroComprobante(),
                clienteSeleccionado.getNombre(),
                detalleVentaItems.size(),
                formatoMoneda.format(totalPagar),
                metodoPago,
                ("Efectivo".equals(metodoPago) && cambio > 0) 
                    ? String.format("💵 Cambio: %s", formatoMoneda.format(cambio))
                    : ""
            );
            
            mostrarExito(mensaje);
            limpiarFormulario();
            
        } catch (Exception e) {
            mostrarError("Error al guardar la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarVenta() {
        if (clienteSeleccionado == null) {
            mostrarAdvertencia("Debe seleccionar un cliente antes de guardar la venta");
            txtBuscarCliente.requestFocus();
            return false;
        }
        
        if (detalleVentaItems.isEmpty()) {
            mostrarAdvertencia("Debe agregar al menos un producto a la venta");
            txtBuscarProducto.requestFocus();
            return false;
        }
        
        if (cmbMetodoPago == null || cmbMetodoPago.getValue() == null) {
            mostrarAdvertencia("Debe seleccionar un método de pago");
            if (cmbMetodoPago != null) cmbMetodoPago.requestFocus();
            return false;
        }
        
        if (txtMontoRecibido.getText().trim().isEmpty()) {
            mostrarAdvertencia("Debe ingresar el monto recibido");
            txtMontoRecibido.requestFocus();
            return false;
        }
        
        try {
            double montoRecibido = Double.parseDouble(txtMontoRecibido.getText());
            String metodoPago = cmbMetodoPago.getValue();
            
            if ("Efectivo".equals(metodoPago)) {
                if (montoRecibido < totalPagar) {
                    mostrarAdvertencia(
                        String.format(
                            "El monto recibido es insuficiente\n\n" +
                            "Monto recibido: %s\n" +
                            "Total a pagar: %s\n" +
                            "Falta: %s",
                            formatoMoneda.format(montoRecibido),
                            formatoMoneda.format(totalPagar),
                            formatoMoneda.format(totalPagar - montoRecibido)
                        )
                    );
                    txtMontoRecibido.requestFocus();
                    return false;
                }
            } else {
                if (montoRecibido <= 0) {
                    mostrarAdvertencia("El monto debe ser mayor a cero");
                    txtMontoRecibido.requestFocus();
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("El monto recibido debe ser un número válido");
            txtMontoRecibido.requestFocus();
            return false;
        }
        
        return true;
    }

    @FXML
    private void cancelarVenta() {
        if (detalleVentaItems.isEmpty() && clienteSeleccionado == null) {
            mostrarInformacion("No hay ninguna venta en proceso para cancelar");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar Venta");
        confirmacion.setHeaderText("¿Está seguro de cancelar esta venta?");
        confirmacion.setContentText(
            String.format(
                "Se perderán todos los datos:\n\n" +
                "• Cliente: %s\n" +
                "• Productos: %d items\n" +
                "• Total: %s",
                clienteSeleccionado != null ? clienteSeleccionado.getNombre() : "Sin seleccionar",
                detalleVentaItems.size(),
                formatoMoneda.format(totalPagar)
            )
        );
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                limpiarFormulario();
                mostrarInformacion("✓ La venta ha sido cancelada");
            }
        });
    }

    private void limpiarFormulario() {
        detalleVentaItems.clear();
        clientesEncontrados.clear();
        productosEncontrados.clear();
        
        clienteSeleccionado = null;
        if (lblClienteSeleccionado != null) {
            lblClienteSeleccionado.setText("Ninguno");
            lblClienteSeleccionado.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: normal;");
        }
        
        if (txtBuscarCliente != null) txtBuscarCliente.clear();
        if (txtBuscarProducto != null) txtBuscarProducto.clear();
        if (txtCantidad != null) txtCantidad.setText("1");
        if (txtDescuento != null) txtDescuento.setText("0");
        if (txtMontoRecibido != null) txtMontoRecibido.clear();
        if (txtCambio != null) txtCambio.clear();
        
        if (cmbMetodoPago != null) {
            cmbMetodoPago.setValue("Efectivo");
        }
        
        if (txtIVA != null) {
            txtIVA.setText("16");
        }
        
        subtotalNeto = 0.0;
        descuentoGlobalTotal = 0.0;
        montoIVA = 0.0;
        totalPagar = 0.0;
        porcentajeIVA = 0.16;
        
        actualizarLabelsResumen();
        
        contadorVentas++;
        generarCodigoVenta();
        
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtFecha.setText(fechaActual.format(formatter));
        
        System.out.println("Formulario limpiado para nueva venta");
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("❌ Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("⚠️ Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ℹ️ Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("✓ Éxito");
        alert.setHeaderText("Operación Completada");
        alert.setContentText(mensaje);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
            "-fx-background-color: #f0f9ff; " +
            "-fx-border-color: #27ae60; " +
            "-fx-border-width: 2px;"
        );
        
        alert.showAndWait();
    }

    public void setCodigoVenta(String codigo) {
        if (txtCodigoVenta != null) {
            txtCodigoVenta.setText(codigo);
        }
    }
    
    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }
    
    public void setClienteSeleccionado(Cliente cliente) {
        seleccionarCliente(cliente);
    }

    public static class DetalleVentaItem {
        private SimpleIntegerProperty numero;
        private final Producto producto;
        private final SimpleStringProperty nombreProducto;
        private SimpleIntegerProperty cantidad;
        private final SimpleDoubleProperty precioUnitario;
        private SimpleDoubleProperty descuento;
        private SimpleDoubleProperty subtotal;

        public DetalleVentaItem(int numero, Producto producto, int cantidad, 
                                double precioUnitario, double descuento, double subtotal) {
            this.numero = new SimpleIntegerProperty(numero);
            this.producto = producto;
            this.nombreProducto = new SimpleStringProperty(producto.getNombre());
            this.cantidad = new SimpleIntegerProperty(cantidad);
            this.precioUnitario = new SimpleDoubleProperty(precioUnitario);
            this.descuento = new SimpleDoubleProperty(descuento);
            this.subtotal = new SimpleDoubleProperty(subtotal);
        }

        public int getNumero() { 
            return numero.get(); 
        }
        
        public void setNumero(int num) { 
            numero.set(num); 
        }
        
        public SimpleIntegerProperty numeroProperty() { 
            return numero; 
        }
        
        public Producto getProducto() { 
            return producto; 
        }
        
        public String getNombreProducto() { 
            return nombreProducto.get(); 
        }
        
        public SimpleStringProperty nombreProductoProperty() { 
            return nombreProducto; 
        }
        
        public int getCantidad() { 
            return cantidad.get(); 
        }
        
        public void setCantidad(int cant) { 
            cantidad.set(cant); 
        }
        
        public SimpleIntegerProperty cantidadProperty() { 
            return cantidad; 
        }
        
        public double getPrecioUnitario() { 
            return precioUnitario.get(); 
        }
        
        public SimpleDoubleProperty precioUnitarioProperty() { 
            return precioUnitario; 
        }
        
        public double getDescuento() { 
            return descuento.get(); 
        }
        
        public void setDescuento(double desc) { 
            descuento.set(desc); 
        }
        
        public SimpleDoubleProperty descuentoProperty() { 
            return descuento; 
        }
        
        public double getSubtotal() { 
            return subtotal.get(); 
        }
        
        public void setSubtotal(double sub) { 
            subtotal.set(sub); 
        }
        
        public SimpleDoubleProperty subtotalProperty() { 
            return subtotal; 
        }
    }
}