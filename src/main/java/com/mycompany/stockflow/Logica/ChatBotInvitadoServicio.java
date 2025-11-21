/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.ChatMensaje;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.excepciones.*;
import com.mycompany.stockflow.utils.DeepSeekAPIClient;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de ChatBot libre para clientes invitados con recomendaciones basadas en stock.
 * 
 * <p>Este servicio proporciona un asistente de IA conversacional que puede responder 
 * preguntas generales Y recomendar productos disponibles en inventario cuando el usuario 
 * pregunta por artículos específicos.</p>
 * 
 * <p>A diferencia del ChatBot de negocio, este asistente:</p>
 * <ul>
 *   <li>No tiene acceso a datos internos del negocio (ventas, clientes)</li>
 *   <li>Responde preguntas generales y de recomendación</li>
 *   <li>Integra información de inventario para recomendaciones personalizadas</li>
 *   <li>Usa completamente la IA de DeepSeek con contexto enriquecido</li>
 *   <li>Es ideal para asistencia al cliente/visitante</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de funcionalidad:</strong></p>
 * <pre>
 * Usuario: "¿Tienen camisetas de Barcelona?"
 * Respuesta: "Sí, tenemos camisetas de Barcelona disponibles. Te recomendamos..."
 * </pre>
 * 
 * @author StockFlow Team
 * @version 2.0
 */
public class ChatBotInvitadoServicio {
    
    /** Cliente API de DeepSeek para procesamiento con IA */
    private final DeepSeekAPIClient deepSeekClient;
    
    /** Historial de mensajes de la conversación */
    private final List<ChatMensaje> historialConversacion;
    
    /** Servicio de acceso a catálogo de productos */
    private final ProductoServicio productoServicio;
    
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
        "- Mantén respuestas de longitud moderada (no muy largas)\n" +
        "- Si mencionamos productos disponibles en inventario, destácalos como excelentes opciones\n\n" +
        "Importante: Tienes acceso a información de productos disponibles en inventario. " +
        "Úsala para dar recomendaciones personalizadas cuando el usuario pregunte por artículos específicos.";
    
    /**
     * Constructor que inicializa el servicio del chatbot de invitado.
     * 
     * <p>Inicializa todas las dependencias necesarias incluyendo acceso 
     * al catálogo de productos para recomendaciones basadas en stock.</p>
     */
    public ChatBotInvitadoServicio() {
        this.deepSeekClient = new DeepSeekAPIClient();
        this.historialConversacion = new ArrayList<>();
        this.productoServicio = new ProductoServicio();
        
        // Mensaje de bienvenida
        historialConversacion.add(new ChatMensaje(
            "assistant",
            "¡Hola! 👋 Soy tu asistente virtual de StockFlow. " +
            "Puedo ayudarte con recomendaciones de productos, cálculos para proyectos, " +
            "consejos de compra y mucho más. ¿En qué puedo ayudarte hoy?"
        ));
    }
    
