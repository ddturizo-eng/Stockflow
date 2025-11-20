package com.mycompany.stockflow;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class BienvenidaController implements Initializable {

    @FXML private Label letra_S, letra_F, lblBienvenido;
    @FXML private Button btnInvitado, btnAdministrador;
    @FXML private Label footer;
    @FXML private HBox contenedorLetras;
    @FXML private AnchorPane rootPane;
    @FXML private Canvas canvasOndas, canvasParticulas;

    private AnimationTimer animacionOndas;
    private AnimationTimer animacionParticulas;
    private double tiempo = 0;
    private List<Particula> particulas = new ArrayList<>();
    private Random random = new Random();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ocultarElementos();
        configurarResponsive();
        iniciarAnimaciones();
        animarEntrada();
        configurarEfectosHover();
    }

    private void ocultarElementos() {
        letra_S.setOpacity(0);
        letra_F.setOpacity(0);
        lblBienvenido.setOpacity(0);
        btnInvitado.setOpacity(0);
        btnAdministrador.setOpacity(0);
        footer.setOpacity(0);
        canvasOndas.setOpacity(0);
    }

    private void configurarResponsive() {
        // Canvas de partículas se ajusta al tamaño del rootPane
        canvasParticulas.widthProperty().bind(rootPane.widthProperty());
        canvasParticulas.heightProperty().bind(rootPane.heightProperty());
        
        // Listener para recrear partículas cuando cambia el tamaño
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            recrearParticulas();
        });
        
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            recrearParticulas();
        });
    }

    private void recrearParticulas() {
        particulas.clear();
        double width = canvasParticulas.getWidth();
        double height = canvasParticulas.getHeight();
        
        // Ajustar número de partículas según tamaño de ventana
        int numParticulas = (int) Math.min(50, (width * height) / 20000);
        
        for (int i = 0; i < numParticulas; i++) {
            particulas.add(new Particula(
                random.nextDouble() * width,
                random.nextDouble() * height,
                random.nextDouble() * 2 + 0.5
            ));
        }
    }

    private void iniciarAnimaciones() {
        // Crear partículas iniciales
        recrearParticulas();

        // Animación de ondas tecnológicas
        animacionOndas = new AnimationTimer() {
            @Override
            public void handle(long now) {
                dibujarOndas();
                tiempo += 0.02;
            }
        };

        // Animación de partículas flotantes
        animacionParticulas = new AnimationTimer() {
            @Override
            public void handle(long now) {
                dibujarParticulas();
            }
        };

        animacionOndas.start();
        animacionParticulas.start();
    }

    private void dibujarOndas() {
        if (canvasOndas == null) return;
        
        GraphicsContext gc = canvasOndas.getGraphicsContext2D();
        double width = canvasOndas.getWidth();
        double height = canvasOndas.getHeight();
        
        // Limpiar canvas
        gc.clearRect(0, 0, width, height);
        
        // Dibujar múltiples ondas con efecto de conexión
        for (int onda = 0; onda < 3; onda++) {
            gc.setStroke(Color.rgb(79, 195, 247, 0.4 - onda * 0.1));
            gc.setLineWidth(2.0 - onda * 0.3);
            
            gc.beginPath();
            
            for (double x = 0; x <= width; x += 5) {
                double y = height / 2 + 
                          Math.sin((x / 50) + tiempo + onda) * 20 +
                          Math.sin((x / 30) + tiempo * 1.5 + onda) * 15 +
                          Math.cos((x / 80) + tiempo * 0.8) * 10;
                
                if (x == 0) {
                    gc.moveTo(x, y);
                } else {
                    gc.lineTo(x, y);
                }
                
                // Puntos de conexión brillantes
                if (x % 40 == 0) {
                    double brillo = Math.abs(Math.sin(tiempo + x / 20));
                    gc.setFill(Color.rgb(79, 195, 247, brillo * 0.8));
                    gc.fillOval(x - 2, y - 2, 4, 4);
                }
            }
            
            gc.stroke();
        }
        
        // Partículas flotantes en las ondas
        for (int i = 0; i < 15; i++) {
            double x = (tiempo * 50 + i * 40) % width;
            double y = height / 2 + Math.sin((x / 50) + tiempo) * 20;
            double alpha = Math.abs(Math.sin(tiempo + i));
            
            gc.setFill(Color.rgb(255, 152, 0, alpha * 0.6));
            gc.fillOval(x - 3, y - 3, 6, 6);
            
            // Estela
            gc.setFill(Color.rgb(79, 195, 247, alpha * 0.3));
            gc.fillOval(x - 1, y - 1, 2, 2);
        }
    }

    private void dibujarParticulas() {
        if (canvasParticulas == null) return;
        
        GraphicsContext gc = canvasParticulas.getGraphicsContext2D();
        double width = canvasParticulas.getWidth();
        double height = canvasParticulas.getHeight();
        
        // Limpiar con fade para efecto de estela
        gc.setFill(Color.rgb(30, 60, 114, 0.05));
        gc.fillRect(0, 0, width, height);
        
        // Actualizar y dibujar partículas
        for (Particula p : particulas) {
            p.actualizar(width, height);
            
            // Partícula
            gc.setFill(Color.rgb(79, 195, 247, p.alpha));
            gc.fillOval(p.x - p.size/2, p.y - p.size/2, p.size, p.size);
            
            // Conexiones entre partículas cercanas
            for (Particula p2 : particulas) {
                double dist = Math.sqrt(Math.pow(p.x - p2.x, 2) + Math.pow(p.y - p2.y, 2));
                if (dist < 120) {
                    double alpha = (120 - dist) / 120 * 0.2;
                    gc.setStroke(Color.rgb(100, 200, 255, alpha));
                    gc.setLineWidth(0.5);
                    gc.strokeLine(p.x, p.y, p2.x, p2.y);
                }
            }
        }
    }

    // Clase interna para partículas
    private class Particula {
        double x, y, vx, vy, size, alpha;
        
        Particula(double x, double y, double velocidad) {
            this.x = x;
            this.y = y;
            this.vx = (random.nextDouble() - 0.5) * velocidad;
            this.vy = (random.nextDouble() - 0.5) * velocidad;
            this.size = random.nextDouble() * 3 + 1;
            this.alpha = random.nextDouble() * 0.5 + 0.3;
        }
        
        void actualizar(double width, double height) {
            x += vx;
            y += vy;
            
            if (x < 0 || x > width) vx *= -1;
            if (y < 0 || y > height) vy *= -1;
            
            // Mantener dentro de límites
            x = Math.max(0, Math.min(width, x));
            y = Math.max(0, Math.min(height, y));
        }
    }

    private void animarEntrada() {
        // Logo aparece primero
        Timeline logoAnim = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(letra_S.opacityProperty(), 0),
                new KeyValue(letra_F.opacityProperty(), 0),
                new KeyValue(letra_S.scaleXProperty(), 0.5),
                new KeyValue(letra_S.scaleYProperty(), 0.5),
                new KeyValue(letra_F.scaleXProperty(), 0.5),
                new KeyValue(letra_F.scaleYProperty(), 0.5)),
            new KeyFrame(Duration.millis(600),
                new KeyValue(letra_S.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(letra_F.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(letra_S.scaleXProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(letra_S.scaleYProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(letra_F.scaleXProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(letra_F.scaleYProperty(), 1, Interpolator.EASE_OUT))
        );
        
        // Canvas ondas aparece
        FadeTransition canvasFade = new FadeTransition(Duration.millis(800), canvasOndas);
        canvasFade.setFromValue(0);
        canvasFade.setToValue(1);
        canvasFade.setDelay(Duration.millis(400));
        
        // Bienvenido aparece
        FadeTransition bienvenidoFade = new FadeTransition(Duration.millis(600), lblBienvenido);
        bienvenidoFade.setFromValue(0);
        bienvenidoFade.setToValue(1);
        bienvenidoFade.setDelay(Duration.millis(800));
        
        // Botones aparecen
        animarBotonEntrada(btnAdministrador, 1000);
        animarBotonEntrada(btnInvitado, 1150);
        
        // Footer
        FadeTransition footerFade = new FadeTransition(Duration.millis(600), footer);
        footerFade.setFromValue(0);
        footerFade.setToValue(1);
        footerFade.setDelay(Duration.millis(1300));
        
        logoAnim.play();
        canvasFade.play();
        bienvenidoFade.play();
        footerFade.play();
    }

    private void animarBotonEntrada(Button boton, double delayMs) {
        FadeTransition fade = new FadeTransition(Duration.millis(500), boton);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delayMs));
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(500), boton);
        translate.setFromY(20);
        translate.setToY(0);
        translate.setDelay(Duration.millis(delayMs));
        translate.setInterpolator(Interpolator.EASE_OUT);
        
        fade.play();
        translate.play();
    }

    private void configurarEfectosHover() {
        configurarHoverBoton(btnAdministrador, "linear-gradient(to right, #00bcd4, #0097a7)", 
                           "linear-gradient(to right, #00acc1, #00838f)");
        configurarHoverBotonInvitado();
    }

    private void configurarHoverBoton(Button boton, String colorNormal, String colorHover) {
        boton.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), boton);
            scale.setToX(1.08);
            scale.setToY(1.08);
            scale.setInterpolator(Interpolator.EASE_OUT);
            scale.play();
            
            String estilo = boton.getStyle();
            boton.setStyle(estilo.replace(colorNormal, colorHover));
        });
        
        boton.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), boton);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setInterpolator(Interpolator.EASE_IN);
            scale.play();
            
            String estilo = boton.getStyle();
            boton.setStyle(estilo.replace(colorHover, colorNormal));
        });
    }

    private void configurarHoverBotonInvitado() {
        btnInvitado.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btnInvitado);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.setInterpolator(Interpolator.EASE_OUT);
            scale.play();
            
            // Cambiar a estilo con fondo sutil
            btnInvitado.setStyle(btnInvitado.getStyle().replace("transparent", "rgba(79, 195, 247, 0.15)"));
        });
        
        btnInvitado.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btnInvitado);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setInterpolator(Interpolator.EASE_IN);
            scale.play();
            
            // Volver a transparente
            btnInvitado.setStyle(btnInvitado.getStyle().replace("rgba(79, 195, 247, 0.15)", "transparent"));
        });
    }

    @FXML
    private void iniciarComoInvitado() {
        detenerAnimaciones();
        animarSalida(() -> cargarVista("productos_invitado.fxml", "StockFlow - Modo Invitado"));
    }

    @FXML
    private void iniciarComoAdministrador() {
        detenerAnimaciones();
        animarSalida(() -> cargarVista("Login.fxml", "StockFlow - Login Administrador"));
    }

    private void cargarVista(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 600);
            
            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.setResizable(true);
            stage.setMaximized(true);
            
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
            
        } catch (IOException e) {
            System.err.println("Error al cargar " + fxml + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void animarSalida(Runnable accion) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), rootPane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> accion.run());
        fade.play();
    }

    private void detenerAnimaciones() {
        if (animacionOndas != null) animacionOndas.stop();
        if (animacionParticulas != null) animacionParticulas.stop();
    }
}