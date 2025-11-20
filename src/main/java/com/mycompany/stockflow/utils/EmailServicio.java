/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.excepciones.EmailException;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;

/**
 * Servicio para envio de correos electronicos con archivos adjuntos.
 * Soporta multiples proveedores SMTP y maneja errores de forma robusta.
 */
public class EmailServicio {
    
    private ConfiguracionEmail config;
    
    public EmailServicio() {
        this.config = new ConfiguracionEmail();
    }
    
    public EmailServicio(ConfiguracionEmail config) {
        this.config = config;
    }
    
    /**
     * Envia el comprobante de venta por email al cliente
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
     * Envia un email con archivo PDF adjunto
     */
    public void enviarEmailConAdjunto(String destinatario, String asunto, String mensaje,
                                      byte[] pdfBytes, String nombreArchivo) 
            throws EmailException {
        
        try {
            Properties props = config.getMailProperties();
            
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
            
            message.setFrom(new InternetAddress(
                config.getFromEmail(), 
                config.getFromName()
            ));
            
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(destinatario)
            );
            
            message.setSubject(asunto);
            
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(mensaje, "text/html; charset=utf-8");
            
            MimeBodyPart adjuntoBodyPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(pdfBytes, "application/pdf");
            adjuntoBodyPart.setDataHandler(new DataHandler(source));
            adjuntoBodyPart.setFileName(nombreArchivo);
            
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(adjuntoBodyPart);
            
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
    
    private boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
    
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
    
    public boolean verificarConfiguracion() {
        return config.isConfiguracionValida();
    }
    
    /**
     * Clase auxiliar para manejar byte arrays como DataSource
     */
    private static class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;
        
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