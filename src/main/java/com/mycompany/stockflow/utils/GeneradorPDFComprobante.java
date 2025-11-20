/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

import com.mycompany.stockflow.Modelo.Factura;
import com.mycompany.stockflow.Modelo.DetalleVenta;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.io.ByteArrayOutputStream;

/**
 * Generador de comprobantes PDF profesionales para StockFlow
 * Soporta dos formatos: A4 (estándar) y Ticket (80mm)
 */
public class GeneradorPDFComprobante {
    
    // Colores corporativos StockFlow
    private static final DeviceRgb COLOR_PRIMARIO = new DeviceRgb(30, 58, 95); // #1e3a5f
    private static final DeviceRgb COLOR_SECUNDARIO = new DeviceRgb(46, 125, 50); // #2e7d32
    private static final DeviceRgb COLOR_TEXTO = new DeviceRgb(30, 41, 59); // #1e293b
    private static final DeviceRgb COLOR_GRIS = new DeviceRgb(100, 116, 139); // #64748b
    
    // Información de la empresa
    private static final String NOMBRE_EMPRESA = "STOCKFLOW";
    private static final String SUBTITULO_EMPRESA = "Sistema de Gestión de Inventario";
    private static final String NIT_EMPRESA = "NIT: 000000000";
    private static final String DIRECCION_EMPRESA = "Calle 123 #45-67, Valledupar, Cesar";
    private static final String TELEFONO_EMPRESA = "Tel: +57 (5) 123-4567";
    private static final String EMAIL_EMPRESA = "contacto@stockflow.com";
    private static final String WEB_EMPRESA = "www.stockflow.com";
    
    private static final DecimalFormat FORMATO_MONEDA = new DecimalFormat("'COP '#,##0");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Tipos de formato disponibles
     */
    public enum TipoFormato {
        A4,      // Formato estándar A4 (210 x 297 mm)
        TICKET   // Formato ticket (80 mm de ancho)
    }
    
    /**
     * Genera un comprobante PDF en formato A4
     */
    public static void generarComprobanteA4(Factura factura, String rutaDestino) throws Exception {
        generarComprobante(factura, rutaDestino, TipoFormato.A4);
    }
    
    /**
     * Genera un comprobante PDF en formato Ticket
     */
    public static void generarComprobanteTicket(Factura factura, String rutaDestino) throws Exception {
        generarComprobante(factura, rutaDestino, TipoFormato.TICKET);
    }
    
    /**
     * Genera un comprobante PDF según el formato especificado
     */
    public static void generarComprobante(Factura factura, String rutaDestino, TipoFormato formato) throws Exception {
        // Crear el archivo PDF
        PdfWriter writer = new PdfWriter(rutaDestino);
        PdfDocument pdf = new PdfDocument(writer);
        
        // Configurar tamaño de página según formato
        PageSize pageSize = formato == TipoFormato.TICKET 
            ? new PageSize(226.77f, 841.89f)  // 80mm x 297mm
            : PageSize.A4;
        
        pdf.setDefaultPageSize(pageSize);
        
        Document document = new Document(pdf);
        document.setMargins(formato == TipoFormato.TICKET ? 10 : 40, 
                           formato == TipoFormato.TICKET ? 10 : 40, 
                           formato == TipoFormato.TICKET ? 10 : 40, 
                           formato == TipoFormato.TICKET ? 10 : 40);
        
        if (formato == TipoFormato.A4) {
            construirComprobanteA4(document, factura);
        } else {
            construirComprobanteTicket(document, factura);
        }
        
        document.close();
        System.out.println(" Comprobante PDF generado: " + rutaDestino);
    }
    
    /**
     * Construye el contenido del comprobante en formato A4
     */
    private static void construirComprobanteA4(Document document, Factura factura) throws Exception {
        // 1. ENCABEZADO CON LOGO
        agregarEncabezadoA4(document, factura);
        
        // 2. INFORMACIÓN DEL CLIENTE
        agregarInformacionCliente(document, factura);
        
        // 3. TABLA DE PRODUCTOS
        agregarTablaProductos(document, factura);
        
        // 4. TOTALES
        agregarTotales(document, factura);
        
        // 5. INFORMACIÓN DE PAGO
        agregarInformacionPago(document, factura);
        
        // 6. PIE DE PÁGINA
        agregarPieDePagina(document, factura);
    }
    
