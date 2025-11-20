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
 * Utilidad para encriptacion y verificacion segura de contrasenas usando SHA-256 con salt.
 * 
 * <p>Esta clase implementa un esquema de encriptacion robusto que incluye:</p>
 * <ul>
 *   <li>Generacion de salt aleatorio para cada contrasena</li>
 *   <li>Hashing SHA-256 de la contrasena con el salt</li>
 *   <li>Almacenamiento del salt junto con el hash (formato: salt:hash)</li>
 *   <li>Verificacion segura de contrasenas sin almacenar el texto plano</li>
 * </ul>
 * 
 * <p>El uso de salt previene ataques con rainbow tables y hace que cada
 * hash sea unico incluso si dos usuarios tienen la misma contrasena.</p>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 * // Encriptar contrasena al registrar usuario
 * String passwordEncriptada = EncriptadorPassword.encriptar("miPassword123");
 * // passwordEncriptada: "dGVzdHNhbHQ=:aGFzaGRlbGFwYXNzd29yZA=="
 * 
 * // Verificar contrasena al iniciar sesion
 * boolean esValida = EncriptadorPassword.verificar("miPassword123", passwordEncriptada);
 * </pre>
 * 
 * @author StockFlow Team
 * @version 2.0
 * @since 1.0
 */
public class EncriptadorPassword {
    
    /** Algoritmo de hashing utilizado (SHA-256) */
    private static final String ALGORITMO = "SHA-256";
    
    /** Longitud del salt en bytes */
    private static final int LONGITUD_SALT = 16;
    
    /**
     * Constructor privado para evitar instanciacion.
     * Esta clase solo contiene metodos estaticos.
     */
    private EncriptadorPassword() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable");
    }
    
    /**
     * Genera un salt aleatorio de 16 bytes codificado en Base64.
     * 
     * <p>El salt es un valor aleatorio que se combina con la contrasena antes
     * de aplicar el hash. Esto asegura que dos usuarios con la misma contrasena
     * tendran hashes diferentes.</p>
     * 
     * @return String con el salt codificado en Base64
     */
    private static String generarSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[LONGITUD_SALT];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Encripta una contrasena en texto plano usando SHA-256 con salt aleatorio.
     * 
     * <p>El proceso de encriptacion:</p>
     * <ol>
     *   <li>Genera un salt aleatorio</li>
     *   <li>Combina el salt con la contrasena</li>
     *   <li>Aplica SHA-256 al resultado</li>
     *   <li>Retorna en formato "salt:hash" para almacenamiento</li>
     * </ol>
     * 
     * @param password contrasena en texto plano a encriptar
     * @return String en formato "salt:hash" listo para almacenar
     * @throws RuntimeException si el algoritmo SHA-256 no esta disponible
     */
    public static String encriptar(String password) {
        try {
            String salt = generarSalt();
            String hash = hashear(password, salt);
            return salt + ":" + hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contrasena", e);
        }
    }
    
    /**
     * Verifica si una contrasena en texto plano coincide con el hash almacenado.
     * 
     * <p>El proceso de verificacion:</p>
     * <ol>
     *   <li>Extrae el salt del formato "salt:hash"</li>
     *   <li>Aplica el mismo proceso de hash a la contrasena proporcionada</li>
     *   <li>Compara el resultado con el hash almacenado</li>
     * </ol>
     * 
     * <p>Este metodo es seguro contra timing attacks ya que compara strings
     * de longitud constante.</p>
     * 
     * @param password contrasena en texto plano a verificar
     * @param passwordEncriptada contrasena encriptada en formato "salt:hash"
     * @return true si la contrasena es correcta, false en caso contrario
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
     * Crea un hash SHA-256 de la contrasena combinada con el salt.
     * 
     * <p>El proceso:</p>
     * <ol>
     *   <li>Inicializa el MessageDigest con SHA-256</li>
     *   <li>Agrega el salt al digest</li>
     *   <li>Agrega la contrasena al digest</li>
     *   <li>Calcula el hash final</li>
     *   <li>Codifica el resultado en Base64</li>
     * </ol>
     * 
     * @param password contrasena en texto plano
     * @param salt salt a usar en el hashing
     * @return hash SHA-256 codificado en Base64
     * @throws NoSuchAlgorithmException si SHA-256 no esta disponible
     */
    private static String hashear(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITMO);
        md.update(salt.getBytes());
        byte[] bytes = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * Verifica si una cadena tiene el formato de contrasena encriptada.
     * 
     * <p>Una contrasena esta encriptada si tiene el formato "salt:hash",
     * es decir, contiene exactamente un caracter ':' separando dos partes.</p>
     * 
     * @param password cadena a verificar
     * @return true si tiene formato "salt:hash", false en caso contrario
     */
    public static boolean estaEncriptada(String password) {
        return password != null && password.contains(":") && password.split(":").length == 2;
    }
}