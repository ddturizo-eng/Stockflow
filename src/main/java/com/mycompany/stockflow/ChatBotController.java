/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.stockflow;

import com.mycompany.stockflow.Logica.*;
import com.mycompany.stockflow.Modelo.*;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ChatBotController implements Initializable {
    
    @FXML private Label lblInventarioTotal;
    @FXML private Label lblProductosRiesgo;
    @FXML private Label lblCambioRiesgo;
    @FXML private Label lblPrediccionesActivas;
    @FXML private Label lblConfianzaPromedio;
    @FXML private Label lblMargenPromedio;
    @FXML private Label lblTendenciaMargen;
    @FXML private Label lblTotalInsights;
    @FXML private Label lblInsightsCriticos;
    @FXML private Label lblTituloGrafica;
    @FXML private Label lblSubtituloGrafica;
    @FXML private Label lblEstadoIA;
    @FXML private Label lblEstadoIAChat;
    @FXML private Label lblUltimaActualizacion;
    @FXML private Label lblContadorNotificaciones;
    
    @FXML private LineChart<String, Number> chartPrincipal;
    @FXML private BarChart<String, Number> chartTopProductos;
    
    @FXML private VBox sidebarContainer;
    @FXML private VBox notificacionesContainer;
    @FXML private VBox todasNotificacionesContainer;
    @FXML private VBox prediccionesContainer;
    @FXML private VBox chatContainer;
    @FXML private VBox panelNotificaciones;
    
    @FXML private StackPane chatModalContainer;
    
    @FXML private ScrollPane scrollPaneChat;
    
    @FXML private TextField txtInputUsuario;
    
    @FXML private ComboBox<String> cbProductos;
    @FXML private ComboBox<String> cbPeriodo;
    @FXML private ComboBox<String> cbLimiteTop;
    
    @FXML private Text txtAlertCard;
    @FXML private Text txtOpportunityCard;
    @FXML private Text txtInsightCard;
    @FXML private Text txtExplicacionIA;
    
    @FXML private Label lblToggleAlerta;
    @FXML private Label lblToggleOportunidad;
    @FXML private Label lblToggleAnalisis;
    
    @FXML private Button btnToggleSidebar;
    @FXML private Button btnNotificaciones;
    @FXML private Button btnCerrarNotif;
    @FXML private Button btnUsuario;
    @FXML private Button btnDashboard;
    @FXML private Button btnStockCritico;
    @FXML private Button btnPromociones;
    @FXML private Button btnProveedores;
    @FXML private Button btnAnalisisIA;
    @FXML private Button btnPredicciones;
    @FXML private Button btnAplicarFiltros;
    @FXML private Button btnRefrescar;
    @FXML private Button btnExportar;
    @FXML private Button btnCambiarVista;
    @FXML private Button btnVerTodasNotif;
    @FXML private Button btnChatFlotante;
    @FXML private Button btnCerrarChat;
    @FXML private Button btnEnviarChat;
    @FXML private Button btnCmdVentasSemanal;
    @FXML private Button btnCmdInventario;
    @FXML private Button btnCmdSugerencia;
    
    private ProductoServicio productoServicio;
    private VentaServicio ventaServicio;
    private PrediccionServicio prediccionServicio;
    private VisualizacionServicio visualizacionServicio;
    private NotificacionIAServicio notificacionServicio;
    private ChatBotServicio chatBotServicio;
    private AnaliticaAvanzadaServicio analiticaServicio;
    
    private Producto productoActual;
    private List<InsightIA> notificacionesActuales;
    private List<PrediccionDemanda> prediccionesActivas;
    private Map<String, Producto> mapaProductos;
    
    private boolean sidebarVisible = true;
    private boolean chatVisible = false;
    private boolean notificacionesVisible = false;
    private boolean esperandoRespuestaIA = false;
    
    private int diasProyeccion = 30;
    private int limiteTopProductos = 5;
    
    private final DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final NumberFormat formatoCOP = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    
    @Override
public void initialize(URL url, ResourceBundle rb) {
    System.out.println("=== INICIALIZANDO CONTROLADOR ===");
    System.out.println("cbProductos: " + (cbProductos != null ? "OK" : "NULL"));
    System.out.println("cbPeriodo: " + (cbPeriodo != null ? "OK" : "NULL"));
    System.out.println("btnAplicarFiltros: " + (btnAplicarFiltros != null ? "OK" : "NULL"));
    
    try {
        inicializarServicios();
        System.out.println("Servicios inicializados");
        
        inicializarComboBoxes();
        System.out.println("ComboBoxes inicializados");
        
        configurarEventos();
        System.out.println("Eventos configurados");
        
        cargarDatosIniciales();
        System.out.println("Datos iniciales cargados");
        
        iniciarActualizacionAutomatica();
        System.out.println("Actualizacion automatica iniciada");
    } catch (Exception e) {
        System.err.println("ERROR EN INITIALIZE: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    private void inicializarServicios() {
        this.productoServicio = new ProductoServicio();
        this.ventaServicio = new VentaServicio();
        this.prediccionServicio = new PrediccionServicio();
        this.visualizacionServicio = new VisualizacionServicio();
        this.notificacionServicio = new NotificacionIAServicio();
        this.chatBotServicio = new ChatBotServicio();
        this.analiticaServicio = new AnaliticaAvanzadaServicio();
        
        this.notificacionesActuales = new ArrayList<>();
        this.prediccionesActivas = new ArrayList<>();
        this.mapaProductos = new HashMap<>();
    }
    
    private void inicializarComboBoxes() {
        cbPeriodo.getItems().addAll("7 dias", "15 dias", "30 dias", "60 dias", "90 dias");
        cbPeriodo.setValue("30 dias");
        
        cbLimiteTop.getItems().addAll("Top 3", "Top 5", "Top 10", "Top 15");
        cbLimiteTop.setValue("Top 5");
    }
    
    private void configurarEventos() {
        btnToggleSidebar.setOnAction(e -> toggleSidebar());
        btnNotificaciones.setOnAction(e -> toggleNotificaciones());
        btnCerrarNotif.setOnAction(e -> toggleNotificaciones());
        btnUsuario.setOnAction(e -> mostrarPerfilUsuario());
        
        btnDashboard.setOnAction(e -> cargarDashboard());
        btnStockCritico.setOnAction(e -> mostrarStockCritico());
        btnPromociones.setOnAction(e -> mostrarPromociones());
        btnProveedores.setOnAction(e -> mostrarProveedores());
        btnAnalisisIA.setOnAction(e -> mostrarAnalisisIA());
        btnPredicciones.setOnAction(e -> mostrarPredicciones());
        
        btnAplicarFiltros.setOnAction(e -> aplicarFiltros());
        btnRefrescar.setOnAction(e -> refrescarDatos());
        btnExportar.setOnAction(e -> exportarDatos());
        btnCambiarVista.setOnAction(e -> cambiarVistaGrafica());
        btnVerTodasNotif.setOnAction(e -> toggleNotificaciones());
        
        btnChatFlotante.setOnAction(e -> toggleChat());
        btnCerrarChat.setOnAction(e -> toggleChat());
        btnEnviarChat.setOnAction(e -> procesarMensajeChat());
        txtInputUsuario.setOnAction(e -> procesarMensajeChat());
        
        btnCmdVentasSemanal.setOnAction(e -> ejecutarComandoRapido("ventas"));
        btnCmdInventario.setOnAction(e -> ejecutarComandoRapido("inventario"));
        btnCmdSugerencia.setOnAction(e -> ejecutarComandoRapido("sugerencia"));
        
        cbProductos.setOnAction(e -> cambiarProducto());
        cbLimiteTop.setOnAction(e -> actualizarTopProductos());
        
        chatModalContainer.setOnMouseClicked(e -> {
            if (e.getTarget() == chatModalContainer) toggleChat();
        });
        
        lblToggleAlerta.setOnMouseClicked(e -> toggleCard("alerta"));
        lblToggleOportunidad.setOnMouseClicked(e -> toggleCard("oportunidad"));
        lblToggleAnalisis.setOnMouseClicked(e -> toggleCard("analisis"));
    }
    
    private void cargarDatosIniciales() {
        CompletableFuture.runAsync(() -> {
            try {
                cargarProductos();
                cargarProductoInicial();
                actualizarKPIsGlobales();
                actualizarGraficaPrincipal();
                actualizarTopProductos();
                cargarNotificaciones();
                cargarPrediccionesMultiples();
                mostrarMensajeBienvenidaChat();
                
                Platform.runLater(() -> {
                    actualizarEstadoIA("lista");
                    actualizarHoraActualizacion();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarError("Error cargando datos: " + e.getMessage());
                    actualizarEstadoIA("error");
                });
            }
        });
    }
    
    private void cargarProductos() {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            
            Platform.runLater(() -> {
                cbProductos.getItems().clear();
                for (Producto p : productos) {
                    cbProductos.getItems().add(p.getNombre());
                    mapaProductos.put(p.getNombre(), p);
                }
                if (!productos.isEmpty()) {
                    cbProductos.setValue(productos.get(0).getNombre());
                }
            });
        } catch (Exception e) {
            System.err.println("Error cargando productos: " + e.getMessage());
        }
    }
    
    private void cargarProductoInicial() {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            if (!productos.isEmpty()) {
                this.productoActual = productos.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error cargando producto inicial: " + e.getMessage());
        }
    }
    
    private void actualizarKPIsGlobales() {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            List<Venta> ventas = ventaServicio.listarVentas();
            
            double valorInventario = productos.stream()
                .mapToDouble(p -> p.getPrecioVenta() * p.getStock())
                .sum();
            
            long productosRiesgo = productos.stream()
                .filter(Producto::tieneStockBajo)
                .count();
            
            List<Producto> enRiesgo = prediccionServicio.identificarProductosEnRiesgo(diasProyeccion);
            int prediccionesActivas = enRiesgo.size();
            
            double confianzaPromedio = prediccionesActivas > 0 ? 
                enRiesgo.stream()
                    .mapToDouble(p -> {
                        try {
                            PrediccionDemanda pred = prediccionServicio.predecirDemandaMejorada(p, diasProyeccion);
                            return pred.getNivelConfianza();
                        } catch (Exception e) {
                            return 0.0;
                        }
                    })
                    .average()
                    .orElse(0.0) : 0.0;
            
            double margenPromedio = calcularMargenPromedio(ventas);
            String tendenciaMargen = margenPromedio > 30 ? "Excelente" : 
                                    margenPromedio > 20 ? "Bueno" : "Mejorable";
            
            notificacionesActuales = notificacionServicio.generarNotificacionesActuales();
            int totalInsights = notificacionesActuales.size();
            long insightsCriticos = notificacionesActuales.stream()
                .filter(n -> n.getPrioridad() == InsightIA.NivelPrioridad.CRITICAL || 
                            n.getPrioridad() == InsightIA.NivelPrioridad.HIGH)
                .count();
            
            Platform.runLater(() -> {
                lblInventarioTotal.setText(formatearMonedaCOP(valorInventario));
                lblProductosRiesgo.setText(String.valueOf(productosRiesgo));
                lblCambioRiesgo.setText(productosRiesgo == 0 ? "Sin alertas" : 
                    productosRiesgo > 5 ? "+Critico" : "Vigilar");
                lblPrediccionesActivas.setText(String.valueOf(prediccionesActivas));
                lblConfianzaPromedio.setText(String.format("Confianza: %.0f%%", confianzaPromedio * 100));
                lblMargenPromedio.setText(String.format("%.0f%%", margenPromedio));
                lblTendenciaMargen.setText(tendenciaMargen);
                lblTotalInsights.setText(String.valueOf(totalInsights));
                lblInsightsCriticos.setText(insightsCriticos + " criticos");
                
                lblContadorNotificaciones.setText(String.valueOf(insightsCriticos));
                lblContadorNotificaciones.setVisible(insightsCriticos > 0);
            });
        } catch (Exception e) {
            System.err.println("Error actualizando KPIs: " + e.getMessage());
        }
    }
    
    private double calcularMargenPromedio(List<Venta> ventas) {
        if (ventas.isEmpty()) return 0.0;
        
        double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
        double totalCostos = ventas.stream().mapToDouble(Venta::getCostoTotal).sum();
        
        return totalCostos > 0 ? ((totalVentas - totalCostos) / totalCostos) * 100 : 0.0;
    }
    
    private String formatearMonedaCOP(double valor) {
        if (valor >= 1_000_000) {
            return String.format("$%.1fM COP", valor / 1_000_000);
        } else if (valor >= 1_000) {
            return String.format("$%.1fK COP", valor / 1_000);
        }
        return formatoCOP.format(valor);
    }
    
    private void actualizarGraficaPrincipal() {
        if (productoActual == null) return;
        
        try {
            Platform.runLater(() -> {
                lblTituloGrafica.setText("PRONOSTICO DE DEMANDA");
                lblSubtituloGrafica.setText(productoActual.getNombre().toUpperCase());
            });
            
            chartPrincipal.getData().clear();
            
            int intervaloMuestreo = diasProyeccion > 60 ? 5 : diasProyeccion > 30 ? 3 : 2;
            
            XYChart.Series<String, Number> serieHistorica = 
                visualizacionServicio.generarSerieVentasHistoricasMuestreada(
                    productoActual, diasProyeccion * 2, intervaloMuestreo);
            serieHistorica.setName("Ventas Historicas");
            
            XYChart.Series<String, Number> seriePrediccion = 
                visualizacionServicio.generarSeriePrediccionMuestreada(
                    productoActual, diasProyeccion, intervaloMuestreo);
            seriePrediccion.setName("Prediccion IA");
            
            XYChart.Series<String, Number> serieStock = 
                visualizacionServicio.generarSerieStockActualMuestreada(
                    productoActual, diasProyeccion, intervaloMuestreo);
            serieStock.setName("Stock Actual");
            
            Platform.runLater(() -> {
                chartPrincipal.getData().addAll(serieHistorica, seriePrediccion, serieStock);
                aplicarEstilosGrafica();
            });
            
            actualizarTarjetasResumen();
            
        } catch (Exception e) {
            System.err.println("Error actualizando grafica: " + e.getMessage());
        }
    }
    
    private void aplicarEstilosGrafica() {
        chartPrincipal.setStyle("-fx-background-color: transparent;");
        
        if (chartPrincipal.lookup(".chart-plot-background") != null) {
            chartPrincipal.lookup(".chart-plot-background")
                .setStyle("-fx-background-color: #ffffff;");
        }
        
        if (chartPrincipal.getData().size() >= 3) {
            chartPrincipal.getData().get(0).getNode()
                .setStyle("-fx-stroke: #51cf66; -fx-stroke-width: 3px;");
            chartPrincipal.getData().get(1).getNode()
                .setStyle("-fx-stroke: #0066cc; -fx-stroke-width: 3px; -fx-stroke-dash-array: 8 5;");
            chartPrincipal.getData().get(2).getNode()
                .setStyle("-fx-stroke: #ff9900; -fx-stroke-width: 2px;");
        }
    }
    
    private void actualizarTarjetasResumen() {
        if (productoActual == null) return;
        
        try {
            PrediccionDemanda prediccion = prediccionServicio.predecirDemandaMejorada(
                productoActual, diasProyeccion);
            
            Platform.runLater(() -> {
                txtAlertCard.setText(String.format(
                    "%s - Tendencia %s. Stock: %d unidades. Confianza: %d%%",
                    productoActual.getNombre(),
                    prediccion.getTendencia(),
                    productoActual.getStock(),
                    prediccion.getNivelConfianzaPorcentaje()
                ));
                
                if (prediccion.requiereReabastecimiento()) {
                    txtOpportunityCard.setText(String.format(
                        "IA sugiere reabastecer con %d unidades en los proximos %d dias. " +
                        "Demanda estimada: %d unidades.",
                        prediccion.getCantidadSugeridaReabastecimiento(),
                        diasProyeccion,
                        prediccion.getDemandaEstimada()
                    ));
                } else {
                    txtOpportunityCard.setText(
                        "Stock suficiente para el periodo. Considerar promocion " +
                        "para aumentar rotacion del inventario."
                    );
                }
                
                txtInsightCard.setText(String.format(
                    "Factor estacional: %.2f. Demanda estimada: %d unidades. " +
                    "Rango de confianza: %d-%d unidades.",
                    prediccion.getFactorEstacional(),
                    prediccion.getDemandaEstimada(),
                    prediccion.getLimiteInferior(),
                    prediccion.getLimiteSuperior()
                ));
            });
            
            generarExplicacionIA(prediccion);
            
        } catch (Exception e) {
            System.err.println("Error actualizando tarjetas: " + e.getMessage());
        }
    }
    
    private void generarExplicacionIA(PrediccionDemanda prediccion) {
        CompletableFuture.runAsync(() -> {
            try {
                StringBuilder contexto = new StringBuilder();
                contexto.append("Explica brevemente este analisis de prediccion:\n\n");
                contexto.append("Producto: ").append(productoActual.getNombre()).append("\n");
                contexto.append("Stock actual: ").append(productoActual.getStock()).append("\n");
                contexto.append("Demanda estimada (").append(diasProyeccion).append(" dias): ")
                    .append(prediccion.getDemandaEstimada()).append("\n");
                contexto.append("Tendencia: ").append(prediccion.getTendencia()).append("\n");
                contexto.append("Confianza: ").append(prediccion.getNivelConfianzaPorcentaje()).append("%\n");
                contexto.append("Requiere reabastecimiento: ")
                    .append(prediccion.requiereReabastecimiento() ? "Si" : "No").append("\n\n");
                contexto.append("Genera una explicacion clara en 2-3 oraciones.");
                
                String explicacion = chatBotServicio.procesarPregunta(contexto.toString())
                    .getContenido();
                
                Platform.runLater(() -> txtExplicacionIA.setText(explicacion));
                
            } catch (Exception e) {
                Platform.runLater(() -> txtExplicacionIA.setText(
                    "El analisis muestra un patron consistente basado en datos historicos. " +
                    "La prediccion considera tendencias y estacionalidad para estimar demanda futura."
                ));
            }
        });
    }
    
    private void actualizarTopProductos() {
        CompletableFuture.runAsync(() -> {
            try {
                String seleccion = cbLimiteTop.getValue();
                int limite = Integer.parseInt(seleccion.replaceAll("[^0-9]", ""));
                
                XYChart.Series<String, Number> serie = 
                    visualizacionServicio.generarSerieTopProductos(limite);
                
                Platform.runLater(() -> {
                    chartTopProductos.getData().clear();
                    chartTopProductos.getData().add(serie);
                    
                    if (chartTopProductos.lookup(".chart-plot-background") != null) {
                        chartTopProductos.lookup(".chart-plot-background")
                            .setStyle("-fx-background-color: #ffffff;");
                    }
                });
                
            } catch (Exception e) {
                System.err.println("Error actualizando top productos: " + e.getMessage());
            }
        });
    }
    
    private void cargarNotificaciones() {
        CompletableFuture.runAsync(() -> {
            try {
                List<InsightIA> notifPrioritarias = notificacionServicio.filtrarPorPrioridad(
                    notificacionesActuales,
                    InsightIA.NivelPrioridad.MEDIUM
                );
                
                Platform.runLater(() -> {
                    notificacionesContainer.getChildren().clear();
                    
                    int limite = Math.min(4, notifPrioritarias.size());
                    for (int i = 0; i < limite; i++) {
                        notificacionesContainer.getChildren().add(
                            crearTarjetaNotificacion(notifPrioritarias.get(i)));
                    }
                    
                    todasNotificacionesContainer.getChildren().clear();
                    for (InsightIA notif : notificacionesActuales) {
                        todasNotificacionesContainer.getChildren().add(
                            crearTarjetaNotificacion(notif));
                    }
                });
                
            } catch (Exception e) {
                System.err.println("Error cargando notificaciones: " + e.getMessage());
            }
        });
    }
    
    private VBox crearTarjetaNotificacion(InsightIA notif) {
        VBox card = new VBox(8);
        card.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 10; -fx-padding: 16; " +
            "-fx-border-color: %s; -fx-border-radius: 10; -fx-border-width: 1; -fx-cursor: hand;",
            obtenerColorFondo(notif.getPrioridad()),
            notif.getColorPrioridad()
        ));
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label etiqueta = new Label(notif.getTipoString());
        etiqueta.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 9px; " +
            "-fx-font-weight: 600; -fx-padding: 3 8; -fx-background-radius: 4;",
            notif.getColorPrioridad()
        ));
        
        Label prioridad = new Label(traducirPrioridad(notif.getPrioridadString()));
        prioridad.setStyle(String.format(
            "-fx-text-fill: %s; -fx-font-size: 10px; -fx-font-weight: 600;",
            notif.getColorPrioridad()
        ));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label tiempo = new Label("Ahora");
        tiempo.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 10px;");
        
        header.getChildren().addAll(etiqueta, prioridad, spacer, tiempo);
        
        Text titulo = new Text(notif.getTitulo());
        titulo.setStyle("-fx-fill: #212529; -fx-font-size: 13px; -fx-font-weight: 600;");
        titulo.setWrappingWidth(350);
        
        Text descripcion = new Text(notif.getDescripcion());
        descripcion.setStyle("-fx-fill: #495057; -fx-font-size: 12px;");
        descripcion.setWrappingWidth(350);
        
        card.getChildren().addAll(header, titulo, descripcion);
        
        if (notif.getAccionRecomendada() != null && !notif.getAccionRecomendada().isEmpty()) {
            HBox accionBox = new HBox(8);
            accionBox.setAlignment(Pos.CENTER_LEFT);
            accionBox.setStyle("-fx-background-color: rgba(0,0,0,0.03); " +
                "-fx-background-radius: 6; -fx-padding: 8;");
            
            Label accionLabel = new Label("Accion: " + notif.getAccionRecomendada());
            accionLabel.setStyle("-fx-text-fill: #495057; -fx-font-size: 11px;");
            accionLabel.setWrapText(true);
            accionLabel.setMaxWidth(330);
            
            accionBox.getChildren().add(accionLabel);
            card.getChildren().add(accionBox);
        }
        
        card.setOnMouseClicked(e -> verDetalleNotificacion(notif));
        
        return card;
    }
    
    private String obtenerColorFondo(InsightIA.NivelPrioridad prioridad) {
        switch (prioridad) {
            case CRITICAL: return "#fff5f5";
            case HIGH: return "#fff5f5";
            case MEDIUM: return "#fff9e6";
            case LOW: return "#f0fff4";
            default: return "#f8f9fa";
        }
    }
    
    private String traducirPrioridad(String prioridad) {
        switch (prioridad.toUpperCase()) {
            case "CRITICAL": return "CRITICA";
            case "HIGH": return "ALTA";
            case "MEDIUM": return "MEDIA";
            case "LOW": return "BAJA";
            default: return prioridad;
        }
    }
    
    private void verDetalleNotificacion(InsightIA notif) {
        String mensaje = String.format(
            "Explica en detalle esta notificacion: %s - %s. Accion recomendada: %s",
            notif.getTitulo(),
            notif.getDescripcion(),
            notif.getAccionRecomendada()
        );
        
        if (!chatVisible) toggleChat();
        txtInputUsuario.setText(mensaje);
        procesarMensajeChat();
    }
    
    private void cargarPrediccionesMultiples() {
        CompletableFuture.runAsync(() -> {
            try {
                prediccionesActivas = prediccionServicio.generarPrediccionesMultiples(
                    diasProyeccion, 10);
                
                Platform.runLater(() -> {
                    prediccionesContainer.getChildren().clear();
                    
                    for (PrediccionDemanda pred : prediccionesActivas) {
                        prediccionesContainer.getChildren().add(
                            crearTarjetaPrediccion(pred));
                    }
                });
                
            } catch (Exception e) {
                System.err.println("Error cargando predicciones: " + e.getMessage());
            }
        });
    }
    
    private VBox crearTarjetaPrediccion(PrediccionDemanda pred) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-padding: 16; " +
            "-fx-border-color: #dee2e6; -fx-border-radius: 10; -fx-border-width: 1; -fx-cursor: hand;"
        );
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label nombreProducto = new Label(pred.getProducto().getNombre());
        nombreProducto.setStyle("-fx-text-fill: #212529; -fx-font-size: 14px; -fx-font-weight: 600;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label confianza = new Label(pred.getNivelConfianzaPorcentaje() + "%");
        confianza.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 11px; " +
            "-fx-font-weight: 600; -fx-padding: 4 10; -fx-background-radius: 6;",
            pred.getNivelConfianza() > 0.7 ? "#51cf66" : 
            pred.getNivelConfianza() > 0.5 ? "#ff9900" : "#ff6b6b"
        ));
        
        header.getChildren().addAll(nombreProducto, spacer, confianza);
        
        HBox metricas = new HBox(20);
        metricas.setAlignment(Pos.CENTER_LEFT);
        
        VBox metrica1 = crearMetricaSmall("Stock Actual", 
            String.valueOf(pred.getProducto().getStock()));
        VBox metrica2 = crearMetricaSmall("Demanda Estimada", 
            String.valueOf(pred.getDemandaEstimada()));
        VBox metrica3 = crearMetricaSmall("Tendencia", pred.getTendencia());
        
        metricas.getChildren().addAll(metrica1, metrica2, metrica3);
        
        card.getChildren().addAll(header, metricas);
        
        if (pred.requiereReabastecimiento()) {
            HBox alerta = new HBox(8);
            alerta.setAlignment(Pos.CENTER_LEFT);
            alerta.setStyle(
                "-fx-background-color: #fff5f5; -fx-background-radius: 6; -fx-padding: 8; " +
                "-fx-border-color: #ff6b6b; -fx-border-radius: 6; -fx-border-width: 1;"
            );
            
            Label alertaLabel = new Label(String.format(
                "Requiere reabastecimiento: %d unidades",
                pred.getCantidadSugeridaReabastecimiento()
            ));
            alertaLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px; -fx-font-weight: 600;");
            
            alerta.getChildren().add(alertaLabel);
            card.getChildren().add(alerta);
        }
        
        card.setOnMouseClicked(e -> {
            this.productoActual = pred.getProducto();
            cbProductos.setValue(pred.getProducto().getNombre());
            actualizarGraficaPrincipal();
        });
        
        return card;
    }
    
    private VBox crearMetricaSmall(String label, String value) {
        VBox box = new VBox(4);
        
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 10px;");
        
        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-text-fill: #212529; -fx-font-size: 14px; -fx-font-weight: 600;");
        
        box.getChildren().addAll(lblLabel, lblValue);
        return box;
    }
    
    private void procesarMensajeChat() {
        String mensaje = txtInputUsuario.getText().trim();
        
        if (mensaje.isEmpty() || esperandoRespuestaIA) return;
        
        txtInputUsuario.clear();
        agregarMensajeChat("usuario", mensaje);
        
        esperandoRespuestaIA = true;
        actualizarEstadoIA("pensando");
        
        VBox indicador = mostrarIndicadorCarga();
        
        CompletableFuture.runAsync(() -> {
            try {
                ChatMensaje respuesta = chatBotServicio.procesarPregunta(mensaje);
                
                Platform.runLater(() -> {
                    chatContainer.getChildren().remove(indicador);
                    agregarMensajeChat("asistente", respuesta.getContenido());
                    esperandoRespuestaIA = false;
                    actualizarEstadoIA("lista");
                });
                
                verificarActualizacionVista(mensaje);
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    chatContainer.getChildren().remove(indicador);
                    agregarMensajeChat("asistente", 
                        "Error procesando mensaje: " + e.getMessage());
                    esperandoRespuestaIA = false;
                    actualizarEstadoIA("error");
                });
            }
        });
    }
    
    private void agregarMensajeChat(String rol, String contenido) {
        VBox mensaje = crearBurbujaMensaje(rol, contenido);
        chatContainer.getChildren().add(mensaje);
        
        Platform.runLater(() -> 
            scrollPaneChat.setVvalue(scrollPaneChat.getVmax())
        );
    }
    
    private VBox crearBurbujaMensaje(String rol, String contenido) {
        VBox burbuja = new VBox(6);
        burbuja.setMaxWidth(600);
        burbuja.setPadding(new Insets(12, 14, 12, 14));
        
        boolean esUsuario = "usuario".equals(rol);
        
        String estilo = esUsuario ?
            "-fx-background-color: #0066cc; -fx-background-radius: 16 16 4 16;" :
            "-fx-background-color: #f8f9fa; -fx-background-radius: 16 16 16 4; " +
            "-fx-border-color: #dee2e6; -fx-border-radius: 16 16 16 4; -fx-border-width: 1;";
        
        burbuja.setStyle(estilo);
        burbuja.setAlignment(esUsuario ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        Text texto = new Text(contenido);
        texto.setStyle(String.format(
            "-fx-fill: %s; -fx-font-size: 14px;",
            esUsuario ? "white" : "#212529"
        ));
        texto.setWrappingWidth(550);
        
        Label hora = new Label(LocalDateTime.now().format(formatterHora));
        hora.setStyle(String.format(
            "-fx-text-fill: %s; -fx-font-size: 10px;",
            esUsuario ? "rgba(255,255,255,0.8)" : "#6c757d"
        ));
        
        burbuja.getChildren().addAll(texto, hora);
        
        return burbuja;
    }
    
    private VBox mostrarIndicadorCarga() {
        VBox indicador = new VBox(6);
        indicador.setMaxWidth(160);
        indicador.setPadding(new Insets(12, 14, 12, 14));
        indicador.setStyle(
            "-fx-background-color: #f8f9fa; -fx-background-radius: 16 16 16 4; " +
            "-fx-border-color: #dee2e6; -fx-border-radius: 16 16 16 4; -fx-border-width: 1;"
        );
        
        Label texto = new Label("IA pensando...");
        texto.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px;");
        
        indicador.getChildren().add(texto);
        chatContainer.getChildren().add(indicador);
        
        return indicador;
    }
    
    private void mostrarMensajeBienvenidaChat() {
        Platform.runLater(() -> {
            agregarMensajeChat("asistente",
                "Hola, soy tu asistente inteligente de StockFlow. " +
                "Puedo ayudarte con analisis de ventas, predicciones, " +
                "recomendaciones de inventario y mas. Como puedo asistirte hoy?"
            );
        });
    }
    
    private void verificarActualizacionVista(String mensaje) {
        String msgLower = mensaje.toLowerCase();
        
        if (msgLower.contains("actualizar") || msgLower.contains("refrescar")) {
            Platform.runLater(this::refrescarDatos);
        }
    }
    
    private void ejecutarComandoRapido(String comando) {
        switch (comando) {
            case "ventas":
                txtInputUsuario.setText("Muestrame las ventas de esta semana");
                procesarMensajeChat();
                break;
            case "inventario":
                txtInputUsuario.setText("Dame un resumen completo del inventario");
                procesarMensajeChat();
                break;
            case "sugerencia":
                generarSugerenciaInteligente();
                break;
        }
    }
    
    private void generarSugerenciaInteligente() {
        if (!notificacionesActuales.isEmpty()) {
            InsightIA principal = notificacionesActuales.get(0);
            
            String pregunta = String.format(
                "Analiza esta situacion y dame una recomendacion: %s - %s",
                principal.getTitulo(),
                principal.getDescripcion()
            );
            
            txtInputUsuario.setText(pregunta);
            procesarMensajeChat();
        }
    }
    
    private void cambiarProducto() {
        String nombreSeleccionado = cbProductos.getValue();
        if (nombreSeleccionado != null && mapaProductos.containsKey(nombreSeleccionado)) {
            this.productoActual = mapaProductos.get(nombreSeleccionado);
            actualizarGraficaPrincipal();
        }
    }
    
    private void aplicarFiltros() {
        String periodoStr = cbPeriodo.getValue();
        diasProyeccion = Integer.parseInt(periodoStr.replaceAll("[^0-9]", ""));
        
        refrescarDatos();
    }
    
    private void refrescarDatos() {
        actualizarKPIsGlobales();
        actualizarGraficaPrincipal();
        actualizarTopProductos();
        cargarNotificaciones();
        cargarPrediccionesMultiples();
        actualizarHoraActualizacion();
    }
    
    private void exportarDatos() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar Datos");
        alert.setHeaderText("Funcionalidad de Exportacion");
        alert.setContentText("Los datos seran exportados a formato CSV/Excel.");
        alert.showAndWait();
    }
    
    private void cambiarVistaGrafica() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cambiar Vista");
        alert.setHeaderText("Opciones de Visualizacion");
        alert.setContentText("Puedes cambiar entre vista de lineas, barras o area.");
        alert.showAndWait();
    }
    
    private void mostrarPerfilUsuario() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Perfil de Usuario");
        alert.setHeaderText("Informacion del Usuario");
        alert.setContentText("Administrador del sistema StockFlow.");
        alert.showAndWait();
    }
    
    private void cargarDashboard() {
        btnDashboard.setStyle(btnDashboard.getStyle().replace("#f8f9fa", "#e7f3ff"));
        resetearOtrosBotones(btnDashboard);
        refrescarDatos();
    }
    
    private void mostrarStockCritico() {
        try {
            List<Producto> criticos = productoServicio.listarProductos().stream()
                .filter(Producto::tieneStockBajo)
                .limit(1)
                .collect(Collectors.toList());
            
            if (!criticos.isEmpty()) {
                this.productoActual = criticos.get(0);
                cbProductos.setValue(productoActual.getNombre());
                actualizarGraficaPrincipal();
            }
        } catch (Exception e) {
            mostrarError("Error mostrando stock critico: " + e.getMessage());
        }
        
        resetearOtrosBotones(btnStockCritico);
    }
    
    private void mostrarPromociones() {
        if (!chatVisible) toggleChat();
        txtInputUsuario.setText("Que productos deberia poner en promocion?");
        procesarMensajeChat();
        
        resetearOtrosBotones(btnPromociones);
    }
    
    private void mostrarProveedores() {
        if (!chatVisible) toggleChat();
        txtInputUsuario.setText("Que productos necesito ordenar a proveedores?");
        procesarMensajeChat();
        
        resetearOtrosBotones(btnProveedores);
    }
    
    private void mostrarAnalisisIA() {
        if (!chatVisible) toggleChat();
        txtInputUsuario.setText("Genera un analisis completo del negocio");
        procesarMensajeChat();
        
        resetearOtrosBotones(btnAnalisisIA);
    }
    
    private void mostrarPredicciones() {
        scrollPaneChat.setVvalue(1.0);
        resetearOtrosBotones(btnPredicciones);
    }
    
    private void resetearOtrosBotones(Button activo) {
        Button[] botones = {btnDashboard, btnStockCritico, btnPromociones, 
                           btnProveedores, btnAnalisisIA, btnPredicciones};
        
        for (Button btn : botones) {
            if (btn != activo) {
                String estilo = btn.getStyle();
                if (estilo.contains("#e7f3ff")) {
                    btn.setStyle(estilo.replace("#e7f3ff", "#f8f9fa")
                        .replace("#0066cc", "#212529"));
                }
            }
        }
    }
    
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        
        TranslateTransition transition = new TranslateTransition(
            Duration.millis(300), sidebarContainer);
        
        if (!sidebarVisible) {
            transition.setToX(-240);
            transition.setOnFinished(e -> {
                sidebarContainer.setVisible(false);
                sidebarContainer.setManaged(false);
            });
        } else {
            sidebarContainer.setVisible(true);
            sidebarContainer.setManaged(true);
            transition.setFromX(-240);
            transition.setToX(0);
        }
        
        transition.play();
    }
    
    private void toggleChat() {
        chatVisible = !chatVisible;
        
        FadeTransition fade = new FadeTransition(Duration.millis(200), chatModalContainer);
        
        if (chatVisible) {
            chatModalContainer.setVisible(true);
            chatModalContainer.setOpacity(0);
            fade.setToValue(1.0);
            fade.setOnFinished(e -> txtInputUsuario.requestFocus());
        } else {
            fade.setToValue(0);
            fade.setOnFinished(e -> chatModalContainer.setVisible(false));
        }
        
        fade.play();
    }
    
    private void toggleNotificaciones() {
        notificacionesVisible = !notificacionesVisible;
        
        FadeTransition fade = new FadeTransition(Duration.millis(200), panelNotificaciones);
        
        if (notificacionesVisible) {
            panelNotificaciones.setVisible(true);
            panelNotificaciones.setManaged(true);
            panelNotificaciones.setOpacity(0);
            fade.setToValue(1.0);
        } else {
            fade.setToValue(0);
            fade.setOnFinished(e -> {
                panelNotificaciones.setVisible(false);
                panelNotificaciones.setManaged(false);
            });
        }
        
        fade.play();
    }
    
    private void toggleCard(String cardName) {
        Label toggle = null;
        Text content = null;
        
        switch (cardName) {
            case "alerta":
                toggle = lblToggleAlerta;
                content = txtAlertCard;
                break;
            case "oportunidad":
                toggle = lblToggleOportunidad;
                content = txtOpportunityCard;
                break;
            case "analisis":
                toggle = lblToggleAnalisis;
                content = txtInsightCard;
                break;
        }
        
        if (toggle != null && content != null) {
            boolean visible = content.isVisible();
            content.setVisible(!visible);
            content.setManaged(!visible);
            toggle.setText(visible ? "+" : "-");
        }
    }
    
    private void actualizarEstadoIA(String estado) {
        String texto, color, bgColor;
        
        switch (estado) {
            case "pensando":
                texto = "IA Procesando";
                color = "#ff9900";
                bgColor = "#fff9e6";
                break;
            case "error":
                texto = "Error IA";
                color = "#ff6b6b";
                bgColor = "#fff5f5";
                break;
            default:
                texto = "IA Lista";
                color = "#51cf66";
                bgColor = "#f0fff4";
        }
        
        String estilo = String.format(
            "-fx-text-fill: %s; -fx-font-size: 11; -fx-font-weight: 600; " +
            "-fx-padding: 8 12; -fx-background-color: %s; -fx-background-radius: 8;",
            color, bgColor
        );
        
        lblEstadoIA.setText(texto);
        lblEstadoIA.setStyle(estilo);
        
        if (lblEstadoIAChat != null) {
            lblEstadoIAChat.setText(texto);
        }
    }
    
    private void actualizarHoraActualizacion() {
        String hora = LocalDateTime.now().format(formatterFecha);
        lblUltimaActualizacion.setText("Actualizado: " + hora);
    }
    
    private void iniciarActualizacionAutomatica() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                actualizarKPIsGlobales();
                cargarNotificaciones();
                actualizarHoraActualizacion();
            }
        }, 60000, 60000);
    }
    
    private void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error en el Sistema");
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
}