package com.mycompany.stockflow.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidad para gestionar imágenes de productos.
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 */
public class ImagenProductoUtil {
    
    /** Directorio base para almacenar imágenes de productos */
    private static final String DIRECTORIO_IMAGENES = "data/imagenes_productos";
    
    /** Formatos de imagen soportados */
    private static final String[] FORMATOS_SOPORTADOS = {"png", "jpg", "jpeg", "bmp", "gif"};
    
    /** Ancho máximo para redimensionar imágenes */
    private static final int ANCHO_MAXIMO = 800;
    
    /** Alto máximo para redimensionar imágenes */
    private static final int ALTO_MAXIMO = 800;
    
    /**
     * Inicializa el directorio de imágenes si no existe.
     * 
     * @throws IOException si no se puede crear el directorio
     */
    public static void inicializarDirectorio() throws IOException {
        Path path = Paths.get(DIRECTORIO_IMAGENES);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            System.out.println("✓ Directorio de imágenes creado: " + DIRECTORIO_IMAGENES);
        }
    }
    
    /**
     * Guarda una imagen de JavaFX en el directorio de productos.
     * 
     * @param imagen la imagen de JavaFX a guardar
     * @param codigoProducto código único del producto
     * @return la ruta relativa del archivo guardado
     * @throws IOException si ocurre un error al guardar
     */
    public static String guardarImagen(Image imagen, String codigoProducto) throws IOException {
        if (imagen == null) {
            throw new IOException("La imagen no puede ser null");
        }
        
        inicializarDirectorio();
        
        // Generar nombre único para el archivo
        String nombreArchivo = generarNombreUnico(codigoProducto) + ".png";
        Path rutaCompleta = Paths.get(DIRECTORIO_IMAGENES, nombreArchivo);
        
        // Convertir Image de JavaFX a BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imagen, null);
        
        if (bufferedImage == null) {
            throw new IOException("Error al convertir la imagen");
        }
        
        // Redimensionar si es necesario
        bufferedImage = redimensionarSiNecesario(bufferedImage);
        
        // Guardar la imagen
        File archivoSalida = rutaCompleta.toFile();
        boolean guardado = ImageIO.write(bufferedImage, "png", archivoSalida);
        
        if (!guardado) {
            throw new IOException("No se pudo guardar la imagen");
        }
        
        System.out.println("✓ Imagen guardada: " + rutaCompleta.toAbsolutePath());
        
        // Retornar ruta relativa
        return DIRECTORIO_IMAGENES + "/" + nombreArchivo;
    }
    
    /**
     * Copia un archivo de imagen al directorio de productos.
     * 
     * @param archivoOrigen archivo de imagen a copiar
     * @param codigoProducto código único del producto
     * @return la ruta relativa del archivo copiado
     * @throws IOException si ocurre un error al copiar
     */
    public static String copiarImagen(File archivoOrigen, String codigoProducto) throws IOException {
        if (archivoOrigen == null || !archivoOrigen.exists()) {
            throw new IOException("El archivo de origen no existe");
        }
        
        inicializarDirectorio();
        
        // Validar formato
        if (!esFormatoValido(archivoOrigen)) {
            throw new IOException("Formato de imagen no soportado. Use: PNG, JPG, JPEG, BMP o GIF");
        }
        
        // Generar nombre único
        String extension = obtenerExtension(archivoOrigen);
        String nombreArchivo = generarNombreUnico(codigoProducto) + "." + extension;
        Path rutaDestino = Paths.get(DIRECTORIO_IMAGENES, nombreArchivo);
        
        // Copiar archivo
        Files.copy(archivoOrigen.toPath(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("✓ Imagen copiada: " + rutaDestino.toAbsolutePath());
        
        // Retornar ruta relativa
        return DIRECTORIO_IMAGENES + "/" + nombreArchivo;
    }
    
    /**
     * Carga una imagen desde una ruta.
     * 
     * @param ruta ruta del archivo de imagen
     * @return la imagen de JavaFX o null si no se puede cargar
     */
    public static Image cargarImagen(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            System.out.println("⚠ Ruta de imagen vacía");
            return null;
        }
        
        try {
            File archivo = new File(ruta);
            
            if (!archivo.exists()) {
                System.err.println("⚠ Archivo no encontrado: " + ruta);
                System.err.println("⚠ Ruta absoluta: " + archivo.getAbsolutePath());
                return null;
            }
            
            if (!archivo.canRead()) {
                System.err.println("⚠ No se puede leer el archivo: " + ruta);
                return null;
            }
            
            Image imagen = new Image(archivo.toURI().toString());
            
            if (imagen.isError()) {
                System.err.println("⚠ Error al cargar imagen: " + ruta);
                return null;
            }
            
            System.out.println("✓ Imagen cargada correctamente: " + ruta);
            return imagen;
            
        } catch (Exception e) {
            System.err.println("⚠ Error al cargar imagen: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Elimina una imagen del sistema de archivos.
     * 
     * @param ruta ruta del archivo a eliminar
     * @return true si se eliminó correctamente
     */
    public static boolean eliminarImagen(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            return false;
        }
        
        try {
            File archivo = new File(ruta);
            if (archivo.exists()) {
                boolean eliminado = archivo.delete();
                if (eliminado) {
                    System.out.println("✓ Imagen eliminada: " + ruta);
                } else {
                    System.err.println("⚠ No se pudo eliminar: " + ruta);
                }
                return eliminado;
            }
            return false;
        } catch (Exception e) {
            System.err.println("⚠ Error al eliminar imagen: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Genera un nombre único para un archivo de imagen.
     * 
     * @param codigoProducto código del producto
     * @return nombre único del archivo sin extensión
     */
    private static String generarNombreUnico(String codigoProducto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
        String timestamp = LocalDateTime.now().format(formatter);
        String codigoLimpio = codigoProducto.replaceAll("[^a-zA-Z0-9-_]", "_");
        return codigoLimpio + "_" + timestamp;
    }
    
    /**
     * Valida si un archivo tiene un formato de imagen soportado.
     * 
     * @param archivo archivo a validar
     * @return true si el formato es válido
     */
    private static boolean esFormatoValido(File archivo) {
        String extension = obtenerExtension(archivo).toLowerCase();
        for (String formato : FORMATOS_SOPORTADOS) {
            if (formato.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene la extensión de un archivo.
     * 
     * @param archivo archivo del cual obtener la extensión
     * @return la extensión sin el punto
     */
    private static String obtenerExtension(File archivo) {
        String nombre = archivo.getName();
        int ultimoPunto = nombre.lastIndexOf('.');
        if (ultimoPunto > 0 && ultimoPunto < nombre.length() - 1) {
            return nombre.substring(ultimoPunto + 1);
        }
        return "";
    }
    
    /**
     * Redimensiona una imagen si excede las dimensiones máximas.
     * 
     * @param imagenOriginal imagen a redimensionar
     * @return imagen redimensionada o la original si no es necesario
     */
    private static BufferedImage redimensionarSiNecesario(BufferedImage imagenOriginal) {
        int anchoOriginal = imagenOriginal.getWidth();
        int altoOriginal = imagenOriginal.getHeight();
        
        // Si la imagen es menor que el máximo, no redimensionar
        if (anchoOriginal <= ANCHO_MAXIMO && altoOriginal <= ALTO_MAXIMO) {
            return imagenOriginal;
        }
        
        // Calcular nuevas dimensiones manteniendo proporción
        double ratio = Math.min(
            (double) ANCHO_MAXIMO / anchoOriginal,
            (double) ALTO_MAXIMO / altoOriginal
        );
        
        int nuevoAncho = (int) (anchoOriginal * ratio);
        int nuevoAlto = (int) (altoOriginal * ratio);
        
        // Crear imagen redimensionada
        BufferedImage imagenRedimensionada = new BufferedImage(
            nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_ARGB
        );
        
        java.awt.Graphics2D g2d = imagenRedimensionada.createGraphics();
        g2d.setRenderingHint(
            java.awt.RenderingHints.KEY_INTERPOLATION,
            java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );
        g2d.setRenderingHint(
            java.awt.RenderingHints.KEY_RENDERING,
            java.awt.RenderingHints.VALUE_RENDER_QUALITY
        );
        g2d.setRenderingHint(
            java.awt.RenderingHints.KEY_ANTIALIASING,
            java.awt.RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.drawImage(imagenOriginal, 0, 0, nuevoAncho, nuevoAlto, null);
        g2d.dispose();
        
        System.out.println("✓ Imagen redimensionada: " + anchoOriginal + "x" + altoOriginal + 
                          " → " + nuevoAncho + "x" + nuevoAlto);
        
        return imagenRedimensionada;
    }
    
    /**
     * Obtiene la imagen por defecto para productos sin imagen.
     * Genera una imagen placeholder si no hay una predefinida.
     * 
     * @return imagen por defecto
     */
    public static Image obtenerImagenPorDefecto() {
        // Primero intentar cargar desde recursos
        try {
            InputStream is = ImagenProductoUtil.class.getResourceAsStream("/com/mycompany/stockflow/IMG/producto-default.png");
            if (is != null) {
                Image imagen = new Image(is);
                if (!imagen.isError()) {
                    System.out.println("✓ Imagen por defecto cargada desde recursos");
                    return imagen;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠ No se encontró imagen por defecto en recursos, generando placeholder...");
        }
        
        // Si no existe, generar un placeholder
        return generarImagenPlaceholder();
    }
    
    /**
     * Genera una imagen placeholder moderna para productos sin imagen.
     * 
     * @return imagen placeholder
     */
    private static Image generarImagenPlaceholder() {
        int size = 400;
        WritableImage imagen = new WritableImage(size, size);
        PixelWriter writer = imagen.getPixelWriter();
        
        // Fondo degradado suave
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double factorY = (double) y / size;
                int r = (int) (245 + (225 - 245) * factorY);
                int g = (int) (247 + (235 - 247) * factorY);
                int b = (int) (250 + (245 - 250) * factorY);
                writer.setColor(x, y, Color.rgb(r, g, b));
            }
        }
        
        // Dibujar icono de imagen (rectángulo con montaña y sol)
        int centerX = size / 2;
        int centerY = size / 2;
        
        // Marco del icono
        int marcoSize = 180;
        int marcoX1 = centerX - marcoSize / 2;
        int marcoY1 = centerY - marcoSize / 2;
        int marcoX2 = centerX + marcoSize / 2;
        int marcoY2 = centerY + marcoSize / 2;
        
        // Dibujar marco
        for (int y = marcoY1; y < marcoY2; y++) {
            for (int x = marcoX1; x < marcoX2; x++) {
                if (x == marcoX1 || x == marcoX2 - 1 || y == marcoY1 || y == marcoY2 - 1) {
                    if (x >= 0 && x < size && y >= 0 && y < size) {
                        writer.setColor(x, y, Color.rgb(42, 82, 152));
                    }
                }
            }
        }
        
        // Sol (círculo)
        int solX = centerX + 40;
        int solY = centerY - 40;
        int solRadius = 20;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double distance = Math.sqrt(Math.pow(x - solX, 2) + Math.pow(y - solY, 2));
                if (distance <= solRadius) {
                    writer.setColor(x, y, Color.rgb(249, 168, 37));
                }
            }
        }
        
        // Montaña (triángulo)
        int montanaBase = marcoY2 - 20;
        int montanaAltura = 80;
        for (int y = montanaBase - montanaAltura; y < montanaBase; y++) {
            int ancho = (montanaBase - y) * 60 / montanaAltura;
            for (int x = centerX - ancho; x < centerX + ancho; x++) {
                if (x >= marcoX1 && x < marcoX2 && y >= marcoY1 && y < marcoY2) {
                    writer.setColor(x, y, Color.rgb(42, 82, 152));
                }
            }
        }
        
        System.out.println("✓ Imagen placeholder generada");
        return imagen;
    }
}