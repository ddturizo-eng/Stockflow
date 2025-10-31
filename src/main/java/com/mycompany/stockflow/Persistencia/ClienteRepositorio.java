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

public class ClienteRepositorio {
    
    private static final String ARCHIVO = "data/clientes.dat";
    private List<Cliente> clientes;
    
    public ClienteRepositorio() {
        this.clientes = cargarClientes();
    }
    
    public void guardar(Cliente cliente) throws IOException {
        clientes.add(cliente);
        guardarArchivo();
    }
    
    public Cliente buscar(String cedula) throws ClienteNoEncontradoExcepcion {
        return clientes.stream()
                .filter(c -> c.getCedula().equals(cedula))
                .findFirst()
                .orElseThrow(() -> new ClienteNoEncontradoExcepcion(cedula));
    }
    
    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes);
    }
    
    public void actualizar(Cliente cliente) throws IOException, ClienteNoEncontradoExcepcion {
        Cliente existente = buscar(cliente.getCedula());
        int index = clientes.indexOf(existente);
        clientes.set(index, cliente);
        guardarArchivo();
    }
    
    public void eliminar(String cedula) throws IOException, ClienteNoEncontradoExcepcion {
        Cliente cliente = buscar(cedula);
        clientes.remove(cliente);
        guardarArchivo();
    }
    
    private void guardarArchivo() throws IOException {
        new File("data").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(clientes);
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Cliente> cargarClientes() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<Cliente>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
