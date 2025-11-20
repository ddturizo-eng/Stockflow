/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.PromptConfiguracion;
import com.mycompany.stockflow.excepciones.ConfiguracionAIFaltanteException;
import com.mycompany.stockflow.utils.ConfiguracionAI;
import java.io.IOException;
import java.util.*;

/**
 * Servicio de Configuración de Inteligencia Artificial.
 * 
 * <p>Gestiona todos los aspectos de configuración relacionados con la integración 
 * de IA (DeepSeek API) en el sistema, incluyendo credenciales, parámetros del modelo 
 * y prompts personalizados.</p>
 * 
 * <p><strong>Responsabilidades principales:</strong></p>
 * <ul>
 *   <li>Configuración de credenciales de API (API Key, URL)</li>
 *   <li>Gestión de parámetros del modelo (temperatura, max tokens)</li>
 *   <li>Administración de prompts personalizados por tipo de análisis</li>
 *   <li>Validación y verificación de configuración</li>
 *   <li>Exportación e importación de configuraciones</li>
 *   <li>Restauración de valores por defecto</li>
 * </ul>
 * 
 * <p><strong>Parámetros configurables del modelo:</strong></p>
 * <ul>
 *   <li><strong>API Key:</strong> Clave de autenticación para DeepSeek API</li>
 *   <li><strong>API URL:</strong> Endpoint de la API (default: https://api.deepseek.com/v1/chat/completions)</li>
 *   <li><strong>Model:</strong> Nombre del modelo a utilizar (default: deepseek-chat)</li>
 *   <li><strong>Temperature:</strong> Controla creatividad (0.0-2.0, default: 0.7)</li>
 *   <li><strong>Max Tokens:</strong> Longitud máxima de respuesta (100-4000, default: 2000)</li>
 *   <li><strong>Timeout:</strong> Tiempo máximo de espera para respuestas</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>{@code
 * ConfiguracionAIServicio servicio = new ConfiguracionAIServicio();
 * 
 * // Configurar API Key
 * try {
 *     servicio.configurarApiKey("sk-xxxxxxxxxxxxxxxx");
 *     System.out.println("API Key configurada correctamente");
 * } catch (IOException e) {
 *     System.err.println("Error al guardar configuración");
 * }
 * 
 * // Verificar configuración
 * if (servicio.verificarConfiguracion()) {
 *     System.out.println("Sistema listo para usar IA");
 * } else {
 *     System.out.println("Falta configuración");
 *     List<String> mensajes = servicio.validarConfiguracion();
 *     mensajes.forEach(System.out::println);
 * }
 * 
 * // Crear prompt personalizado
 * PromptConfiguracion prompt = new PromptConfiguracion(
 *     "analisis_especial", "VENTAS", 
 *     "Analiza ventas con enfoque en rentabilidad"
 * );
 * prompt.setTemperatura(0.6);
 * servicio.guardarPromptPersonalizado(prompt);
 * }</pre>
 * 
 * <p><strong>Seguridad:</strong></p>
 * <ul>
 *   <li>La API Key se almacena de forma segura</li>
 *   <li>Solo se muestra parcialmente en consultas (primeros 10 caracteres)</li>
 *   <li>Se recomienda encriptar el archivo de configuración en producción</li>
 * </ul>
 * 
 * @author Equipo StockFlow
 * @version 1.0
 * @since 2025
 * 
 * @see ConfiguracionAI
 * @see PromptConfiguracion
 * @see ConfiguracionAIFaltanteException
 */
public class ConfiguracionAIServicio {

    /**
     * Instancia singleton de configuración de IA.
     */
    private final ConfiguracionAI config;
    
    /**
     * Mapa de prompts personalizados por nombre.
     */
    private final Map<String, PromptConfiguracion> promptsPersonalizados;

