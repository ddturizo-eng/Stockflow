/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Persistencia;

import com.mycompany.stockflow.Modelo.Cliente;
import com.mycompany.stockflow.excepciones.ClienteNoEncontradoExcepcion;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para la persistencia de clientes del sistema.
 * 
 * <p>Gestiona el almacenamiento y recuperación de información de clientes
 * utilizando serialización de objetos en archivos. Proporciona operaciones
 * CRUD (Crear, Leer, Actualizar, Eliminar) completas para la entidad Cliente.</p>
 * 
 * <p>Características de persistencia:</p>
 * <ul>
 *   <li>Almacenamiento en archivo binario mediante serialización</li>
 *   <li>Ubicación: data/clientes.dat</li>
 *   <li>Carga automática de datos al inicializar</li>
 *   <li>Guardado inmediato después de cada operación de escritura</li>
 * </ul>
 * 
 * <p>La cédula del cliente se utiliza como identificador único.</p>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see Cliente
 */
public class ClienteRepositorio {
    
    /** Ruta del archivo de persistencia */
    private static final String ARCHIVO = "data/clientes.dat";
    
    /** Lista en memoria de todos los clientes */
    private List<Cliente> clientes;
    
    /**
     * Constructor que inicializa el repositorio.
     * Carga automáticamente los clientes desde el archivo.
     */
    public ClienteRepositorio() {
        this.clientes = cargarClientes();
    }
    
    /**
     * Guarda un nuevo cliente en el repositorio.
     * 
     * @param cliente el cliente a guardar
     * @throws IOException si ocurre un error al escribir
     */
    public void guardar(Cliente cliente) throws IOException {
        clientes.add(cliente);
        guardarArchivo();
    }
    
    /**
     * Busca un cliente por su cédula.
     * 
     * @param cedula número de cédula del cliente
     * @return el cliente encontrado
     * @throws ClienteNoEncontradoExcepcion si no existe
     */
    public Cliente buscar(String cedula) throws ClienteNoEncontradoExcepcion {
        return clientes.stream()
                .filter(c -> c.getCedula().equals(cedula))
                .findFirst()
                .orElseThrow(() -> new ClienteNoEncontradoExcepcion(cedula));
    }
    
    /**
     * Lista todos los clientes registrados.
     * 
     * @return copia de la lista de clientes
     */
    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes);
    }
    
    /**
     * Actualiza la información de un cliente existente.
     * 
     * @param cliente el cliente con datos actualizados
     * @throws IOException si hay error al guardar
     * @throws ClienteNoEncontradoExcepcion si el cliente no existe
     */
    public void actualizar(Cliente cliente) throws IOException, ClienteNoEncontradoExcepcion {
        Cliente existente = buscar(cliente.getCedula());
        int index = clientes.indexOf(existente);
        clientes.set(index, cliente);
        guardarArchivo();
    }
    
    /**
     * Elimina un cliente del sistema.
     * 
     * @param cedula cédula del cliente a eliminar
     * @throws IOException si hay error al guardar
     * @throws ClienteNoEncontradoExcepcion si el cliente no existe
     */
    public void eliminar(String cedula) throws IOException, ClienteNoEncontradoExcepcion {
        Cliente cliente = buscar(cedula);
        clientes.remove(cliente);
        guardarArchivo();
    }
    
    /**
     * Persiste la lista de clientes en el archivo.
     * 
     * @throws IOException si ocurre un error de escritura
     */
    private void guardarArchivo() throws IOException {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(clientes);
        }
    }
    
    /**
     * Carga los clientes desde el archivo de persistencia.
     * Si el archivo no existe o hay error, retorna lista vacía.
     * 
     * @return lista de clientes cargados
     */
    @SuppressWarnings("unchecked")
    private List<Cliente> cargarClientes() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<Cliente>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}