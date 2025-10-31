/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad para encriptar y verificar contraseñas usando SHA-256
 */
public class EncriptadorPassword {
    
    private static final String ALGORITMO = "SHA-256";
    private static final int LONGITUD_SALT = 16;
    
    /**
     * Genera un salt aleatorio
     */
    private static String generarSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[LONGITUD_SALT];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Encripta una contraseña con salt
     * @param password Contraseña en texto plano
     * @return Contraseña encriptada en formato: salt:hash
     */
    public static String encriptar(String password) {
        try {
            String salt = generarSalt();
            String hash = hashear(password, salt);
            return salt + ":" + hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }
    
    /**
     * Verifica si una contraseña coincide con el hash almacenado
     * @param password Contraseña en texto plano
     * @param passwordEncriptada Contraseña encriptada (formato: salt:hash)
     * @return true si coincide, false en caso contrario
     */
    public static boolean verificar(String password, String passwordEncriptada) {
        try {
            String[] partes = passwordEncriptada.split(":");
            if (partes.length != 2) {
                return false;
            }
            
            String salt = partes[0];
            String hashAlmacenado = partes[1];
            String hashCalculado = hashear(password, salt);
            
            return hashAlmacenado.equals(hashCalculado);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Crea un hash SHA-256 de la contraseña con salt
     */
    private static String hashear(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITMO);
        md.update(salt.getBytes());
        byte[] bytes = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * Verifica si una cadena está encriptada (tiene formato salt:hash)
     */
    public static boolean estaEncriptada(String password) {
        return password != null && password.contains(":") && password.split(":").length == 2;
    }
}
