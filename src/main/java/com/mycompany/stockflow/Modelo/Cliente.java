    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    /**
     * Representa un cliente del sistema StockFlow.
     * 
     * <p>Esta clase modela la información básica de un cliente, incluyendo
     * sus datos de contacto y documentación. Los clientes son entidades
     * fundamentales en el proceso de ventas y facturación.</p>
     * 
     * <p>La cédula del cliente se utiliza como identificador único en el sistema.</p>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * Cliente cliente = new Cliente(
     *     "1234567890",
     *     "Juan Pérez",
     *     "3001234567",
     *     "Calle 123 #45-67",
     *     "juan@email.com"
     * );
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see Venta
     * @see Factura
     */
    public class Cliente extends Entidad {

        /** Número de cédula o documento de identidad del cliente */
        private String cedula;

        /** Nombre completo del cliente */
        private String nombre;

        /** Número de teléfono de contacto del cliente */
        private String telefono;

        /** Dirección física del cliente */
        private String direccion;

        /** Correo electrónico del cliente */
        private String email;

        /**
         * Constructor completo para crear un cliente con todos sus datos.
         * 
         * @param cedula el número de cédula o documento de identidad
         * @param nombre el nombre completo del cliente
         * @param telefono el número de teléfono de contacto
         * @param direccion la dirección física del cliente
         * @param email el correo electrónico del cliente
         */
        public Cliente(String cedula, String nombre, String telefono, String direccion, String email) {
            super(cedula);
            this.cedula = cedula;
            this.nombre = nombre;
            this.telefono = telefono;
            this.direccion = direccion;
            this.email = email;
        }

        /**
         * Obtiene el número de cédula del cliente.
         * 
         * @return la cédula del cliente
         */
        public String getCedula() { 
            return cedula; 
        }

        /**
         * Establece el número de cédula del cliente.
         * 
         * @param cedula el nuevo número de cédula
         */
        public void setCedula(String cedula) { 
            this.cedula = cedula; 
        }

        /**
         * Obtiene el nombre completo del cliente.
         * 
         * @return el nombre del cliente
         */
        public String getNombre() { 
            return nombre; 
        }

        /**
         * Establece el nombre completo del cliente.
         * 
         * @param nombre el nuevo nombre del cliente
         */
        public void setNombre(String nombre) { 
            this.nombre = nombre;
        }

        /**
         * Obtiene el número de teléfono del cliente.
         * 
         * @return el teléfono del cliente
         */
        public String getTelefono() { 
            return telefono;
        }

        /**
         * Establece el número de teléfono del cliente.
         * 
         * @param telefono el nuevo número de teléfono
         */
        public void setTelefono(String telefono) { 
            this.telefono = telefono; 
        }

        /**
         * Obtiene la dirección física del cliente.
         * 
         * @return la dirección del cliente
         */
        public String getDireccion() {
            return direccion;
        }

        /**
         * Establece la dirección física del cliente.
         * 
         * @param direccion la nueva dirección
         */
        public void setDireccion(String direccion) {
            this.direccion = direccion; 
        }

        /**
         * Obtiene el correo electrónico del cliente.
         * 
         * @return el email del cliente
         */
        public String getEmail() { 
            return email; 
        }

        /**
         * Establece el correo electrónico del cliente.
         * 
         * @param email el nuevo correo electrónico
         */
        public void setEmail(String email) { 
            this.email = email; 
        }

        /**
         * Devuelve una representación en cadena del cliente.
         * 
         * @return una cadena con el formato "nombre (cédula)"
         */
        @Override
        public String toString() {
            return nombre + " (" + cedula + ")";
        }
    }