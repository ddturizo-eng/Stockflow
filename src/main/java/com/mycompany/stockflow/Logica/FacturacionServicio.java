/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Factura;
import com.mycompany.stockflow.Modelo.Venta;
import com.mycompany.stockflow.Persistencia.FacturacionRepositorio;
import java.io.IOException;
import java.util.List;

/**
 * Servicio de Gestión de Facturación.
 * 
 * <p>Maneja todo el ciclo de vida de las facturas del sistema, desde su 
 * generación a partir de ventas hasta su anulación. Proporciona operaciones 
 * completas de CRUD y cálculos financieros relacionados.</p>
 * 
 * <p><strong>Responsabilidades principales:</strong></p>
 * <ul>
 *   <li>Generación automática de facturas desde ventas</li>
 *   <li>Asignación de números de comprobante únicos y secuenciales</li>
 *   <li>Cálculo de subtotales, IVA, descuentos y totales</li>
 *   <li>Gestión de métodos de pago y cambio</li>
 *   <li>Consulta y búsqueda de facturas</li>
 *   <li>Anulación de facturas (con validaciones)</li>
 *   <li>Cálculos de totales facturados</li>
 * </ul>
 * 
 * <p><strong>Estructura de numeración:</strong></p>
 * <p>Las facturas se numeran secuencialmente con el formato:</p>
 * <pre>COMP-001, COMP-002, COMP-003, ...</pre>
 * 
 * <p><strong>Estados de factura:</strong></p>
 * <ul>
 *   <li><strong>PAGADA:</strong> Factura completada y pagada</li>
 *   <li><strong>PENDIENTE:</strong> Factura generada pero no pagada</li>
 *   <li><strong>ANULADA:</strong> Factura cancelada (no se puede modificar)</li>
 * </ul>
 * 
 * <p><strong>Ejemplo de uso completo:</strong></p>
 * <pre>{@code
 * FacturacionServicio servicio = new FacturacionServicio();
 * 
 * // 1. Generar factura desde una venta
 * Factura factura = servicio.generarFacturaDesdeVenta(
 *     venta,              // Venta completada
 *     "EFECTIVO",         // Método de pago
 *     50000.0,            // Monto recibido
 *     5000.0,             // Cambio
 *     40000.0,            // Subtotal
 *     5000.0,             // IVA (12.5%)
 *     0.0                 // Descuento
 * );
 * 
 * System.out.println("Factura generada: " + factura.getNumeroComprobante());
 * 
 * // 2. Buscar factura
 * Factura encontrada = servicio.buscarFactura("COMP-001");
 * 
 * // 3. Listar facturas de un cliente
 * List<Factura> facturasCliente = servicio.buscarPorCliente("Juan Pérez");
 * 
 * // 4. Calcular total facturado
 * double totalFacturado = servicio.calcularTotalFacturado();
 * System.out.println("Total facturado: $" + totalFacturado);
 * 
 * // 5. Anular factura si es necesario
 * try {
 *     servicio.anularFactura("COMP-001");
 *     System.out.println("Factura anulada exitosamente");
 * } catch (Exception e) {
 *     System.err.println("Error: " + e.getMessage());
 * }
 * }</pre>
 * 
 * <p><strong>Consideraciones legales y contables:</strong></p>
 * <ul>
 *   <li>Los números de comprobante deben ser únicos y secuenciales</li>
 *   <li>Las facturas anuladas deben mantener su número para auditoría</li>
 *   <li>Se debe registrar fecha y hora de cada operación</li>
 *   <li>Cumplir con normativas fiscales locales de facturación</li>
 * </ul>
 * 
 * @author Equipo StockFlow
 * @version 1.0
 * @since 2025
 * 
 * @see Factura
 * @see Venta
 * @see FacturacionRepositorio
 */
public class FacturacionServicio {
    
    /**
     * Repositorio para acceso a datos de facturación.
     */
    private final FacturacionRepositorio repositorio;
    
    /**
     * Constructor por defecto.
     * <p>Inicializa el repositorio de facturación necesario para 
     * las operaciones de persistencia.</p>
     */
    public FacturacionServicio() {
        this.repositorio = new FacturacionRepositorio();
    }
    
