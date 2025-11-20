
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.ChatMensaje;
import com.mycompany.stockflow.excepciones.*;
import com.mycompany.stockflow.utils.DeepSeekAPIClient;
import java.util.*;

/**
 * Servicio de ChatBot libre para clientes invitados.
 * 
 * <p>Este servicio proporciona un asistente de IA conversacional sin restricciones
 * que puede responder cualquier tipo de pregunta: recomendaciones de productos,
 * cálculos de construcción, consejos generales, etc.</p>
 * 
 * <p>A diferencia del ChatBot de negocio, este asistente:</p>
 * <ul>
 *   <li>No tiene acceso a datos internos del negocio</li>
 *   <li>Responde preguntas generales y de recomendación</li>
 *   <li>Usa completamente la IA de DeepSeek</li>
 *   <li>Es ideal para asistencia al cliente/visitante</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 */
public class ChatBotInvitadoServicio {
    
    /** Cliente API de DeepSeek para procesamiento con IA */
    private final DeepSeekAPIClient deepSeekClient;
    
    /** Historial de mensajes de la conversación */
    private final List<ChatMensaje> historialConversacion;
    
    /** Prompt del sistema que define el comportamiento del asistente */
    private static final String SYSTEM_PROMPT = 
        "Eres un asistente virtual amigable y útil de StockFlow, una tienda de productos. " +
        "Tu objetivo es ayudar a los visitantes con:\n\n" +
        "- Recomendaciones de productos para regalos u ocasiones especiales\n" +
        "- Consejos y cálculos para proyectos (construcción, reparaciones, etc.)\n" +
        "- Responder preguntas generales sobre productos\n" +
        "- Orientación sobre qué comprar según necesidades\n" +
        "- Cualquier otra consulta que tengan\n\n" +
        "ESTILO DE RESPUESTA:\n" +
        "- Sé amigable, profesional y conciso\n" +
        "- Da respuestas prácticas y útiles\n" +
        "- Si haces cálculos, explica paso a paso\n" +
        "- Para recomendaciones, considera presupuesto y preferencias\n" +
        "- Usa formato de texto plano sin markdown\n" +
        "- Mantén respuestas de longitud moderada (no muy largas)\n\n" +
        "Importante: No tienes acceso al inventario específico de la tienda, " +
        "así que da recomendaciones generales y sugiere que consulten disponibilidad.";
    
    /**
     * Constructor que inicializa el servicio del chatbot de invitado.
     */
    public ChatBotInvitadoServicio() {
        this.deepSeekClient = new DeepSeekAPIClient();
        this.historialConversacion = new ArrayList<>();
        
        // Mensaje de bienvenida
        historialConversacion.add(new ChatMensaje(
            "assistant",
            "¡Hola! 👋 Soy tu asistente virtual de StockFlow. " +
            "Puedo ayudarte con recomendaciones de productos, cálculos para proyectos, " +
            "consejos de compra y mucho más. ¿En qué puedo ayudarte hoy?"
        ));
    }
    
    /**
     * Procesa una pregunta del usuario y genera respuesta usando IA.
     * 
     * <p>A diferencia del chatbot de negocio, este método envía todas las
     * preguntas directamente a DeepSeek sin análisis de patrones local.</p>
     * 
     * @param pregunta la consulta del usuario
     * @return mensaje con la respuesta del asistente
     */
    public ChatMensaje procesarPregunta(String pregunta) {
        // Agregar pregunta del usuario al historial
        ChatMensaje mensajeUsuario = new ChatMensaje("user", pregunta);
        historialConversacion.add(mensajeUsuario);
        
        try {
            // Construir el prompt completo con contexto
            String promptCompleto = construirPromptConContexto(pregunta);
            
            // Obtener respuesta de DeepSeek
            String respuesta = deepSeekClient.enviarPrompt(promptCompleto);
            
            // Limpiar respuesta si viene con formato markdown
            respuesta = limpiarRespuesta(respuesta);
            
            // Crear y guardar mensaje de respuesta
            ChatMensaje mensajeAsistente = new ChatMensaje("assistant", respuesta);
            historialConversacion.add(mensajeAsistente);
            
            return mensajeAsistente;
            
        } catch (AIAPIException e) {
            ChatMensaje mensajeError = new ChatMensaje(
                "assistant",
                "Lo siento, tuve problemas para conectarme con el servicio de IA. " +
                "Por favor, intenta de nuevo en un momento. Error: " + e.getMessage(),
                true
            );
            historialConversacion.add(mensajeError);
            return mensajeError;
            
        } catch (ConfiguracionAIFaltanteException e) {
            ChatMensaje mensajeError = new ChatMensaje(
                "assistant",
                "El servicio de asistencia está temporalmente deshabilitado. " +
                "Por favor, contacta con el administrador.",
                true
            );
            historialConversacion.add(mensajeError);
            return mensajeError;
            
        } catch (Exception e) {
            ChatMensaje mensajeError = new ChatMensaje(
                "assistant",
                "Ocurrió un error inesperado: " + e.getMessage() + 
                "\n\nPor favor, intenta reformular tu pregunta.",
                true
            );
            historialConversacion.add(mensajeError);
            return mensajeError;
        }
    }
    
