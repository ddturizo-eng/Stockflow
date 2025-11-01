    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.time.LocalDateTime;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.UUID;

    /**
     * Resultado completo de un análisis realizado con inteligencia artificial.
     * 
     * <p>Esta clase unifica el análisis textual generado por IA con datos
     * estructurados para gráficas y métricas numéricas. Es el contenedor
     * principal para resultados del módulo de inteligencia de negocio,
     * facilitando tanto la visualización como el procesamiento de insights.</p>
     * 
     * <p><strong>Componentes del resultado:</strong></p>
     * <ul>
     *   <li><strong>Análisis textual</strong>: Interpretación en lenguaje natural generada por IA</li>
     *   <li><strong>Datos de gráfica</strong>: Información estructurada para visualización</li>
     *   <li><strong>Métricas</strong>: Indicadores numéricos clave (KPIs)</li>
     *   <li><strong>Recomendaciones</strong>: Acciones sugeridas basadas en el análisis</li>
     * </ul>
     * 
     * <p><strong>Tipos de análisis soportados:</strong></p>
     * <ul>
     *   <li><strong>VENTAS</strong>: Análisis de desempeño de ventas</li>
     *   <li><strong>INVENTARIO</strong>: Análisis de stock y rotación</li>
     *   <li><strong>COMPLETO</strong>: Análisis integral del negocio</li>
     *   <li><strong>RENTABILIDAD</strong>: Análisis de márgenes y ganancias</li>
     *   <li><strong>PREDICTIVO</strong>: Predicciones y proyecciones</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * // Crear resultado
     * ResultadoAnalisisIA resultado = new ResultadoAnalisisIA();
     * resultado.setTipoAnalisis("VENTAS");
     * resultado.setAnalisisTexto("Las ventas han crecido un 15%...");
     * 
     * // Agregar métricas
     * resultado.agregarMetrica("totalVentas", 50);
     * resultado.agregarMetrica("ingresosTotal", 25000000.0);
     * 
     * // Agregar recomendaciones
     * resultado.agregarRecomendacion(
     *     "Aumentar stock",
     *     "Los productos X, Y, Z están agotándose rápidamente"
     * );
     * 
     * // Establecer datos para gráfica
     * DatosGrafica grafica = new DatosGrafica();
     * grafica.setTipo(TipoGrafica.LINEA);
     * resultado.setDatosGrafica(grafica);
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see DatosGrafica
     * @see AnalisisEstadistico
     * @see ContextoNegocio
     */
    public class ResultadoAnalisisIA {

        /** Identificador único del resultado */
        private String id;

        /** Fecha y hora de generación del análisis */
        private LocalDateTime fechaGeneracion;

        /** Tipo de análisis realizado (VENTAS, INVENTARIO, COMPLETO, etc.) */
        private String tipoAnalisis;

        /** Respuesta textual generada por el motor de IA (DeepSeek, etc.) */
        private String analisisTexto;

        /** Datos estructurados para renderizar gráficas */
        private DatosGrafica datosGrafica;

        /** Mapa de métricas extraídas del análisis (KPIs y valores calculados) */
        private Map<String, Object> metricas;

        /** Mapa de recomendaciones accionables (título → descripción) */
        private Map<String, String> recomendaciones;

        /**
         * Constructor por defecto que inicializa un resultado vacío.
         * Genera un ID único (UUID) y establece la fecha actual.
         * Inicializa los mapas de métricas y recomendaciones vacíos.
         */
        public ResultadoAnalisisIA() {
            this.id = UUID.randomUUID().toString();
            this.fechaGeneracion = LocalDateTime.now();
            this.metricas = new HashMap<>();
            this.recomendaciones = new HashMap<>();
        }

        /**
         * Constructor completo que inicializa con tipo, texto y datos de gráfica.
         * 
         * @param tipoAnalisis el tipo de análisis realizado
         * @param analisisTexto el texto interpretativo generado por IA
         * @param datosGrafica los datos estructurados para visualización
         */
        public ResultadoAnalisisIA(String tipoAnalisis, String analisisTexto, DatosGrafica datosGrafica) {
            this();
            this.tipoAnalisis = tipoAnalisis;
            this.analisisTexto = analisisTexto;
            this.datosGrafica = datosGrafica;
        }

        /**
         * Obtiene el identificador único del resultado.
         * 
         * @return el ID del resultado
         */
        public String getId() { 
            return id; 
        }

        /**
         * Establece el identificador del resultado.
         * 
         * @param id el nuevo ID
         */
        public void setId(String id) { 
            this.id = id; 
        }

        /**
         * Obtiene la fecha de generación del análisis.
         * 
         * @return la fecha y hora de generación
         */
        public LocalDateTime getFechaGeneracion() { 
            return fechaGeneracion; 
        }

        /**
         * Establece la fecha de generación.
         * 
         * @param fechaGeneracion la nueva fecha
         */
        public void setFechaGeneracion(LocalDateTime fechaGeneracion) { 
            this.fechaGeneracion = fechaGeneracion; 
        }

        /**
         * Obtiene el tipo de análisis realizado.
         * 
         * @return el tipo de análisis (VENTAS, INVENTARIO, COMPLETO, etc.)
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
         * Obtiene el texto del análisis generado por IA.
         * 
         * @return el análisis en lenguaje natural
         */
        public String getAnalisisTexto() { 
            return analisisTexto; 
        }

        /**
         * Establece el texto del análisis.
         * 
         * @param analisisTexto el nuevo texto de análisis
         */
        public void setAnalisisTexto(String analisisTexto) { 
            this.analisisTexto = analisisTexto; 
        }

        /**
         * Obtiene los datos estructurados para gráficas.
         * 
         * @return los datos de la gráfica
         */
        public DatosGrafica getDatosGrafica() { 
            return datosGrafica; 
        }

        /**
         * Establece los datos para gráficas.
         * 
         * @param datosGrafica los nuevos datos de gráfica
         */
        public void setDatosGrafica(DatosGrafica datosGrafica) { 
            this.datosGrafica = datosGrafica; 
        }

        /**
         * Obtiene el mapa de métricas calculadas.
         * 
         * @return el mapa de métricas (clave → valor)
         */
        public Map<String, Object> getMetricas() { 
            return metricas; 
        }

        /**
         * Establece el mapa completo de métricas.
         * 
         * @param metricas el nuevo mapa de métricas
         */
        public void setMetricas(Map<String, Object> metricas) { 
            this.metricas = metricas; 
        }

        /**
         * Obtiene el mapa de recomendaciones.
         * 
         * @return el mapa de recomendaciones (título → descripción)
         */
        public Map<String, String> getRecomendaciones() { 
            return recomendaciones; 
        }

        /**
         * Establece el mapa completo de recomendaciones.
         * 
         * @param recomendaciones el nuevo mapa de recomendaciones
         */
        public void setRecomendaciones(Map<String, String> recomendaciones) { 
            this.recomendaciones = recomendaciones; 
        }

        /**
         * Agrega una métrica individual al resultado.
         * Si ya existe una métrica con la misma clave, se sobrescribe.
         * 
         * @param clave el identificador de la métrica
         * @param valor el valor de la métrica (puede ser cualquier tipo)
         */
        public void agregarMetrica(String clave, Object valor) {
            this.metricas.put(clave, valor);
        }

        /**
         * Agrega una recomendación individual al resultado.
         * Si ya existe una recomendación con el mismo título, se sobrescribe.
         * 
         * @param titulo el título de la recomendación
         * @param descripcion la descripción detallada de la recomendación
         */
        public void agregarRecomendacion(String titulo, String descripcion) {
            this.recomendaciones.put(titulo, descripcion);
        }

        /**
         * Devuelve una representación en cadena del resultado.
         * Incluye tipo, fecha, cantidad de métricas y recomendaciones.
         * 
         * @return cadena con resumen del resultado
         */
        @Override
        public String toString() {
            return "ResultadoAnalisisIA{" +
                    "id='" + id + '\'' +
                    ", tipoAnalisis='" + tipoAnalisis + '\'' +
                    ", fechaGeneracion=" + fechaGeneracion +
                    ", metricas=" + metricas.size() +
                    ", recomendaciones=" + recomendaciones.size() +
                    '}';
        }
    }