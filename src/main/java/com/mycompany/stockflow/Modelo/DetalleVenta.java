    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */

    package com.mycompany.stockflow.Modelo;

    import java.io.Serializable;

    /**
     * Representa el detalle de un producto dentro de una venta.
     * 
     * <p>Esta clase modela una línea individual en una venta, incluyendo
     * información del producto, cantidades, precios y cálculos automáticos
     * de rentabilidad por ítem vendido.</p>
     * 
     * <p>Cada detalle calcula automáticamente:</p>
     * <ul>
     *   <li><strong>Subtotal</strong>: Precio de venta unitario × cantidad</li>
     *   <li><strong>Costo total</strong>: Precio de compra unitario × cantidad</li>
     *   <li><strong>Ganancia</strong>: Subtotal - Costo total</li>
     *   <li><strong>Margen de ganancia</strong>: (Ganancia / Costo) × 100</li>
     *   <li><strong>ROI</strong>: Return on Investment del producto</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * Producto laptop = new Producto("P001", "Laptop HP", 1200000, 1500000, 10, "");
     * DetalleVenta detalle = new DetalleVenta(laptop, 2);
     * 
     * double subtotal = detalle.getSubtotal();      // 3,000,000
     * double ganancia = detalle.getGanancia();      // 600,000
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see Venta
     * @see Producto
     */
    public class DetalleVenta implements Serializable {

        /** Identificador único de serialización para control de versiones */
        private static final long serialVersionUID = 1L;

        /** Venta a la que pertenece este detalle */
        private Venta venta;

        /** Producto vendido en esta línea de detalle */
        private Producto producto;

        /** Cantidad de unidades vendidas del producto */
        private int cantidad;

        /** Precio de compra unitario del producto al momento de la venta */
        private double precioCompraUnitario;

        /** Precio de venta unitario del producto al momento de la venta */
        private double precioVentaUnitario;

        /** Subtotal de la línea (precio venta × cantidad) */
        private double subtotal;

        /** Costo total de la línea (precio compra × cantidad) */
        private double costoTotal;

        /** Ganancia generada en esta línea de venta */
        private double ganancia;

        /**
         * Constructor por defecto que inicializa un detalle vacío.
         * Todos los valores numéricos se inicializan en cero.
         */
        public DetalleVenta() {
            this.cantidad = 0;
            this.precioCompraUnitario = 0.0;
            this.precioVentaUnitario = 0.0;
            this.subtotal = 0.0;
            this.costoTotal = 0.0;
            this.ganancia = 0.0;
        }

        /**
         * Constructor que crea un detalle de venta con producto y cantidad.
         * Los precios se toman automáticamente del producto y se calculan los totales.
         * 
         * @param producto el producto a vender
         * @param cantidad la cantidad de unidades a vender
         */
        public DetalleVenta(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.precioCompraUnitario = producto.getPrecioCompra();
            this.precioVentaUnitario = producto.getPrecioVenta();
            calcularTotales();
        }

        /**
         * Calcula automáticamente los totales del detalle de venta.
         * Este método se invoca automáticamente cuando cambian precios o cantidad.
         * 
         * <p>Cálculos realizados:</p>
         * <ul>
         *   <li>Subtotal = precioVentaUnitario × cantidad</li>
         *   <li>Costo total = precioCompraUnitario × cantidad</li>
         *   <li>Ganancia = subtotal - costoTotal</li>
         * </ul>
         */
        private void calcularTotales() {
            this.subtotal = precioVentaUnitario * cantidad;
            this.costoTotal = precioCompraUnitario * cantidad;
            this.ganancia = subtotal - costoTotal;
        }

        /**
         * Obtiene la venta a la que pertenece este detalle.
         * 
         * @return la venta padre
         */
        public Venta getVenta() { 
            return venta; 
        }

        /**
         * Establece la venta a la que pertenece este detalle.
         * 
         * @param venta la venta padre
         */
        public void setVenta(Venta venta) { 
            this.venta = venta; 
        }

        /**
         * Obtiene el producto vendido en este detalle.
         * 
         * @return el producto
         */
        public Producto getProducto() { 
            return producto; 
        }

        /**
         * Establece el producto y actualiza los precios desde el producto.
         * Los totales se recalculan automáticamente.
         * 
         * @param producto el nuevo producto
         */
        public void setProducto(Producto producto) { 
            this.producto = producto;
            if (producto != null) {
                this.precioCompraUnitario = producto.getPrecioCompra();
                this.precioVentaUnitario = producto.getPrecioVenta();
                calcularTotales();
            }
        }

        /**
         * Obtiene la cantidad de unidades vendidas.
         * 
         * @return la cantidad
         */
        public int getCantidad() { 
            return cantidad; 
        }

        /**
         * Establece la cantidad de unidades y recalcula los totales.
         * 
         * @param cantidad la nueva cantidad
         */
        public void setCantidad(int cantidad) { 
            this.cantidad = cantidad;
            calcularTotales();
        }

        /**
         * Obtiene el precio de compra unitario.
         * 
         * @return el precio de compra por unidad
         */
        public double getPrecioCompraUnitario() { 
            return precioCompraUnitario; 
        }

        /**
         * Establece el precio de compra unitario y recalcula los totales.
         * 
         * @param precioCompraUnitario el nuevo precio de compra unitario
         */
        public void setPrecioCompraUnitario(double precioCompraUnitario) { 
            this.precioCompraUnitario = precioCompraUnitario;
            calcularTotales();
        }

        /**
         * Obtiene el precio de venta unitario.
         * 
         * @return el precio de venta por unidad
         */
        public double getPrecioVentaUnitario() { 
            return precioVentaUnitario; 
        }

        /**
         * Establece el precio de venta unitario y recalcula los totales.
         * 
         * @param precioVentaUnitario el nuevo precio de venta unitario
         */
        public void setPrecioVentaUnitario(double precioVentaUnitario) { 
            this.precioVentaUnitario = precioVentaUnitario;
            calcularTotales();
        }

        /**
         * Obtiene el precio unitario (precio de venta).
         * 
         * @return el precio de venta unitario
         * @deprecated Usar {@link #getPrecioVentaUnitario()} para mayor claridad
         */
        @Deprecated
        public double getPrecioUnitario() { 
            return precioVentaUnitario; 
        }

        /**
         * Establece el precio unitario (precio de venta).
         * 
         * @param precioUnitario el nuevo precio unitario
         * @deprecated Usar {@link #setPrecioVentaUnitario(double)} para mayor claridad
         */
        @Deprecated
        public void setPrecioUnitario(double precioUnitario) { 
            this.precioVentaUnitario = precioUnitario;
            calcularTotales();
        }

        /**
         * Obtiene el subtotal de la línea de venta.
         * 
         * @return el subtotal (precio venta × cantidad)
         */
        public double getSubtotal() { 
            return subtotal; 
        }

        /**
         * Establece el subtotal manualmente.
         * Normalmente se calcula automáticamente.
         * 
         * @param subtotal el nuevo subtotal
         */
        public void setSubtotal(double subtotal) { 
            this.subtotal = subtotal; 
        }

        /**
         * Obtiene el costo total de la línea.
         * 
         * @return el costo total (precio compra × cantidad)
         */
        public double getCostoTotal() { 
            return costoTotal; 
        }

        /**
         * Establece el costo total manualmente.
         * 
         * @param costoTotal el nuevo costo total
         */
        public void setCostoTotal(double costoTotal) { 
            this.costoTotal = costoTotal; 
        }

        /**
         * Obtiene la ganancia de la línea de venta.
         * 
         * @return la ganancia (subtotal - costo total)
         */
        public double getGanancia() { 
            return ganancia; 
        }

        /**
         * Establece la ganancia manualmente.
         * 
         * @param ganancia la nueva ganancia
         */
        public void setGanancia(double ganancia) { 
            this.ganancia = ganancia; 
        }

        /**
         * Calcula el margen de ganancia como porcentaje.
         * Formula: (ganancia / costoTotal) × 100
         * 
         * @return el margen de ganancia en porcentaje, 0 si el costo es cero
         */
        public double getMargenGanancia() {
            if (costoTotal == 0) return 0.0;
            return (ganancia / costoTotal) * 100;
        }

        /**
         * Calcula el ROI (Return on Investment) del detalle de venta.
         * Es equivalente al margen de ganancia.
         * 
         * @return el ROI en porcentaje
         */
        public double getROI() {
            return getMargenGanancia();
        }

        /**
         * Verifica si este detalle de venta es rentable.
         * 
         * @return {@code true} si la ganancia es positiva
         */
        public boolean esRentable() {
            return ganancia > 0;
        }

        /**
         * Devuelve una representación en cadena del detalle de venta.
         * 
         * @return cadena con información del detalle
         */
        @Override
        public String toString() {
            return "DetalleVenta{" +
                    "producto=" + (producto != null ? producto.getNombre() : "null") +
                    ", cantidad=" + cantidad +
                    ", precioVenta=" + precioVentaUnitario +
                    ", subtotal=" + subtotal +
                    ", ganancia=" + ganancia +
                    '}';
        }
    }