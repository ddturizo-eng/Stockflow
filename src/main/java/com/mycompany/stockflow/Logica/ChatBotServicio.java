/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.*;
import com.mycompany.stockflow.excepciones.*;
import com.mycompany.stockflow.utils.DeepSeekAPIClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de ChatBot conversacional con capacidades de análisis de negocio.
 * 
 * <p>Este servicio proporciona una interfaz conversacional para que los usuarios
 * interactúen con el sistema mediante lenguaje natural. Combina análisis de 
 * patrones de lenguaje con acceso a datos reales del negocio para generar
 * respuestas contextualizadas y útiles.</p>
 * 
 * <p>Capacidades principales:</p>
 * <ul>
 *   <li>Análisis de mejores clientes por período</li>
 *   <li>Tendencias de ventas y cambios porcentuales</li>
 *   <li>Identificación de productos más vendidos</li>
 *   <li>Alertas de stock crítico</li>
 *   <li>Resúmenes generales del negocio</li>
 *   <li>Análisis de márgenes y rentabilidad</li>
 *   <li>Detección de clientes inactivos</li>
 *   <li>Consultas personalizadas mediante IA</li>
 * </ul>
 * 
 * <p>El servicio mantiene un historial de conversación para proporcionar
 * contexto en consultas relacionadas.</p>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 * ChatBotServicio chatBot = new ChatBotServicio();
 * 
 * ChatMensaje respuesta = chatBot.procesarPregunta(
 *     "¿Quién fue mi mejor cliente este mes?"
 * );
 * 
 * System.out.println(respuesta.getContenido());
 * 
 * List obtenerHistorial = chatBot.getHistorialConversacion();
 * </pre>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 * @see ChatMensaje
 * @see EstadisticasServicio
 * @see DeepSeekAPIClient
 */
public class ChatBotServicio {
    
    /** Servicio de estadísticas para cálculos de métricas básicas */
    private final EstadisticasServicio estadisticasServicio;
    
    /** Servicio de analítica avanzada para cálculos complejos */
    private final AnaliticaAvanzadaServicio analiticaServicio;
    
    /** Servicio de inteligencia de negocio para análisis con IA */
    private final InteligenciaNegocioServicio inteligenciaServicio;
    
    /** Cliente API para consultas a DeepSeek cuando sea necesario */
    private final DeepSeekAPIClient deepSeekClient;
    
    /** Historial de mensajes de la conversación actual */
    private final List<ChatMensaje> historialConversacion;
    
    /** Servicio de ventas para acceso a datos de transacciones */
    private final VentaServicio ventaServicio;
    
    /** Servicio de productos para acceso a inventario */
    private final ProductoServicio productoServicio;
    
    /** Servicio de clientes para acceso a información de clientes */
    private final ClienteServicio clienteServicio;
    
    /**
     * Constructor por defecto que inicializa todos los servicios necesarios.
     * 
     * <p>El historial de conversación se inicializa con un mensaje de bienvenida
     * del asistente.</p>
     */
    public ChatBotServicio() {
        this.estadisticasServicio = new EstadisticasServicio();
        this.analiticaServicio = new AnaliticaAvanzadaServicio();
        this.inteligenciaServicio = new InteligenciaNegocioServicio();
        this.deepSeekClient = new DeepSeekAPIClient();
        this.historialConversacion = new ArrayList<>();
        this.ventaServicio = new VentaServicio();
        this.productoServicio = new ProductoServicio();
        this.clienteServicio = new ClienteServicio();
        
        // Mensaje de bienvenida inicial
        historialConversacion.add(new ChatMensaje(
            "assistant",
            "Hola, soy tu asistente inteligente de StockFlow. " +
            "Puedo ayudarte a analizar ventas, inventario, clientes y más. " +
            "¿Qué te gustaría saber?"
        ));
    }
    
