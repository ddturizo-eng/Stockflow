/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import javafx.geometry.Insets;
/**
 * Controlador principal para el módulo de Inteligencia de Negocios.
 * 
 * <p>Esta clase gestiona la visualización y análisis de datos empresariales mediante
 * gráficas estadísticas, análisis con IA y generación de recomendaciones automáticas.</p>
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Generación de gráficas de tendencias de ventas, productos más vendidos e inventario crítico</li>
 *   <li>Análisis mediante IA (análisis completo, ventas e inventario)</li>
 *   <li>Generación automática de recomendaciones de negocio</li>
 *   <li>Exportación de reportes en formato PDF y texto</li>
 *   <li>Persistencia de análisis generados</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1-0
 * @since 2025
 */


public class InteligenciaNegocioController {
    
    private static final int MAX_CARACTERES_NOMBRE = 15;
    
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
    @FXML private LineChart<String, Number> chartTendenciaVentas1;
    @FXML private BarChart<String, Number> chartTop5Productos1;
    @FXML private StackedBarChart<String, Number> chartMargenGanancia;
    @FXML private BarChart<String, Number> chartTop5Productos2;
    @FXML private ToggleGroup tipoAnalisisGroup;
    
    private InteligenciaNegocioServicio inteligenciaServicio;
    private AnaliticaAvanzadaServicio analiticaServicio;
    private ConfiguracionAIServicio configuracionServicio;
    private ProductoServicio productoServicio;
    private VentaServicio ventaServicio;
    private ClienteServicio clienteServicio;
    private DatosGraficaServicio graficaServicio;
    //private com.mycompany.stockflow.utils.GeneradorPDF generadorPDF;
    private AnalisisRepositorio analisisRepositorio;
    
    private ObservableList<Recomendacion> recomendaciones;
    private AnalisisEstadistico ultimoAnalisis;
    private StackPane overlayProgreso;
    private boolean analisisEnProgreso = false;
    
