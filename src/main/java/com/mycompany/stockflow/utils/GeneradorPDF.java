package com.mycompany.stockflow.utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.SolidBorder;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Chart;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para generar reportes PDF con análisis y gráficas
 */
public class GeneradorPDF {
    
    private static final DeviceRgb COLOR_PRIMARIO = new DeviceRgb(30, 58, 138);
    private static final DeviceRgb COLOR_SECUNDARIO = new DeviceRgb(59, 130, 246);
    private static final DeviceRgb COLOR_TEXTO = new DeviceRgb(30, 41, 59);
    private static final DeviceRgb COLOR_GRIS = new DeviceRgb(100, 116, 139);
    private static final DeviceRgb COLOR_FONDO = new DeviceRgb(248, 250, 252);
    
    /**
     * Genera un reporte PDF completo con análisis de IA y gráficas
     */
    public void generarReporteCompleto(
            String rutaArchivo,
            String tipoAnalisis,
            String analisisTexto,
            String metricas,
            List<Chart> graficas) throws Exception {
        
        PdfWriter writer = new PdfWriter(rutaArchivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(40, 40, 40, 40);
        
        // Encabezado principal
        agregarEncabezado(document);
        
        // Información del reporte
        agregarInfoReporte(document, tipoAnalisis);
        
        // Línea divisoria
        agregarLinea(document);
        
        // Análisis de IA
        if (analisisTexto != null && !analisisTexto.isEmpty()) {
            agregarSeccionAnalisis(document, analisisTexto);
            document.add(new Paragraph("\n"));
        }
        
        // Métricas
        if (metricas != null && !metricas.isEmpty()) {
            agregarSeccionMetricas(document, metricas);
        }
        
        // Gráficas en nueva página
        if (graficas != null && !graficas.isEmpty()) {
            document.add(new AreaBreak());
            agregarSeccionGraficas(document, graficas);
        }
        
        // Pie de página con número de páginas
        agregarPiePagina(document, pdf);
        
        document.close();
    }
    
    /**
     * Genera un reporte simple sin gráficas
     */
    public void generarReporteSimple(
            String rutaArchivo,
            String tipoAnalisis,
            String analisisTexto,
            String metricas) throws Exception {
        
        generarReporteCompleto(rutaArchivo, tipoAnalisis, analisisTexto, metricas, null);
    }
    
    /**
     * Agrega el encabezado principal del PDF
     */
    private void agregarEncabezado(Document document) {
        // Título principal
        Paragraph titulo = new Paragraph("STOCKFLOW")
                .setFontSize(32)
                .setBold()
                .setFontColor(COLOR_PRIMARIO)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        
        // Subtítulo
        Paragraph subtitulo = new Paragraph("Sistema de Gestión de Inventario e Inteligencia de Negocios")
                .setFontSize(11)
                .setFontColor(COLOR_GRIS)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        
        document.add(titulo);
        document.add(subtitulo);
    }
    
    /**
     * Agrega información del reporte con diseño tipo tarjeta
     */
    private void agregarInfoReporte(Document document, String tipoAnalisis) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        // Tabla para información del reporte
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.setBackgroundColor(COLOR_FONDO);
        tabla.setPadding(15);
        tabla.setMarginBottom(20);
        
        // Tipo de análisis
        tabla.addCell(crearCeldaEtiqueta("Tipo de Análisis:"));
        tabla.addCell(crearCeldaValor(tipoAnalisis));
        
        // Fecha de generación
        tabla.addCell(crearCeldaEtiqueta("Fecha de Generación:"));
        tabla.addCell(crearCeldaValor(ahora.format(formatter)));
        
        // Usuario (si existe sesión)
        tabla.addCell(crearCeldaEtiqueta("Generado por:"));
        tabla.addCell(crearCeldaValor(obtenerUsuarioActual()));
        
        document.add(tabla);
    }
    
