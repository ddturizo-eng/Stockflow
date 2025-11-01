package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.*;
import com.mycompany.stockflow.Modelo.*;
import com.mycompany.stockflow.Persistencia.AnalisisRepositorio;
import com.mycompany.stockflow.excepciones.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para Inteligencia de Negocios
 * Gestiona análisis estadísticos, gráficas y recomendaciones con IA
 */
public class InteligenciaNegocioController {
    
    // COMPONENTES FXML
    
    @FXML private Label lblEstadoConexion;
    @FXML private Label lblFechaAnalisis;
    @FXML private ComboBox<String> cbPeriodo;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private TabPane tabPane;
    @FXML private TextArea txtAnalisisIA;
    @FXML private TextArea txtMetricas;
    @FXML private TableView<Recomendacion> tableRecomendaciones;
    @FXML private TableColumn<Recomendacion, String> colTipo;
    @FXML private TableColumn<Recomendacion, String> colTitulo;
    @FXML private TableColumn<Recomendacion, String> colPrioridad;
    @FXML private TableColumn<Recomendacion, String> colFecha;
    @FXML private TableColumn<Recomendacion, String> colAplicada;
    @FXML private Button btnGenerarRecomendaciones;
    @FXML private Button btnSoloGraficas;
    @FXML private VBox progressContainer;
    @FXML private Button btnCopiarMetricas;
    @FXML private Button btnGuardarMetricas;
    @FXML private Button btnCopiarAnalisis;
    @FXML private Button btnExportarPDF;
    
    // Gráficas
    @FXML private LineChart<String, Number> chartTendenciaVentas1;
    @FXML private BarChart<String, Number> chartTop5Productos1;
    @FXML private StackedBarChart<String, Number> chartMargenGanancia;
    @FXML private BarChart<String, Number> chartTop5Productos2;
    
    @FXML private ToggleGroup tipoAnalisisGroup;
    
    // SERVICIOS
    
    private InteligenciaNegocioServicio inteligenciaServicio;
    private AnaliticaAvanzadaServicio analiticaServicio;
    private ConfiguracionAIServicio configuracionServicio;
    private ProductoServicio productoServicio;
    private VentaServicio ventaServicio;
    private ClienteServicio clienteServicio;
    private DatosGraficaServicio graficaServicio;
    private com.mycompany.stockflow.utils.GeneradorPDF generadorPDF;
    
    // PERSISTENCIA
    private AnalisisRepositorio analisisRepositorio;
    
    // DATOS Y ESTADO
    
    private ObservableList<Recomendacion> recomendaciones;
    private AnalisisEstadistico ultimoAnalisis;
    private StackPane overlayProgreso;
    private boolean analisisEnProgreso = false;
    
    // INICIALIZACIÓN
    
    @FXML
    public void initialize() {
        inicializarServicios();
        configurarTablas();
        configurarComboBoxes();
        configurarGraficas();
        crearOverlayProgreso();
        verificarConfiguracion();
        cargarDatosGraficasIniciales();
        restaurarAnalisisGuardado();
    }
    
    private void inicializarServicios() {
        inteligenciaServicio = new InteligenciaNegocioServicio();
        analiticaServicio = new AnaliticaAvanzadaServicio();
        configuracionServicio = new ConfiguracionAIServicio();
        productoServicio = new ProductoServicio();
        ventaServicio = new VentaServicio();
        clienteServicio = new ClienteServicio();
        graficaServicio = new DatosGraficaServicio();
        recomendaciones = FXCollections.observableArrayList();
        analisisRepositorio = AnalisisRepositorio.getInstance();
        generadorPDF = new com.mycompany.stockflow.utils.GeneradorPDF();
    }
    
    /**
     * Restaura el último análisis guardado al abrir la vista
     */
    private void restaurarAnalisisGuardado() {
        if (analisisRepositorio.tieneAnalisisActual()) {
            ResultadoAnalisisIA analisisGuardado = analisisRepositorio.obtenerAnalisisActual();
            
            if (analisisGuardado != null) {
                ultimoAnalisis = convertirAAnalisisEstadistico(analisisGuardado);
                
                Platform.runLater(() -> {
                    if (txtAnalisisIA != null) {
                        // Limpiar el texto antes de mostrarlo
                        String textoOriginal = analisisGuardado.getAnalisisTexto();
                        txtAnalisisIA.setText(formatearAnalisisIA(textoOriginal));
                    }
                    
                    if (txtMetricas != null) {
                        actualizarMetricas(ultimoAnalisis);
                    }
                    
                    if (lblFechaAnalisis != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        lblFechaAnalisis.setText("Último análisis: " + 
                            analisisGuardado.getFechaGeneracion().format(formatter));
                    }
                    
                    System.out.println("Análisis restaurado: " + analisisGuardado.getTipoAnalisis());
                });
            }
        }
    }
    
