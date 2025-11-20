/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.MovimientoInventario;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Persistencia.InventarioRepositorio;
import java.io.IOException;
import java.util.List;

/**
 * Servicio para la gestión de movimientos de inventario.
 * 
 * <p>Esta clase proporciona funcionalidades para registrar, consultar y controlar
 * todos los movimientos de inventario (entradas, salidas y ajustes), manteniendo
 * la trazabilidad completa del stock y actualizando automáticamente las existencias
 * de productos.</p>
 * 
 * <p>Tipos de movimientos soportados:</p>
 * <ul>
 *   <li><b>ENTRADA:</b> Incrementa el stock (compras, devoluciones de clientes)</li>
 *   <li><b>SALIDA:</b> Disminuye el stock (ventas, devoluciones a proveedores)</li>
 *   <li><b>AJUSTE:</b> Correcciones de inventario (positivas o negativas)</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see MovimientoInventario
 * @see Producto
 */
public class InventarioServicio {
    
    /** Repositorio para persistencia de movimientos de inventario */
    private final InventarioRepositorio repositorio;
    
    /** Servicio de productos para actualizar el stock */
    private final ProductoServicio productoServicio;
    
    /**
     * Constructor que inicializa el repositorio y el servicio de productos.
     * 
     * <p>Crea instancias nuevas de los servicios necesarios para gestionar
     * los movimientos de inventario y mantener sincronizado el stock.</p>
     */
    public InventarioServicio() {
        this.repositorio = new InventarioRepositorio();
        this.productoServicio = new ProductoServicio();
    }
    
    /**
     * Registra un movimiento de inventario y actualiza el stock del producto.
     * 
     * <p>Este método es la función principal para registrar cualquier tipo de movimiento.
     * Realiza las siguientes operaciones:</p>
     * <ol>
     *   <li>Valida el movimiento según el tipo y cantidad</li>
     *   <li>Genera un código único para el movimiento</li>
     *   <li>Crea el registro del movimiento con stock anterior y nuevo</li>
     *   <li>Actualiza el stock del producto en el sistema</li>
     *   <li>Persiste el movimiento en el repositorio</li>
     * </ol>
     * 
     * @param producto producto sobre el que se realiza el movimiento
     * @param tipoMovimiento tipo de movimiento: "ENTRADA", "SALIDA" o "AJUSTE"
     * @param cantidad cantidad de unidades del movimiento (puede ser negativa en ajustes)
     * @param motivo descripción o razón del movimiento
     * @return el movimiento de inventario creado y persistido
     * @throws Exception si el movimiento no es válido o si hay problemas de persistencia
     */
    public MovimientoInventario registrarMovimiento(Producto producto, String tipoMovimiento, 
                                                   int cantidad, String motivo) throws Exception {
        validarMovimiento(producto, tipoMovimiento, cantidad);
        
        int ultimoNumero = repositorio.obtenerUltimoNumero();
        String codigo = String.format("MOV-%04d", ultimoNumero + 1);
        
        int stockActual = producto.getStock();
        
        MovimientoInventario movimiento = new MovimientoInventario(
            codigo, producto, tipoMovimiento, cantidad, stockActual, motivo
        );
        
        int nuevoStock = calcularNuevoStock(stockActual, tipoMovimiento, cantidad);
        movimiento.setStockNuevo(nuevoStock);
        
        productoServicio.actualizarStock(producto.getCodigo(), nuevoStock);
        
        repositorio.guardar(movimiento);
        
        return movimiento;
    }
    
    /**
     * Registra una entrada de mercancía al inventario.
     * 
     * <p>Método de conveniencia para registrar entradas de inventario
     * (compras, recepciones, devoluciones de clientes, etc.).</p>
     * 
     * @param producto producto que ingresa al inventario
     * @param cantidad cantidad de unidades que ingresan (debe ser positiva)
     * @param motivo descripción del motivo de la entrada
     * @return el movimiento de entrada registrado
     * @throws Exception si la entrada no es válida o hay problemas de persistencia
     */
    public MovimientoInventario registrarEntrada(Producto producto, int cantidad, String motivo) throws Exception {
        return registrarMovimiento(producto, "ENTRADA", cantidad, motivo);
    }
    
    /**
     * Registra un ajuste de inventario.
     * 
     * <p>Los ajustes se utilizan para corregir diferencias entre el stock físico
     * y el stock registrado en el sistema. La cantidad puede ser positiva
     * (para incrementar) o negativa (para disminuir).</p>
     * 
     * @param producto producto a ajustar
     * @param cantidad cantidad del ajuste (positiva o negativa)
     * @param motivo descripción del motivo del ajuste (ej: "Inventario físico", "Producto dañado")
     * @return el movimiento de ajuste registrado
     * @throws Exception si el ajuste no es válido o dejaría el stock negativo
     */
    public MovimientoInventario registrarAjuste(Producto producto, int cantidad, String motivo) throws Exception {
        return registrarMovimiento(producto, "AJUSTE", cantidad, motivo);
    }
    
    /**
     * Registra una salida de mercancía del inventario.
     * 
     * <p>Método de conveniencia para registrar salidas de inventario
     * (ventas, devoluciones a proveedores, productos dados de baja, etc.).</p>
     * 
     * @param producto producto que sale del inventario
     * @param cantidad cantidad de unidades que salen (debe ser positiva)
     * @param motivo descripción del motivo de la salida
     * @return el movimiento de salida registrado
     * @throws Exception si no hay stock suficiente o hay problemas de persistencia
     */
    public MovimientoInventario registrarSalida(Producto producto, int cantidad, String motivo) throws Exception {
        return registrarMovimiento(producto, "SALIDA", cantidad, motivo);
    }
    
