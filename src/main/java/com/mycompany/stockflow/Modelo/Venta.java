    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.io.Serializable;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    /**
     * Representa una transacción de venta en el sistema StockFlow.
     * 
     * <p>Una venta agrupa múltiples detalles de productos vendidos a un cliente,
     * calculando automáticamente totales, costos y márgenes de ganancia.
     * Esta clase es fundamental para el módulo de facturación y análisis
     * de rentabilidad del negocio.</p>
     * 
     * <p><strong>Cálculos automáticos:</strong></p>
     * <ul>
     *   <li><strong>Subtotal</strong>: Suma de todos los subtotales de detalles</li>
     *   <li><strong>Costo total</strong>: Suma de todos los costos de detalles</li>
     *   <li><strong>Ganancia bruta</strong>: Subtotal - Costo total</li>
     *   <li><strong>Ganancia neta</strong>: Ganancia bruta (sin deducciones)</li>
     *   <li><strong>Margen de ganancia (ROI)</strong>: (Ganancia / Costo) × 100</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * Cliente cliente = new Cliente("123", "Juan Pérez", "300123", "Calle 1", "juan@mail.com");
     * Venta venta = new Venta("V001", cliente);
     * 
     * Producto laptop = new Producto("P001", "Laptop", 1200000, 1500000, 10, "");
     * DetalleVenta detalle = new DetalleVenta(laptop, 2);
     * venta.agregarDetalle(detalle);
     * 
     * double total = venta.getTotal();           // Total de la venta
     * double ganancia = venta.getGananciaNeta(); // Ganancia neta
     * double roi = venta.getROI();               // Retorno de inversión
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see DetalleVenta
     * @see Cliente
     * @see Factura
     */
    public class Venta extends Entidad implements Serializable {

        /** Identificador único de serialización para control de versiones */
        private static final long serialVersionUID = 1L;

        /** Código único de la venta en el sistema */
        private String codigo;

        /** Cliente que realiza la compra */
        private Cliente cliente;

        /** Fecha y hora en que se realizó la venta */
        private LocalDateTime fecha;

        /** Lista de detalles (líneas) de la venta */
        private List<DetalleVenta> detalles;

        /** Subtotal de la venta (suma de subtotales de detalles) */
        private double subtotal;

        /** IVA aplicado a la venta */
        private double iva;

        /** Total de la venta (subtotal + iva) */
        private double total;

        /** Costo total de los productos vendidos */
        private double costoTotal;

        /** Ganancia bruta (subtotal - costo total) */
        private double gananciaBruta;

        /** Ganancia neta después de deducciones */
        private double gananciaNeta;

        /** Método de pago utilizado (Efectivo, Tarjeta, etc.) */
        private String metodoPago;

        /**
         * Constructor por defecto que inicializa una venta vacía.
         * La fecha se establece automáticamente a la hora actual.
         */
        public Venta() {
            super();
            this.detalles = new ArrayList<>();
            this.fecha = LocalDateTime.now();
            this.subtotal = 0.0;
            this.iva = 0.0;
            this.total = 0.0;
            this.costoTotal = 0.0;
            this.gananciaBruta = 0.0;
            this.gananciaNeta = 0.0;
        }

        /**
         * Constructor que crea una venta con código y cliente.
         * 
         * @param codigo el código único de la venta
         * @param cliente el cliente que realiza la compra
         */
        public Venta(String codigo, Cliente cliente) {
            super(codigo);
            this.codigo = codigo;
            this.cliente = cliente;
            this.fecha = LocalDateTime.now();
            this.detalles = new ArrayList<>();
            this.subtotal = 0.0;
            this.iva = 0.0;
            this.total = 0.0;
            this.costoTotal = 0.0;
            this.gananciaBruta = 0.0;
            this.gananciaNeta = 0.0;
        }

        /**
         * Agrega un detalle de venta y recalcula los totales automáticamente.
         * 
         * @param detalle el detalle de venta a agregar
         */
        public void agregarDetalle(DetalleVenta detalle) {
            if (detalles == null) {
                detalles = new ArrayList<>();
            }
            detalles.add(detalle);
            calcularTotales();
        }

        /**
         * Calcula automáticamente todos los totales de la venta.
         * Este método se invoca cada vez que se agrega o modifica un detalle.
         * 
         * <p>Cálculos realizados:</p>
         * <ul>
         *   <li>Subtotal: Suma de subtotales de todos los detalles</li>
         *   <li>Costo total: Suma de costos de todos los detalles</li>
         *   <li>Ganancia bruta: Subtotal - Costo total</li>
         *   <li>Ganancia neta: Ganancia bruta (sin deducciones adicionales)</li>
         * </ul>
         */
        private void calcularTotales() {
            if (detalles != null && !detalles.isEmpty()) {
                this.subtotal = detalles.stream()
                        .mapToDouble(DetalleVenta::getSubtotal)
                        .sum();

                this.costoTotal = detalles.stream()
                        .mapToDouble(DetalleVenta::getCostoTotal)
                        .sum();

                this.gananciaBruta = subtotal - costoTotal;
                this.gananciaNeta = gananciaBruta;

            } else {
                this.subtotal = 0.0;
                this.costoTotal = 0.0;
                this.gananciaBruta = 0.0;
                this.gananciaNeta = 0.0;
            }
        }

        /**
         * Obtiene el código de la venta.
         * 
         * @return el código único de la venta
         */
        public String getCodigo() { 
            return codigo; 
        }

        /**
         * Establece el código de la venta.
         * 
         * @param codigo el nuevo código de la venta
         */
        public void setCodigo(String codigo) { 
            this.codigo = codigo; 
        }

        /**
         * Obtiene el cliente de la venta.
         * 
         * @return el cliente que realiza la compra
         */
        public Cliente getCliente() { 
            return cliente; 
        }

        /**
         * Establece el cliente de la venta.
         * 
         * @param cliente el nuevo cliente
         */
        public void setCliente(Cliente cliente) { 
            this.cliente = cliente; 
        }

        /**
         * Obtiene la fecha y hora de la venta.
         * 
         * @return la fecha de la venta
         */
        public LocalDateTime getFecha() { 
            return fecha; 
        }

        /**
         * Establece la fecha y hora de la venta.
         * 
         * @param fecha la nueva fecha de la venta
         */
        public void setFecha(LocalDateTime fecha) { 
            this.fecha = fecha; 
        }

        /**
         * Obtiene la lista de detalles de la venta.
         * Si la lista es null, se inicializa automáticamente.
         * 
         * @return la lista de detalles de la venta
         */
        public List<DetalleVenta> getDetalles() { 
            if (detalles == null) {
                detalles = new ArrayList<>();
            }
            return detalles; 
        }

        /**
         * Establece la lista de detalles y recalcula los totales.
         * 
         * @param detalles la nueva lista de detalles
         */
        public void setDetalles(List<DetalleVenta> detalles) { 
            this.detalles = detalles;
            calcularTotales();
        }

        /**
         * Obtiene el subtotal de la venta.
         * 
         * @return el subtotal (suma de subtotales de detalles)
         */
        public double getSubtotal() { 
            return subtotal; 
        }

        /**
         * Establece el subtotal de la venta manualmente.
         * 
         * @param subtotal el nuevo subtotal
         */
        public void setSubtotal(double subtotal) { 
            this.subtotal = subtotal; 
        }

        /**
         * Obtiene el IVA de la venta.
         * 
         * @return el valor del IVA
         */
        public double getIva() { 
            return iva; 
        }

        /**
         * Establece el IVA de la venta.
         * 
         * @param iva el nuevo valor de IVA
         */
        public void setIva(double iva) { 
            this.iva = iva; 
        }

        /**
         * Obtiene el total de la venta.
         * 
         * @return el total (subtotal + IVA)
         */
        public double getTotal() { 
            return total; 
        }

        /**
         * Establece el total de la venta.
         * 
         * @param total el nuevo total
         */
        public void setTotal(double total) { 
            this.total = total; 
        }

        /**
         * Obtiene el costo total de la venta.
         * 
         * @return el costo total de los productos vendidos
         */
        public double getCostoTotal() { 
            return costoTotal; 
        }

        /**
         * Establece el costo total de la venta.
         * 
         * @param costoTotal el nuevo costo total
         */
        public void setCostoTotal(double costoTotal) { 
            this.costoTotal = costoTotal; 
        }

        /**
         * Obtiene la ganancia bruta de la venta.
         * 
         * @return la ganancia bruta (subtotal - costo total)
         */
        public double getGananciaBruta() { 
            return gananciaBruta; 
        }

        /**
         * Establece la ganancia bruta de la venta.
         * 
         * @param gananciaBruta la nueva ganancia bruta
         */
        public void setGananciaBruta(double gananciaBruta) { 
            this.gananciaBruta = gananciaBruta; 
        }

        /**
         * Obtiene la ganancia neta de la venta.
         * 
         * @return la ganancia neta después de deducciones
         */
        public double getGananciaNeta() { 
            return gananciaNeta; 
        }

        /**
         * Establece la ganancia neta de la venta.
         * 
         * @param gananciaNeta la nueva ganancia neta
         */
        public void setGananciaNeta(double gananciaNeta) { 
            this.gananciaNeta = gananciaNeta; 
        }

        /**
         * Obtiene el método de pago utilizado.
         * 
         * @return el método de pago (Efectivo, Tarjeta, etc.)
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
         * Calcula el margen de ganancia de la venta como porcentaje.
         * Formula: (gananciaBruta / costoTotal) × 100
         * 
         * @return el margen de ganancia en porcentaje, 0 si el costo es cero
         */
        public double getMargenGanancia() {
            if (costoTotal == 0) return 0.0;
            return (gananciaBruta / costoTotal) * 100;
        }

        /**
         * Calcula el ROI (Return on Investment) de la venta.
         * Es equivalente al margen de ganancia.
         * 
         * @return el ROI en porcentaje
         */
        public double getROI() {
            return getMargenGanancia();
        }

        /**
         * Calcula el porcentaje de ganancia respecto al total de la venta.
         * Formula: (gananciaNeta / total) × 100
         * 
         * @return el porcentaje de ganancia del total, 0 si el total es cero
         */
        public double getPorcentajeGananciaDelTotal() {
            if (total == 0) return 0.0;
            return (gananciaNeta / total) * 100;
        }

        /**
         * Verifica si la venta es rentable.
         * 
         * @return {@code true} si la ganancia neta es positiva
         */
        public boolean esRentable() {
            return gananciaNeta > 0;
        }

        /**
         * Devuelve una representación en cadena de la venta.
         * 
         * @return cadena con información completa de la venta
         */
        @Override
        public String toString() {
            return "Venta{" +
                    "codigo='" + codigo + '\'' +
                    ", cliente=" + (cliente != null ? cliente.getNombre() : "null") +
                    ", fecha=" + fecha +
                    ", subtotal=" + subtotal +
                    ", total=" + total +
                    ", costoTotal=" + costoTotal +
                    ", ganancia=" + gananciaNeta +
                    ", ROI=" + String.format("%.2f", getROI()) + "%" +
                    ", detalles=" + (detalles != null ? detalles.size() : 0) + " items" +
                    '}';
        }
    }