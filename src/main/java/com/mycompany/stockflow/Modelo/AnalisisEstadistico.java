    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.HashMap;
    import java.util.Map;

    /**
     * Representa un análisis estadístico realizado sobre datos del negocio.
     * 
     * <p>Esta clase almacena el resultado de análisis estadísticos realizados
     * sobre la información del negocio, incluyendo métricas calculadas,
     * resúmenes generados por inteligencia artificial, conclusiones y
     * tendencias identificadas.</p>
     * 
     * <p><strong>Componentes del análisis:</strong></p>
     * <ul>
     *   <li><strong>Tipo de análisis</strong>: Categoría del análisis realizado</li>
     *   <li><strong>Métricas</strong>: Indicadores numéricos calculados (Map flexible)</li>
     *   <li><strong>Resumen IA</strong>: Interpretación en lenguaje natural</li>
     *   <li><strong>Conclusiones</strong>: Hallazgos principales del análisis</li>
     *   <li><strong>Tendencias</strong>: Patrones identificados en los datos</li>
     * </ul>
     * 
     * <p><strong>Tipos de análisis comunes:</strong></p>
     * <ul>
     *   <li>VENTAS - Análisis de desempeño de ventas</li>
     *   <li>INVENTARIO - Análisis de rotación y stock</li>
     *   <li>RENTABILIDAD - Análisis de márgenes y ganancias</li>
     *   <li>CLIENTES - Análisis de comportamiento de clientes</li>
     *   <li>PRODUCTOS - Análisis de productos más/menos vendidos</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * AnalisisEstadistico analisis = new AnalisisEstadistico("VENTAS");
     * 
     * // Agregar métricas calculadas
     * analisis.agregarMetrica("totalVentas", 50);
     * analisis.agregarMetrica("ingresoTotal", 25000000.0);
     * analisis.agregarMetrica("promedioVenta", 500000.0);
     * 
     * // Establecer interpretaciones
     * analisis.setResumenIA("Las ventas muestran crecimiento del 15%...");
     * analisis.setConclusiones("Mayor demanda en electrónica...");
     * analisis.setTendencias("Tendencia alcista en los últimos 3 meses");
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see ContextoNegocio
     * @see ResultadoAnalisisIA
     */
    public class AnalisisEstadistico extends Entidad {

        /** Tipo o categoría del análisis estadístico */
        private String tipoAnalisis;

        /** Fecha y hora en que se realizó el análisis */
        private LocalDateTime fechaAnalisis;

        /** Mapa de métricas calculadas (clave: nombre métrica, valor: valor calculado) */
        private Map<String, Object> metricas;

        /** Resumen generado por inteligencia artificial */
        private String resumenIA;

        /** Conclusiones principales del análisis */
        private String conclusiones;

        /** Tendencias identificadas en los datos */
        private String tendencias;

        /**
         * Constructor por defecto que inicializa un análisis vacío.
         * La fecha se establece automáticamente a la hora actual.
         * El mapa de métricas se inicializa vacío.
         */
        public AnalisisEstadistico() {
            super();
            this.fechaAnalisis = LocalDateTime.now();
            this.metricas = new HashMap<>();
        }

        /**
         * Constructor que crea un análisis con tipo específico.
         * Genera un ID único basado en el tipo y timestamp.
         * 
         * @param tipoAnalisis el tipo o categoría del análisis
         */
        public AnalisisEstadistico(String tipoAnalisis) {
            super(tipoAnalisis + "_" + System.currentTimeMillis());
            this.tipoAnalisis = tipoAnalisis;
            this.fechaAnalisis = LocalDateTime.now();
            this.metricas = new HashMap<>();
        }

        /**
         * Obtiene el tipo de análisis.
         * 
         * @return el tipo o categoría del análisis
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
         * Obtiene la fecha y hora del análisis.
         * 
         * @return la fecha en que se realizó el análisis
         */
        public LocalDateTime getFechaAnalisis() {
            return fechaAnalisis;
        }

        /**
         * Establece la fecha y hora del análisis.
         * 
         * @param fechaAnalisis la nueva fecha del análisis
         */
        public void setFechaAnalisis(LocalDateTime fechaAnalisis) {
            this.fechaAnalisis = fechaAnalisis;
        }

        /**
         * Obtiene el mapa de métricas calculadas.
         * 
         * @return el mapa de métricas (nombre → valor)
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
         * Agrega una métrica individual al análisis.
         * Si ya existe una métrica con el mismo nombre, se sobrescribe.
         * 
         * @param nombre el nombre identificador de la métrica
         * @param valor el valor calculado de la métrica (puede ser cualquier tipo)
         */
        public void agregarMetrica(String nombre, Object valor) {
            this.metricas.put(nombre, valor);
        }

        /**
         * Obtiene el valor de una métrica específica.
         * 
         * @param nombre el nombre de la métrica a obtener
         * @return el valor de la métrica, o null si no existe
         */
        public Object obtenerMetrica(String nombre) {
            return this.metricas.get(nombre);
        }

        /**
         * Obtiene el resumen generado por IA.
         * 
         * @return el resumen interpretativo del análisis
         */
        public String getResumenIA() {
            return resumenIA;
        }

        /**
         * Establece el resumen generado por inteligencia artificial.
         * 
         * @param resumenIA el nuevo resumen del análisis
         */
        public void setResumenIA(String resumenIA) {
            this.resumenIA = resumenIA;
        }

        /**
         * Obtiene las conclusiones del análisis.
         * 
         * @return las conclusiones principales
         */
        public String getConclusiones() {
            return conclusiones;
        }

        /**
         * Establece las conclusiones del análisis.
         * 
         * @param conclusiones las nuevas conclusiones
         */
        public void setConclusiones(String conclusiones) {
            this.conclusiones = conclusiones;
        }

        /**
         * Obtiene las tendencias identificadas.
         * 
         * @return las tendencias detectadas en los datos
         */
        public String getTendencias() {
            return tendencias;
        }

        /**
         * Establece las tendencias identificadas.
         * 
         * @param tendencias las nuevas tendencias
         */
        public void setTendencias(String tendencias) {
            this.tendencias = tendencias;
        }

        /**
         * Formatea la fecha del análisis en formato legible.
         * 
         * @return la fecha formateada como "dd/MM/yyyy HH:mm"
         */
        public String getFechaFormateada() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return fechaAnalisis.format(formatter);
        }

        /**
         * Devuelve una representación en cadena del análisis.
         * 
         * @return cadena con el tipo y fecha del análisis
         */
        @Override
        public String toString() {
            return "Analisis " + tipoAnalisis + " - " + getFechaFormateada();
        }
    }