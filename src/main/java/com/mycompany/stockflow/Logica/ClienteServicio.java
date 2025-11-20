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

/**
 * Servicio de Gestión de Clientes.
 * 
 * <p>Proporciona operaciones completas de CRUD (Crear, Leer, Actualizar, Eliminar) 
 * para la gestión de clientes del sistema. Actúa como capa intermedia entre la 
 * interfaz de usuario y la capa de persistencia, aplicando validaciones y 
 * reglas de negocio.</p>
 * 
 * <p><strong>Operaciones disponibles:</strong></p>
 * <ul>
 *   <li>Creación de nuevos clientes con validación de datos</li>
 *   <li>Búsqueda de clientes por cédula (identificador único)</li>
 *   <li>Listado completo de clientes registrados</li>
 *   <li>Actualización de información de clientes existentes</li>
 *   <li>Eliminación de clientes del sistema</li>
 * </ul>
 * 
 * <p><strong>Validaciones implementadas:</strong></p>
 * <ul>
 *   <li>Cédula obligatoria y no vacía</li>
 *   <li>Nombre obligatorio y no vacío</li>
 *   <li>Formato correcto de datos antes de persistir</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>{@code
 * ClienteServicio servicio = new ClienteServicio();
 * 
 * // Crear nuevo cliente
 * Cliente cliente = new Cliente("1234567890", "Juan Pérez", 
 *                               "juan@email.com", "555-1234");
 * try {
 *     servicio.crearCliente(cliente);
 *     System.out.println("Cliente registrado exitosamente");
 * } catch (IOException e) {
 *     System.err.println("Error al guardar: " + e.getMessage());
 * } catch (IllegalArgumentException e) {
 *     System.err.println("Datos inválidos: " + e.getMessage());
 * }
 * 
 * // Buscar cliente
 * try {
 *     Cliente encontrado = servicio.buscarCliente("1234567890");
 *     System.out.println("Cliente: " + encontrado.getNombre());
 * } catch (ClienteNoEncontradoExcepcion e) {
 *     System.err.println("Cliente no existe");
 * }
 * }</pre>
 * 
 * <p><strong>Consideraciones de diseño:</strong></p>
 * <ul>
 *   <li>La cédula es el identificador único inmutable</li>
 *   <li>Todas las operaciones pasan por validación antes de persistir</li>
 *   <li>Manejo de excepciones específicas para cada tipo de error</li>
 *   <li>Separación clara entre lógica de negocio y persistencia</li>
 * </ul>
 * 
 * @author Equipo StockFlow
 * @version 1.0
 * @since 2025
 * 
 * @see Cliente
 * @see ClienteRepositorio
 * @see ClienteNoEncontradoExcepcion
 */
public class ClienteServicio {
    
    /**
     * Repositorio para acceso a datos de clientes.
     */
    private final ClienteRepositorio repositorio;
    
    /**
     * Constructor por defecto.
     * <p>Inicializa el repositorio de clientes necesario para 
     * las operaciones de persistencia.</p>
     */
    public ClienteServicio() {
        this.repositorio = new ClienteRepositorio();
    }
    
    /**
     * Crea y registra un nuevo cliente en el sistema.
     * 
     * <p>Valida que los datos del cliente cumplan con los requisitos mínimos 
     * antes de persistirlos. Los campos obligatorios son cédula y nombre.</p>
     * 
     * <p><strong>Validaciones realizadas:</strong></p>
     * <ul>
     *   <li>Cédula no nula y no vacía (después de trim)</li>
     *   <li>Nombre no nulo y no vacío (después de trim)</li>
     * </ul>
     * 
     * <p><strong>Flujo de ejecución:</strong></p>
     * <ol>
     *   <li>Validar datos del cliente</li>
     *   <li>Si pasa validación, delegar al repositorio para guardar</li>
     *   <li>Si falla validación, lanzar IllegalArgumentException</li>
     *   <li>Si falla persistencia, propagar IOException</li>
     * </ol>
     * 
     * @param cliente Objeto Cliente con los datos a registrar
     * @throws IOException Si ocurre un error al persistir los datos
     * @throws IllegalArgumentException Si los datos del cliente no son válidos
     * 
     * @see #validarCliente(Cliente)
     * @see ClienteRepositorio#guardar(Cliente)
     */
    public void crearCliente(Cliente cliente) throws IOException {
        validarCliente(cliente);
        repositorio.guardar(cliente);
    }
    
    /**
     * Busca un cliente por su cédula (identificador único).
     * 
     * <p>Recupera la información completa de un cliente registrado 
     * utilizando su número de cédula como criterio de búsqueda.</p>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Consultar información de cliente antes de una venta</li>
     *   <li>Verificar si un cliente existe en el sistema</li>
     *   <li>Cargar datos para edición de perfil</li>
     *   <li>Validar cliente antes de generar factura</li>
     * </ul>
     * 
     * @param cedula Número de cédula del cliente a buscar
     * @return Objeto Cliente con toda la información del cliente encontrado
     * @throws ClienteNoEncontradoExcepcion Si no existe ningún cliente con esa cédula
     * 
     * @see ClienteRepositorio#buscar(String)
     */
    public Cliente buscarCliente(String cedula) throws ClienteNoEncontradoExcepcion {
        return repositorio.buscar(cedula);
    }
    
    /**
     * Lista todos los clientes registrados en el sistema.
     * 
     * <p>Recupera la colección completa de clientes sin ningún filtro. 
     * Útil para:</p>
     * <ul>
     *   <li>Mostrar listados completos en interfaz de usuario</li>
     *   <li>Generar reportes de clientes</li>
     *   <li>Operaciones de análisis de base de clientes</li>
     *   <li>Exportación de datos</li>
     * </ul>
     * 
     * <p><strong>Nota:</strong> Para sistemas con gran cantidad de clientes, 
     * considere implementar paginación o filtros para mejorar el rendimiento.</p>
     * 
     * @return Lista con todos los clientes registrados. 
     *         Retorna lista vacía si no hay clientes registrados.
     * 
     * @see ClienteRepositorio#listarTodos()
     */
    public List<Cliente> listarClientes() {
        return repositorio.listarTodos();
    }
    