    /**
     * Procesa una pregunta del usuario y genera respuesta usando IA con contexto de stock.
     * 
     * <p>Este método realiza los siguientes pasos:</p>
     * <ol>
     *   <li>Agrega la pregunta al historial</li>
     *   <li>Busca productos relacionados en el inventario</li>
     *   <li>Enriquece el prompt con información de stock disponible</li>
     *   <li>Envía a DeepSeek para procesar</li>
     *   <li>Limpia y guarda la respuesta</li>
     * </ol>
     * 
     * @param pregunta la consulta del usuario
     * @return mensaje con la respuesta del asistente
     */
    public ChatMensaje procesarPregunta(String pregunta) {
        // Agregar pregunta del usuario al historial
        ChatMensaje mensajeUsuario = new ChatMensaje("user", pregunta);
        historialConversacion.add(mensajeUsuario);
        
        try {
            // Construir el prompt completo con contexto de inventario
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
     * Construye el prompt completo incluyendo contexto de conversación e inventario.
     * 
     * <p>El prompt incluye:</p>
     * <ul>
     *   <li>Instrucciones del sistema (SYSTEM_PROMPT)</li>
     *   <li>Información de productos disponibles relacionados con la pregunta</li>
     *   <li>Historial reciente de conversación (últimos 6 mensajes)</li>
     *   <li>La pregunta actual del usuario</li>
     * </ul>
     * 
     * @param preguntaActual la pregunta actual del usuario
     * @return prompt formateado para enviar a la API
     */
    private String construirPromptConContexto(String preguntaActual) {
        StringBuilder prompt = new StringBuilder();
        
        // Agregar prompt del sistema
        prompt.append(SYSTEM_PROMPT).append("\n\n");
        
        // Enriquecer con información de stock si es relevante
        String infoStock = enriquecerPromptConStock(preguntaActual);
        if (!infoStock.isEmpty()) {
            prompt.append(infoStock).append("\n\n");
        }
        
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
     * Enriquece el prompt con información de productos disponibles en stock.
     * 
     * <p>Este método:</p>
     * <ol>
     *   <li>Detecta palabras clave en la pregunta del usuario</li>
     *   <li>Busca productos relacionados en el inventario</li>
     *   <li>Filtra solo productos con stock disponible</li>
     *   <li>Formatea los datos para que la IA los entienda</li>
     * </ol>
     * 
     * @param pregunta la pregunta del usuario
     * @return string con información de productos disponibles, vacío si no hay coincidencias
     */
    private String enriquecerPromptConStock(String pregunta) {
        try {
            // Buscar productos relacionados con la pregunta
            List<Producto> productosRelacionados = buscarProductosRelacionados(pregunta);
            
            // Filtrar solo los que tienen stock disponible
            List<Producto> productosDisponibles = productosRelacionados.stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
            
            // Si hay productos disponibles, formatearlos para el prompt
            if (!productosDisponibles.isEmpty()) {
                return formatearDatosProductos(productosDisponibles);
            }
            
        } catch (Exception e) {
            // Si hay error al acceder al inventario, continuar sin esa información
            // No afecta al funcionamiento del chatbot
        }
        
        return "";
    }
    
    /**
     * Busca productos en el inventario relacionados con la pregunta del usuario.
     * 
     * <p>Utiliza búsqueda por palabras clave en:</p>
     * <ul>
     *   <li>Nombre del producto</li>
     *   <li>Descripción del producto</li>
     *   <li>Categoría del producto</li>
     * </ul>
     * 
     * <p>La búsqueda es case-insensitive y busca coincidencias parciales.</p>
     * 
     * @param pregunta la pregunta del usuario
     * @return lista de productos relacionados encontrados
     * @throws Exception si hay error al acceder a datos de productos
     */
    private List<Producto> buscarProductosRelacionados(String pregunta) throws Exception {
        List<Producto> todosProductos = productoServicio.listarProductos();
        String preguntaLower = pregunta.toLowerCase();
        
        // Extraer palabras clave (palabras con más de 3 caracteres)
        String[] palabras = preguntaLower.split("\\s+");
        List<String> palabrasClave = Arrays.stream(palabras)
            .filter(p -> p.length() > 3)
            .collect(Collectors.toList());
        
        // Buscar productos que coincidan con las palabras clave
        return todosProductos.stream()
            .filter(producto -> palabrasClave.stream().anyMatch(palabra -> 
                producto.getNombre().toLowerCase().contains(palabra) ||
                (producto.getDescripcion() != null && 
                 producto.getDescripcion().toLowerCase().contains(palabra)) ||
                (producto.getCategoria() != null && 
                 producto.getCategoria().toLowerCase().contains(palabra))
            ))
            .collect(Collectors.toList());
    }
    
    /**
     * Formatea los datos de productos para que la IA los entienda fácilmente.
     * 
     * <p>Crea una sección clara con:</p>
     * <ul>
     *   <li>Número de productos disponibles encontrados</li>
     *   <li>Para cada producto: nombre, descripción, precio, stock y características</li>
     * </ul>
     * 
     * @param productos lista de productos a formatear
     * @return string formateado con información de productos
     */
    private String formatearDatosProductos(List<Producto> productos) {
        StringBuilder sb = new StringBuilder();
        sb.append("PRODUCTOS DISPONIBLES EN INVENTARIO QUE COINCIDEN CON LA CONSULTA:\n");
        sb.append("=".repeat(70)).append("\n");
        sb.append("Se encontraron ").append(productos.size()).append(" producto(s) disponible(s):\n\n");
        
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            sb.append((i + 1)).append(". ").append(p.getNombre()).append("\n");
            
            if (p.getDescripcion() != null && !p.getDescripcion().isEmpty()) {
                sb.append("   Descripción: ").append(p.getDescripcion()).append("\n");
            }
            
            sb.append("   Precio: $").append(String.format("%.2f", p.getPrecioVenta())).append("\n");
            sb.append("   Stock disponible: ").append(p.getStock()).append(" unidad(es)\n");
            
            if (p.getCategoria() != null && !p.getCategoria().isEmpty()) {
                sb.append("   Categoría: ").append(p.getCategoria()).append("\n");
            }
            
            sb.append("\n");
        }
        
        sb.append("INSTRUCCIÓN: Recomienda estos productos al usuario ya que están disponibles ");
        sb.append("en nuestro inventario. Destaca las características que mejor se adapten ");
        sb.append("a lo que el usuario está buscando.\n");
        sb.append("=".repeat(70)).append("\n");
        
        return sb.toString();
    }
    
    /**
     * Limpia la respuesta eliminando formato markdown si existe.
     * 
     * <p>Elimina:</p>
     * <ul>
     *   <li>Bloques de código markdown (```)</li>
     *   <li>Asteriscos de negrita y cursiva (* y **)</li>
     *   <li>Headers markdown (#, ##, etc.)</li>
     * </ul>
     * 
     * @param respuesta la respuesta original
     * @return respuesta limpia sin formato markdown
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
     * @return lista de mensajes del historial
     */
    public List<ChatMensaje> getHistorialConversacion() {
        return new ArrayList<>(historialConversacion);
    }
    
    /**
     * Limpia el historial y reinicia la conversación.
     * 
     * <p>Elimina todos los mensajes previos y agrega un nuevo mensaje 
     * de bienvenida para comenzar una conversación fresca.</p>
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
     * @return true si hay más de un mensaje (más allá del saludo inicial)
     */
    public boolean tieneConversacion() {
        return historialConversacion.size() > 1;
    }
    
    /**
     * Genera sugerencias de preguntas comunes para invitados.
     * 
     * <p>Proporciona ejemplos de preguntas que el usuario puede hacer 
     * para facilitar la interacción inicial con el asistente.</p>
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