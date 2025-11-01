    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package com.mycompany.stockflow.Modelo;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import javafx.beans.property.SimpleStringProperty;
    import javafx.beans.property.StringProperty;

    /**
     * Representa un usuario del sistema StockFlow.
     * 
     * <p>Esta clase modela los usuarios que pueden acceder al sistema,
     * incluyendo sus credenciales, información personal y permisos de acceso.
     * Implementa propiedades JavaFX para facilitar la vinculación con componentes
     * de interfaz gráfica como TableView.</p>
     * 
     * <p>El sistema registra el último acceso de cada usuario para propósitos
     * de auditoría y seguridad.</p>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * Usuario usuario = new Usuario(
     *     "admin",
     *     "password123",
     *     "Juan Administrador",
     *     "USUARIO"
     * );
     * usuario.actualizarUltimoAcceso();
     * </pre>
     * 
     * @author StockFlow Team
     * @version 1.0
     * @since 1.0
     * @see Rol
     */
    public class Usuario extends Entidad {

        /** Nombre de usuario para autenticación */
        private String username;

        /** Contraseña del usuario (debe almacenarse encriptada) */
        private String password;

        /** Nombre completo del usuario */
        private String nombreCompleto;

        /** Rol del usuario en el sistema */
        private String rol;

        /** Fecha y hora del último acceso al sistema */
        private LocalDateTime ultimoAcceso;

        /** Propiedad JavaFX para el nombre de usuario */
        private StringProperty usernameProperty;

        /** Propiedad JavaFX para el nombre completo */
        private StringProperty nombreCompletoProperty;

        /** Propiedad JavaFX para el rol */
        private StringProperty rolProperty;

        /** Propiedad JavaFX para el último acceso */
        private StringProperty ultimoAccesoProperty;

        /**
         * Constructor completo para crear un usuario.
         * Inicializa todas las propiedades JavaFX para binding con UI.
         * 
         * @param username el nombre de usuario único
         * @param password la contraseña del usuario
         * @param nombreCompleto el nombre completo del usuario
         * @param rol el rol del usuario en el sistema
         */
        public Usuario(String username, String password, String nombreCompleto, String rol) {
            super(username);
            this.username = username;
            this.password = password;
            this.nombreCompleto = nombreCompleto;
            this.rol = rol;
            this.ultimoAcceso = LocalDateTime.now();
            this.usernameProperty = new SimpleStringProperty(username);
            this.nombreCompletoProperty = new SimpleStringProperty(nombreCompleto);
            this.rolProperty = new SimpleStringProperty(rol);
            this.ultimoAccesoProperty = new SimpleStringProperty(formatearFecha(ultimoAcceso));
        }

        /**
         * Obtiene el nombre de usuario.
         * 
         * @return el username del usuario
         */
        public String getUsername() { 
            return username; 
        }

        /**
         * Establece el nombre de usuario y actualiza la propiedad JavaFX.
         * 
         * @param username el nuevo nombre de usuario
         */
        public void setUsername(String username) { 
            this.username = username;
            if (usernameProperty != null) {
                usernameProperty.set(username);
            }
        }

        /**
         * Obtiene la contraseña del usuario.
         * 
         * @return la contraseña (encriptada)
         */
        public String getPassword() { 
            return password; 
        }

        /**
         * Establece la contraseña del usuario.
         * 
         * @param password la nueva contraseña (debe estar encriptada)
         */
        public void setPassword(String password) { 
            this.password = password; 
        }

        /**
         * Obtiene el nombre completo del usuario.
         * 
         * @return el nombre completo
         */
        public String getNombreCompleto() { 
            return nombreCompleto; 
        }

        /**
         * Establece el nombre completo y actualiza la propiedad JavaFX.
         * 
         * @param nombreCompleto el nuevo nombre completo
         */
        public void setNombreCompleto(String nombreCompleto) { 
            this.nombreCompleto = nombreCompleto;
            if (nombreCompletoProperty != null) {
                nombreCompletoProperty.set(nombreCompleto);
            }
        }

        /**
         * Obtiene el rol del usuario.
         * 
         * @return el rol del usuario
         */
        public String getRol() { 
            return rol; 
        }

        /**
         * Establece el rol del usuario y actualiza la propiedad JavaFX.
         * 
         * @param rol el nuevo rol
         */
        public void setRol(String rol) { 
            this.rol = rol;
            if (rolProperty != null) {
                rolProperty.set(rol);
            }
        }

        /**
         * Obtiene la fecha y hora del último acceso.
         * 
         * @return el último acceso registrado
         */
        public LocalDateTime getUltimoAcceso() {
            return ultimoAcceso;
        }

        /**
         * Establece la fecha y hora del último acceso y actualiza la propiedad JavaFX.
         * 
         * @param ultimoAcceso la nueva fecha de último acceso
         */
        public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
            this.ultimoAcceso = ultimoAcceso;
            if (ultimoAccesoProperty != null) {
                ultimoAccesoProperty.set(formatearFecha(ultimoAcceso));
            }
        }

        /**
         * Actualiza el registro de último acceso a la fecha y hora actual.
         * Útil para registrar cuando el usuario inicia sesión.
         */
        public void actualizarUltimoAcceso() {
            this.ultimoAcceso = LocalDateTime.now();
            if (ultimoAccesoProperty != null) {
                ultimoAccesoProperty.set(formatearFecha(this.ultimoAcceso));
            }
        }

        /**
         * Obtiene el nombre del usuario (alias de nombreCompleto).
         * 
         * @return el nombre completo del usuario
         */
        public String getNombre() {
            return this.nombreCompleto;
        }

        /**
         * Obtiene la propiedad JavaFX para el nombre de usuario.
         * Útil para binding con componentes TableView.
         * 
         * @return la propiedad del username
         */
        public StringProperty usernameProperty() {
            if (usernameProperty == null) {
                usernameProperty = new SimpleStringProperty(username);
            }
            return usernameProperty;
        }

        /**
         * Obtiene la propiedad JavaFX para el nombre completo.
         * 
         * @return la propiedad del nombre completo
         */
        public StringProperty nombreCompletoProperty() {
            if (nombreCompletoProperty == null) {
                nombreCompletoProperty = new SimpleStringProperty(nombreCompleto);
            }
            return nombreCompletoProperty;
        }

        /**
         * Obtiene la propiedad JavaFX para el rol.
         * 
         * @return la propiedad del rol
         */
        public StringProperty rolProperty() {
            if (rolProperty == null) {
                rolProperty = new SimpleStringProperty(rol);
            }
            return rolProperty;
        }

        /**
         * Obtiene la propiedad JavaFX para el último acceso.
         * 
         * @return la propiedad del último acceso formateado
         */
        public StringProperty ultimoAccesoProperty() {
            if (ultimoAccesoProperty == null) {
                ultimoAccesoProperty = new SimpleStringProperty(formatearFecha(ultimoAcceso));
            }
            return ultimoAccesoProperty;
        }

        /**
         * Formatea una fecha al formato dd/MM/yyyy HH:mm.
         * 
         * @param fecha la fecha a formatear
         * @return la fecha formateada como String, o "N/A" si es null
         */
        private String formatearFecha(LocalDateTime fecha) {
            if (fecha == null) {
                return "N/A";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return fecha.format(formatter);
        }

        /**
         * Devuelve una representación en cadena del usuario.
         * 
         * @return una cadena con el formato "nombreCompleto (rol)"
         */
        @Override
        public String toString() {
            return nombreCompleto + " (" + rol + ")";
        }
    }

