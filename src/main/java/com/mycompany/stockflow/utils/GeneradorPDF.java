package com.mycompany.stockflow.utils;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
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
 * Servicio para generar reportes PDF profesionales con análisis y gráficas
 */
public class GeneradorPDF {
    
    // Paleta de colores corporativa
    private static final DeviceRgb COLOR_PRIMARIO = new DeviceRgb(30, 58, 138);
    private static final DeviceRgb COLOR_SECUNDARIO = new DeviceRgb(59, 130, 246);
    private static final DeviceRgb COLOR_ACENTO = new DeviceRgb(16, 185, 129);
    private static final DeviceRgb COLOR_TEXTO = new DeviceRgb(30, 41, 59);
    private static final DeviceRgb COLOR_GRIS = new DeviceRgb(100, 116, 139);
    private static final DeviceRgb COLOR_GRIS_CLARO = new DeviceRgb(203, 213, 225);
    private static final DeviceRgb COLOR_FONDO = new DeviceRgb(248, 250, 252);
    
    // Fuentes profesionales
    private PdfFont fuenteTitulo;
    private PdfFont fuenteNormal;
    private PdfFont fuenteNegrita;
    
    /**
     * Constructor que inicializa las fuentes
     */
    public GeneradorPDF() {
        try {
            fuenteTitulo = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            fuenteNormal = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            fuenteNegrita = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        } catch (Exception e) {
            System.err.println("Error cargando fuentes: " + e.getMessage());
        }
    }
    
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
        document.setMargins(50, 50, 50, 50);
        document.setFont(fuenteNormal);
        
        // PÁGINA DE PORTADA
        agregarPortada(document, tipoAnalisis);
        document.add(new AreaBreak());
        
        // CONTENIDO PRINCIPAL
        agregarEncabezadoSeccion(document);
        agregarInfoReporte(document, tipoAnalisis);
        agregarLineaDivisoria(document);
        