    /**
     * Crea una celda de etiqueta con estilo
     */
    private Cell crearCeldaEtiqueta(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setBold().setFontSize(10).setFontColor(COLOR_GRIS))
                .setBorder(null)
                .setPadding(5);
    }
    
    /**
     * Crea una celda de valor con estilo
     */
    private Cell crearCeldaValor(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setFontSize(10).setFontColor(COLOR_TEXTO))
                .setBorder(null)
                .setPadding(5);
    }
    
    /**
     * Agrega una línea divisoria
     */
    private void agregarLinea(Document document) {
        LineSeparator linea = new LineSeparator(new SolidLine());
        linea.setMarginTop(10);
        linea.setMarginBottom(15);
        document.add(linea);
    }
    
    /**
     * Agrega la sección de análisis de IA
     */
    private void agregarSeccionAnalisis(Document document, String analisisTexto) {
        // Título de sección
        Paragraph titulo = new Paragraph("ANÁLISIS DE INTELIGENCIA ARTIFICIAL")
                .setFontSize(16)
                .setBold()
                .setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(10);
        
        document.add(titulo);
        
        // Limpiar el texto Markdown antes de agregarlo
        String textoLimpio = limpiarMarkdown(analisisTexto);
        
        // Contenedor con fondo para el análisis
        Div contenedor = new Div();
        contenedor.setBackgroundColor(COLOR_FONDO);
        contenedor.setPadding(15);
        contenedor.setMarginBottom(15);
        contenedor.setBorder(new SolidBorder(COLOR_SECUNDARIO, 1));
        
        // Texto del análisis con formato
        String[] parrafos = textoLimpio.split("\n");
        for (String parrafo : parrafos) {
            if (!parrafo.trim().isEmpty()) {
                Paragraph p = new Paragraph(parrafo)
                        .setFontSize(10)
                        .setFontColor(COLOR_TEXTO)
                        .setTextAlignment(TextAlignment.JUSTIFIED)
                        .setMarginBottom(8);
                contenedor.add(p);
            }
        }
        
        document.add(contenedor);
    }
    
    /**
     * Limpia el formato Markdown del texto de la IA
     */
    private String limpiarMarkdown(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        
        StringBuilder resultado = new StringBuilder();
        String[] lineas = texto.split("\n");
        
        for (String linea : lineas) {
            String lineaLimpia = linea;
            
            // Eliminar asteriscos de negrita/cursiva
            lineaLimpia = lineaLimpia.replaceAll("\\*\\*\\*(.+?)\\*\\*\\*", "$1");
            lineaLimpia = lineaLimpia.replaceAll("\\*\\*(.+?)\\*\\*", "$1");
            lineaLimpia = lineaLimpia.replaceAll("\\*(.+?)\\*", "$1");
            
            // Convertir encabezados Markdown
            if (lineaLimpia.startsWith("####")) {
                lineaLimpia = "    " + lineaLimpia.replaceFirst("####\\s*", "");
            } else if (lineaLimpia.startsWith("###")) {
                lineaLimpia = "  • " + lineaLimpia.replaceFirst("###\\s*", "");
            } else if (lineaLimpia.startsWith("##")) {
                lineaLimpia = "\n" + lineaLimpia.replaceFirst("##\\s*", "");
            } else if (lineaLimpia.startsWith("#")) {
                lineaLimpia = "\n" + lineaLimpia.replaceFirst("#\\s*", "");
            }
            
            // Convertir listas
            if (lineaLimpia.trim().startsWith("-")) {
                lineaLimpia = lineaLimpia.replaceFirst("-\\s*", "  • ");
            }
            
            // Eliminar emojis
            lineaLimpia = lineaLimpia.replaceAll("🔴|⚠️|✅|📊|💡|🎯|🔄|⚡", "");
            
            resultado.append(lineaLimpia).append("\n");
        }
        
        return resultado.toString().trim();
    }
    
    /**
     * Agrega la sección de métricas
     */
    private void agregarSeccionMetricas(Document document, String metricas) {
        // Título de sección
        Paragraph titulo = new Paragraph("MÉTRICAS Y ESTADÍSTICAS")
                .setFontSize(16)
                .setBold()
                .setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(10);
        
        document.add(titulo);
        
        // Contenedor para las métricas
        Div contenedor = new Div();
        contenedor.setBackgroundColor(ColorConstants.WHITE);
        contenedor.setPadding(15);
        contenedor.setMarginBottom(15);
        contenedor.setBorder(new SolidBorder(COLOR_GRIS, 1));
        
        // Parsear y formatear métricas
        String[] lineas = metricas.split("\n");
        for (String linea : lineas) {
            if (!linea.trim().isEmpty()) {
                Paragraph p;
                
                // Detectar encabezados
                if (linea.contains("===") || linea.contains("---")) {
                    continue; // Saltar líneas decorativas
                } else if (linea.matches("^[A-Z][A-Z\\s]+$")) {
                    // Encabezado en mayúsculas
                    p = new Paragraph(linea)
                            .setFontSize(12)
                            .setBold()
                            .setFontColor(COLOR_SECUNDARIO)
                            .setMarginTop(8)
                            .setMarginBottom(5);
                } else if (linea.contains(":")) {
                    // Línea métrica: etiqueta : valor
                    String[] partes = linea.split(":", 2);
                    if (partes.length == 2) {
                        Text etiqueta = new Text(partes[0] + ": ")
                                .setBold()
                                .setFontColor(COLOR_GRIS);
                        Text valor = new Text(partes[1].trim())
                                .setFontColor(COLOR_TEXTO);
                        p = new Paragraph()
                                .add(etiqueta)
                                .add(valor)
                                .setFontSize(10)
                                .setMarginBottom(4);
                    } else {
                        p = new Paragraph(linea)
                                .setFontSize(10)
                                .setFontColor(COLOR_TEXTO)
                                .setMarginBottom(4);
                    }
                } else {
                    p = new Paragraph(linea)
                            .setFontSize(10)
                            .setFontColor(COLOR_TEXTO)
                            .setMarginBottom(4);
                }
                
                contenedor.add(p);
            }
        }
        
        document.add(contenedor);
    }
    
    /**
     * Agrega la sección de gráficas
     */
    private void agregarSeccionGraficas(Document document, List<Chart> graficas) throws Exception {
        // Título de sección
        Paragraph titulo = new Paragraph("VISUALIZACIONES Y GRÁFICAS")
                .setFontSize(16)
                .setBold()
                .setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(15);
        
        document.add(titulo);
        
        int contador = 1;
        int totalGraficas = graficas.size();
        
        for (int i = 0; i < totalGraficas; i++) {
            Chart chart = graficas.get(i);
            
            if (chart != null) {
                // Título de la gráfica
                String nombreGrafica = obtenerNombreGrafica(chart);
                Paragraph tituloGrafica = new Paragraph("Figura " + contador + ": " + nombreGrafica)
                        .setFontSize(11)
                        .setBold()
                        .setFontColor(COLOR_SECUNDARIO)
                        .setMarginBottom(8);
                
                document.add(tituloGrafica);
                
                // Convertir gráfica a imagen
                byte[] imagenBytes = convertirGraficaAImagen(chart);
                ImageData imageData = ImageDataFactory.create(imagenBytes);
                Image imagen = new Image(imageData);
                
                // Ajustar tamaño de la imagen
                float anchoMaximo = document.getPdfDocument().getDefaultPageSize().getWidth() - 80;
                imagen.setAutoScale(true);
                imagen.setWidth(anchoMaximo);
                imagen.setMarginBottom(20);
                
                document.add(imagen);
                
                contador++;
                
                // Salto de página cada 2 gráficas (excepto la última)
                if (contador % 2 == 1 && i < totalGraficas - 1) {
                    document.add(new AreaBreak());
                }
            }
        }
    }
    
    /**
     * Convierte una gráfica de JavaFX a bytes de imagen PNG
     */
    private byte[] convertirGraficaAImagen(Chart chart) throws Exception {
        SnapshotParameters params = new SnapshotParameters();
        WritableImage snapshot = chart.snapshot(params, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }
    
    /**
     * Obtiene el nombre de la gráfica desde su título
     */
    private String obtenerNombreGrafica(Chart chart) {
        String titulo = chart.getTitle();
        if (titulo == null || titulo.isEmpty()) {
            return "Gráfica";
        }
        return titulo;
    }
    
    /**
     * Agrega pie de página con información adicional
     */
    private void agregarPiePagina(Document document, PdfDocument pdf) {
        int numeroPaginas = pdf.getNumberOfPages();
        
        document.add(new Paragraph("\n"));
        agregarLinea(document);
        
        Table tablaPie = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        tablaPie.setWidth(UnitValue.createPercentValue(100));
        
        // Información izquierda
        Cell celdaIzq = new Cell()
                .add(new Paragraph("STOCKFLOW - Reporte Generado Automáticamente")
                        .setFontSize(8)
                        .setFontColor(COLOR_GRIS))
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT);
        
        // Información derecha
        Cell celdaDer = new Cell()
                .add(new Paragraph("Total de páginas: " + numeroPaginas)
                        .setFontSize(8)
                        .setFontColor(COLOR_GRIS))
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT);
        
        tablaPie.addCell(celdaIzq);
        tablaPie.addCell(celdaDer);
        
        document.add(tablaPie);
    }
    
    /**
     * Obtiene el usuario actual de la sesión
     */
    private String obtenerUsuarioActual() {
        try {
            if (SesionUsuario.getInstancia() != null && 
                SesionUsuario.getInstancia().getUsuarioActual() != null) {
                // Usar getUsername() o getNombreCompleto() según lo que tengas en Usuario
                String username = SesionUsuario.getInstancia().getUsuarioActual().getUsername();
                return username != null ? username : "Admin";
            }
        } catch (Exception e) {
            // Si no hay sesión activa o hay algún error
        }
        return "Admin";
    }
}