package com.mycompany.stockflow.utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Servicio para captura de imágenes con cámara web.
 * Utiliza la biblioteca Webcam Capture de Sarxos.
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 */
public class CamaraServicio {
    
    private static Webcam webcam = null;
    private static boolean camaraDisponible = false;
    
    static {
        try {
            webcam = Webcam.getDefault();
            if (webcam != null) {
                camaraDisponible = true;
                System.out.println("Cámara web detectada: " + webcam.getName());
            } else {
                System.out.println("No se detectó ninguna cámara web");
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar cámara: " + e.getMessage());
            camaraDisponible = false;
        }
    }
    
    /**
     * Verifica si hay una cámara disponible en el sistema.
     * 
     * @return true si hay cámara disponible
     */
    public static boolean isCamaraDisponible() {
        return camaraDisponible && webcam != null;
    }
    
    /**
     * Abre una ventana para capturar una foto con la cámara.
     * 
     * @return imagen capturada o null si se cancela
     */
    public static Image capturarFoto() {
        if (!isCamaraDisponible()) {
            System.err.println("No hay cámara disponible");
            return null;
        }
        
        AtomicReference<Image> imagenCapturada = new AtomicReference<>(null);
        
        try {
            // Configurar resolución de la cámara
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcam.open();
            
            // Crear ventana de captura
            Stage ventanaCaptura = new Stage();
            ventanaCaptura.initModality(Modality.APPLICATION_MODAL);
            ventanaCaptura.setTitle("Capturar Foto del Producto");
            
            VBox contenedor = new VBox(15);
            contenedor.setAlignment(Pos.CENTER);
            contenedor.setPadding(new Insets(20));
            contenedor.setStyle("-fx-background-color: #2c3e50;");
            
            // Vista previa de la cámara
            ImageView vistaPrevia = new ImageView();
            vistaPrevia.setFitWidth(640);
            vistaPrevia.setFitHeight(480);
            vistaPrevia.setPreserveRatio(true);
            vistaPrevia.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);");
            
            // Botones
            Button btnCapturar = new Button("📷 Capturar Foto");
            btnCapturar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                               "-fx-font-size: 16px; -fx-font-weight: bold; " +
                               "-fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
            
            Button btnCancelar = new Button("❌ Cancelar");
            btnCancelar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                               "-fx-font-size: 16px; -fx-font-weight: bold; " +
                               "-fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
            
            javafx.scene.layout.HBox botonesContainer = new javafx.scene.layout.HBox(15);
            botonesContainer.setAlignment(Pos.CENTER);
            botonesContainer.getChildren().addAll(btnCapturar, btnCancelar);
            
            contenedor.getChildren().addAll(vistaPrevia, botonesContainer);
            
            // Thread para actualizar vista previa
            Thread hiloActualizacion = new Thread(() -> {
                while (webcam.isOpen() && !Thread.interrupted()) {
                    try {
                        BufferedImage imagen = webcam.getImage();
                        if (imagen != null) {
                            Image fxImagen = SwingFXUtils.toFXImage(imagen, null);
                            javafx.application.Platform.runLater(() -> vistaPrevia.setImage(fxImagen));
                        }
                        Thread.sleep(50); // ~20 FPS
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        System.err.println("Error actualizando vista previa: " + e.getMessage());
                    }
                }
            });
            hiloActualizacion.setDaemon(true);
            hiloActualizacion.start();
            
            // Acción del botón capturar
            btnCapturar.setOnAction(e -> {
                try {
                    BufferedImage foto = webcam.getImage();
                    if (foto != null) {
                        imagenCapturada.set(SwingFXUtils.toFXImage(foto, null));
                        hiloActualizacion.interrupt();
                        webcam.close();
                        ventanaCaptura.close();
                    }
                } catch (Exception ex) {
                    System.err.println("Error al capturar foto: " + ex.getMessage());
                }
            });
            
            // Acción del botón cancelar
            btnCancelar.setOnAction(e -> {
                hiloActualizacion.interrupt();
                webcam.close();
                ventanaCaptura.close();
            });
            
            // Cerrar cámara al cerrar ventana
            ventanaCaptura.setOnCloseRequest(e -> {
                hiloActualizacion.interrupt();
                webcam.close();
            });
            
            Scene escena = new Scene(contenedor);
            ventanaCaptura.setScene(escena);
            ventanaCaptura.showAndWait();
            
        } catch (Exception e) {
            System.err.println("Error al capturar foto: " + e.getMessage());
            e.printStackTrace();
            if (webcam != null && webcam.isOpen()) {
                webcam.close();
            }
        }
        
        return imagenCapturada.get();
    }
    
    /**
     * Genera una imagen de placeholder cuando no hay cámara disponible.
     * 
     * @return imagen de placeholder
     */
    public static Image generarImagenPlaceholder() {
        WritableImage imagen = new WritableImage(400, 400);
        PixelWriter writer = imagen.getPixelWriter();
        
        // Fondo gris claro
        for (int y = 0; y < 400; y++) {
            for (int x = 0; x < 400; x++) {
                writer.setColor(x, y, Color.rgb(245, 247, 250));
            }
        }
        
        // Dibujar un icono de cámara simple (círculo y rectángulo)
        // Centro del círculo
        int centerX = 200;
        int centerY = 180;
        int radius = 60;
        
        // Dibujar círculo (lente)
        for (int y = 0; y < 400; y++) {
            for (int x = 0; x < 400; x++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                if (distance <= radius && distance >= radius - 8) {
                    writer.setColor(x, y, Color.rgb(42, 82, 152));
                }
            }
        }
        
        // Dibujar rectángulo superior (flash)
        for (int y = 100; y < 120; y++) {
            for (int x = 170; x < 230; x++) {
                writer.setColor(x, y, Color.rgb(42, 82, 152));
            }
        }
        
        return imagen;
    }
    
    /**
     * Cierra la cámara si está abierta.
     */
    public static void cerrarCamara() {
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }
}