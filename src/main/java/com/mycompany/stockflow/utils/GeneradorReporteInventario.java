/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Modelo.MovimientoInventario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generador de reportes de inventario en PDF
 */
public class GeneradorReporteInventario {
    
    // Colores corporativos
    private static final DeviceRgb COLOR_PRIMARIO = new DeviceRgb(30, 58, 138);
    private static final DeviceRgb COLOR_SECUNDARIO = new DeviceRgb(59, 130, 246);
    private static final DeviceRgb COLOR_ACENTO = new DeviceRgb(16, 185, 129);
    private static final DeviceRgb COLOR_TEXTO = new DeviceRgb(30, 41, 59);
    private static final DeviceRgb COLOR_GRIS = new DeviceRgb(100, 116, 139);
    private static final DeviceRgb COLOR_GRIS_CLARO = new DeviceRgb(203, 213, 225);
    private static final DeviceRgb COLOR_FONDO = new DeviceRgb(248, 250, 252);
    private static final DeviceRgb COLOR_ROJO = new DeviceRgb(198, 40, 40);
    private static final DeviceRgb COLOR_AMARILLO = new DeviceRgb(230, 81, 0);
    private static final DeviceRgb COLOR_VERDE = new DeviceRgb(46, 125, 50);
    
    private PdfFont fuenteTitulo;
    private PdfFont fuenteNormal;
    private PdfFont fuenteNegrita;
    
    public GeneradorReporteInventario() {
        try {
            fuenteTitulo = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            fuenteNormal = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            fuenteNegrita = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        } catch (Exception e) {
            System.err.println("Error cargando fuentes: " + e.getMessage());
        }
    }
    