    /**
     * Procesa una pregunta del usuario y genera una respuesta contextualizada.
     * 
     * <p>El método analiza la pregunta para identificar la intención del usuario
     * y determina qué tipo de análisis realizar. Si la pregunta no coincide con
     * ningún patrón conocido, se envía a DeepSeek para procesamiento con IA.</p>
     * 
     * <p>Los mensajes (tanto pregunta como respuesta) se agregan automáticamente
     * al historial de conversación.</p>
     * 
     * @param pregunta la consulta del usuario en lenguaje natural
     * @return el mensaje de respuesta del asistente
     */
    public ChatMensaje procesarPregunta(String pregunta) {
        // Agregar mensaje del usuario al historial
        ChatMensaje mensajeUsuario = new ChatMensaje("user", pregunta);
        historialConversacion.add(mensajeUsuario);
        
        try {
            String respuesta = analizarYResponder(pregunta);
            ChatMensaje mensajeAsistente = new ChatMensaje("assistant", respuesta);
            historialConversacion.add(mensajeAsistente);
            return mensajeAsistente;
            
        } catch (Exception e) {
            ChatMensaje mensajeError = new ChatMensaje(
                "assistant",
                "Ocurrió un error al procesar tu consulta: " + e.getMessage() + 
                "\n\nPor favor, intenta reformular tu pregunta o verifica que los datos estén disponibles.",
                true
            );
            historialConversacion.add(mensajeError);
            return mensajeError;
        }
    }
    
    /**
     * Analiza la pregunta e identifica qué tipo de respuesta generar.
     * 
     * <p>Utiliza detección de patrones de lenguaje para identificar la intención
     * del usuario y ejecuta el análisis correspondiente. Los patrones soportados
     * incluyen consultas sobre:</p>
     * <ul>
     *   <li>Mejores clientes</li>
     *   <li>Tendencias de ventas</li>
     *   <li>Productos más vendidos</li>
     *   <li>Stock crítico</li>
     *   <li>Resumen general</li>
     *   <li>Márgenes de ganancia</li>
     *   <li>Clientes inactivos</li>
     * </ul>
     * 
     * @param pregunta la consulta del usuario
     * @return la respuesta generada como texto
     * @throws Exception si ocurre un error durante el análisis
     */
    private String analizarYResponder(String pregunta) throws Exception {
        String preguntaLower = pregunta.toLowerCase();
        
        // 1. MEJOR CLIENTE
        if (contienePatron(preguntaLower, "mejor cliente", "top cliente", "cliente más")) {
            return responderMejorCliente(preguntaLower);
        }
        
        // 2. TENDENCIA DE VENTAS
        if (contienePatron(preguntaLower, "ventas", "tendencia", "cómo van") &&
            contienePatron(preguntaLower, "semana", "mes", "día")) {
            return responderTendenciaVentas(preguntaLower);
        }
        
        // 3. PRODUCTOS MÁS VENDIDOS
        if (contienePatron(preguntaLower, "producto", "más vendido", "top", "popular")) {
            return responderProductosMasVendidos();
        }
        
        // 4. STOCK CRÍTICO
        if (contienePatron(preguntaLower, "stock", "inventario") &&
            contienePatron(preguntaLower, "bajo", "crítico", "alerta", "poco")) {
            return responderStockCritico();
        }
        
        // 5. RESUMEN GENERAL
        if (contienePatron(preguntaLower, "resumen", "general", "panorama", "dashboard", "estado")) {
            return responderResumenGeneral();
        }
        
        // 6. MARGEN DE GANANCIA
        if (contienePatron(preguntaLower, "margen", "ganancia", "rentabilidad", "utilidad")) {
            return responderMargen();
        }
        
        // 7. CLIENTES INACTIVOS
        if (contienePatron(preguntaLower, "cliente", "inactivo", "sin comprar", "perdido")) {
            return responderClientesInactivos();
        }
        
        // 8. Si no se reconoce, usar IA con contexto
        return responderConIA(pregunta);
    }
    
