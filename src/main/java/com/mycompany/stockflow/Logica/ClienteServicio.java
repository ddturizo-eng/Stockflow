/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Cliente;
import com.mycompany.stockflow.Persistencia.ClienteRepositorio;
import com.mycompany.stockflow.excepciones.ClienteNoEncontradoExcepcion;
import java.io.IOException;
import java.util.List;

public class ClienteServicio {
    
    private final ClienteRepositorio repositorio;
    
    public ClienteServicio() {
        this.repositorio = new ClienteRepositorio();
    }
    
    public void crearCliente(Cliente cliente) throws IOException {
        validarCliente(cliente);
        repositorio.guardar(cliente);
    }
    
    public Cliente buscarCliente(String cedula) throws ClienteNoEncontradoExcepcion {
        return repositorio.buscar(cedula);
    }
    
    public List<Cliente> listarClientes() {
        return repositorio.listarTodos();
    }
    
    public void actualizarCliente(Cliente cliente) throws IOException, ClienteNoEncontradoExcepcion {
        validarCliente(cliente);
        repositorio.actualizar(cliente);
    }
    
    public void eliminarCliente(String cedula) throws IOException, ClienteNoEncontradoExcepcion {
        repositorio.eliminar(cedula);
    }
    
    private void validarCliente(Cliente cliente) {
        if (cliente.getCedula() == null || cliente.getCedula().trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula es obligatoria");
        }
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}
