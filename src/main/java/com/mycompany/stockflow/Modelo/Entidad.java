/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
    package com.mycompany.stockflow.Modelo;

    import java.io.Serializable;

    /**
     * Clase abstracta base que representa una entidad genérica del sistema.
     * Proporciona funcionalidad común para todas las entidades del dominio,
     * principalmente la gestión de identificadores únicos.
     * 
     * <p>Esta clase implementa {@link Serializable} para permitir la persistencia
     * y transferencia de objetos entre diferentes capas de la aplicación.</p>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     */
    public abstract class Entidad implements Serializable {

        /** Identificador único de serialización para control de versiones */
        private static final long serialVersionUID = 1L;

        /** Identificador único de la entidad */
        private String id;

        /**
         * Constructor por defecto que inicializa la entidad sin identificador.
         * El identificador debe ser asignado posteriormente mediante {@link #setId(String)}.
         */
        public Entidad() {
            this.id = null;
        }

        /**
         * Constructor que inicializa la entidad con un identificador específico.
         * 
         * @param id el identificador único de la entidad
         */
        public Entidad(String id) {
            this.id = id;
        }

        /**
         * Obtiene el identificador único de la entidad.
         * 
         * @return el identificador de la entidad, puede ser {@code null} si no ha sido asignado
         */
        public String getId() {
            return id;
        }

        /**
         * Establece el identificador único de la entidad.
         * 
         * @param id el nuevo identificador de la entidad
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Devuelve una representación en cadena de la entidad.
         * 
         * @return una cadena que contiene el identificador de la entidad
         */
        @Override
        public String toString() {
            return "Entidad{id='" + id + "'}";
        }
    }