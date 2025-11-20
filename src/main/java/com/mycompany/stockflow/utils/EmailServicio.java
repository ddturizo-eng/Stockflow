/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.excepciones.EmailException;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Servicio para envio de correos electronicos con archivos adjuntos.
 * 
 * <p>Esta clase proporciona funcionalidad completa para enviar correos electronicos
 * a traves de servidores SMTP, incluyendo:</p>
 * <ul>
 *   <li>Envio de correos con archivos PDF adjuntos</li>
 *   <li>Soporte para multiples proveedores SMTP (Gmail, Outlook, etc.)</li>
 *   <li>Validacion de direcciones de correo</li>
 *   <li>Manejo robusto de errores y excepciones</li>
 *   <li>Mensajes HTML formateados</li>
 * </ul>
 * 
 * <p>El servicio utiliza ConfiguracionEmail para obtener los parametros SMTP
 * necesarios (servidor, puerto, autenticacion).</p>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 * EmailServicio servicio = new EmailServicio();
 * servicio.enviarComprobanteCliente(
 *     "cliente@ejemplo.com",
 *     "Juan Perez",
 *     "COMP-001",
 *     pdfBytes
 * );
 * </pre>
 * 
 * @author StockFlow Team
 * @version 2.0
 * @since 1.0
 */
public class EmailServicio {
    
    /** Configuracion SMTP utilizada por el servicio */
    private ConfiguracionEmail config;
    
    /**
     * Constructor que inicializa el servicio con configuracion por defecto.
     * Carga la configuracion desde ConfiguracionEmail.
     */
    public EmailServicio() {
        this.config = new ConfiguracionEmail();
    }
    
    /**
     * Constructor que inicializa el servicio con configuracion personalizada.
     * 
     * @param config objeto de configuracion SMTP personalizado
     */
    public EmailServicio(ConfiguracionEmail config) {
        this.config = config;
    }
    
    /**
     * Envia el comprobante de venta por email al cliente.
     * 
     * <p>Este metodo es especializado para enviar comprobantes de ventas,
     * generando automaticamente un mensaje HTML profesional con la informacion
     * de la compra y adjuntando el PDF del comprobante.</p>
     * 
     * @param emailDestino direccion de correo del cliente
     * @param nombreCliente nombre completo del cliente
     * @param numeroComprobante numero identificador del comprobante
     * @param pdfBytes contenido del PDF en bytes
     * @throws EmailException si hay error en la configuracion o envio
     */
    public void enviarComprobanteCliente(String emailDestino, String nombreCliente, 
                                         String numeroComprobante, byte[] pdfBytes) 
            throws EmailException {
        
        if (!config.isConfiguracionValida()) {
            throw new EmailException(
                "Configuracion de email incompleta. Verifique email.properties"
            );
        }
        
        if (emailDestino == null || emailDestino.trim().isEmpty()) {
            throw new EmailException("El email del destinatario no puede estar vacio");
        }
        
        if (!esEmailValido(emailDestino)) {
            throw new EmailException("El formato del email no es valido: " + emailDestino);
        }
        
        String asunto = "Comprobante de Venta - " + numeroComprobante;
        String mensaje = construirMensajeComprobante(nombreCliente, numeroComprobante);
        String nombreArchivo = "Comprobante_" + numeroComprobante + ".pdf";
        
        enviarEmailConAdjunto(emailDestino, asunto, mensaje, pdfBytes, nombreArchivo);
    }
    
