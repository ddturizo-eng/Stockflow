    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    /**
     * Configuración de prompts para análisis de inteligencia artificial.
     * 
     * <p>Esta clase define plantillas y parámetros de configuración para las
     * consultas que se envían a los servicios de IA (como DeepSeek). Permite
     * personalizar el comportamiento del análisis según el tipo de información
     * requerida, optimizando la calidad y relevancia de las respuestas.</p>
     * 
     * <p><strong>Componentes de configuración:</strong></p>
     * <ul>
     *   <li><strong>Template prompt</strong>: Estructura base de la consulta</li>
     *   <li><strong>Temperatura</strong>: Controla la creatividad (0.0 = determinista, 1.0 = creativa)</li>
     *   <li><strong>Max tokens</strong>: Longitud máxima de la respuesta</li>
     *   <li><strong>Contexto adicional</strong>: Información complementaria para el análisis</li>
     * </ul>
     * 
     * <p><strong>Parámetros de temperatura recomendados:</strong></p>
     * <ul>
     *   <li><strong>0.0 - 0.3</strong>: Análisis técnicos y numéricos (determinista)</li>
     *   <li><strong>0.4 - 0.7</strong>: Análisis balanceados (recomendado por defecto)</li>
     *   <li><strong>0.8 - 1.0</strong>: Análisis creativos y exploratorios</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * PromptConfiguracion config = new PromptConfiguracion(
     *     "Análisis de Ventas",
     *     "VENTAS",
     *     "Analiza las siguientes ventas: {datos}\n" +
     *     "Proporciona insights sobre tendencias y oportunidades."
     * );
     * 
     * config.setTemperatura(0.5);
     * config.setMaxTokens(3000);
     * config.setContextoAdicional("Enfocarse en productos electrónicos");
     * config.setActivo(true);
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see ResultadoAnalisisIA
     * @see AnalisisEstadistico
     */
    public class PromptConfiguracion extends Entidad {

        /** Nombre descriptivo de la configuración */
        private String nombre;

        /** Tipo de análisis al que aplica esta configuración */
        private String tipoAnalisis;

        /** Plantilla del prompt con marcadores de posición para datos */
        private String templatePrompt;

        /** Contexto adicional que se incluirá en el prompt */
        private String contextoAdicional;

        /** Indica si esta configuración está activa para uso */
        private boolean activo;

        /** 
         * Temperatura del modelo IA (0.0 - 1.0)
         * Controla la aleatoriedad y creatividad de las respuestas
         */
        private double temperatura;

        /** Número máximo de tokens en la respuesta del modelo */
        private int maxTokens;

        /**
         * Constructor por defecto que inicializa una configuración vacía.
         * Establece valores por defecto: activo=true, temperatura=0.7, maxTokens=2000.
         */
        public PromptConfiguracion() {
            super();
            this.activo = true;
            this.temperatura = 0.7;
            this.maxTokens = 2000;
        }

        /**
         * Constructor que crea una configuración con nombre, tipo y template.
         * Establece el nombre como ID de la entidad.
         * 
         * @param nombre el nombre descriptivo de la configuración
         * @param tipoAnalisis el tipo de análisis (VENTAS, INVENTARIO, etc.)
         * @param templatePrompt la plantilla del prompt
         */
        public PromptConfiguracion(String nombre, String tipoAnalisis, String templatePrompt) {
            super(nombre);
            this.nombre = nombre;
            this.tipoAnalisis = tipoAnalisis;
            this.templatePrompt = templatePrompt;
            this.activo = true;
            this.temperatura = 0.7;
            this.maxTokens = 2000;
        }

        /**
         * Obtiene el nombre de la configuración.
         * 
         * @return el nombre descriptivo
         */
        public String getNombre() {
            return nombre;
        }

        /**
         * Establece el nombre de la configuración y actualiza el ID.
         * 
         * @param nombre el nuevo nombre
         */
        public void setNombre(String nombre) {
            this.nombre = nombre;
            setId(nombre);
        }

        /**
         * Obtiene el tipo de análisis asociado.
         * 
         * @return el tipo de análisis
         */
        public String getTipoAnalisis() {
            return tipoAnalisis;
        }

        /**
         * Establece el tipo de análisis.
         * 
         * @param tipoAnalisis el nuevo tipo de análisis
         */
        public void setTipoAnalisis(String tipoAnalisis) {
            this.tipoAnalisis = tipoAnalisis;
        }

        /**
         * Obtiene la plantilla del prompt.
         * La plantilla puede contener marcadores como {datos}, {periodo}, etc.
         * que serán reemplazados con valores reales al ejecutar el análisis.
         * 
         * @return la plantilla del prompt
         */
        public String getTemplatePrompt() {
            return templatePrompt;
        }

        /**
         * Establece la plantilla del prompt.
         * 
         * @param templatePrompt la nueva plantilla
         */
        public void setTemplatePrompt(String templatePrompt) {
            this.templatePrompt = templatePrompt;
        }

        /**
         * Obtiene el contexto adicional.
         * 
         * @return el contexto adicional para enriquecer el análisis
         */
        public String getContextoAdicional() {
            return contextoAdicional;
        }

        /**
         * Establece el contexto adicional.
         * El contexto puede incluir información del negocio, objetivos específicos, etc.
         * 
         * @param contextoAdicional el nuevo contexto adicional
         */
        public void setContextoAdicional(String contextoAdicional) {
            this.contextoAdicional = contextoAdicional;
        }

        /**
         * Verifica si la configuración está activa.
         * 
         * @return {@code true} si está activa y puede ser usada
         */
        public boolean isActivo() {
            return activo;
        }

        /**
         * Establece el estado de activación de la configuración.
         * 
         * @param activo {@code true} para activar, {@code false} para desactivar
         */
        public void setActivo(boolean activo) {
            this.activo = activo;
        }

        /**
         * Obtiene la temperatura del modelo IA.
         * 
         * @return la temperatura (0.0 - 1.0)
         */
        public double getTemperatura() {
            return temperatura;
        }

        /**
         * Establece la temperatura del modelo IA.
         * Valores más bajos generan respuestas más deterministas,
         * valores más altos generan respuestas más creativas.
         * 
         * @param temperatura la nueva temperatura (recomendado: 0.0 - 1.0)
         */
        public void setTemperatura(double temperatura) {
            this.temperatura = temperatura;
        }

        /**
         * Obtiene el número máximo de tokens.
         * 
         * @return el límite de tokens en la respuesta
         */
        public int getMaxTokens() {
            return maxTokens;
        }

        /**
         * Establece el número máximo de tokens en la respuesta.
         * Un token equivale aproximadamente a 0.75 palabras en español.
         * 
         * @param maxTokens el nuevo límite de tokens
         */
        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }

        /**
         * Devuelve una representación en cadena de la configuración.
         * 
         * @return cadena con el nombre y tipo de análisis
         */
        @Override
        public String toString() {
            return nombre + " (" + tipoAnalisis + ")";
        }
    }