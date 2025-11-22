/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.stockflow.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa un insight o notificacion inteligente generada por IA.
 * 
 * <p>Los insights son observaciones, alertas u oportunidades identificadas
 * automaticamente mediante analisis de datos e inteligencia artificial.
 * Cada insight tiene un nivel de prioridad, tipo y accion recomendada.</p>
 * 
 * <p>Tipos de insights disponibles:</p>
 * <ul>
 *   <li><strong>ALERT:</strong> Alerta sobre situacion que requiere atencion</li>
 *   <li><strong>OPPORTUNITY:</strong> Oportunidad de negocio identificada</li>
 *   <li><strong>WARNING:</strong> Advertencia sobre riesgo potencial</li>
 *   <li><strong>RECOMMENDATION:</strong> Recomendacion de mejora</li>
 *   <li><strong>INSIGHT:</strong> Observacion o patron detectado</li>
 * </ul>
 * 
 * <p>Niveles de prioridad:</p>
 * <ul>
 *   <li><strong>CRITICAL:</strong> Requiere accion inmediata</li>
 *   <li><strong>HIGH:</strong> Prioridad alta, atencion pronto</li>
 *   <li><strong>MEDIUM:</strong> Prioridad media, considerar</li>
 *   <li><strong>LOW:</strong> Prioridad baja, informativo</li>
 * </ul>
 * 
 * @author StockFlow Team
 * @version 1.0
 * @since 1.0
 */
public class InsightIA extends Entidad {
    
    /** Tipo de insight */
    private TipoInsight tipo;
    
    /** Nivel de prioridad */
    private NivelPrioridad prioridad;
    
    /** Titulo breve del insight */
    private String titulo;
    
    /** Descripcion detallada */
    private String descripcion;
    
    /** Accion recomendada */
    private String accionRecomendada;
    
    /** Producto relacionado (opcional) */
    private String productoId;
    
    /** Cliente relacionado (opcional) */
    private String clienteId;
    
    /** Metricas asociadas al insight */
    private Map<String, Object> metricas;
    
    /** Fecha de generacion */
    private LocalDateTime fechaGeneracion;
    
    /** Fecha de vencimiento o caducidad */
    private LocalDateTime fechaVencimiento;
    
    /** Indica si el insight ha sido atendido */
    private boolean atendido;
    
    /** Impacto estimado (BAJO, MEDIO, ALTO) */
    private String impacto;
    
    /** Fuente del insight (IA, REGLA, ANALISIS) */
    private String fuente;
    
    /**
     * Enumeracion de tipos de insight.
     */
    public enum TipoInsight {
        ALERT,
        OPPORTUNITY,
        WARNING,
        RECOMMENDATION,
        INSIGHT,
        SPIKE,
        ANOMALY
    }
    
    /**
     * Enumeracion de niveles de prioridad.
     */
    public enum NivelPrioridad {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW
    }
    
    /**
     * Constructor por defecto.
     */
    public InsightIA() {
        super();
        this.metricas = new HashMap<>();
        this.fechaGeneracion = LocalDateTime.now();
        this.atendido = false;
        this.fuente = "IA";
    }
    
    /**
     * Constructor completo para crear un insight.
     * 
     * @param tipo tipo de insight
     * @param prioridad nivel de prioridad
     * @param titulo titulo breve
     * @param descripcion descripcion detallada
     */
    public InsightIA(TipoInsight tipo, NivelPrioridad prioridad, String titulo, String descripcion) {
        super();
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.metricas = new HashMap<>();
        this.fechaGeneracion = LocalDateTime.now();
        this.atendido = false;
        this.fuente = "IA";
    }
    
    /**
     * Constructor simplificado con tipo y prioridad en String.
     * 
     * @param tipoStr tipo de insight como String
     * @param prioridadStr prioridad como String
     * @param titulo titulo breve
     * @param descripcion descripcion detallada
     */
    public InsightIA(String tipoStr, String prioridadStr, String titulo, String descripcion) {
        this(
            parseTipo(tipoStr),
            parsePrioridad(prioridadStr),
            titulo,
            descripcion
        );
    }
    
    /**
     * Parsea un String a TipoInsight.
     * 
     * @param tipo tipo como String
     * @return TipoInsight correspondiente
     */
    private static TipoInsight parseTipo(String tipo) {
        try {
            return TipoInsight.valueOf(tipo.toUpperCase());
        } catch (Exception e) {
            return TipoInsight.INSIGHT;
        }
    }
    
    /**
     * Parsea un String a NivelPrioridad.
     * 
     * @param prioridad prioridad como String
     * @return NivelPrioridad correspondiente
     */
    private static NivelPrioridad parsePrioridad(String prioridad) {
        try {
            return NivelPrioridad.valueOf(prioridad.toUpperCase());
        } catch (Exception e) {
            return NivelPrioridad.MEDIUM;
        }
    }
    
    /**
     * Obtiene el tipo de insight.
     * 
     * @return tipo de insight
     */
    public TipoInsight getTipo() {
        return tipo;
    }
    
    /**
     * Establece el tipo de insight.
     * 
     * @param tipo nuevo tipo
     */
    public void setTipo(TipoInsight tipo) {
        this.tipo = tipo;
    }
    
    /**
     * Obtiene el tipo como String.
     * 
     * @return tipo en formato String
     */
    public String getTipoString() {
        return tipo != null ? tipo.name() : "INSIGHT";
    }
    
    /**
     * Obtiene la prioridad.
     * 
     * @return nivel de prioridad
     */
    public NivelPrioridad getPrioridad() {
        return prioridad;
    }
    
    /**
     * Establece la prioridad.
     * 
     * @param prioridad nueva prioridad
     */
    public void setPrioridad(NivelPrioridad prioridad) {
        this.prioridad = prioridad;
    }
    
