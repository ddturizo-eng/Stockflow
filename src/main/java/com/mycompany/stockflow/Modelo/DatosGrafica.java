    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    /**
     * Contenedor de datos estructurados para generación de gráficas.
     * 
     * <p>Esta clase organiza la información necesaria para renderizar
     * diferentes tipos de gráficas en la interfaz de usuario. Soporta
     * múltiples series de datos y metadatos adicionales para personalización
     * de la visualización.</p>
     * 
     * <p><strong>Componentes de la gráfica:</strong></p>
     * <ul>
     *   <li><strong>Tipo</strong>: Define el tipo de visualización (línea, barras, pastel, etc.)</li>
     *   <li><strong>Título</strong>: Título descriptivo de la gráfica</li>
     *   <li><strong>Series</strong>: Conjuntos de datos a visualizar</li>
     *   <li><strong>Metadata</strong>: Información adicional (ejes, colores, etiquetas, etc.)</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso para gráfica de líneas:</strong></p>
     * <pre>
     * DatosGrafica grafica = new DatosGrafica();
     * grafica.setTipo(TipoGrafica.LINEA);
     * grafica.setTitulo("Ventas Mensuales 2024");
     * 
     * // Crear serie de datos
     * SerieGrafica serie = new SerieGrafica("Ventas");
     * serie.agregarPunto(new PuntoGrafica("Enero", 15000000.0));
     * serie.agregarPunto(new PuntoGrafica("Febrero", 18000000.0));
     * serie.agregarPunto(new PuntoGrafica("Marzo", 22000000.0));
     * 
     * grafica.agregarSerie(serie);
     * 
     * // Metadata opcional
     * grafica.getMetadata().put("ejeY", "Ventas ($)");
     * grafica.getMetadata().put("ejeX", "Mes");
     * </pre>
     * 
     * <p><strong>Ejemplo para gráfica de pastel:</strong></p>
     * <pre>
     * DatosGrafica grafica = new DatosGrafica();
     * grafica.setTipo(TipoGrafica.PASTEL);
     * grafica.setTitulo("Distribución de Ventas por Categoría");
     * 
     * SerieGrafica serie = new SerieGrafica("Categorías");
     * serie.agregarPunto(new PuntoGrafica("Electrónica", 45.5));
     * serie.agregarPunto(new PuntoGrafica("Ropa", 30.2));
     * serie.agregarPunto(new PuntoGrafica("Alimentos", 24.3));
     * 
     * grafica.agregarSerie(serie);
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see TipoGrafica
     * @see SerieGrafica
     * @see PuntoGrafica
     */
    public class DatosGrafica {

        /** Tipo de gráfica a renderizar */
        private TipoGrafica tipo;

        /** Título descriptivo de la gráfica */
        private String titulo;

        /** Lista de series de datos a visualizar */
        private List<SerieGrafica> series;

        /** Mapa de metadatos adicionales para configuración de la gráfica */
        private Map<String, Object> metadata;

        /**
         * Constructor por defecto que inicializa las colecciones vacías.
         * La lista de series y el mapa de metadata se inicializan
         * para evitar problemas de NullPointerException.
         */
        public DatosGrafica() {
            this.series = new ArrayList<>();
            this.metadata = new HashMap<>();
        }

        /**
         * Agrega una serie de datos a la gráfica.
         * Permite visualizar múltiples conjuntos de datos en la misma gráfica.
         * 
         * @param serie la serie de datos a agregar
         */
        public void agregarSerie(SerieGrafica serie) {
            this.series.add(serie);
        }

        /**
         * Obtiene el tipo de gráfica.
         * 
         * @return el tipo de gráfica (LINEA, BARRAS, PASTEL, etc.)
         */
        public TipoGrafica getTipo() { 
            return tipo; 
        }

        /**
         * Establece el tipo de gráfica.
         * El tipo determina cómo se visualizarán los datos.
         * 
         * @param tipo el nuevo tipo de gráfica
         */
        public void setTipo(TipoGrafica tipo) { 
            this.tipo = tipo; 
        }

        /**
         * Obtiene el título de la gráfica.
         * 
         * @return el título descriptivo
         */
        public String getTitulo() { 
            return titulo; 
        }

        /**
         * Establece el título de la gráfica.
         * 
         * @param titulo el nuevo título
         */
        public void setTitulo(String titulo) { 
            this.titulo = titulo; 
        }

        /**
         * Obtiene la lista de series de datos.
         * 
         * @return la lista de series
         */
        public List<SerieGrafica> getSeries() { 
            return series; 
        }

        /**
         * Establece la lista completa de series.
         * 
         * @param series la nueva lista de series
         */
        public void setSeries(List<SerieGrafica> series) { 
            this.series = series; 
        }

        /**
         * Obtiene el mapa de metadata.
         * La metadata puede incluir información como:
         * <ul>
         *   <li>Etiquetas de ejes (ejeX, ejeY)</li>
         *   <li>Colores personalizados</li>
         *   <li>Configuración de leyenda</li>
         *   <li>Formato de tooltips</li>
         *   <li>Límites de ejes</li>
         * </ul>
         * 
         * @return el mapa de metadata
         */
        public Map<String, Object> getMetadata() { 
            return metadata; 
        }

        /**
         * Establece el mapa completo de metadata.
         * 
         * @param metadata el nuevo mapa de metadata
         */
        public void setMetadata(Map<String, Object> metadata) { 
            this.metadata = metadata; 
        }
    }