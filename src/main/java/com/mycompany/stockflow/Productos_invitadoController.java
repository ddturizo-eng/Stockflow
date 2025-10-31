package com.mycompany.stockflow;

import javafx.animation.FadeTransition;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Logica.ProductoServicio;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    
    private ProductoServicio productoServicio;
    private ObservableList<Producto> listaProductos;
    private FilteredList<Producto> filtrado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productoServicio = new ProductoServicio();
        listaProductos = FXCollections.observableArrayList();
        
        configurarColumnas();
        cargarProductos();
        configurarBusqueda();
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
        
        // Columna de Precio con formato
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
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });
        
        // Columna de Stock con colores según disponibilidad
        colStock.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getStock()));
        
        colStock.setCellFactory(col -> new TableCell<Producto, Number>() {
            @Override
            protected void updateItem(Number stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    int stockValue = stock.intValue();
                    setText(stockValue + " unidades");
                    
                    // Colores según disponibilidad
                    if (stockValue == 0) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        setText("AGOTADO");
                    } else if (stockValue < 10) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else if (stockValue < 30) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60;");
                    }
                }
            }
        });
    }

    private void cargarProductos() {
        try {
            // Cargar productos desde la base de datos usando el servicio
            List<Producto> productos = productoServicio.listarProductos();
            listaProductos.clear();
            listaProductos.addAll(productos);
            
            // Aplicar filtro inicial (mostrar todos)
            filtrado = new FilteredList<>(listaProductos, p -> true);
            tablaProductos.setItems(filtrado);
            
            actualizarContador();
            
            // Mostrar mensaje si no hay productos
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
            
            // Inicializar con lista vacía en caso de error
            filtrado = new FilteredList<>(listaProductos, p -> true);
            tablaProductos.setItems(filtrado);
        }
    }

    private void configurarBusqueda() {
        // Búsqueda en tiempo real mientras el usuario escribe
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrado.setPredicate(producto -> {
                // Si el campo está vacío, mostrar todos los productos
                if (newVal == null || newVal.isEmpty()) {
                    actualizarContador();
                    return true;
                }
                
                String filtro = newVal.toLowerCase().trim();
                
                // Buscar en código, nombre y categoría
                boolean coincide = producto.getCodigo().toLowerCase().contains(filtro)
                    || producto.getNombre().toLowerCase().contains(filtro)
                    || (producto.getCategoria() != null && 
                        producto.getCategoria().toLowerCase().contains(filtro));
                
                actualizarContador();
                return coincide;
            });
        });
    }

    private void actualizarContador() {
        int total = filtrado != null ? filtrado.size() : listaProductos.size();
        lblTotalProductos.setText("Total: " + total + " producto" + (total != 1 ? "s" : ""));
    }

    @FXML
    private void buscarProducto(ActionEvent event) {
        // La búsqueda ya funciona en tiempo real con el listener
        String texto = txtBuscar.getText().trim();
        
        if (texto.isEmpty()) {
            mostrarAlerta("Búsqueda", 
                "Por favor ingrese un término de búsqueda", 
                Alert.AlertType.INFORMATION);
            return;
        }
        
        // Verificar si hay resultados
        if (filtrado != null && filtrado.isEmpty()) {
            mostrarAlerta("Sin resultados", 
                "No se encontraron productos que coincidan con: \"" + texto + "\"", 
                Alert.AlertType.INFORMATION);
        } else if (filtrado != null) {
            mostrarAlerta("Resultados", 
                "Se encontraron " + filtrado.size() + " producto(s)", 
                Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void volverInicio(ActionEvent event) {
        try {
            // Cargar pantalla de bienvenida
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Bienvenida.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 600);
            
            stage.setScene(scene);
            stage.setTitle("StockFlow - Bienvenida");
            stage.setResizable(true);
            stage.setMaximized(true);
            
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
        alerta.showAndWait();
    }
}