    /**
     * Genera una factura completa a partir de una venta realizada.
     * 
     * <p>Este es el método principal para crear facturas. Toma una venta 
     * existente y genera el documento fiscal correspondiente con todos 
     * los detalles financieros.</p>
     * 
     * <p><strong>Proceso de generación:</strong></p>
     * <ol>
     *   <li>Obtiene el último número de comprobante usado</li>
     *   <li>Genera el siguiente número secuencial (formato: COMP-XXX)</li>
     *   <li>Crea objeto Factura asociado a la venta</li>
     *   <li>Establece todos los valores financieros</li>
     *   <li>Configura estado como "PAGADA"</li>
     *   <li>Persiste la factura en el sistema</li>
     * </ol>
     * 
     * <p><strong>Componentes financieros:</strong></p>
     * <ul>
     *   <li><strong>Subtotal:</strong> Suma de productos sin impuestos ni descuentos</li>
     *   <li><strong>IVA:</strong> Impuesto al valor agregado aplicado</li>
     *   <li><strong>Descuento:</strong> Reducción aplicada al subtotal</li>
     *   <li><strong>Total:</strong> Subtotal + IVA - Descuento</li>
     *   <li><strong>Monto Recibido:</strong> Dinero entregado por el cliente</li>
     *   <li><strong>Cambio:</strong> Diferencia entre monto recibido y total</li>
     * </ul>
     * 
     * <p><strong>Fórmula del total:</strong></p>
     * <pre>
     * Total = (Subtotal - Descuento) + IVA
     * Cambio = Monto Recibido - Total
     * </pre>
     * 
     * <p><strong>Métodos de pago soportados:</strong></p>
     * <ul>
     *   <li>EFECTIVO</li>
     *   <li>TARJETA</li>
     *   <li>TRANSFERENCIA</li>
     *   <li>Otros según configuración del sistema</li>
     * </ul>
     * 
     * @param venta Venta completada que origina la factura
     * @param metodoPago Método de pago utilizado (EFECTIVO, TARJETA, TRANSFERENCIA)
     * @param montoRecibido Cantidad de dinero recibida del cliente
     * @param cambio Vuelto o cambio entregado al cliente
     * @param subtotal Suma de productos antes de impuestos y descuentos
     * @param iva Monto del impuesto al valor agregado
     * @param descuento Monto del descuento aplicado
     * @return Factura generada y guardada con número de comprobante asignado
     * @throws IOException Si ocurre un error al guardar la factura
     * 
     * @see #obtenerUltimoNumero()
     * @see Factura#setEstado(String)
     */
    public Factura generarFacturaDesdeVenta(Venta venta, String metodoPago, double montoRecibido, double cambio, 
                                           double subtotal, double iva, double descuento) throws IOException {
       
        // Obtener y generar número de comprobante secuencial
        int ultimoNumero = repositorio.obtenerUltimoNumero();
        String numeroComprobante = String.format("COMP-%03d", ultimoNumero + 1);
        
        // Crear factura
        Factura factura = new Factura(numeroComprobante, venta);
        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setDescuento(descuento);
        factura.setTotal(venta.getTotal());
        factura.setMetodoPago(metodoPago);
        factura.setMontoRecibido(montoRecibido);
        factura.setCambio(cambio);
        factura.setEstado("PAGADA");
        
        repositorio.guardar(factura);
        
        return factura;
    }
    
    /**
     * Crea y guarda una factura en el sistema.
     * 
     * <p>Método genérico para crear facturas. A diferencia de 
     * {@link #generarFacturaDesdeVenta}, este método requiere que 
     * la factura ya esté completamente construida.</p>
     * 
     * <p><strong>Nota:</strong> Se recomienda usar 
     * {@link #generarFacturaDesdeVenta} para el flujo normal de facturación.</p>
     * 
     * @param factura Objeto Factura completamente configurado
     * @throws IOException Si ocurre un error al persistir la factura
     * @throws IllegalArgumentException Si la factura no pasa las validaciones
     * 
     * @see #validarFactura(Factura)
     */
    public void crearFactura(Factura factura) throws IOException {
        validarFactura(factura);
        repositorio.guardar(factura);
    }
    