    /**
     * Envia un email con archivo PDF adjunto.
     * 
     * <p>Este es el metodo principal de envio que:</p>
     * <ol>
     *   <li>Establece la sesion SMTP con autenticacion</li>
     *   <li>Construye el mensaje MIME multipart</li>
     *   <li>Adjunta el contenido HTML y el archivo PDF</li>
     *   <li>Envia el correo a traves del servidor SMTP</li>
     * </ol>
     * 
     * @param destinatario direccion de correo del destinatario
     * @param asunto asunto del correo
     * @param mensaje cuerpo del mensaje en formato HTML
     * @param pdfBytes contenido del PDF en bytes
     * @param nombreArchivo nombre del archivo adjunto
     * @throws EmailException si hay error durante el envio
     */
    public void enviarEmailConAdjunto(String destinatario, String asunto, String mensaje,
                                      byte[] pdfBytes, String nombreArchivo) 
            throws EmailException {
        
        try {
            Properties props = config.getMailProperties();
            
            // Crear sesion con autenticacion
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        config.getUsername(), 
                        config.getPassword()
                    );
                }
            });
            
            Message message = new MimeMessage(session);
            
            // Configurar remitente
            message.setFrom(new InternetAddress(
                config.getFromEmail(), 
                config.getFromName()
            ));
            
            // Configurar destinatario
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(destinatario)
            );
            
            // Configurar asunto
            message.setSubject(asunto);
            
            // Crear parte del mensaje HTML
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(mensaje, "text/html; charset=utf-8");
            
            // Crear parte del adjunto PDF
            MimeBodyPart adjuntoBodyPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(pdfBytes, "application/pdf");
            adjuntoBodyPart.setDataHandler(new DataHandler(source));
            adjuntoBodyPart.setFileName(nombreArchivo);
            
            // Combinar partes en multipart
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(adjuntoBodyPart);
            
            // Establecer contenido y enviar
            message.setContent(multipart);
            Transport.send(message);
            
            System.out.println("Email enviado exitosamente a: " + destinatario);
            
        } catch (MessagingException e) {
            throw new EmailException(
                "Error al enviar email: " + extraerMensajeError(e), 
                e
            );
        } catch (Exception e) {
            throw new EmailException(
                "Error inesperado al enviar email: " + e.getMessage(), 
                e
            );
        }
    }
    
    /**
     * Construye el mensaje HTML para el comprobante de venta.
     * 
     * <p>Genera un HTML profesional y responsive con:</p>
     * <ul>
     *   <li>Encabezado con branding de StockFlow</li>
     *   <li>Informacion del comprobante resaltada</li>
     *   <li>Mensaje de agradecimiento personalizado</li>
     *   <li>Footer con informacion de contacto</li>
     * </ul>
     * 
     * @param nombreCliente nombre del cliente para personalizar el mensaje
     * @param numeroComprobante numero del comprobante
     * @return String con el HTML completo del mensaje
     */
    private String construirMensajeComprobante(String nombreCliente, String numeroComprobante) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        html.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");
        
        html.append("<h2 style='color: #1e3a5f;'>StockFlow</h2>");
        html.append("<h3 style='color: #2e7d32;'>Comprobante de Venta</h3>");
        
        html.append("<p>Estimado/a <strong>").append(nombreCliente).append("</strong>,</p>");
        html.append("<p>Adjunto encontrara el comprobante de su compra.</p>");
        
        html.append("<div style='background-color: #f5f7fa; padding: 15px; border-left: 4px solid #1e3a5f; margin: 20px 0;'>");
        html.append("<p style='margin: 5px 0;'><strong>Numero de Comprobante:</strong> ").append(numeroComprobante).append("</p>");
        html.append("<p style='margin: 5px 0;'>Archivo adjunto: Comprobante_").append(numeroComprobante).append(".pdf</p>");
        html.append("</div>");
        
        html.append("<p>Gracias por su compra.</p>");
        
        html.append("<hr style='border: none; border-top: 1px solid #e0e0e0; margin: 30px 0;'>");
        
        html.append("<p style='font-size: 12px; color: #64748b;'>");
        html.append("Este es un mensaje automatico, por favor no responder a este correo.<br>");
        html.append("StockFlow - Sistema de Gestion de Inventario<br>");
        html.append("</p>");
        
        html.append("</div></body></html>");
        
        return html.toString();
    }
    
    /**
     * Valida el formato de una direccion de correo electronico.
     * 
     * @param email direccion de correo a validar
     * @return true si el formato es valido, false en caso contrario
     */
    private boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
    
    /**
     * Extrae un mensaje de error comprensible desde una MessagingException.
     * 
     * <p>Traduce errores tecnicos de JavaMail a mensajes mas amigables para el usuario.</p>
     * 
     * @param e la excepcion de mensajeria
     * @return mensaje de error simplificado y comprensible
     */
    private String extraerMensajeError(MessagingException e) {
        String mensaje = e.getMessage();
        
        if (mensaje.contains("Authentication failed")) {
            return "Autenticacion fallida. Verifique usuario y contrasena SMTP";
        }
        if (mensaje.contains("Could not connect")) {
            return "No se pudo conectar al servidor SMTP. Verifique host y puerto";
        }
        if (mensaje.contains("Invalid Addresses")) {
            return "Direccion de email invalida";
        }
        if (mensaje.contains("550")) {
            return "Email rechazado por el servidor destinatario";
        }
        
        return mensaje;
    }
    
    /**
     * Verifica si la configuracion SMTP es valida y completa.
     * 
     * @return true si la configuracion es valida
     */
    public boolean verificarConfiguracion() {
        return config.isConfiguracionValida();
    }
    
    /**
     * Clase auxiliar interna para manejar arrays de bytes como DataSource.
     * Implementa la interfaz DataSource de JavaMail para permitir adjuntar
     * contenido de bytes como archivos PDF.
     */
    private static class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;
        
        /**
         * Constructor del DataSource.
         * 
         * @param data contenido en bytes
         * @param type tipo MIME del contenido
         */
        public ByteArrayDataSource(byte[] data, String type) {
            this.data = data;
            this.type = type;
        }
        
        @Override
        public InputStream getInputStream() {
            return new java.io.ByteArrayInputStream(data);
        }
        
        @Override
        public OutputStream getOutputStream() {
            throw new UnsupportedOperationException("No se soporta escritura");
        }
        
        @Override
        public String getContentType() {
            return type;
        }
        
        @Override
        public String getName() {
            return "ByteArrayDataSource";
        }
    }
}