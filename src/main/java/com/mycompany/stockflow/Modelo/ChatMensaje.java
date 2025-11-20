/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un mensaje individual en una conversación de chat.
 * 
 * <p>Esta clase modela los mensajes intercambiados entre el usuario y el 
 * asistente de inteligencia artificial. Cada mensaje tiene un rol (usuario 
 * o asistente), contenido textual y marca temporal.</p>
 * 
 * <p>Los mensajes pueden ser de dos tipos:</p>
 * <ul>
 *   <li><strong>Usuario:</strong> Preguntas o consultas del usuario</li>
 *   <li><strong>Asistente:</strong> Respuestas generadas por el sistema</li>
 * </ul>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 * ChatMensaje pregunta = new ChatMensaje("user", "¿Cuáles son mis mejores productos?");
 * ChatMensaje respuesta = new ChatMensaje("assistant", "Tus productos más vendidos son...");
 * 
 * if (pregunta.esUsuario()) {
 *     System.out.println("Procesando pregunta: " + pregunta.getContenido());
 * }
 * </pre>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see ChatBotServicio
 */
public class ChatMensaje extends Entidad {
    
    /** Rol del emisor del mensaje: "user" o "assistant" */
    private String rol;
    
    /** Contenido textual del mensaje */
    private String contenido;
    
    /** Fecha y hora en que se creó el mensaje */
    private LocalDateTime timestamp;
    
    /** Indica si el mensaje representa un error en el procesamiento */
    private boolean esError;
    
    /**
     * Constructor que crea un mensaje con rol y contenido especificados.
     * La marca temporal se establece automáticamente a la hora actual.
     * 
     * @param rol el rol del emisor ("user" o "assistant")
     * @param contenido el texto del mensaje
     */
    public ChatMensaje(String rol, String contenido) {
        super(rol + "_" + System.currentTimeMillis());
        this.rol = rol;
        this.contenido = contenido;
        this.timestamp = LocalDateTime.now();
        this.esError = false;
    }
    
    /**
     * Constructor que crea un mensaje con indicador de error.
     * 
     * @param rol el rol del emisor ("user" o "assistant")
     * @param contenido el texto del mensaje
     * @param esError true si el mensaje representa un error
     */
    public ChatMensaje(String rol, String contenido, boolean esError) {
        this(rol, contenido);
        this.esError = esError;
    }
    
    /**
     * Obtiene el rol del mensaje.
     * 
     * @return "user" si es del usuario, "assistant" si es del asistente
     */
    public String getRol() {
        return rol;
    }
    
    /**
     * Establece el rol del mensaje.
     * 
     * @param rol el nuevo rol del mensaje
     */
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    /**
     * Obtiene el contenido textual del mensaje.
     * 
     * @return el texto del mensaje
     */
    public String getContenido() {
        return contenido;
    }
    
    /**
     * Establece el contenido textual del mensaje.
     * 
     * @param contenido el nuevo contenido
     */
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    
    /**
     * Obtiene la marca temporal del mensaje.
     * 
     * @return la fecha y hora de creación del mensaje
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Establece la marca temporal del mensaje.
     * 
     * @param timestamp la nueva marca temporal
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Verifica si el mensaje representa un error.
     * 
     * @return true si es un mensaje de error
     */
    public boolean isEsError() {
        return esError;
    }
    
    /**
     * Establece el indicador de error del mensaje.
     * 
     * @param esError true si el mensaje es de error
     */
    public void setEsError(boolean esError) {
        this.esError = esError;
    }
    
    /**
     * Verifica si el mensaje es del usuario.
     * 
     * @return true si el rol es "user"
     */
    public boolean esUsuario() {
        return "user".equals(rol);
    }
    
    /**
     * Verifica si el mensaje es del asistente.
     * 
     * @return true si el rol es "assistant"
     */
    public boolean esAsistente() {
        return "assistant".equals(rol);
    }
    
    /**
     * Formatea la marca temporal en formato legible.
     * 
     * @return la hora formateada como "HH:mm"
     */
    public String getHoraFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return timestamp.format(formatter);
    }
    
    /**
     * Devuelve una representación en cadena del mensaje.
     * 
     * @return cadena con formato "[rol] contenido (hora)"
     */
    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", 
            rol.toUpperCase(), 
            contenido.substring(0, Math.min(50, contenido.length())), 
            getHoraFormateada()
        );
    }
}
