/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Usuario;
import com.mycompany.stockflow.Persistencia.UsuarioRepositorio;



public class AutenticacionServicio {

    private UsuarioRepositorio usuarioRepositorio;

    public AutenticacionServicio() {
        this.usuarioRepositorio = new UsuarioRepositorio();
    }

    public Usuario autenticar(String username, String password) {
        // Buscar usuario y verificar contraseña
        return usuarioRepositorio.autenticar(username, password);
    }
}