    /**
     * Obtiene la prioridad como String.
     * 
     * @return prioridad en formato String
     */
    public String getPrioridadString() {
        return prioridad != null ? prioridad.name() : "MEDIUM";
    }
    
    /**
     * Obtiene el titulo.
     * 
     * @return titulo del insight
     */
    public String getTitulo() {
        return titulo;
    }
    
    /**
     * Establece el titulo.
     * 
     * @param titulo nuevo titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    /**
     * Obtiene la descripcion.
     * 
     * @return descripcion detallada
     */
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Establece la descripcion.
     * 
     * @param descripcion nueva descripcion
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    /**
     * Obtiene la accion recomendada.
     * 
     * @return accion recomendada
     */
    public String getAccionRecomendada() {
        return accionRecomendada;
    }
    
    /**
     * Establece la accion recomendada.
     * 
     * @param accionRecomendada nueva accion
     */
    public void setAccionRecomendada(String accionRecomendada) {
        this.accionRecomendada = accionRecomendada;
    }
    
    /**
     * Obtiene el ID del producto relacionado.
     * 
     * @return ID del producto
     */
    public String getProductoId() {
        return productoId;
    }
    
    /**
     * Establece el ID del producto relacionado.
     * 
     * @param productoId nuevo ID
     */
    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }
    
    /**
     * Obtiene el ID del cliente relacionado.
     * 
     * @return ID del cliente
     */
    public String getClienteId() {
        return clienteId;
    }
    
    /**
     * Establece el ID del cliente relacionado.
     * 
     * @param clienteId nuevo ID
     */
    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }
    
    /**
     * Obtiene las metricas asociadas.
     * 
     * @return mapa de metricas
     */
    public Map<String, Object> getMetricas() {
        return metricas;
    }
    
    /**
     * Establece las metricas.
     * 
     * @param metricas nuevo mapa de metricas
     */
    public void setMetricas(Map<String, Object> metricas) {
        this.metricas = metricas;
    }
    
    /**
     * Agrega una metrica al insight.
     * 
     * @param clave nombre de la metrica
     * @param valor valor de la metrica
     */
    public void agregarMetrica(String clave, Object valor) {
        this.metricas.put(clave, valor);
    }
    
    /**
     * Obtiene la fecha de generacion.
     * 
     * @return fecha de generacion
     */
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }
    
    /**
     * Establece la fecha de generacion.
     * 
     * @param fechaGeneracion nueva fecha
     */
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
    
    /**
     * Obtiene la fecha de vencimiento.
     * 
     * @return fecha de vencimiento
     */
    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    /**
     * Establece la fecha de vencimiento.
     * 
     * @param fechaVencimiento nueva fecha
     */
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    /**
     * Verifica si el insight ha sido atendido.
     * 
     * @return true si fue atendido
     */
    public boolean isAtendido() {
        return atendido;
    }
    
    /**
     * Establece el estado de atencion.
     * 
     * @param atendido nuevo estado
     */
    public void setAtendido(boolean atendido) {
        this.atendido = atendido;
    }
    
    /**
     * Obtiene el impacto estimado.
     * 
     * @return impacto (BAJO, MEDIO, ALTO)
     */
    public String getImpacto() {
        return impacto;
    }
    
    /**
     * Establece el impacto estimado.
     * 
     * @param impacto nuevo impacto
     */
    public void setImpacto(String impacto) {
        this.impacto = impacto;
    }
    
    /**
     * Obtiene la fuente del insight.
     * 
     * @return fuente (IA, REGLA, ANALISIS)
     */
    public String getFuente() {
        return fuente;
    }
    
    /**
     * Establece la fuente del insight.
     * 
     * @param fuente nueva fuente
     */
    public void setFuente(String fuente) {
        this.fuente = fuente;
    }
    
    /**
     * Verifica si el insight ha vencido.
     * 
     * @return true si ha vencido
     */
    public boolean haVencido() {
        if (fechaVencimiento == null) return false;
        return LocalDateTime.now().isAfter(fechaVencimiento);
    }
    
    /**
     * Obtiene la fecha formateada.
     * 
     * @return fecha formateada
     */
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaGeneracion.format(formatter);
    }
    
    /**
     * Obtiene el color asociado a la prioridad.
     * 
     * @return codigo de color hexadecimal
     */
    public String getColorPrioridad() {
        if (prioridad == null) return "#3b82f6";
        switch (prioridad) {
            case CRITICAL: return "#ef4444";
            case HIGH: return "#f59e0b";
            case MEDIUM: return "#3b82f6";
            case LOW: return "#10b981";
            default: return "#3b82f6";
        }
    }
    
    /**
     * Calcula la puntuacion de importancia del insight.
     * Combina prioridad, tipo e impacto.
     * 
     * @return puntuacion de 0 a 100
     */
    public int getPuntuacionImportancia() {
        int puntos = 0;
        
        if (prioridad != null) {
            switch (prioridad) {
                case CRITICAL: puntos += 40; break;
                case HIGH: puntos += 30; break;
                case MEDIUM: puntos += 20; break;
                case LOW: puntos += 10; break;
            }
        }
        
        if (tipo != null && (tipo == TipoInsight.ALERT || tipo == TipoInsight.ANOMALY)) {
            puntos += 20;
        }
        
        if ("ALTO".equals(impacto)) {
            puntos += 30;
        } else if ("MEDIO".equals(impacto)) {
            puntos += 15;
        }
        
        if (atendido) {
            puntos -= 20;
        }
        
        return Math.max(0, Math.min(100, puntos));
    }
    
    @Override
    public String toString() {
        return String.format(
            "%s [%s]: %s",
            tipo,
            prioridad,
            titulo
        );
    }
}