    /**
     * Busca un movimiento de inventario por su código.
     * 
     * @param codigo código único del movimiento (formato: MOV-XXXX)
     * @return el movimiento encontrado
     * @throws Exception si el movimiento no existe
     */
    public MovimientoInventario buscarMovimiento(String codigo) throws Exception {
        return repositorio.buscar(codigo);
    }
    
    /**
     * Lista todos los movimientos de inventario registrados.
     * 
     * @return lista completa de movimientos ordenados por fecha
     */
    public List<MovimientoInventario> listarMovimientos() {
        return repositorio.listarTodos();
    }
    
    /**
     * Busca todos los movimientos relacionados con un producto específico.
     * 
     * <p>Útil para ver el historial completo de movimientos de un producto
     * y hacer seguimiento de su trazabilidad.</p>
     * 
     * @param codigoProducto código del producto a consultar
     * @return lista de movimientos del producto
     */
    public List<MovimientoInventario> buscarPorProducto(String codigoProducto) {
        return repositorio.buscarPorProducto(codigoProducto);
    }
    
    /**
     * Busca movimientos por tipo específico.
     * 
     * @param tipoMovimiento tipo a filtrar: "ENTRADA", "SALIDA" o "AJUSTE"
     * @return lista de movimientos del tipo especificado
     */
    public List<MovimientoInventario> buscarPorTipo(String tipoMovimiento) {
        return repositorio.buscarPorTipo(tipoMovimiento);
    }
    
    /**
     * Obtiene la lista de productos que tienen stock por debajo del mínimo.
     * 
     * <p>Esta función es útil para identificar productos que necesitan
     * reabastecimiento urgente y generar alertas o reportes.</p>
     * 
     * @return lista de productos con stock bajo
     */
    public List<Producto> obtenerProductosStockBajo() {
        List<Producto> todosLosProductos = productoServicio.listarProductos();
        return todosLosProductos.stream()
                .filter(Producto::tieneStockBajo)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Obtiene los movimientos más recientes del inventario.
     * 
     * <p>Útil para dashboards o vistas resumidas que muestran la actividad
     * reciente del inventario.</p>
     * 
     * @param cantidad número de movimientos recientes a obtener
     * @return lista de los últimos N movimientos
     */
    public List<MovimientoInventario> obtenerUltimosMovimientos(int cantidad) {
        return repositorio.obtenerUltimosMovimientos(cantidad);
    }
    
    /**
     * Calcula el nuevo stock después de aplicar un movimiento.
     * 
     * <p>Lógica de cálculo según tipo de movimiento:</p>
     * <ul>
     *   <li><b>ENTRADA:</b> stock_actual + cantidad</li>
     *   <li><b>SALIDA:</b> stock_actual - cantidad</li>
     *   <li><b>AJUSTE:</b> stock_actual + cantidad (cantidad puede ser negativa)</li>
     * </ul>
     * 
     * @param stockActual stock actual del producto
     * @param tipoMovimiento tipo de movimiento a aplicar
     * @param cantidad cantidad del movimiento
     * @return el stock resultante después del movimiento
     */
    private int calcularNuevoStock(int stockActual, String tipoMovimiento, int cantidad) {
        switch (tipoMovimiento) {
            case "ENTRADA":
                return stockActual + cantidad;
            case "SALIDA":
                return stockActual - cantidad;
            case "AJUSTE":
                return stockActual + cantidad;
            default:
                return stockActual;
        }
    }
    
    /**
     * Valida que un movimiento de inventario sea correcto antes de registrarlo.
     * 
     * <p>Validaciones realizadas:</p>
     * <ul>
     *   <li>El producto no puede ser null</li>
     *   <li>El tipo de movimiento debe ser válido</li>
     *   <li>La cantidad no puede ser cero</li>
     *   <li>Las salidas no pueden exceder el stock disponible</li>
     *   <li>Los ajustes negativos no pueden dejar el stock en negativo</li>
     * </ul>
     * 
     * @param producto producto del movimiento
     * @param tipoMovimiento tipo de movimiento
     * @param cantidad cantidad del movimiento
     * @throws Exception si alguna validación falla
     */
    private void validarMovimiento(Producto producto, String tipoMovimiento, int cantidad) throws Exception {
        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        
        if (tipoMovimiento == null || tipoMovimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio");
        }
        
        if (!tipoMovimiento.equals("ENTRADA") && !tipoMovimiento.equals("SALIDA") && !tipoMovimiento.equals("AJUSTE")) {
            throw new IllegalArgumentException("Tipo de movimiento inválido");
        }
        
        if (cantidad == 0) {
            throw new IllegalArgumentException("La cantidad debe ser diferente de 0");
        }
        
        if ("SALIDA".equals(tipoMovimiento) && cantidad > producto.getStock()) {
            throw new Exception("Stock insuficiente. Stock actual: " + producto.getStock());
        }
        
        if ("AJUSTE".equals(tipoMovimiento) && cantidad < 0) {
            int nuevoStock = producto.getStock() + cantidad;
            if (nuevoStock < 0) {
                throw new Exception("El ajuste dejaría el stock en negativo. Stock actual: " + producto.getStock());
            }
        }
    }
}