    /**
     * Genera respuesta sobre el mejor cliente del período solicitado.
     * 
     * <p>Analiza la pregunta para identificar el período específico (mes actual,
     * octubre, mes pasado, etc.) y calcula cuál cliente generó más ingresos.</p>
     * 
     * @param pregunta la pregunta del usuario
     * @return respuesta formateada con información del mejor cliente
     * @throws Exception si hay error al acceder a los datos
     */
    private String responderMejorCliente(String pregunta) throws Exception {
        LocalDate fechaInicio, fechaFin;
        String nombrePeriodo;
        
        // Detectar el período solicitado
        if (pregunta.contains("octubre")) {
            fechaInicio = LocalDate.now().withMonth(10).withDayOfMonth(1);
            fechaFin = fechaInicio.plusMonths(1).minusDays(1);
            nombrePeriodo = "en octubre";
        } else if (pregunta.contains("mes pasado")) {
            fechaInicio = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            fechaFin = fechaInicio.plusMonths(1).minusDays(1);
            nombrePeriodo = "el mes pasado";
        } else {
            // Mes actual por defecto
            fechaInicio = LocalDate.now().withDayOfMonth(1);
            fechaFin = LocalDate.now();
            nombrePeriodo = "este mes";
        }
        
        // Obtener mejor cliente del período
        Map<String, Double> ventasPorCliente = new HashMap<>();
        List<Venta> ventas = ventaServicio.listarVentas();
        
        for (Venta venta : ventas) {
            LocalDate fechaVenta = venta.getFecha().toLocalDate();
            if (!fechaVenta.isBefore(fechaInicio) && !fechaVenta.isAfter(fechaFin)) {
                if (venta.getCliente() != null) {
                    String nombreCliente = venta.getCliente().getNombre();
                    ventasPorCliente.merge(nombreCliente, venta.getTotal(), Double::sum);
                }
            }
        }
        
        if (ventasPorCliente.isEmpty()) {
            return "No se encontraron ventas para el período " + nombrePeriodo + ".";
        }
        
        Map.Entry<String, Double> mejorEntry = ventasPorCliente.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);
        
        if (mejorEntry == null) {
            return "No se pudo determinar el mejor cliente para " + nombrePeriodo + ".";
        }
        
        // Contar número de compras
        long numeroCompras = ventas.stream()
            .filter(v -> !v.getFecha().toLocalDate().isBefore(fechaInicio) && 
                        !v.getFecha().toLocalDate().isAfter(fechaFin))
            .filter(v -> v.getCliente() != null && 
                        v.getCliente().getNombre().equals(mejorEntry.getKey()))
            .count();
        
