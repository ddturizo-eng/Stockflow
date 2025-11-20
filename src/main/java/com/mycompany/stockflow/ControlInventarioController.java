/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
    package com.mycompany.stockflow;

    import com.mycompany.stockflow.Modelo.Producto;
    import com.mycompany.stockflow.Modelo.MovimientoInventario;
    import com.mycompany.stockflow.Logica.InventarioServicio;
    import com.mycompany.stockflow.Logica.ProductoServicio;

    import javafx.beans.property.SimpleIntegerProperty;
    import javafx.beans.property.SimpleStringProperty;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.util.StringConverter;
    import javafx.geometry.Pos;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.List;
    import java.util.stream.Collectors;
    import com.mycompany.stockflow.utils.GeneradorReporteInventario;
    import javafx.stage.FileChooser;
    import java.io.File;
    import java.io.FileWriter;
    import java.io.PrintWriter;

    /**
     * Controlador para la gestión integral del inventario de productos.
     * 
     * Esta clase implementa la lógica de presentación para el módulo de control de inventario,
     * proporcionando funcionalidades completas para monitorear, registrar movimientos y generar
     * reportes del stock. Integra JavaFX con los servicios de inventario y productos.
     * 
     * Características principales:
     * - Dashboard con estadísticas en tiempo real (total productos, stock, alertas, movimientos)
     * - Tabla de alertas para productos con stock bajo, crítico o sin stock
     * - Registro de movimientos de inventario (entrada, salida, ajuste)
     * - Historial completo de movimientos con filtros avanzados
     * - Vista previa dinámica de cambios de stock
     * - Generación de reportes en PDF
     * - Exportación de alertas a CSV
     * - Notificaciones de alertas críticas con múltiples opciones de exportación
     * - Reabastecimiento rápido desde alertas
     * - Búsqueda y filtrado en tiempo real
     * 
     * @author StockFlow Team
     * @version 2.0
     * @see InventarioServicio
     * @see ProductoServicio
     * @see MovimientoInventario
     * @see Producto
     */
    public class ControlInventarioController {

        @FXML private Label lblTotalProductos;
        @FXML private Label lblStockTotal;
        @FXML private Label lblCantidadAlertas;
        @FXML private Label lblMovimientosHoy;
        @FXML private Label lblCantidadAlertasBadge;


        @FXML private TableView<ProductoStockItem> tblProductosStockBajo;
        @FXML private TableColumn<ProductoStockItem, String> colAlertaCodigo;
        @FXML private TableColumn<ProductoStockItem, String> colAlertaProducto;
        @FXML private TableColumn<ProductoStockItem, Integer> colAlertaStock;
        @FXML private TableColumn<ProductoStockItem, Integer> colAlertaMinimo;
        @FXML private TableColumn<ProductoStockItem, String> colAlertaEstado;
        @FXML private TableColumn<ProductoStockItem, Void> colAlertaAccion;

        @FXML private ComboBox<String> cmbTipoMovimiento;
        @FXML private ComboBox<Producto> cmbProducto;
        @FXML private TextField txtCantidad;
        @FXML private TextField txtMotivo;
        @FXML private Label lblStockActualNum;
        @FXML private Label lblStockNuevoNum;


        @FXML private TextField txtBuscarProducto;
        @FXML private ComboBox<String> cmbFiltroTipo;
        @FXML private ComboBox<String> cmbFiltroPeriodo;
        @FXML private TableView<MovimientoItem> tblMovimientos;
        @FXML private TableColumn<MovimientoItem, String> colCodigo;
        @FXML private TableColumn<MovimientoItem, String> colFecha;
        @FXML private TableColumn<MovimientoItem, String> colTipo;
        @FXML private TableColumn<MovimientoItem, String> colProducto;
        @FXML private TableColumn<MovimientoItem, String> colCantidad;
        @FXML private TableColumn<MovimientoItem, Integer> colStockAnterior;
        @FXML private TableColumn<MovimientoItem, Integer> colStockNuevo;
        @FXML private TableColumn<MovimientoItem, String> colMotivo;
        @FXML private Label lblTotalMovimientos;


        private InventarioServicio inventarioServicio;
        private ProductoServicio productoServicio;
        private ObservableList<ProductoStockItem> productosStockBajo;
        private ObservableList<MovimientoItem> movimientoItems;
        private ObservableList<MovimientoItem> movimientosFiltrados;

        /**
         * Constructor que inicializa los servicios y listas observables.
         * 
         * Crea instancias de los servicios necesarios y las colecciones
         * observables para mantener sincronizados los datos con la interfaz.
         */
        public ControlInventarioController() {
            this.inventarioServicio = new InventarioServicio();
            this.productoServicio = new ProductoServicio();
            this.productosStockBajo = FXCollections.observableArrayList();
            this.movimientoItems = FXCollections.observableArrayList();
            this.movimientosFiltrados = FXCollections.observableArrayList();
        }

        /**
         * Inicializa el controlador cuando el archivo FXML se carga.
         * 
         * Configura todos los componentes de la interfaz, carga datos iniciales
         * y establece los listeners para eventos en tiempo real.
         */
        @FXML
        public void initialize() {
            System.out.println("ControlInventarioController inicializado - Versión mejorada");

            configurarDashboard();
            configurarTablaAlertas();
            configurarTablaMovimientos();
            configurarComboBoxes();
            configurarEventos();

            cargarDatosIniciales();
        }

        /**
         * Configura el dashboard con estadísticas iniciales.
         * 
         * Llamado en la inicialización para preparar los labels del dashboard.
         */
        private void configurarDashboard() {
            actualizarDashboard();
        }

        /**
         * Actualiza las estadísticas mostradas en el dashboard.
         * 
         * Recalcula y actualiza:
         * - Total de productos en el sistema
         * - Stock total acumulado
         * - Cantidad de alertas activas
         * - Movimientos registrados hoy
         * 
         * Si ocurre un error, lo registra sin interrumpir la operación.
         */
        private void actualizarDashboard() {
            try {
                List<Producto> productos = productoServicio.listarProductos();
                lblTotalProductos.setText(String.valueOf(productos.size()));

                int stockTotal = productos.stream().mapToInt(Producto::getStock).sum();
                lblStockTotal.setText(String.format("%,d", stockTotal));

                List<Producto> alertas = inventarioServicio.obtenerProductosStockBajo();
                lblCantidadAlertas.setText(String.valueOf(alertas.size()));
                lblCantidadAlertasBadge.setText(String.valueOf(alertas.size()));

                long movimientosHoy = contarMovimientosHoy();
                lblMovimientosHoy.setText(String.valueOf(movimientosHoy));

            } catch (Exception e) {
                System.err.println("Error actualizando dashboard: " + e.getMessage());
            }
        }

        /**
         * Cuenta la cantidad de movimientos registrados en el día actual.
         * 
         * @return Número de movimientos desde las 00:00 del día actual
         */
        private long contarMovimientosHoy() {
            try {
                LocalDateTime hoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
                List<MovimientoInventario> movimientos = inventarioServicio.listarMovimientos();
                return movimientos.stream()
                    .filter(m -> m.getFecha().isAfter(hoy))
                    .count();
            } catch (Exception e) {
                return 0;
            }
        }

        /**
         * Configura la tabla de alertas de productos con stock bajo.
         * 
         * Establece:
         * - Vinculación de datos con PropertyValueFactory
         * - Estilos personalizados para estados (SIN STOCK, CRÍTICO, BAJO)
         * - Columna de acción con botón de reabastecimiento rápido
         * - Codificación de colores según criticidad
         */
        private void configurarTablaAlertas() {
            colAlertaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            colAlertaProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colAlertaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            colAlertaMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
            colAlertaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

            // Estilo personalizado para el estado
            colAlertaEstado.setCellFactory(col -> new TableCell<ProductoStockItem, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER);
                        String style = "-fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6;";

                        switch (item) {
                            case "SIN STOCK":
                                setStyle(style + "-fx-background-color: #fee; -fx-text-fill: #c62828;");
                                break;
                            case "CRÍTICO":
                                setStyle(style + "-fx-background-color: #ffe0b2; -fx-text-fill: #e65100;");
                                break;
                            default:
                                setStyle(style + "-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                        }
                    }
                }
            });

            // Columna de acción con botón
            colAlertaAccion.setCellFactory(col -> new TableCell<ProductoStockItem, Void>() {
                private final Button btnReabastecer = new Button("Reabastecer");

                {
                    btnReabastecer.setStyle(
                        "-fx-background-color: #2e7d32; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 11; " +
                        "-fx-font-weight: 600; " +
                        "-fx-padding: 6 12; " +
                        "-fx-background-radius: 6; " +
                        "-fx-cursor: hand;"
                    );

                    btnReabastecer.setOnAction(e -> {
                        ProductoStockItem item = getTableView().getItems().get(getIndex());
                        reabastecerRapido(item);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btnReabastecer);
                    setAlignment(Pos.CENTER);
                }
            });

            tblProductosStockBajo.setItems(productosStockBajo);
        }

        /**
         * Configura la tabla de historial de movimientos.
         * 
         * Establece:
         * - Vinculación de datos con PropertyValueFactory
         * - Estilos personalizados para tipos de movimiento (ENTRADA, SALIDA, AJUSTE)
         * - Código de colores para cantidades (verde para entrada, rojo para salida)
         * - Formateo de fechas y números
         */
        private void configurarTablaMovimientos() {
            colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
            colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
            colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadConSigno"));
            colStockAnterior.setCellValueFactory(new PropertyValueFactory<>("stockAnterior"));
            colStockNuevo.setCellValueFactory(new PropertyValueFactory<>("stockNuevo"));
            colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

            // Estilo para el tipo
            colTipo.setCellFactory(col -> new TableCell<MovimientoItem, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER);
                        String baseStyle = "-fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6;";

                        switch (item) {
                            case "ENTRADA":
                                setStyle(baseStyle + "-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32;");
                                break;
                            case "SALIDA":
                                setStyle(baseStyle + "-fx-background-color: #ffebee; -fx-text-fill: #c62828;");
                                break;
                            case "AJUSTE":
                                setStyle(baseStyle + "-fx-background-color: #fff3e0; -fx-text-fill: #e65100;");
                                break;
                        }
                    }
                }
            });

            // Estilo para la cantidad
            colCantidad.setCellFactory(col -> new TableCell<MovimientoItem, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER);
                        if (item.startsWith("+")) {
                            setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 13;");
                        } else if (item.startsWith("-")) {
                            setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold; -fx-font-size: 13;");
                        } else {
                            setStyle("-fx-font-weight: 600; -fx-font-size: 13;");
                        }
                    }
                }
            });

            tblMovimientos.setItems(movimientosFiltrados);
        }

        /**
         * Configura los ComboBox con sus opciones y convertidores.
         * 
         * Inicializa:
         * - Tipos de movimiento (ENTRADA, AJUSTE)
         * - Productos con formato personalizado
         * - Filtros de tipo de movimiento
         * - Filtros de período de tiempo
         */
        private void configurarComboBoxes() {
            cmbTipoMovimiento.setItems(FXCollections.observableArrayList("ENTRADA", "AJUSTE"));

            cmbProducto.setConverter(new StringConverter<Producto>() {
                @Override
                public String toString(Producto producto) {
                    if (producto == null) return null;
                    return String.format("%s - %s (Stock: %d)", 
                        producto.getCodigo(), producto.getNombre(), producto.getStock());
                }

                @Override
                public Producto fromString(String string) {
                    return null;
                }
            });

            cmbFiltroTipo.setItems(FXCollections.observableArrayList("Todos", "ENTRADA", "SALIDA", "AJUSTE"));
            cmbFiltroTipo.setValue("Todos");

            cmbFiltroPeriodo.setItems(FXCollections.observableArrayList(
                "Todos", "Hoy", "Esta Semana", "Este Mes", "Últimos 3 Meses"
            ));
            cmbFiltroPeriodo.setValue("Todos");
        }

        /**
         * Configura los listeners para eventos de cambio en los controles.
         * 
         * Establece:
         * - Actualización de vista previa al cambiar cantidad, producto o tipo
         * - Búsqueda en tiempo real de movimientos
         * - Filtrado automático al cambiar los filtros
         */
        private void configurarEventos() {
            cmbProducto.setOnAction(e -> actualizarVistaPrevia());
            txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaPrevia());
            cmbTipoMovimiento.setOnAction(e -> actualizarVistaPrevia());

            // Búsqueda en tiempo real
            txtBuscarProducto.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && newVal.length() > 2) {
                    buscarMovimientos();
                } else if (newVal == null || newVal.isEmpty()) {
                    movimientosFiltrados.setAll(movimientoItems);
                }
            });
        }

        /**
         * Carga todos los datos iniciales necesarios para la interfaz.
         * 
         * Ejecuta en orden:
         * - Carga de productos
         * - Carga de alertas
         * - Carga de movimientos
         * - Actualización del dashboard
         */
        private void cargarDatosIniciales() {
            cargarProductos();
            cargarProductosStockBajo();
            cargarMovimientos();
            actualizarDashboard();
        }

        /**
         * Carga la lista de productos desde la base de datos.
         * 
         * Obtiene todos los productos y los coloca en el ComboBox para
         * permitir su selección al registrar movimientos.
         */
        private void cargarProductos() {
            try {
                List<Producto> productos = productoServicio.listarProductos();
                cmbProducto.setItems(FXCollections.observableArrayList(productos));
            } catch (Exception e) {
                mostrarError("Error al cargar productos: " + e.getMessage());
            }
        }

        /**
         * Carga productos con stock bajo o crítico.
         * 
         * Obtiene la lista de productos que cumplen criterios de alerta
         * (stock bajo, crítico o sin stock) y los muestra en la tabla
         * de alertas.
         */
        private void cargarProductosStockBajo() {
            try {
                productosStockBajo.clear();
                List<Producto> productos = inventarioServicio.obtenerProductosStockBajo();

                for (Producto p : productos) {
                    productosStockBajo.add(new ProductoStockItem(p));
                }

            } catch (Exception e) {
                mostrarError("Error al cargar alertas: " + e.getMessage());
            }
        }

        /**
         * Carga el historial de movimientos de inventario.
         * 
         * Obtiene todos los movimientos, los ordena por fecha descendente
         * (más recientes primero) y los muestra en la tabla del historial.
         */
        private void cargarMovimientos() {
            try {
                movimientoItems.clear();
                List<MovimientoInventario> movimientos = inventarioServicio.listarMovimientos();

                movimientos.sort((m1, m2) -> m2.getFecha().compareTo(m1.getFecha()));

                for (MovimientoInventario m : movimientos) {
                    movimientoItems.add(new MovimientoItem(m));
                }

                movimientosFiltrados.setAll(movimientoItems);
                lblTotalMovimientos.setText(String.valueOf(movimientos.size()));

            } catch (Exception e) {
                mostrarError("Error al cargar movimientos: " + e.getMessage());
            }
        }

        /**
         * Actualiza la vista previa del nuevo stock calculado.
         * 
         * Calcula dinámicamente cómo cambiaría el stock según:
         * - Producto seleccionado
         * - Tipo de movimiento (ENTRADA, AJUSTE)
         * - Cantidad ingresada
         * 
         * Muestra alertas visuales si el nuevo stock es negativo o menor
         * al mínimo recomendado.
         */
        private void actualizarVistaPrevia() {
            Producto producto = cmbProducto.getValue();
            String tipo = cmbTipoMovimiento.getValue();

            if (producto == null) {
                lblStockActualNum.setText("--");
                lblStockNuevoNum.setText("--");
                lblStockNuevoNum.setStyle("-fx-text-fill: #64748b; -fx-font-size: 20; -fx-font-weight: bold;");
                return;
            }

            int stockActual = producto.getStock();
            lblStockActualNum.setText(String.format("%,d", stockActual));

            try {
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                int nuevoStock = stockActual;

                if ("ENTRADA".equals(tipo)) {
                    nuevoStock = stockActual + cantidad;
                } else if ("AJUSTE".equals(tipo)) {
                    nuevoStock = stockActual + cantidad;
                }

                lblStockNuevoNum.setText(String.format("%,d", nuevoStock));

                if (nuevoStock < 0) {
                    lblStockNuevoNum.setStyle("-fx-text-fill: #c62828; -fx-font-size: 20; -fx-font-weight: bold;");
                } else if (nuevoStock <= producto.getStockMinimo()) {
                    lblStockNuevoNum.setStyle("-fx-text-fill: #f57c00; -fx-font-size: 20; -fx-font-weight: bold;");
                } else {
                    lblStockNuevoNum.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 20; -fx-font-weight: bold;");
                }

            } catch (NumberFormatException e) {
                lblStockNuevoNum.setText("--");
                lblStockNuevoNum.setStyle("-fx-text-fill: #64748b; -fx-font-size: 20; -fx-font-weight: bold;");
            }
        }

        /**
         * Registra un nuevo movimiento de inventario.
         * 
         * Valida que todos los campos requeridos estén completos:
         * - Tipo de movimiento
         * - Producto
         * - Cantidad (número válido y diferente de 0)
         * - Motivo
         * 
         * Si la validación es exitosa, persiste el movimiento, muestra confirmación
         * y refresca todos los datos. En caso contrario, muestra el error específico.
         * 
         * @throws Exception Si ocurre un error al guardar en la base de datos
         */
        @FXML
        private void registrarMovimiento() {
            if (cmbTipoMovimiento.getValue() == null) {
                mostrarAdvertencia("Debe seleccionar un tipo de movimiento");
                return;
            }

            Producto producto = cmbProducto.getValue();
            if (producto == null) {
                mostrarAdvertencia("Debe seleccionar un producto");
                return;
            }

            int cantidad;
            try {
                cantidad = Integer.parseInt(txtCantidad.getText().trim());
                if (cantidad == 0) {
                    mostrarAdvertencia("La cantidad debe ser diferente de 0");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAdvertencia("La cantidad debe ser un número válido");
                return;
            }

            String motivo = txtMotivo.getText().trim();
            if (motivo.isEmpty()) {
                mostrarAdvertencia("Debe ingresar un motivo");
                return;
            }

            try {
                MovimientoInventario movimiento = inventarioServicio.registrarMovimiento(
                    producto, cmbTipoMovimiento.getValue(), cantidad, motivo
                );

                mostrarExito(String.format(
                    "Movimiento registrado exitosamente\n\n" +
                    "Código: %s\nProducto: %s\nCantidad: %s\nNuevo Stock: %d",
                    movimiento.getCodigo(),
                    movimiento.getNombreProducto(),
                    movimiento.getSignoCantidad(),
                    movimiento.getStockNuevo()
                ));

                refrescarTodo();
                limpiarFormulario();

            } catch (Exception e) {
                mostrarError("Error al registrar movimiento: " + e.getMessage());
            }
        }

        /**
         * Limpia todos los campos del formulario de registro.
         * 
         * Reinicia:
         * - ComboBox de tipo y producto
         * - Campos de cantidad y motivo
         * - Labels de vista previa
         */
        @FXML
        private void limpiarFormulario() {
            cmbTipoMovimiento.setValue(null);
            cmbProducto.setValue(null);
            txtCantidad.clear();
            txtMotivo.clear();
            lblStockActualNum.setText("--");
            lblStockNuevoNum.setText("--");
            lblStockNuevoNum.setStyle("-fx-text-fill: #64748b; -fx-font-size: 20; -fx-font-weight: bold;");
        }

        /**
         * Busca movimientos según criterios de búsqueda y filtros.
         * 
         * Aplica filtros de:
         * - Búsqueda por nombre o código de producto
         * - Tipo de movimiento (ENTRADA, SALIDA, AJUSTE)
         * - Período de tiempo (Hoy, Esta Semana, Este Mes, Últimos 3 Meses)
         * 
         * Los resultados se muestran en la tabla del historial.
         */
        @FXML
        private void buscarMovimientos() {
            String buscar = txtBuscarProducto.getText().trim().toLowerCase();
            String filtroTipo = cmbFiltroTipo.getValue();
            String filtroPeriodo = cmbFiltroPeriodo.getValue();

            LocalDateTime fechaLimite = calcularFechaLimite(filtroPeriodo);

            List<MovimientoItem> resultados = movimientoItems.stream()
                .filter(item -> {
                    boolean coincideProducto = buscar.isEmpty() || 
                        item.getNombreProducto().toLowerCase().contains(buscar) ||
                        item.getCodigoProducto().toLowerCase().contains(buscar);

                    boolean coincideTipo = "Todos".equals(filtroTipo) || 
                        item.getTipo().equals(filtroTipo);

                    boolean coincidePeriodo = fechaLimite == null ||
                        item.getMovimiento().getFecha().isAfter(fechaLimite);

                    return coincideProducto && coincideTipo && coincidePeriodo;
                })
                .collect(Collectors.toList());

            movimientosFiltrados.setAll(resultados);
            lblTotalMovimientos.setText(String.valueOf(resultados.size()));
        }

        /**
         * Calcula la fecha límite según el período seleccionado.
         * 
         * @param periodo El período seleccionado (Hoy, Esta Semana, Este Mes, Últimos 3 Meses)
         * @return LocalDateTime representando la fecha límite, o null si no hay límite
         */
        private LocalDateTime calcularFechaLimite(String periodo) {
            if (periodo == null || "Todos".equals(periodo)) return null;

            LocalDateTime ahora = LocalDateTime.now();
            switch (periodo) {
                case "Hoy":
                    return ahora.withHour(0).withMinute(0).withSecond(0);
                case "Esta Semana":
                    return ahora.minusWeeks(1);
                case "Este Mes":
                    return ahora.minusMonths(1);
                case "Últimos 3 Meses":
                    return ahora.minusMonths(3);
                default:
                    return null;
            }
        }

        /**
         * Actualiza el historial limpiando todos los filtros y búsquedas.
         * 
         * Reinicia los filtros a sus valores por defecto y recarga
         * todos los movimientos.
         */
        @FXML
        private void actualizarHistorial() {
            txtBuscarProducto.clear();
            cmbFiltroTipo.setValue("Todos");
            cmbFiltroPeriodo.setValue("Todos");
            refrescarTodo();
            mostrarInformacion("Datos actualizados correctamente");
        }

        /**
         * Prepara el formulario para reabastecimiento rápido desde una alerta.
         * 
         * Completa automáticamente todos los campos del formulario de registro
         * para facilitar el reabastecimiento de productos con stock crítico:
         * 
         * Configura:
         * - Tipo de movimiento: ENTRADA
         * - Producto: El producto de la alerta seleccionada
         * - Cantidad: Cantidad sugerida calculada como (stockMínimo * 2 - stock actual)
         * - Motivo: "Reabastecimiento de stock crítico"
         * 
         * El usuario solo necesita revisar los datos y presionar Registrar para
         * completar el movimiento. Esta función optimiza el proceso de reabastecimiento
         * de productos en alerta.
         * 
         * @param item El ProductoStockItem con la alerta de bajo stock a reabastecer
         */
        private void reabastecerRapido(ProductoStockItem item) {
            cmbTipoMovimiento.setValue("ENTRADA");

            // Buscar el producto en la lista
            for (Producto p : cmbProducto.getItems()) {
                if (p.getCodigo().equals(item.getCodigo())) {
                    cmbProducto.setValue(p);
                    break;
                }
            }

            int cantidadSugerida = Math.max(item.getStockMinimo() * 2 - item.getStock(), 1);
            txtCantidad.setText(String.valueOf(cantidadSugerida));
            txtMotivo.setText("Reabastecimiento de stock crítico");

            mostrarInformacion("Formulario prellenado. Verifique los datos y presione Registrar.");
        }

        /**
         * Genera un reporte completo de inventario en formato PDF.
         * 
         * Permite al usuario seleccionar la ubicación de guardado del archivo.
         * El proceso se ejecuta en un hilo separado para no bloquear la interfaz.
         * 
         * El reporte incluye:
         * - Listado completo de productos
         * - Productos con stock bajo/crítico
         * - Historial de movimientos
         * - Estadísticas y resúmenes
         * 
         * Tras la generación, muestra una notificación con opción de abrir el archivo.
         */
        @FXML
        private void generarReporteInventario() {
            try {
                // FileChooser para seleccionar ubicación
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Guardar Reporte de Inventario");
                fileChooser.setInitialFileName("Reporte_Inventario_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf")
                );

                File file = fileChooser.showSaveDialog(txtCantidad.getScene().getWindow());

                if (file != null) {
                    // Mostrar indicador de carga
                    Alert alertaCargando = new Alert(Alert.AlertType.INFORMATION);
                    alertaCargando.setTitle("Generando Reporte");
                    alertaCargando.setHeaderText("Por favor espere...");
                    alertaCargando.setContentText("Generando reporte de inventario en PDF");
                    alertaCargando.show();

                    // Generar reporte en hilo separado
                    new Thread(() -> {
                        try {
                            // Obtener datos
                            List<Producto> productos = productoServicio.listarProductos();
                            List<Producto> productosStockBajo = inventarioServicio.obtenerProductosStockBajo();
                            List<MovimientoInventario> movimientos = inventarioServicio.listarMovimientos();

                            // Generar PDF
                            GeneradorReporteInventario generador = new GeneradorReporteInventario();
                            generador.generarReporteCompleto(
                                file.getAbsolutePath(),
                                productos,
                                productosStockBajo,
                                movimientos
                            );

                            // Cerrar alerta de carga y mostrar éxito
                            javafx.application.Platform.runLater(() -> {
                                alertaCargando.close();

                                Alert alertaExito = new Alert(Alert.AlertType.INFORMATION);
                                alertaExito.setTitle("✓ Reporte Generado");
                                alertaExito.setHeaderText("Reporte generado exitosamente");
                                alertaExito.setContentText(
                                    "El reporte se ha guardado en:\n" + file.getAbsolutePath() + 
                                    "\n\n¿Desea abrir el archivo?"
                                );

                                alertaExito.getButtonTypes().clear();
                                alertaExito.getButtonTypes().addAll(
                                    new ButtonType("Abrir", ButtonBar.ButtonData.OK_DONE),
                                    new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE)
                                );

                                alertaExito.showAndWait().ifPresent(response -> {
                                    if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                                        try {
                                            java.awt.Desktop.getDesktop().open(file);
                                        } catch (Exception e) {
                                            mostrarError("No se pudo abrir el archivo: " + e.getMessage());
                                        }
                                    }
                                });
                            });

                        } catch (Exception e) {
                            javafx.application.Platform.runLater(() -> {
                                alertaCargando.close();
                                mostrarError("Error al generar el reporte: " + e.getMessage());
                            });
                            e.printStackTrace();
                        }
                    }).start();
                }

            } catch (Exception e) {
                mostrarError("Error al iniciar generación de reporte: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Exporta la lista de alertas de stock bajo a un archivo CSV.
         * 
         * Crea un archivo con formato de valores separados por comas conteniendo:
         * - Código del producto
         * - Nombre del producto
         * - Stock actual
         * - Stock mínimo
         * - Estado (SIN STOCK, CRÍTICO, BAJO)
         * - Diferencia con el mínimo
         * 
         * Tras la exportación, ofrece la opción de abrir el archivo.
         */
        @FXML
        private void exportarAlertas() {
            if (productosStockBajo.isEmpty()) {
                mostrarInformacion("No hay alertas para exportar");
                return;
            }

            try {
                // FileChooser para seleccionar ubicación
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Exportar Alertas de Stock");
                fileChooser.setInitialFileName("Alertas_Stock_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv")
                );

                File file = fileChooser.showSaveDialog(tblProductosStockBajo.getScene().getWindow());

                if (file != null) {
                    try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                        // Encabezados
                        writer.println("Código,Producto,Stock Actual,Stock Mínimo,Estado,Diferencia");

                        // Datos
                        for (ProductoStockItem item : productosStockBajo) {
                            int diferencia = item.getStockMinimo() - item.getStock();
                            writer.printf("%s,%s,%d,%d,%s,%d%n",
                                item.getCodigo(),
                                item.getNombre().replace(",", ";"), // Evitar problemas con comas
                                item.getStock(),
                                item.getStockMinimo(),
                                item.getEstado(),
                                diferencia
                            );
                        }

                        mostrarExito(
                            "Alertas exportadas exitosamente\n\n" +
                            "Archivo: " + file.getName() + "\n" +
                            "Registros: " + productosStockBajo.size() + "\n\n" +
                            "¿Desea abrir el archivo?"
                        );

                        // Preguntar si desea abrir
                        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmacion.setTitle("Abrir archivo");
                        confirmacion.setHeaderText("¿Desea abrir el archivo exportado?");
                        confirmacion.setContentText(file.getAbsolutePath());

                        confirmacion.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    java.awt.Desktop.getDesktop().open(file);
                                } catch (Exception e) {
                                    mostrarError("No se pudo abrir el archivo: " + e.getMessage());
                                }
                            }
                        });

                    }
                }

            } catch (Exception e) {
                mostrarError("Error al exportar alertas: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Muestra un reporte detallado de alertas críticas con múltiples opciones de exportación.
         * 
         * Genera un reporte formateado que incluye:
         * - Resumen de alertas (total, sin stock, críticas, bajos)
         * - Listado de productos sin stock
         * - Listado de productos con stock crítico
         * - Indicadores visuales con emojis
         * 
         * Ofrece opciones para:
         * - Copiar al portapapeles
         * - Guardar en archivo de texto
         * - Solo visualizar
         */
        @FXML
        private void notificarAlertas() {
            if (productosStockBajo.isEmpty()) {
                mostrarInformacion("No hay alertas para notificar");
                return;
            }

            try {
                // Contar productos por nivel de criticidad
                long sinStock = productosStockBajo.stream()
                    .filter(p -> p.getStock() == 0)
                    .count();

                long criticos = productosStockBajo.stream()
                    .filter(p -> p.getStock() > 0 && p.getStock() < p.getStockMinimo())
                    .count();

                long bajos = productosStockBajo.stream()
                    .filter(p -> p.getStock() >= p.getStockMinimo())
                    .count();

                // Crear reporte de alertas
                StringBuilder reporte = new StringBuilder();
                reporte.append("═══════════════════════════════════════\n");
                reporte.append("   REPORTE DE ALERTAS CRÍTICAS\n");
                reporte.append("═══════════════════════════════════════\n\n");

                reporte.append(String.format("RESUMEN:\n"));
                reporte.append(String.format("   • Total de alertas: %d\n", productosStockBajo.size()));
                reporte.append(String.format("   • Sin stock: %d \n", sinStock));
                reporte.append(String.format("   • Stock crítico: %d ️\n", criticos));
                reporte.append(String.format("   • Stock bajo: %d \n\n", bajos));

                reporte.append("═══════════════════════════════════════\n");
                reporte.append("PRODUCTOS CON STOCK CRÍTICO:\n");
                reporte.append("═══════════════════════════════════════\n\n");

                // Listar productos sin stock primero
                if (sinStock > 0) {
                    reporte.append("SIN STOCK:\n");
                    reporte.append("─────────────────────────────────────\n");
                    productosStockBajo.stream()
                        .filter(p -> p.getStock() == 0)
                        .forEach(p -> reporte.append(String.format(
                            "• %s - %s\n  Stock: %d | Mínimo: %d\n\n",
                            p.getCodigo(), p.getNombre(),
                            p.getStock(), p.getStockMinimo()
                        )));
                }

                // Luego críticos
                if (criticos > 0) {
                    reporte.append("\n⚠️ STOCK CRÍTICO:\n");
                    reporte.append("─────────────────────────────────────\n");
                    productosStockBajo.stream()
                        .filter(p -> p.getStock() > 0 && p.getStock() < p.getStockMinimo())
                        .forEach(p -> reporte.append(String.format(
                            "• %s - %s\n  Stock: %d | Mínimo: %d | Diferencia: %d\n\n",
                            p.getCodigo(), p.getNombre(),
                            p.getStock(), p.getStockMinimo(),
                            p.getStockMinimo() - p.getStock()
                        )));
                }

                reporte.append("\n═══════════════════════════════════════\n");
                reporte.append("Generado: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                reporte.append("\n═══════════════════════════════════════\n");

                // Mostrar reporte en ventana con opción de copiar o guardar
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle(" Notificación de Alertas");
                alerta.setHeaderText(String.format(
                    "Se encontraron %d productos con stock crítico", 
                    productosStockBajo.size()
                ));

                TextArea textArea = new TextArea(reporte.toString());
                textArea.setEditable(false);
                textArea.setWrapText(false);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                textArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12;");

                alerta.getDialogPane().setContent(textArea);
                alerta.getDialogPane().setPrefSize(600, 500);

                // Botones personalizados
                ButtonType btnCopiar = new ButtonType("Copiar al Portapapeles");
                ButtonType btnGuardar = new ButtonType("Guardar en TXT");
                ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);

                alerta.getButtonTypes().setAll(btnCopiar, btnGuardar, btnCerrar);

                alerta.showAndWait().ifPresent(response -> {
                    if (response == btnCopiar) {
                        // Copiar al portapapeles
                        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                        content.putString(reporte.toString());
                        clipboard.setContent(content);
                        mostrarInformacion("Reporte copiado al portapapeles");

                    } else if (response == btnGuardar) {
                        // Guardar en archivo
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Guardar Reporte de Alertas");
                        fileChooser.setInitialFileName("Alertas_" + 
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
                        fileChooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")
                        );

                        File file = fileChooser.showSaveDialog(tblProductosStockBajo.getScene().getWindow());

                        if (file != null) {
                            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                                writer.print(reporte.toString());
                                mostrarExito("Reporte guardado exitosamente en:\n" + file.getAbsolutePath());
                            } catch (Exception e) {
                                mostrarError("Error al guardar el reporte: " + e.getMessage());
                            }
                        }
                    }
                });

            } catch (Exception e) {
                mostrarError("Error al generar notificación de alertas: " + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * Refresca todos los datos de las tablas y dashboard.
         * 
         * Ejecuta en orden:
         * - Recarga de movimientos
         * - Recarga de alertas
         * - Recarga de productos
         * - Actualización del dashboard
         */
        private void refrescarTodo() {
            cargarMovimientos();
            cargarProductosStockBajo();
            cargarProductos();
            actualizarDashboard();
        }


        /**
         * Muestra un diálogo de error al usuario.
         * 
         * @param mensaje El mensaje de error a mostrar
         */
        private void mostrarError(String mensaje) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }

        /**
         * Muestra un diálogo de advertencia al usuario.
         * 
         * @param mensaje El mensaje de advertencia a mostrar
         */
        private void mostrarAdvertencia(String mensaje) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }

        /**
         * Muestra un diálogo informativo al usuario.
         * 
         * @param mensaje El mensaje informativo a mostrar
         */
        private void mostrarInformacion(String mensaje) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }

        /**
         * Muestra un diálogo de éxito con confirmación de operación realizada.
         * 
         * @param mensaje El mensaje de éxito a mostrar
         */
        private void mostrarExito(String mensaje) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("✓ Éxito");
            alert.setHeaderText("Operación Exitosa");
            alert.setContentText(mensaje);
            alert.showAndWait();
        }


        /**
         * Clase interna que representa un producto con su información de stock.
         * 
         * Utilizada para mostrar información de alertas de stock en la tabla.
         * Encapsula la lógica de determinación del estado (SIN STOCK, CRÍTICO, BAJO).
         */
        public static class ProductoStockItem {
            private final SimpleStringProperty codigo;
            private final SimpleStringProperty nombre;
            private final SimpleIntegerProperty stock;
            private final SimpleIntegerProperty stockMinimo;
            private final SimpleStringProperty estado;

            /**
             * Constructor que crea un ProductoStockItem a partir de un Producto.
             * 
             * Determina automáticamente el estado según el stock:
             * - SIN STOCK: Si stock == 0
             * - CRÍTICO: Si stock < stockMinimo
             * - BAJO: En los demás casos
             * 
             * @param producto El producto a convertir
             */
            public ProductoStockItem(Producto producto) {
                this.codigo = new SimpleStringProperty(producto.getCodigo());
                this.nombre = new SimpleStringProperty(producto.getNombre());
                this.stock = new SimpleIntegerProperty(producto.getStock());
                this.stockMinimo = new SimpleIntegerProperty(producto.getStockMinimo());

                String estadoTexto;
                if (producto.getStock() == 0) {
                    estadoTexto = "SIN STOCK";
                } else if (producto.getStock() < producto.getStockMinimo()) {
                    estadoTexto = "CRÍTICO";
                } else {
                    estadoTexto = "BAJO";
                }
                this.estado = new SimpleStringProperty(estadoTexto);
            }

            /**
             * Obtiene el código del producto.
             * @return Código del producto
             */
            public String getCodigo() { return codigo.get(); }

            /**
             * Obtiene el nombre del producto.
             * @return Nombre del producto
             */
            public String getNombre() { return nombre.get(); }

            /**
             * Obtiene el stock actual del producto.
             * @return Stock actual
             */
            public int getStock() { return stock.get(); }

            /**
             * Obtiene el stock mínimo recomendado.
             * @return Stock mínimo
             */
            public int getStockMinimo() { return stockMinimo.get(); }

            /**
             * Obtiene el estado del producto.
             * @return Estado (SIN STOCK, CRÍTICO o BAJO)
             */
            public String getEstado() { return estado.get(); }
        }

        /**
         * Clase interna que representa un movimiento de inventario.
         * 
         * Utilizada para mostrar información de movimientos en la tabla del historial.
         * Contiene tanto los datos del movimiento como información formateada para
         * presentación en la interfaz.
         */
        public static class MovimientoItem {
            private final MovimientoInventario movimiento;
            private final SimpleStringProperty codigo;
            private final SimpleStringProperty fecha;
            private final SimpleStringProperty tipo;
            private final SimpleStringProperty nombreProducto;
            private final SimpleStringProperty codigoProducto;
            private final SimpleStringProperty cantidadConSigno;
            private final SimpleIntegerProperty stockAnterior;
            private final SimpleIntegerProperty stockNuevo;
            private final SimpleStringProperty motivo;

            /**
             * Constructor que crea un MovimientoItem a partir de un MovimientoInventario.
             * 
             * Prepara todos los datos formateados para su presentación en la interfaz:
             * - Fecha en formato legible
             * - Cantidad con signo (+/-)
             * - Información del producto
             * 
             * @param movimiento El movimiento de inventario a convertir
             */
            public MovimientoItem(MovimientoInventario movimiento) {
                this.movimiento = movimiento;
                this.codigo = new SimpleStringProperty(movimiento.getCodigo());
                this.fecha = new SimpleStringProperty(movimiento.getFechaFormateada());
                this.tipo = new SimpleStringProperty(movimiento.getTipoMovimiento());
                this.nombreProducto = new SimpleStringProperty(movimiento.getNombreProducto());
                this.codigoProducto = new SimpleStringProperty(movimiento.getCodigoProducto());
                this.cantidadConSigno = new SimpleStringProperty(movimiento.getSignoCantidad());
                this.stockAnterior = new SimpleIntegerProperty(movimiento.getStockAnterior());
                this.stockNuevo = new SimpleIntegerProperty(movimiento.getStockNuevo());
                this.motivo = new SimpleStringProperty(movimiento.getMotivo() != null ? movimiento.getMotivo() : "");
            }

            /**
             * Obtiene el código único del movimiento.
             * @return Código del movimiento
             */
            public String getCodigo() { return codigo.get(); }

            /**
             * Obtiene la fecha del movimiento formateada.
             * @return Fecha en formato legible
             */
            public String getFecha() { return fecha.get(); }

            /**
             * Obtiene el tipo de movimiento.
             * @return Tipo (ENTRADA, SALIDA, AJUSTE)
             */
            public String getTipo() { return tipo.get(); }

            /**
             * Obtiene el nombre del producto.
             * @return Nombre del producto
             */
            public String getNombreProducto() { return nombreProducto.get(); }

            /**
             * Obtiene el código del producto.
             * @return Código del producto
             */
            public String getCodigoProducto() { return codigoProducto.get(); }

            /**
             * Obtiene la cantidad con signo (+/- según tipo).
             * @return Cantidad con signo
             */
            public String getCantidadConSigno() { return cantidadConSigno.get(); }

            /**
             * Obtiene el stock anterior al movimiento.
             * @return Stock anterior
             */
            public int getStockAnterior() { return stockAnterior.get(); }

            /**
             * Obtiene el stock posterior al movimiento.
             * @return Stock nuevo
             */
            public int getStockNuevo() { return stockNuevo.get(); }

            /**
             * Obtiene el motivo del movimiento.
             * @return Motivo (puede estar vacío)
             */
            public String getMotivo() { return motivo.get(); }

            /**
             * Obtiene el objeto MovimientoInventario original.
             * @return MovimientoInventario
             */
            public MovimientoInventario getMovimiento() { return movimiento; }
        }
    }