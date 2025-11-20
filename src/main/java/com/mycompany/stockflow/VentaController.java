/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import com.mycompany.stockflow.utils.GeneradorPDFComprobante;
import com.mycompany.stockflow.utils.EmailServicio;
import com.mycompany.stockflow.excepciones.EmailException;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador JavaFX para la gestion de ventas en el sistema StockFlow.
 * Permite crear ventas, seleccionar clientes, agregar productos, calcular totales,
 * aplicar descuentos, generar facturas y enviar comprobantes por email.
 * 
 * @author Equipo StockFlow/StockFlow Team
 * @version 1.0
 * @since 2025
 */
public class VentaController {

    @FXML private TextField txtCodigoVenta;
    @FXML private TextField txtFecha;
    @FXML private TextField txtBuscarCliente;
    @FXML private Button btnRefrescarClientes;
    @FXML private Button btnNuevoCliente;
    
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colClienteCedula;
    @FXML private TableColumn<Cliente, String> colClienteNombre;
    @FXML private TableColumn<Cliente, String> colClienteTelefono;
    @FXML private TableColumn<Cliente, String> colClienteEmail;
    @FXML private TableColumn<Cliente, Void> colClienteSeleccionar;
    @FXML private Label lblClienteSeleccionado;
    @FXML private Label lblTotalClientes;
    
    @FXML private TextField txtBuscarProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtDescuento;
    @FXML private Button btnRefrescarProductos;
    
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colProductoCodigo;
    @FXML private TableColumn<Producto, String> colProductoNombre;
    @FXML private TableColumn<Producto, String> colProductoCategoria;
    @FXML private TableColumn<Producto, Double> colProductoPrecio;
    @FXML private TableColumn<Producto, Integer> colProductoStock;
    @FXML private TableColumn<Producto, Void> colProductoAgregar;
    @FXML private Label lblTotalProductos;

    @FXML private TableView<DetalleVentaItem> tblDetalleVenta;
    @FXML private TableColumn<DetalleVentaItem, Integer> colNumero;
    @FXML private TableColumn<DetalleVentaItem, String> colProducto;
    @FXML private TableColumn<DetalleVentaItem, Integer> colCantidad;
    @FXML private TableColumn<DetalleVentaItem, Double> colPrecioUnitario;
    @FXML private TableColumn<DetalleVentaItem, Double> colDescuento;
    @FXML private TableColumn<DetalleVentaItem, Double> colSubtotal;

    @FXML private TextField txtIVA;
    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuentoTotal;
    @FXML private Label lblIVA;
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
    private ObservableList<Cliente> listaCompletaClientes;
    private ObservableList<Producto> listaCompletaProductos;
    private FilteredList<Cliente> clientesFiltrados;
    private FilteredList<Producto> productosFiltrados;
    
    private DecimalFormat formatoMoneda = new DecimalFormat("'COP '#,##0");
    private DecimalFormat formatoEntero = new DecimalFormat("#,##0");
    
    private Cliente clienteSeleccionado = null;
    private double subtotalNeto = 0.0;
    private double descuentoGlobalTotal = 0.0;
    private double porcentajeIVA = 0.0;
    private double montoIVA = 0.0;
    private double totalPagar = 0.0;
    private int contadorVentas = 1;

    /**
     * Constructor del controlador de ventas.
     * Inicializa los servicios necesarios y las listas observables.
     */
    public VentaController() {
        this.clienteServicio = new ClienteServicio();
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
        this.facturacionServicio = new FacturacionServicio();
        this.detalleVentaItems = FXCollections.observableArrayList();
        this.listaCompletaClientes = FXCollections.observableArrayList();
        this.listaCompletaProductos = FXCollections.observableArrayList();
    }

    /**
     * Metodo de inicializacion del controlador.
     * Configura las tablas, combos, eventos y carga los datos iniciales.
     */
    @FXML
    public void initialize() {
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtFecha.setText(fechaActual.format(formatter));

        generarCodigoVenta();
        configurarTablaClientes();
        configurarTablaProductos();
        configurarTablaDetalleVenta();
        configurarComboBoxes();
        configurarBusquedaEnTiempoReal();
        configurarEventos();
        
        cargarTodosLosClientes();
        cargarTodosLosProductos();
        
        actualizarLabelsResumen();
    }

