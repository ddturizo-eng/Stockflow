/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 package com.mycompany.stockflow.Modelo;

    /**
     * Enumeración que define los diferentes roles de usuario en el sistema StockFlow.
     * <p>
     * Esta clase gestiona los permisos y privilegios de acceso según el tipo de usuario,
     * diferenciando entre usuarios con acceso administrativo completo y clientes con
     * acceso limitado a funcionalidades específicas.
     * </p>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     */
    public enum Rol {

        /**
         * Rol de usuario con privilegios administrativos completos.
         * Tiene acceso total al sistema de gestión incluyendo inventario,
         * estadísticas, gestión de clientes y ventas.
         */
        USUARIO("Usuario", "Acceso completo al sistema de gestión"),

        /**
         * Rol de cliente con acceso limitado.
         * Solo puede visualizar sus propias compras y el catálogo de productos disponibles.
         */
        CLIENTE("Cliente", "Solo puede ver sus compras y productos");

        /**
         * Nombre descriptivo del rol.
         */
        private final String nombre;

        /**
         * Descripción detallada de los permisos y alcance del rol.
         */
        private final String descripcion;

        /**
         * Constructor privado para inicializar los valores del enum.
         * 
         * @param nombre Nombre descriptivo del rol
         * @param descripcion Descripción de los permisos del rol
         */
        Rol(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        /**
         * Obtiene el nombre descriptivo del rol.
         * 
         * @return El nombre del rol
         */
        public String getNombre() {
            return nombre;
        }

        /**
         * Obtiene la descripción detallada del rol y sus permisos.
         * 
         * @return La descripción del rol
         */
        public String getDescripcion() {
            return descripcion;
        }

        /**
         * Verifica si el rol corresponde a un usuario administrativo.
         * 
         * @return {@code true} si es rol USUARIO, {@code false} en caso contrario
         */
        public boolean esUsuario() {
            return this == USUARIO;
        }

        /**
         * Verifica si el rol corresponde a un cliente.
         * 
         * @return {@code true} si es rol CLIENTE, {@code false} en caso contrario
         */
        public boolean esCliente() {
            return this == CLIENTE;
        }

        /**
         * Verifica si el rol tiene permisos para gestionar el inventario.
         * <p>
         * Solo los usuarios con rol USUARIO pueden realizar operaciones de
         * gestión de inventario como agregar, modificar o eliminar productos.
         * </p>
         * 
         * @return {@code true} si tiene permisos de gestión de inventario, {@code false} en caso contrario
         */
        public boolean puedeGestionarInventario() {
            return this == USUARIO;
        }

        /**
         * Verifica si el rol tiene permisos para visualizar estadísticas del sistema.
         * <p>
         * Solo los usuarios con rol USUARIO pueden acceder a reportes estadísticos,
         * métricas de ventas y análisis de datos del negocio.
         * </p>
         * 
         * @return {@code true} si tiene permisos para ver estadísticas, {@code false} en caso contrario
         */
        public boolean puedeVerEstadisticas() {
            return this == USUARIO;
        }

        /**
         * Verifica si el rol tiene permisos para gestionar la información de clientes.
         * <p>
         * Solo los usuarios con rol USUARIO pueden crear, modificar, eliminar
         * o consultar información detallada de clientes en el sistema.
         * </p>
         * 
         * @return {@code true} si tiene permisos de gestión de clientes, {@code false} en caso contrario
         */
        public boolean puedeGestionarClientes() {
            return this == USUARIO;
        }

        /**
         * Verifica si el rol tiene permisos para realizar operaciones de venta.
         * <p>
         * Solo los usuarios con rol USUARIO pueden procesar ventas,
         * generar facturas y registrar transacciones comerciales.
         * </p>
         * 
         * @return {@code true} si tiene permisos para realizar ventas, {@code false} en caso contrario
         */
        public boolean puedeRealizarVentas() {
            return this == USUARIO;
        }

        /**
         * Verifica si el rol tiene permisos para visualizar el catálogo de productos.
         * <p>
         * Este permiso está disponible para todos los roles, tanto USUARIO como CLIENTE
         * pueden consultar la lista de productos disponibles en el sistema.
         * </p>
         * 
         * @return {@code true} siempre, ya que ambos roles pueden ver productos
         */
        public boolean puedeVerProductos() {
            return true; // Ambos pueden ver productos
        }

        /**
         * Devuelve una representación en texto del rol.
         * 
         * @return El nombre del rol
         */
        @Override
        public String toString() {
            return nombre;
        }
    }