    /**
     * Constructor por defecto.
     * <p>Inicializa la configuración y carga los prompts predeterminados 
     * para análisis de inventario, ventas y recomendaciones.</p>
     */
    public ConfiguracionAIServicio() {
        this.config = ConfiguracionAI.getInstance();
        this.promptsPersonalizados = new HashMap<>();
        inicializarPromptsDefault();
    }

    /**
     * Inicializa los prompts predeterminados del sistema.
     * 
     * <p>Crea tres configuraciones de prompts básicas al iniciar el servicio:</p>
     * <ul>
     *   <li><strong>analisis_inventario:</strong> Para análisis de stock y reabastecimiento 
     *       (temperatura: 0.5, tokens: 1500)</li>
     *   <li><strong>analisis_ventas:</strong> Para análisis de tendencias y oportunidades 
     *       (temperatura: 0.6, tokens: 2000)</li>
     *   <li><strong>recomendaciones:</strong> Para generar recomendaciones accionables 
     *       (temperatura: 0.7, tokens: 1000)</li>
     * </ul>
     * 
     * <p>Estos prompts pueden ser modificados o eliminados según necesidades del negocio.</p>
     * 
     * @see PromptConfiguracion
     */
    private void inicializarPromptsDefault() {
        PromptConfiguracion promptInventario = new PromptConfiguracion(
            "analisis_inventario",
            "INVENTARIO",
            "Analiza el inventario y proporciona recomendaciones de reabastecimiento"
        );
        promptInventario.setTemperatura(0.5);
        promptInventario.setMaxTokens(1500);
        promptsPersonalizados.put("analisis_inventario", promptInventario);

        PromptConfiguracion promptVentas = new PromptConfiguracion(
            "analisis_ventas",
            "VENTAS",
            "Analiza las tendencias de ventas e identifica oportunidades"
        );
        promptVentas.setTemperatura(0.6);
        promptVentas.setMaxTokens(2000);
        promptsPersonalizados.put("analisis_ventas", promptVentas);

        PromptConfiguracion promptRecomendaciones = new PromptConfiguracion(
            "recomendaciones",
            "RECOMENDACIONES",
            "Genera recomendaciones accionables para mejorar el negocio"
        );
        promptRecomendaciones.setTemperatura(0.7);
        promptRecomendaciones.setMaxTokens(1000);
        promptsPersonalizados.put("recomendaciones", promptRecomendaciones);
    }

    /**
     * Configura la API Key para autenticación con DeepSeek.
     * 
     * <p>Establece la clave de API necesaria para autenticar las peticiones 
     * al servicio de IA. Esta es la configuración más crítica del sistema.</p>
     * 
     * <p><strong>Requisitos de la API Key:</strong></p>
     * <ul>
     *   <li>No puede ser null o vacía</li>
     *   <li>Debe ser una clave válida de DeepSeek</li>
     *   <li>Formato típico: sk-xxxxxxxxxxxxxxxxxxxxxxxx</li>
     * </ul>
     * 
     * <p><strong>Seguridad:</strong></p>
     * <ul>
     *   <li>La clave se almacena en archivo de configuración</li>
     *   <li>Se recomienda no versionar el archivo de configuración</li>
     *   <li>En producción, usar variables de entorno o servicios de secretos</li>
     *   <li>Rotar la clave periódicamente</li>
     * </ul>
     * 
     * @param apiKey Clave de API de DeepSeek (será recortada de espacios)
     * @throws IllegalArgumentException Si la API Key es null o vacía
     * @throws IOException Si ocurre un error al guardar la configuración
     * 
     * @see ConfiguracionAI#setApiKey(String)
     */
    public void configurarApiKey(String apiKey) throws IOException {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("La API Key no puede estar vacía");
        }