    /**
     * Carga todos los clientes desde la base de datos y actualiza la tabla.
     */
    private void cargarTodosLosClientes() {
        try {
            List<Cliente> clientes = clienteServicio.listarClientes();
            listaCompletaClientes.clear();
            listaCompletaClientes.addAll(clientes);
            
            if (lblTotalClientes != null) {
                lblTotalClientes.setText(String.format("Total: %d clientes", clientes.size()));
            }
        } catch (Exception e) {
            mostrarError("Error al cargar clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga todos los productos con stock disponible desde la base de datos.
     */
    private void cargarTodosLosProductos() {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            
            List<Producto> productosConStock = new ArrayList<>();
            for (Producto p : productos) {
                if (p.getStock() > 0) {
                    productosConStock.add(p);
                }
            }
            
            listaCompletaProductos.clear();
            listaCompletaProductos.addAll(productosConStock);
            
            if (lblTotalProductos != null) {
                lblTotalProductos.setText(String.format("Total: %d productos disponibles", productosConStock.size()));
            }
        } catch (Exception e) {
            mostrarError("Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura los listeners para la busqueda en tiempo real de clientes y productos.
     */
    private void configurarBusquedaEnTiempoReal() {
        clientesFiltrados = new FilteredList<>(listaCompletaClientes, p -> true);
        tablaClientes.setItems(clientesFiltrados);
        
        if (txtBuscarCliente != null) {
            txtBuscarCliente.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarClientes(newValue);
            });
        }
        
        productosFiltrados = new FilteredList<>(listaCompletaProductos, p -> true);
        tablaProductos.setItems(productosFiltrados);
        
        if (txtBuscarProducto != null) {
            txtBuscarProducto.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrarProductos(newValue);
            });
        }
    }
    
    /**
     * Filtra la lista de clientes segun el criterio de busqueda.
     * Busca en cedula, nombre, telefono y email.
     * 
     * @param criterio Texto a buscar en los campos del cliente
     */
    private void filtrarClientes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            clientesFiltrados.setPredicate(cliente -> true);
            if (lblTotalClientes != null) {
                lblTotalClientes.setText(String.format("Total: %d clientes", listaCompletaClientes.size()));
            }
            return;
        }
        
        String criterioBusqueda = criterio.toLowerCase().trim();
        
        clientesFiltrados.setPredicate(cliente -> {
            if (cliente.getCedula().toLowerCase().contains(criterioBusqueda)) return true;
            if (cliente.getNombre().toLowerCase().contains(criterioBusqueda)) return true;
            if (cliente.getTelefono() != null && cliente.getTelefono().toLowerCase().contains(criterioBusqueda)) return true;
            if (cliente.getEmail() != null && cliente.getEmail().toLowerCase().contains(criterioBusqueda)) return true;
            return false;
        });
        
