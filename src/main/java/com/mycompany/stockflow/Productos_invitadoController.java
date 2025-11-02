package com.mycompany.stockflow;

import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Logica.ProductoServicio;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Productos_invitadoController implements Initializable {
    
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Number> colPrecioVenta;
    @FXML private TableColumn<Producto, Number> colStock;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotalProductos;
    @FXML private Button btnBuscar;
    @FXML private Button btnVolver;
    @FXML private HBox contenedorFiltros; // Contenedor donde se agregarán los botones dinámicos
    
    private ProductoServicio productoServicio;
    private ObservableList<Producto> listaProductos;
    private FilteredList<Producto> filtrado;
    private String categoriaActual = "TODAS";
    private List<Button> botonesCategorias = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productoServicio = new ProductoServicio();
        listaProductos = FXCollections.observableArrayList();
        
        configurarColumnas();
        cargarProductos();
        cargarCategoriasdinamicas();
        configurarBusqueda();
        configurarAnimaciones();
        actualizarContador();
    }

    private void configurarColumnas() {
        // Configurar las columnas usando PropertyValueFactory
        colCodigo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCodigo()));
        
        colNombre.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombre()));
        
        colCategoria.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategoria()));
        
        // Columna de Precio con formato moderno
        colPrecioVenta.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getPrecioVenta()));
        
        colPrecioVenta.setCellFactory(col -> new TableCell<Producto, Number>() {
            @Override
            protected void updateItem(Number precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("$%,.2f", precio.doubleValue()));
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 13px;");
                }
            }
        });
        
        // Columna de Stock con badges modernos
        colStock.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getStock()));
        
        colStock.setCellFactory(col -> new TableCell<Producto, Number>() {
            @Override
            protected void updateItem(Number stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                    setGraphic(null);
                } else {
                    Producto producto = getTableView().getItems().get(getIndex());
                    int stockValue = stock.intValue();
                    int stockMinimo = producto.getStockMinimo();
                    
                    // Crear badge con estilo
                    Label badge = new Label();
                    badge.setStyle("-fx-background-radius: 15; -fx-padding: 5 12 5 12; -fx-font-weight: bold; -fx-font-size: 11px;");
                    
                    if (stockValue == 0) {
                        badge.setText("AGOTADO");
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #ffe6e6; -fx-text-fill: #e74c3c;");
                    } else if (stockValue <= stockMinimo) {
                        // Stock bajo: está en o por debajo del mínimo
                        badge.setText("Bajo Stock (" + stockValue + " unidades)");
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #ffe6e6; -fx-text-fill: #e74c3c;");
                    } else if (stockValue <= stockMinimo * 2) {
                        // Stock limitado: está entre el mínimo y el doble del mínimo
                        badge.setText("Stock Limitado (" + stockValue + " unidades)");
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #fff3cd; -fx-text-fill: #f39c12;");
                    } else {
                        badge.setText("Disponible (" + stockValue + " unidades)");
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #d4edda; -fx-text-fill: #27ae60;");
                    }
                    
                    setText(null);
                    setGraphic(badge);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
        
        // Estilo para las celdas
        colCodigo.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        colNombre.setStyle("-fx-text-fill: #34495e; -fx-font-size: 13px;");
        colCategoria.setStyle("-fx-text-fill: #7f8c8d;");
    }

    private void cargarProductos() {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            listaProductos.clear();
            listaProductos.addAll(productos);
            
            filtrado = new FilteredList<>(listaProductos, p -> true);
            tablaProductos.setItems(filtrado);
            
            actualizarContador();
            animarEntradaTabla();
            
            if (productos.isEmpty()) {
                mostrarAlerta("Catálogo vacío", 
                    "No hay productos registrados en el sistema", 
                    Alert.AlertType.INFORMATION);
            }
            
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", 
                "No se pudieron cargar los productos desde la base de datos", 
                Alert.AlertType.ERROR);
            
            filtrado = new FilteredList<>(listaProductos, p -> true);
            tablaProductos.setItems(filtrado);
        }
    }

    private void cargarCategoriasdinamicas() {
        if (contenedorFiltros == null) {
            System.err.println("Error: contenedorFiltros es null");
            return;
        }
        
        // Limpiar botones existentes
        contenedorFiltros.getChildren().clear();
        botonesCategorias.clear();
        
        // Agregar label "Categorías:"
        Label lblCategorias = new Label("Categorías:");
        lblCategorias.setStyle("-fx-text-fill: #34495e; -fx-font-weight: bold; -fx-font-size: 13px;");
        contenedorFiltros.getChildren().add(lblCategorias);
        
        // Botón "Todas las Categorías"
        Button btnTodas = crearBotonCategoria("Todas las Categorías", true);
        btnTodas.setOnAction(e -> filtrarPorCategoria("TODAS", btnTodas));
        contenedorFiltros.getChildren().add(btnTodas);
        botonesCategorias.add(btnTodas);
        
        // Obtener categorías únicas de los productos
        Set<String> categoriasUnicas = listaProductos.stream()
            .map(Producto::getCategoria)
            .filter(Objects::nonNull)
            .filter(cat -> !cat.trim().isEmpty())
            .collect(Collectors.toCollection(TreeSet::new)); // TreeSet para ordenar alfabéticamente
        
        // Crear un botón por cada categoría
        for (String categoria : categoriasUnicas) {
            Button btnCategoria = crearBotonCategoria(categoria, false);
            btnCategoria.setOnAction(e -> filtrarPorCategoria(categoria, btnCategoria));
            contenedorFiltros.getChildren().add(btnCategoria);
            botonesCategorias.add(btnCategoria);
        }
        
        // Agregar espaciador y botón de filtros al final
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        contenedorFiltros.getChildren().add(spacer);
        
        Button btnFiltros = new Button("️");
        btnFiltros.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #6B7C93; " +
                           "-fx-border-color: #E0E6ED; -fx-border-radius: 8; -fx-border-width: 1.5; " +
                           "-fx-padding: 8 15 8 15; -fx-font-size: 14px;");
        agregarEfectoHover(btnFiltros);
        contenedorFiltros.getChildren().add(btnFiltros);
    }

    private Button crearBotonCategoria(String texto, boolean activo) {
        Button boton = new Button(texto);
        
        if (activo) {
            boton.setStyle("-fx-background-color: #2a5298; -fx-background-radius: 20; -fx-cursor: hand; " +
                          "-fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-font-size: 12px;");
        } else {
            boton.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-cursor: hand; " +
                          "-fx-text-fill: #6B7C93; -fx-border-color: #E0E6ED; -fx-border-radius: 20; " +
                          "-fx-border-width: 1.5; -fx-padding: 8 20 8 20; -fx-font-size: 12px;");
        }
        
        agregarEfectoHover(boton);
        return boton;
    }

    private void filtrarPorCategoria(String categoria, Button botonPresionado) {
        categoriaActual = categoria;
        actualizarEstiloBotones(botonPresionado);
        aplicarFiltros();
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            aplicarFiltros();
        });
    }

    private void aplicarFiltros() {
        filtrado.setPredicate(producto -> {
            String textoBusqueda = txtBuscar.getText();
            
            // Filtro de búsqueda
            boolean cumpleBusqueda = true;
            if (textoBusqueda != null && !textoBusqueda.isEmpty()) {
                String filtro = textoBusqueda.toLowerCase().trim();
                cumpleBusqueda = producto.getCodigo().toLowerCase().contains(filtro)
                    || producto.getNombre().toLowerCase().contains(filtro)
                    || (producto.getCategoria() != null && 
                        producto.getCategoria().toLowerCase().contains(filtro));
            }
            
            // Filtro de categoría
            boolean cumpleCategoria = true;
            if (!categoriaActual.equals("TODAS")) {
                cumpleCategoria = producto.getCategoria() != null && 
                    producto.getCategoria().equalsIgnoreCase(categoriaActual);
            }
            
            return cumpleBusqueda && cumpleCategoria;
        });
        
        actualizarContador();
        animarActualizacion();
    }

    private void actualizarContador() {
        int total = filtrado != null ? filtrado.size() : listaProductos.size();
        lblTotalProductos.setText("Total: " + total + " producto" + (total != 1 ? "s" : ""));
        
        // Animación del contador
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), lblTotalProductos);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    private void actualizarEstiloBotones(Button botonActivo) {
        // Estilo inactivo para todos
        String estiloInactivo = "-fx-background-color: white; -fx-background-radius: 20; -fx-cursor: hand; " +
                               "-fx-text-fill: #6B7C93; -fx-border-color: #E0E6ED; -fx-border-radius: 20; " +
                               "-fx-border-width: 1.5; -fx-padding: 8 20 8 20; -fx-font-size: 12px;";
        
        // Estilo activo
        String estiloActivo = "-fx-background-color: #2a5298; -fx-background-radius: 20; -fx-cursor: hand; " +
                             "-fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-font-size: 12px;";
        
        // Aplicar estilo inactivo a todos los botones de categoría
        for (Button boton : botonesCategorias) {
            boton.setStyle(estiloInactivo);
        }
        
        // Aplicar estilo activo al botón seleccionado
        botonActivo.setStyle(estiloActivo);
        
        // Animación del botón
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), botonActivo);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
    }

    @FXML
    private void buscarProducto(ActionEvent event) {
        String texto = txtBuscar.getText().trim();
        
        if (texto.isEmpty()) {
            mostrarAlerta("Búsqueda", 
                "Por favor ingrese un término de búsqueda", 
                Alert.AlertType.INFORMATION);
            return;
        }
        
        if (filtrado != null && filtrado.isEmpty()) {
            mostrarAlerta("Sin resultados", 
                "No se encontraron productos que coincidan con: \"" + texto + "\"", 
                Alert.AlertType.INFORMATION);
        } else if (filtrado != null) {
            // Animar feedback de búsqueda exitosa
            animarPulso(btnBuscar);
        }
    }

    private void configurarAnimaciones() {
        // Hover effects para botones principales
        agregarEfectoHover(btnBuscar);
        agregarEfectoHover(btnVolver);
        
        // Animación del campo de búsqueda
        txtBuscar.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), txtBuscar.getParent());
                scale.setToX(1.02);
                scale.setToY(1.02);
                scale.play();
            } else {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), txtBuscar.getParent());
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            }
        });
    }

    private void agregarEfectoHover(Button boton) {
        boton.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), boton);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });
        
        boton.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), boton);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }

    private void animarEntradaTabla() {
        tablaProductos.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(400), tablaProductos);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void animarActualizacion() {
        FadeTransition fade = new FadeTransition(Duration.millis(200), tablaProductos);
        fade.setFromValue(0.7);
        fade.setToValue(1.0);
        fade.play();
    }

    private void animarPulso(Button boton) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), boton);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.95);
        scale.setToY(0.95);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    @FXML
    private void volverInicio(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Bienvenida.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 600);
            
            stage.setScene(scene);
            stage.setTitle("StockFlow - Bienvenida");
            stage.setResizable(true);
            stage.setMaximized(true);
            
            // Configurar F11 para pantalla completa (compatible con Bienvenida)
            scene.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.F11) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });
            
            stage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");
            
            // Animación de entrada
            root.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            
        } catch (IOException e) {
            System.err.println("Error al volver a inicio: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", 
                "No se pudo cargar la pantalla de inicio", 
                Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        
        // Estilo personalizado para alertas
        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-border-color: #E0E6ED; -fx-border-width: 1;");
        
        alerta.showAndWait();
    }
}