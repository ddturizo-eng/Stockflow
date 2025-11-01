    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;

    /**
     * Representa una factura generada a partir de una venta.
     * 
     * <p>La factura es el documento oficial que respalda una transacción
     * de venta en el sistema StockFlow, incluyendo información del cliente,
     * método de pago, montos detallados y estado de pago.</p>
     * 
     * <p><strong>Estados posibles de una factura:</strong></p>
     * <ul>
     *   <li><strong>PAGADA</strong>: La factura ha sido cancelada completamente</li>
     *   <li><strong>PENDIENTE</strong>: Existe saldo por pagar</li>
     *   <li><strong>ANULADA</strong>: La factura fue anulada y no es válida</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * Venta venta = new Venta("V001", cliente);
     * Factura factura = new Factura("F001", venta);
     * 
     * factura.setMetodoPago("Efectivo");
     * factura.setMontoRecibido(1500000);
     * factura.setCambio(factura.getMontoRecibido() - factura.getTotal());
     * 
     * boolean pagada = factura.isPagada(); // true
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see Venta
     * @see Cliente
     */
    public class Factura extends Entidad {

        /** Número de comprobante o folio de la factura */
        private String numeroComprobante;

        /** Venta asociada a esta factura */
        private Venta venta;

        /** Fecha y hora de emisión de la factura */
        private LocalDateTime fechaEmision;

        /** Estado actual de la factura (PAGADA, PENDIENTE, ANULADA) */
        private String estado;

        /** Subtotal de la factura antes de impuestos */
        private double subtotal;

        /** Monto de IVA aplicado */
        private double iva;

        /** Descuento aplicado a la factura */
        private double descuento;

        /** Total de la factura (subtotal + iva - descuento) */
        private double total;

        /** Método de pago utilizado (Efectivo, Tarjeta, Transferencia) */
        private String metodoPago;

        /** Monto recibido del cliente */
        private double montoRecibido;

        /** Cambio o vuelto entregado al cliente */
        private double cambio;

        /**
         * Constructor por defecto que inicializa una factura vacía.
         * El estado se establece como "PAGADA" por defecto.
         * La fecha de emisión se establece a la hora actual.
         */
        public Factura() {
            super();
            this.fechaEmision = LocalDateTime.now();
            this.estado = "PAGADA";
        }

        /**
         * Constructor que crea una factura asociada a una venta.
         * Copia automáticamente el total desde la venta.
         * 
         * @param numeroComprobante el número único de comprobante
         * @param venta la venta asociada a esta factura
         */
        public Factura(String numeroComprobante, Venta venta) {
            super(numeroComprobante);
            this.numeroComprobante = numeroComprobante;
            this.venta = venta;
            this.fechaEmision = LocalDateTime.now();
            this.estado = "PAGADA";

            // Copiar datos de la venta
            if (venta != null) {
                this.total = venta.getTotal();
            }
        }

        /**
         * Obtiene el número de comprobante de la factura.
         * 
         * @return el número de comprobante
         */
        public String getNumeroComprobante() {
            return numeroComprobante;
        }

        /**
         * Establece el número de comprobante y actualiza el ID de la entidad.
         * 
         * @param numeroComprobante el nuevo número de comprobante
         */
        public void setNumeroComprobante(String numeroComprobante) {
            this.numeroComprobante = numeroComprobante;
            setId(numeroComprobante);
        }

        /**
         * Obtiene la venta asociada a esta factura.
         * 
         * @return la venta
         */
        public Venta getVenta() {
            return venta;
        }

        /**
         * Establece la venta asociada a esta factura.
         * 
         * @param venta la nueva venta
         */
        public void setVenta(Venta venta) {
            this.venta = venta;
        }

        /**
         * Obtiene la fecha y hora de emisión de la factura.
         * 
         * @return la fecha de emisión
         */
        public LocalDateTime getFechaEmision() {
            return fechaEmision;
        }

        /**
         * Establece la fecha y hora de emisión de la factura.
         * 
         * @param fechaEmision la nueva fecha de emisión
         */
        public void setFechaEmision(LocalDateTime fechaEmision) {
            this.fechaEmision = fechaEmision;
        }

        /**
         * Obtiene el estado actual de la factura.
         * 
         * @return el estado (PAGADA, PENDIENTE, ANULADA)
         */
        public String getEstado() {
            return estado;
        }

        /**
         * Establece el estado de la factura.
         * 
         * @param estado el nuevo estado
         */
        public void setEstado(String estado) {
            this.estado = estado;
        }

        /**
         * Obtiene el subtotal de la factura.
         * 
         * @return el subtotal antes de impuestos
         */
        public double getSubtotal() {
            return subtotal;
        }

        /**
         * Establece el subtotal de la factura.
         * 
         * @param subtotal el nuevo subtotal
         */
        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }

        /**
         * Obtiene el monto de IVA de la factura.
         * 
         * @return el valor del IVA
         */
        public double getIva() {
            return iva;
        }

        /**
         * Establece el monto de IVA.
         * 
         * @param iva el nuevo valor de IVA
         */
        public void setIva(double iva) {
            this.iva = iva;
        }

        /**
         * Obtiene el descuento aplicado a la factura.
         * 
         * @return el monto del descuento
         */
        public double getDescuento() {
            return descuento;
        }

        /**
         * Establece el descuento aplicado.
         * 
         * @param descuento el nuevo descuento
         */
        public void setDescuento(double descuento) {
            this.descuento = descuento;
        }

        /**
         * Obtiene el total de la factura.
         * 
         * @return el total (subtotal + iva - descuento)
         */
        public double getTotal() {
            return total;
        }

        /**
         * Establece el total de la factura.
         * 
         * @param total el nuevo total
         */
        public void setTotal(double total) {
            this.total = total;
        }

        /**
         * Obtiene el método de pago utilizado.
         * 
         * @return el método de pago
         */
        public String getMetodoPago() {
            return metodoPago;
        }

        /**
         * Establece el método de pago.
         * 
         * @param metodoPago el nuevo método de pago
         */
        public void setMetodoPago(String metodoPago) {
            this.metodoPago = metodoPago;
        }

        /**
         * Obtiene el monto recibido del cliente.
         * 
         * @return el monto recibido
         */
        public double getMontoRecibido() {
            return montoRecibido;
        }

        /**
         * Establece el monto recibido del cliente.
         * 
         * @param montoRecibido el nuevo monto recibido
         */
        public void setMontoRecibido(double montoRecibido) {
            this.montoRecibido = montoRecibido;
        }

        /**
         * Obtiene el cambio o vuelto entregado al cliente.
         * 
         * @return el cambio
         */
        public double getCambio() {
            return cambio;
        }

        /**
         * Establece el cambio entregado al cliente.
         * 
         * @param cambio el nuevo cambio
         */
        public void setCambio(double cambio) {
            this.cambio = cambio;
        }

        /**
         * Formatea la fecha de emisión en formato legible.
         * 
         * @return la fecha formateada como "dd/MM/yyyy HH:mm:ss" o "N/A" si es null
         */
        public String getFechaFormateada() {
            if (fechaEmision == null) {
                return "N/A";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return fechaEmision.format(formatter);
        }

        /**
         * Obtiene el nombre del cliente asociado a la venta.
         * 
         * @return el nombre del cliente o "Sin cliente" si no existe
         */
        public String getNombreCliente() {
            if (venta != null && venta.getCliente() != null) {
                return venta.getCliente().getNombre();
            }
            return "Sin cliente";
        }

        /**
         * Obtiene la cédula del cliente asociado a la venta.
         * 
         * @return la cédula del cliente o "N/A" si no existe
         */
        public String getCedulaCliente() {
            if (venta != null && venta.getCliente() != null) {
                return venta.getCliente().getCedula();
            }
            return "N/A";
        }

        /**
         * Verifica si la factura está pagada.
         * 
         * @return {@code true} si el estado es "PAGADA"
         */
        public boolean isPagada() {
            return "PAGADA".equals(estado);
        }

        /**
         * Verifica si la factura está pendiente de pago.
         * 
         * @return {@code true} si el estado es "PENDIENTE"
         */
        public boolean isPendiente() {
            return "PENDIENTE".equals(estado);
        }

        /**
         * Verifica si la factura ha sido anulada.
         * 
         * @return {@code true} si el estado es "ANULADA"
         */
        public boolean isAnulada() {
            return "ANULADA".equals(estado);
        }

        /**
         * Devuelve una representación en cadena de la factura.
         * 
         * @return cadena con información de la factura
         */
        @Override
        public String toString() {
            return "Factura{" +
                    "numeroComprobante='" + numeroComprobante + '\'' +
                    ", fechaEmision=" + getFechaFormateada() +
                    ", cliente=" + getNombreCliente() +
                    ", total=" + total +
                    ", estado='" + estado + '\'' +
                    '}';
        }
    }