        if (lblTotalClientes != null) {
            lblTotalClientes.setText(String.format("Mostrando: %d de %d clientes", 
                clientesFiltrados.size(), listaCompletaClientes.size()));
        }
    }
    
    /**
     * Filtra la lista de productos segun el criterio de busqueda.
     * Busca en codigo, nombre y categoria.
     * 
     * @param criterio Texto a buscar en los campos del producto
     */
    private void filtrarProductos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            productosFiltrados.setPredicate(producto -> true);
            if (lblTotalProductos != null) {
                lblTotalProductos.setText(String.format("Total: %d productos disponibles", listaCompletaProductos.size()));
            }
            return;
        }
        
        String criterioBusqueda = criterio.toLowerCase().trim();
        
        productosFiltrados.setPredicate(producto -> {
            if (producto.getCodigo().toLowerCase().contains(criterioBusqueda)) return true;
            if (producto.getNombre().toLowerCase().contains(criterioBusqueda)) return true;
            if (producto.getCategoria() != null && producto.getCategoria().toLowerCase().contains(criterioBusqueda)) return true;
            return false;
        });
        
        if (lblTotalProductos != null) {
            lblTotalProductos.setText(String.format("Mostrando: %d de %d productos", 
                productosFiltrados.size(), listaCompletaProductos.size()));
        }
    }

    /**
     * Recarga la lista completa de clientes desde la base de datos.
     */
    @FXML
    private void refrescarClientes() {
        txtBuscarCliente.clear();
        cargarTodosLosClientes();
        mostrarInformacion("Lista de clientes actualizada");
    }
    
    /**
     * Recarga la lista completa de productos desde la base de datos.
     */
    @FXML
    private void refrescarProductos() {
        txtBuscarProducto.clear();
        cargarTodosLosProductos();
        mostrarInformacion("Lista de productos actualizada");
    }

    /**
     * Configura las columnas y el comportamiento de la tabla de clientes.
     * Incluye boton de seleccion en cada fila.
     */
    private void configurarTablaClientes() {
        if (tablaClientes == null) return;
        
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
    }
    
    /**
     * Configura las columnas y el comportamiento de la tabla de productos.
     * Incluye formato de moneda y colores segun el nivel de stock.
     */
    private void configurarTablaProductos() {
        if (tablaProductos == null) return;
        
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
        
        colProductoStock.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(stock));
                    if (stock < 5) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else if (stock < 10) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60;");
                    }
                }
            }
        });
        
        colProductoAgregar.setCellFactory(param -> new TableCell<>() {
            private final Button btnAgregar = new Button("Agregar");
            
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
    }

    /**
     * Configura las columnas de la tabla de detalle de venta.
     * Aplica formato de moneda a precios, descuentos y subtotales.
     */
    private void configurarTablaDetalleVenta() {
        if (tblDetalleVenta == null) return;
        
        colNumero.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getNumero()).asObject());
        
        colProducto.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombreProducto()));
        
        colCantidad.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
        
        if (colPrecioUnitario != null) {
            colPrecioUnitario.setCellValueFactory(cellData -> 
                new SimpleDoubleProperty(cellData.getValue().getPrecioUnitario()).asObject());
            
            colPrecioUnitario.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : formatoMoneda.format(item));
                }
            });
        }
        
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

    /**
     * Selecciona un cliente para la venta actual.
     * 
     * @param cliente Cliente a seleccionar
     */
    private void seleccionarCliente(Cliente cliente) {
        if (cliente == null) return;
        
        clienteSeleccionado = cliente;
        lblClienteSeleccionado.setText(
            String.format("%s - %s", cliente.getNombre(), cliente.getCedula())
        );
        lblClienteSeleccionado.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        mostrarInformacion("Cliente seleccionado: " + cliente.getNombre());
    }
    
    /**
     * Abre el formulario de gestion de clientes en una ventana modal.
     */
    @FXML
    private void mostrarFormularioCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Clientes.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestion de Clientes");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            
            stage.setOnHidden(e -> cargarTodosLosClientes());
            
            stage.showAndWait();

        } catch (IOException e) {
            mostrarError("Error al abrir el formulario de clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Agrega un producto al detalle de venta desde la tabla de productos.
     * Valida cantidad y descuento antes de agregar.
     * 
     * @param producto Producto a agregar
     */
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
            mostrarAdvertencia("La cantidad debe ser un numero valido");
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
            mostrarAdvertencia("El descuento debe ser un numero valido");
            txtDescuento.requestFocus();
            return;
        }
        
        agregarProducto(producto, cantidad, descuento);
        
        txtBuscarProducto.clear();
        txtCantidad.setText("1");
        txtDescuento.setText("0");
    }

    /**
     * Agrega un producto al detalle de venta.
     * Si el producto ya existe, actualiza la cantidad y descuento.
     * Verifica stock disponible antes de agregar.
     * 
     * @param producto Producto a agregar
     * @param cantidad Cantidad a agregar
     * @param descuento Descuento a aplicar en pesos
     */
    private void agregarProducto(Producto producto, int cantidad, double descuento) {
        if (producto == null || cantidad <= 0) {
            mostrarError("Producto o cantidad invalidos");
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
                        String.format("No puede agregar mas unidades de %s.\nStock disponible: %d", 
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
            String.format("Producto agregado: %s x%d", producto.getNombre(), cantidad)
        );
    }

    /**
     * Elimina el producto seleccionado del detalle de venta.
     * Solicita confirmacion antes de eliminar.
     */
    @FXML
    public void eliminarProductoSeleccionado() {
        DetalleVentaItem itemSeleccionado = tblDetalleVenta.getSelectionModel().getSelectedItem();
        if (itemSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Eliminar Producto");
            confirmacion.setHeaderText("Desea eliminar este producto de la venta?");
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

    /**
     * Renumera los items del detalle de venta despues de eliminar uno.
     */
    private void renumerarItems() {
        for (int i = 0; i < detalleVentaItems.size(); i++) {
            detalleVentaItems.get(i).setNumero(i + 1);
        }
    }

    /**
     * Calcula los totales de la venta incluyendo subtotal, descuentos, IVA y total final.
     */
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
            porcentajeIVA = 0.0;
        }
        
        montoIVA = subtotalNeto * porcentajeIVA;
        totalPagar = subtotalNeto + montoIVA;
        
        actualizarLabelsResumen();
        calcularCambio();
    }

    /**
     * Actualiza los labels del resumen de venta con los valores calculados.
     */
    private void actualizarLabelsResumen() {
        if (lblSubtotal != null) {
            lblSubtotal.setText(formatoMoneda.format(subtotalNeto));
        }
        
        if (lblDescuentoTotal != null) {
            lblDescuentoTotal.setText(formatoMoneda.format(descuentoGlobalTotal));
        }
        
        if (lblIVA != null) {
            lblIVA.setText(formatoMoneda.format(montoIVA));
        }
        
        if (lblTotalPagar != null) {
            lblTotalPagar.setText(formatoMoneda.format(totalPagar));
            lblTotalPagar.setStyle(
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #1e3c72;"
            );
        }
    }

    /**
     * Calcula el cambio a devolver al cliente segun el monto recibido.
     * Si el monto es insuficiente, muestra cuanto falta.
     */
    private void calcularCambio() {
        if (txtMontoRecibido == null || txtCambio == null) return;
        
        try {
            String montoTexto = txtMontoRecibido.getText().trim();
            if (montoTexto.isEmpty()) {
                txtCambio.clear();
                return;
            }
            
            double montoRecibido = Double.parseDouble(montoTexto.replace(",", ""));
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

    /**
     * Genera un codigo unico para la venta basado en el numero de ventas existentes.
     */
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

    /**
     * Configura los ComboBox de la interfaz.
     * Configura metodos de pago y listeners para IVA.
     */
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
                        txtMontoRecibido.setText(formatoEntero.format(Math.round(totalPagar)));
                        txtCambio.setText("0");
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

    /**
     * Configura los eventos de la interfaz como listeners y acciones de botones.
     */
    private void configurarEventos() {
        if (txtMontoRecibido != null) {
            txtMontoRecibido.textProperty().addListener((obs, oldVal, newVal) -> {
                calcularCambio();
            });
        }
        
        if (txtCantidad != null) {
            txtCantidad.setOnAction(e -> txtDescuento.requestFocus());
        }
        
        if (txtDescuento != null) {
            txtDescuento.setOnAction(e -> {
                if (!productosFiltrados.isEmpty()) {
                    agregarProductoDesdeTabla(productosFiltrados.get(0));
                }
            });
        }
        
        if (btnCancelarVenta != null) {
            btnCancelarVenta.setOnAction(e -> cancelarVenta());
        }
        
        if (tablaClientes != null) {
            tablaClientes.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Cliente cliente = tablaClientes.getSelectionModel().getSelectedItem();
                    if (cliente != null) {
                        seleccionarCliente(cliente);
                    }
                }
            });
        }
        
        if (tablaProductos != null) {
            tablaProductos.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Producto producto = tablaProductos.getSelectionModel().getSelectedItem();
                    if (producto != null) {
                        agregarProductoDesdeTabla(producto);
                    }
                }
            });
        }
    }

    /**
     * Guarda la venta en la base de datos, genera la factura y opcionalmente
     * envia el comprobante por email al cliente.
     * Valida todos los datos antes de procesar la venta.
     */
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
                montoRecibido = Double.parseDouble(txtMontoRecibido.getText().trim().replace(",", ""));
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
            
            String mensaje = construirMensajeVentaExitosa(factura, metodoPago, cambio);
            mostrarExito(mensaje);

            final Cliente clienteParaEmail = clienteSeleccionado;
            final Factura facturaParaEmail = factura;

            limpiarFormulario();
            cargarTodosLosProductos();

            if (clienteParaEmail.getEmail() != null && !clienteParaEmail.getEmail().trim().isEmpty()) {
                preguntarEnviarComprobante(facturaParaEmail, clienteParaEmail);
            }
            
        } catch (Exception e) {
            mostrarError("Error al generar o enviar el comprobante:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Pregunta al usuario si desea enviar el comprobante de venta por email.
     * 
     * @param factura Factura generada
     * @param cliente Cliente que recibira el email
     */
    private void preguntarEnviarComprobante(Factura factura, Cliente cliente) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Enviar Comprobante");
        confirmacion.setHeaderText("Desea enviar el comprobante por email?");
        confirmacion.setContentText(
            String.format(
                "Cliente: %s\n" +
                "Email: %s\n" +
                "Comprobante: %s",
                cliente.getNombre(),
                cliente.getEmail(),
                factura.getNumeroComprobante()
            )
        );
        
        ButtonType btnEnviar = new ButtonType("Enviar");
        ButtonType btnNoEnviar = new ButtonType("No enviar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        confirmacion.getButtonTypes().setAll(btnEnviar, btnNoEnviar);
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == btnEnviar) {
                enviarEmailEnSegundoPlano(factura, cliente);
            }
        });
    }

    /**
     * Envia el comprobante por email en un hilo separado para no bloquear la interfaz.
     * Muestra progreso y resultado de la operacion.
     * 
     * @param factura Factura a enviar
     * @param cliente Cliente destinatario
     */
    private void enviarEmailEnSegundoPlano(Factura factura, Cliente cliente) {
        Alert progress = new Alert(Alert.AlertType.INFORMATION);
        progress.setTitle("Enviando Email");
        progress.setHeaderText("Generando y enviando comprobante...");
        progress.setContentText("Por favor espere");
        progress.show();
        
        new Thread(() -> {
            try {
                byte[] pdfBytes = GeneradorPDFComprobante.generarComprobanteTicketBytes(factura);
                
                EmailServicio emailServicio = new EmailServicio();
                emailServicio.enviarComprobanteCliente(
                    cliente.getEmail(),
                    cliente.getNombre(),
                    factura.getNumeroComprobante(),
                    pdfBytes
                );
                
                Platform.runLater(() -> {
                    progress.close();
                    mostrarInformacion(
                        String.format(
                            "Comprobante enviado exitosamente a:\n%s",
                            cliente.getEmail()
                        )
                    );
                });
                
            } catch (EmailException e) {
                Platform.runLater(() -> {
                    progress.close();
                    mostrarAdvertencia(
                        "No se pudo enviar el comprobante por email:\n" + e.getMessage()
                    );
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progress.close();
                    mostrarError(
                        "Error al generar o enviar el comprobante:\n" + e.getMessage()
                    );
                });
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Construye el mensaje de confirmacion de venta exitosa con todos los detalles.
     * 
     * @param factura Factura generada
     * @param metodoPago Metodo de pago utilizado
     * @param cambio Cambio a devolver
     * @return Mensaje formateado con los detalles de la venta
     */
    private String construirMensajeVentaExitosa(Factura factura, String metodoPago, double cambio) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("VENTA GUARDADA EXITOSAMENTE\n\n");
        mensaje.append(String.format("Codigo de Venta: %s\n", txtCodigoVenta.getText()));
        mensaje.append(String.format("Comprobante: %s\n\n", factura.getNumeroComprobante()));
        mensaje.append(String.format("Cliente: %s\n", clienteSeleccionado.getNombre()));
        mensaje.append(String.format("Productos: %d items\n", detalleVentaItems.size()));
        mensaje.append(String.format("Subtotal: %s\n", formatoMoneda.format(subtotalNeto)));
        mensaje.append(String.format("Descuento: %s\n", formatoMoneda.format(descuentoGlobalTotal)));
        mensaje.append(String.format("IVA (%.0f%%): %s\n", porcentajeIVA * 100, formatoMoneda.format(montoIVA)));
        mensaje.append(String.format("Total: %s\n", formatoMoneda.format(totalPagar)));
        mensaje.append(String.format("Metodo de Pago: %s\n", metodoPago));
        
        if ("Efectivo".equals(metodoPago) && cambio > 0) {
            mensaje.append(String.format("Cambio: %s", formatoMoneda.format(cambio)));
        }
        
        return mensaje.toString();
    }

    /**
     * Valida que todos los datos necesarios para procesar la venta esten completos y sean correctos.
     * 
     * @return true si la venta es valida, false en caso contrario
     */
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
            mostrarAdvertencia("Debe seleccionar un metodo de pago");
            if (cmbMetodoPago != null) cmbMetodoPago.requestFocus();
            return false;
        }
        
        if (txtMontoRecibido.getText().trim().isEmpty()) {
            mostrarAdvertencia("Debe ingresar el monto recibido");
            txtMontoRecibido.requestFocus();
            return false;
        }
        
        try {
            String montoTexto = txtMontoRecibido.getText().trim().replace(",", "");
            double montoRecibido = Double.parseDouble(montoTexto);
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
            mostrarAdvertencia("El monto recibido debe ser un numero valido");
            txtMontoRecibido.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Cancela la venta actual despues de solicitar confirmacion al usuario.
     * Limpia todos los datos del formulario.
     */
    @FXML
    private void cancelarVenta() {
        if (detalleVentaItems.isEmpty() && clienteSeleccionado == null) {
            mostrarInformacion("No hay ninguna venta en proceso para cancelar");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar Venta");
        confirmacion.setHeaderText("Esta seguro de cancelar esta venta?");
        confirmacion.setContentText(
            String.format(
                "Se perderan todos los datos:\n\n" +
                "Cliente: %s\n" +
                "Productos: %d items\n" +
                "Total: %s",
                clienteSeleccionado != null ? clienteSeleccionado.getNombre() : "Sin seleccionar",
                detalleVentaItems.size(),
                formatoMoneda.format(totalPagar)
            )
        );
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                limpiarFormulario();
                mostrarInformacion("La venta ha sido cancelada");
            }
        });
    }

    /**
     * Limpia todos los campos del formulario y restablece los valores por defecto.
     * Genera un nuevo codigo de venta y actualiza la fecha.
     */
    private void limpiarFormulario() {
        detalleVentaItems.clear();
        
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
            txtIVA.setText("0");
        }
        
        subtotalNeto = 0.0;
        descuentoGlobalTotal = 0.0;
        montoIVA = 0.0;
        totalPagar = 0.0;
        porcentajeIVA = 0.0;
        
        actualizarLabelsResumen();
        
        contadorVentas++;
        generarCodigoVenta();
        
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtFecha.setText(fechaActual.format(formatter));
    }

    /**
     * Muestra un dialogo de error con el mensaje especificado.
     * 
     * @param mensaje Mensaje de error a mostrar
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un dialogo de advertencia con el mensaje especificado.
     * 
     * @param mensaje Mensaje de advertencia a mostrar
     */
    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un dialogo de informacion con el mensaje especificado.
     * 
     * @param mensaje Mensaje informativo a mostrar
     */
    private void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacion");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un dialogo de exito con formato especial.
     * 
     * @param mensaje Mensaje de exito a mostrar
     */
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exito");
        alert.setHeaderText("Operacion Completada");
        alert.setContentText(mensaje);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
            "-fx-background-color: #f0f9ff; " +
            "-fx-border-color: #27ae60; " +
            "-fx-border-width: 2px;"
        );
        
        alert.showAndWait();
    }

    /**
     * Establece el codigo de venta en el campo correspondiente.
     * 
     * @param codigo Codigo de venta a establecer
     */
    public void setCodigoVenta(String codigo) {
        if (txtCodigoVenta != null) {
            txtCodigoVenta.setText(codigo);
        }
    }
    
    /**
     * Obtiene el cliente actualmente seleccionado para la venta.
     * 
     * @return Cliente seleccionado o null si no hay ninguno
     */
    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }
    
    /**
     * Establece el cliente seleccionado para la venta actual.
     * 
     * @param cliente Cliente a seleccionar
     */
    public void setClienteSeleccionado(Cliente cliente) {
        seleccionarCliente(cliente);
    }

    /**
     * Clase interna que representa un item en el detalle de venta.
     * Encapsula la informacion de un producto agregado a la venta con su cantidad,
     * precio, descuento y subtotal.
     * 
     * @author Equipo StockFlow/StockFlow Team
     * @version 1.0
     * @since 2025
     */
    public static class DetalleVentaItem {
        private SimpleIntegerProperty numero;
        private final Producto producto;
        private final SimpleStringProperty nombreProducto;
        private SimpleIntegerProperty cantidad;
        private final SimpleDoubleProperty precioUnitario;
        private SimpleDoubleProperty descuento;
        private SimpleDoubleProperty subtotal;

        /**
         * Constructor de DetalleVentaItem.
         * 
         * @param numero Numero de linea en el detalle
         * @param producto Producto asociado
         * @param cantidad Cantidad del producto
         * @param precioUnitario Precio unitario del producto
         * @param descuento Descuento aplicado en pesos
         * @param subtotal Subtotal de la linea
         */
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

        /**
         * Obtiene el numero de linea.
         * 
         * @return Numero de linea
         */
        public int getNumero() { return numero.get(); }
        
        /**
         * Establece el numero de linea.
         * 
         * @param num Nuevo numero de linea
         */
        public void setNumero(int num) { numero.set(num); }
        
        /**
         * Obtiene la propiedad numero para binding JavaFX.
         * 
         * @return Propiedad numero
         */
        public SimpleIntegerProperty numeroProperty() { return numero; }
        
        /**
         * Obtiene el producto asociado.
         * 
         * @return Producto
         */
        public Producto getProducto() { return producto; }
        
        /**
         * Obtiene el nombre del producto.
         * 
         * @return Nombre del producto
         */
        public String getNombreProducto() { return nombreProducto.get(); }
        
        /**
         * Obtiene la propiedad nombreProducto para binding JavaFX.
         * 
         * @return Propiedad nombreProducto
         */
        public SimpleStringProperty nombreProductoProperty() { return nombreProducto; }
        
        /**
         * Obtiene la cantidad del producto.
         * 
         * @return Cantidad
         */
        public int getCantidad() { return cantidad.get(); }
        
        /**
         * Establece la cantidad del producto.
         * 
         * @param cant Nueva cantidad
         */
        public void setCantidad(int cant) { cantidad.set(cant); }
        
        /**
         * Obtiene la propiedad cantidad para binding JavaFX.
         * 
         * @return Propiedad cantidad
         */
        public SimpleIntegerProperty cantidadProperty() { return cantidad; }
        
        /**
         * Obtiene el precio unitario del producto.
         * 
         * @return Precio unitario
         */
        public double getPrecioUnitario() { return precioUnitario.get(); }
        
        /**
         * Obtiene la propiedad precioUnitario para binding JavaFX.
         * 
         * @return Propiedad precioUnitario
         */
        public SimpleDoubleProperty precioUnitarioProperty() { return precioUnitario; }
        
        /**
         * Obtiene el descuento aplicado.
         * 
         * @return Descuento en pesos
         */
        public double getDescuento() { return descuento.get(); }
        
        /**
         * Establece el descuento aplicado.
         * 
         * @param desc Nuevo descuento en pesos
         */
        public void setDescuento(double desc) { descuento.set(desc); }
        
        /**
         * Obtiene la propiedad descuento para binding JavaFX.
         * 
         * @return Propiedad descuento
         */
        public SimpleDoubleProperty descuentoProperty() { return descuento; }
        
        /**
         * Obtiene el subtotal de la linea.
         * 
         * @return Subtotal
         */
        public double getSubtotal() { return subtotal.get(); }
        
        /**
         * Establece el subtotal de la linea.
         * 
         * @param sub Nuevo subtotal
         */
        public void setSubtotal(double sub) { subtotal.set(sub); }
        
        /**
         * Obtiene la propiedad subtotal para binding JavaFX.
         * 
         * @return Propiedad subtotal
         */
        public SimpleDoubleProperty subtotalProperty() { return subtotal; }
    }
}