        return String.format(
            "MEJOR CLIENTE %s\n\n" +
            "Cliente: %s\n" +
            "Total de compras: $%,.2f\n" +
            "Numero de transacciones: %d\n" +
            "Promedio por compra: $%,.2f\n\n" +
            "Excelente relacion comercial. Considera programas de fidelizacion " +
            "para mantener a este cliente valioso.",
            nombrePeriodo.toUpperCase(),
            mejorEntry.getKey(),
            mejorEntry.getValue(),
            numeroCompras,
            mejorEntry.getValue() / numeroCompras
        );
    }
    
    /**
     * Genera respuesta sobre tendencias de ventas en un período específico.
     * 
     * <p>Calcula el total de ventas del período, número de transacciones y
     * compara con el promedio histórico para identificar tendencias de
     * crecimiento o decrecimiento.</p>
     * 
     * @param pregunta la pregunta del usuario
     * @return respuesta con análisis de tendencia
     * @throws Exception si hay error al procesar
     */
    private String responderTendenciaVentas(String pregunta) throws Exception {
        int diasAnalisis = 7; // Por defecto una semana
        String nombrePeriodo = "esta semana";
        
        if (pregunta.contains("mes")) {
            diasAnalisis = 30;
            nombrePeriodo = "este mes";
        } else if (pregunta.contains("día") || pregunta.contains("hoy")) {
            diasAnalisis = 1;
            nombrePeriodo = "hoy";
        }
        
        LocalDateTime fechaFin = LocalDateTime.now();
        LocalDateTime fechaInicio = fechaFin.minusDays(diasAnalisis);
        
        List<Venta> todasVentas = ventaServicio.listarVentas();
        List<Venta> ventasPeriodo = new ArrayList<>();
        
        for (Venta venta : todasVentas) {
            if (!venta.getFecha().isBefore(fechaInicio) && !venta.getFecha().isAfter(fechaFin)) {
                ventasPeriodo.add(venta);
            }
        }
        
        double totalVentas = ventasPeriodo.stream()
            .mapToDouble(Venta::getTotal)
            .sum();
        
        int cantidadVentas = ventasPeriodo.size();
        
        // Calcular promedio histórico
        double promedioHistorico = todasVentas.stream()
            .mapToDouble(Venta::getTotal)
            .average()
            .orElse(0.0);
        
        double promedioActual = cantidadVentas > 0 ? totalVentas / cantidadVentas : 0.0;
        
        double cambio = promedioHistorico > 0 
            ? ((promedioActual - promedioHistorico) / promedioHistorico) * 100 
            : 0.0;
        
        String tendencia = cambio > 5 ? "CRECIMIENTO" : 
                          cambio < -5 ? "DECRECIMIENTO" : "ESTABLE";
        
        String simbolo = cambio > 0 ? "+" : "";
        
        StringBuilder respuesta = new StringBuilder();
        respuesta.append(String.format("ANALISIS DE VENTAS - %s\n\n", nombrePeriodo.toUpperCase()));
        respuesta.append(String.format("Total vendido: $%,.2f\n", totalVentas));
        respuesta.append(String.format("Numero de ventas: %d\n", cantidadVentas));
        respuesta.append(String.format("Promedio por venta: $%,.2f\n", promedioActual));
        respuesta.append(String.format("Cambio vs historico: %s%.1f%%\n", simbolo, cambio));
        respuesta.append(String.format("Tendencia: %s\n\n", tendencia));
        
        // Agregar recomendaciones según tendencia
        if (cambio < -10) {
            respuesta.append("RECOMENDACION: Las ventas han bajado significativamente. ");
            respuesta.append("Considera implementar promociones, revisar estrategia de ");
            respuesta.append("marketing o analizar si hay factores externos afectando el negocio.");
        } else if (cambio > 20) {
            respuesta.append("EXCELENTE: Las ventas estan en fuerte crecimiento. ");
            respuesta.append("Asegurate de mantener suficiente inventario de productos ");
            respuesta.append("populares para capitalizar esta tendencia positiva.");
        } else if (Math.abs(cambio) < 5) {
            respuesta.append("Las ventas se mantienen estables. Considera estrategias ");
            respuesta.append("para impulsar el crecimiento como nuevas promociones o ");
            respuesta.append("expansion de catalogo.");
        }
        
        return respuesta.toString();
    }
    
    /**
     * Genera respuesta con los productos más vendidos.
     * 
     * <p>Lista los 5 productos con mayor volumen de ventas por cantidad
     * de unidades vendidas.</p>
     * 
     * @return respuesta con top productos
     * @throws Exception si hay error al procesar
     */
    private String responderProductosMasVendidos() throws Exception {
        List<Producto> topProductos = estadisticasServicio.obtenerProductosMasVendidos(5);
        
        if (topProductos.isEmpty()) {
            return "No hay suficientes datos de ventas para mostrar productos populares.";
        }
        
        // Calcular unidades vendidas por cada producto
        Map<String, Integer> unidadesVendidas = new HashMap<>();
        List<Venta> ventas = ventaServicio.listarVentas();
        
        for (Venta venta : ventas) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                String codigo = detalle.getProducto().getCodigo();
                unidadesVendidas.merge(codigo, detalle.getCantidad(), Integer::sum);
            }
        }
        
        StringBuilder respuesta = new StringBuilder("TOP 5 PRODUCTOS MAS VENDIDOS\n\n");
        
        for (int i = 0; i < topProductos.size(); i++) {
            Producto p = topProductos.get(i);
            int unidades = unidadesVendidas.getOrDefault(p.getCodigo(), 0);
            double ingresos = unidades * p.getPrecioVenta();
            
            respuesta.append(String.format("%d. %s\n", i + 1, p.getNombre()));
            respuesta.append(String.format("   - Unidades vendidas: %d\n", unidades));
            respuesta.append(String.format("   - Ingresos generados: $%,.2f\n", ingresos));
            respuesta.append(String.format("   - Stock actual: %d\n\n", p.getStock()));
        }
        
        respuesta.append("TIP: Asegurate de mantener buen stock de estos productos estrella.");
        
        return respuesta.toString();
    }
    
    /**
     * Genera respuesta sobre productos con stock crítico.
     * 
     * <p>Identifica productos cuyo stock está por debajo del mínimo configurado
     * y genera alertas de reabastecimiento.</p>
     * 
     * @return respuesta con productos en alerta
     * @throws Exception si hay error al procesar
     */
    private String responderStockCritico() throws Exception {
        List<Producto> productosCriticos = new ArrayList<>();
        List<Producto> todosProductos = productoServicio.listarProductos();
        
        for (Producto p : todosProductos) {
            if (p.tieneStockBajo()) {
                productosCriticos.add(p);
            }
        }
        
        if (productosCriticos.isEmpty()) {
            return "INVENTARIO OPTIMO\n\n" +
                   "Todos tus productos tienen stock suficiente. " +
                   "No hay alertas de inventario bajo en este momento.";
        }
        
        StringBuilder respuesta = new StringBuilder();
        respuesta.append(String.format("ALERTA DE STOCK CRITICO - %d producto(s)\n\n", 
            productosCriticos.size()));
        
        for (Producto p : productosCriticos) {
            String nivelAlerta;
            if (p.getStock() == 0) {
                nivelAlerta = "AGOTADO";
            } else if (p.getStock() <= p.getStockMinimo() / 2) {
                nivelAlerta = "URGENTE";
            } else {
                nivelAlerta = "BAJO";
            }
            
            respuesta.append(String.format("- %s [%s]\n", p.getNombre(), nivelAlerta));
            respuesta.append(String.format("  Stock actual: %d unidades\n", p.getStock()));
            respuesta.append(String.format("  Stock minimo: %d unidades\n", p.getStockMinimo()));
            respuesta.append(String.format("  Precio venta: $%,.2f\n\n", p.getPrecioVenta()));
        }
        
        respuesta.append("ACCION RECOMENDADA:\n");
        respuesta.append("Realizar pedido de reabastecimiento pronto para evitar ");
        respuesta.append("quiebres de stock y perdida de ventas.");
        
        return respuesta.toString();
    }
    
    /**
     * Genera un resumen ejecutivo completo del negocio.
     * 
     * <p>Incluye métricas clave de ventas, inventario, clientes y rentabilidad.</p>
     * 
     * @return respuesta con resumen general
     * @throws Exception si hay error al procesar
     */
    private String responderResumenGeneral() throws Exception {
        List<Venta> ventas = ventaServicio.listarVentas();
        List<Producto> productos = productoServicio.listarProductos();
        List<Cliente> clientes = clienteServicio.listarClientes();
        
        // Calcular métricas
        double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
        int numeroVentas = ventas.size();
        double ticketPromedio = numeroVentas > 0 ? totalVentas / numeroVentas : 0.0;
        
        int totalProductos = productos.size();
        long productosStockBajo = productos.stream().filter(Producto::tieneStockBajo).count();
        
        double valorInventario = productos.stream()
            .mapToDouble(p -> p.getPrecioVenta() * p.getStock())
            .sum();
        
        int clientesActivos = clientes.size();
        
        // Calcular margen promedio
        double totalCostos = ventas.stream().mapToDouble(Venta::getCostoTotal).sum();
        double gananciaBruta = totalVentas - totalCostos;
        double margenPromedio = totalCostos > 0 ? (gananciaBruta / totalCostos) * 100 : 0.0;
        
        // Mejor cliente
        Map<String, Double> ventasPorCliente = new HashMap<>();
        for (Venta v : ventas) {
            if (v.getCliente() != null) {
                ventasPorCliente.merge(v.getCliente().getNombre(), v.getTotal(), Double::sum);
            }
        }
        
        String mejorCliente = ventasPorCliente.isEmpty() ? "N/A" : 
            ventasPorCliente.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        
        return String.format(
            "RESUMEN GENERAL DEL NEGOCIO\n" +
            "=================================\n\n" +
            
            "VENTAS\n" +
            "  Total acumulado: $%,.2f\n" +
            "  Numero de ventas: %d\n" +
            "  Ticket promedio: $%,.2f\n\n" +
            
            "INVENTARIO\n" +
            "  Productos en catalogo: %d\n" +
            "  Valor del inventario: $%,.2f\n" +
            "  Productos con stock bajo: %d\n\n" +
            
            "CLIENTES\n" +
            "  Clientes registrados: %d\n" +
            "  Cliente top: %s\n\n" +
            
            "RENTABILIDAD\n" +
            "  Ganancia bruta: $%,.2f\n" +
            "  Margen promedio: %.1f%%\n\n" +
            
            "El negocio %s en estado %s.",
            totalVentas, numeroVentas, ticketPromedio,
            totalProductos, valorInventario, productosStockBajo,
            clientesActivos, mejorCliente,
            gananciaBruta, margenPromedio,
            margenPromedio > 30 ? "se encuentra" : "requiere atencion, esta",
            margenPromedio > 30 ? "optimo" : margenPromedio > 15 ? "aceptable" : "critico"
        );
    }
    
    /**
     * Genera respuesta sobre márgenes de ganancia del negocio.
     * 
     * <p>Analiza la rentabilidad general y proporciona recomendaciones
     * basadas en los márgenes calculados.</p>
     * 
     * @return respuesta con análisis de márgenes
     * @throws Exception si hay error al procesar
     */
    private String responderMargen() throws Exception {
        List<Venta> ventas = ventaServicio.listarVentas();
        
        double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
        double totalCostos = ventas.stream().mapToDouble(Venta::getCostoTotal).sum();
        double gananciaBruta = totalVentas - totalCostos;
        
        double margenPromedio = totalCostos > 0 ? (gananciaBruta / totalCostos) * 100 : 0.0;
        
        // Encontrar producto con mejor margen
        List<Producto> productos = productoServicio.listarProductos();
        Producto mejorMargen = productos.stream()
            .max(Comparator.comparingDouble(Producto::getMargenGanancia))
            .orElse(null);
        
        String calificacion;
        String recomendacion;
        
        if (margenPromedio >= 40) {
            calificacion = "EXCELENTE";
            recomendacion = "Tu rentabilidad es sobresaliente. Mantén este nivel de márgenes " +
                          "y considera expansion del negocio.";
        } else if (margenPromedio >= 25) {
            calificacion = "BUENO";
            recomendacion = "Margen saludable. Busca oportunidades para optimizar costos " +
                          "y mejorar precios estrategicamente.";
        } else if (margenPromedio >= 15) {
            calificacion = "ACEPTABLE";
            recomendacion = "Margen mejorable. IMPORTANTE: Revisa estructura de costos, " +
                          "negocia con proveedores y ajusta precios si el mercado lo permite.";
        } else {
            calificacion = "CRITICO";
            recomendacion = "URGENTE: Margen muy bajo. Debes revisar inmediatamente tu " +
                          "estructura de costos y precios. El negocio podría no ser sostenible.";
        }
        
        StringBuilder respuesta = new StringBuilder();
        respuesta.append("ANALISIS DE RENTABILIDAD\n\n");
        respuesta.append(String.format("Margen promedio: %.1f%% [%s]\n\n", margenPromedio, calificacion));
        respuesta.append(String.format("Total ventas: $%,.2f\n", totalVentas));
        respuesta.append(String.format("Total costos: $%,.2f\n", totalCostos));
        respuesta.append(String.format("Ganancia bruta: $%,.2f\n\n", gananciaBruta));
        
        if (mejorMargen != null) {
            respuesta.append(String.format("Producto mas rentable: %s\n", mejorMargen.getNombre()));
            respuesta.append(String.format("  Margen: %.1f%%\n\n", mejorMargen.getMargenGanancia()));
        }
        
        respuesta.append("RECOMENDACION:\n");
        respuesta.append(recomendacion);
        
        return respuesta.toString();
    }
    
    /**
     * Genera respuesta sobre clientes inactivos.
     * 
     * <p>Identifica clientes que no han realizado compras en los últimos 30 días.</p>
     * 
     * @return respuesta con clientes inactivos
     * @throws Exception si hay error al procesar
     */
    private String responderClientesInactivos() throws Exception {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        List<Cliente> todosClientes = clienteServicio.listarClientes();
        List<Venta> ventas = ventaServicio.listarVentas();
        
        Map<String, LocalDateTime> ultimaCompra = new HashMap<>();
        Map<String, Double> totalCompras = new HashMap<>();
        
        for (Venta venta : ventas) {
            if (venta.getCliente() != null) {
                String cedula = venta.getCliente().getCedula();
                
                if (!ultimaCompra.containsKey(cedula) || 
                    venta.getFecha().isAfter(ultimaCompra.get(cedula))) {
                    ultimaCompra.put(cedula, venta.getFecha());
                }
                
                totalCompras.merge(cedula, venta.getTotal(), Double::sum);
            }
        }
        
        List<Map<String, Object>> clientesInactivos = new ArrayList<>();
        
        for (Cliente cliente : todosClientes) {
            LocalDateTime ultCompra = ultimaCompra.get(cliente.getCedula());
            
            if (ultCompra == null || ultCompra.isBefore(hace30Dias)) {
                Map<String, Object> info = new HashMap<>();
                info.put("nombre", cliente.getNombre());
                info.put("ultimaCompra", ultCompra != null ? ultCompra : LocalDateTime.MIN);
                info.put("total", totalCompras.getOrDefault(cliente.getCedula(), 0.0));
                clientesInactivos.add(info);
            }
        }
        
        if (clientesInactivos.isEmpty()) {
            return "CLIENTES ACTIVOS\n\n" +
                   "Excelente: No tienes clientes inactivos en los ultimos 30 dias. " +
                   "Todos tus clientes han realizado compras recientemente.";
        }
        
        StringBuilder respuesta = new StringBuilder();
        respuesta.append(String.format("CLIENTES INACTIVOS - %d cliente(s) sin comprar en 30+ dias\n\n", 
            clientesInactivos.size()));
        
        int mostrar = Math.min(5, clientesInactivos.size());
        
        for (int i = 0; i < mostrar; i++) {
            Map<String, Object> cliente = clientesInactivos.get(i);
            LocalDateTime ultima = (LocalDateTime) cliente.get("ultimaCompra");
            String fechaTexto = ultima.equals(LocalDateTime.MIN) ? 
                "Nunca ha comprado" : 
                ultima.toLocalDate().toString();
            
            respuesta.append(String.format("- %s\n", cliente.get("nombre")));
            respuesta.append(String.format("  Ultima compra: %s\n", fechaTexto));
            respuesta.append(String.format("  Total historico: $%,.2f\n\n", 
                (Double) cliente.get("total")));
        }
        
        if (clientesInactivos.size() > 5) {
            respuesta.append(String.format("... y %d cliente(s) mas\n\n", 
                clientesInactivos.size() - 5));
        }
        
        respuesta.append("SUGERENCIA:\n");
        respuesta.append("Considera enviar promociones personalizadas o comunicarte directamente ");
        respuesta.append("con estos clientes para reactivar la relacion comercial.");
        
        return respuesta.toString();
    }
    
    /**
     * Usa DeepSeek AI para responder preguntas complejas o no reconocidas.
     * 
     * <p>Prepara un contexto estructurado con datos del negocio y envía
     * la consulta a la API de DeepSeek para obtener una respuesta
     * contextualizada mediante inteligencia artificial.</p>
     * 
     * @param pregunta la pregunta del usuario
     * @return respuesta generada por IA
     * @throws AIAPIException si hay error en la comunicación con la API
     * @throws ConfiguracionAIFaltanteException si la configuración de IA no está completa
     */
    private String responderConIA(String pregunta) 
            throws AIAPIException, ConfiguracionAIFaltanteException {
        
        // Preparar contexto con datos del negocio
        StringBuilder contexto = new StringBuilder();
        contexto.append("Eres un asistente experto en analisis de negocios. ");
        contexto.append("Tienes acceso a los siguientes datos del negocio:\n\n");
        
        try {
            List<Venta> ventas = ventaServicio.listarVentas();
            List<Producto> productos = productoServicio.listarProductos();
            List<Cliente> clientes = clienteServicio.listarClientes();
            
            contexto.append(String.format("Total de ventas registradas: %d\n", ventas.size()));
            contexto.append(String.format("Total de productos: %d\n", productos.size()));
            contexto.append(String.format("Total de clientes: %d\n\n", clientes.size()));
            
            double totalVentas = ventas.stream().mapToDouble(Venta::getTotal).sum();
            contexto.append(String.format("Ingresos totales: $%,.2f\n", totalVentas));
            
            long productosStockBajo = productos.stream()
                .filter(Producto::tieneStockBajo).count();
            contexto.append(String.format("Productos con stock bajo: %d\n\n", productosStockBajo));
            
        } catch (Exception e) {
            contexto.append("Error al cargar datos del negocio.\n\n");
        }
        
        contexto.append("INSTRUCCIONES:\n");
        contexto.append("- Proporciona respuestas concisas y profesionales\n");
        contexto.append("- Usa formato de texto plano, sin markdown\n");
        contexto.append("- Incluye insights accionables cuando sea relevante\n");
        contexto.append("- Si no tienes suficiente informacion, indicalo claramente\n\n");
        contexto.append("PREGUNTA DEL USUARIO:\n");
        contexto.append(pregunta);
        
        String respuestaIA = deepSeekClient.enviarPrompt(contexto.toString());
        
        return respuestaIA;
    }
    
    /**
     * Verifica si el texto contiene alguno de los patrones especificados.
     * 
     * @param texto el texto a analizar
     * @param palabras las palabras o frases a buscar
     * @return true si se encuentra al menos uno de los patrones
     */
    private boolean contienePatron(String texto, String... palabras) {
        for (String palabra : palabras) {
            if (texto.contains(palabra)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene una copia del historial de conversación.
     * 
     * @return lista de mensajes de la conversación
     */
    public List<ChatMensaje> getHistorialConversacion() {
        return new ArrayList<>(historialConversacion);
    }
    
    /**
     * Limpia el historial de conversación y reinicia con mensaje de bienvenida.
     */
    public void limpiarHistorial() {
        historialConversacion.clear();
        historialConversacion.add(new ChatMensaje(
            "assistant",
            "Historial limpiado. ¿En que mas puedo ayudarte?"
        ));
    }
    
    /**
     * Obtiene el número de mensajes en el historial.
     * 
     * @return cantidad de mensajes
     */
    public int getTamañoHistorial() {
        return historialConversacion.size();
    }
    
    /**
     * Verifica si hay mensajes en el historial además del mensaje de bienvenida.
     * 
     * @return true si hay conversación activa
     */
    public boolean tieneConversacion() {
        return historialConversacion.size() > 1;
    }
}