        // Análisis de IA
        if (analisisTexto != null && !analisisTexto.isEmpty()) {
            agregarSeccionAnalisis(document, analisisTexto);
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
        
        // CRÍTICO: Agregar números de página ANTES de cerrar el documento
        int totalPaginas = pdf.getNumberOfPages();
        agregarNumerosPagina(pdf, totalPaginas);
        
        // Cerrar documento
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
     * Genera un reporte completo usando bytes de imágenes en lugar de Charts
     * Este método es thread-safe y evita problemas con JavaFX threading
     */
    public void generarReporteCompletoConBytes(
            String rutaArchivo,
            String tipoAnalisis,
            String analisisTexto,
            String metricas,
            List<byte[]> graficasBytes,
            List<String> nombresGraficas) throws Exception {
        
        PdfWriter writer = new PdfWriter(rutaArchivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(50, 50, 50, 50);
        document.setFont(fuenteNormal);
        
        // PÁGINA DE PORTADA
        agregarPortada(document, tipoAnalisis);
        document.add(new AreaBreak());
        
        // CONTENIDO PRINCIPAL
        agregarEncabezadoSeccion(document);
        agregarInfoReporte(document, tipoAnalisis);
        agregarLineaDivisoria(document);
        
        // Análisis de IA
        if (analisisTexto != null && !analisisTexto.isEmpty()) {
            agregarSeccionAnalisis(document, analisisTexto);
        }
        
        // Métricas
        if (metricas != null && !metricas.isEmpty()) {
            agregarSeccionMetricas(document, metricas);
        }
        
        // Gráficas en nueva página (usando bytes)
        if (graficasBytes != null && !graficasBytes.isEmpty()) {
            document.add(new AreaBreak());
            agregarSeccionGraficasDesdeBytes(document, graficasBytes, nombresGraficas);
        }
        
        // CRÍTICO: Agregar números de página ANTES de cerrar el documento
        int totalPaginas = pdf.getNumberOfPages();
        agregarNumerosPagina(pdf, totalPaginas);
        
        // Cerrar documento
        document.close();
    }
    
    /**
     * Crea una portada profesional para el reporte
     */
    private void agregarPortada(Document document, String tipoAnalisis) {
        document.add(new Paragraph("\n\n\n\n\n"));
        
        Paragraph titulo = new Paragraph("STOCKFLOW")
                .setFont(fuenteTitulo)
                .setFontSize(48)
                .setFontColor(COLOR_PRIMARIO)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        
        Paragraph subtitulo = new Paragraph("Sistema de Gestión de Inventario")
                .setFont(fuenteNormal)
                .setFontSize(18)
                .setFontColor(COLOR_GRIS)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(80);
        
        document.add(titulo);
        document.add(subtitulo);
        
        Div cajaPortada = new Div();
        cajaPortada.setBackgroundColor(COLOR_PRIMARIO);
        cajaPortada.setPadding(30);
        cajaPortada.setMarginTop(40);
        cajaPortada.setMarginBottom(40);
        
        Paragraph tipoReporte = new Paragraph("REPORTE DE " + tipoAnalisis.toUpperCase())
                .setFont(fuenteNegrita)
                .setFontSize(20)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        
        cajaPortada.add(tipoReporte);
        
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
        
        Paragraph fecha = new Paragraph(ahora.format(formatter))
                .setFont(fuenteNormal)
                .setFontSize(14)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
        
        cajaPortada.add(fecha);
        document.add(cajaPortada);
        document.add(new Paragraph("\n\n\n\n\n\n"));
        
        Paragraph piePortada = new Paragraph("Documento Confidencial - Uso Interno")
                .setFont(fuenteNormal)
                .setFontSize(10)
                .setFontColor(COLOR_GRIS)
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic();
        
        document.add(piePortada);
    }
    
    /**
     * Agrega un encabezado de sección en cada página de contenido
     */
    private void agregarEncabezadoSeccion(Document document) {
        Table tablaEncabezado = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
        tablaEncabezado.setWidth(UnitValue.createPercentValue(100));
        tablaEncabezado.setMarginBottom(20);
        
        Cell celdaTitulo = new Cell()
                .add(new Paragraph("STOCKFLOW")
                        .setFont(fuenteNegrita)
                        .setFontSize(16)
                        .setFontColor(COLOR_PRIMARIO))
                .setBorder(null)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        Cell celdaFecha = new Cell()
                .add(new Paragraph(ahora.format(formatter))
                        .setFont(fuenteNormal)
                        .setFontSize(10)
                        .setFontColor(COLOR_GRIS))
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        
        tablaEncabezado.addCell(celdaTitulo);
        tablaEncabezado.addCell(celdaFecha);
        document.add(tablaEncabezado);
        
        LineSeparator linea = new LineSeparator(new SolidLine(1f));
        linea.setStrokeColor(COLOR_PRIMARIO);
        linea.setMarginBottom(20);
        document.add(linea);
    }
    
    /**
     * Agrega información del reporte en formato tabla profesional
     */
    private void agregarInfoReporte(Document document, String tipoAnalisis) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        
        Paragraph titulo = new Paragraph("Información del Reporte")
                .setFont(fuenteNegrita)
                .setFontSize(14)
                .setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(15);
        
        document.add(titulo);
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.setMarginBottom(25);
        
        agregarFilaInfo(tabla, "Tipo de Análisis:", tipoAnalisis);
        agregarFilaInfo(tabla, "Fecha y Hora:", ahora.format(formatter));
        agregarFilaInfo(tabla, "Generado por:", obtenerUsuarioActual());
        agregarFilaInfo(tabla, "Estado:", "Completado");
        
        document.add(tabla);
    }
    
    /**
     * Agrega una fila a la tabla de información
     */
    private void agregarFilaInfo(Table tabla, String etiqueta, String valor) {
        Cell celdaEtiqueta = new Cell()
                .add(new Paragraph(etiqueta)
                        .setFont(fuenteNegrita)
                        .setFontSize(11)
                        .setFontColor(COLOR_TEXTO))
                .setBackgroundColor(COLOR_FONDO)
                .setBorder(new SolidBorder(COLOR_GRIS_CLARO, 0.5f))
                .setPadding(10);
        
        Cell celdaValor = new Cell()
                .add(new Paragraph(valor)
                        .setFont(fuenteNormal)
                        .setFontSize(11)
                        .setFontColor(COLOR_TEXTO))
                .setBorder(new SolidBorder(COLOR_GRIS_CLARO, 0.5f))
                .setPadding(10);
        
        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }
    
    /**
     * Agrega una línea divisoria elegante
     */
    private void agregarLineaDivisoria(Document document) {
        LineSeparator linea = new LineSeparator(new SolidLine(1.5f));
        linea.setStrokeColor(COLOR_SECUNDARIO);
        linea.setMarginTop(15);
        linea.setMarginBottom(20);
        document.add(linea);
    }
    
    /**
     * Agrega la sección de análisis de IA con formato profesional
     */
    private void agregarSeccionAnalisis(Document document, String analisisTexto) {
        Paragraph titulo = new Paragraph("ANÁLISIS DE INTELIGENCIA ARTIFICIAL")
                .setFont(fuenteNegrita)
                .setFontSize(14)
                .setFontColor(COLOR_PRIMARIO)
                .setMarginTop(10)
                .setMarginBottom(15);
        
        document.add(titulo);
        
        String textoLimpio = limpiarMarkdown(analisisTexto);
        
        Div contenedor = new Div();
        contenedor.setBackgroundColor(ColorConstants.WHITE);
        contenedor.setBorder(new SolidBorder(COLOR_GRIS_CLARO, 1));
        contenedor.setPadding(20);
        contenedor.setMarginBottom(25);
        
        String[] parrafos = textoLimpio.split("\n\n");
        
        for (String parrafo : parrafos) {
            if (!parrafo.trim().isEmpty()) {
                Paragraph p = new Paragraph(parrafo.trim())
                        .setFont(fuenteNormal)
                        .setFontSize(12)
                        .setFontColor(COLOR_TEXTO)
                        .setTextAlignment(TextAlignment.JUSTIFIED)
                        .setMarginBottom(12)
                        .setFixedLeading(18);
                
                contenedor.add(p);
            }
        }
        
        document.add(contenedor);
    }
    
    /**
     * Limpia el formato Markdown del texto
     */
    private String limpiarMarkdown(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        
        StringBuilder resultado = new StringBuilder();
        String[] lineas = texto.split("\n");
        boolean enParrafo = false;
        
        for (String linea : lineas) {
            String lineaLimpia = linea;
            
            lineaLimpia = lineaLimpia.replaceAll("\\*\\*\\*(.+?)\\*\\*\\*", "$1");
            lineaLimpia = lineaLimpia.replaceAll("\\*\\*(.+?)\\*\\*", "$1");
            lineaLimpia = lineaLimpia.replaceAll("\\*(.+?)\\*", "$1");
            
            if (lineaLimpia.startsWith("####")) {
                if (enParrafo) resultado.append("\n\n");
                resultado.append("    ").append(lineaLimpia.replaceFirst("####\\s*", "")).append("\n");
                enParrafo = false;
            } else if (lineaLimpia.startsWith("###")) {
                if (enParrafo) resultado.append("\n\n");
                resultado.append("  • ").append(lineaLimpia.replaceFirst("###\\s*", "")).append("\n");
                enParrafo = false;
            } else if (lineaLimpia.startsWith("##")) {
                if (enParrafo) resultado.append("\n\n");
                resultado.append("\n").append(lineaLimpia.replaceFirst("##\\s*", "")).append("\n");
                enParrafo = false;
            } else if (lineaLimpia.startsWith("#")) {
                if (enParrafo) resultado.append("\n\n");
                resultado.append("\n").append(lineaLimpia.replaceFirst("#\\s*", "")).append("\n");
                enParrafo = false;
            } else if (lineaLimpia.trim().startsWith("-")) {
                lineaLimpia = lineaLimpia.replaceFirst("-\\s*", "  • ");
                resultado.append(lineaLimpia).append("\n");
                enParrafo = false;
            } else if (lineaLimpia.trim().isEmpty()) {
                if (enParrafo) {
                    resultado.append("\n\n");
                    enParrafo = false;
                }
            } else {
                if (enParrafo) resultado.append(" ");
                resultado.append(lineaLimpia);
                enParrafo = true;
            }
            
            String temp = resultado.toString();
            temp = temp.replaceAll("🔴|⚠️|✅|📊|💡|🎯|🔄|⚡|📈|📉|🔍|💰|📋", "");
            resultado = new StringBuilder(temp);
        }
        
        return resultado.toString().trim();
    }
    
    /**
     * Agrega la sección de métricas con diseño tipo tabla
     */
    private void agregarSeccionMetricas(Document document, String metricas) {
        Paragraph titulo = new Paragraph("MÉTRICAS Y ESTADÍSTICAS CLAVE")
                .setFont(fuenteNegrita)
                .setFontSize(14)
                .setFontColor(COLOR_PRIMARIO)
                .setMarginTop(10)
                .setMarginBottom(15);
        
        document.add(titulo);
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1, 1.5f}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.setMarginBottom(25);
        
        String[] lineas = metricas.split("\n");
        
        for (String linea : lineas) {
            if (linea.trim().isEmpty() || linea.contains("===") || linea.contains("---")) {
                continue;
            }
            
            if (linea.matches("^[A-ZÁÉÍÓÚ][A-ZÁÉÍÓÚ\\s]+$")) {
                Cell celdaEncabezado = new Cell(1, 2)
                        .add(new Paragraph(linea.trim())
                                .setFont(fuenteNegrita)
                                .setFontSize(11)
                                .setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(COLOR_SECUNDARIO)
                        .setPadding(8)
                        .setBorder(null);
                
                tabla.addCell(celdaEncabezado);
                
            } else if (linea.contains(":")) {
                String[] partes = linea.split(":", 2);
                if (partes.length == 2) {
                    Cell celdaEtiqueta = new Cell()
                            .add(new Paragraph(partes[0].trim())
                                    .setFont(fuenteNormal)
                                    .setFontSize(11)
                                    .setFontColor(COLOR_TEXTO))
                            .setBackgroundColor(COLOR_FONDO)
                            .setBorder(new SolidBorder(COLOR_GRIS_CLARO, 0.5f))
                            .setPadding(8);
                    
                    Cell celdaValor = new Cell()
                            .add(new Paragraph(partes[1].trim())
                                    .setFont(fuenteNegrita)
                                    .setFontSize(11)
                                    .setFontColor(COLOR_PRIMARIO))
                            .setBorder(new SolidBorder(COLOR_GRIS_CLARO, 0.5f))
                            .setPadding(8);
                    
                    tabla.addCell(celdaEtiqueta);
                    tabla.addCell(celdaValor);
                }
            }
        }
        
        document.add(tabla);
    }
    
    /**
     * Agrega la sección de gráficas
     */
    private void agregarSeccionGraficas(Document document, List<Chart> graficas) throws Exception {
        Paragraph titulo = new Paragraph("ANÁLISIS GRÁFICO Y VISUALIZACIONES")
                .setFont(fuenteNegrita)
                .setFontSize(14)
                .setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(20);
        
        document.add(titulo);
        
        int contador = 1;
        int totalGraficas = graficas.size();
        
        for (int i = 0; i < totalGraficas; i++) {
            Chart chart = graficas.get(i);
            
            if (chart != null) {
                Div contenedorGrafica = new Div();
                contenedorGrafica.setBorder(new SolidBorder(COLOR_GRIS_CLARO, 1));
                contenedorGrafica.setPadding(15);
                contenedorGrafica.setMarginBottom(25);
                contenedorGrafica.setBackgroundColor(ColorConstants.WHITE);
                
                String nombreGrafica = obtenerNombreGrafica(chart);
                Paragraph tituloGrafica = new Paragraph("Figura " + contador + ": " + nombreGrafica)
                        .setFont(fuenteNegrita)
                        .setFontSize(11)
                        .setFontColor(COLOR_SECUNDARIO)
                        .setMarginBottom(12)
                        .setTextAlignment(TextAlignment.CENTER);
                
                contenedorGrafica.add(tituloGrafica);
                
                byte[] imagenBytes = convertirGraficaAImagen(chart);
                ImageData imageData = ImageDataFactory.create(imagenBytes);
                Image imagen = new Image(imageData);
                
                float anchoMaximo = document.getPdfDocument().getDefaultPageSize().getWidth() - 130;
                imagen.setAutoScale(true);
                imagen.setWidth(anchoMaximo);
                imagen.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                
                contenedorGrafica.add(imagen);
                document.add(contenedorGrafica);
                
                contador++;
                
                if (i % 2 == 1 && i < totalGraficas - 1) {
                    document.add(new AreaBreak());
                }
            }
        }
    }
    
    /**
     * Agrega la sección de gráficas desde bytes de imágenes
     * Método thread-safe que no requiere acceso a objetos JavaFX
     */
    private void agregarSeccionGraficasDesdeBytes(
            Document document, 
            List<byte[]> graficasBytes, 
            List<String> nombresGraficas) throws Exception {
        
        Paragraph titulo = new Paragraph("ANÁLISIS GRÁFICO Y VISUALIZACIONES")
                .setFont(fuenteNegrita)
                .setFontSize(14)
                .setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(20);
        
        document.add(titulo);
        
        int totalGraficas = graficasBytes.size();
        
        for (int i = 0; i < totalGraficas; i++) {
            byte[] imagenBytes = graficasBytes.get(i);
            String nombreGrafica = (nombresGraficas != null && i < nombresGraficas.size()) 
                ? nombresGraficas.get(i) 
                : "Gráfica de Análisis";
            
            if (imagenBytes != null && imagenBytes.length > 0) {
                Div contenedorGrafica = new Div();
                contenedorGrafica.setBorder(new SolidBorder(COLOR_GRIS_CLARO, 1));
                contenedorGrafica.setPadding(15);
                contenedorGrafica.setMarginBottom(25);
                contenedorGrafica.setBackgroundColor(ColorConstants.WHITE);
                
                Paragraph tituloGrafica = new Paragraph("Figura " + (i + 1) + ": " + nombreGrafica)
                        .setFont(fuenteNegrita)
                        .setFontSize(11)
                        .setFontColor(COLOR_SECUNDARIO)
                        .setMarginBottom(12)
                        .setTextAlignment(TextAlignment.CENTER);
                
                contenedorGrafica.add(tituloGrafica);
                
                ImageData imageData = ImageDataFactory.create(imagenBytes);
                Image imagen = new Image(imageData);
                
                float anchoMaximo = document.getPdfDocument().getDefaultPageSize().getWidth() - 130;
                imagen.setAutoScale(true);
                imagen.setWidth(anchoMaximo);
                imagen.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                
                contenedorGrafica.add(imagen);
                document.add(contenedorGrafica);
                
                if (i % 2 == 1 && i < totalGraficas - 1) {
                    document.add(new AreaBreak());
                }
            }
        }
    }
    
    /**
     * Convierte gráfica JavaFX a imagen PNG
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
     * Obtiene el nombre de la gráfica
     */
    private String obtenerNombreGrafica(Chart chart) {
        String titulo = chart.getTitle();
        return (titulo == null || titulo.isEmpty()) ? "Gráfica de Análisis" : titulo;
    }
    
    /**
     * Agrega números de página a todas las páginas excepto la portada
     * CRÍTICO: Debe llamarse ANTES de cerrar el documento
     */
    private void agregarNumerosPagina(PdfDocument pdf, int totalPaginas) {
        try {
            for (int i = 2; i <= totalPaginas; i++) {
                com.itextpdf.kernel.pdf.canvas.PdfCanvas canvas = 
                    new com.itextpdf.kernel.pdf.canvas.PdfCanvas(
                        pdf.getPage(i).newContentStreamBefore(), 
                        pdf.getPage(i).getResources(), 
                        pdf);
                
                // Footer izquierdo
                canvas.beginText()
                    .setFontAndSize(fuenteNormal, 8)
                    .setColor(COLOR_GRIS, true)
                    .moveText(50, 30)
                    .showText("STOCKFLOW")
                    .endText();
                
                // Footer centro - número de página
                String textoPagina = "Página " + (i - 1) + " de " + (totalPaginas - 1);
                float anchoPagina = pdf.getPage(i).getPageSize().getWidth();
                float anchoTexto = fuenteNormal.getWidth(textoPagina, 8);
                float xCentro = (anchoPagina - anchoTexto) / 2;
                
                canvas.beginText()
                    .setFontAndSize(fuenteNormal, 8)
                    .setColor(COLOR_GRIS, true)
                    .moveText(xCentro, 30)
                    .showText(textoPagina)
                    .endText();
                
                // Footer derecho
                String textoConfidencial = "Confidencial";
                float anchoConfidencial = fuenteNormal.getWidth(textoConfidencial, 8);
                
                canvas.beginText()
                    .setFontAndSize(fuenteNormal, 8)
                    .setColor(COLOR_GRIS, true)
                    .moveText(anchoPagina - 50 - anchoConfidencial, 30)
                    .showText(textoConfidencial)
                    .endText();
            }
        } catch (Exception e) {
            System.err.println("Error agregando números de página: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene el usuario actual de la sesión
     */
    private String obtenerUsuarioActual() {
        try {
            if (SesionUsuario.getInstancia() != null && 
                SesionUsuario.getInstancia().getUsuarioActual() != null) {
                String username = SesionUsuario.getInstancia().getUsuarioActual().getUsername();
                return username != null ? username : "Administrador del Sistema";
            }
        } catch (Exception e) {
            // Si no hay sesión activa
        }
        return "Administrador del Sistema";
    }
}