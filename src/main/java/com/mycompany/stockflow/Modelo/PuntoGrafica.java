    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.time.LocalDateTime;

    /**
     * Representa un punto de datos individual en una gráfica.
     * 
     * <p>Cada punto contiene una etiqueta identificadora (típicamente para el eje X)
     * y un valor numérico (típicamente para el eje Y). Opcionalmente puede incluir
     * metadatos adicionales como timestamp y color para personalización.</p>
     * 
     * <p><strong>Componentes de un punto:</strong></p>
     * <ul>
     *   <li><strong>Etiqueta</strong>: Identificador del punto (ej: "Enero", "Producto A", "2024")</li>
     *   <li><strong>Valor</strong>: Dato numérico a visualizar</li>
     *   <li><strong>Timestamp</strong>: Fecha y hora asociada (opcional)</li>
     *   <li><strong>Color</strong>: Color específico del punto (opcional)</li>
     * </ul>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Representar ventas mensuales (etiqueta: mes, valor: monto)</li>
     *   <li>Mostrar distribución de categorías (etiqueta: categoría, valor: porcentaje)</li>
     *   <li>Visualizar evolución temporal (etiqueta: fecha, valor: métrica)</li>
     *   <li>Comparar productos (etiqueta: producto, valor: unidades)</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso - Serie temporal:</strong></p>
     * <pre>
     * // Punto simple
     * PuntoGrafica punto1 = new PuntoGrafica("Enero", 15000000.0);
     * 
     * // Punto con timestamp
     * PuntoGrafica punto2 = new PuntoGrafica("Febrero", 18000000.0);
     * punto2.setTimestamp(LocalDateTime.of(2024, 2, 1, 0, 0));
     * </pre>
     * 
     * <p><strong>Ejemplo de uso - Gráfica de pastel:</strong></p>
     * <pre>
     * PuntoGrafica electronica = new PuntoGrafica("Electrónica", 45.5);
     * electronica.setColor("#FF6384");
     * 
     * PuntoGrafica ropa = new PuntoGrafica("Ropa", 30.2);
     * ropa.setColor("#36A2EB");
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see SerieGrafica
     * @see DatosGrafica
     */
    public class PuntoGrafica {

        /** Etiqueta identificadora del punto (eje X o nombre de categoría) */
        private String etiqueta;

        /** Valor numérico del punto (eje Y o magnitud) */
        private Double valor;

        /** Timestamp asociado al punto (opcional, útil para series temporales) */
        private LocalDateTime timestamp;

        /** Color específico del punto (opcional, sobrescribe el color de la serie) */
        private String color;

        /**
         * Constructor por defecto que inicializa un punto vacío.
         */
        public PuntoGrafica() {}

        /**
         * Constructor que crea un punto con etiqueta y valor.
         * Este es el constructor más utilizado para crear puntos de datos.
         * 
         * @param etiqueta la etiqueta identificadora del punto
         * @param valor el valor numérico del punto
         */
        public PuntoGrafica(String etiqueta, Double valor) {
            this.etiqueta = etiqueta;
            this.valor = valor;
        }

        /**
         * Obtiene la etiqueta del punto.
         * 
         * @return la etiqueta identificadora
         */
        public String getEtiqueta() { 
            return etiqueta; 
        }

        /**
         * Establece la etiqueta del punto.
         * La etiqueta se utiliza para identificar el punto en el eje X
         * o como nombre en gráficas de categorías (pastel, barras).
         * 
         * @param etiqueta la nueva etiqueta
         */
        public void setEtiqueta(String etiqueta) { 
            this.etiqueta = etiqueta; 
        }

        /**
         * Obtiene el valor numérico del punto.
         * 
         * @return el valor del punto
         */
        public Double getValor() { 
            return valor; 
        }

        /**
         * Establece el valor numérico del punto.
         * El valor representa la magnitud que se visualizará en el eje Y
         * o el tamaño en gráficas de proporciones.
         * 
         * @param valor el nuevo valor
         */
        public void setValor(Double valor) { 
            this.valor = valor; 
        }

        /**
         * Obtiene el timestamp asociado al punto.
         * 
         * @return la fecha y hora del punto, puede ser null
         */
        public LocalDateTime getTimestamp() { 
            return timestamp; 
        }

        /**
         * Establece el timestamp del punto.
         * Útil para series temporales donde se necesita la fecha exacta
         * además de la etiqueta descriptiva.
         * 
         * @param timestamp la nueva fecha y hora
         */
        public void setTimestamp(LocalDateTime timestamp) { 
            this.timestamp = timestamp; 
        }

        /**
         * Obtiene el color específico del punto.
         * 
         * @return el color del punto, puede ser null para usar el color de la serie
         */
        public String getColor() { 
            return color; 
        }

        /**
         * Establece el color específico del punto.
         * Si se establece, este color sobrescribe el color de la serie
         * para este punto en particular. Útil para destacar valores
         * específicos o crear gráficas de pastel multicolores.
         * 
         * <p>Formatos de color soportados:</p>
         * <ul>
         *   <li>Hexadecimal: "#FF6384"</li>
         *   <li>RGB: "rgb(255, 99, 132)"</li>
         *   <li>RGBA: "rgba(255, 99, 132, 0.8)"</li>
         *   <li>Nombre: "red", "blue"</li>
         * </ul>
         * 
         * @param color el nuevo color del punto
         */
        public void setColor(String color) { 
            this.color = color; 
        }
    }