    /**
     * Construye el prompt completo incluyendo contexto de conversación.
     * 
     * @param preguntaActual la pregunta actual del usuario
     * @return prompt formateado para enviar a la API
     */
    private String construirPromptConContexto(String preguntaActual) {
        StringBuilder prompt = new StringBuilder();
        
        // Agregar prompt del sistema
        prompt.append(SYSTEM_PROMPT).append("\n\n");
        
        // Si hay historial reciente (últimos 6 mensajes), incluirlo para contexto
        int inicio = Math.max(0, historialConversacion.size() - 6);
        if (inicio > 0) {
            prompt.append("CONTEXTO DE CONVERSACIÓN RECIENTE:\n");
            for (int i = inicio; i < historialConversacion.size(); i++) {
                ChatMensaje msg = historialConversacion.get(i);
                if (msg.esUsuario()) {
                    prompt.append("Usuario: ").append(msg.getContenido()).append("\n");
                } else if (!msg.isEsError()) {
                    prompt.append("Asistente: ").append(msg.getContenido()).append("\n");
                }
            }
            prompt.append("\n");
        }
        
        // Agregar pregunta actual
        prompt.append("PREGUNTA ACTUAL DEL USUARIO:\n");
        prompt.append(preguntaActual).append("\n\n");
        prompt.append("Por favor, proporciona una respuesta útil y amigable:");
        
        return prompt.toString();
    }
    
    /**
     * Limpia la respuesta eliminando formato markdown si existe.
     * 
     * @param respuesta la respuesta original
     * @return respuesta limpia
     */
    private String limpiarRespuesta(String respuesta) {
        if (respuesta == null) {
            return "Lo siento, no pude generar una respuesta.";
        }
        
        // Eliminar bloques de código markdown si existen
        respuesta = respuesta.replaceAll("```[a-z]*\\n", "");
        respuesta = respuesta.replaceAll("```", "");
        
        // Eliminar asteriscos de negrita/cursiva
        respuesta = respuesta.replaceAll("\\*\\*([^*]+)\\*\\*", "$1");
        respuesta = respuesta.replaceAll("\\*([^*]+)\\*", "$1");
        
        // Eliminar headers markdown
        respuesta = respuesta.replaceAll("^#+\\s+", "");
        
        return respuesta.trim();
    }
    
    /**
     * Obtiene una copia del historial de conversación.
     * 
     * @return lista de mensajes
     */
    public List<ChatMensaje> getHistorialConversacion() {
        return new ArrayList<>(historialConversacion);
    }
    
    /**
     * Limpia el historial y reinicia la conversación.
     */
    public void limpiarHistorial() {
        historialConversacion.clear();
        historialConversacion.add(new ChatMensaje(
            "assistant",
            "¡Conversación reiniciada! ¿En qué más puedo ayudarte?"
        ));
    }
    
    /**
     * Obtiene el número de mensajes en el historial.
     * 
     * @return cantidad de mensajes
     */
    public int getTamañoHistorial() {
        return historialConversacion.size();
    }
    
    /**
     * Verifica si hay conversación activa.
     * 
     * @return true si hay más de un mensaje
     */
    public boolean tieneConversacion() {
        return historialConversacion.size() > 1;
    }
    
    /**
     * Genera sugerencias de preguntas comunes para invitados.
     * 
     * @return array con preguntas sugeridas
     */
    public String[] obtenerSugerencias() {
        return new String[] {
            "¿Qué puedo regalarle a mi pareja que le gusta la tecnología?",
            "Necesito calcular material para una pared de 4x3 metros",
            "¿Qué herramientas básicas necesito para comenzar bricolaje?",
            "Busco un regalo de cumpleaños para un niño de 10 años",
            "¿Cuánta pintura necesito para una habitación de 4x4 metros?",
            "¿Qué electrodomésticos son más eficientes energéticamente?"
        };
    }
}