    /**
     * Construye el contenido del comprobante en formato Ticket
     */
    private static void construirComprobanteTicket(Document document, Factura factura) throws Exception {
        // 1. ENCABEZADO SIMPLE
        agregarEncabezadoTicket(document, factura);
        
        // 2. SEPARADOR
        document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(5));
        
        // 3. INFORMACIÓN DEL CLIENTE
        agregarInformacionClienteTicket(document, factura);
        
        // 4. PRODUCTOS (sin tabla, lista simple)
        agregarProductosTicket(document, factura);
        
        // 5. TOTALES
        agregarTotalesTicket(document, factura);
        
        // 6. INFORMACIÓN DE PAGO
        agregarInformacionPagoTicket(document, factura);
        
        // 7. PIE DE PÁGINA
        agregarPieDePaginaTicket(document, factura);
    }
    
    // ==================== MÉTODOS PARA FORMATO A4 ====================
    
    private static void agregarEncabezadoA4(Document document, Factura factura) throws Exception {
        Table headerTable = new Table(2);
        headerTable.setWidth(UnitValue.createPercentValue(100));
        
        // Columna izquierda: Logo y nombre
        Cell leftCell = new Cell();
        leftCell.setBorder(null);
        
        // Intentar cargar el logo
        try {
            URL logoUrl = GeneradorPDFComprobante.class.getResource("/com/mycompany/stockflow/IMG/logoStockflow.png");
            if (logoUrl != null) {
                ImageData imageData = ImageDataFactory.create(logoUrl);
                Image logo = new Image(imageData);
                logo.setWidth(100);
                logo.setHeight(100);
                leftCell.add(logo);
            }
        } catch (Exception e) {
            System.out.println(" No se pudo cargar el logo: " + e.getMessage());
        }
        
        Paragraph nombreEmpresa = new Paragraph(NOMBRE_EMPRESA)
            .setFontSize(24)
            .setBold()
            .setFontColor(COLOR_PRIMARIO)
            .setMarginTop(10);
        leftCell.add(nombreEmpresa);
        
        Paragraph subtitulo = new Paragraph(SUBTITULO_EMPRESA)
            .setFontSize(11)
            .setFontColor(COLOR_GRIS);
        leftCell.add(subtitulo);
        
        // Columna derecha: Información del comprobante
        Cell rightCell = new Cell();
        rightCell.setBorder(null);
        rightCell.setTextAlignment(TextAlignment.RIGHT);
        
        Paragraph tipoDoc = new Paragraph("COMPROBANTE DE VENTA")
            .setFontSize(16)
            .setBold()
            .setFontColor(COLOR_PRIMARIO);
        rightCell.add(tipoDoc);
        
        Paragraph noValido = new Paragraph("(No válido como factura fiscal)")
            .setFontSize(8)
            .setFontColor(COLOR_GRIS)
            .setItalic();
        rightCell.add(noValido);
        
        rightCell.add(new Paragraph("\n"));
        
        Paragraph numero = new Paragraph("N° " + factura.getNumeroComprobante())
            .setFontSize(14)
            .setBold()
            .setFontColor(COLOR_SECUNDARIO);
        rightCell.add(numero);
        
        Paragraph fecha = new Paragraph("Fecha: " + factura.getFecha().format(FORMATO_FECHA))
            .setFontSize(10)
            .setFontColor(COLOR_TEXTO);
        rightCell.add(fecha);
        
        Paragraph estado = new Paragraph("Estado: " + factura.getEstado())
            .setFontSize(10)
            .setBold()
            .setFontColor("PAGADA".equals(factura.getEstado()) ? COLOR_SECUNDARIO : ColorConstants.RED);
        rightCell.add(estado);
        
        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);
        
        document.add(headerTable);
        document.add(new Paragraph("\n"));
    }
    
    private static void agregarInformacionCliente(Document document, Factura factura) {
        // Recuadro con información del cliente
        Div clienteDiv = new Div();
        clienteDiv.setBorder(new SolidBorder(COLOR_PRIMARIO, 1));
        clienteDiv.setBackgroundColor(new DeviceRgb(245, 247, 250)); // #F5F7FA
        clienteDiv.setPadding(15);
        
        Paragraph titulo = new Paragraph("INFORMACIÓN DEL CLIENTE")
            .setFontSize(11)
            .setBold()
            .setFontColor(COLOR_PRIMARIO)
            .setMarginBottom(8);
        clienteDiv.add(titulo);
        
        Paragraph nombre = new Paragraph("Cliente: " + factura.getNombreCliente())
            .setFontSize(11)
            .setFontColor(COLOR_TEXTO);
        clienteDiv.add(nombre);
        
        Paragraph cedula = new Paragraph("Cédula: " + factura.getCedulaCliente())
            .setFontSize(11)
            .setFontColor(COLOR_TEXTO);
        clienteDiv.add(cedula);
        
        document.add(clienteDiv);
        document.add(new Paragraph("\n"));
    }
    
    private static void agregarTablaProductos(Document document, Factura factura) {
        // Título de la sección
        Paragraph titulo = new Paragraph("DETALLE DE PRODUCTOS")
            .setFontSize(12)
            .setBold()
            .setFontColor(COLOR_PRIMARIO)
            .setMarginBottom(10);
        document.add(titulo);
        
        // Crear tabla con 5 columnas
        float[] columnWidths = {50f, 250f, 80f, 100f, 100f};
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        String[] headers = {"#", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        for (String header : headers) {
            Cell cell = new Cell();
            cell.add(new Paragraph(header).setBold().setFontSize(10));
            cell.setBackgroundColor(COLOR_PRIMARIO);
            cell.setFontColor(ColorConstants.WHITE);
            cell.setTextAlignment(TextAlignment.CENTER);
            cell.setPadding(8);
            table.addHeaderCell(cell);
        }
        
        // Contenido
        int numero = 1;
        if (factura.getVenta() != null && factura.getVenta().getDetalles() != null) {
            for (DetalleVenta detalle : factura.getVenta().getDetalles()) {
                // Número
                table.addCell(crearCeldaProducto(String.valueOf(numero++), TextAlignment.CENTER));
                
                // Producto
                table.addCell(crearCeldaProducto(detalle.getProducto().getNombre(), TextAlignment.LEFT));
                
                // Cantidad
                table.addCell(crearCeldaProducto(String.valueOf(detalle.getCantidad()), TextAlignment.CENTER));
                
                // Precio unitario
                table.addCell(crearCeldaProducto(FORMATO_MONEDA.format(detalle.getPrecioUnitario()), TextAlignment.RIGHT));
                
                // Subtotal
                table.addCell(crearCeldaProducto(FORMATO_MONEDA.format(detalle.getSubtotal()), TextAlignment.RIGHT));
            }
        }
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }
    
    private static Cell crearCeldaProducto(String texto, TextAlignment alineacion) {
        Cell cell = new Cell();
        Paragraph p = new Paragraph(texto).setFontSize(10).setFontColor(COLOR_TEXTO);
        cell.add(p);
        cell.setTextAlignment(alineacion);
        cell.setPadding(6);
        return cell;
    }
    
    private static void agregarTotales(Document document, Factura factura) {
        // Tabla de totales alineada a la derecha
        float[] columnWidths = {300f, 120f};
        Table totalesTable = new Table(columnWidths);
        totalesTable.setWidth(UnitValue.createPercentValue(100));
        totalesTable.setMarginTop(10);
        
        // Subtotal
        totalesTable.addCell(crearCeldaTotal("Subtotal:", false));
        totalesTable.addCell(crearCeldaTotalValor(FORMATO_MONEDA.format(factura.getSubtotal()), false));
        
        // Descuento
        if (factura.getDescuento() > 0) {
            totalesTable.addCell(crearCeldaTotal("Descuento:", false));
            totalesTable.addCell(crearCeldaTotalValor("-" + FORMATO_MONEDA.format(factura.getDescuento()), false));
        }
        
        // IVA
        totalesTable.addCell(crearCeldaTotal("IVA:", false));
        totalesTable.addCell(crearCeldaTotalValor(FORMATO_MONEDA.format(factura.getIva()), false));
        
        // Total (destacado)
        Cell totalLabel = crearCeldaTotal("TOTAL:", true);
        totalLabel.setBackgroundColor(COLOR_PRIMARIO);
        totalLabel.setFontColor(ColorConstants.WHITE);
        totalesTable.addCell(totalLabel);
        
        Cell totalValor = crearCeldaTotalValor(FORMATO_MONEDA.format(factura.getTotal()), true);
        totalValor.setBackgroundColor(COLOR_SECUNDARIO);
        totalValor.setFontColor(ColorConstants.WHITE);
        totalesTable.addCell(totalValor);
        
        document.add(totalesTable);
        document.add(new Paragraph("\n"));
    }
    
    private static Cell crearCeldaTotal(String texto, boolean negrita) {
        Cell cell = new Cell();
        Paragraph p = new Paragraph(texto).setFontSize(11).setFontColor(COLOR_TEXTO);
        if (negrita) p.setBold().setFontSize(13);
        cell.add(p);
        cell.setTextAlignment(TextAlignment.RIGHT);
        cell.setPadding(8);
        cell.setBorder(null);
        return cell;
    }
    
    private static Cell crearCeldaTotalValor(String valor, boolean negrita) {
        Cell cell = new Cell();
        Paragraph p = new Paragraph(valor).setFontSize(11).setFontColor(COLOR_TEXTO);
        if (negrita) p.setBold().setFontSize(13);
        cell.add(p);
        cell.setTextAlignment(TextAlignment.RIGHT);
        cell.setPadding(8);
        cell.setBorder(null);
        return cell;
    }
    
    private static void agregarInformacionPago(Document document, Factura factura) {
        Div pagoDiv = new Div();
        pagoDiv.setBorder(new SolidBorder(COLOR_SECUNDARIO, 1));
        pagoDiv.setBackgroundColor(new DeviceRgb(232, 245, 233)); // #e8f5e9
        pagoDiv.setPadding(15);
        
        Paragraph titulo = new Paragraph("INFORMACIÓN DE PAGO")
            .setFontSize(11)
            .setBold()
            .setFontColor(COLOR_SECUNDARIO)
            .setMarginBottom(8);
        pagoDiv.add(titulo);
        
        Paragraph metodoPago = new Paragraph("Método de pago: " + factura.getMetodoPago())
            .setFontSize(11)
            .setBold()
            .setFontColor(COLOR_TEXTO);
        pagoDiv.add(metodoPago);
        
        if ("Efectivo".equals(factura.getMetodoPago())) {
            Paragraph recibido = new Paragraph("Monto recibido: " + FORMATO_MONEDA.format(factura.getMontoRecibido()))
                .setFontSize(10)
                .setFontColor(COLOR_TEXTO);
            pagoDiv.add(recibido);
            
            Paragraph cambio = new Paragraph("Cambio: " + FORMATO_MONEDA.format(factura.getCambio()))
                .setFontSize(10)
                .setFontColor(COLOR_TEXTO);
            pagoDiv.add(cambio);
        }
        
        document.add(pagoDiv);
    }
    
    private static void agregarPieDePagina(Document document, Factura factura) {
        document.add(new Paragraph("\n"));
        
        // Línea separadora
        document.add(new LineSeparator(new SolidLine()).setMarginBottom(15));
        
        // Información de la empresa
        Paragraph infoEmpresa = new Paragraph()
            .setFontSize(9)
            .setFontColor(COLOR_GRIS)
            .setTextAlignment(TextAlignment.CENTER);
        
        infoEmpresa.add(NOMBRE_EMPRESA + " • " + NIT_EMPRESA + "\n");
        infoEmpresa.add(DIRECCION_EMPRESA + "\n");
        infoEmpresa.add(TELEFONO_EMPRESA + " • " + EMAIL_EMPRESA + "\n");
        infoEmpresa.add(WEB_EMPRESA);
        
        document.add(infoEmpresa);
        
        document.add(new Paragraph("\n"));
        
        // Mensaje de agradecimiento
        Paragraph agradecimiento = new Paragraph("¡Gracias por su compra!")
            .setFontSize(12)
            .setBold()
            .setFontColor(COLOR_PRIMARIO)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(agradecimiento);
        
        // Nota legal
        Paragraph notaLegal = new Paragraph("Este documento es un comprobante de venta y no tiene validez fiscal.")
            .setFontSize(7)
            .setFontColor(COLOR_GRIS)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(10);
        document.add(notaLegal);
    }
 
    
    private static void agregarEncabezadoTicket(Document document, Factura factura) throws Exception {
        // Logo centrado (más pequeño)
        try {
            URL logoUrl = GeneradorPDFComprobante.class.getResource("/com/mycompany/stockflow/IMG/logoStockflow.png");
            if (logoUrl != null) {
                ImageData imageData = ImageDataFactory.create(logoUrl);
                Image logo = new Image(imageData);
                logo.setWidth(60);
                logo.setHeight(60);
                logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                document.add(logo);
            }
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo cargar el logo: " + e.getMessage());
        }
        
        Paragraph nombreEmpresa = new Paragraph(NOMBRE_EMPRESA)
            .setFontSize(14)
            .setBold()
            .setFontColor(COLOR_PRIMARIO)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(nombreEmpresa);
        
        Paragraph subtitulo = new Paragraph(SUBTITULO_EMPRESA)
            .setFontSize(8)
            .setFontColor(COLOR_GRIS)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(subtitulo);
        
        Paragraph nit = new Paragraph(NIT_EMPRESA)
            .setFontSize(7)
            .setFontColor(COLOR_GRIS)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(nit);
        
        document.add(new Paragraph("\n"));
        
        Paragraph tipoDoc = new Paragraph("COMPROBANTE DE VENTA")
            .setFontSize(10)
            .setBold()
            .setFontColor(COLOR_PRIMARIO)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(tipoDoc);
        
        Paragraph noValido = new Paragraph("(No válido como factura fiscal)")
            .setFontSize(6)
            .setFontColor(COLOR_GRIS)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER);
        document.add(noValido);
        
        document.add(new Paragraph("\n"));
        
        Paragraph numero = new Paragraph("N° " + factura.getNumeroComprobante())
            .setFontSize(11)
            .setBold()
            .setFontColor(COLOR_TEXTO)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(numero);
        
        Paragraph fecha = new Paragraph(factura.getFecha().format(FORMATO_FECHA))
            .setFontSize(8)
            .setFontColor(COLOR_GRIS)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(fecha);
    }
    
    private static void agregarInformacionClienteTicket(Document document, Factura factura) {
        Paragraph cliente = new Paragraph("Cliente: " + factura.getNombreCliente())
            .setFontSize(8)
            .setFontColor(COLOR_TEXTO);
        document.add(cliente);
        
        Paragraph cedula = new Paragraph("CC: " + factura.getCedulaCliente())
            .setFontSize(8)
            .setFontColor(COLOR_TEXTO);
        document.add(cedula);
        
        document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(5));
    }
    
    private static void agregarProductosTicket(Document document, Factura factura) {
        Paragraph titulo = new Paragraph("PRODUCTOS")
            .setFontSize(9)
            .setBold()
            .setFontColor(COLOR_PRIMARIO);
        document.add(titulo);
        
        document.add(new Paragraph("\n"));
        
        int numero = 1;
        if (factura.getVenta() != null && factura.getVenta().getDetalles() != null) {
            for (DetalleVenta detalle : factura.getVenta().getDetalles()) {
                Paragraph producto = new Paragraph(numero + ". " + detalle.getProducto().getNombre())
                    .setFontSize(8)
                    .setBold()
                    .setFontColor(COLOR_TEXTO);
                document.add(producto);
                
                Paragraph detalleLinea = new Paragraph(
                    String.format("   %d x %s = %s",
                        detalle.getCantidad(),
                        FORMATO_MONEDA.format(detalle.getPrecioUnitario()),
                        FORMATO_MONEDA.format(detalle.getSubtotal())))
                    .setFontSize(8)
                    .setFontColor(COLOR_GRIS);
                document.add(detalleLinea);
                
                numero++;
            }
        }
        
        document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(5));
    }
    
    private static void agregarTotalesTicket(Document document, Factura factura) {
        // Subtotal
        Paragraph subtotal = new Paragraph()
            .add(new Text("Subtotal: ").setFontSize(8))
            .add(new Text(FORMATO_MONEDA.format(factura.getSubtotal())).setFontSize(8).setBold())
            .setTextAlignment(TextAlignment.RIGHT);
        document.add(subtotal);
        
        // Descuento
        if (factura.getDescuento() > 0) {
            Paragraph descuento = new Paragraph()
                .add(new Text("Descuento: ").setFontSize(8))
                .add(new Text("-" + FORMATO_MONEDA.format(factura.getDescuento())).setFontSize(8).setBold())
                .setTextAlignment(TextAlignment.RIGHT);
            document.add(descuento);
        }
        
        // IVA
        Paragraph iva = new Paragraph()
            .add(new Text("IVA (%): ").setFontSize(8))
            .add(new Text(FORMATO_MONEDA.format(factura.getIva())).setFontSize(8).setBold())
            .setTextAlignment(TextAlignment.RIGHT);
        document.add(iva);
        
        document.add(new LineSeparator(new SolidLine()).setMarginTop(3).setMarginBottom(3));
        
        // Total
        Paragraph total = new Paragraph()
            .add(new Text("TOTAL: ").setFontSize(11).setBold())
            .add(new Text(FORMATO_MONEDA.format(factura.getTotal())).setFontSize(11).setBold().setFontColor(COLOR_SECUNDARIO))
            .setTextAlignment(TextAlignment.RIGHT);
        document.add(total);
        
        document.add(new LineSeparator(new SolidLine()).setMarginTop(3).setMarginBottom(5));
    }
    
    private static void agregarInformacionPagoTicket(Document document, Factura factura) {
        Paragraph metodoPago = new Paragraph("Pago: " + factura.getMetodoPago())
            .setFontSize(8)
            .setBold()
            .setFontColor(COLOR_TEXTO);
        document.add(metodoPago);
        
        if ("Efectivo".equals(factura.getMetodoPago())) {
            Paragraph recibido = new Paragraph("Recibido: " + FORMATO_MONEDA.format(factura.getMontoRecibido()))
                .setFontSize(8)
                .setFontColor(COLOR_GRIS);
            document.add(recibido);
            
            Paragraph cambio = new Paragraph("Cambio: " + FORMATO_MONEDA.format(factura.getCambio()))
                .setFontSize(8)
                .setFontColor(COLOR_GRIS);
            document.add(cambio);
        }
        
        document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(5));
    }
    
    private static void agregarPieDePaginaTicket(Document document, Factura factura) {
        Paragraph agradecimiento = new Paragraph("¡Gracias por su compra!")
            .setFontSize(9)
            .setBold()
            .setFontColor(COLOR_PRIMARIO)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(agradecimiento);
        
        document.add(new Paragraph("\n"));
        
        // Información de contacto
        Paragraph contacto = new Paragraph()
            .setFontSize(6)
            .setFontColor(COLOR_GRIS)
            .setTextAlignment(TextAlignment.CENTER);
        contacto.add(DIRECCION_EMPRESA + "\n");
        contacto.add(TELEFONO_EMPRESA + "\n");
        contacto.add(EMAIL_EMPRESA);
        document.add(contacto);
        
        document.add(new Paragraph("\n"));
        
        // Nota legal
        Paragraph notaLegal = new Paragraph("Sin validez fiscal")
            .setFontSize(6)
            .setFontColor(COLOR_GRIS)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER);
        document.add(notaLegal);
        }

    /**
     * Genera comprobante en formato Ticket como byte array (para enviar por email)
     */
    public static byte[] generarComprobanteTicketBytes(Factura factura) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);

        // Tamaño ticket 80mm
        PageSize pageSize = new PageSize(226.77f, 841.89f);
        pdf.setDefaultPageSize(pageSize);

        Document document = new Document(pdf);
        document.setMargins(10, 10, 10, 10);

        // Reutilizar el método existente para construir el ticket
        construirComprobanteTicket(document, factura);

        document.close();

        return baos.toByteArray();
    }

    /**
     * Genera comprobante en formato A4 como byte array (para enviar por email)
     */
    public static byte[] generarComprobanteA4Bytes(Factura factura) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);

        pdf.setDefaultPageSize(PageSize.A4);

        Document document = new Document(pdf);
        document.setMargins(40, 40, 40, 40);

        // Reutilizar el método existente para construir el A4
        construirComprobanteA4(document, factura);

        document.close();

        return baos.toByteArray();
    }}