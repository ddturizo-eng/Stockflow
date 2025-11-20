package com.mycompany.stockflow.Logica;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Servicio para enviar notificaciones por email
 */
public class EmailNotificacionServicio {
    
    private static final String EMAIL_ADMIN = "ddturizo@unicesar.edu.co";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Envia un correo al administrador notificando cambio de contraseña
     */
    public static boolean enviarNotificacionEmail(String usuarioSolicitante, String emailUsuario) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        
        // Obtener credenciales de variables de entorno
        final String emailOrigen = System.getenv("STOCKFLOW_EMAIL");
        String passwordTemp = System.getenv("STOCKFLOW_PASSWORD");
        
        // Validacion de credenciales
        if (emailOrigen == null || emailOrigen.trim().isEmpty()) {
            System.err.println("ERROR: Variable de entorno STOCKFLOW_EMAIL no configurada");
            System.err.println("Configura: STOCKFLOW_EMAIL=dieg6427@gmail.com");
            return false;
        }
        
        if (passwordTemp == null || passwordTemp.trim().isEmpty()) {
            System.err.println("ERROR: Variable de entorno STOCKFLOW_PASSWORD no configurada");
            System.err.println("Configura: STOCKFLOW_PASSWORD=tu_contrasena_de_aplicacion");
            return false;
        }
        
        // Limpiar espacios de la contraseña por si acaso
        final String passwordEmail = passwordTemp.replaceAll("\\s+", "");
        
        System.out.println("Intentando enviar email desde: " + emailOrigen);
        System.out.println("Enviando a: " + EMAIL_ADMIN);
        
        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailOrigen, passwordEmail);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailOrigen));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_ADMIN));
            message.setSubject("SOLICITUD: Cambio de Contraseña en StockFlow");
            
            String fechaActual = LocalDateTime.now().format(FORMATTER);
            
            String contenido = String.format(
                "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #f8fafb; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #0f172a;'>Solicitud de Cambio de Contraseña</h2>" +
                "<p style='color: #64748b; font-size: 14px;'>Se ha recibido una solicitud de cambio de contraseña en <strong>StockFlow</strong>:</p>" +
                "<div style='background-color: white; padding: 15px; border-radius: 5px; margin: 15px 0; border-left: 4px solid #3b82f6;'>" +
                "<p><strong>Usuario:</strong> %s</p>" +
                "<p><strong>Email:</strong> %s</p>" +
                "<p><strong>Hora de solicitud:</strong> %s</p>" +
                "</div>" +
                "<p style='color: #64748b; font-size: 12px;'>Por favor, accede al sistema para procesar este cambio y verificar la identidad del usuario.</p>" +
                "<hr style='border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;'>" +
                "<p style='color: #94a3b8; font-size: 11px;'>Este es un mensaje automatico de StockFlow. No responder a este correo.</p>" +
                "</div>" +
                "</body>" +
                "</html>",
                usuarioSolicitante,
                emailUsuario != null && !emailUsuario.isEmpty() ? emailUsuario : "No registrado",
                fechaActual
            );
            
            message.setContent(contenido, "text/html; charset=utf-8");
            
            Transport.send(message);
            
            System.out.println("Email enviado exitosamente al administrador: " + EMAIL_ADMIN);
            return true;
            
        } catch (AuthenticationFailedException e) {
            System.err.println("ERROR DE AUTENTICACION:");
            System.err.println("- Verifica que STOCKFLOW_EMAIL sea correcto: " + emailOrigen);
            System.err.println("- Verifica que STOCKFLOW_PASSWORD sea la contraseña de aplicacion (sin espacios)");
            System.err.println("- Genera una contraseña de aplicacion en: https://myaccount.google.com/apppasswords");
            System.err.println("Detalle: " + e.getMessage());
            return false;
            
        } catch (MessagingException e) {
            System.err.println("ERROR al enviar email:");
            System.err.println(e.getMessage());
            
            if (e.getMessage().contains("Could not connect")) {
                System.err.println("- Verifica tu conexion a Internet");
                System.err.println("- Verifica que no haya firewall bloqueando el puerto 587");
            }
            
            e.printStackTrace();
            return false;
            
        } catch (Exception e) {
            System.err.println("ERROR inesperado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene el email del administrador
     */
    public static String getEmailAdmin() {
        return EMAIL_ADMIN;
    }
}