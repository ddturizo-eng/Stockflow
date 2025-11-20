/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Gestiona la configuración SMTP para el envío de correos electrónicos.
 * Soporta múltiples proveedores: Gmail, Outlook, servidores institucionales.
 *
 * Proporciona métodos para cargar y configurar datos desde archivo de propiedades (.properties),
 * así como ajustes programáticos para distintos proveedores de email.
 *
 * @author Stockflow Team
 * @version 1.0
 * @since 2025
 */
public class ConfiguracionEmail {
    
    private static final String PROPERTIES_FILE = "/email.properties";
    
    private String host;
    private String port;
    private String username;
    private String password;
    private boolean useTLS;
    private boolean useSSL;
    private String fromEmail;
    private String fromName;
    
    public ConfiguracionEmail() {
        cargarConfiguracion();
    }
    
    private void cargarConfiguracion() {
        Properties props = new Properties();
        
        try (InputStream input = getClass().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                configuracionPorDefecto();
                return;
            }
            
            props.load(input);
            
            this.host = props.getProperty("mail.smtp.host", "smtp.gmail.com");
            this.port = props.getProperty("mail.smtp.port", "587");
            this.username = props.getProperty("mail.username", "");
            this.password = props.getProperty("mail.password", "").replace(" ", "");
            this.useTLS = Boolean.parseBoolean(props.getProperty("mail.smtp.starttls.enable", "true"));
            this.useSSL = Boolean.parseBoolean(props.getProperty("mail.smtp.ssl.enable", "false"));
            this.fromEmail = props.getProperty("mail.from.email", this.username);
            this.fromName = props.getProperty("mail.from.name", "StockFlow");
            
        } catch (IOException e) {
            System.err.println("Error al cargar configuracion de email: " + e.getMessage());
            configuracionPorDefecto();
        }
    }
    
    private void configuracionPorDefecto() {
        this.host = "smtp.gmail.com";
        this.port = "587";
        this.username = "";
        this.password = "";
        this.useTLS = true;
        this.useSSL = false;
        this.fromEmail = "";
        this.fromName = "StockFlow";
    }
    
    public Properties getMailProperties() {
        Properties props = new Properties();
        
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", String.valueOf(useTLS));
        props.put("mail.smtp.ssl.enable", String.valueOf(useSSL));
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        
        return props;
    }
    
    public void configurarSMTP(String host, String port, String username, String password, 
                               boolean useTLS, boolean useSSL) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.useTLS = useTLS;
        this.useSSL = useSSL;
        this.fromEmail = username;
    }
    
    public void configurarGmail(String email, String appPassword) {
        this.host = "smtp.gmail.com";
        this.port = "587";
        this.username = email;
        this.password = appPassword;
        this.useTLS = true;
        this.useSSL = false;
        this.fromEmail = email;
        this.fromName = "StockFlow";
    }
    
    public void configurarOutlook(String email, String password) {
        this.host = "smtp-mail.outlook.com";
        this.port = "587";
        this.username = email;
        this.password = password;
        this.useTLS = true;
        this.useSSL = false;
        this.fromEmail = email;
        this.fromName = "StockFlow";
    }
    
    public boolean isConfiguracionValida() {
        return host != null && !host.isEmpty() &&
               port != null && !port.isEmpty() &&
               username != null && !username.isEmpty() &&
               password != null && !password.isEmpty();
    }
    
    public String getHost() { return host; }
    public String getPort() { return port; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isUseTLS() { return useTLS; }
    public boolean isUseSSL() { return useSSL; }
    public String getFromEmail() { return fromEmail; }
    public String getFromName() { return fromName; }
    public void setFromName(String fromName) { this.fromName = fromName; }
}