/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Venta;
import com.mycompany.stockflow.Modelo.DetalleVenta;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Persistencia.VentaRepositorio;
import com.mycompany.stockflow.excepciones.*;
import java.io.IOException;
import java.util.List;

/**
 * Servicio para la gestión completa de ventas.
 * 
 * <p>Este servicio maneja todo el ciclo de vida de las ventas en el sistema,
 * desde su creación hasta la consulta de registros históricos. Se encarga de
 * coordinar la actualización automática del inventario cuando se registra una venta.</p>
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Registro de nuevas ventas con múltiples productos</li>
 *   <li>Validación de disponibilidad de stock antes de vender</li>
 *   <li>Actualización automática del inventario</li>
 *   <li>Cálculo automático de totales</li>
 *   <li>Consulta de ventas por código</li>
 *   <li>Listado completo de ventas históricas</li>
 *   <li>Cálculo de totales de ventas para reportes</li>
 * </ul>
 * 
 * <p><b>Proceso de una venta:</b></p>
 * <ol>
 *   <li>Se valida la venta (cliente obligatorio, al menos un producto)</li>
 *   <li>Se verifica disponibilidad de stock para cada producto</li>
 *   <li>Se calcula el total de la venta</li>
 *   <li>Se actualiza el stock de cada producto vendido</li>
 *   <li>Se persiste la venta en el repositorio</li>
 * </ol>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see Venta
 * @see DetalleVenta
 * @see VentaRepositorio
 */
public class VentaServicio {
    
    /** Repositorio para la persistencia de ventas */
    private final VentaRepositorio repositorio;
    
    /** Servicio de productos para actualizar el inventario */
    private final ProductoServicio productoServicio;
    
    /**
     * Constructor que inicializa el repositorio de ventas y el servicio de productos.
     * 
     * <p>Establece las dependencias necesarias para gestionar ventas
     * y mantener sincronizado el inventario.</p>
     */
    public VentaServicio() {
        this.repositorio = new VentaRepositorio();
        this.productoServicio = new ProductoServicio();
    }
    
