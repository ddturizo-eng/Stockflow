package com.mycompany.stockflow;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Logica.ProductoServicio;
import com.mycompany.stockflow.utils.ImagenProductoUtil;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Productos_invitadoController implements Initializable {
    
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotalProductos;
    @FXML private Button btnBuscar;
    @FXML private Button btnVolver;
    @FXML private HBox contenedorFiltros;
    @FXML private FlowPane gridProductos;
    @FXML private Button btnabrirChatBot;
    
    private ProductoServicio productoServicio;
    private ObservableList<Producto> listaProductos;
    private FilteredList<Producto> filtrado;
    private String categoriaActual = "TODAS";
    private List<Button> botonesCategorias = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productoServicio = new ProductoServicio();
        listaProductos = FXCollections.observableArrayList();
        
        cargarProductos();
        cargarCategoriasdinamicas();
        configurarBusqueda();
        configurarAnimaciones();
        actualizarContador();
    }

    private void cargarProductos() {
        try {
            List<Producto> productos = productoServicio.listarProductos();
            listaProductos.clear();
            listaProductos.addAll(productos);
            
            filtrado = new FilteredList<>(listaProductos, p -> true);
            
            mostrarProductosEnGrid();
            actualizarContador();
            animarEntradaGrid();
            
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
        }
    }

    /**
     * Muestra los productos en formato de grid (cards de tienda)
     */
    private void mostrarProductosEnGrid() {
        gridProductos.getChildren().clear();
        
        for (Producto producto : filtrado) {
            VBox card = crearTarjetaProducto(producto);
            gridProductos.getChildren().add(card);
        }
    }

    /**
     * Crea una tarjeta visual para cada producto (estilo e-commerce)
     */
    private VBox crearTarjetaProducto(Producto producto) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(240);
        card.setMaxWidth(240);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-border-color: #E0E6ED; -fx-border-radius: 12; -fx-border-width: 1.5; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); " +
                     "-fx-cursor: hand;");
        card.setPadding(new Insets(15));
        
        // Contenedor de imagen
        VBox contenedorImagen = new VBox();
        contenedorImagen.setAlignment(Pos.CENTER);
        contenedorImagen.setPrefHeight(200);
        contenedorImagen.setMaxHeight(200);
        contenedorImagen.setStyle("-fx-background-color: #F5F7FA; -fx-background-radius: 8;");
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        
        // Cargar imagen del producto
        Image imagen;
        if (producto.tieneImagen()) {
            imagen = ImagenProductoUtil.cargarImagen(producto.getRutaImagen());
            if (imagen == null) {
                imagen = ImagenProductoUtil.obtenerImagenPorDefecto();
            }
        } else {
            imagen = ImagenProductoUtil.obtenerImagenPorDefecto();
        }
        
        if (imagen != null) {
            imageView.setImage(imagen);
        }
        
        contenedorImagen.getChildren().add(imageView);
        
        // Badge de descuento/oferta (opcional - basado en margen)
        if (producto.tieneMargenBajo()) {
            Label badgeOferta = new Label("¡OFERTA!");
            badgeOferta.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                               "-fx-background-radius: 15; -fx-padding: 4 12; -fx-font-size: 10px; " +
                               "-fx-font-weight: bold;");
            VBox.setMargin(badgeOferta, new Insets(-10, 0, 0, 10));
            contenedorImagen.getChildren().add(0, badgeOferta);
        }
        
        // Nombre del producto
        Label lblNombre = new Label(producto.getNombre());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; " +
                          "-fx-text-alignment: center;");
        lblNombre.setWrapText(true);
        lblNombre.setMaxWidth(220);
        lblNombre.setAlignment(Pos.CENTER);
        
        // Código del producto
        Label lblCodigo = new Label("Código: " + producto.getCodigo());
        lblCodigo.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
        
        // Categoría
        Label lblCategoria = new Label(producto.getCategoria());
        lblCategoria.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2a5298; " +
                            "-fx-background-radius: 12; -fx-padding: 4 10; -fx-font-size: 10px; " +
                            "-fx-font-weight: bold;");
        
        // Precio
        Label lblPrecio = new Label(String.format("$%,.2f", producto.getPrecioVenta()));
        lblPrecio.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        
        // Stock badge
        Label lblStock = new Label();
        int stock = producto.getStock();
        int stockMinimo = producto.getStockMinimo();
        
        if (stock == 0) {
            lblStock.setText("AGOTADO");
            lblStock.setStyle("-fx-background-color: #ffe6e6; -fx-text-fill: #e74c3c; " +
                            "-fx-background-radius: 12; -fx-padding: 6 12; -fx-font-size: 11px; " +
                            "-fx-font-weight: bold;");
        } else if (stock <= stockMinimo) {
            lblStock.setText("ÚLTIMAS UNIDADES (" + stock + ")");
            lblStock.setStyle("-fx-background-color: #ffe6e6; -fx-text-fill: #e74c3c; " +
                            "-fx-background-radius: 12; -fx-padding: 6 12; -fx-font-size: 11px; " +
                            "-fx-font-weight: bold;");
        } else if (stock <= stockMinimo * 2) {
            lblStock.setText("POCAS UNIDADES (" + stock + ")");
            lblStock.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #f39c12; " +
                            "-fx-background-radius: 12; -fx-padding: 6 12; -fx-font-size: 11px; " +
                            "-fx-font-weight: bold;");
        } else {
            lblStock.setText("DISPONIBLE (" + stock + " unidades)");
            lblStock.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #27ae60; " +
                            "-fx-background-radius: 12; -fx-padding: 6 12; -fx-font-size: 11px; " +
                            "-fx-font-weight: bold;");
        }
        
        // Separador
        javafx.scene.control.Separator separador = new javafx.scene.control.Separator();
        separador.setStyle("-fx-background-color: #E0E6ED;");
        
        // Botón de ver detalles
        Button btnVerDetalles = new Button("Ver Detalles");
        btnVerDetalles.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; " +
                               "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold; " +
                               "-fx-padding: 10 20; -fx-font-size: 12px;");
        btnVerDetalles.setMaxWidth(Double.MAX_VALUE);
        btnVerDetalles.setOnAction(e -> mostrarDetallesProducto(producto));
        
        // Efecto hover en el botón
        btnVerDetalles.setOnMouseEntered(e -> {
            btnVerDetalles.setStyle("-fx-background-color: #1e3a72; -fx-text-fill: white; " +
                                   "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold; " +
                                   "-fx-padding: 10 20; -fx-font-size: 12px;");
        });
        btnVerDetalles.setOnMouseExited(e -> {
            btnVerDetalles.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; " +
                                   "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold; " +
                                   "-fx-padding: 10 20; -fx-font-size: 12px;");
        });
        
        // Efecto hover en la tarjeta completa
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                         "-fx-border-color: #2a5298; -fx-border-radius: 12; -fx-border-width: 2; " +
                         "-fx-effect: dropshadow(gaussian, rgba(42,82,152,0.3), 12, 0, 0, 4); " +
                         "-fx-cursor: hand;");
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), card);
            scale.setToX(1.03);
            scale.setToY(1.03);
            scale.play();
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                         "-fx-border-color: #E0E6ED; -fx-border-radius: 12; -fx-border-width: 1.5; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); " +
                         "-fx-cursor: hand;");
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        // Agregar todos los elementos a la tarjeta
        card.getChildren().addAll(
            contenedorImagen,
            lblNombre,
            lblCodigo,
            lblCategoria,
            lblPrecio,
            lblStock,
            separador,
            btnVerDetalles
        );
        
        return card;
    }

    /**
     * Muestra un diálogo con los detalles completos del producto
     */
    private void mostrarDetallesProducto(Producto producto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Producto");
        alert.setHeaderText(producto.getNombre());
        
        // Crear contenido personalizado
        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(10));
        
        // Imagen del producto
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        
        Image imagen;
        if (producto.tieneImagen()) {
            imagen = ImagenProductoUtil.cargarImagen(producto.getRutaImagen());
            if (imagen == null) {
                imagen = ImagenProductoUtil.obtenerImagenPorDefecto();
            }
        } else {
            imagen = ImagenProductoUtil.obtenerImagenPorDefecto();
        }
        
        if (imagen != null) {
            imageView.setImage(imagen);
        }
        
        VBox contenedorImagen = new VBox(imageView);
        contenedorImagen.setAlignment(Pos.CENTER);
        contenedorImagen.setStyle("-fx-background-color: #F5F7FA; -fx-background-radius: 8; -fx-padding: 10;");
        
        // Información del producto
        String detalles = String.format(
            "Código: %s\n\n" +
            "Categoría: %s\n\n" +
            "Precio de Venta: $%,.2f\n\n" +
            "Stock Disponible: %d unidades\n\n" +
            "%s",
            producto.getCodigo(),
            producto.getCategoria(),
            producto.getPrecioVenta(),
            producto.getStock(),
            producto.getDescripcion() != null && !producto.getDescripcion().isEmpty() 
                ? "Descripción:\n" + producto.getDescripcion() 
                : "Sin descripción disponible"
        );

        
        Label lblDetalles = new Label(detalles);
        lblDetalles.setWrapText(true);
        lblDetalles.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");
        
        contenido.getChildren().addAll(contenedorImagen, lblDetalles);
        
        alert.getDialogPane().setContent(contenido);
        alert.getDialogPane().setMinWidth(500);
        alert.getDialogPane().setStyle("-fx-background-color: white;");
        
        alert.showAndWait();
    }

    private void cargarCategoriasdinamicas() {
        if (contenedorFiltros == null) {
            System.err.println("Error: contenedorFiltros es null");
            return;
        }
        
        contenedorFiltros.getChildren().clear();
        botonesCategorias.clear();
        
        Label lblCategorias = new Label("Categorías:");
        lblCategorias.setStyle("-fx-text-fill: #34495e; -fx-font-weight: bold; -fx-font-size: 13px;");
        contenedorFiltros.getChildren().add(lblCategorias);
        
        Button btnTodas = crearBotonCategoria("Todas las Categorías", true);
        btnTodas.setOnAction(e -> filtrarPorCategoria("TODAS", btnTodas));
        contenedorFiltros.getChildren().add(btnTodas);
        botonesCategorias.add(btnTodas);
        
        Set<String> categoriasUnicas = listaProductos.stream()
            .map(Producto::getCategoria)
            .filter(Objects::nonNull)
            .filter(cat -> !cat.trim().isEmpty())
            .collect(Collectors.toCollection(TreeSet::new));
        
        for (String categoria : categoriasUnicas) {
            Button btnCategoria = crearBotonCategoria(categoria, false);
            btnCategoria.setOnAction(e -> filtrarPorCategoria(categoria, btnCategoria));
            contenedorFiltros.getChildren().add(btnCategoria);
            botonesCategorias.add(btnCategoria);
        }
        
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        contenedorFiltros.getChildren().add(spacer);
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
            
            boolean cumpleBusqueda = true;
            if (textoBusqueda != null && !textoBusqueda.isEmpty()) {
                String filtro = textoBusqueda.toLowerCase().trim();
                cumpleBusqueda = producto.getCodigo().toLowerCase().contains(filtro)
                    || producto.getNombre().toLowerCase().contains(filtro)
                    || (producto.getCategoria() != null && 
                        producto.getCategoria().toLowerCase().contains(filtro));
            }
            
            boolean cumpleCategoria = true;
            if (!categoriaActual.equals("TODAS")) {
                cumpleCategoria = producto.getCategoria() != null && 
                    producto.getCategoria().equalsIgnoreCase(categoriaActual);
            }
            
            return cumpleBusqueda && cumpleCategoria;
        });
        
        mostrarProductosEnGrid();
        actualizarContador();
        animarActualizacion();
    }

    private void actualizarContador() {
        int total = filtrado != null ? filtrado.size() : listaProductos.size();
        lblTotalProductos.setText("Total: " + total + " producto" + (total != 1 ? "s" : ""));
        
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
        String estiloInactivo = "-fx-background-color: white; -fx-background-radius: 20; -fx-cursor: hand; " +
                               "-fx-text-fill: #6B7C93; -fx-border-color: #E0E6ED; -fx-border-radius: 20; " +
                               "-fx-border-width: 1.5; -fx-padding: 8 20 8 20; -fx-font-size: 12px;";
        
        String estiloActivo = "-fx-background-color: #2a5298; -fx-background-radius: 20; -fx-cursor: hand; " +
                             "-fx-text-fill: white; -fx-padding: 8 20 8 20; -fx-font-size: 12px;";
        
        for (Button boton : botonesCategorias) {
            boton.setStyle(estiloInactivo);
        }
        
        botonActivo.setStyle(estiloActivo);
        
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
            animarPulso(btnBuscar);
        }
    }

    private void configurarAnimaciones() {
        agregarEfectoHover(btnBuscar);
        agregarEfectoHover(btnVolver);
        
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

    private void animarEntradaGrid() {
        gridProductos.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(400), gridProductos);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void animarActualizacion() {
        FadeTransition fade = new FadeTransition(Duration.millis(200), gridProductos);
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
            
            scene.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.F11) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });
            
            stage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");
            
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
        
        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-border-color: #E0E6ED; -fx-border-width: 1;");
        
        alerta.showAndWait();
    }
    // AGREGAR ESTE MÉTODO AL FINAL DE LA CLASE Productos_invitadoController
/**
     * Abre una ventana del chatbot de asistencia al cliente
     */
// TAMBIÉN AGREGAR ESTA IMPORT AL INICIO DEL ARCHIVO:
// import javafx.scene.Scene;
    


// ====== CÓDIGO COMPLETO DEL MÉTODO PARA COPIAR ======

// Agregar después del método volverInicio y antes del método mostrarAlerta:

    /**
     * Abre una ventana del chatbot de asistencia al cliente
     */
    @FXML
    private void abrirChatBot(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatBotInvitado.fxml"));
            Parent root = loader.load();
            
            Stage chatStage = new Stage();
            chatStage.setTitle("Asistente Virtual - StockFlow");
            chatStage.setScene(new Scene(root, 800, 650));
            chatStage.setResizable(true);
            chatStage.setMinWidth(600);
            chatStage.setMinHeight(500);
            
            chatStage.show();
            
        } catch (IOException e) {
            System.err.println("Error al abrir chatbot: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", 
                "No se pudo abrir el asistente virtual", 
                Alert.AlertType.ERROR);
        }
    }
}