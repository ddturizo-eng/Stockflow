    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.util.ArrayList;
    import java.util.List;

    /**
     * Proporciona contexto empresarial para análisis de inteligencia artificial.
     * 
     * <p>Esta clase agrupa información relevante del negocio en un período específico
     * que será procesada por los módulos de análisis estadístico e inteligencia artificial.
     * Actúa como contenedor de datos estructurados que facilita el análisis integral
     * del desempeño del negocio.</p>
     * 
     * <p><strong>Información contenida:</strong></p>
     * <ul>
     *   <li>Lista de productos en el inventario</li>
     *   <li>Ventas realizadas en el período</li>
     *   <li>Clientes activos</li>
     *   <li>Movimientos de inventario</li>
     *   <li>Período de análisis (descripción temporal)</li>
     * </ul>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Entrada para análisis de tendencias de ventas</li>
     *   <li>Generación de recomendaciones de inventario</li>
     *   <li>Análisis de comportamiento de clientes</li>
     *   <li>Predicciones de demanda con IA</li>
     *   <li>Optimización de precios y stock</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * ContextoNegocio contexto = new ContextoNegocio();
     * contexto.setProductos(productoServicio.obtenerTodos());
     * contexto.setVentas(ventaServicio.obtenerPorPeriodo(inicio, fin));
     * contexto.setClientes(clienteServicio.obtenerTodos());
     * contexto.setPeriodoAnalisis("Enero - Marzo 2024");
     * 
     * // Usar en análisis
     * ResultadoAnalisisIA resultado = analisisIAServicio.analizar(contexto);
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see AnalisisEstadistico
     * @see ResultadoAnalisisIA
     * @see Recomendacion
     */
    public class ContextoNegocio {

        /** Lista de productos en el inventario */
        private List<Producto> productos;

        /** Lista de ventas realizadas en el período de análisis */
        private List<Venta> ventas;

        /** Lista de clientes activos */
        private List<Cliente> clientes;

        /** Lista de movimientos de inventario en el período */
        private List<MovimientoInventario> movimientos;

        /** Descripción del período de análisis (ej: "Último mes", "Q1 2024") */
        private String periodoAnalisis;

        /**
         * Constructor por defecto que inicializa todas las listas vacías.
         * Las listas se inicializan para evitar problemas de NullPointerException.
         */
        public ContextoNegocio() {
            this.productos = new ArrayList<>();
            this.ventas = new ArrayList<>();
            this.clientes = new ArrayList<>();
            this.movimientos = new ArrayList<>();
        }

        /**
         * Obtiene la lista de productos.
         * 
         * @return la lista de productos en el contexto
         */
        public List<Producto> getProductos() {
            return productos;
        }

        /**
         * Establece la lista de productos.
         * 
         * @param productos la nueva lista de productos
         */
        public void setProductos(List<Producto> productos) {
            this.productos = productos;
        }

        /**
         * Obtiene la lista de ventas.
         * 
         * @return la lista de ventas en el contexto
         */
        public List<Venta> getVentas() {
            return ventas;
        }

        /**
         * Establece la lista de ventas.
         * 
         * @param ventas la nueva lista de ventas
         */
        public void setVentas(List<Venta> ventas) {
            this.ventas = ventas;
        }

        /**
         * Obtiene la lista de clientes.
         * 
         * @return la lista de clientes en el contexto
         */
        public List<Cliente> getClientes() {
            return clientes;
        }

        /**
         * Establece la lista de clientes.
         * 
         * @param clientes la nueva lista de clientes
         */
        public void setClientes(List<Cliente> clientes) {
            this.clientes = clientes;
        }

        /**
         * Obtiene la lista de movimientos de inventario.
         * 
         * @return la lista de movimientos en el contexto
         */
        public List<MovimientoInventario> getMovimientos() {
            return movimientos;
        }

        /**
         * Establece la lista de movimientos de inventario.
         * 
         * @param movimientos la nueva lista de movimientos
         */
        public void setMovimientos(List<MovimientoInventario> movimientos) {
            this.movimientos = movimientos;
        }

        /**
         * Obtiene la descripción del período de análisis.
         * 
         * @return el período de análisis como cadena descriptiva
         */
        public String getPeriodoAnalisis() {
            return periodoAnalisis;
        }

        /**
         * Establece la descripción del período de análisis.
         * 
         * @param periodoAnalisis la nueva descripción del período
         */
        public void setPeriodoAnalisis(String periodoAnalisis) {
            this.periodoAnalisis = periodoAnalisis;
        }

        /**
         * Obtiene el número total de productos en el contexto.
         * 
         * @return cantidad de productos, 0 si la lista es null
         */
        public int getTotalProductos() {
            return productos != null ? productos.size() : 0;
        }

        /**
         * Obtiene el número total de ventas en el contexto.
         * 
         * @return cantidad de ventas, 0 si la lista es null
         */
        public int getTotalVentas() {
            return ventas != null ? ventas.size() : 0;
        }

        /**
         * Obtiene el número total de clientes en el contexto.
         * 
         * @return cantidad de clientes, 0 si la lista es null
         */
        public int getTotalClientes() {
            return clientes != null ? clientes.size() : 0;
        }

        /**
         * Devuelve una representación en cadena del contexto de negocio.
         * Incluye contadores de cada tipo de entidad.
         * 
         * @return cadena con resumen del contexto
         */
        @Override
        public String toString() {
            return String.format("Contexto: %d productos, %d ventas, %d clientes",
                    getTotalProductos(), getTotalVentas(), getTotalClientes());
        }
    }