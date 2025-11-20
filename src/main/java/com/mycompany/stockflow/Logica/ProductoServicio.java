/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Persistencia.ProductoRepositorio;
import com.mycompany.stockflow.excepciones.ProductoNoEncontradoExcepcion;
import java.io.IOException;
import java.util.List;

/**
 * Servicio para la gestión de productos del inventario.
 * 
 * <p>Esta clase proporciona todas las operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para productos, incluyendo validaciones de negocio y mantenimiento de la integridad
 * de los datos. Actúa como capa intermedia entre la interfaz de usuario y la capa
 * de persistencia.</p>
 * 
 * <p>Responsabilidades principales:</p>
 * <ul>
 *   <li>Crear y registrar nuevos productos</li>
 *   <li>Consultar productos por código o listar todos</li>
 *   <li>Actualizar información de productos existentes</li>
 *   <li>Eliminar productos del sistema</li>
 *   <li>Gestionar actualizaciones de stock</li>
 *   <li>Validar datos antes de persistirlos</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see Producto
 * @see ProductoRepositorio
 */
public class ProductoServicio {
    
    /** Repositorio para la persistencia de productos */
    private final ProductoRepositorio repositorio;
    
    /**
     * Constructor que inicializa el repositorio de productos.
     * 
     * <p>Crea una nueva instancia del repositorio que se encargará
     * de todas las operaciones de persistencia de datos.</p>
     */
    public ProductoServicio() {
        this.repositorio = new ProductoRepositorio();
    }
    
    /**
     * Crea y registra un nuevo producto en el sistema.
     * 
     * <p>Antes de guardar el producto, se realizan las siguientes validaciones:</p>
     * <ul>
     *   <li>El código no puede estar vacío</li>
     *   <li>El nombre no puede estar vacío</li>
     *   <li>El precio debe ser mayor a 0</li>
     *   <li>El stock no puede ser negativo</li>
     * </ul>
     * 
     * @param producto el producto a crear con toda su información
     * @throws IOException si ocurre un error al guardar el producto
     * @throws IllegalArgumentException si los datos del producto no son válidos
     */
    public void crearProducto(Producto producto) throws IOException {
        validarProducto(producto);
        repositorio.guardar(producto);
    }
    
    /**
     * Busca y retorna un producto por su código único.
     * 
     * @param codigo código único del producto a buscar
     * @return el producto encontrado
     * @throws ProductoNoEncontradoExcepcion si no existe un producto con ese código
     */
    public Producto buscarProducto(String codigo) throws ProductoNoEncontradoExcepcion {
        return repositorio.buscar(codigo);
    }
    
    /**
     * Lista todos los productos registrados en el sistema.
     * 
     * <p>Este método retorna la lista completa de productos disponibles
     * en el inventario, útil para vistas generales, reportes y búsquedas.</p>
     * 
     * @return lista completa de productos
     */
    public List<Producto> listarProductos() {
        return repositorio.listarTodos();
    }
    
    /**
     * Actualiza la información de un producto existente.
     * 
     * <p>Permite modificar cualquier atributo del producto excepto su código.
     * Los datos actualizados son validados antes de ser persistidos.</p>
     * 
     * @param producto el producto con los datos actualizados
     * @throws IOException si ocurre un error al actualizar el producto
     * @throws ProductoNoEncontradoExcepcion si el producto no existe
     * @throws IllegalArgumentException si los nuevos datos no son válidos
     */
    public void actualizarProducto(Producto producto) throws IOException, ProductoNoEncontradoExcepcion {
        validarProducto(producto);
        repositorio.actualizar(producto);
    }
    
    /**
     * Elimina un producto del sistema.
     * 
     * <p><b>Nota:</b> Esta operación es permanente. Se recomienda verificar
     * que el producto no tenga movimientos de inventario o ventas asociadas
     * antes de eliminarlo.</p>
     * 
     * @param codigo código del producto a eliminar
     * @throws IOException si ocurre un error al eliminar el producto
     * @throws ProductoNoEncontradoExcepcion si el producto no existe
     */
    public void eliminarProducto(String codigo) throws IOException, ProductoNoEncontradoExcepcion {
        repositorio.eliminar(codigo);
    }
    
    /**
     * Actualiza únicamente el stock de un producto.
     * 
     * <p>Método optimizado para actualizar solo la cantidad en stock sin
     * modificar otros atributos del producto. Es utilizado principalmente
     * por el servicio de inventario y ventas.</p>
     * 
     * @param codigo código del producto a actualizar
     * @param nuevaCantidad nueva cantidad de stock (debe ser no negativa)
     * @throws IOException si ocurre un error al actualizar
     * @throws ProductoNoEncontradoExcepcion si el producto no existe
     */
    public void actualizarStock(String codigo, int nuevaCantidad) throws IOException, ProductoNoEncontradoExcepcion {
        Producto producto = buscarProducto(codigo);
        producto.setStock(nuevaCantidad);
        repositorio.actualizar(producto);
    }
    
    /**
     * Valida que todos los datos del producto sean correctos.
     * 
     * <p>Reglas de validación aplicadas:</p>
     * <ul>
     *   <li><b>Código:</b> Obligatorio y no puede estar vacío</li>
     *   <li><b>Nombre:</b> Obligatorio y no puede estar vacío</li>
     *   <li><b>Precio:</b> Debe ser mayor a 0</li>
     *   <li><b>Stock:</b> No puede ser negativo</li>
     * </ul>
     * 
     * @param producto el producto a validar
     * @throws IllegalArgumentException si alguna validación falla, con un mensaje
     *         descriptivo del error encontrado
     */
    private void validarProducto(Producto producto) {
        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }
}