/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Persistencia;

import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.excepciones.ProductoNoEncontradoExcepcion;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la persistencia de productos del inventario.
 * 
 * <p>Gestiona el almacenamiento y recuperación de información de productos
 * mediante serialización de objetos. Proporciona operaciones CRUD completas
 * y utiliza el código del producto como identificador único.</p>
 * 
 * <p>Características de almacenamiento:</p>
 * <ul>
 *   <li>Persistencia en archivo binario (data/productos.dat)</li>
 *   <li>Carga automática al inicializar</li>
 *   <li>Guardado inmediato tras cada operación de escritura</li>
 *   <li>Búsqueda eficiente por código de producto</li>
 * </ul>
 * 
 * <p>El código del producto es único e inmutable, se utiliza como
 * identificador principal en todas las operaciones.</p>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see Producto
 * @see ProductoNoEncontradoExcepcion
 */
public class ProductoRepositorio {
    
    /** Ruta del archivo de persistencia de productos */
    private static final String ARCHIVO = "data/productos.dat";
    
    /** Lista en memoria de todos los productos */
    private List<Producto> productos;
    
    /**
     * Constructor que inicializa el repositorio.
     * 
     * <p>Carga automáticamente los productos desde el archivo de persistencia.
     * Si el archivo no existe, inicializa con una lista vacía.</p>
     */
    public ProductoRepositorio() {
        this.productos = cargarProductos();
    }
    
    /**
     * Guarda un nuevo producto en el repositorio.
     * 
     * <p>El producto se añade a la lista en memoria y se persiste
     * inmediatamente en el archivo. Asegúrese de que el código
     * del producto sea único antes de llamar a este método.</p>
     * 
     * @param producto el producto a guardar
     * @throws IOException si ocurre un error al escribir en el archivo
     */
    public void guardar(Producto producto) throws IOException {
        productos.add(producto);
        guardarArchivo();
    }
    
    /**
     * Busca un producto por su código único.
     * 
     * <p>El código es el identificador principal del producto en el sistema.</p>
     * 
     * @param codigo código único del producto a buscar
     * @return el producto encontrado
     * @throws ProductoNoEncontradoExcepcion si no existe un producto con ese código
     */
    public Producto buscar(String codigo) throws ProductoNoEncontradoExcepcion {
        return productos.stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new ProductoNoEncontradoExcepcion(codigo));
    }
    
    /**
     * Lista todos los productos registrados en el inventario.
     * 
     * <p>Retorna una copia de la lista para prevenir modificaciones
     * externas no controladas.</p>
     * 
     * @return lista completa de productos del inventario
     */
    public List<Producto> listarTodos() {
        return new ArrayList<>(productos);
    }
    
    /**
     * Actualiza la información de un producto existente.
     * 
     * <p>Busca el producto por código, reemplaza sus datos con los nuevos
     * valores y persiste inmediatamente los cambios. El código del producto
     * no puede ser modificado.</p>
     * 
     * @param producto el producto con los datos actualizados
     * @throws IOException si ocurre un error al escribir en el archivo
     * @throws ProductoNoEncontradoExcepcion si el producto no existe
     */
    public void actualizar(Producto producto) throws IOException, ProductoNoEncontradoExcepcion {
        Producto existente = buscar(producto.getCodigo());
        int index = productos.indexOf(existente);
        productos.set(index, producto);
        guardarArchivo();
    }
    
    /**
     * Elimina un producto del inventario.
     * 
     * <p>Busca el producto por código, lo elimina de la lista y persiste
     * los cambios. Esta operación es permanente.</p>
     * 
     * <p><b>Advertencia:</b> Eliminar un producto que tiene movimientos
     * de inventario o ventas asociadas puede causar inconsistencias.
     * Verifique las dependencias antes de eliminar.</p>
     * 
     * @param codigo código del producto a eliminar
     * @throws IOException si ocurre un error al escribir en el archivo
     * @throws ProductoNoEncontradoExcepcion si el producto no existe
     */
    public void eliminar(String codigo) throws IOException, ProductoNoEncontradoExcepcion {
        Producto producto = buscar(codigo);
        productos.remove(producto);
        guardarArchivo();
    }
    
    /**
     * Persiste la lista de productos en el archivo.
     * 
     * <p>Crea el directorio data si no existe y serializa la lista completa
     * de productos.</p>
     * 
     * @throws IOException si ocurre un error al escribir en el archivo
     */
    private void guardarArchivo() throws IOException {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(productos);
        }
    }
    
    /**
     * Carga los productos desde el archivo de persistencia.
     * 
     * <p>Deserializa la lista de productos del archivo. Si el archivo no existe,
     * está corrupto o hay error al leer, retorna una lista vacía para permitir
     * que la aplicación inicie correctamente.</p>
     * 
     * @return lista de productos cargados o lista vacía si hay error
     */
    @SuppressWarnings("unchecked")
    private List<Producto> cargarProductos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<Producto>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}