    /**
     * Actualiza la información de un cliente existente.
     * 
     * <p>Modifica los datos de un cliente ya registrado en el sistema. 
     * La cédula se utiliza como identificador y no puede ser modificada.</p>
     * 
     * <p><strong>Validaciones realizadas:</strong></p>
     * <ul>
     *   <li>El cliente debe tener datos válidos (cédula y nombre obligatorios)</li>
     *   <li>El cliente debe existir en el sistema (verificación por cédula)</li>
     * </ul>
     * 
     * <p><strong>Campos actualizables:</strong></p>
     * <ul>
     *   <li>Nombre</li>
     *   <li>Correo electrónico</li>
     *   <li>Teléfono</li>
     *   <li>Dirección</li>
     *   <li>Otros campos adicionales del modelo Cliente</li>
     * </ul>
     * 
     * <p><strong>Campo NO actualizable:</strong></p>
     * <ul>
     *   <li>Cédula (es el identificador único e inmutable)</li>
     * </ul>
     * 
     * @param cliente Objeto Cliente con los datos actualizados. 
     *                La cédula debe corresponder al cliente a actualizar.
     * @throws IOException Si ocurre un error al persistir los cambios
     * @throws ClienteNoEncontradoExcepcion Si no existe un cliente con esa cédula
     * @throws IllegalArgumentException Si los datos del cliente no son válidos
     * 
     * @see #validarCliente(Cliente)
     * @see ClienteRepositorio#actualizar(Cliente)
     */
    public void actualizarCliente(Cliente cliente) throws IOException, ClienteNoEncontradoExcepcion {
        validarCliente(cliente);
        repositorio.actualizar(cliente);
    }
    
    /**
     * Elimina un cliente del sistema.
     * 
     * <p>Elimina permanentemente el registro de un cliente utilizando 
     * su cédula como identificador.</p>
     * 
     * <p><strong>Advertencias importantes:</strong></p>
     * <ul>
     *   <li><strong>Operación irreversible:</strong> Los datos eliminados no se pueden recuperar</li>
     *   <li><strong>Verificar dependencias:</strong> Asegurarse de que el cliente no tenga 
     *       ventas o facturas asociadas activas</li>
     *   <li><strong>Considerar desactivación:</strong> En lugar de eliminar, considere agregar 
     *       un campo "activo" para desactivación lógica</li>
     *   <li><strong>Auditoría:</strong> Registrar quién y cuándo eliminó el cliente</li>
     * </ul>
     * 
     * <p><strong>Alternativa recomendada:</strong></p>
     * <p>En lugar de eliminar físicamente, considere implementar eliminación lógica 
     * (soft delete) mediante un campo booleano "activo" o "eliminado". Esto permite:</p>
     * <ul>
     *   <li>Mantener integridad referencial con ventas históricas</li>
     *   <li>Recuperar clientes si fue un error</li>
     *   <li>Cumplir con requisitos de auditoría</li>
     *   <li>Análisis histórico de clientes</li>
     * </ul>
     * 
     * @param cedula Número de cédula del cliente a eliminar
     * @throws IOException Si ocurre un error al persistir los cambios
     * @throws ClienteNoEncontradoExcepcion Si no existe un cliente con esa cédula
     * 
     * @see ClienteRepositorio#eliminar(String)
     */
    public void eliminarCliente(String cedula) throws IOException, ClienteNoEncontradoExcepcion {
        repositorio.eliminar(cedula);
    }
    
    /**
     * Valida que un cliente cumpla con los requisitos mínimos de datos.
     * 
     * <p>Verifica que los campos obligatorios del cliente estén presentes 
     * y sean válidos antes de realizar operaciones de persistencia.</p>
     * 
     * <p><strong>Validaciones realizadas:</strong></p>
     * <ul>
     *   <li><strong>Cédula:</strong> No nula, no vacía después de trim</li>
     *   <li><strong>Nombre:</strong> No nulo, no vacío después de trim</li>
     * </ul>
     * 
     * <p><strong>Validaciones adicionales recomendadas:</strong></p>
     * <ul>
     *   <li>Formato de cédula (expresión regular según país)</li>
     *   <li>Formato de email válido</li>
     *   <li>Formato de teléfono</li>
     *   <li>Longitud mínima/máxima de nombre</li>
     *   <li>Caracteres permitidos en nombre (sin números)</li>
     * </ul>
     * 
     * <p><strong>Ejemplo de validaciones mejoradas:</strong></p>
     * <pre>{@code
     * // Validar formato de email
     * if (cliente.getEmail() != null && !cliente.getEmail().isEmpty()) {
     *     if (!cliente.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
     *         throw new IllegalArgumentException("Formato de email inválido");
     *     }
     * }
     * 
     * // Validar formato de cédula (ejemplo Colombia)
     * if (!cliente.getCedula().matches("^[0-9]{7,10}$")) {
     *     throw new IllegalArgumentException("Cédula debe tener entre 7 y 10 dígitos");
     * }
     * }</pre>
     * 
     * @param cliente Objeto Cliente a validar
     * @throws IllegalArgumentException Si algún campo obligatorio es inválido, 
     *                                  con mensaje descriptivo del error
     */
    private void validarCliente(Cliente cliente) {
        if (cliente.getCedula() == null || cliente.getCedula().trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula es obligatoria");
        }
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}