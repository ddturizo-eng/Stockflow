    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;

    /**
     * Representa una recomendación de negocio generada por el sistema.
     * 
     * <p>Las recomendaciones son sugerencias automáticas generadas mediante
     * análisis de datos e inteligencia artificial para optimizar la gestión
     * del negocio. Incluyen acciones específicas, justificación y nivel de
     * prioridad para facilitar la toma de decisiones.</p>
     * 
     * <p><strong>Tipos de recomendaciones comunes:</strong></p>
     * <ul>
     *   <li><strong>REABASTECIMIENTO</strong>: Sugerencia de compra de productos con stock bajo</li>
     *   <li><strong>AJUSTE_PRECIO</strong>: Modificación de precios para optimizar ventas</li>
     *   <li><strong>PROMOCION</strong>: Creación de promociones para productos específicos</li>
     *   <li><strong>DESCONTINUAR</strong>: Eliminación de productos de baja rotación</li>
     *   <li><strong>OPORTUNIDAD</strong>: Identificación de oportunidades de negocio</li>
     * </ul>
     * 
     * <p><strong>Niveles de prioridad:</strong></p>
     * <ul>
     *   <li><strong>ALTA</strong>: Requiere atención inmediata</li>
     *   <li><strong>MEDIA</strong>: Debe considerarse en corto plazo</li>
     *   <li><strong>BAJA</strong>: Sugerencia opcional para mejora gradual</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * Recomendacion recomendacion = new Recomendacion(
     *     "REABASTECIMIENTO",
     *     "P001",
     *     "Reabastecer Laptop HP",
     *     "El stock está por debajo del mínimo (3 unidades)",
     *     "ALTA"
     * );
     * 
     * recomendacion.setAccionRecomendada("Ordenar 20 unidades al proveedor");
     * recomendacion.setJustificacion("Producto de alta rotación con ventas constantes");
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see AnalisisEstadistico
     * @see ResultadoAnalisisIA
     */
    public class Recomendacion extends Entidad {

        /** Tipo de recomendación (REABASTECIMIENTO, AJUSTE_PRECIO, etc.) */
        private String tipo;

        /** ID del producto relacionado con la recomendación (opcional) */
        private String productoId;

        /** Título descriptivo de la recomendación */
        private String titulo;

        /** Descripción detallada de la recomendación */
        private String descripcion;

        /** Nivel de prioridad (ALTA, MEDIA, BAJA) */
        private String prioridad;

        /** Fecha y hora en que se generó la recomendación */
        private LocalDateTime fechaGeneracion;

        /** Acción específica recomendada */
        private String accionRecomendada;

        /** Justificación o razón de la recomendación */
        private String justificacion;

        /** Indica si la recomendación ha sido aplicada o ejecutada */
        private boolean aplicada;

        /**
         * Constructor por defecto que inicializa una recomendación vacía.
         * La fecha se establece automáticamente y el estado es "no aplicada".
         */
        public Recomendacion() {
            super();
            this.fechaGeneracion = LocalDateTime.now();
            this.aplicada = false;
        }

        /**
         * Constructor completo para crear una recomendación.
         * Genera un ID único basado en tipo, productoId y timestamp.
         * 
         * @param tipo el tipo de recomendación
         * @param productoId el ID del producto relacionado (puede ser null)
         * @param titulo el título de la recomendación
         * @param descripcion la descripción detallada
         * @param prioridad el nivel de prioridad (ALTA, MEDIA, BAJA)
         */
        public Recomendacion(String tipo, String productoId, String titulo, String descripcion, String prioridad) {
            super(tipo + "_" + productoId + "_" + System.currentTimeMillis());
            this.tipo = tipo;
            this.productoId = productoId;
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.prioridad = prioridad;
            this.fechaGeneracion = LocalDateTime.now();
            this.aplicada = false;
        }

        /**
         * Obtiene el tipo de recomendación.
         * 
         * @return el tipo de recomendación
         */
        public String getTipo() {
            return tipo;
        }

        /**
         * Establece el tipo de recomendación.
         * 
         * @param tipo el nuevo tipo
         */
        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        /**
         * Obtiene el ID del producto relacionado.
         * 
         * @return el ID del producto, puede ser null si no aplica
         */
        public String getProductoId() {
            return productoId;
        }

        /**
         * Establece el ID del producto relacionado.
         * 
         * @param productoId el nuevo ID del producto
         */
        public void setProductoId(String productoId) {
            this.productoId = productoId;
        }

        /**
         * Obtiene el título de la recomendación.
         * 
         * @return el título descriptivo
         */
        public String getTitulo() {
            return titulo;
        }

        /**
         * Establece el título de la recomendación.
         * 
         * @param titulo el nuevo título
         */
        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        /**
         * Obtiene la descripción de la recomendación.
         * 
         * @return la descripción detallada
         */
        public String getDescripcion() {
            return descripcion;
        }

        /**
         * Establece la descripción de la recomendación.
         * 
         * @param descripcion la nueva descripción
         */
        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        /**
         * Obtiene el nivel de prioridad.
         * 
         * @return la prioridad (ALTA, MEDIA, BAJA)
         */
        public String getPrioridad() {
            return prioridad;
        }

        /**
         * Establece el nivel de prioridad.
         * 
         * @param prioridad la nueva prioridad
         */
        public void setPrioridad(String prioridad) {
            this.prioridad = prioridad;
        }

        /**
         * Obtiene la fecha de generación de la recomendación.
         * 
         * @return la fecha y hora de generación
         */
        public LocalDateTime getFechaGeneracion() {
            return fechaGeneracion;
        }

        /**
         * Establece la fecha de generación.
         * 
         * @param fechaGeneracion la nueva fecha de generación
         */
        public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
            this.fechaGeneracion = fechaGeneracion;
        }

        /**
         * Obtiene la acción recomendada.
         * 
         * @return la acción específica a realizar
         */
        public String getAccionRecomendada() {
            return accionRecomendada;
        }

        /**
         * Establece la acción recomendada.
         * 
         * @param accionRecomendada la nueva acción recomendada
         */
        public void setAccionRecomendada(String accionRecomendada) {
            this.accionRecomendada = accionRecomendada;
        }

        /**
         * Obtiene la justificación de la recomendación.
         * 
         * @return la razón o fundamento de la recomendación
         */
        public String getJustificacion() {
            return justificacion;
        }

        /**
         * Establece la justificación de la recomendación.
         * 
         * @param justificacion la nueva justificación
         */
        public void setJustificacion(String justificacion) {
            this.justificacion = justificacion;
        }

        /**
         * Verifica si la recomendación ha sido aplicada.
         * 
         * @return {@code true} si fue aplicada, {@code false} en caso contrario
         */
        public boolean isAplicada() {
            return aplicada;
        }

        /**
         * Establece el estado de aplicación de la recomendación.
         * 
         * @param aplicada {@code true} para marcar como aplicada
         */
        public void setAplicada(boolean aplicada) {
            this.aplicada = aplicada;
        }

        /**
         * Formatea la fecha de generación.
         * 
         * @return la fecha formateada como "dd/MM/yyyy HH:mm"
         */
        public String getFechaFormateada() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return fechaGeneracion.format(formatter);
        }

        /**
         * Devuelve una representación en cadena de la recomendación.
         * 
         * @return cadena con el título y prioridad
         */
        @Override
        public String toString() {
            return "Recomendacion: " + titulo + " [" + prioridad + "]";
        }
    }