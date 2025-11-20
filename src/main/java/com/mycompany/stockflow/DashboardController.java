    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
    package com.mycompany.stockflow;

    import com.mycompany.stockflow.Modelo.Usuario;
    import com.mycompany.stockflow.utils.SesionUsuario;
    import java.io.IOException;
    import java.net.URL;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.Locale;
    import java.util.ResourceBundle;
    import javafx.animation.*;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.Node;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.ButtonType;
    import javafx.scene.control.Label;
    import javafx.scene.input.KeyCode;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.layout.VBox;
    import javafx.stage.Stage;
    import javafx.util.Duration;

    /**
     * Controlador principal del Dashboard de StockFlow.
     * <p>
     * Gestiona la navegación entre las diferentes vistas del sistema
     * e implementa control de acceso basado en roles de usuario.
     * </p>
     * 
     * <h2>Control de Acceso por Roles:</h2>
     * <ul>
     *   <li><b>ADMIN:</b> Acceso total incluyendo gestión de usuarios</li>
     *   <li><b>DUEÑO:</b> Acceso completo excepto gestión de usuarios</li>
     *   <li><b>CAJERO:</b> Acceso a ventas, productos y clientes</li>
     * </ul>
     * 
     * <h2>Características:</h2>
     * <ul>
     *   <li>Animaciones suaves en transiciones de vistas</li>
     *   <li>Efectos hover interactivos en menú y tarjetas</li>
     *   <li>Validación de permisos antes de cargar vistas</li>
     *   <li>Integración con sistema de sesión de usuario</li>
     * </ul>
     * 
     * @author StockFlow Team
     * @version 3.0
     * @since 1.0
     * 
     * @see SesionUsuario
     * @see Usuario
     * @see com.mycompany.stockflow.Modelo.Rol
     */
    public class DashboardController implements Initializable {

        @FXML private Button btnInicio;
        @FXML private Button btnVentas;
        @FXML private Button btnProductos;
        @FXML private Button btnClientes;
        @FXML private Button btnInventario;
        @FXML private Button btnFacturacion;
        @FXML private Button btnEstadisticas;
        @FXML private Button btnUsuarios;
        @FXML private Button btnCerrarSesion;
        @FXML private Button btnChatBotFlotante;

        @FXML private Label lblTituloSeccion;
        @FXML private Label lblUsuario;
        @FXML private Label lblFecha;

        @FXML private AnchorPane contenedorPrincipal;

        @FXML private VBox vistaInicio;
        @FXML private VBox cardProductos;
        @FXML private VBox cardVentas;
        @FXML private VBox cardInventario;
        @FXML private VBox btnNuevoCliente;
        @FXML private VBox btnNuevaFactura;
        @FXML private VBox btnVerReportes;


        private Button botonActivo;
        private InteligenciaNegocioController inteligenciaControllerActual;
        private ProductosController productosControllerActual;
        private SesionUsuario sesionUsuario;

        private static final String ESTILO_BOTON_ACTIVO = 
            "-fx-background-color: rgba(255, 255, 255, 0.2); -fx-background-radius: 10; " +
            "-fx-cursor: hand; -fx-text-fill: white; -fx-border-color: rgba(255, 255, 255, 0.3); " +
            "-fx-border-radius: 10; -fx-border-width: 1;";

        private static final String ESTILO_BOTON_INACTIVO = 
            "-fx-background-color: transparent; -fx-background-radius: 10; -fx-cursor: hand; " +
            "-fx-border-color: transparent; -fx-border-radius: 10;";

        private static final String ESTILO_BOTON_HOVER = 
            "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; " +
            "-fx-cursor: hand; -fx-text-fill: white; -fx-border-color: transparent; -fx-border-radius: 10;";

        private static final String ESTILO_BOTON_DESHABILITADO = 
            "-fx-background-color: transparent; -fx-background-radius: 10; " +
            "-fx-text-fill: rgba(255, 255, 255, 0.4); -fx-opacity: 0.5;";


        /**
         * Inicializa el controlador del Dashboard.
         * <p>
         * Configura la interfaz según el rol del usuario actual,
         * aplica animaciones iniciales y establece los permisos de acceso.
         * </p>
         * 
         * @param url Ubicación del FXML
         * @param rb ResourceBundle para internacionalización
         */
        @Override
        public void initialize(URL url, ResourceBundle rb) {
            // Obtener sesión actual
            sesionUsuario = SesionUsuario.getInstancia();

            // Verificar que hay sesión activa
            if (!sesionUsuario.haySesionActiva()) {
                System.err.println(" Error: No hay sesión activa en el Dashboard");
                mostrarError("Error de Sesión", "No hay un usuario activo. Regresando al login...");
                volverABienvenida();
                return;
            }

            // Configurar interfaz según usuario
            configurarInterfazPorUsuario();

            // Configurar efectos visuales
            configurarEfectosHover();
            aplicarAnimacionesIniciales();
            configurarEfectosTarjetas();
            configurarBotonFlotante();

            // Configurar eventos
            configurarClicksTarjetasPrincipales();
            configurarClicksAccionesRapidas();

            // Actualizar fecha
            actualizarFecha();

            // Establecer botón Inicio como activo
            establecerBotonActivo(btnInicio);

            System.out.println(" Dashboard inicializado para: " + sesionUsuario.getNombreUsuario());
            System.out.println("  Rol: " + sesionUsuario.getRolActual().getNombre());
        }


        /**
         * Configura la interfaz según el rol del usuario actual.
         * <p>
         * Oculta o deshabilita opciones según los permisos:
         * <ul>
         *   <li><b>Gestión de Usuarios:</b> Solo ADMIN</li>
         *   <li><b>Estadísticas/BI:</b> ADMIN y DUEÑO</li>
         *   <li><b>Otras opciones:</b> Todos los roles</li>
         * </ul>
         * </p>
         */
        private void configurarInterfazPorUsuario() {
            Usuario usuario = sesionUsuario.getUsuarioActual();

            // Actualizar label de usuario con nombre y rol
            lblUsuario.setText(
                usuario.getNombreCompleto() + " (" + 
                usuario.getRol().getNombre() + ")"
            );

            // ⭐ CONTROL DE ACCESO: Gestión de Usuarios (Solo ADMIN)
            if (!sesionUsuario.puedeGestionarUsuarios()) {
                btnUsuarios.setVisible(false);
                btnUsuarios.setManaged(false);
                System.out.println("Botón Usuarios ocultado - Requiere rol ADMIN");
            }

            // ⭐ CONTROL DE ACCESO: Estadísticas/BI (ADMIN y DUEÑO)
            if (!sesionUsuario.puedeVerEstadisticas()) {
                btnEstadisticas.setVisible(false);
                btnEstadisticas.setManaged(false);
                System.out.println("Botón Estadísticas ocultado - Requiere rol ADMIN o DUEÑO");
            }

            // Registrar permisos del usuario actual
            registrarPermisosUsuario();
        }

        /**
         * Registra en consola los permisos del usuario actual.
         * Útil para debugging y auditoría.
         */
        private void registrarPermisosUsuario() {
            System.out.println("\n═══════════════════════════════════════");
            System.out.println("  PERMISOS DEL USUARIO");
            System.out.println("═══════════════════════════════════════");
            System.out.println(" Usuario: " + sesionUsuario.getNombreUsuario());
            System.out.println("Rol: " + sesionUsuario.getRolActual().getNombre());
            System.out.println("───────────────────────────────────────");
            System.out.println(" Gestionar Usuarios: " + (sesionUsuario.puedeGestionarUsuarios() ? "SÍ" : "NO"));
            System.out.println(" Ver Estadísticas: " + (sesionUsuario.puedeVerEstadisticas() ? "SÍ" : "NO"));
            System.out.println("Ver BI: " + (sesionUsuario.puedeVerInteligenciaNegocio() ? "SÍ" : "NO"));
            System.out.println("═══════════════════════════════════════\n");
        }


        /**
         * Muestra la vista de gestión de usuarios.
         * <p>
         * <b>Requisito:</b> Solo accesible para usuarios con rol ADMIN.
         * Si el usuario no tiene permisos, se muestra un mensaje de error.
         * </p>
         */
        @FXML
        private void mostrarUsuarios() {
            // ⭐ VALIDAR PERMISOS ANTES DE CARGAR
            if (!sesionUsuario.puedeGestionarUsuarios()) {
                System.err.println("Acceso denegado: Usuario sin permisos para gestionar usuarios");
                mostrarAccesoDenegado(
                    "Solo los administradores pueden acceder a la gestión de usuarios"
                );
                return;
            }

            System.out.println(" Acceso permitido: Cargando gestión de usuarios");
            cargarVista("Usuarios.fxml", "Gestión de Usuarios", btnUsuarios);
        }

        /**
         * Muestra la vista de estadísticas e inteligencia de negocio.
         * <p>
         * <b>Requisito:</b> Solo accesible para ADMIN y DUEÑO.
         * </p>
         */
        @FXML
        private void mostrarEstadisticas() {
            // ⭐ VALIDAR PERMISOS
            if (!sesionUsuario.puedeVerEstadisticas()) {
                System.err.println(" Acceso denegado: Usuario sin permisos para ver estadísticas");
                mostrarAccesoDenegado(
                    "Esta sección requiere permisos de Administrador o Dueño"
                );
                return;
            }

            System.out.println("Acceso permitido: Cargando estadísticas");
            cargarVista("inteligenciaDeNegocio.fxml", "", btnEstadisticas);
        }

        // Resto de métodos de navegación (sin restricciones especiales)

        @FXML
        private void mostrarVentas() {
            cargarVista("Venta.fxml", "Registro de Ventas", btnVentas);
        }

        @FXML
        private void mostrarProductos() {
            cargarVista("Productos.fxml", "Gestión de Productos", btnProductos);
        }

        @FXML
        private void mostrarClientes() {
            cargarVista("clientes.fxml", "Gestión de Clientes", btnClientes);
        }

        @FXML
        private void mostrarInventario() {
            cargarVista("ControlInventario.fxml", "Control de Inventario", btnInventario);
        }

        @FXML
        private void mostrarFacturacion() {
            cargarVista("facturacion.fxml", "Facturación", btnFacturacion);
        }

        /**
         * Muestra la vista de inicio (Dashboard con tarjetas).
         */
        @FXML
        private void mostrarInicio() {
            contenedorPrincipal.getChildren().clear();
            animarCambioTexto(lblTituloSeccion, "Dashboard Principal");
            establecerBotonActivo(btnInicio);

            if (vistaInicio != null) {
                AnchorPane.setTopAnchor(vistaInicio, 0.0);
                AnchorPane.setBottomAnchor(vistaInicio, 0.0);
                AnchorPane.setLeftAnchor(vistaInicio, 0.0);
                AnchorPane.setRightAnchor(vistaInicio, 0.0);

                contenedorPrincipal.getChildren().add(vistaInicio);
                aplicarAnimacionFadeIn(vistaInicio);

                configurarClicksTarjetasPrincipales();
                configurarClicksAccionesRapidas();
                configurarEfectosTarjetas();

                System.out.println("Vista de inicio cargada");
            } else {
                System.err.println("Error: vistaInicio es null");
            }
        }

        /**
         * Carga una vista FXML en el contenedor principal con animación.
         * 
         * @param rutaFxml Ruta del archivo FXML a cargar
         * @param titulo Título de la sección
         * @param boton Botón del menú que se activó
         */
        private void cargarVista(String rutaFxml, String titulo, Button boton) {
            try {
                animarCambioTexto(lblTituloSeccion, titulo);
                establecerBotonActivo(boton);

                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
                Parent vista = loader.load();

                // Guardar referencias a controladores
                if (rutaFxml.contains("inteligenciaDeNegocio")) {
                    inteligenciaControllerActual = loader.getController();
                    System.out.println("InteligenciaNegocioController cargado");
                } else if (rutaFxml.contains("Productos")) {
                    productosControllerActual = loader.getController();
                    productosControllerActual.setDashboardController(this);
                    System.out.println(" ProductosController cargado");
                }

                // Animación de transición
                if (!contenedorPrincipal.getChildren().isEmpty()) {
                    Node contenidoActual = contenedorPrincipal.getChildren().get(0);

                    FadeTransition fadeOut = new FadeTransition(Duration.millis(200), contenidoActual);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);

                    fadeOut.setOnFinished(e -> {
                        contenedorPrincipal.getChildren().clear();

                        AnchorPane.setTopAnchor(vista, 0.0);
                        AnchorPane.setBottomAnchor(vista, 0.0);
                        AnchorPane.setLeftAnchor(vista, 0.0);
                        AnchorPane.setRightAnchor(vista, 0.0);

                        contenedorPrincipal.getChildren().add(vista);
                        aplicarAnimacionFadeIn(vista);
                    });

                    fadeOut.play();
                } else {
                    AnchorPane.setTopAnchor(vista, 0.0);
                    AnchorPane.setBottomAnchor(vista, 0.0);
                    AnchorPane.setLeftAnchor(vista, 0.0);
                    AnchorPane.setRightAnchor(vista, 0.0);

                    contenedorPrincipal.getChildren().add(vista);
                    aplicarAnimacionFadeIn(vista);
                }

                System.out.println("Vista cargada: " + titulo);

            } catch (IOException e) {
                System.err.println("Error al cargar la vista: " + rutaFxml);
                e.printStackTrace();
                mostrarError("Error al cargar vista", 
                    "No se pudo cargar la vista: " + titulo + "\n\nAsegúrese de que el archivo " + 
                    rutaFxml + " existe en la carpeta de recursos.");
            }
        }

        /**
         * Notifica que los productos han cambiado y actualiza las gráficas del dashboard.
         * Este método es público para que otros controladores lo puedan llamar.
         */
        public void notificarCambioEnProductos() {
            if (inteligenciaControllerActual != null) {
                System.out.println(" Dashboard: Productos cambió - Actualizando gráficas...");
                inteligenciaControllerActual.actualizarTodasLasGraficas();
            } else {
                System.out.println(" Dashboard: InteligenciaNegocioController no está cargado aún");
            }
        }

        // ========== CIERRE DE SESIÓN ==========

        /**
         * Cierra la sesión del usuario actual.
         * <p>
         * Muestra un diálogo de confirmación y, si el usuario confirma,
         * cierra la sesión en {@link SesionUsuario} y regresa a la pantalla de bienvenida.
         * </p>
         */
        @FXML
        private void cerrarSesion() {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cerrar Sesión");
            alert.setHeaderText("¿Está seguro que desea cerrar sesión?");
            alert.setContentText(
                "Usuario: " + sesionUsuario.getNombreUsuario() + "\n" +
                "Será redirigido a la pantalla de bienvenida."
            );

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    System.out.println("Cerrando sesión de: " + sesionUsuario.getUsername());

                    // ⭐ CERRAR SESIÓN EN EL SISTEMA
                    sesionUsuario.cerrarSesion();

                    System.out.println("✓ Sesión cerrada correctamente");
                    volverABienvenida();
                }
            });
        }

        /**
         * Regresa a la pantalla de Bienvenida con animación.
         */
        private void volverABienvenida() {
            try {
                Parent rootActual = btnCerrarSesion.getScene().getRoot();

                ScaleTransition scale = new ScaleTransition(Duration.millis(300), rootActual);
                scale.setToX(0.95);
                scale.setToY(0.95);

                FadeTransition fade = new FadeTransition(Duration.millis(300), rootActual);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);

                ParallelTransition salida = new ParallelTransition(scale, fade);

                salida.setOnFinished(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("bienvenida.fxml"));
                        Parent root = loader.load();

                        Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
                        Scene scene = new Scene(root, 800, 600);

                        stage.setScene(scene);
                        stage.setTitle("StockFlow - Sistema de Gestión de Inventario");
                        stage.setResizable(true);
                        stage.setMinWidth(800);
                        stage.setMinHeight(600);

                        if (stage.isMaximized()) {
                            stage.setMaximized(false);
                        }

                        stage.centerOnScreen();

                        scene.setOnKeyPressed(event -> {
                            if (event.getCode() == KeyCode.F11) {
                                stage.setFullScreen(!stage.isFullScreen());
                            }
                        });

                        stage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");

                        root.setOpacity(0);
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), root);
                        fadeIn.setFromValue(0);
                        fadeIn.setToValue(1);
                        fadeIn.play();

                        System.out.println("✓ Sesión cerrada - Regresando a Bienvenida");

                    } catch (IOException ex) {
                        System.err.println("Error al cargar Bienvenida: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });

                salida.play();

            } catch (Exception ex) {
                System.err.println("Error al cerrar sesión: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        // ========== MENSAJES DE ALERTA ==========

        /**
         * Muestra un mensaje de acceso denegado.
         * 
         * @param mensaje Descripción del motivo de denegación
         */
        private void mostrarAccesoDenegado(String mensaje) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Acceso Denegado");
            alert.setHeaderText(" No tienes permisos para esta acción");
            alert.setContentText(
                mensaje + "\n\n" +
                "Tu rol actual: " + sesionUsuario.getRolActual().getNombre() + "\n" +
                "Contacta a un administrador si necesitas acceso."
            );
            alert.showAndWait();
        }

        /**
         * Muestra un mensaje de error.
         * 
         * @param titulo Título del diálogo
         * @param mensaje Contenido del mensaje
         */
        private void mostrarError(String titulo, String mensaje) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }

        /**
         * Muestra un mensaje informativo.
         * 
         * @param titulo Título del diálogo
         * @param mensaje Contenido del mensaje
         */
        private void mostrarInformacion(String titulo, String mensaje) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        }

        // ========== MÉTODOS DE ANIMACIÓN ==========
        // (Se mantienen igual que en tu código original)

        private void aplicarAnimacionesIniciales() {
            if (contenedorPrincipal.getChildren().isEmpty()) return;

            Node contenido = contenedorPrincipal.getChildren().get(0);
            if (contenido instanceof VBox) {
                VBox vbox = (VBox) contenido;

                for (int i = 0; i < vbox.getChildren().size(); i++) {
                    Node nodo = vbox.getChildren().get(i);
                    nodo.setOpacity(0);
                    nodo.setTranslateY(30);

                    FadeTransition fade = new FadeTransition(Duration.millis(800), nodo);
                    fade.setFromValue(0);
                    fade.setToValue(1);
                    fade.setDelay(Duration.millis(i * 150));

                    TranslateTransition translate = new TranslateTransition(Duration.millis(800), nodo);
                    translate.setFromY(30);
                    translate.setToY(0);
                    translate.setDelay(Duration.millis(i * 150));
                    translate.setInterpolator(Interpolator.EASE_OUT);

                    ParallelTransition parallel = new ParallelTransition(fade, translate);
                    parallel.play();
                }
            }
        }

        private void configurarEfectosTarjetas() {
            if (contenedorPrincipal.getChildren().isEmpty()) return;

            Node contenido = contenedorPrincipal.getChildren().get(0);
            if (contenido instanceof VBox) {
                VBox vbox = (VBox) contenido;
                buscarYAnimarTarjetas(vbox);
            }
        }

        private void buscarYAnimarTarjetas(Parent parent) {
            for (Node node : parent.getChildrenUnmodifiable()) {
                if (node instanceof VBox) {
                    VBox vboxCard = (VBox) node;
                    String style = vboxCard.getStyle();

                    if (style != null && (style.contains("linear-gradient") || 
                        (style.contains("#FFFFFF") && style.contains("-fx-cursor: hand")))) {
                        configurarEfectoHoverTarjeta(vboxCard);
                    }
                }

                if (node instanceof Parent) {
                    buscarYAnimarTarjetas((Parent) node);
                }
            }
        }

        private void configurarEfectoHoverTarjeta(VBox tarjeta) {
            final double scaleOriginal = 1.0;
            final double scaleHover = 1.05;

            tarjeta.setOnMouseEntered(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), tarjeta);
                scale.setToX(scaleHover);
                scale.setToY(scaleHover);
                scale.setInterpolator(Interpolator.EASE_OUT);
                scale.play();
            });

            tarjeta.setOnMouseExited(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), tarjeta);
                scale.setToX(scaleOriginal);
                scale.setToY(scaleOriginal);
                scale.setInterpolator(Interpolator.EASE_OUT);
                scale.play();
            });

            tarjeta.setOnMousePressed(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), tarjeta);
                scale.setToX(0.98);
                scale.setToY(0.98);
                scale.play();
            });

            tarjeta.setOnMouseReleased(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), tarjeta);
                scale.setToX(scaleHover);
                scale.setToY(scaleHover);
                scale.play();
            });
        }

        private void actualizarFecha() {
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", new Locale("es", "ES"));
            lblFecha.setText(ahora.format(formato));
        }

        private void configurarEfectosHover() {
            configurarHoverBoton(btnInicio);
            configurarHoverBoton(btnVentas);
            configurarHoverBoton(btnProductos);
            configurarHoverBoton(btnClientes);
            configurarHoverBoton(btnInventario);
            configurarHoverBoton(btnFacturacion);
            configurarHoverBoton(btnEstadisticas);
            configurarHoverBoton(btnUsuarios);
        }

        private void configurarHoverBoton(Button boton) {
            boton.setOnMouseEntered(e -> {
                if (boton != botonActivo) {
                    boton.setStyle(ESTILO_BOTON_HOVER);

                    TranslateTransition slide = new TranslateTransition(Duration.millis(200), boton);
                    slide.setToX(5);
                    slide.setInterpolator(Interpolator.EASE_OUT);
                    slide.play();
                }
            });

            boton.setOnMouseExited(e -> {
                if (boton != botonActivo) {
                    boton.setStyle(ESTILO_BOTON_INACTIVO);

                    TranslateTransition slide = new TranslateTransition(Duration.millis(200), boton);
                    slide.setToX(0);
                    slide.setInterpolator(Interpolator.EASE_OUT);
                    slide.play();
                }
            });
        }

        private void establecerBotonActivo(Button boton) {
            if (botonActivo != null) {
                botonActivo.setStyle(ESTILO_BOTON_INACTIVO);
                botonActivo.setTranslateX(0);
            }

            boton.setStyle(ESTILO_BOTON_ACTIVO);
            boton.setTranslateX(5);
            botonActivo = boton;
        }

        /**
         * Aplica una animación de fade-in a un nodo.
         * 
         * @param nodo Parent al que aplicar la animación
         */
        private void aplicarAnimacionFadeIn(Parent nodo) {
            nodo.setOpacity(0);

            FadeTransition fade = new FadeTransition(Duration.millis(400), nodo);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.setInterpolator(Interpolator.EASE_IN);
            fade.play();
        }

        /**
         * Anima el cambio de texto en un Label.
         * 
         * @param label Label a animar
         * @param nuevoTexto Nuevo texto a mostrar
         */
        private void animarCambioTexto(Label label, String nuevoTexto) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), label);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                label.setText(nuevoTexto);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), label);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        }

        /**
         * Configura los clicks en las tarjetas principales para navegar.
         */
        private void configurarClicksTarjetasPrincipales() {
            if (cardProductos != null) {
                cardProductos.setOnMouseClicked(e -> mostrarProductos());
            }

            if (cardVentas != null) {
                cardVentas.setOnMouseClicked(e -> mostrarVentas());
            }

            if (cardInventario != null) {
                cardInventario.setOnMouseClicked(e -> mostrarInventario());
            }
        }

        /**
         * Configura los clicks en las acciones rápidas.
         */
        private void configurarClicksAccionesRapidas() {
            if (btnNuevoCliente != null) {
                btnNuevoCliente.setOnMouseClicked(e -> mostrarClientes());
            }

            if (btnNuevaFactura != null) {
                btnNuevaFactura.setOnMouseClicked(e -> mostrarProductos());
            }

            if (btnVerReportes != null) {
                btnVerReportes.setOnMouseClicked(e -> mostrarEstadisticas());
            }
        }

        /**
         * Establece el nombre de usuario en la interfaz.
         * 
         * @param nombreUsuario Nombre a mostrar
         * @deprecated Usar {@link #configurarInterfazPorUsuario()} en su lugar
         */
        @Deprecated
        public void setUsuario(String nombreUsuario) {
            if (lblUsuario != null) {
                lblUsuario.setText("Usuario: " + nombreUsuario);
            }
        }

        // ========== CHATBOT FLOTANTE ==========

        /**
         * Configura las animaciones y efectos del botón flotante del ChatBot.
         * <p>
         * Aplica efectos hover, animaciones de pulsación y una animación
         * de entrada suave cuando se carga el dashboard.
         * </p>
         */
        private void configurarBotonFlotante() {
            if (btnChatBotFlotante == null) {
                System.err.println("Error: btnChatBotFlotante es null");
                return;
            }

            // Animación de entrada del botón
            btnChatBotFlotante.setOpacity(0);
            btnChatBotFlotante.setScaleX(0.5);
            btnChatBotFlotante.setScaleY(0.5);

            PauseTransition pausa = new PauseTransition(Duration.millis(800));
            pausa.setOnFinished(e -> {
                FadeTransition fade = new FadeTransition(Duration.millis(500), btnChatBotFlotante);
                fade.setFromValue(0);
                fade.setToValue(1);

                ScaleTransition scale = new ScaleTransition(Duration.millis(500), btnChatBotFlotante);
                scale.setFromX(0.5);
                scale.setFromY(0.5);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.setInterpolator(Interpolator.EASE_OUT);

                ParallelTransition entrada = new ParallelTransition(fade, scale);
                entrada.play();
            });
            pausa.play();

            // Efecto hover - Agrandar ligeramente
            btnChatBotFlotante.setOnMouseEntered(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), btnChatBotFlotante);
                scale.setToX(1.1);
                scale.setToY(1.1);
                scale.setInterpolator(Interpolator.EASE_OUT);
                scale.play();

                // Aumentar sombra
                btnChatBotFlotante.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #1e88e5, #1565c0); " +
                    "-fx-background-radius: 50; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.7), 20, 0, 0, 8);"
                );
            });

            btnChatBotFlotante.setOnMouseExited(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), btnChatBotFlotante);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.setInterpolator(Interpolator.EASE_OUT);
                scale.play();

                // Restaurar sombra original
                btnChatBotFlotante.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #1e88e5, #1565c0); " +
                    "-fx-background-radius: 50; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.5), 15, 0, 0, 5);"
                );
            });

            // Efecto de pulsación al hacer click
            btnChatBotFlotante.setOnMousePressed(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), btnChatBotFlotante);
                scale.setToX(0.95);
                scale.setToY(0.95);
                scale.play();
            });

            btnChatBotFlotante.setOnMouseReleased(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), btnChatBotFlotante);
                scale.setToX(1.1);
                scale.setToY(1.1);
                scale.play();
            });

            // Animación de latido periódica para llamar la atención
            Timeline latido = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(btnChatBotFlotante.scaleXProperty(), 1.0),
                    new KeyValue(btnChatBotFlotante.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(1000), 
                    new KeyValue(btnChatBotFlotante.scaleXProperty(), 1.05, Interpolator.EASE_BOTH),
                    new KeyValue(btnChatBotFlotante.scaleYProperty(), 1.05, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(2000), 
                    new KeyValue(btnChatBotFlotante.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                    new KeyValue(btnChatBotFlotante.scaleYProperty(), 1.0, Interpolator.EASE_BOTH)
                )
            );
            latido.setCycleCount(Timeline.INDEFINITE);
            latido.play();

            System.out.println("Botón flotante del ChatBot configurado correctamente");
        }

        /**
         * Abre la ventana emergente del ChatBot.
         * <p>
         * Carga la interfaz del asistente de IA en una ventana modal
         * independiente que no afecta el funcionamiento del dashboard.
         * </p>
         */
        @FXML
        private void abrirChatBot() {
            try {
                System.out.println("Abriendo ChatBot...");

                // Cargar el FXML del ChatBot
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("ChatBot.fxml")
                );
                Parent root = loader.load();

                // Crear nueva ventana (Stage)
                Stage chatBotStage = new Stage();
                chatBotStage.setTitle("Asistente IA - StockFlow");
                chatBotStage.initOwner(btnChatBotFlotante.getScene().getWindow());

                // Configurar la escena
                Scene scene = new Scene(root, 1200, 700);
                chatBotStage.setScene(scene);

                // Configurar dimensiones mínimas
                chatBotStage.setMinWidth(900);
                chatBotStage.setMinHeight(600);

                // Centrar la ventana
                chatBotStage.centerOnScreen();

                // Animación de entrada de la ventana
                root.setOpacity(0);
                chatBotStage.show();

                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();

                // Efecto de rebote al botón flotante como confirmación
                ScaleTransition rebote = new ScaleTransition(Duration.millis(150), btnChatBotFlotante);
                rebote.setFromX(1.0);
                rebote.setFromY(1.0);
                rebote.setToX(1.2);
                rebote.setToY(1.2);
                rebote.setAutoReverse(true);
                rebote.setCycleCount(2);
                rebote.play();

                System.out.println("ChatBot abierto correctamente");

            } catch (IOException e) {
                System.err.println("Error al abrir el ChatBot: " + e.getMessage());
                e.printStackTrace();

                mostrarError(
                    "Error al abrir ChatBot", 
                    "No se pudo cargar la interfaz del ChatBot.\n\n" +
                    "Verifique que el archivo ChatBot.fxml existe en:\n" +
                    "src/main/resources/com/mycompany/stockflow/ChatBot.fxml\n\n" +
                    "Error: " + e.getMessage()
                );
            }
        }
    }