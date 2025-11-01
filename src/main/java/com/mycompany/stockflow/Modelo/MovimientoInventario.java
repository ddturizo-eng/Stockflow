    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;

    /**
     * Representa un movimiento de inventario (entrada, salida o ajuste).
     * 
     * <p>Esta clase registra todas las transacciones que afectan el stock
     * de productos en el sistema, proporcionando trazabilidad completa
     * de las operaciones de inventario y su impacto financiero.</p>
     * 
     * <p><strong>Tipos de movimientos:</strong></p>
     * <ul>
     *   <li><strong>ENTRADA</strong>: Incrementa el stock (compras, devoluciones de clientes)</li>
     *   <li><strong>SALIDA</strong>: Reduce el stock (ventas, mermas, consumo interno)</li>
     *   <li><strong>AJUSTE</strong>: Correcciones de inventario (positivas o negativas)</li>
     * </ul>
     * 
     * <p><strong>Información registrada:</strong></p>
     * <ul>
     *   <li>Stock anterior y nuevo después del movimiento</li>
     *   <li>Precio de compra unitario al momento del movimiento</li>
     *   <li>Valor total del movimiento</li>
     *   <li>Impacto financiero de la operación</li>
     *   <li>Motivo o razón del movimiento</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * Producto laptop = productoServicio.obtenerPorCodigo("P001");
     * MovimientoInventario entrada = new MovimientoInventario(
     *     "M001",
     *     laptop,
     *     "ENTRADA",
     *     50,              // cantidad
     *     laptop.getStock(), // stock anterior
     *     1200000.0,       // precio compra
     *     "Compra a proveedor XYZ"
     * );
     * 
     * // Stock nuevo se calcula automáticamente
     * int stockNuevo = entrada.getStockNuevo(); // stock anterior + 50
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see Producto
     */
    public class MovimientoInventario extends Entidad {

        /** Código único del movimiento */
        private String codigo;

        /** Producto afectado por el movimiento */
        private Producto producto;

        /** Tipo de movimiento (ENTRADA, SALIDA, AJUSTE) */
        private String tipoMovimiento;

        /** Cantidad de unidades del movimiento (positiva o negativa) */
        private int cantidad;

        /** Stock del producto antes del movimiento */
        private int stockAnterior;

        /** Stock del producto después del movimiento */
        private int stockNuevo;

        /** Precio de compra unitario en el momento del movimiento */
        private double precioCompraUnitario;

        /** Valor total del movimiento (precio × cantidad) */
        private double valorTotal;

        /** Fecha y hora del movimiento */
        private LocalDateTime fecha;

        /** Motivo o descripción del movimiento */
        private String motivo;

        /**
         * Constructor por defecto que inicializa un movimiento vacío.
         * La fecha se establece a la hora actual.
         */
        public MovimientoInventario() {
            super();
            this.fecha = LocalDateTime.now();
            this.precioCompraUnitario = 0.0;
            this.valorTotal = 0.0;
        }

        /**
         * Constructor que crea un movimiento con precio del producto.
         * El precio de compra se toma automáticamente del producto.
         * 
         * @param codigo el código único del movimiento
         * @param producto el producto afectado
         * @param tipoMovimiento el tipo (ENTRADA, SALIDA, AJUSTE)
         * @param cantidad la cantidad de unidades (positiva o negativa)
         * @param stockAnterior el stock antes del movimiento
         * @param motivo la razón del movimiento
         */
        public MovimientoInventario(String codigo, Producto producto, String tipoMovimiento, 
                                    int cantidad, int stockAnterior, String motivo) {
            super(codigo);
            this.codigo = codigo;
            this.producto = producto;
            this.tipoMovimiento = tipoMovimiento;
            this.cantidad = cantidad;
            this.stockAnterior = stockAnterior;
            this.stockNuevo = calcularStockNuevo();
            this.precioCompraUnitario = producto != null ? producto.getPrecioCompra() : 0.0;
            this.valorTotal = precioCompraUnitario * Math.abs(cantidad);
            this.fecha = LocalDateTime.now();
            this.motivo = motivo;
        }

        /**
         * Constructor completo que permite especificar el precio de compra.
         * Útil cuando el precio de compra difiere del precio actual del producto.
         * 
         * @param codigo el código único del movimiento
         * @param producto el producto afectado
         * @param tipoMovimiento el tipo (ENTRADA, SALIDA, AJUSTE)
         * @param cantidad la cantidad de unidades
         * @param stockAnterior el stock antes del movimiento
         * @param precioCompraUnitario el precio de compra unitario
         * @param motivo la razón del movimiento
         */
        public MovimientoInventario(String codigo, Producto producto, String tipoMovimiento, 
                                    int cantidad, int stockAnterior, double precioCompraUnitario, String motivo) {
            super(codigo);
            this.codigo = codigo;
            this.producto = producto;
            this.tipoMovimiento = tipoMovimiento;
            this.cantidad = cantidad;
            this.stockAnterior = stockAnterior;
            this.stockNuevo = calcularStockNuevo();
            this.precioCompraUnitario = precioCompraUnitario;
            this.valorTotal = precioCompraUnitario * Math.abs(cantidad);
            this.fecha = LocalDateTime.now();
            this.motivo = motivo;
        }

        /**
         * Calcula el stock nuevo basado en el tipo de movimiento.
         * 
         * <p>Lógica de cálculo:</p>
         * <ul>
         *   <li>ENTRADA: stockAnterior + cantidad</li>
         *   <li>SALIDA: stockAnterior - cantidad</li>
         *   <li>AJUSTE: stockAnterior + cantidad (puede ser + o -)</li>
         * </ul>
         * 
         * @return el stock resultante después del movimiento
         */
        private int calcularStockNuevo() {
            if ("ENTRADA".equals(tipoMovimiento)) {
                return stockAnterior + cantidad;
            } else if ("SALIDA".equals(tipoMovimiento)) {
                return stockAnterior - cantidad;
            } else if ("AJUSTE".equals(tipoMovimiento)) {
                return stockAnterior + cantidad;
            }
            return stockAnterior;
        }

        /**
         * Obtiene el código del movimiento.
         * 
         * @return el código único del movimiento
         */
        public String getCodigo() { 
            return codigo; 
        }

        /**
         * Establece el código del movimiento y actualiza el ID de la entidad.
         * 
         * @param codigo el nuevo código
         */
        public void setCodigo(String codigo) { 
            this.codigo = codigo;
            setId(codigo);
        }

        /**
         * Obtiene el producto afectado por el movimiento.
         * 
         * @return el producto
         */
        public Producto getProducto() { 
            return producto; 
        }

        /**
         * Establece el producto afectado.
         * 
         * @param producto el nuevo producto
         */
        public void setProducto(Producto producto) { 
            this.producto = producto; 
        }

        /**
         * Obtiene el tipo de movimiento.
         * 
         * @return el tipo (ENTRADA, SALIDA, AJUSTE)
         */
        public String getTipoMovimiento() { 
            return tipoMovimiento; 
        }

        /**
         * Establece el tipo de movimiento.
         * 
         * @param tipoMovimiento el nuevo tipo
         */
        public void setTipoMovimiento(String tipoMovimiento) { 
            this.tipoMovimiento = tipoMovimiento; 
        }

        /**
         * Obtiene la cantidad del movimiento.
         * 
         * @return la cantidad de unidades
         */
        public int getCantidad() { 
            return cantidad; 
        }

        /**
         * Establece la cantidad del movimiento.
         * 
         * @param cantidad la nueva cantidad
         */
        public void setCantidad(int cantidad) { 
            this.cantidad = cantidad; 
        }

        /**
         * Obtiene el stock anterior al movimiento.
         * 
         * @return el stock antes del movimiento
         */
        public int getStockAnterior() { 
            return stockAnterior; 
        }

        /**
         * Establece el stock anterior.
         * 
         * @param stockAnterior el nuevo stock anterior
         */
        public void setStockAnterior(int stockAnterior) { 
            this.stockAnterior = stockAnterior; 
        }

        /**
         * Obtiene el stock después del movimiento.
         * 
         * @return el stock nuevo
         */
        public int getStockNuevo() { 
            return stockNuevo; 
        }

        /**
         * Establece el stock nuevo manualmente.
         * 
         * @param stockNuevo el nuevo stock
         */
        public void setStockNuevo(int stockNuevo) { 
            this.stockNuevo = stockNuevo; 
        }

        /**
         * Obtiene el precio de compra unitario del movimiento.
         * 
         * @return el precio de compra por unidad
         */
        public double getPrecioCompraUnitario() { 
            return precioCompraUnitario; 
        }

        /**
         * Establece el precio de compra unitario y recalcula el valor total.
         * 
         * @param precioCompraUnitario el nuevo precio de compra
         */
        public void setPrecioCompraUnitario(double precioCompraUnitario) { 
            this.precioCompraUnitario = precioCompraUnitario;
            this.valorTotal = precioCompraUnitario * Math.abs(cantidad);
        }

        /**
         * Obtiene el valor total del movimiento.
         * 
         * @return el valor total (precio × cantidad)
         */
        public double getValorTotal() { 
            return valorTotal; 
        }

        /**
         * Establece el valor total manualmente.
         * 
         * @param valorTotal el nuevo valor total
         */
        public void setValorTotal(double valorTotal) { 
            this.valorTotal = valorTotal; 
        }

        /**
         * Obtiene la fecha y hora del movimiento.
         * 
         * @return la fecha del movimiento
         */
        public LocalDateTime getFecha() { 
            return fecha; 
        }

        /**
         * Establece la fecha y hora del movimiento.
         * 
         * @param fecha la nueva fecha
         */
        public void setFecha(LocalDateTime fecha) { 
            this.fecha = fecha; 
        }

        /**
         * Obtiene el motivo del movimiento.
         * 
         * @return la descripción o razón del movimiento
         */
        public String getMotivo() { 
            return motivo; 
        }

        /**
         * Establece el motivo del movimiento.
         * 
         * @param motivo el nuevo motivo
         */
        public void setMotivo(String motivo) { 
            this.motivo = motivo; 
        }

        /**
         * Formatea la fecha del movimiento.
         * 
         * @return la fecha formateada como "dd/MM/yyyy HH:mm" o "N/A" si es null
         */
        public String getFechaFormateada() {
            if (fecha == null) return "N/A";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return fecha.format(formatter);
        }

        /**
         * Obtiene el nombre del producto afectado.
         * 
         * @return el nombre del producto o "N/A" si no existe
         */
        public String getNombreProducto() {
            return producto != null ? producto.getNombre() : "N/A";
        }

        /**
         * Obtiene el código del producto afectado.
         * 
         * @return el código del producto o "N/A" si no existe
         */
        public String getCodigoProducto() {
            return producto != null ? producto.getCodigo() : "N/A";
        }

        /**
         * Verifica si el movimiento es de tipo ENTRADA.
         * 
         * @return {@code true} si es una entrada
         */
        public boolean esEntrada() {
            return "ENTRADA".equals(tipoMovimiento);
        }

        /**
         * Verifica si el movimiento es de tipo SALIDA.
         * 
         * @return {@code true} si es una salida
         */
        public boolean esSalida() {
            return "SALIDA".equals(tipoMovimiento);
        }

        /**
         * Verifica si el movimiento es de tipo AJUSTE.
         * 
         * @return {@code true} si es un ajuste
         */
        public boolean esAjuste() {
            return "AJUSTE".equals(tipoMovimiento);
        }

        /**
         * Obtiene la cantidad con signo apropiado para visualización.
         * 
         * @return cadena con signo "+" para entradas, sin signo para salidas
         */
        public String getSignoCantidad() {
            if (esEntrada() || (esAjuste() && cantidad > 0)) {
                return "+" + cantidad;
            } else if (esSalida() || (esAjuste() && cantidad < 0)) {
                return String.valueOf(cantidad);
            }
            return String.valueOf(cantidad);
        }

        /**
         * Calcula el impacto financiero del movimiento.
         * Las entradas tienen impacto negativo (gasto), las salidas no tienen impacto directo.
         * 
         * @return el impacto financiero (negativo para entradas, 0 para salidas)
         */
        public double getImpactoFinanciero() {
            if (esEntrada()) {
                return -valorTotal; // Las entradas son gastos
            } else if (esSalida()) {
                return 0; // Las salidas no tienen impacto directo (la ganancia se calcula en ventas)
            }
            return 0;
        }

        /**
         * Devuelve una representación en cadena del movimiento.
         * 
         * @return cadena con información del movimiento
         */
        @Override
        public String toString() {
            return "MovimientoInventario{" +
                    "codigo='" + codigo + '\'' +
                    ", producto=" + getNombreProducto() +
                    ", tipo='" + tipoMovimiento + '\'' +
                    ", cantidad=" + cantidad +
                    ", precioCompra=" + precioCompraUnitario +
                    ", valorTotal=" + valorTotal +
                    ", fecha=" + getFechaFormateada() +
                    '}';
        }
    }