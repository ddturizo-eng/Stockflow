package com.mycompany.stockflow;

import com.mycompany.stockflow.Modelo.Factura;
import com.mycompany.stockflow.Modelo.DetalleVenta;
import com.mycompany.stockflow.Logica.FacturacionServicio;
import com.mycompany.stockflow.utils.GeneradorPDFComprobante;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class FacturacionController {

    // FXML Components - Filtros
    @FXML private TextField txtBuscarNumero;
    @FXML private TextField txtBuscarCliente;
    @FXML private ComboBox<String> cmbEstado;

    // FXML Components - Tabla
    @FXML private TableView<FacturaItem> tblFacturas;
    @FXML private TableColumn<FacturaItem, String> colNumero;
    @FXML private TableColumn<FacturaItem, String> colFecha;
    @FXML private TableColumn<FacturaItem, String> colCliente;
    @FXML private TableColumn<FacturaItem, String> colCedula;
    @FXML private TableColumn<FacturaItem, Double> colTotal;
    @FXML private TableColumn<FacturaItem, String> colMetodoPago;
    @FXML private TableColumn<FacturaItem, String> colEstado;

    // FXML Components - Labels (Estadísticas)
    @FXML private Label lblTotalComprobantes;
    @FXML private Label lblTotalFacturado;
    @FXML private Label lblTotalAnulados;
    @FXML private Label lblTicketPromedio;

    // Servicios y datos
    private FacturacionServicio facturacionServicio;
    private ObservableList<FacturaItem> facturaItems;
    private ObservableList<FacturaItem> facturasFiltradas;
    private DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");

    public FacturacionController() {
        this.facturacionServicio = new FacturacionServicio();
        this.facturaItems = FXCollections.observableArrayList();
        this.facturasFiltradas = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        System.out.println("✅ FacturacionController inicializado");
        
        configurarTabla();
        configurarComboBoxEstado();
        cargarFacturas();
        actualizarEstadisticas();
    }

    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroComprobante"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        // Formato de moneda para la columna Total
        colTotal.setCellFactory(col -> new TableCell<FacturaItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatoMoneda.format(item));
                }
            }
        });
        
        // Colorear el estado
        colEstado.setCellFactory(col -> new TableCell<FacturaItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("PAGADA".equals(item)) {
                        setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                    } else if ("ANULADA".equals(item)) {
                        setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #F39C12; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        tblFacturas.setItems(facturasFiltradas);
    }

    /**
     * Configura el ComboBox de estados
     */
    private void configurarComboBoxEstado() {
        cmbEstado.setItems(FXCollections.observableArrayList("Todos", "PAGADA", "PENDIENTE", "ANULADA"));
        cmbEstado.setValue("Todos");
    }

    /**
     * Carga todas las facturas desde el servicio
     */
    private void cargarFacturas() {
        try {
            facturaItems.clear();
            List<Factura> facturas = facturacionServicio.listarFacturas();
            
            for (Factura factura : facturas) {
                facturaItems.add(new FacturaItem(factura));
            }
            
            facturasFiltradas.setAll(facturaItems);
            
            System.out.println("📊 Facturas cargadas: " + facturas.size());
            
        } catch (Exception e) {
            mostrarError("Error al cargar facturas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Busca facturas según los filtros aplicados
     */
    @FXML
    private void buscarFacturas() {
        String numeroBuscar = txtBuscarNumero.getText().trim().toLowerCase();
        String clienteBuscar = txtBuscarCliente.getText().trim().toLowerCase();
        String estadoSeleccionado = cmbEstado.getValue();
        
        List<FacturaItem> resultados = facturaItems.stream()
            .filter(item -> {
                boolean coincideNumero = numeroBuscar.isEmpty() || 
                    item.getNumeroComprobante().toLowerCase().contains(numeroBuscar);
                
                boolean coincideCliente = clienteBuscar.isEmpty() || 
                    item.getCliente().toLowerCase().contains(clienteBuscar) ||
                    item.getCedula().toLowerCase().contains(clienteBuscar);
                
                boolean coincideEstado = "Todos".equals(estadoSeleccionado) || 
                    item.getEstado().equals(estadoSeleccionado);
                
                return coincideNumero && coincideCliente && coincideEstado;
            })
            .collect(Collectors.toList());
        
        facturasFiltradas.setAll(resultados);
        actualizarEstadisticas();
        
        if (resultados.isEmpty()) {
            mostrarInformacion("No se encontraron facturas con los criterios especificados");
        } else {
            mostrarInformacion("Se encontraron " + resultados.size() + " comprobantes");
        }
    }

    /**
     * Limpia los filtros de búsqueda
     */
    @FXML
    private void limpiarFiltros() {
        txtBuscarNumero.clear();
        txtBuscarCliente.clear();
        cmbEstado.setValue("Todos");
        facturasFiltradas.setAll(facturaItems);
        actualizarEstadisticas();
    }

    /**
     * Actualiza las estadísticas en pantalla
     */
    private void actualizarEstadisticas() {
        // Total de comprobantes
        int totalComprobantes = facturasFiltradas.size();
        
        // Total facturado (solo PAGADAS)
        double totalFacturado = facturasFiltradas.stream()
            .filter(item -> "PAGADA".equals(item.getEstado()))
            .mapToDouble(FacturaItem::getTotal)
            .sum();
        
        // Total anulados
        long totalAnulados = facturasFiltradas.stream()
            .filter(item -> "ANULADA".equals(item.getEstado()))
            .count();
        
        // Ticket promedio (solo PAGADAS)
        double ticketPromedio = 0;
        long comprobantesValidos = facturasFiltradas.stream()
            .filter(item -> "PAGADA".equals(item.getEstado()))
            .count();
        
        if (comprobantesValidos > 0) {
            ticketPromedio = totalFacturado / comprobantesValidos;
        }
        
        // Actualizar labels
        lblTotalComprobantes.setText(String.valueOf(totalComprobantes));
        lblTotalFacturado.setText(formatoMoneda.format(totalFacturado));
        lblTotalAnulados.setText(String.valueOf(totalAnulados));
        lblTicketPromedio.setText(formatoMoneda.format(ticketPromedio));
    }

    /**
     * Muestra el detalle de la factura seleccionada
     */
    @FXML
    private void verDetalleFactura() {
        FacturaItem facturaSeleccionada = tblFacturas.getSelectionModel().getSelectedItem();
        
        if (facturaSeleccionada == null) {
            mostrarAdvertencia("⚠️ Seleccione una factura para ver el detalle");
            return;
        }
        
        try {
            Factura factura = facturacionServicio.buscarFactura(facturaSeleccionada.getNumeroComprobante());
            mostrarDetalleFactura(factura);
            
        } catch (Exception e) {
            mostrarError("Error al cargar el detalle: " + e.getMessage());
        }
    }

    /**
     * Muestra el detalle completo de una factura en un diálogo
     */
    private void mostrarDetalleFactura(Factura factura) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("════════════════════════════════════════\n");
        detalle.append("              STOCKFLOW\n");
        detalle.append("   Sistema de Gestión de Inventario\n");
        detalle.append("════════════════════════════════════════\n");
        detalle.append("       COMPROBANTE DE VENTA\n");
        detalle.append("   (No válido como factura fiscal)\n\n");
        detalle.append("Nro: ").append(factura.getNumeroComprobante()).append("\n");
        detalle.append("Fecha: ").append(factura.getFechaFormateada()).append("\n");
        detalle.append("Estado: ").append(factura.getEstado()).append("\n");
        detalle.append("────────────────────────────────────────\n");
        detalle.append("CLIENTE:\n");
        detalle.append("  ").append(factura.getNombreCliente()).append("\n");
        detalle.append("  CC: ").append(factura.getCedulaCliente()).append("\n");
        detalle.append("────────────────────────────────────────\n");
        detalle.append("PRODUCTOS:\n\n");
        
        if (factura.getVenta() != null && factura.getVenta().getDetalles() != null) {
            int num = 1;
            for (DetalleVenta detalle2 : factura.getVenta().getDetalles()) {
                detalle.append(String.format("%d. %s\n", num++, detalle2.getProducto().getNombre()));
                detalle.append(String.format("   Cant: %d x %s = %s\n", 
                    detalle2.getCantidad(),
                    formatoMoneda.format(detalle2.getPrecioUnitario()),
                    formatoMoneda.format(detalle2.getSubtotal())));
            }
        }
        
        detalle.append("\n────────────────────────────────────────\n");
        detalle.append(String.format("                Subtotal: %s\n", formatoMoneda.format(factura.getSubtotal())));
        
        if (factura.getDescuento() > 0) {
            detalle.append(String.format("              Descuento: -%s\n", formatoMoneda.format(factura.getDescuento())));
        }
        
        detalle.append(String.format("             IVA (16%%): %s\n", formatoMoneda.format(factura.getIva())));
        detalle.append("────────────────────────────────────────\n");
        detalle.append(String.format("                  TOTAL: %s\n", formatoMoneda.format(factura.getTotal())));
        detalle.append("════════════════════════════════════════\n");
        detalle.append("Método de Pago: ").append(factura.getMetodoPago()).append("\n");
        
        if ("Efectivo".equals(factura.getMetodoPago())) {
            detalle.append("Recibido: ").append(formatoMoneda.format(factura.getMontoRecibido())).append("\n");
            detalle.append("Cambio: ").append(formatoMoneda.format(factura.getCambio())).append("\n");
        }
        
        detalle.append("════════════════════════════════════════\n");
        detalle.append("        ¡Gracias por su compra!\n");
        detalle.append("════════════════════════════════════════\n");
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle de Comprobante");
        alert.setHeaderText("Comprobante: " + factura.getNumeroComprobante());
        alert.setContentText(detalle.toString());
        
        // Hacer el diálogo más grande
        alert.getDialogPane().setPrefWidth(550);
        alert.getDialogPane().setPrefHeight(650);
        
        alert.showAndWait();
    }

    /**
     * Imprime o exporta la factura a PDF formato A4
     */
    @FXML
    private void imprimirComprobanteA4() {
        FacturaItem facturaSeleccionada = tblFacturas.getSelectionModel().getSelectedItem();
        
        if (facturaSeleccionada == null) {
            mostrarAdvertencia("⚠️ Seleccione una factura para imprimir");
            return;
        }
        
        try {
            Factura factura = facturacionServicio.buscarFactura(facturaSeleccionada.getNumeroComprobante());
            
            // Selector de archivo
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Comprobante PDF (A4)");
            fileChooser.setInitialFileName("Comprobante_" + factura.getNumeroComprobante() + "_A4.pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File archivo = fileChooser.showSaveDialog(tblFacturas.getScene().getWindow());
            
            if (archivo != null) {
                // Generar PDF en formato A4
                GeneradorPDFComprobante.generarComprobanteA4(factura, archivo.getAbsolutePath());
                
                mostrarExito("✅ Comprobante PDF (A4) generado exitosamente:\n" + archivo.getAbsolutePath());
                
                // Preguntar si desea abrir el archivo
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("PDF Generado");
                confirmacion.setHeaderText("Comprobante generado exitosamente");
                confirmacion.setContentText("¿Desea abrir el archivo PDF?");
                
                confirmacion.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            java.awt.Desktop.getDesktop().open(archivo);
                        } catch (Exception e) {
                            mostrarError("No se pudo abrir el archivo: " + e.getMessage());
                        }
                    }
                });
            }
            
        } catch (Exception e) {
            mostrarError("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Imprime o exporta la factura a PDF formato Ticket
     */
    @FXML
    private void imprimirComprobanteTicket() {
        FacturaItem facturaSeleccionada = tblFacturas.getSelectionModel().getSelectedItem();
        
        if (facturaSeleccionada == null) {
            mostrarAdvertencia("⚠️ Seleccione una factura para imprimir");
            return;
        }
        
        try {
            Factura factura = facturacionServicio.buscarFactura(facturaSeleccionada.getNumeroComprobante());
            
            // Selector de archivo
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Comprobante PDF (Ticket)");
            fileChooser.setInitialFileName("Comprobante_" + factura.getNumeroComprobante() + "_Ticket.pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File archivo = fileChooser.showSaveDialog(tblFacturas.getScene().getWindow());
            
            if (archivo != null) {
                // Generar PDF en formato Ticket
                GeneradorPDFComprobante.generarComprobanteTicket(factura, archivo.getAbsolutePath());
                
                mostrarExito("✅ Comprobante PDF (Ticket) generado exitosamente:\n" + archivo.getAbsolutePath());
                
                // Preguntar si desea abrir el archivo
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("PDF Generado");
                confirmacion.setHeaderText("Comprobante generado exitosamente");
                confirmacion.setContentText("¿Desea abrir el archivo PDF?");
                
                confirmacion.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            java.awt.Desktop.getDesktop().open(archivo);
                        } catch (Exception e) {
                            mostrarError("No se pudo abrir el archivo: " + e.getMessage());
                        }
                    }
                });
            }
            
        } catch (Exception e) {
            mostrarError("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Anula una factura
     */
    @FXML
    private void anularFactura() {
        FacturaItem facturaSeleccionada = tblFacturas.getSelectionModel().getSelectedItem();
        
        if (facturaSeleccionada == null) {
            mostrarAdvertencia("⚠️ Seleccione una factura para anular");
            return;
        }
        
        if ("ANULADA".equals(facturaSeleccionada.getEstado())) {
            mostrarAdvertencia("⚠️ Esta factura ya está anulada");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Anular Factura");
        confirmacion.setHeaderText("¿Está seguro de anular esta factura?");
        confirmacion.setContentText("Factura: " + facturaSeleccionada.getNumeroComprobante() + 
                                   "\nCliente: " + facturaSeleccionada.getCliente() +
                                   "\nTotal: " + formatoMoneda.format(facturaSeleccionada.getTotal()) +
                                   "\n\n⚠️ Esta acción no se puede deshacer");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    facturacionServicio.anularFactura(facturaSeleccionada.getNumeroComprobante());
                    mostrarExito("✅ Factura anulada exitosamente");
                    actualizarLista();
                } catch (Exception e) {
                    mostrarError("Error al anular factura: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Actualiza la lista de facturas
     */
    @FXML
    private void actualizarLista() {
        cargarFacturas();
        limpiarFiltros();
        mostrarInformacion("✅ Lista actualizada");
    }

    // ==================== MÉTODOS DE ALERTAS ====================
    
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
        alert.setTitle("✅ Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ==================== CLASE INTERNA ====================
    
    /**
     * Clase interna para representar items en la tabla
     */
    public static class FacturaItem {
        private final Factura factura;
        private final SimpleStringProperty numeroComprobante;
        private final SimpleStringProperty fecha;
        private final SimpleStringProperty cliente;
        private final SimpleStringProperty cedula;
        private final SimpleDoubleProperty total;
        private final SimpleStringProperty metodoPago;
        private final SimpleStringProperty estado;

        public FacturaItem(Factura factura) {
            this.factura = factura;
            this.numeroComprobante = new SimpleStringProperty(factura.getNumeroComprobante());
            this.fecha = new SimpleStringProperty(factura.getFechaFormateada());
            this.cliente = new SimpleStringProperty(factura.getNombreCliente());
            this.cedula = new SimpleStringProperty(factura.getCedulaCliente());
            this.total = new SimpleDoubleProperty(factura.getTotal());
            this.metodoPago = new SimpleStringProperty(factura.getMetodoPago());
            this.estado = new SimpleStringProperty(factura.getEstado());
        }

        public String getNumeroComprobante() { return numeroComprobante.get(); }
        public SimpleStringProperty numeroComprobanteProperty() { return numeroComprobante; }
        
        public String getFecha() { return fecha.get(); }
        public SimpleStringProperty fechaProperty() { return fecha; }
        
        public String getCliente() { return cliente.get(); }
        public SimpleStringProperty clienteProperty() { return cliente; }
        
        public String getCedula() { return cedula.get(); }
        public SimpleStringProperty cedulaProperty() { return cedula; }
        
        public double getTotal() { return total.get(); }
        public SimpleDoubleProperty totalProperty() { return total; }
        
        public String getMetodoPago() { return metodoPago.get(); }
        public SimpleStringProperty metodoPagoProperty() { return metodoPago; }
        
        public String getEstado() { return estado.get(); }
        public SimpleStringProperty estadoProperty() { return estado; }
        
        public Factura getFactura() { return factura; }
    }
}