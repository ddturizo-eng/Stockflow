/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Persistencia;

import com.mycompany.stockflow.Modelo.Usuario;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositorio {
    
    private List<Usuario> usuarios;
    
    public UsuarioRepositorio() {
        this.usuarios = new ArrayList<>();
        crearUsuariosPorDefecto();
    }
    
    private void crearUsuariosPorDefecto() {
        // usuarios de prueba
        usuarios.add(new Usuario("admin", "admin123", "Administrador", "ADMIN"));
        usuarios.add(new Usuario("user", "user123", "Usuario Normal", "USUARIO"));
    }
    
    public Usuario autenticar(String username, String password) {
        // Busco usuario con username y password correctos
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null; // No encontrado
    }
    
    public List<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios);
    }
}