    /**
     * Busca una factura por su número de comprobante.
     * 
     * <p>El número de comprobante es el identificador único de cada factura 
     * y sigue el formato: COMP-XXX (ej: COMP-001, COMP-042).</p>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Consulta de factura por cliente</li>
     *   <li>Reimpresión de facturas</li>
     *   <li>Auditoría y verificación</li>
     *   <li>Anulación de facturas</li>
     * </ul>
     * 
     * @param numeroComprobante Número de comprobante único (ej: "COMP-001")
     * @return Factura encontrada con todos sus datos
     * @throws Exception Si no existe una factura con ese número
     * 
     * @see FacturacionRepositorio#buscar(String)
     */
    public Factura buscarFactura(String numeroComprobante) throws Exception {
        return repositorio.buscar(numeroComprobante);
    }
    
    /**
     * Lista todas las facturas registradas en el sistema.
     * 
     * <p>Retorna la colección completa de facturas sin filtros. 
     * Para sistemas con muchas facturas, considere implementar paginación.</p>
     * 
     * @return Lista con todas las facturas registradas. 
     *         Retorna lista vacía si no hay facturas.
     * 
     * @see FacturacionRepositorio#listarTodos()
     */
    public List<Factura> listarFacturas() {
        return repositorio.listarTodos();
    }
    
    /**
     * Busca facturas por nombre del cliente.
     * 
     * <p>Permite encontrar todas las facturas emitidas a un cliente específico. 
     * Útil para:</p>
     * <ul>
     *   <li>Historial de compras del cliente</li>
     *   <li>Estado de cuenta del cliente</li>
     *   <li>Análisis de comportamiento de compra</li>
     *   <li>Soporte al cliente</li>
     * </ul>
     * 
     * <p><strong>Nota:</strong> La búsqueda se realiza por nombre del cliente 
     * asociado a la venta de la factura.</p>
     * 
     * @param nombreCliente Nombre del cliente a buscar
     * @return Lista de facturas del cliente. Lista vacía si no tiene facturas.
     * 
     * @see FacturacionRepositorio#buscarPorCliente(String)
     */
    public List<Factura> buscarPorCliente(String nombreCliente) {
        return repositorio.buscarPorCliente(nombreCliente);
    }
    
    /**
     * Busca facturas por estado.
     * 
     * <p>Filtra facturas según su estado actual. Estados válidos:</p>
     * <ul>
     *   <li><strong>PAGADA:</strong> Facturas completadas</li>
     *   <li><strong>PENDIENTE:</strong> Facturas por pagar</li>
     *   <li><strong>ANULADA:</strong> Facturas canceladas</li>
     * </ul>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Listar facturas pendientes de pago</li>
     *   <li>Auditoría de facturas anuladas</li>
     *   <li>Reporte de cobros completados</li>
     * </ul>
     * 
     * @param estado Estado a filtrar ("PAGADA", "PENDIENTE", "ANULADA")
     * @return Lista de facturas con el estado especificado
     * 
     * @see Factura#getEstado()
     * @see FacturacionRepositorio#buscarPorEstado(String)
     */
    public List<Factura> buscarPorEstado(String estado) {
        return repositorio.buscarPorEstado(estado);
    }
    
    /**
     * Anula una factura existente.
     * 
     * <p>Cambia el estado de una factura a "ANULADA". Esta es una operación 
     * crítica que debe realizarse con cuidado y solo cuando es necesario.</p>
     * 
     * <p><strong>Validaciones:</strong></p>
     * <ul>
     *   <li>La factura debe existir</li>
     *   <li>La factura no puede estar ya anulada</li>
     *   <li>Se debe verificar si ya existe</li>
     * </ul>
     * 
     * <p><strong>Razones comunes para anular:</strong></p>
     * <ul>
     *   <li>Error en datos de la factura</li>
     *   <li>Devolución completa de productos</li>
     *   <li>Cancelación de venta</li>
     *   <li>Duplicación accidental</li>
     * </ul>
     * 
     * <p><strong>Importante:</strong></p>
     * <ul>
     *   <li>La factura anulada mantiene su número para auditoría</li>
     *   <li>No se puede "des-anular" una factura</li>
     *   <li>El número no se reutiliza para mantener secuencia</li>
     *   <li>Considere registrar motivo de anulación en logs</li>
     * </ul>
     * 
     * <p><strong>Mejora sugerida:</strong> Agregar parámetro "motivo" para 
     * registrar la razón de anulación con fines de auditoría.</p>
     * 
     * @param numeroComprobante Número de comprobante de la factura a anular
     * @throws Exception Si la factura no existe
     * @throws Exception Si la factura ya está anulada
     * @throws IOException Si ocurre un error al actualizar el estado
     * 
     * @see #buscarFactura(String)
     * @see FacturacionRepositorio#actualizarEstado(String, String)
     */
    public void anularFactura(String numeroComprobante) throws Exception, IOException {
        Factura factura = repositorio.buscar(numeroComprobante);
        
        if ("ANULADA".equals(factura.getEstado())) {
            throw new Exception("La factura ya está anulada");
        }
        
        repositorio.actualizarEstado(numeroComprobante, "ANULADA");
    }
    