    /**
     * Guarda una venta completa con sus detalles y actualiza el inventario.
     * 
     * <p>Este método es el punto de entrada principal para registrar ventas desde
     * la interfaz de usuario. Maneja todas las validaciones y excepciones de forma
     * robusta, proporcionando logging detallado para depuración.</p>
     * 
     * <p>Operaciones realizadas:</p>
     * <ul>
     *   <li>Validación de datos de entrada (venta y detalles no nulos)</li>
     *   <li>Asociación de detalles con la venta</li>
     *   <li>Cálculo automático del total</li>
     *   <li>Verificación de stock disponible</li>
     *   <li>Actualización del inventario</li>
     *   <li>Persistencia de la transacción</li>
     * </ul>
     * 
     * @param venta objeto Venta con información de cliente, fecha y código
     * @param detalles lista de DetalleVenta con productos y cantidades
     * @return {@code true} si la venta se guardó exitosamente, {@code false} si hubo algún error
     */
    public boolean guardarVenta(Venta venta, List<DetalleVenta> detalles) {
        try {
            System.out.println("=== VentaServicio.guardarVenta() INICIADO ===");
            
            if (venta == null) {
                System.err.println("ERROR: La venta es null");
                return false;
            }
            
            if (detalles == null || detalles.isEmpty()) {
                System.err.println("ERROR: Los detalles son null o están vacíos");
                throw new VentaInvalidaExcepcion("La venta debe tener al menos un producto");
            }
            
            System.out.println("Venta código: " + venta.getCodigo());
            System.out.println("Cliente: " + (venta.getCliente() != null ? venta.getCliente().getNombre() : "null"));
            System.out.println("Detalles: " + detalles.size());
            
            venta.setDetalles(detalles);
            
            System.out.println("Total calculado: " + venta.getTotal());
            
            crearVenta(venta);
            
            System.out.println("VENTA GUARDADA EXITOSAMENTE ");
            return true;
            
        } catch (VentaInvalidaExcepcion e) {
            System.err.println("ERROR: Venta inválida - " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } catch (InventarioInsuficienteExcepcion e) {
            System.err.println("ERROR: Inventario insuficiente - " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } catch (ProductoNoEncontradoExcepcion e) {
            System.err.println("ERROR: Producto no encontrado - " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } catch (IOException e) {
            System.err.println("ERROR: Error de I/O - " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } catch (Exception e) {
            System.err.println("ERROR: Error inesperado - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Crea una venta y actualiza el stock de los productos vendidos.
     * 
     * <p>Este método realiza la lógica central del proceso de venta:</p>
     * <ol>
     *   <li>Valida que la venta tenga todos los datos requeridos</li>
     *   <li>Itera sobre cada detalle de venta</li>
     *   <li>Verifica que hay stock suficiente para cada producto</li>
     *   <li>Calcula el nuevo stock (stock actual - cantidad vendida)</li>
     *   <li>Actualiza el stock en el sistema</li>
     *   <li>Guarda la venta en el repositorio</li>
     * </ol>
     * 
     * <p><b>Importante:</b> Si algún producto no tiene stock suficiente,
     * se lanza una excepción y no se procesa la venta, manteniendo la
     * integridad del inventario.</p>
     * 
     * @param venta la venta a crear con sus detalles completos
     * @throws IOException si ocurre un error al guardar la venta
     * @throws VentaInvalidaExcepcion si los datos de la venta no son válidos
     * @throws InventarioInsuficienteExcepcion si no hay stock suficiente para algún producto
     * @throws ProductoNoEncontradoExcepcion si algún producto del detalle no existe
     */
    public void crearVenta(Venta venta) throws IOException, VentaInvalidaExcepcion, InventarioInsuficienteExcepcion, ProductoNoEncontradoExcepcion {
        System.out.println("Validando venta...");
        validarVenta(venta);
        
        System.out.println("Actualizando stock de productos...");
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            
            if (producto == null) {
                System.err.println("ERROR: Producto null en detalle");
                throw new ProductoNoEncontradoExcepcion("Producto null en el detalle de venta");
            }
            
            int stockActual = producto.getStock();
            int cantidadVendida = detalle.getCantidad();
            int nuevoStock = stockActual - cantidadVendida;
            
            System.out.println("Producto: " + producto.getNombre() + 
                             " | Stock actual: " + stockActual + 
                             " | Cantidad vendida: " + cantidadVendida + 
                             " | Nuevo stock: " + nuevoStock);
            
            if (nuevoStock < 0) {
                throw new InventarioInsuficienteExcepcion(
                    producto.getNombre(), 
                    stockActual, 
                    cantidadVendida
                );
            }
            
            productoServicio.actualizarStock(producto.getCodigo(), nuevoStock);
        }
        
        System.out.println("Guardando venta en repositorio...");
        repositorio.guardar(venta);
        System.out.println("Venta guardada en repositorio correctamente");
    }
    
    /**
     * Busca y retorna una venta específica por su código.
     * 
     * @param codigo código único de la venta a buscar
     * @return la venta encontrada con todos sus detalles
     * @throws Exception si la venta no existe
     */
    public Venta buscarVenta(String codigo) throws Exception {
        return repositorio.buscar(codigo);
    }
    
    /**
     * Lista todas las ventas registradas en el sistema.
     * 
     * <p>Útil para reportes, historial de ventas y análisis estadísticos.</p>
     * 
     * @return lista completa de todas las ventas
     */
    public List<Venta> listarVentas() {
        return repositorio.listarTodos();
    }
    
    /**
     * Calcula el total acumulado de todas las ventas.
     * 
     * <p>Suma los totales de todas las ventas registradas en el sistema,
     * útil para reportes financieros y análisis de ingresos.</p>
     * 
     * @return suma total de todas las ventas en formato double
     */
    public double calcularTotalVentas() {
        return repositorio.listarTodos().stream()
                .mapToDouble(Venta::getTotal)
                .sum();
    }
    
    /**
     * Valida que una venta tenga todos los datos requeridos.
     * 
     * <p>Reglas de validación:</p>
     * <ul>
     *   <li>La venta no puede ser null</li>
     *   <li>Debe tener un cliente asignado</li>
     *   <li>Debe tener al menos un producto en los detalles</li>
     * </ul>
     * 
     * @param venta la venta a validar
     * @throws VentaInvalidaExcepcion si alguna validación falla, con un mensaje
     *         descriptivo del error
     */
    private void validarVenta(Venta venta) throws VentaInvalidaExcepcion {
        if (venta == null) {
            throw new VentaInvalidaExcepcion("La venta no puede ser null");
        }
        
        if (venta.getCliente() == null) {
            throw new VentaInvalidaExcepcion("Debe seleccionar un cliente");
        }
        
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new VentaInvalidaExcepcion("La venta debe tener al menos un producto");
        }
        
        System.out.println("Venta validada correctamente");
    }
}