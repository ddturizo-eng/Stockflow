    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    /**
     * Representa un producto en el inventario del sistema StockFlow.
     * 
     * <p>Esta clase modela todos los aspectos de un producto, incluyendo información
     * básica, precios de compra y venta, gestión de stock y cálculos de rentabilidad.</p>
     * 
     * <p>El sistema diferencia entre:</p>
     * <ul>
     *   <li><strong>Precio de compra</strong>: Costo de adquisición del producto</li>
     *   <li><strong>Precio de venta</strong>: Precio al que se vende al cliente</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * Producto producto = new Producto(
     *     "P001",
     *     "Laptop HP",
     *     "Electrónica",
     *     1200000.0,  // precio compra
     *     1500000.0,  // precio venta
     *     10,         // stock
     *     5,          // stock mínimo
     *     "Laptop HP 15.6 pulgadas"
     * );
     * 
     * double margen = producto.getMargenGanancia(); // 25%
     * boolean rentable = producto.esRentable(); // true
     * </pre>
     * 
     * @author StockFlow Team
     * @version 2.0
     * @since 1.0
     * @see MovimientoInventario
     * @see DetalleVenta
     */
    public class Producto extends Entidad {

        /** Código único del producto en el sistema */
        private String codigo;

        /** Nombre descriptivo del producto */
        private String nombre;

        /** Categoría a la que pertenece el producto */
        private String categoria;

        /** Precio de compra o costo de adquisición del producto */
        private double precioCompra;

        /** Precio de venta al cliente final */
        private double precioVenta;

        /** Cantidad actual en inventario */
        private int stock;

        /** Cantidad mínima de stock antes de generar alertas */
        private int stockMinimo;

        /** Descripción detallada del producto */
        private String descripcion;

        /**
         * Constructor completo para crear un producto con todos sus atributos.
         * 
         * @param codigo el código único del producto
         * @param nombre el nombre del producto
         * @param categoria la categoría del producto
         * @param precioCompra el precio de compra o costo de adquisición
         * @param precioVenta el precio de venta al público
         * @param stock la cantidad inicial en inventario
         * @param stockMinimo el nivel mínimo de stock
         * @param descripcion la descripción detallada del producto
         */
        public Producto(String codigo, String nombre, String categoria, double precioCompra, 
                        double precioVenta, int stock, int stockMinimo, String descripcion) {
            super(codigo);
            this.codigo = codigo;
            this.nombre = nombre;
            this.categoria = categoria;
            this.precioCompra = precioCompra;
            this.precioVenta = precioVenta;
            this.stock = stock;
            this.stockMinimo = stockMinimo;
            this.descripcion = descripcion;
        }

        /**
         * Constructor simplificado sin especificar categoría y stock mínimo.
         * Usa valores por defecto: categoría "Sin categoría" y stock mínimo 5.
         * 
         * @param codigo el código único del producto
         * @param nombre el nombre del producto
         * @param precioCompra el precio de compra
         * @param precioVenta el precio de venta
         * @param stock la cantidad inicial en inventario
         * @param descripcion la descripción del producto
         */
        public Producto(String codigo, String nombre, double precioCompra, double precioVenta, 
                        int stock, String descripcion) {
            this(codigo, nombre, "Sin categoría", precioCompra, precioVenta, stock, 5, descripcion);
        }

        /**
         * Constructor compatible con versión anterior del sistema.
         * Calcula automáticamente el precio de compra como 60% del precio de venta.
         * 
         * @param codigo el código único del producto
         * @param nombre el nombre del producto
         * @param categoria la categoría del producto
         * @param precio el precio de venta (se calculará precio de compra automáticamente)
         * @param stock la cantidad inicial en inventario
         * @param stockMinimo el nivel mínimo de stock
         * @param descripcion la descripción del producto
         * @deprecated Usar constructor con precios de compra y venta explícitos
         */
        @Deprecated
        public Producto(String codigo, String nombre, String categoria, double precio, 
                        int stock, int stockMinimo, String descripcion) {
            this(codigo, nombre, categoria, precio * 0.6, precio, stock, stockMinimo, descripcion);
        }

        /**
         * Constructor por defecto que inicializa un producto vacío.
         * Se deben establecer los valores mediante los métodos setter.
         */
        public Producto() {
            super("");
            this.codigo = "";
            this.nombre = "";
            this.categoria = "Sin categoría";
            this.precioCompra = 0.0;
            this.precioVenta = 0.0;
            this.stock = 0;
            this.stockMinimo = 5;
            this.descripcion = "";
        }

        /**
         * Obtiene el código del producto.
         * 
         * @return el código único del producto
         */
        public String getCodigo() { 
            return codigo; 
        }

        /**
         * Establece el código del producto y actualiza el ID de la entidad.
         * 
         * @param codigo el nuevo código del producto
         */
        public void setCodigo(String codigo) { 
            this.codigo = codigo;
            setId(codigo);
        }

        /**
         * Obtiene el nombre del producto.
         * 
         * @return el nombre del producto
         */
        public String getNombre() { 
            return nombre; 
        }

        /**
         * Establece el nombre del producto.
         * 
         * @param nombre el nuevo nombre del producto
         */
        public void setNombre(String nombre) { 
            this.nombre = nombre; 
        }

        /**
         * Obtiene la categoría del producto.
         * 
         * @return la categoría del producto
         */
        public String getCategoria() { 
            return categoria; 
        }

        /**
         * Establece la categoría del producto.
         * 
         * @param categoria la nueva categoría
         */
        public void setCategoria(String categoria) { 
            this.categoria = categoria; 
        }

        /**
         * Obtiene el precio de compra del producto.
         * 
         * @return el costo de adquisición del producto
         */
        public double getPrecioCompra() { 
            return precioCompra; 
        }

        /**
         * Establece el precio de compra del producto.
         * 
         * @param precioCompra el nuevo precio de compra
         * @throws IllegalArgumentException si el precio de compra es negativo
         */
        public void setPrecioCompra(double precioCompra) { 
            if (precioCompra < 0) {
                throw new IllegalArgumentException("El precio de compra no puede ser negativo");
            }
            this.precioCompra = precioCompra;
        }

        /**
         * Obtiene el precio de venta del producto.
         * 
         * @return el precio de venta al público
         */
        public double getPrecioVenta() { 
            return precioVenta; 
        }

        /**
         * Establece el precio de venta del producto.
         * 
         * @param precioVenta el nuevo precio de venta
         * @throws IllegalArgumentException si el precio de venta es negativo
         */
        public void setPrecioVenta(double precioVenta) { 
            if (precioVenta < 0) {
                throw new IllegalArgumentException("El precio de venta no puede ser negativo");
            }
            this.precioVenta = precioVenta;
        }

        /**
         * Obtiene el precio del producto (precio de venta).
         * 
         * @return el precio de venta
         * @deprecated Usar {@link #getPrecioVenta()} para mayor claridad
         */
        @Deprecated
        public double getPrecio() { 
            return precioVenta; 
        }

        /**
         * Establece el precio del producto (precio de venta).
         * 
         * @param precio el nuevo precio
         * @deprecated Usar {@link #setPrecioVenta(double)} para mayor claridad
         */
        @Deprecated
        public void setPrecio(double precio) { 
            this.precioVenta = precio; 
        }

        /**
         * Obtiene la cantidad actual en stock.
         * 
         * @return la cantidad disponible en inventario
         */
        public int getStock() { 
            return stock; 
        }

        /**
         * Establece la cantidad en stock del producto.
         * 
         * @param stock la nueva cantidad en inventario
         * @throws IllegalArgumentException si el stock es negativo
         */
        public void setStock(int stock) { 
            if (stock < 0) {
                throw new IllegalArgumentException("Stock no puede ser negativo");
            }
            this.stock = stock; 
        }

        /**
         * Obtiene el nivel mínimo de stock configurado.
         * 
         * @return el stock mínimo antes de generar alertas
         */
        public int getStockMinimo() { 
            return stockMinimo; 
        }

        /**
         * Establece el nivel mínimo de stock.
         * 
         * @param stockMinimo el nuevo nivel mínimo de stock
         */
        public void setStockMinimo(int stockMinimo) { 
            this.stockMinimo = stockMinimo; 
        }

        /**
         * Obtiene la descripción del producto.
         * 
         * @return la descripción detallada
         */
        public String getDescripcion() { 
            return descripcion; 
        }

        /**
         * Establece la descripción del producto.
         * 
         * @param descripcion la nueva descripción
         */
        public void setDescripcion(String descripcion) { 
            this.descripcion = descripcion; 
        }

        /**
         * Calcula la utilidad unitaria del producto.
         * La utilidad es la diferencia entre el precio de venta y el precio de compra.
         * 
         * @return la ganancia por unidad vendida
         */
        public double getUtilidadUnitaria() {
            return precioVenta - precioCompra;
        }

        /**
         * Calcula el margen de ganancia del producto como porcentaje.
         * Formula: ((precioVenta - precioCompra) / precioCompra) * 100
         * 
         * @return el margen de ganancia en porcentaje
         */
        public double getMargenGanancia() {
            if (precioCompra == 0) return 0.0;
            return ((precioVenta - precioCompra) / precioCompra) * 100;
        }

        /**
         * Calcula el markup del producto como porcentaje.
         * Formula: ((precioVenta - precioCompra) / precioVenta) * 100
         * 
         * @return el markup en porcentaje
         */
        public double getMarkenUp() {
            if (precioCompra == 0) return 0.0;
            return ((precioVenta - precioCompra) / precioVenta) * 100;
        }

        /**
         * Calcula la utilidad total del inventario actual.
         * Es el producto de la utilidad unitaria por el stock disponible.
         * 
         * @return la ganancia potencial total del stock actual
         */
        public double getUtilidadTotal() {
            return getUtilidadUnitaria() * stock;
        }

        /**
         * Calcula la inversión total en el inventario actual.
         * Es el producto del precio de compra por el stock disponible.
         * 
         * @return el costo total del inventario actual
         */
        public double getInversionTotal() {
            return precioCompra * stock;
        }

        /**
         * Calcula el valor del inventario a precio de venta.
         * 
         * @return el valor potencial de venta del stock actual
         */
        public double getValorInventarioVenta() {
            return precioVenta * stock;
        }

        /**
         * Verifica si el producto es rentable.
         * Un producto es rentable cuando el precio de venta es mayor al precio de compra.
         * 
         * @return {@code true} si el producto genera ganancia, {@code false} en caso contrario
         */
        public boolean esRentable() {
            return precioVenta > precioCompra;
        }

        /**
         * Verifica si el producto tiene un margen de ganancia bajo.
         * Se considera bajo cuando es menor al 10%.
         * 
         * @return {@code true} si el margen es menor al 10%
         */
        public boolean tieneMargenBajo() {
            return getMargenGanancia() < 10.0;
        }

        /**
         * Verifica si el producto tiene un margen de ganancia alto.
         * Se considera alto cuando es mayor al 50%.
         * 
         * @return {@code true} si el margen es mayor al 50%
         */
        public boolean tieneMargenAlto() {
            return getMargenGanancia() > 50.0;
        }

        /**
         * Verifica si el stock actual está por debajo del nivel mínimo.
         * 
         * @return {@code true} si el stock es menor o igual al stock mínimo
         */
        public boolean tieneStockBajo() {
            return stock <= stockMinimo;
        }

        /**
         * Verifica si hay suficiente stock para una cantidad específica.
         * 
         * @param cantidad la cantidad a verificar
         * @return {@code true} si hay stock suficiente, {@code false} en caso contrario
         */
        public boolean tieneStock(int cantidad) {
            return stock >= cantidad;
        }

        /**
         * Reduce el stock del producto en una cantidad específica.
         * 
         * @param cantidad la cantidad a reducir del stock
         * @throws IllegalArgumentException si la cantidad es mayor al stock disponible
         */
        public void reducirStock(int cantidad) {
            if (cantidad > stock) {
                throw new IllegalArgumentException("Stock insuficiente");
            }
            this.stock -= cantidad;
        }

        /**
         * Aumenta el stock del producto en una cantidad específica.
         * 
         * @param cantidad la cantidad a agregar al stock
         */
        public void aumentarStock(int cantidad) {
            this.stock += cantidad;
        }

        /**
         * Devuelve una representación en cadena del producto.
         * 
         * @return una cadena con el formato "nombre - $precioVenta"
         */
        @Override
        public String toString() {
            return nombre + " - $" + precioVenta;
        }
    }