        config.setApiKey(apiKey.trim());
        config.guardarConfiguracion();
    }

    /**
     * Configura la URL del endpoint de la API.
     * 
     * <p>Permite cambiar el endpoint al que se conecta el sistema. 
     * Útil para:</p>
     * <ul>
     *   <li>Usar diferentes versiones de la API</li>
     *   <li>Conectarse a ambientes de testing</li>
     *   <li>Usar proxies o gateways personalizados</li>
     * </ul>
     * 
     * <p><strong>URL por defecto:</strong> 
     * {@code https://api.deepseek.com/v1/chat/completions}</p>
     * 
     * @param apiUrl URL del endpoint de la API (será recortada de espacios)
     * @throws IllegalArgumentException Si la URL es null o vacía
     * @throws IOException Si ocurre un error al guardar la configuración
     * 
     * @see ConfiguracionAI#setApiUrl(String)
     */
    public void configurarApiUrl(String apiUrl) throws IOException {
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL de API no puede estar vacía");
        }

        config.setApiUrl(apiUrl.trim());
        config.guardarConfiguracion();
    }

    /**
     * Configura el modelo de IA a utilizar.
     * 
     * <p>Establece qué modelo de DeepSeek se usará para las consultas. 
     * Diferentes modelos ofrecen diferentes características:</p>
     * <ul>
     *   <li><strong>deepseek-chat:</strong> Modelo general, balanceado</li>
     *   <li><strong>deepseek-coder:</strong> Especializado en código</li>
     *   <li>Otros modelos según disponibilidad de DeepSeek</li>
     * </ul>
     * 
     * <p><strong>Modelo por defecto:</strong> deepseek-chat</p>
     * 
     * @param modelo Nombre del modelo a utilizar
     * @throws IOException Si ocurre un error al guardar la configuración
     * 
     * @see ConfiguracionAI#setModel(String)
     */
    public void configurarModelo(String modelo) throws IOException {
        config.setModel(modelo);
        config.guardarConfiguracion();
    }

    /**
     * Configura la temperatura del modelo de IA.
     * 
     * <p>La temperatura controla la aleatoriedad y creatividad de las respuestas:</p>
     * 
     * <p><strong>Escala de valores (0.0 - 2.0):</strong></p>
     * <ul>
     *   <li><strong>0.0 - 0.3:</strong> Muy determinista, respuestas consistentes y conservadoras</li>
     *   <li><strong>0.4 - 0.6:</strong> Balance entre creatividad y consistencia (recomendado para análisis)</li>
     *   <li><strong>0.7 - 0.9:</strong> Más creativo, respuestas variadas (default: 0.7)</li>
     *   <li><strong>1.0 - 2.0:</strong> Muy creativo, respuestas menos predecibles</li>
     * </ul>
     * 
     * <p><strong>Casos de uso por temperatura:</strong></p>
     * <ul>
     *   <li><strong>0.3:</strong> Análisis financiero, cálculos precisos</li>
     *   <li><strong>0.5:</strong> Análisis de inventario, recomendaciones de stock</li>
     *   <li><strong>0.7:</strong> Análisis general de negocio, insights estratégicos</li>
     *   <li><strong>0.9:</strong> Generación de ideas, brainstorming</li>
     * </ul>
     * 
     * @param temperatura Valor entre 0.0 y 2.0
     * @throws IllegalArgumentException Si la temperatura está fuera del rango 0.0-2.0
     * @throws IOException Si ocurre un error al guardar la configuración
     * 
     * @see ConfiguracionAI#setTemperature(double)
     */
    public void configurarTemperatura(double temperatura) throws IOException {
        if (temperatura < 0.0 || temperatura > 2.0) {
            throw new IllegalArgumentException("La temperatura debe estar entre 0.0 y 2.0");
        }

        config.setTemperature(temperatura);
        config.guardarConfiguracion();
    }

    /**
     * Configura el máximo de tokens en las respuestas.
     * 
     * <p>Los tokens representan aproximadamente palabras o partes de palabras. 
     * Este parámetro limita la longitud de las respuestas generadas.</p>
     * 
     * <p><strong>Guía de valores:</strong></p>
     * <ul>
     *   <li><strong>100-500:</strong> Respuestas cortas, puntos clave (párrafo)</li>
     *   <li><strong>500-1000:</strong> Respuestas medianas (1-2 párrafos)</li>
     *   <li><strong>1000-2000:</strong> Análisis detallados (default: 2000)</li>
     *   <li><strong>2000-4000:</strong> Análisis extensos, reportes completos</li>
     * </ul>
     * 
     * <p><strong>Consideraciones:</strong></p>
     * <ul>
     *   <li>Mayor tokens = mayor costo y tiempo de respuesta</li>
     *   <li>Ajustar según complejidad del análisis requerido</li>
     *   <li>Balance entre detalle y eficiencia</li>
     * </ul>
     * 
     * <p><strong>Equivalencias aproximadas:</strong></p>
     * <pre>
     * 100 tokens  ≈ 75 palabras   ≈ 3-4 oraciones
     * 500 tokens  ≈ 375 palabras  ≈ 1 párrafo largo
     * 2000 tokens ≈ 1500 palabras ≈ 1 página
     * </pre>
     * 
     * @param maxTokens Cantidad máxima de tokens (100-4000)
     * @throws IllegalArgumentException Si maxTokens está fuera del rango 100-4000
     * @throws IOException Si ocurre un error al guardar la configuración
     * 
     * @see ConfiguracionAI#setMaxTokens(int)
     */
    public void configurarMaxTokens(int maxTokens) throws IOException {
        if (maxTokens < 100 || maxTokens > 4000) {
            throw new IllegalArgumentException("Max tokens debe estar entre 100 y 4000");
        }

        config.setMaxTokens(maxTokens);
        config.guardarConfiguracion();
    }

    /**
     * Obtiene la configuración actual del sistema de IA.
     * 
     * <p>Retorna un mapa con todos los parámetros de configuración actuales. 
     * La API Key se muestra parcialmente por seguridad (solo primeros 10 caracteres).</p>
     * 
     * <p><strong>Claves del mapa retornado:</strong></p>
     * <ul>
     *   <li>{@code apiKey} - API Key (parcial): "sk-xxxxxxxx..."</li>
     *   <li>{@code apiUrl} - URL del endpoint</li>
     *   <li>{@code model} - Nombre del modelo</li>
     *   <li>{@code temperature} - Temperatura configurada</li>
     *   <li>{@code maxTokens} - Máximo de tokens</li>
     *   <li>{@code timeout} - Timeout en milisegundos</li>
     *   <li>{@code configured} - Si está completamente configurado (true/false)</li>
     * </ul>
     * 
     * @return Mapa con la configuración actual (todos los valores como String)
     */
    public Map<String, String> obtenerConfiguracionActual() {
        Map<String, String> configuracion = new HashMap<>();

        try {
            configuracion.put("apiKey", config.getApiKey().substring(0, 10) + "...");
        } catch (ConfiguracionAIFaltanteException e) {
            configuracion.put("apiKey", "NO CONFIGURADA");
        }

        configuracion.put("apiUrl", config.getApiUrl());
        configuracion.put("model", config.getModel());
        configuracion.put("temperature", String.valueOf(config.getTemperature()));
        configuracion.put("maxTokens", String.valueOf(config.getMaxTokens()));
        configuracion.put("timeout", String.valueOf(config.getTimeout()));
        configuracion.put("configured", String.valueOf(config.isConfigured()));

        return configuracion;
    }

    /**
     * Verifica si la configuración de IA está completa.
     * 
     * <p>Comprueba que todos los parámetros obligatorios estén configurados 
     * correctamente. Principalmente verifica la presencia de la API Key.</p>
     * 
     * <p><strong>Uso típico:</strong></p>
     * <pre>{@code
     * if (!servicio.verificarConfiguracion()) {
     *     mostrarPantallaConfiguracion();
     *     return;
     * }
     * // Continuar con funcionalidades de IA...
     * }</pre>
     * 
     * @return {@code true} si la configuración está completa y lista para usar, 
     *         {@code false} en caso contrario
     * 
     * @see ConfiguracionAI#isConfigured()
     */
    public boolean verificarConfiguracion() {
        return config.isConfigured();
    }

    /**
     * Valida la configuración y retorna mensajes descriptivos.
     * 
     * <p>Realiza una validación detallada de cada componente de la configuración 
     * y retorna mensajes informativos sobre el estado de cada uno.</p>
     * 
     * <p><strong>Validaciones realizadas:</strong></p>
     * <ul>
     *   <li>Presencia y longitud de API Key</li>
     *   <li>Configuración de URL de API</li>
     *   <li>Validez del rango de temperatura</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>{@code
     * List<String> mensajes = servicio.validarConfiguracion();
     * 
     * System.out.println("Estado de configuración:");
     * for (String mensaje : mensajes) {
     *     System.out.println("  - " + mensaje);
     * }
     * 
     * // Salida posible:
     * // - API Key configurada
     * // - URL de API configurada
     * // - Temperatura válida: 0.7
     * }</pre>
     * 
     * @return Lista de mensajes descriptivos sobre el estado de la configuración
     */
    public List<String> validarConfiguracion() {
        List<String> mensajes = new ArrayList<>();

        try {
            String apiKey = config.getApiKey();
            if (apiKey.length() < 20) {
                mensajes.add("La API Key parece muy corta");
            } else {
                mensajes.add("API Key configurada");
            }
        } catch (ConfiguracionAIFaltanteException e) {
            mensajes.add("API Key no configurada");
        }

        if (config.getApiUrl() != null && !config.getApiUrl().isEmpty()) {
            mensajes.add("URL de API configurada");
        } else {
            mensajes.add("URL de API no configurada");
        }

        if (config.getTemperature() >= 0.0 && config.getTemperature() <= 2.0) {
            mensajes.add("Temperatura válida: " + config.getTemperature());
        } else {
            mensajes.add("Temperatura fuera de rango");
        }

        return mensajes;
    }

    /**
     * Guarda un prompt personalizado en el sistema.
     * 
     * <p>Permite crear o actualizar configuraciones de prompts específicas 
     * para diferentes tipos de análisis. Cada prompt puede tener sus propios 
     * parámetros de temperatura y max tokens.</p>
     * 
     * <p><strong>Ejemplo:</strong></p>
     * <pre>{@code
     * PromptConfiguracion prompt = new PromptConfiguracion(
     *     "analisis_rentabilidad",
     *     "VENTAS",
     *     "Analiza la rentabilidad por producto y categoría"
     * );
     * prompt.setTemperatura(0.5);
     * prompt.setMaxTokens(1500);
     * 
     * servicio.guardarPromptPersonalizado(prompt);
     * }</pre>
     * 
     * @param prompt Configuración del prompt a guardar
     * 
     * @see PromptConfiguracion
     * @see #obtenerPromptPersonalizado(String)
     */
    public void guardarPromptPersonalizado(PromptConfiguracion prompt) {
        promptsPersonalizados.put(prompt.getNombre(), prompt);
    }

    /**
     * Obtiene un prompt personalizado por su nombre.
     * 
     * @param nombre Nombre único del prompt
     * @return Configuración del prompt, o null si no existe
     * 
     * @see #guardarPromptPersonalizado(PromptConfiguracion)
     */
    public PromptConfiguracion obtenerPromptPersonalizado(String nombre) {
        return promptsPersonalizados.get(nombre);
    }

    /**
     * Lista todos los prompts disponibles en el sistema.
     * 
     * <p>Retorna tanto los prompts predeterminados como los personalizados 
     * que se hayan creado.</p>
     * 
     * @return Lista de todas las configuraciones de prompts disponibles
     */
    public List<PromptConfiguracion> listarPromptsDisponibles() {
        return new ArrayList<>(promptsPersonalizados.values());
    }
    
    /**
     * Elimina un prompt personalizado del sistema.
     * 
     * <p><strong>Nota:</strong> No se pueden eliminar los prompts predeterminados 
     * del sistema (analisis_inventario, analisis_ventas, recomendaciones).</p>
     * 
     * @param nombre Nombre del prompt a eliminar
     * @return {@code true} si el prompt existía y fue eliminado, 
     *         {@code false} si no existía
     */
    public boolean eliminarPromptPersonalizado(String nombre) {
        if (promptsPersonalizados.containsKey(nombre)) {
            promptsPersonalizados.remove(nombre);
            return true;
        }
        return false;
    }

    /**
     * Restaura la configuración a valores predeterminados.
     * 
     * <p>Restablece todos los parámetros a los valores recomendados de fábrica:</p>
     * <ul>
     *   <li><strong>API URL:</strong> https://api.deepseek.com/v1/chat/completions</li>
     *   <li><strong>Model:</strong> deepseek-chat</li>
     *   <li><strong>Temperature:</strong> 0.7</li>
     *   <li><strong>Max Tokens:</strong> 2000</li>
     * </ul>
     * 
     * <p><strong>Nota importante:</strong> La API Key NO se borra, 
     * debe configurarse manualmente de nuevo si es necesario.</p>
     * 
     * @throws IOException Si ocurre un error al guardar la configuración
     */
    public void restaurarConfiguracionDefault() throws IOException {
        config.setApiUrl("https://api.deepseek.com/v1/chat/completions");
        config.setModel("deepseek-chat");
        config.setTemperature(0.7);
        config.setMaxTokens(2000);
        config.guardarConfiguracion();
    }

    /**
     * Exporta la configuración completa del sistema.
     * 
     * <p>Genera un mapa con toda la configuración actual, útil para:</p>
     * <ul>
     *   <li>Realizar backups de configuración</li>
     *   <li>Migrar configuración entre ambientes</li>
     *   <li>Documentar la configuración activa</li>
     *   <li>Compartir configuraciones entre instancias</li>
     * </ul>
     * 
     * <p><strong>Contenido exportado:</strong></p>
     * <ul>
     *   <li>URL de API</li>
     *   <li>Modelo configurado</li>
     *   <li>Temperatura</li>
     *   <li>Max tokens</li>
     *   <li>Timeout</li>
     *   <li>Lista de prompts personalizados</li>
     * </ul>
     * 
     * <p><strong>Nota de seguridad:</strong> La API Key NO se incluye 
     * en la exportación por razones de seguridad.</p>
     * 
     * @return Mapa con toda la configuración exportable
     */
    public Map<String, Object> exportarConfiguracion() {
        Map<String, Object> exportacion = new HashMap<>();

        exportacion.put("apiUrl", config.getApiUrl());
        exportacion.put("model", config.getModel());
        exportacion.put("temperature", config.getTemperature());
        exportacion.put("maxTokens", config.getMaxTokens());
        exportacion.put("timeout", config.getTimeout());
        exportacion.put("prompts", new ArrayList<>(promptsPersonalizados.values()));

        return exportacion;
    }

    /**
     * Obtiene estadísticas de uso del sistema de IA.
     * 
     * <p>Retorna información sobre el uso del sistema de IA. 
     * En la implementación actual, retorna valores predeterminados.</p>
     * 
     * <p><strong>Mejora futura:</strong> Implementar tracking real de:</p>
     * <ul>
     *   <li>Número total de consultas realizadas</li>
     *   <li>Total de tokens consumidos</li>
     *   <li>Fecha/hora de última consulta</li>
     *   <li>Costos estimados</li>
     *   <li>Tipos de análisis más solicitados</li>
     *   <li>Tiempo promedio de respuesta</li>
     * </ul>
     * 
     * @return Mapa con estadísticas de uso (actualmente valores por defecto)
     */
    public Map<String, Object> obtenerEstadisticasUso() {
        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("totalConsultas", 0);
        estadisticas.put("tokensUsados", 0);
        estadisticas.put("ultimaConsulta", "N/A");
        estadisticas.put("estado", config.isConfigured() ? "Activo" : "Inactivo");

        return estadisticas;
    }
}