    /**
     * Genera reporte completo de inventario
     */
    public void generarReporteCompleto(
            String rutaArchivo,
            List<Producto> productos,
            List<Producto> productosStockBajo,
            List<MovimientoInventario> movimientos) throws Exception {
        
        PdfWriter writer = new PdfWriter(rutaArchivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(50, 50, 50, 50);
        document.setFont(fuenteNormal);
        
        // PORTADA
        agregarPortada(document);
        document.add(new AreaBreak());
        
        // RESUMEN EJECUTIVO
        agregarEncabezado(document);
        agregarResumenEjecutivo(document, productos, productosStockBajo, movimientos);
        
        // PRODUCTOS CON STOCK CRÍTICO
        if (!productosStockBajo.isEmpty()) {
            document.add(new AreaBreak());
            agregarSeccionStockCritico(document, productosStockBajo);
        }
        
        // INVENTARIO COMPLETO
        document.add(new AreaBreak());
        agregarSeccionInventarioCompleto(document, productos);
        
        // MOVIMIENTOS RECIENTES
        if (!movimientos.isEmpty()) {
            document.add(new AreaBreak());
            agregarSeccionMovimientos(document, movimientos);
        }
        
        // Números de página
        int totalPaginas = pdf.getNumberOfPages();
        agregarNumerosPagina(pdf, totalPaginas);
        
        document.close();
    }
    
    /**
     * Portada del reporte
     */
    private void agregarPortada(Document document) {
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
        
        Paragraph tipoReporte = new Paragraph("REPORTE DE CONTROL DE INVENTARIO")
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
    }
    
    /**
     * Encabezado de página
     */
    private void agregarEncabezado(Document document) {
        Table tablaEncabezado = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
        tablaEncabezado.setWidth(UnitValue.createPercentValue(100));
        tablaEncabezado.setMarginBottom(20);
        
        Cell celdaTitulo = new Cell()
                .add(new Paragraph("STOCKFLOW - Control de Inventario")
                        .setFont(fuenteNegrita)
                        .setFontSize(14)
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
     * Resumen ejecutivo con estadísticas
     */
    private void agregarResumenEjecutivo(Document document, List<Producto> productos,
                                         List<Producto> productosStockBajo,
                                         List<MovimientoInventario> movimientos) {
        
        Paragraph titulo = new Paragraph("RESUMEN EJECUTIVO")
                .setFont(fuenteNegrita)
                .setFontSize(16)
                .setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(15);
        document.add(titulo);
        
        // Calcular estadísticas
        int totalProductos = productos.size();
        int stockTotal = productos.stream().mapToInt(Producto::getStock).sum();
        double valorTotal = productos.stream()
                .mapToDouble(p -> p.getPrecioVenta() * p.getStock())
                .sum();
        int alertasCriticas = productosStockBajo.size();
        long movimientosHoy = movimientos.stream()
                .filter(m -> m.getFecha().isAfter(LocalDateTime.now().withHour(0).withMinute(0)))
                .count();
        
        // Tabla de estadísticas
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.setMarginBottom(20);
        
        agregarCeldaEstadistica(tabla, "Total Productos", String.valueOf(totalProductos), COLOR_SECUNDARIO);
        agregarCeldaEstadistica(tabla, "Stock Total", String.format("%,d unidades", stockTotal), COLOR_ACENTO);
        agregarCeldaEstadistica(tabla, "Valor Inventario", String.format("$%,.2f", valorTotal), COLOR_VERDE);
        agregarCeldaEstadistica(tabla, "Alertas Críticas", String.valueOf(alertasCriticas), COLOR_ROJO);
        
        document.add(tabla);
        
        // Información adicional
        Div infoBox = new Div();
        infoBox.setBackgroundColor(COLOR_FONDO);
        infoBox.setBorder(new SolidBorder(COLOR_GRIS_CLARO, 1));
        infoBox.setPadding(15);
        infoBox.setMarginTop(15);
        
        Paragraph info = new Paragraph()
                .add(new Text("Movimientos registrados hoy: ").setFont(fuenteNormal).setFontSize(11))
                .add(new Text(String.valueOf(movimientosHoy)).setFont(fuenteNegrita).setFontSize(11).setFontColor(COLOR_PRIMARIO))
                .add(new Text(" | ").setFont(fuenteNormal).setFontSize(11))
                .add(new Text("Total de movimientos: ").setFont(fuenteNormal).setFontSize(11))
                .add(new Text(String.valueOf(movimientos.size())).setFont(fuenteNegrita).setFontSize(11).setFontColor(COLOR_PRIMARIO));
        
        infoBox.add(info);
        document.add(infoBox);
    }
    
    /**
     * Celda de estadística
     */
    private void agregarCeldaEstadistica(Table tabla, String etiqueta, String valor, DeviceRgb color) {
        Cell celda = new Cell();
        celda.setBackgroundColor(ColorConstants.WHITE);
        celda.setBorder(new SolidBorder(COLOR_GRIS_CLARO, 1));
        celda.setPadding(15);
        celda.setTextAlignment(TextAlignment.CENTER);
        
        Paragraph lblEtiqueta = new Paragraph(etiqueta)
                .setFont(fuenteNormal)
                .setFontSize(10)
                .setFontColor(COLOR_GRIS)
                .setMarginBottom(5);
        
        Paragraph lblValor = new Paragraph(valor)
                .setFont(fuenteNegrita)
                .setFontSize(16)
                .setFontColor(color);
        
        celda.add(lblEtiqueta);
        celda.add(lblValor);
        tabla.addCell(celda);
    }
    
    /**
     * Sección de productos con stock crítico
     */
    private void agregarSeccionStockCritico(Document document, List<Producto> productosStockBajo) {
        agregarEncabezado(document);
        
        Paragraph titulo = new Paragraph("⚠ PRODUCTOS CON STOCK CRÍTICO")
                .setFont(fuenteNegrita)
                .setFontSize(16)
                .setFontColor(COLOR_ROJO)
                .setMarginBottom(15);
        document.add(titulo);
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1.5f, 3, 1.2f, 1.2f, 1.5f}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        agregarCeldaEncabezado(tabla, "Código");
        agregarCeldaEncabezado(tabla, "Producto");
        agregarCeldaEncabezado(tabla, "Stock Actual");
        agregarCeldaEncabezado(tabla, "Stock Mínimo");
        agregarCeldaEncabezado(tabla, "Estado");
        
        // Datos
        for (Producto p : productosStockBajo) {
            tabla.addCell(crearCelda(p.getCodigo()));
            tabla.addCell(crearCelda(p.getNombre()));
            tabla.addCell(crearCeldaCentrada(String.valueOf(p.getStock())));
            tabla.addCell(crearCeldaCentrada(String.valueOf(p.getStockMinimo())));
            
            String estado;
            DeviceRgb colorEstado;
            if (p.getStock() == 0) {
                estado = "SIN STOCK";
                colorEstado = COLOR_ROJO;
            } else if (p.getStock() < p.getStockMinimo()) {
                estado = "CRÍTICO";
                colorEstado = COLOR_AMARILLO;
            } else {
                estado = "BAJO";
                colorEstado = new DeviceRgb(133, 100, 4);
            }
            
            Cell celdaEstado = new Cell()
                    .add(new Paragraph(estado).setFont(fuenteNegrita).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(colorEstado)
                    .setPadding(8);
            tabla.addCell(celdaEstado);
        }
        
        document.add(tabla);
    }
    
    /**
     * Sección de inventario completo
     */
    private void agregarSeccionInventarioCompleto(Document document, List<Producto> productos) {
        agregarEncabezado(document);
        
        Paragraph titulo = new Paragraph(" INVENTARIO COMPLETO")
                .setFont(fuenteNegrita)
                .setFontSize(16)
                .setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(15);
        document.add(titulo);
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1.2f, 2.5f, 1f, 1.2f, 1.2f, 1.5f}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        agregarCeldaEncabezado(tabla, "Código");
        agregarCeldaEncabezado(tabla, "Producto");
        agregarCeldaEncabezado(tabla, "Stock");
        agregarCeldaEncabezado(tabla, "Precio");
        agregarCeldaEncabezado(tabla, "Stock Mín.");
        agregarCeldaEncabezado(tabla, "Valor Total");
        
        // Datos (máximo 20 productos por página)
        int contador = 0;
        for (Producto p : productos) {
            if (contador > 0 && contador % 20 == 0) {
                document.add(tabla);
                document.add(new AreaBreak());
                agregarEncabezado(document);
                
                tabla = new Table(UnitValue.createPercentArray(new float[]{1.2f, 2.5f, 1f, 1.2f, 1.2f, 1.5f}));
                tabla.setWidth(UnitValue.createPercentValue(100));
                
                agregarCeldaEncabezado(tabla, "Código");
                agregarCeldaEncabezado(tabla, "Producto");
                agregarCeldaEncabezado(tabla, "Stock");
                agregarCeldaEncabezado(tabla, "Precio");
                agregarCeldaEncabezado(tabla, "Stock Mín.");
                agregarCeldaEncabezado(tabla, "Valor Total");
            }
            
            tabla.addCell(crearCelda(p.getCodigo()));
            tabla.addCell(crearCelda(p.getNombre()));
            tabla.addCell(crearCeldaCentrada(String.valueOf(p.getStock())));
            tabla.addCell(crearCeldaCentrada(String.format("$%.2f", p.getPrecioVenta())));
            tabla.addCell(crearCeldaCentrada(String.valueOf(p.getStockMinimo())));
            tabla.addCell(crearCeldaCentrada(String.format("$%.2f", p.getPrecioVenta() * p.getStock())));
            
            contador++;
        }
        
        document.add(tabla);
    }
    
    /**
     * Sección de movimientos recientes
     */
    private void agregarSeccionMovimientos(Document document, List<MovimientoInventario> movimientos) {
        agregarEncabezado(document);
        
        Paragraph titulo = new Paragraph(" MOVIMIENTOS RECIENTES (Últimos 30)")
                .setFont(fuenteNegrita)
                .setFontSize(16)
                .setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(15);
        document.add(titulo);
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1.2f, 1.5f, 1f, 2f, 1f, 2f}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        agregarCeldaEncabezado(tabla, "Código");
        agregarCeldaEncabezado(tabla, "Fecha");
        agregarCeldaEncabezado(tabla, "Tipo");
        agregarCeldaEncabezado(tabla, "Producto");
        agregarCeldaEncabezado(tabla, "Cantidad");
        agregarCeldaEncabezado(tabla, "Motivo");
        
        // Últimos 30 movimientos
        List<MovimientoInventario> ultimos30 = movimientos.stream()
                .sorted((m1, m2) -> m2.getFecha().compareTo(m1.getFecha()))
                .limit(30)
                .collect(Collectors.toList());
        
        for (MovimientoInventario m : ultimos30) {
            tabla.addCell(crearCelda(m.getCodigo()));
            tabla.addCell(crearCelda(m.getFechaFormateada()));
            
            // Tipo con color
            DeviceRgb colorTipo;
            switch (m.getTipoMovimiento()) {
                case "ENTRADA":
                    colorTipo = COLOR_VERDE;
                    break;
                case "SALIDA":
                    colorTipo = COLOR_ROJO;
                    break;
                default:
                    colorTipo = COLOR_AMARILLO;
            }
            
            Cell celdaTipo = new Cell()
                    .add(new Paragraph(m.getTipoMovimiento()).setFont(fuenteNegrita).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(colorTipo)
                    .setPadding(8);
            tabla.addCell(celdaTipo);
            
            tabla.addCell(crearCelda(m.getNombreProducto()));
            tabla.addCell(crearCeldaCentrada(m.getSignoCantidad()));
            tabla.addCell(crearCelda(m.getMotivo() != null ? m.getMotivo() : ""));
        }
        
        document.add(tabla);
    }
    
    /**
     * Celda de encabezado
     */
    private void agregarCeldaEncabezado(Table tabla, String texto) {
        Cell celda = new Cell()
                .add(new Paragraph(texto).setFont(fuenteNegrita).setFontSize(10))
                .setBackgroundColor(COLOR_PRIMARIO)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        tabla.addCell(celda);
    }
    
    /**
     * Celda normal
     */
    private Cell crearCelda(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setFont(fuenteNormal).setFontSize(9))
                .setPadding(8)
                .setBorder(new SolidBorder(COLOR_GRIS_CLARO, 0.5f));
    }
    
    /**
     * Celda centrada
     */
    private Cell crearCeldaCentrada(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setFont(fuenteNormal).setFontSize(9))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8)
                .setBorder(new SolidBorder(COLOR_GRIS_CLARO, 0.5f));
    }
    
    /**
     * Números de página
     */
    private void agregarNumerosPagina(PdfDocument pdf, int totalPaginas) {
        try {
            for (int i = 2; i <= totalPaginas; i++) {
                com.itextpdf.kernel.pdf.canvas.PdfCanvas canvas = 
                    new com.itextpdf.kernel.pdf.canvas.PdfCanvas(
                        pdf.getPage(i).newContentStreamBefore(), 
                        pdf.getPage(i).getResources(), 
                        pdf);
                
                canvas.beginText()
                    .setFontAndSize(fuenteNormal, 8)
                    .setColor(COLOR_GRIS, true)
                    .moveText(50, 30)
                    .showText("STOCKFLOW")
                    .endText();
                
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
                
                canvas.beginText()
                    .setFontAndSize(fuenteNormal, 8)
                    .setColor(COLOR_GRIS, true)
                    .moveText(anchoPagina - 100, 30)
                    .showText("Confidencial")
                    .endText();
            }
        } catch (Exception e) {
            System.err.println("Error agregando números de página: " + e.getMessage());
        }
    }
}