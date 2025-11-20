/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import com.mycompany.stockflow.Logica.UsuarioServicio;
import com.mycompany.stockflow.Modelo.Usuario;
import com.mycompany.stockflow.Modelo.Rol;

/**
 * Script para crear el primer usuario administrador
 * Ejecutar solo una vez para setup inicial
 * 
 * INSTRUCCIONES:
 * 1. Ejecutar este archivo como Java Application
 * 2. Se creará el usuario "Danieltur" con contraseña "1052041109"
 * 3. Ya podrás iniciar sesión en el sistema
 */
public class CrearPrimerAdmin {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  CREAR PRIMER ADMINISTRADOR");
        System.out.println("========================================");
        System.out.println();
        
        UsuarioServicio usuarioServicio = new UsuarioServicio();
        
        try {
            // Verificar si ya existen usuarios
            if (usuarioServicio.contarUsuarios() > 0) {
                System.out.println("⚠️  ERROR: Ya existen usuarios en el sistema");
                System.out.println("    Total de usuarios: " + usuarioServicio.contarUsuarios());
                System.out.println();
                System.out.println("    Si deseas reiniciar el sistema, elimina el archivo:");
                System.out.println("    data/usuarios.dat");
                System.out.println();
                return;
            }
            
            // Datos del primer admin
            String username = "Danieltur";
            String password = "1052041109";
            String nombreCompleto = "Daniel Turizo";
            String email = "daniel@stockflow.com";
            
            System.out.println("📝 Creando usuario administrador...");
            System.out.println("   Username: " + username);
            System.out.println("   Nombre: " + nombreCompleto);
            System.out.println("   Email: " + email);
            System.out.println("   Rol: Administrador");
            System.out.println();
            
            // Crear primer admin
            Usuario admin = usuarioServicio.crearPrimerAdmin(
                username, password, nombreCompleto, email
            );
            
            System.out.println("✅ ¡USUARIO CREADO EXITOSAMENTE!");
            System.out.println();
            System.out.println("========================================");
            System.out.println("  CREDENCIALES DE ACCESO");
            System.out.println("========================================");
            System.out.println("  Usuario: " + admin.getUsername());
            System.out.println("  Contraseña: " + password);
            System.out.println("  Rol: " + admin.getRol().getNombre());
            System.out.println("========================================");
            System.out.println();
            System.out.println("✓ Ahora puedes iniciar sesión en StockFlow");
            System.out.println();
            
        } catch (IllegalStateException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            System.out.println();
            
        } catch (Exception e) {
            System.out.println("❌ ERROR INESPERADO:");
            System.out.println("   " + e.getMessage());
            System.out.println();
            e.printStackTrace();
        }
    }
}