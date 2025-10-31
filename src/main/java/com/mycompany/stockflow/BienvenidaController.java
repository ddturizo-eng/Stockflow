
package com.mycompany.stockflow;




import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BienvenidaController implements Initializable {

    @FXML
    private Label letra_S, letra_T, letra_O, letra_C, letra_K;
    @FXML
    private Label letra_F, letra_L, letra_O2, letra_W;
    @FXML
    private Button btnInvitado, btnAdministrador;
    @FXML
    private Label subtitulo, footer;
    @FXML
    private VBox contenedorBotones;
    @FXML
    private HBox contenedorLetras;
    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ocultarElementos();
        configurarResponsive();
        animacionEscrituraLetras();
        configurarEfectosHover();
    }

    private void ocultarElementos() {
        Label[] letras = {letra_S, letra_T, letra_O, letra_C, letra_K, 
                         letra_F, letra_L, letra_O2, letra_W};
        
        for (Label letra : letras) {
            letra.setOpacity(0);
            letra.setTranslateY(-30);
        }
        
        subtitulo.setOpacity(0);
        subtitulo.setTranslateY(20);
        
        btnInvitado.setOpacity(0);
        btnInvitado.setTranslateY(30);
        
        btnAdministrador.setOpacity(0);
        btnAdministrador.setTranslateY(30);
        
        footer.setOpacity(0);
    }

    private void animacionEscrituraLetras() {
        Label[] letras = {letra_S, letra_T, letra_O, letra_C, letra_K, 
                         letra_F, letra_L, letra_O2, letra_W};
        
        SequentialTransition escritura = new SequentialTransition();
        
        // Animar cada letra con efecto de escritura
        for (int i = 0; i < letras.length; i++) {
            Label letra = letras[i];
            
            ParallelTransition letraAnim = new ParallelTransition();
            
            // Fade in
            FadeTransition fade = new FadeTransition(Duration.millis(200), letra);
            fade.setFromValue(0);
            fade.setToValue(1);
            
            // Caída suave
            TranslateTransition caida = new TranslateTransition(Duration.millis(300), letra);
            caida.setFromY(-30);
            caida.setToY(0);
            caida.setInterpolator(Interpolator.EASE_OUT);
            
            // Pequeño rebote
            ScaleTransition escala = new ScaleTransition(Duration.millis(300), letra);
            escala.setFromX(0.5);
            escala.setFromY(0.5);
            escala.setToX(1.1);
            escala.setToY(1.1);
            escala.setInterpolator(Interpolator.EASE_OUT);
            
            letraAnim.getChildren().addAll(fade, caida, escala);
            
            // Ajuste final (regresa a escala 1.0)
            ScaleTransition ajuste = new ScaleTransition(Duration.millis(150), letra);
            ajuste.setFromX(1.1);
            ajuste.setFromY(1.1);
            ajuste.setToX(1.0);
            ajuste.setToY(1.0);
            
            SequentialTransition letraCompleta = new SequentialTransition(letraAnim, ajuste);
            escritura.getChildren().add(letraCompleta);
        }
        
        // Cuando termina la escritura, iniciar animaciones continuas
        escritura.setOnFinished(e -> {
            animarSubtituloYBotones();
            iniciarAnimacionInfinita();
        });
        
        escritura.play();
    }

    private void animarSubtituloYBotones() {
        // Animar subtítulo
        TranslateTransition subtituloTrans = new TranslateTransition(Duration.seconds(0.8), subtitulo);
        subtituloTrans.setFromY(20);
        subtituloTrans.setToY(0);
        subtituloTrans.setInterpolator(Interpolator.EASE_OUT);
        
        FadeTransition subtituloFade = new FadeTransition(Duration.seconds(0.8), subtitulo);
        subtituloFade.setFromValue(0);
        subtituloFade.setToValue(1);
        
        ParallelTransition subtituloAnim = new ParallelTransition(subtituloTrans, subtituloFade);
        subtituloAnim.setDelay(Duration.seconds(0.3));
        subtituloAnim.play();
        
        // Animar botones
        animarBoton(btnInvitado, 0.6);
        animarBoton(btnAdministrador, 0.8);
        
        // Animar footer
        FadeTransition footerFade = new FadeTransition(Duration.seconds(1.0), footer);
        footerFade.setFromValue(0);
        footerFade.setToValue(1);
        footerFade.setDelay(Duration.seconds(1.2));
        footerFade.play();
    }

    private void iniciarAnimacionInfinita() {
        Label[] letras = {letra_S, letra_T, letra_O, letra_C, letra_K, 
                         letra_F, letra_L, letra_O2, letra_W};
        
        // Onda de color que recorre las letras
        for (int i = 0; i < letras.length; i++) {
            Label letra = letras[i];
            
            // Animación de escala pulsante
            Timeline pulso = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(letra.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                    new KeyValue(letra.scaleYProperty(), 1.0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.5), 
                    new KeyValue(letra.scaleXProperty(), 1.15, Interpolator.EASE_BOTH),
                    new KeyValue(letra.scaleYProperty(), 1.15, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(1.0), 
                    new KeyValue(letra.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                    new KeyValue(letra.scaleYProperty(), 1.0, Interpolator.EASE_BOTH))
            );
            
            // Animación de translación suave (flotación)
            Timeline flotacion = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(letra.translateYProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(1.5), 
                    new KeyValue(letra.translateYProperty(), -8, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(3.0), 
                    new KeyValue(letra.translateYProperty(), 0, Interpolator.EASE_BOTH))
            );
            
            // Cada letra comienza con un delay diferente para crear efecto de onda
            pulso.setDelay(Duration.seconds(i * 0.15));
            pulso.setCycleCount(Timeline.INDEFINITE);
            
            flotacion.setDelay(Duration.seconds(i * 0.2));
            flotacion.setCycleCount(Timeline.INDEFINITE);
            
            pulso.play();
            flotacion.play();
        }
        
        // Efecto de brillo intermitente en todo el contenedor
        Timeline brillo = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(contenedorLetras.opacityProperty(), 1.0)),
            new KeyFrame(Duration.seconds(2), 
                new KeyValue(contenedorLetras.opacityProperty(), 0.85)),
            new KeyFrame(Duration.seconds(4), 
                new KeyValue(contenedorLetras.opacityProperty(), 1.0))
        );
        brillo.setCycleCount(Timeline.INDEFINITE);
        brillo.play();
    }

    private void animarBoton(Button boton, double delay) {
        TranslateTransition translate = new TranslateTransition(Duration.seconds(0.6), boton);
        translate.setFromY(30);
        translate.setToY(0);
        translate.setInterpolator(Interpolator.EASE_OUT);
        
        FadeTransition fade = new FadeTransition(Duration.seconds(0.6), boton);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.4), boton);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        ParallelTransition parallel = new ParallelTransition(translate, fade, scale);
        parallel.setDelay(Duration.seconds(delay));
        parallel.play();
    }

    private void configurarEfectosHover() {
        configurarHoverBoton(btnInvitado, "#4CAF50", "#45a049");
        configurarHoverBoton(btnAdministrador, "#2196F3", "#1976D2");
    }

    private void configurarHoverBoton(Button boton, String colorNormal, String colorHover) {
        boton.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), boton);
            scale.setToX(1.08);
            scale.setToY(1.08);
            scale.setInterpolator(Interpolator.EASE_OUT);
            scale.play();
            
            boton.setStyle(
                "-fx-background-color: " + colorHover + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 25;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0.5, 0, 5);"
            );
            
            RotateTransition rotate = new RotateTransition(Duration.millis(100), boton);
            rotate.setByAngle(2);
            rotate.setCycleCount(2);
            rotate.setAutoReverse(true);
            rotate.play();
        });
        
        boton.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), boton);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setInterpolator(Interpolator.EASE_IN);
            scale.play();
            
            boton.setStyle(
                "-fx-background-color: " + colorNormal + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 25;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.3, 0, 2);"
            );
        });
        
        boton.setOnMousePressed(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), boton);
            scale.setToX(0.95);
            scale.setToY(0.95);
            scale.play();
        });
        
        boton.setOnMouseReleased(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), boton);
            scale.setToX(1.08);
            scale.setToY(1.08);
            scale.play();
        });
    }

    private void configurarResponsive() {
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            ajustarTamanos(newVal.doubleValue(), rootPane.getHeight());
        });
        
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            ajustarTamanos(rootPane.getWidth(), newVal.doubleValue());
        });
    }

    private void ajustarTamanos(double ancho, double alto) {
        double tamanoBase = Math.min(ancho, alto);
        double tamanoLetra = tamanoBase * 0.09;
        tamanoLetra = Math.max(36, Math.min(tamanoLetra, 72));
        
        Label[] letras = {letra_S, letra_T, letra_O, letra_C, letra_K, 
                         letra_F, letra_L, letra_O2, letra_W};
        
        for (Label letra : letras) {
            letra.setStyle("-fx-font-size: " + tamanoLetra + "px; -fx-font-weight: bold;");
        }
        
        double tamanoSubtitulo = tamanoBase * 0.022;
        tamanoSubtitulo = Math.max(14, Math.min(tamanoSubtitulo, 18));
        subtitulo.setStyle("-fx-font-size: " + tamanoSubtitulo + "px; -fx-font-style: italic;");
        
        double tamanoBoton = tamanoBase * 0.02;
        tamanoBoton = Math.max(14, Math.min(tamanoBoton, 16));
        
        double anchoBoton = ancho * 0.375;
        anchoBoton = Math.max(250, Math.min(anchoBoton, 400));
        
        btnInvitado.setPrefWidth(anchoBoton);
        btnAdministrador.setPrefWidth(anchoBoton);
        
        double tamanoFooter = tamanoBase * 0.015;
        tamanoFooter = Math.max(10, Math.min(tamanoFooter, 12));
        footer.setStyle("-fx-font-size: " + tamanoFooter + "px;");
    }

    @FXML
    private void iniciarComoInvitado() {
        animarSalidaBoton(btnInvitado, () -> {
            try {
                // Cargar Login con escena completa y maximizada
                FXMLLoader loader = new FXMLLoader(getClass().getResource("productos_invitado.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(root, 1000, 600);
                
                stage.setScene(scene);
                stage.setTitle("StockFlow - Modo Invitado");
                stage.setResizable(true);
                stage.setMaximized(true);
                
                // IMPORTANTE: Configurar F11 correctamente
                scene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.F11) {
                        stage.setFullScreen(!stage.isFullScreen());
                    }
                });
                
                // Configurar mensaje de salida de pantalla completa
                stage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");
                
                // Fade in
                root.setOpacity(0);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
                
            } catch (IOException e) {
                System.err.println("Error al cargar Login: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void iniciarComoAdministrador() {
        animarSalidaBoton(btnAdministrador, () -> {
            try {
                // Cargar Login con escena completa y maximizada
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(root, 1000, 600);
                
                stage.setScene(scene);
                stage.setTitle("StockFlow - Login Administrador");
                stage.setResizable(true);
                stage.setMaximized(true);
                
                // IMPORTANTE: Configurar F11 correctamente
                scene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.F11) {
                        stage.setFullScreen(!stage.isFullScreen());
                    }
                });
                
                // Configurar mensaje de salida de pantalla completa
                stage.setFullScreenExitHint("Presiona F11 o ESC para salir de pantalla completa");
                
                // Fade in
                root.setOpacity(0);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
                
            } catch (IOException e) {
                System.err.println("Error al cargar Login: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void animarSalidaBoton(Button boton, Runnable accion) {
        // Animación de salida estilo Netflix
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), boton);
        scale.setToX(1.15);
        scale.setToY(1.15);
        
        FadeTransition fade = new FadeTransition(Duration.millis(300), rootPane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.millis(200));
        
        ParallelTransition parallel = new ParallelTransition(scale, fade);
        parallel.setOnFinished(e -> accion.run());
        parallel.play();
    }
}