    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.util.ArrayList;
    import java.util.List;

    /**
     * Representa una serie de datos dentro de una gráfica.
     * 
     * <p>Una serie es un conjunto de puntos relacionados que se visualizan
     * juntos en una gráfica. Por ejemplo, en una gráfica de ventas mensuales,
     * cada producto podría representarse como una serie diferente, o en una
     * comparación anual, cada año sería una serie.</p>
     * 
     * <p><strong>Características de una serie:</strong></p>
     * <ul>
     *   <li><strong>Nombre</strong>: Identificador descriptivo de la serie</li>
     *   <li><strong>Valores</strong>: Lista de puntos de datos</li>
     *   <li><strong>Color</strong>: Color de visualización (opcional)</li>
     * </ul>
     * 
     * <p><strong>Casos de uso comunes:</strong></p>
     * <ul>
     *   <li>Comparar ventas de diferentes productos en el tiempo</li>
     *   <li>Mostrar múltiples categorías en una gráfica de barras</li>
     *   <li>Visualizar tendencias de diferentes métricas</li>
     *   <li>Representar datos de diferentes períodos</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso - Serie temporal:</strong></p>
     * <pre>
     * SerieGrafica ventasLaptops = new SerieGrafica("Laptops");
     * ventasLaptops.setColor("#FF6384");
     * ventasLaptops.agregarPunto(new PuntoGrafica("Enero", 50.0));
     * ventasLaptops.agregarPunto(new PuntoGrafica("Febrero", 65.0));
     * ventasLaptops.agregarPunto(new PuntoGrafica("Marzo", 72.0));
     * </pre>
     * 
     * <p><strong>Ejemplo de uso - Comparación múltiple:</strong></p>
     * <pre>
     * // Serie 2023
     * SerieGrafica ventas2023 = new SerieGrafica("2023");
     * ventas2023.agregarPunto(new PuntoGrafica("Q1", 100000.0));
     * ventas2023.agregarPunto(new PuntoGrafica("Q2", 120000.0));
     * 
     * // Serie 2024
     * SerieGrafica ventas2024 = new SerieGrafica("2024");
     * ventas2024.agregarPunto(new PuntoGrafica("Q1", 135000.0));
     * ventas2024.agregarPunto(new PuntoGrafica("Q2", 150000.0));
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see PuntoGrafica
     * @see DatosGrafica
     */
    public class SerieGrafica {

        /** Nombre descriptivo de la serie */
        private String nombre;

        /** Lista de puntos de datos que componen la serie */
        private List<PuntoGrafica> valores;

        /** Color de la serie en formato hexadecimal (ej: "#FF6384") o nombre (ej: "red") */
        private String color;

        /**
         * Constructor que crea una serie con nombre.
         * La lista de valores se inicializa vacía.
         * 
         * @param nombre el nombre descriptivo de la serie
         */
        public SerieGrafica(String nombre) {
            this.nombre = nombre;
            this.valores = new ArrayList<>();
        }

        /**
         * Agrega un punto de datos a la serie.
         * Los puntos se agregan en el orden en que se invocan.
         * 
         * @param punto el punto de datos a agregar
         */
        public void agregarPunto(PuntoGrafica punto) {
            this.valores.add(punto);
        }

        /**
         * Obtiene el nombre de la serie.
         * 
         * @return el nombre descriptivo de la serie
         */
        public String getNombre() { 
            return nombre; 
        }

        /**
         * Establece el nombre de la serie.
         * El nombre se utiliza típicamente en la leyenda de la gráfica.
         * 
         * @param nombre el nuevo nombre de la serie
         */
        public void setNombre(String nombre) { 
            this.nombre = nombre; 
        }

        /**
         * Obtiene la lista de puntos de datos.
         * 
         * @return la lista de valores de la serie
         */
        public List<PuntoGrafica> getValores() { 
            return valores; 
        }

        /**
         * Establece la lista completa de puntos de datos.
         * 
         * @param valores la nueva lista de valores
         */
        public void setValores(List<PuntoGrafica> valores) { 
            this.valores = valores; 
        }

        /**
         * Obtiene el color de la serie.
         * 
         * @return el color en formato hexadecimal o nombre
         */
        public String getColor() { 
            return color; 
        }

        /**
         * Establece el color de la serie.
         * El color puede especificarse en varios formatos:
         * <ul>
         *   <li>Hexadecimal: "#FF6384", "#36A2EB"</li>
         *   <li>RGB: "rgb(255, 99, 132)"</li>
         *   <li>RGBA: "rgba(255, 99, 132, 0.8)"</li>
         *   <li>Nombre: "red", "blue", "green"</li>
         * </ul>
         * 
         * @param color el nuevo color de la serie
         */
        public void setColor(String color) { 
            this.color = color; 
        }
    }