    /**
     * Inicializa el controlador y todos sus componentes.
     * Este método se ejecuta automáticamente después de que se cargue el archivo FXML.
     * 
     * <p>Realiza las siguientes operaciones:</p>
     * <ul>
     *   <li>Inicializa servicios de negocio</li>
     *   <li>Configura tablas, comboboxes y gráficas</li>
     *   <li>Crea overlay de progreso</li>
     *   <li>Verifica configuración de IA</li>
     *   <li>Carga datos iniciales de gráficas</li>
     *   <li>Restaura último análisis guardado</li>
     * </ul>
     */
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
        
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && newTab.getText().contains("Dashboard")) {
                Platform.runLater(() -> {
                    recolorearTodasLasGraficas();
                });
            }
        });
    }
    
    /**
     * Inicializa todos los servicios de negocio necesarios para el funcionamiento del controlador.
     */
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
        //generadorPDF = new com.mycompany.stockflow.utils.GeneradorPDF();
    }
    
    /**
     * Configura las columnas y comportamiento de la tabla de recomendaciones.
     * Establece los cell value factories y el comportamiento de doble clic.
     */
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
    
    /**
     * Configura los valores disponibles en los ComboBox de periodo.
     */
    private void configurarComboBoxes() {
        if (cbPeriodo != null) {
            cbPeriodo.setItems(FXCollections.observableArrayList(
                "Último mes", "Últimos 3 meses", "Últimos 6 meses", 
                "Último año", "Personalizado"
            ));
            cbPeriodo.getSelectionModel().selectFirst();
        }
    }
    
    /**
     * Verifica si la API de IA está configurada correctamente.
     * Actualiza el indicador visual de estado de conexión.
     */
    private void verificarConfiguracion() {
        if (inteligenciaServicio.verificarConfiguracion()) {
            lblEstadoConexion.setText("Online");
            lblEstadoConexion.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
        } else {
            lblEstadoConexion.setText("No Configurado");
            lblEstadoConexion.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            
            Platform.runLater(() -> {
                mostrarAlerta("Configuración Requerida", 
                    "Debes configurar la API Key de DeepSeek para usar la IA.", 
                    Alert.AlertType.WARNING);
            });
        }
    }
    
    /**
     * Crea el overlay de progreso modal que se muestra durante análisis prolongados.
     * El overlay incluye un spinner animado y mensajes informativos.
     */
    private void crearOverlayProgreso() {
        overlayProgreso = new StackPane();
        overlayProgreso.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
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
        
        Label lblMensaje = new Label("Procesando datos del negocio...\nEsto puede tardar un momento");
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
     * Muestra u oculta el overlay de progreso modal.
     * 
     * @param mostrar true para mostrar el overlay, false para ocultarlo
     */
    private void mostrarOverlayProgreso(boolean mostrar) {
        if (overlayProgreso == null) return;
        
        Platform.runLater(() -> {
            try {
                if (mostrar && !analisisEnProgreso) {
                    analisisEnProgreso = true;
                    
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
                        StackPane overlayContainer = new StackPane();
                        overlayContainer.setAlignment(Pos.CENTER);
                        javafx.scene.Node centerContent = borderPaneRoot.getCenter();
                        overlayContainer.getChildren().addAll(centerContent, overlayProgreso);
                        
                        overlayProgreso.prefWidthProperty().bind(overlayContainer.widthProperty());
                        overlayProgreso.prefHeightProperty().bind(overlayContainer.heightProperty());
                        overlayProgreso.setVisible(true);
                        overlayProgreso.setManaged(true);
                        
                        borderPaneRoot.setCenter(overlayContainer);
                    } else {
                        mostrarProgreso(true);
                    }
                    
                } else if (!mostrar && analisisEnProgreso) {
                    analisisEnProgreso = false;
                    
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
    
    /**
     * Configura todas las gráficas del dashboard con estilos y comportamientos profesionales.
     */
    private void configurarGraficas() {
        configurarGrafica(chartTendenciaVentas1);
        configurarGrafica(chartTop5Productos1);
        configurarGrafica(chartTop5Productos2);
        configurarGrafica(chartMargenGanancia);
        aplicarEstilosProfesionales();
    }
    
    /**
     * Configura una gráfica individual con animaciones y ejes formateados.
     * 
     * @param chart la gráfica a configurar
     */
    private void configurarGrafica(Chart chart) {
        if (chart == null) return;
        chart.setAnimated(true);
        
        if (chart instanceof XYChart) {
            XYChart<?, ?> xyChart = (XYChart<?, ?>) chart;
            configurarEjes(xyChart);
        }
    }
    
    /**
     * Configura los ejes X e Y de una gráfica XY.
     * Establece rotación de etiquetas, colores y fuentes.
     * 
     * @param chart la gráfica XY cuyos ejes se van a configurar
     */
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
    
    /**
     * Aplica estilos CSS profesionales a todas las gráficas del dashboard.
     */
    private void aplicarEstilosProfesionales() {
        String estiloCSS = 
            ".chart-series-line { " +
            "    -fx-stroke-width: 3px; " +
            "} " +
            ".chart-line-symbol { " +
            "    -fx-background-insets: 0, 2; " +
            "    -fx-background-radius: 5px; " +
            "    -fx-padding: 5px; " +
            "} ";
        
        if (chartTendenciaVentas1 != null) {
            chartTendenciaVentas1.setStyle(estiloCSS);
        }
        if (chartTop5Productos1 != null) {
            chartTop5Productos1.setStyle(estiloCSS);
        }
        if (chartTop5Productos2 != null) {
            chartTop5Productos2.setStyle(estiloCSS);
        }
        if (chartMargenGanancia != null) {
            chartMargenGanancia.setStyle(estiloCSS);
        }
    }
    
    /**
     * Carga los datos iniciales en todas las gráficas del dashboard.
     * Este método se ejecuta al inicializar el controlador.
     */
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
    
    /**
     * Carga la gráfica de tendencia de ventas con datos de los últimos 30 días.
     * Aplica colores personalizados a las series de datos.
     */
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
                
                final String color = serieVentas.getColor();
                aplicarColorASerie(serie, color);
            }
        } catch (Exception e) {
            System.err.println("Error en tendencia de ventas: " + e.getMessage());
        }
    }
    
    /**
     * Carga la gráfica de productos más vendidos (top 10).
     * Los nombres de productos largos se truncan para mejorar la visualización.
     */
    private void cargarGraficaTopProductos() {
        try {
            DatosGrafica datos = graficaServicio.generarGraficaTopProductos(10);
            chartTop5Productos1.getData().clear();
            
            if (!datos.getSeries().isEmpty()) {
                SerieGrafica serie = datos.getSeries().get(0);
                XYChart.Series<String, Number> chartSerie = new XYChart.Series<>();
                chartSerie.setName(serie.getNombre());
                
                for (PuntoGrafica punto : serie.getValores()) {
                    String nombreTruncado = truncarNombre(punto.getEtiqueta(), MAX_CARACTERES_NOMBRE);
                    chartSerie.getData().add(new XYChart.Data<>(nombreTruncado, punto.getValor()));
                }
                
                chartTop5Productos1.getData().add(chartSerie);
                
                final List<PuntoGrafica> puntos = serie.getValores();
                aplicarColoresABarras(chartSerie, puntos);
            }
        } catch (Exception e) {
            System.err.println("Error en top productos: " + e.getMessage());
        }
    }
    
    /**
     * Carga la gráfica de inventario crítico mostrando productos con stock bajo.
     * Aplica código de colores según el nivel de criticidad:
     * <ul>
     *   <li>Rojo: stock agotado</li>
     *   <li>Naranja: stock por debajo de la mitad del mínimo</li>
     *   <li>Amarillo: stock bajo</li>
     *   <li>Verde: sin alertas</li>
     * </ul>
     */
    private void cargarGraficaInventarioCritico() {
        try {
            var productos = productoServicio.listarProductos();
            chartTop5Productos2.getData().clear();
            
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Stock Crítico");
            
            List<String> colores = new ArrayList<>();
            
            productos.stream()
                .filter(Producto::tieneStockBajo)
                .sorted((p1, p2) -> Integer.compare(p1.getStock(), p2.getStock()))
                .limit(10)
                .forEach(p -> {
                    String nombre = truncarNombre(p.getNombre(), MAX_CARACTERES_NOMBRE);
                    serie.getData().add(new XYChart.Data<>(nombre, p.getStock()));
                    
                    if (p.getStock() == 0) {
                        colores.add("#D0021B");
                    } else if (p.getStock() <= p.getStockMinimo() / 2) {
                        colores.add("#F5A623");
                    } else {
                        colores.add("#FFCC00");
                    }
                });
            
            if (serie.getData().isEmpty()) {
                serie.getData().add(new XYChart.Data<>("Sin alertas", 0));
                colores.add("#10B981");
            }
            
            chartTop5Productos2.getData().add(serie);
            aplicarColoresABarrasDirecto(serie, colores);
            
        } catch (Exception e) {
            System.err.println("Error en inventario crítico: " + e.getMessage());
        }
    }
    
    /**
     * Carga la gráfica de margen de ganancia comparando costos vs ganancias.
     * Muestra los 10 productos con mayor utilidad en formato de barras apiladas.
     * Incluye una leyenda personalizada con los colores correctos.
     */
    private void cargarGraficaMargenGananciaReal() {
        try {
            var productos = productoServicio.listarProductos();
            chartMargenGanancia.getData().clear();
            
            chartMargenGanancia.setLegendVisible(false);
            
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
                String nombre = truncarNombre(p.getNombre(), 12);
                
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
            
            aplicarColorASerieBarras(serieCostos, "#5960E3");
            aplicarColorASerieBarras(serieGanancias, "#70E359");
            
            crearLeyendaPersonalizada(chartMargenGanancia);
            
        } catch (Exception e) {
            System.err.println("Error en margen de ganancia: " + e.getMessage());
        }
    }
    
    /**
     * Trunca un nombre largo agregando puntos suspensivos.
     * 
     * @param nombre el nombre original a truncar
     * @param maxCaracteres el número máximo de caracteres permitidos
     * @return el nombre truncado con "..." si excede el límite, o el nombre original si no
     */
    private String truncarNombre(String nombre, int maxCaracteres) {
        if (nombre == null) return "";
        if (nombre.length() <= maxCaracteres) return nombre;
        return nombre.substring(0, maxCaracteres) + "...";
    }

    /**
     * Crea una leyenda personalizada para la gráfica de margen de ganancia.
     * La leyenda muestra cuadros de colores con las etiquetas "Costos" y "Ganancias".
     * 
     * @param chart la gráfica a la que se añadirá la leyenda
     */
    private void crearLeyendaPersonalizada(StackedBarChart<String, Number> chart) {
        Platform.runLater(() -> {
            try {
                chart.setLegendVisible(false);

                HBox leyendaPersonalizada = new HBox(20);
                leyendaPersonalizada.setAlignment(Pos.CENTER);
                leyendaPersonalizada.setPadding(new Insets(10));

                HBox itemCostos = crearItemLeyenda("Costos", "#5960E3");
                HBox itemGanancias = crearItemLeyenda("Ganancias", "#70E359");

                leyendaPersonalizada.getChildren().addAll(itemCostos, itemGanancias);

                if (chart.getParent() instanceof VBox) {
                    VBox parent = (VBox) chart.getParent();
                    parent.getChildren().add(leyendaPersonalizada);
                }

            } catch (Exception e) {
                System.err.println("Error creando leyenda: " + e.getMessage());
            }
        });
    }

    /**
     * Crea un item individual para la leyenda personalizada.
     * 
     * @param texto el texto del item de leyenda
     * @param color el color hexadecimal del símbolo
     * @return un HBox conteniendo el símbolo de color y la etiqueta
     */
    private HBox crearItemLeyenda(String texto, String color) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);

        javafx.scene.shape.Rectangle simbolo = new javafx.scene.shape.Rectangle(15, 15);
        simbolo.setFill(javafx.scene.paint.Color.web(color));

        Label label = new Label(texto);
        label.setStyle("-fx-font-size: 12px;");

        item.getChildren().addAll(simbolo, label);

        return item;
    }
    
    /**
     * Aplica un color específico a una serie de línea en una gráfica.
     * El color se aplica tanto a la línea como a los símbolos de datos.
     * 
     * @param serie la serie de datos a colorear
     * @param color el color hexadecimal a aplicar
     */
    private void aplicarColorASerie(XYChart.Series<String, Number> serie, String color) {
        Platform.runLater(() -> {
            javafx.scene.Node nodoSerie = serie.getNode();
            if (nodoSerie != null) {
                nodoSerie.setStyle("-fx-stroke: " + color + "; -fx-stroke-width: 3px;");
            }
            
            for (XYChart.Data<String, Number> data : serie.getData()) {
                javafx.scene.Node nodo = data.getNode();
                if (nodo != null) {
                    nodo.setStyle("-fx-background-color: " + color + ", white; " +
                                 "-fx-background-insets: 0, 2; " +
                                 "-fx-background-radius: 5px; " +
                                 "-fx-padding: 5px;");
                }
            }
        });
    }
    
    /**
     * Aplica colores individuales a cada barra basándose en los puntos de datos.
     * 
     * @param serie la serie de barras a colorear
     * @param puntos la lista de puntos que contienen información de color
     */
    private void aplicarColoresABarras(XYChart.Series<String, Number> serie, List<PuntoGrafica> puntos) {
        Platform.runLater(() -> {
            for (int i = 0; i < serie.getData().size() && i < puntos.size(); i++) {
                XYChart.Data<String, Number> data = serie.getData().get(i);
                String color = puntos.get(i).getColor();
                
                if (color != null && data.getNode() != null) {
                    javafx.scene.Node nodo = data.getNode();
                    nodo.setStyle("-fx-bar-fill: " + color + "; -fx-background-color: " + color + ";");
                }
            }
        });
    }
    
    /**
     * Aplica colores directos a las barras de una serie desde una lista de colores.
     * 
     * @param serie la serie de barras a colorear
     * @param colores lista de colores hexadecimales a aplicar
     */
    private void aplicarColoresABarrasDirecto(XYChart.Series<String, Number> serie, List<String> colores) {
        Platform.runLater(() -> {
            for (int i = 0; i < serie.getData().size() && i < colores.size(); i++) {
                XYChart.Data<String, Number> data = serie.getData().get(i);
                String color = colores.get(i);
                
                if (data.getNode() != null) {
                    javafx.scene.Node nodo = data.getNode();
                    nodo.setStyle("-fx-bar-fill: " + color + "; -fx-background-color: " + color + ";");
                }
            }
        });
    }
    
    /**
     * Aplica un color uniforme a todas las barras de una serie.
     * 
     * @param serie la serie de barras a colorear
     * @param color el color hexadecimal a aplicar
     */
    private void aplicarColorASerieBarras(XYChart.Series<String, Number> serie, String color) {
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : serie.getData()) {
                if (data.getNode() != null) {
                    javafx.scene.Node nodo = data.getNode();
                    nodo.setStyle("-fx-bar-fill: " + color + "; -fx-background-color: " + color + ";");
                }
            }
        });
    }
    
    /**
     * Reaplica los colores a todas las gráficas del dashboard.
     * Útil cuando se cambia de pestaña y los colores se pierden por el renderizado.
     */
    private void recolorearTodasLasGraficas() {
        try {
            Platform.runLater(() -> {
                try {
                    Thread.sleep(100);
                    
                    if (chartTendenciaVentas1 != null && !chartTendenciaVentas1.getData().isEmpty()) {
                        for (XYChart.Series<String, Number> serie : chartTendenciaVentas1.getData()) {
                            aplicarColorASerie(serie, "#3B82F6");
                        }
                    }
                    
                    if (chartTop5Productos1 != null && !chartTop5Productos1.getData().isEmpty()) {
                        cargarGraficaTopProductos();
                    }
                    
                    if (chartTop5Productos2 != null && !chartTop5Productos2.getData().isEmpty()) {
                        cargarGraficaInventarioCritico();
                    }
                    
                    if (chartMargenGanancia != null && !chartMargenGanancia.getData().isEmpty()) {
                        cargarGraficaMargenGananciaReal();
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error recoloreando: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error en recoloreo: " + e.getMessage());
        }
    }
    
    /**
     * Restaura el último análisis guardado en el repositorio.
     * Si existe un análisis previo, lo carga y muestra en la interfaz.
     */
    private void restaurarAnalisisGuardado() {
        if (analisisRepositorio.tieneAnalisisActual()) {
            ResultadoAnalisisIA analisisGuardado = analisisRepositorio.obtenerAnalisisActual();
            
            if (analisisGuardado != null) {
                ultimoAnalisis = convertirAAnalisisEstadistico(analisisGuardado);
                
                Platform.runLater(() -> {
                    if (txtAnalisisIA != null) {
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
     * Convierte un ResultadoAnalisisIA a AnalisisEstadistico.
     * 
     * @param resultado el resultado de análisis IA a convertir
     * @return un objeto AnalisisEstadistico equivalente
     */
    private AnalisisEstadistico convertirAAnalisisEstadistico(ResultadoAnalisisIA resultado) {
        AnalisisEstadistico analisis = new AnalisisEstadistico(resultado.getTipoAnalisis());
        analisis.setResumenIA(resultado.getAnalisisTexto());
        analisis.setMetricas(resultado.getMetricas());
        return analisis;
    }
    
    /**
     * Convierte un AnalisisEstadistico a ResultadoAnalisisIA.
     * 
     * @param analisis el análisis estadístico a convertir
     * @return un objeto ResultadoAnalisisIA equivalente
     */
    private ResultadoAnalisisIA convertirAResultadoAnalisisIA(AnalisisEstadistico analisis) {
        ResultadoAnalisisIA resultado = new ResultadoAnalisisIA();
        resultado.setTipoAnalisis(analisis.getTipoAnalisis());
        resultado.setAnalisisTexto(analisis.getResumenIA());
        resultado.setMetricas(analisis.getMetricas());
        resultado.setFechaGeneracion(LocalDateTime.now());
        return resultado;
    }
    
    /**
     * Maneja la acción de actualizar solo las gráficas sin ejecutar análisis IA.
     * Cambia a la pestaña de dashboard y recarga todos los datos visuales.
     */
    @FXML
    private void handleSoloGraficas() {
        if (tabPane != null) {
            tabPane.getSelectionModel().select(0);
        }
        cargarDatosGraficasIniciales();
        mostrarInfo("Gráficas Actualizadas", "Datos recargados exitosamente.");
    }
    
    /**
     * Maneja la generación de un análisis completo del negocio.
     * Ejecuta un análisis integral considerando ventas, inventario y clientes.
     */
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
    
    /**
     * Maneja la generación de un análisis específico de ventas.
     * Analiza tendencias, productos más vendidos y métricas de ventas.
     */
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
    
    /**
     * Maneja la generación de un análisis específico de inventario.
     * Evalúa niveles de stock, productos críticos y rotación de inventario.
     */
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
    
    /**
     * Maneja la generación automática de recomendaciones de negocio.
     * Crea sugerencias basadas en el contexto actual del negocio.
     */
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
    
    /**
     * Maneja la visualización de detalles de una recomendación seleccionada.
     * Muestra un diálogo modal con información completa de la recomendación.
     */
    @FXML
    private void handleVerDetallesRecomendacion() {
        Recomendacion seleccionada = tableRecomendaciones.getSelectionModel().getSelectedItem();
        
        if (seleccionada == null) {
            mostrarAdvertencia("Sin Selección", "Selecciona una recomendación");
            return;
        }
        
        mostrarDetallesRecomendacion(seleccionada);
    }
    
    /**
     * Maneja el marcado de una recomendación como aplicada.
     * Solicita confirmación antes de cambiar el estado.
     */
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
    
    /**
     * Maneja la eliminación de todas las recomendaciones marcadas como aplicadas.
     * Solicita confirmación antes de eliminar.
     */
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
    
    /**
     * Maneja la limpieza y recarga de la vista.
     * Restaura el último análisis guardado y recarga las gráficas.
     */
    @FXML
    private void handleLimpiar() {
        if (analisisRepositorio.tieneAnalisisActual()) {
            restaurarAnalisisGuardado();
        }
        
        cargarDatosGraficasIniciales();
        mostrarInfo("Vista Actualizada", "Gráficas recargadas");
    }
    
    /**
     * Maneja la copia de métricas al portapapeles del sistema.
     */
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
    
    /**
     * Maneja el guardado de métricas en un archivo de texto.
     * Abre un diálogo de selección de archivo.
     */
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
    
    /**
     * Maneja la copia del análisis IA al portapapeles del sistema.
     */
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
    
    /**
     * Maneja la exportación directa del análisis a formato PDF.
     * Abre un diálogo de selección de archivo.
     */
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

    /**
     * Maneja la exportación del análisis a archivo de texto o PDF.
     * Permite elegir entre ambos formatos mediante el diálogo de guardado.
     */
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
                    handleExportarPDF(ruta);
                } else {
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
     * Crea una nueva instancia local de GeneradorPDF.
     * Este método garantiza que cada PDF tenga su propia instancia del generador.
     * 
     * @return una nueva instancia de GeneradorPDF lista para usar
     */
    private com.mycompany.stockflow.utils.GeneradorPDF crearGeneradorPDFLocal() {
        try {
            return new com.mycompany.stockflow.utils.GeneradorPDF();
        } catch (Exception e) {
            System.err.println("Error creando instancia local de GeneradorPDF: " + e.getMessage());
            return new com.mycompany.stockflow.utils.GeneradorPDF();
        }
    }

        /**
     * Exporta el análisis completo a formato PDF de forma asíncrona.
     * Captura snapshots de las gráficas en el hilo de JavaFX antes de generar el PDF.
     * Crea una nueva instancia de GeneradorPDF para cada PDF generado.
     * 
     * @param rutaArchivo la ruta donde se guardará el archivo PDF
     */
    private void handleExportarPDF(String rutaArchivo) {
        mostrarOverlayProgreso(true);

        try {
            final String tipoAnalisis = ultimoAnalisis != null ? 
                ultimoAnalisis.getTipoAnalisis() : "Análisis General";

            final String analisisTexto = txtAnalisisIA != null ? 
                txtAnalisisIA.getText() : "";

            final String metricas = txtMetricas != null ? 
                txtMetricas.getText() : "";

            final List<byte[]> graficasBytes = new ArrayList<>();
            final List<String> nombresGraficas = new ArrayList<>();

            // Capturar gráfica 1: Tendencia de Ventas
            if (chartTendenciaVentas1 != null && chartTendenciaVentas1.isVisible()) {
                try {
                    graficasBytes.add(convertirChartABytes(chartTendenciaVentas1));
                    nombresGraficas.add(chartTendenciaVentas1.getTitle() != null ? 
                        chartTendenciaVentas1.getTitle() : "Tendencia de Ventas");
                } catch (Exception e) {
                    System.err.println("Error capturando gráfica tendencia de ventas: " + e.getMessage());
                }
            }

            // Capturar gráfica 2: Top 5 Productos
            if (chartTop5Productos1 != null && chartTop5Productos1.isVisible()) {
                try {
                    graficasBytes.add(convertirChartABytes(chartTop5Productos1));
                    nombresGraficas.add(chartTop5Productos1.getTitle() != null ? 
                        chartTop5Productos1.getTitle() : "Productos Más Vendidos");
                } catch (Exception e) {
                    System.err.println("Error capturando gráfica top productos: " + e.getMessage());
                }
            }

            // Capturar gráfica 3: Margen de Ganancia
            if (chartMargenGanancia != null && chartMargenGanancia.isVisible()) {
                try {
                    graficasBytes.add(convertirChartABytes(chartMargenGanancia));
                    nombresGraficas.add(chartMargenGanancia.getTitle() != null ? 
                        chartMargenGanancia.getTitle() : "Margen de Ganancia");
                } catch (Exception e) {
                    System.err.println("Error capturando gráfica margen de ganancia: " + e.getMessage());
                }
            }

            // Capturar gráfica 4: Inventario Crítico
            if (chartTop5Productos2 != null && chartTop5Productos2.isVisible()) {
                try {
                    graficasBytes.add(convertirChartABytes(chartTop5Productos2));
                    nombresGraficas.add(chartTop5Productos2.getTitle() != null ? 
                        chartTop5Productos2.getTitle() : "Inventario Crítico");
                } catch (Exception e) {
                    System.err.println("Error capturando gráfica inventario crítico: " + e.getMessage());
                }
            }

            // Generar PDF en un hilo separado
            new Thread(() -> {
                try {
                    // CLAVE: Crear nueva instancia aquí, dentro del hilo
                    com.mycompany.stockflow.utils.GeneradorPDF generadorLocal = 
                        crearGeneradorPDFLocal();

                    generadorLocal.generarReporteCompletoConBytes(
                        rutaArchivo,
                        tipoAnalisis,
                        analisisTexto,
                        metricas,
                        graficasBytes,
                        nombresGraficas
                    );

                    // Limpiar recursos después de generar
                    if (generadorLocal != null) {
                        generadorLocal.limpiarRecursos();
                    }
                    abrirPDF(rutaArchivo);
                    Platform.runLater(() -> {
                        mostrarOverlayProgreso(false);
                        mostrarInfo("PDF Generado", 
                            "Reporte exportado exitosamente:\n" + rutaArchivo);
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        mostrarOverlayProgreso(false);
                        mostrarError("Error al generar PDF", e.getMessage());
                        e.printStackTrace();
                    });
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            mostrarOverlayProgreso(false);
            mostrarError("Error", "Error al preparar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
        /**
     * Abre el PDF generado en el visor PDF del sistema (navegador o lector PDF).
     * Este es el método más simple y confiable.
     * 
     * @param rutaArchivo la ruta completa del archivo PDF a abrir
     */
    private void abrirPDF(String rutaArchivo) {
        try {
            java.io.File archivo = new java.io.File(rutaArchivo);

            if (!archivo.exists()) {
                mostrarError("Archivo no encontrado", "El archivo PDF no existe en: " + rutaArchivo);
                return;
            }

            // Usar Desktop para abrir el archivo con la aplicación predeterminada
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

                if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                    desktop.open(archivo);
                    System.out.println("PDF abierto: " + rutaArchivo);
                } else {
                    mostrarAdvertencia("No disponible", 
                        "Tu sistema no puede abrir archivos PDF directamente.");
                }
            } else {
                mostrarError("Error", 
                    "Desktop no está soportado en tu sistema.");
            }

        } catch (java.io.IOException e) {
            mostrarError("Error al abrir PDF", 
                "No se pudo abrir el archivo: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Convierte una gráfica de JavaFX a bytes de imagen PNG.
     * Este método debe ejecutarse en el hilo de JavaFX.
     * 
     * @param chart la gráfica a convertir
     * @return array de bytes representando la imagen PNG
     * @throws Exception si ocurre un error durante la conversión
     */
    private byte[] convertirChartABytes(javafx.scene.chart.Chart chart) throws Exception {
        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        javafx.scene.image.WritableImage snapshot = chart.snapshot(params, null);
        java.awt.image.BufferedImage bufferedImage = 
            javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, null);

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }
    
    /**
     * Actualiza todas las gráficas del dashboard con los datos más recientes.
     * Este método es público para que otros controladores puedan invocarlo.
     */
    public void actualizarTodasLasGraficas() {
        Platform.runLater(() -> {
            try {
                cargarDatosGraficasIniciales();
                System.out.println("Gráficas actualizadas correctamente");
            } catch (Exception e) {
                System.err.println("Error actualizando gráficas: " + e.getMessage());
            }
        });
    }
    
    /**
     * Ejecuta una tarea de análisis de forma asíncrona mostrando el overlay de progreso.
     * 
     * @param task la tarea de análisis a ejecutar
     * @param tipoAnalisis el tipo de análisis que se está ejecutando
     */
    private void ejecutarAnalisisConOverlay(AnalisisTask task, String tipoAnalisis) {
        mostrarOverlayProgreso(true);
        
        if (txtAnalisisIA != null) {
            txtAnalisisIA.setText("Analizando con IA...\nEsto puede tardar 5-15 segundos.");
        }
        
        new Thread(() -> {
            try {
                AnalisisEstadistico analisis = task.ejecutar();
                
                ResultadoAnalisisIA resultadoParaGuardar = convertirAResultadoAnalisisIA(analisis);
                analisisRepositorio.guardar(resultadoParaGuardar);
                
                Platform.runLater(() -> {
                    ultimoAnalisis = analisis;
                    
                    if (txtAnalisisIA != null) {
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
     * Crea un contexto de negocio con todos los datos necesarios para el análisis.
     * 
     * @return objeto ContextoNegocio con productos, ventas, clientes y periodo
     */
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
    
    /**
     * Muestra un diálogo modal con los detalles completos de una recomendación.
     * 
     * @param rec la recomendación cuyos detalles se mostrarán
     */
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
    
    /**
     * Formatea el texto de análisis IA para visualización en la interfaz.
     * Limpia el formato Markdown y aplica estilos de texto plano estructurados.
     * 
     * @param analisisTexto el texto del análisis en formato Markdown
     * @return el texto formateado para visualización
     */
    private String formatearAnalisisIA(String analisisTexto) {
        if (analisisTexto == null || analisisTexto.isEmpty()) {
            return "No se pudo generar el análisis.";
        }
        
        StringBuilder formateado = new StringBuilder();
        formateado.append("ANÁLISIS DE INTELIGENCIA ARTIFICIAL\n");
        formateado.append("=".repeat(60)).append("\n\n");
        
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
     * Limpia el formato Markdown de un texto convirtiéndolo a texto plano estructurado.
     * Remueve negritas, cursivas, encabezados y símbolos especiales.
     * 
     * @param texto el texto con formato Markdown
     * @return el texto limpio sin formato Markdown
     */
    private String limpiarMarkdown(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        
        StringBuilder resultado = new StringBuilder();
        String[] lineas = texto.split("\n");
        
        for (String linea : lineas) {
            String lineaLimpia = linea;
            
            lineaLimpia = lineaLimpia.replaceAll("\\*\\*\\*(.+?)\\*\\*\\*", "$1");
            lineaLimpia = lineaLimpia.replaceAll("\\*\\*(.+?)\\*\\*", "$1");
            lineaLimpia = lineaLimpia.replaceAll("\\*(.+?)\\*", "$1");
            
            if (lineaLimpia.startsWith("####")) {
                lineaLimpia = "    " + lineaLimpia.replaceFirst("####\\s*", "").toUpperCase();
            } else if (lineaLimpia.startsWith("###")) {
                lineaLimpia = "  • " + lineaLimpia.replaceFirst("###\\s*", "").toUpperCase();
            } else if (lineaLimpia.startsWith("##")) {
                lineaLimpia = "\n" + lineaLimpia.replaceFirst("##\\s*", "").toUpperCase() + "\n" + "-".repeat(50);
            } else if (lineaLimpia.startsWith("#")) {
                lineaLimpia = "\n" + lineaLimpia.replaceFirst("#\\s*", "").toUpperCase() + "\n" + "=".repeat(50);
            }
            
            if (lineaLimpia.trim().startsWith("-")) {
                lineaLimpia = lineaLimpia.replaceFirst("-\\s*", "  • ");
            }
            
            lineaLimpia = lineaLimpia.replaceAll("[🔴⚠️✅📊💡🎯]", "");
            
            resultado.append(lineaLimpia).append("\n");
        }
        
        return resultado.toString().trim();
    }
    
    /**
     * Actualiza el área de texto de métricas con los datos del análisis.
     * 
     * @param analisis el análisis estadístico con las métricas a mostrar
     */
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
    
    /**
     * Formatea un valor numérico para visualización.
     * Los valores Double se formatean como moneda.
     * 
     * @param valor el valor a formatear
     * @return la representación en String del valor formateado
     */
    private String formatearValor(Object valor) {
        if (valor instanceof Double) {
            return String.format("$%.2f", (Double) valor);
        }
        return String.valueOf(valor);
    }
    
    /**
     * Actualiza la etiqueta de fecha del último análisis con la fecha y hora actuales.
     */
    private void actualizarFechaAnalisis() {
        if (lblFechaAnalisis != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            lblFechaAnalisis.setText("Última actualización: " + LocalDateTime.now().format(formatter));
        }
    }
    
    /**
     * Genera un reporte de texto completo para exportación.
     * Incluye análisis IA y recomendaciones si están disponibles.
     * 
     * @return el contenido del reporte en formato texto
     */
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
    
    /**
     * Muestra u oculta el indicador de progreso simple.
     * 
     * @param visible true para mostrar, false para ocultar
     */
    private void mostrarProgreso(boolean visible) {
        if (progressIndicator != null) {
            progressIndicator.setVisible(visible);
            progressIndicator.setManaged(visible);
        }
    }
    
    /**
     * Muestra un diálogo de alerta genérico.
     * 
     * @param titulo el título del diálogo
     * @param mensaje el mensaje a mostrar
     * @param tipo el tipo de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de error.
     * 
     * @param titulo el título del diálogo
     * @param mensaje el mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, Alert.AlertType.ERROR);
    }
    
    /**
     * Muestra un diálogo informativo.
     * 
     * @param titulo el título del diálogo
     * @param mensaje el mensaje informativo
     */
    private void mostrarInfo(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, Alert.AlertType.INFORMATION);
    }
    
    /**
     * Muestra un diálogo de advertencia.
     * 
     * @param titulo el título del diálogo
     * @param mensaje el mensaje de advertencia
     */
    private void mostrarAdvertencia(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, Alert.AlertType.WARNING);
    }
    
    /**
     * Interface funcional para tareas de análisis asíncronas.
     * Permite ejecutar diferentes tipos de análisis con la misma estructura.
     */
    @FunctionalInterface
    private interface AnalisisTask {
        /**
         * Ejecuta la tarea de análisis.
         * 
         * @return el resultado del análisis estadístico
         * @throws Exception si ocurre un error durante el análisis
         */
        AnalisisEstadistico ejecutar() throws Exception;
    }
}