    /**
     * Convierte ResultadoAnalisisIA a AnalisisEstadistico para compatibilidad
     */
    private AnalisisEstadistico convertirAAnalisisEstadistico(ResultadoAnalisisIA resultado) {
        AnalisisEstadistico analisis = new AnalisisEstadistico(resultado.getTipoAnalisis());
        analisis.setResumenIA(resultado.getAnalisisTexto());
        analisis.setMetricas(resultado.getMetricas());
        return analisis;
    }
    
    private void configurarTablas() {
        colTipo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTipo()));
        colTitulo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTitulo()));
        colPrioridad.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPrioridad()));
        colFecha.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFechaFormateada()));
        colAplicada.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isAplicada() ? "Aplicada" : "Pendiente"));
        
        tableRecomendaciones.setItems(recomendaciones);
        
        tableRecomendaciones.setRowFactory(tv -> {
            TableRow<Recomendacion> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleVerDetallesRecomendacion();
                }
            });
            return row;
        });
    }
    
    private void configurarComboBoxes() {
        if (cbPeriodo != null) {
            cbPeriodo.setItems(FXCollections.observableArrayList(
                "Último mes", "Últimos 3 meses", "Últimos 6 meses", 
                "Último año", "Personalizado"
            ));
            cbPeriodo.getSelectionModel().selectFirst();
        }
    }
    
    // OVERLAY DE PROGRESO
    
    /**
     * Crea un overlay modal centrado que cubre toda la pantalla durante el análisis
     */
    private void crearOverlayProgreso() {
        overlayProgreso = new StackPane();
        overlayProgreso.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.7);"
        );
        overlayProgreso.setAlignment(Pos.CENTER);
        overlayProgreso.setVisible(false);
        overlayProgreso.setManaged(false);
        
        VBox contenido = new VBox(20);
        contenido.setAlignment(Pos.CENTER);
        contenido.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 40 60;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 0);"
        );
        
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(80, 80);
        spinner.setStyle("-fx-progress-color: #3B82F6;");
        
        Label lblTitulo = new Label("Analizando con IA");
        lblTitulo.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #1E3A8A;"
        );
        
        Label lblMensaje = new Label("Procesando datos del negocio...\nEsto puede tardar entre 5 y 15 segundos");
        lblMensaje.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #64748B;" +
            "-fx-text-alignment: center;"
        );
        lblMensaje.setWrapText(true);
        lblMensaje.setMaxWidth(300);
        
        contenido.getChildren().addAll(spinner, lblTitulo, lblMensaje);
        overlayProgreso.getChildren().add(contenido);
    }
    
    /**
     * Muestra u oculta el overlay de progreso correctamente centrado
     */
    private void mostrarOverlayProgreso(boolean mostrar) {
        if (overlayProgreso == null) return;
        
        Platform.runLater(() -> {
            try {
                if (mostrar && !analisisEnProgreso) {
                    analisisEnProgreso = true;
                    
                    // Buscar el BorderPane root desde el TabPane
                    javafx.scene.Node current = tabPane;
                    BorderPane borderPaneRoot = null;
                    
                    while (current != null) {
                        if (current instanceof BorderPane) {
                            borderPaneRoot = (BorderPane) current;
                            break;
                        }
                        current = current.getParent();
                    }
                    
                    if (borderPaneRoot != null) {
                        // Crear un StackPane temporal como contenedor
                        StackPane overlayContainer = new StackPane();
                        overlayContainer.setAlignment(Pos.CENTER);
                        
                        // Copiar el contenido actual del center
                        javafx.scene.Node centerContent = borderPaneRoot.getCenter();
                        
                        // Agregar el overlay al container
                        overlayContainer.getChildren().addAll(centerContent, overlayProgreso);
                        
                        // Configurar el overlay
                        overlayProgreso.prefWidthProperty().bind(overlayContainer.widthProperty());
                        overlayProgreso.prefHeightProperty().bind(overlayContainer.heightProperty());
                        overlayProgreso.setVisible(true);
                        overlayProgreso.setManaged(true);
                        
                        // Reemplazar el center con el container
                        borderPaneRoot.setCenter(overlayContainer);
                        
                    } else {
                        // Fallback: mostrar solo el progress indicator
                        mostrarProgreso(true);
                    }
                    
                } else if (!mostrar && analisisEnProgreso) {
                    analisisEnProgreso = false;
                    
                    // Buscar el BorderPane root
                    javafx.scene.Node current = tabPane;
                    BorderPane borderPaneRoot = null;
                    
                    while (current != null) {
                        if (current instanceof BorderPane) {
                            borderPaneRoot = (BorderPane) current;
                            break;
                        }
                        current = current.getParent();
                    }
                    
                    if (borderPaneRoot != null && borderPaneRoot.getCenter() instanceof StackPane) {
                        StackPane container = (StackPane) borderPaneRoot.getCenter();
                        
                        if (container.getChildren().size() > 1) {
                            // Restaurar el contenido original
                            javafx.scene.Node originalContent = container.getChildren().get(0);
                            borderPaneRoot.setCenter(originalContent);
                        }
                    }
                    
                    overlayProgreso.setVisible(false);
                    overlayProgreso.setManaged(false);
                }
            } catch (Exception e) {
                System.err.println("Error al mostrar overlay: " + e.getMessage());
                e.printStackTrace();
                mostrarProgreso(mostrar);
            }
        });
    }
    
    // CONFIGURACIÓN DE GRÁFICAS
    
    private void configurarGraficas() {
        configurarGrafica(chartTendenciaVentas1);
        configurarGrafica(chartTop5Productos1);
        configurarGrafica(chartTop5Productos2);
        configurarGrafica(chartMargenGanancia);
        aplicarEstilosProfesionales();
    }
    
    private void configurarGrafica(Chart chart) {
        if (chart == null) return;
        chart.setAnimated(true);
        
        if (chart instanceof XYChart) {
            XYChart<?, ?> xyChart = (XYChart<?, ?>) chart;
            configurarEjes(xyChart);
        }
    }
    
    private void configurarEjes(XYChart<?, ?> chart) {
        if (chart.getXAxis() instanceof CategoryAxis) {
            CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
            xAxis.setTickLabelRotation(45);
            xAxis.setTickLabelFill(javafx.scene.paint.Color.web("#64748B"));
            xAxis.setTickLabelFont(javafx.scene.text.Font.font("System", 11));
        }
        
        if (chart.getYAxis() instanceof NumberAxis) {
            NumberAxis yAxis = (NumberAxis) chart.getYAxis();
            yAxis.setTickLabelFill(javafx.scene.paint.Color.web("#64748B"));
            yAxis.setTickLabelFont(javafx.scene.text.Font.font("System", 11));
            yAxis.setAutoRanging(true);
        }
    }
    
    private void aplicarEstilosProfesionales() {
        String estiloCSS = 
            ".chart-series-line { " +
            "    -fx-stroke: #3B82F6; " +
            "    -fx-stroke-width: 3px; " +
            "} " +
            ".chart-line-symbol { " +
            "    -fx-background-color: #3B82F6, white; " +
            "    -fx-background-insets: 0, 2; " +
            "    -fx-background-radius: 5px; " +
            "    -fx-padding: 5px; " +
            "} " +
            ".default-color0.chart-bar { " +
            "    -fx-bar-fill: linear-gradient(to bottom, #60A5FA 0%, #3B82F6 100%); " +
            "} " +
            ".default-color1.chart-bar { " +
            "    -fx-bar-fill: linear-gradient(to bottom, #34D399 0%, #10B981 100%); " +
            "}";
        
        aplicarEstiloAGrafica(chartTendenciaVentas1, estiloCSS);
        aplicarEstiloAGrafica(chartTop5Productos1, estiloCSS);
        aplicarEstiloAGrafica(chartTop5Productos2, estiloCSS);
        aplicarEstiloAGrafica(chartMargenGanancia, estiloCSS);
    }
    
    private void aplicarEstiloAGrafica(Chart chart, String estilo) {
        if (chart != null) {
            chart.setStyle(estilo);
        }
    }
    
    // CARGA DE DATOS EN GRÁFICAS
    
    private void cargarDatosGraficasIniciales() {
        try {
            if (chartTendenciaVentas1 != null) cargarGraficaTendenciaVentas();
            if (chartTop5Productos1 != null) cargarGraficaTopProductos();
            if (chartTop5Productos2 != null) cargarGraficaInventarioCritico();
            if (chartMargenGanancia != null) cargarGraficaMargenGananciaReal();
        } catch (Exception e) {
            System.err.println("Error cargando gráficas: " + e.getMessage());
        }
    }
    
    private void cargarGraficaTendenciaVentas() {
        try {
            java.time.LocalDate fin = java.time.LocalDate.now();
            java.time.LocalDate inicio = fin.minusDays(30);
            
            DatosGrafica datos = graficaServicio.generarGraficaTendenciaVentas(inicio, fin);
            chartTendenciaVentas1.getData().clear();
            
            if (!datos.getSeries().isEmpty()) {
                SerieGrafica serieVentas = datos.getSeries().get(0);
                XYChart.Series<String, Number> serie = new XYChart.Series<>();
                serie.setName(serieVentas.getNombre());
                
                for (PuntoGrafica punto : serieVentas.getValores()) {
                    serie.getData().add(new XYChart.Data<>(punto.getEtiqueta(), punto.getValor()));
                }
                
                chartTendenciaVentas1.getData().add(serie);
            }
        } catch (Exception e) {
            System.err.println("Error en tendencia de ventas: " + e.getMessage());
        }
    }
    
    private void cargarGraficaTopProductos() {
        try {
            DatosGrafica datos = graficaServicio.generarGraficaTopProductos(10);
            chartTop5Productos1.getData().clear();
            
            if (!datos.getSeries().isEmpty()) {
                SerieGrafica serie = datos.getSeries().get(0);
                XYChart.Series<String, Number> chartSerie = new XYChart.Series<>();
                chartSerie.setName(serie.getNombre());
                
                for (PuntoGrafica punto : serie.getValores()) {
                    chartSerie.getData().add(new XYChart.Data<>(punto.getEtiqueta(), punto.getValor()));
                }
                
                chartTop5Productos1.getData().add(chartSerie);
            }
        } catch (Exception e) {
            System.err.println("Error en top productos: " + e.getMessage());
        }
    }
    
    private void cargarGraficaInventarioCritico() {
        try {
            var productos = productoServicio.listarProductos();
            chartTop5Productos2.getData().clear();
            
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Stock Crítico");
            
            productos.stream()
                .filter(Producto::tieneStockBajo)
                .sorted((p1, p2) -> Integer.compare(p1.getStock(), p2.getStock()))
                .limit(10)
                .forEach(p -> {
                    String nombre = p.getNombre().length() > 15 ? 
                        p.getNombre().substring(0, 15) + "..." : p.getNombre();
                    serie.getData().add(new XYChart.Data<>(nombre, p.getStock()));
                });
            
            if (serie.getData().isEmpty()) {
                serie.getData().add(new XYChart.Data<>("Sin alertas", 0));
            }
            
            chartTop5Productos2.getData().add(serie);
        } catch (Exception e) {
            System.err.println("Error en inventario crítico: " + e.getMessage());
        }
    }
    
    private void cargarGraficaMargenGananciaReal() {
        try {
            var productos = productoServicio.listarProductos();
            chartMargenGanancia.getData().clear();
            
            XYChart.Series<String, Number> serieCostos = new XYChart.Series<>();
            serieCostos.setName("Costos");
            
            XYChart.Series<String, Number> serieGanancias = new XYChart.Series<>();
            serieGanancias.setName("Ganancias");
            
            List<Producto> topProductos = productos.stream()
                .filter(p -> p.getStock() > 0)
                .sorted((p1, p2) -> Double.compare(
                    p2.getUtilidadTotal(), 
                    p1.getUtilidadTotal()
                ))
                .limit(10)
                .collect(Collectors.toList());
            
            for (Producto p : topProductos) {
                String nombre = p.getNombre().length() > 12 ? 
                    p.getNombre().substring(0, 12) + "..." : p.getNombre();
                
                double costoTotal = p.getInversionTotal();
                double gananciaTotal = p.getUtilidadTotal();
                
                serieCostos.getData().add(new XYChart.Data<>(nombre, costoTotal));
                serieGanancias.getData().add(new XYChart.Data<>(nombre, gananciaTotal));
            }
            
            if (serieCostos.getData().isEmpty()) {
                serieCostos.getData().add(new XYChart.Data<>("Sin datos", 0));
                serieGanancias.getData().add(new XYChart.Data<>("Sin datos", 0));
            }
            
            chartMargenGanancia.getData().addAll(serieCostos, serieGanancias);
            
        } catch (Exception e) {
            System.err.println("Error en margen de ganancia: " + e.getMessage());
        }
    }
    
    private void verificarConfiguracion() {
        if (inteligenciaServicio.verificarConfiguracion()) {
            lblEstadoConexion.setText("● Sistema Listo");
            lblEstadoConexion.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
        } else {
            lblEstadoConexion.setText("● No Configurado");
            lblEstadoConexion.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            
            Platform.runLater(() -> {
                mostrarAlerta("Configuración Requerida", 
                    "Debes configurar la API Key de DeepSeek para usar la IA.", 
                    Alert.AlertType.WARNING);
            });
        }
    }
    
    // HANDLERS DE EVENTOS
    
    @FXML
    private void handleSoloGraficas() {
        if (tabPane != null) {
            tabPane.getSelectionModel().select(0);
        }
        cargarDatosGraficasIniciales();
        mostrarInfo("Gráficas Actualizadas", "Datos recargados exitosamente.");
    }
    
    @FXML
    private void handleAnalisisCompleto() {
        if (analisisEnProgreso) {
            mostrarAdvertencia("Análisis en Progreso", 
                "Ya hay un análisis ejecutándose. Por favor espera a que termine.");
            return;
        }
        
        ejecutarAnalisisConOverlay(() -> {
            ContextoNegocio contexto = crearContextoNegocio();
            return inteligenciaServicio.generarAnalisisCompleto(contexto);
        }, "Completo");
    }
    
    @FXML
    private void handleAnalisisVentas() {
        if (analisisEnProgreso) {
            mostrarAdvertencia("Análisis en Progreso", 
                "Ya hay un análisis ejecutándose. Por favor espera a que termine.");
            return;
        }
        
        ejecutarAnalisisConOverlay(() -> {
            List<Venta> ventas = ventaServicio.listarVentas();
            List<Producto> productos = productoServicio.listarProductos();
            return inteligenciaServicio.analizarVentas(ventas, productos);
        }, "Ventas");
    }
    
    @FXML
    private void handleAnalisisInventario() {
        if (analisisEnProgreso) {
            mostrarAdvertencia("Análisis en Progreso", 
                "Ya hay un análisis ejecutándose. Por favor espera a que termine.");
            return;
        }
        
        ejecutarAnalisisConOverlay(() -> {
            List<Producto> productos = productoServicio.listarProductos();
            return inteligenciaServicio.analizarInventario(productos);
        }, "Inventario");
    }
    
    @FXML
    private void handleGenerarRecomendaciones() {
        if (analisisEnProgreso) {
            mostrarAdvertencia("Análisis en Progreso", 
                "Ya hay un análisis ejecutándose. Por favor espera.");
            return;
        }
        
        mostrarOverlayProgreso(true);
        
        new Thread(() -> {
            try {
                ContextoNegocio contexto = crearContextoNegocio();
                var nuevasRecomendaciones = inteligenciaServicio.generarRecomendacionesAutomaticas(contexto);
                
                Platform.runLater(() -> {
                    recomendaciones.clear();
                    recomendaciones.addAll(nuevasRecomendaciones);
                    tableRecomendaciones.refresh();
                    
                    if (tabPane != null) {
                        tabPane.getSelectionModel().select(3);
                    }
                    
                    actualizarFechaAnalisis();
                    mostrarOverlayProgreso(false);
                    
                    if (nuevasRecomendaciones.isEmpty()) {
                        mostrarInfo("Sin Recomendaciones", "No hay recomendaciones urgentes.");
                    } else {
                        mostrarInfo("Éxito", 
                            String.format("Se generaron %d recomendaciones.", nuevasRecomendaciones.size()));
                    }
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarError("Error", "Error generando recomendaciones: " + e.getMessage());
                    mostrarOverlayProgreso(false);
                });
            }
        }).start();
    }
    
    @FXML
    private void handleVerDetallesRecomendacion() {
        Recomendacion seleccionada = tableRecomendaciones.getSelectionModel().getSelectedItem();
        
        if (seleccionada == null) {
            mostrarAdvertencia("Sin Selección", "Selecciona una recomendación");
            return;
        }
        
        mostrarDetallesRecomendacion(seleccionada);
    }
    
    @FXML
    private void handleMarcarAplicada() {
        Recomendacion seleccionada = tableRecomendaciones.getSelectionModel().getSelectedItem();
        
        if (seleccionada == null) {
            mostrarAdvertencia("Sin Selección", "Selecciona una recomendación");
            return;
        }
        
        if (seleccionada.isAplicada()) {
            mostrarInfo("Ya Aplicada", "Esta recomendación ya fue marcada");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("Marcar como aplicada");
        confirmacion.setContentText("¿Confirmas esta acción?");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                seleccionada.setAplicada(true);
                tableRecomendaciones.refresh();
                mostrarInfo("Éxito", "Recomendación marcada como aplicada");
            }
        });
    }
    
    @FXML
    private void handleLimpiarAplicadas() {
        long count = recomendaciones.stream().filter(Recomendacion::isAplicada).count();
        
        if (count == 0) {
            mostrarInfo("Sin Cambios", "No hay recomendaciones aplicadas");
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setContentText(String.format("¿Eliminar %d recomendaciones?", count));
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                recomendaciones.removeIf(Recomendacion::isAplicada);
                mostrarInfo("Éxito", String.format("%d recomendaciones eliminadas", count));
            }
        });
    }
    
    @FXML
    private void handleLimpiar() {
        if (analisisRepositorio.tieneAnalisisActual()) {
            restaurarAnalisisGuardado();
        }
        
        cargarDatosGraficasIniciales();
        mostrarInfo("Vista Actualizada", "Gráficas recargadas");
    }
    
    @FXML
    private void handleCopiarMetricas() {
        if (txtMetricas == null || txtMetricas.getText().isEmpty()) {
            mostrarAdvertencia("Sin Datos", "No hay métricas para copiar");
            return;
        }
        
        try {
            final javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            final javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(txtMetricas.getText());
            clipboard.setContent(content);
            
            mostrarInfo("Copiado", "Métricas copiadas al portapapeles");
        } catch (Exception e) {
            mostrarError("Error", "No se pudo copiar al portapapeles");
        }
    }
    
    @FXML
    private void handleGuardarMetricas() {
        if (txtMetricas == null || txtMetricas.getText().isEmpty()) {
            mostrarAdvertencia("Sin Datos", "No hay métricas para guardar");
            return;
        }
        
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Guardar Métricas");
            fileChooser.setInitialFileName("metricas_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Archivo de texto", "*.txt")
            );

            java.io.File archivo = fileChooser.showSaveDialog(tabPane.getScene().getWindow());

            if (archivo != null) {
                java.nio.file.Files.write(archivo.toPath(), txtMetricas.getText().getBytes());
                mostrarInfo("Guardado", "Métricas guardadas exitosamente");
            }
        } catch (Exception e) {
            mostrarError("Error", "Error al guardar: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCopiarAnalisis() {
        if (txtAnalisisIA == null || txtAnalisisIA.getText().isEmpty()) {
            mostrarAdvertencia("Sin Datos", "No hay análisis para copiar");
            return;
        }
        
        try {
            final javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            final javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(txtAnalisisIA.getText());
            clipboard.setContent(content);
            
            mostrarInfo("Copiado", "Análisis copiado al portapapeles");
        } catch (Exception e) {
            mostrarError("Error", "No se pudo copiar al portapapeles");
        }
    }
    
    @FXML
    private void handleExportarPDFDirecto() {
        if (ultimoAnalisis == null) {
            mostrarAdvertencia("Sin Datos", "Debes generar un análisis primero");
            return;
        }
        
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Guardar Reporte PDF");
            fileChooser.setInitialFileName("reporte_analisis_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Documento PDF", "*.pdf")
            );

            java.io.File archivo = fileChooser.showSaveDialog(tabPane.getScene().getWindow());

            if (archivo != null) {
                handleExportarPDF(archivo.getAbsolutePath());
            }
        } catch (Exception e) {
            mostrarError("Error", "Error al seleccionar archivo: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExportar() {
        if (ultimoAnalisis == null && recomendaciones.isEmpty()) {
            mostrarAdvertencia("Sin Datos", "No hay datos para exportar");
            return;
        }

        try {
            String contenido = generarReporteExportacion();
            
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Guardar Análisis");
            fileChooser.setInitialFileName("analisis_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
            fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Archivo de texto", "*.txt"),
                new javafx.stage.FileChooser.ExtensionFilter("Documento PDF", "*.pdf")
            );

            java.io.File archivo = fileChooser.showSaveDialog(tabPane.getScene().getWindow());

            if (archivo != null) {
                String ruta = archivo.getAbsolutePath();
                
                if (ruta.endsWith(".pdf")) {
                    // Exportar como PDF
                    handleExportarPDF(ruta);
                } else {
                    // Exportar como TXT
                    java.nio.file.Files.write(archivo.toPath(), contenido.getBytes());
                    mostrarInfo("Éxito", "Análisis exportado:\n" + archivo.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            mostrarError("Error", "Error al exportar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Exporta el análisis como PDF con gráficas
     */
    private void handleExportarPDF(String rutaArchivo) {
        mostrarOverlayProgreso(true);
        
        new Thread(() -> {
            try {
                final String tipoAnalisis = ultimoAnalisis != null ? 
                    ultimoAnalisis.getTipoAnalisis() : "Análisis General";
                
                final String analisisTexto = txtAnalisisIA != null ? 
                    txtAnalisisIA.getText() : "";
                
                final String metricas = txtMetricas != null ? 
                    txtMetricas.getText() : "";
                
                // Recolectar gráficas visibles (compatible con Java 11)
                final List<Chart> graficas = new java.util.ArrayList<>();
                if (chartTendenciaVentas1 != null && chartTendenciaVentas1.isVisible()) {
                    graficas.add(chartTendenciaVentas1);
                }
                if (chartTop5Productos1 != null && chartTop5Productos1.isVisible()) {
                    graficas.add(chartTop5Productos1);
                }
                if (chartMargenGanancia != null && chartMargenGanancia.isVisible()) {
                    graficas.add(chartMargenGanancia);
                }
                if (chartTop5Productos2 != null && chartTop5Productos2.isVisible()) {
                    graficas.add(chartTop5Productos2);
                }
                
                // Generar PDF en hilo de JavaFX (necesario para capturar gráficas)
                Platform.runLater(() -> {
                    try {
                        generadorPDF.generarReporteCompleto(
                            rutaArchivo,
                            tipoAnalisis,
                            analisisTexto,
                            metricas,
                            graficas
                        );
                        
                        mostrarOverlayProgreso(false);
                        mostrarInfo("PDF Generado", 
                            "Reporte exportado exitosamente:\n" + rutaArchivo);
                        
                    } catch (Exception e) {
                        mostrarOverlayProgreso(false);
                        mostrarError("Error al generar PDF", e.getMessage());
                        e.printStackTrace();
                    }
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarOverlayProgreso(false);
                    mostrarError("Error", "Error al preparar PDF: " + e.getMessage());
                });
            }
        }).start();
    }
    
    // MÉTODOS AUXILIARES
    
    /**
     * Ejecuta análisis con overlay visual centrado
     */
    private void ejecutarAnalisisConOverlay(AnalisisTask task, String tipoAnalisis) {
        mostrarOverlayProgreso(true);
        
        if (txtAnalisisIA != null) {
            txtAnalisisIA.setText("Analizando con IA...\nEsto puede tardar 5-15 segundos.");
        }
        
        new Thread(() -> {
            try {
                AnalisisEstadistico analisis = task.ejecutar();
                
                // Guardar en repositorio
                ResultadoAnalisisIA resultadoParaGuardar = convertirAResultadoAnalisisIA(analisis);
                analisisRepositorio.guardar(resultadoParaGuardar);
                
                Platform.runLater(() -> {
                    ultimoAnalisis = analisis;
                    
                    if (txtAnalisisIA != null) {
                        // Limpiar el texto antes de mostrarlo
                        String textoLimpio = limpiarMarkdown(analisis.getResumenIA());
                        txtAnalisisIA.setText(formatearAnalisisIA(analisis.getResumenIA()));
                    }
                    
                    actualizarMetricas(analisis);
                    actualizarFechaAnalisis();
                    
                    if (tabPane != null) {
                        tabPane.getSelectionModel().select(2);
                    }
                    
                    mostrarOverlayProgreso(false);
                    mostrarInfo("Éxito", "Análisis completado y guardado");
                    cargarDatosGraficasIniciales();
                    
                    System.out.println("Análisis guardado en repositorio: " + resultadoParaGuardar.getId());
                });
                
            } catch (ConfiguracionAIFaltanteException e) {
                Platform.runLater(() -> {
                    if (txtAnalisisIA != null) {
                        txtAnalisisIA.setText("ERROR: API Key no configurada\n\n" + e.getMessage());
                    }
                    mostrarOverlayProgreso(false);
                    verificarConfiguracion();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (txtAnalisisIA != null) {
                        txtAnalisisIA.setText("Error: " + e.getMessage());
                    }
                    mostrarOverlayProgreso(false);
                    mostrarError("Error", e.getMessage());
                });
            }
        }).start();
    }
    
    /**
     * Convierte AnalisisEstadistico a ResultadoAnalisisIA para guardar
     */
    private ResultadoAnalisisIA convertirAResultadoAnalisisIA(AnalisisEstadistico analisis) {
        ResultadoAnalisisIA resultado = new ResultadoAnalisisIA();
        resultado.setTipoAnalisis(analisis.getTipoAnalisis());
        resultado.setAnalisisTexto(analisis.getResumenIA());
        resultado.setMetricas(analisis.getMetricas());
        resultado.setFechaGeneracion(LocalDateTime.now());
        return resultado;
    }
    
    private ContextoNegocio crearContextoNegocio() {
        ContextoNegocio contexto = new ContextoNegocio();
        
        try {
            contexto.setProductos(productoServicio.listarProductos());
            contexto.setVentas(ventaServicio.listarVentas());
            contexto.setClientes(clienteServicio.listarClientes());
            contexto.setPeriodoAnalisis("Últimos 30 días");
        } catch (Exception e) {
            mostrarError("Error", "Error creando contexto: " + e.getMessage());
        }
        
        return contexto;
    }
    
    private void mostrarDetallesRecomendacion(Recomendacion rec) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de Recomendación");
        alert.setHeaderText(rec.getTitulo());
        
        StringBuilder contenido = new StringBuilder();
        contenido.append("Tipo: ").append(rec.getTipo()).append("\n");
        contenido.append("Prioridad: ").append(rec.getPrioridad()).append("\n");
        contenido.append("Fecha: ").append(rec.getFechaFormateada()).append("\n");
        contenido.append("Estado: ").append(rec.isAplicada() ? "Aplicada" : "Pendiente").append("\n\n");
        contenido.append("DESCRIPCIÓN:\n").append(rec.getDescripcion()).append("\n\n");
        
        if (rec.getAccionRecomendada() != null) {
            contenido.append("ACCIÓN:\n").append(rec.getAccionRecomendada()).append("\n\n");
        }
        
        if (rec.getJustificacion() != null) {
            contenido.append("JUSTIFICACIÓN:\n").append(rec.getJustificacion());
        }
        
        TextArea textArea = new TextArea(contenido.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
    
    private String formatearAnalisisIA(String analisisTexto) {
        if (analisisTexto == null || analisisTexto.isEmpty()) {
            return "No se pudo generar el análisis.";
        }
        
        StringBuilder formateado = new StringBuilder();
        formateado.append("ANÁLISIS DE INTELIGENCIA ARTIFICIAL\n");
        formateado.append("=".repeat(60)).append("\n\n");
        
        // Limpiar y formatear el texto Markdown
        String textoLimpio = limpiarMarkdown(analisisTexto);
        formateado.append(textoLimpio);
        
        formateado.append("\n\n");
        formateado.append("=".repeat(60)).append("\n");
        formateado.append("Generado: ").append(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
        
        return formateado.toString();
    }
    
    /**
     * Limpia el formato Markdown y convierte a texto plano legible
     */
    private String limpiarMarkdown(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        
        StringBuilder resultado = new StringBuilder();
        String[] lineas = texto.split("\n");
        
        for (String linea : lineas) {
            String lineaLimpia = linea;
            
            // Eliminar asteriscos de negrita/cursiva
            lineaLimpia = lineaLimpia.replaceAll("\\*\\*\\*(.+?)\\*\\*\\*", "$1"); // ***texto***
            lineaLimpia = lineaLimpia.replaceAll("\\*\\*(.+?)\\*\\*", "$1");       // **texto**
            lineaLimpia = lineaLimpia.replaceAll("\\*(.+?)\\*", "$1");             // *texto*
            
            // Convertir encabezados Markdown a texto con formato
            if (lineaLimpia.startsWith("####")) {
                lineaLimpia = "    " + lineaLimpia.replaceFirst("####\\s*", "").toUpperCase();
            } else if (lineaLimpia.startsWith("###")) {
                lineaLimpia = "  • " + lineaLimpia.replaceFirst("###\\s*", "").toUpperCase();
            } else if (lineaLimpia.startsWith("##")) {
                lineaLimpia = "\n" + lineaLimpia.replaceFirst("##\\s*", "").toUpperCase() + "\n" + "-".repeat(50);
            } else if (lineaLimpia.startsWith("#")) {
                lineaLimpia = "\n" + lineaLimpia.replaceFirst("#\\s*", "").toUpperCase() + "\n" + "=".repeat(50);
            }
            
            // Limpiar guiones de listas pero mantener la estructura
            if (lineaLimpia.trim().startsWith("-")) {
                lineaLimpia = lineaLimpia.replaceFirst("-\\s*", "  • ");
            }
            
            // Eliminar emojis de encabezados críticos
            lineaLimpia = lineaLimpia.replaceAll("🔴|⚠️|✅|📊|💡|🎯", "");
            
            resultado.append(lineaLimpia).append("\n");
        }
        
        return resultado.toString().trim();
    }
    
    private void actualizarMetricas(AnalisisEstadistico analisis) {
        if (txtMetricas == null) return;
        
        StringBuilder metricas = new StringBuilder();
        metricas.append("MÉTRICAS DEL ANÁLISIS\n");
        metricas.append("=".repeat(60)).append("\n\n");
        metricas.append("Tipo: ").append(analisis.getTipoAnalisis()).append("\n");
        metricas.append("Fecha: ").append(analisis.getFechaFormateada()).append("\n\n");
        
        if (analisis.getMetricas() != null && !analisis.getMetricas().isEmpty()) {
            metricas.append("DATOS NUMÉRICOS\n");
            metricas.append("-".repeat(60)).append("\n");
            analisis.getMetricas().forEach((clave, valor) -> {
                metricas.append(String.format("%-30s: %s\n", 
                    clave.replace("_", " ").toUpperCase(), formatearValor(valor)));
            });
        }
        
        txtMetricas.setText(metricas.toString());
    }
    
    private String formatearValor(Object valor) {
        if (valor instanceof Double) {
            return String.format("$%.2f", (Double) valor);
        }
        return String.valueOf(valor);
    }
    
    private void actualizarFechaAnalisis() {
        if (lblFechaAnalisis != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            lblFechaAnalisis.setText("Última actualización: " + LocalDateTime.now().format(formatter));
        }
    }
    
    private String generarReporteExportacion() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("════════════════════════════════════════════════════════════════\n");
        reporte.append("           REPORTE DE ANÁLISIS - STOCKFLOW                      \n");
        reporte.append("════════════════════════════════════════════════════════════════\n\n");
        reporte.append("Fecha: ").append(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n\n");
        
        if (ultimoAnalisis != null) {
            reporte.append("════════════════════════════════════════════════════════════════\n");
            reporte.append("ANÁLISIS: ").append(ultimoAnalisis.getTipoAnalisis()).append("\n");
            reporte.append("════════════════════════════════════════════════════════════════\n\n");
            reporte.append(ultimoAnalisis.getResumenIA()).append("\n\n");
        }
        
        if (!recomendaciones.isEmpty()) {
            reporte.append("════════════════════════════════════════════════════════════════\n");
            reporte.append("RECOMENDACIONES (").append(recomendaciones.size()).append(")\n");
            reporte.append("════════════════════════════════════════════════════════════════\n\n");
            
            int i = 1;
            for (Recomendacion rec : recomendaciones) {
                reporte.append(String.format("%d. [%s] %s\n", i++, rec.getPrioridad(), rec.getTitulo()));
                reporte.append("   ").append(rec.getDescripcion()).append("\n\n");
            }
        }
        
        return reporte.toString();
    }
    
    private void mostrarProgreso(boolean visible) {
        if (progressIndicator != null) {
            progressIndicator.setVisible(visible);
            progressIndicator.setManaged(visible);
        }
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, Alert.AlertType.ERROR);
    }
    
    private void mostrarInfo(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, Alert.AlertType.INFORMATION);
    }
    
    private void mostrarAdvertencia(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, Alert.AlertType.WARNING);
    }
    
    @FunctionalInterface
    private interface AnalisisTask {
        AnalisisEstadistico ejecutar() throws Exception;
    }
}