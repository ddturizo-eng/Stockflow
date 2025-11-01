/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
    package com.mycompany.stockflow.Modelo;

    /**
     * Enumeración que define los diferentes tipos de gráficas disponibles
     * en el sistema de visualización de datos de StockFlow.
     * <p>
     * Esta clase se utiliza para especificar el formato de representación
     * visual de datos estadísticos, permitiendo diferentes tipos de visualizaciones
     * según las necesidades del análisis de información del negocio.
     * </p>
     * 
     * @author user
     * @version 1.0
     * @since 1.0
     * @see com.mycompany.stockflow.Modelo.DatosGrafica
     * @see com.mycompany.stockflow.Logica.EstadisticasServicio
     */
    public enum TipoGrafica {

        /**
         * Gráfica de líneas.
         * <p>
         * Representa datos mediante líneas conectadas, ideal para mostrar
         * tendencias y evolución de valores a lo largo del tiempo.
         * Útil para visualizar ventas por período, cambios en inventario
         * o cualquier métrica temporal continua.
         * </p>
         */
        LINEA,

        /**
         * Gráfica de barras verticales.
         * <p>
         * Representa datos mediante barras verticales, ideal para comparar
         * valores entre diferentes categorías. Útil para mostrar ventas por
         * producto, comparaciones de períodos, o ranking de items.
         * </p>
         */
        BARRAS,

        /**
         * Gráfica de barras horizontales.
         * <p>
         * Representa datos mediante barras horizontales, especialmente útil
         * cuando las etiquetas de categorías son largas o cuando se quiere
         * facilitar la lectura de múltiples categorías. Ideal para rankings
         * de productos o comparaciones extensas.
         * </p>
         */
        BARRAS_HORIZONTAL,

        /**
         * Gráfica de pastel (circular).
         * <p>
         * Representa datos como porciones de un círculo, donde cada porción
         * muestra la proporción relativa de cada categoría respecto al total.
         * Ideal para mostrar distribución porcentual de ventas por categoría,
         * participación de productos en el total, o composición de cualquier conjunto.
         * </p>
         */
        PASTEL,

        /**
         * Gráfica de área.
         * <p>
         * Similar a la gráfica de líneas pero con el área bajo la curva rellena,
         * enfatizando la magnitud de los cambios a lo largo del tiempo.
         * Útil para mostrar volumen acumulado de ventas, tendencias de inventario
         * con énfasis en cantidades totales, o flujos de datos temporales.
         * </p>
         */
        AREA,

        /**
         * Gráfica de barras apiladas.
         * <p>
         * Representa múltiples series de datos apiladas en barras verticales,
         * donde cada barra muestra la suma total y la composición de diferentes
         * categorías. Ideal para mostrar ventas totales desglosadas por tipo de
         * producto, ingresos segmentados por categoría en diferentes períodos,
         * o cualquier comparación multi-dimensional.
         * </p>
         */
        BARRAS_APILADAS
    }
//probando commit funcional