    /**
     * Calcula el total acumulado de todas las facturas pagadas.
     * 
     * <p>Suma los montos de todas las facturas con estado "PAGADA". 
     * Las facturas anuladas o pendientes NO se incluyen en el cálculo.</p>
     * 
     * <p><strong>Diferencia con ventas totales:</strong></p>
     * <ul>
     *   <li><strong>Total facturado:</strong> Solo facturas con estado PAGADA</li>
     *   <li><strong>Total ventas:</strong> Todas las ventas registradas</li>
     * </ul>
     * 
     * <p><strong>Casos de uso:</strong></p>
     * <ul>
     *   <li>Reportes financieros oficiales</li>
     *   <li>Declaraciones fiscales</li>
     *   <li>Conciliación contable</li>
     *   <li>KPIs de ingresos efectivos</li>
     * </ul>
     * 
     * @return Total de facturas pagadas en moneda del sistema
     * 
     * @see Factura#isPagada()
     * @see Factura#getTotal()
     */
    public double calcularTotalFacturado() {
        return repositorio.listarTodos().stream()
                .filter(Factura::isPagada)
                .mapToDouble(Factura::getTotal)
                .sum();
    }
    
    /**
     * Obtiene el último número de comprobante utilizado.
     * 
     * <p>Retorna el número secuencial de la última factura generada. 
     * Se usa para generar el siguiente número en la secuencia.</p>
     * 
     * <p><strong>Ejemplo:</strong></p>
     * <pre>{@code
     * int ultimo = servicio.obtenerUltimoNumero(); // Retorna: 42
     * String siguiente = String.format("COMP-%03d", ultimo + 1); // "COMP-043"
     * }</pre>
     * 
     * @return Número de la última factura generada (0 si no hay facturas)
     * 
     * @see #generarFacturaDesdeVenta
     * @see FacturacionRepositorio#obtenerUltimoNumero()
     */
    public int obtenerUltimoNumero() {
        return repositorio.obtenerUltimoNumero();
    }
    
    /**
     * Valida que una factura cumpla con los requisitos mínimos.
     * 
     * <p>Verifica la integridad y consistencia de los datos de la factura 
     * antes de persistirla en el sistema.</p>
     * 
     * <p><strong>Validaciones realizadas:</strong></p>
     * <ul>
     *   <li>Venta asociada no puede ser null</li>
     *   <li>Número de comprobante obligatorio y no vacío</li>
     *   <li>Total debe ser mayor a 0</li>
     * </ul>
     * 
     * <p><strong>Validaciones adicionales recomendadas:</strong></p>
     * <ul>
     *   <li>Verificar que subtotal + IVA - descuento = total</li>
     *   <li>Validar que método de pago sea válido</li>
     *   <li>Verificar que cambio = monto recibido - total</li>
     *   <li>Validar que IVA sea porcentaje correcto del subtotal</li>
     *   <li>Verificar que descuento no sea mayor al subtotal</li>
     * </ul>
     * 
     * @param factura Factura a validar
     * @throws IllegalArgumentException Si alguna validación falla, 
     *                                  con mensaje descriptivo del error
     */
    private void validarFactura(Factura factura) {
        if (factura.getVenta() == null) {
            throw new IllegalArgumentException("La factura debe tener una venta asociada");
        }
        if (factura.getNumeroComprobante() == null || factura.getNumeroComprobante().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de comprobante es obligatorio");
        }
        if (factura.getTotal() <= 0) {
            throw new IllegalArgumentException("El total debe ser mayor a 0");
        }
    }
}