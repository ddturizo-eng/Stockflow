   /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
/**
 * Controlador para la gestión de facturación y consulta de comprobantes.
 * 
 * Este controlador gestiona la interfaz de facturación, permitiendo:
 * - Listar y filtrar facturas por número, cliente o estado
 * - Ver el detalle completo de cada factura
 * - Exportar comprobantes a PDF en formatos A4 y Ticket
 * - Enviar comprobantes por email a los clientes
 * - Anular facturas
 * - Visualizar estadísticas de facturación
 * 
 * La clase utiliza una tabla interactiva con búsqueda en tiempo real
 * y muestra estadísticas de comprobantes pagados, anulados y promedios.
 * 
 * @author Equipo StockFlow
 * @version 1.0
 * @since 1.0
 */
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
import com.mycompany.stockflow.utils.EmailServicio;
import com.mycompany.stockflow.excepciones.EmailException;
import javafx.application.Platform;

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

    /**
     * Constructor del controlador de facturación.
     * 
     * Inicializa los servicios y las listas observables
     * necesarias para la gestión de facturas.
     */
    public FacturacionController() {
        this.facturacionServicio = new FacturacionServicio();
        this.facturaItems = FXCollections.observableArrayList();
        this.facturasFiltradas = FXCollections.observableArrayList();
    }

    /**
     * Inicializa el controlador de la vista.
     * 
     * Se ejecuta cuando el documento FXML es cargado. Realiza
     * las siguientes acciones:
     * - Configura las columnas de la tabla
     * - Configura el ComboBox de estados
     * - Carga todas las facturas disponibles
     * - Actualiza las estadísticas en pantalla
     */
    @FXML
    public void initialize() {
        System.out.println(" FacturacionController inicializado");
        
        configurarTabla();
        configurarComboBoxEstado();
        cargarFacturas();
        actualizarEstadisticas();
    }

    /**
     * Configura las columnas de la tabla de facturas.
     * 
     * Define el binding entre las propiedades del modelo y las columnas,
     * aplica formato de moneda a la columna de total y colorea los estados.
     * Los colores utilizados son:
     * - Verde (#27AE60) para PAGADA
     * - Rojo (#E74C3C) para ANULADA
     * - Naranja (#F39C12) para PENDIENTE
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
     * Configura el ComboBox de filtrado por estado.
     * 
     * Carga los estados disponibles: Todos, PAGADA, PENDIENTE, ANULADA
     * y establece "Todos" como valor por defecto.
     */
    private void configurarComboBoxEstado() {
        cmbEstado.setItems(FXCollections.observableArrayList("Todos", "PAGADA", "PENDIENTE", "ANULADA"));
        cmbEstado.setValue("Todos");
    }

    /**
     * Carga todas las facturas desde el servicio de facturación.
     * 
     * Obtiene la lista de facturas de la base de datos, las convierte
     * en FacturaItem para mostrar en la tabla y actualiza las listas.
     * Maneja excepciones mostrando un mensaje de error al usuario.
     */
    private void cargarFacturas() {
        try {
            facturaItems.clear();
            List<Factura> facturas = facturacionServicio.listarFacturas();
            
            for (Factura factura : facturas) {
                facturaItems.add(new FacturaItem(factura));
            }
            
            facturasFiltradas.setAll(facturaItems);
            
            System.out.println(" Facturas cargadas: " + facturas.size());
            
        } catch (Exception e) {
            mostrarError("Error al cargar facturas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Busca y filtra facturas según los criterios especificados.
     * 
     * Aplica los siguientes filtros:
     * - Por número de comprobante (búsqueda parcial)
     * - Por nombre del cliente o cédula (búsqueda parcial)
     * - Por estado (exacto)
     * 
     * Muestra un mensaje informativo con la cantidad de resultados encontrados.
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
     * Limpia todos los filtros de búsqueda.
     * 
     * Restaura los campos de búsqueda a su estado inicial,
     * establece el estado en "Todos" y recarga la lista completa de facturas.
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
     * Actualiza las estadísticas mostradas en pantalla.
     * 
     * Calcula y actualiza:
     * - Total de comprobantes en la lista filtrada
     * - Total facturado (suma de comprobantes PAGADAS)
     * - Total de comprobantes anulados
     * - Ticket promedio (total facturado / cantidad de comprobantes pagados)
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
     * Muestra el detalle completo de una factura seleccionada.
     * 
     * Obtiene la factura seleccionada en la tabla, busca su información
     * completa en la base de datos y abre un diálogo con los detalles.
     * Muestra una advertencia si no se ha seleccionado ninguna factura.
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
     * Muestra el detalle formateado de una factura en un diálogo.
     * 
     * Presenta la información de la factura en formato de comprobante con:
     * - Encabezado de la empresa
     * - Número y fecha del comprobante
     * - Datos del cliente
     * - Detalle de productos con cantidad, precio unitario y subtotal
     * - Cálculos: subtotal, descuento, IVA y total
     * - Información de pago (efectivo con cambio o tarjeta)
     * 
     * @param factura La factura cuyo detalle se desea mostrar
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
     * Exporta la factura seleccionada a PDF en formato A4.
     * 
     * Permite al usuario seleccionar la ubicación y nombre del archivo
     * para guardar el comprobante en formato A4 estándar. Después de 
     * generar el PDF, pregunta si desea abrir el archivo.
     */
    @FXML
    private void imprimirComprobanteA4() {
        FacturaItem facturaSeleccionada = tblFacturas.getSelectionModel().getSelectedItem();
        
        if (facturaSeleccionada == null) {
            mostrarAdvertencia("️ Seleccione una factura para imprimir");
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
                
                mostrarExito("Comprobante PDF (A4) generado exitosamente:\n" + archivo.getAbsolutePath());
                
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
     * Exporta la factura seleccionada a PDF en formato Ticket.
     * 
     * Permite al usuario seleccionar la ubicación y nombre del archivo
     * para guardar el comprobante en formato de ticket (más compacto).
     * Después de generar el PDF, pregunta si desea abrir el archivo.
     */
    @FXML
    private void imprimirComprobanteTicket() {
        FacturaItem facturaSeleccionada = tblFacturas.getSelectionModel().getSelectedItem();
        
        if (facturaSeleccionada == null) {
            mostrarAdvertencia("Seleccione una factura para imprimir");
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
                
                mostrarExito("Comprobante PDF (Ticket) generado exitosamente:\n" + archivo.getAbsolutePath());
                
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
     * Anula una factura previamente registrada.
     * 
     * Solicita confirmación del usuario antes de anular la factura.
     * No permite anular facturas que ya están anuladas.
     * Una vez anulada, actualiza la lista y muestra un mensaje de éxito.
     */
    @FXML
    private void anularFactura() {
        FacturaItem facturaSeleccionada = tblFacturas.getSelectionModel().getSelectedItem();
        
        if (facturaSeleccionada == null) {
            mostrarAdvertencia("️ Seleccione una factura para anular");
            return;
        }
        
        if ("ANULADA".equals(facturaSeleccionada.getEstado())) {
            mostrarAdvertencia(" Esta factura ya está anulada");
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
     * Actualiza la lista de facturas recargando datos desde la base de datos.
     * 
     * Carga nuevamente todas las facturas, limpia los filtros aplicados
     * y actualiza las estadísticas. Muestra un mensaje informativo al finalizar.
     */
    @FXML
    private void actualizarLista() {
        cargarFacturas();
        limpiarFiltros();
        mostrarInformacion("Lista actualizada");
    }

    /**
     * Envía el comprobante de la factura seleccionada por email al cliente.
     * 
     * Valida que:
     * - Se haya seleccionado una factura
     * - El cliente tenga email registrado
     * 
     * Solicita confirmación antes de enviar y ejecuta el envío en un hilo
     * separado para no bloquear la interfaz. Maneja excepciones de email
     * mostrando mensajes informativos.
     */
    @FXML
    private void enviarComprobanteEmail() {
        FacturaItem facturaSeleccionada = tblFacturas.getSelectionModel().getSelectedItem();

        if (facturaSeleccionada == null) {
            mostrarAdvertencia("️ Seleccione una factura para enviar por email");
            return;
        }

        try {
            // Buscar la factura completa
            Factura factura = facturacionServicio.buscarFactura(facturaSeleccionada.getNumeroComprobante());

            // Validar que el cliente tenga email
            String emailCliente = factura.getVenta().getCliente().getEmail();

            if (emailCliente == null || emailCliente.trim().isEmpty()) {
                mostrarAdvertencia(
                    "️ El cliente no tiene email registrado\n\n" +
                    "Cliente: " + factura.getNombreCliente() + "\n" +
                    "Cédula: " + factura.getCedulaCliente() + "\n\n" +
                    "Por favor, actualice la información del cliente."
                );
                return;
            }

            // Confirmar envío
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Enviar Comprobante");
            confirmacion.setHeaderText("¿Desea enviar el comprobante por email?");
            confirmacion.setContentText(
                String.format(
                    "Cliente: %s\n" +
                    "Email: %s\n" +
                    "Comprobante: %s\n" +
                    "Total: %s",
                    factura.getNombreCliente(),
                    emailCliente,
                    factura.getNumeroComprobante(),
                    formatoMoneda.format(factura.getTotal())
                )
            );

            ButtonType btnEnviar = new ButtonType("Enviar");
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmacion.getButtonTypes().setAll(btnEnviar, btnCancelar);

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == btnEnviar) {
                    enviarEmailEnSegundoPlano(factura, emailCliente);
                }
            });

        } catch (Exception e) {
            mostrarError("Error al procesar la factura:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Envía el email en un hilo separado para no bloquear la interfaz.
     * 
     * Genera el PDF del comprobante en memoria, lo envía por email
     * al destinatario especificado y actualiza la UI con el resultado.
     * Maneja excepciones de email mostrando mensajes informativos.
     * 
     * @param factura La factura cuyo comprobante se enviará
     * @param emailDestino La dirección de email del cliente
     */
    private void enviarEmailEnSegundoPlano(Factura factura, String emailDestino) {
        // Mostrar diálogo de progreso
        Alert progress = new Alert(Alert.AlertType.INFORMATION);
        progress.setTitle("Enviando Email");
        progress.setHeaderText("Generando y enviando comprobante...");
        progress.setContentText("Por favor espere");
        progress.show();

        // Ejecutar en hilo separado
        new Thread(() -> {
            try {
                // Generar PDF en memoria
                byte[] pdfBytes = GeneradorPDFComprobante.generarComprobanteTicketBytes(factura);

                // Enviar email
                EmailServicio emailServicio = new EmailServicio();
                emailServicio.enviarComprobanteCliente(
                    emailDestino,
                    factura.getNombreCliente(),
                    factura.getNumeroComprobante(),
                    pdfBytes
                );

                // Actualizar UI en el hilo principal
                Platform.runLater(() -> {
                    progress.close();
                    mostrarExito(
                        String.format(
                            " Comprobante enviado exitosamente\n\n" +
                            "Destinatario: %s\n" +
                            "Email: %s\n" +
                            "Comprobante: %s",
                            factura.getNombreCliente(),
                            emailDestino,
                            factura.getNumeroComprobante()
                        )
                    );
                });

            } catch (EmailException e) {
                Platform.runLater(() -> {
                    progress.close();
                    mostrarAdvertencia(
                        "️ No se pudo enviar el comprobante por email:\n\n" + 
                        e.getMessage() + "\n\n" +
                        "Verifique:\n" +
                        "• Conexión a Internet\n" +
                        "• Email del destinatario\n" +
                        "• Configuración del servidor de correo"
                    );
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progress.close();
                    mostrarError(
                        "Error al generar o enviar el comprobante:\n\n" + 
                        e.getMessage()
                    );
                });
                e.printStackTrace();
            }
        }).start();
    }
    // MÉTODOS DE ALERTAS 
    
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

    //  CLASE INTERNA 
    
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