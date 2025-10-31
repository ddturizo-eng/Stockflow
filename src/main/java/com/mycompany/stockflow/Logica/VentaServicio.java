/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.stockflow.Logica;

import com.mycompany.stockflow.Modelo.Venta;
import com.mycompany.stockflow.Modelo.DetalleVenta;
import com.mycompany.stockflow.Modelo.Producto;
import com.mycompany.stockflow.Persistencia.VentaRepositorio;
import com.mycompany.stockflow.excepciones.*;
import java.io.IOException;
import java.util.List;

public class VentaServicio {
    
    private final VentaRepositorio repositorio;
    private final ProductoServicio productoServicio;
    
    public VentaServicio() {
        this.repositorio = new VentaRepositorio();
        this.productoServicio = new ProductoServicio();
    }
    
    
    public boolean guardarVenta(Venta venta, List<DetalleVenta> detalles) {
        try {
            System.out.println("=== VentaServicio.guardarVenta() INICIADO ===");
            
            if (venta == null) {
                System.err.println("ERROR: La venta es null");
                return false;
            }
            
            if (detalles == null || detalles.isEmpty()) {
                System.err.println("ERROR: Los detalles son null o están vacíos");
                throw new VentaInvalidaExcepcion("La venta debe tener al menos un producto");
            }
            
            System.out.println("Venta código: " + venta.getCodigo());
            System.out.println("Cliente: " + (venta.getCliente() != null ? venta.getCliente().getNombre() : "null"));
            System.out.println("Detalles: " + detalles.size());
            
            // Establecer los detalles en la venta (esto ya recalcula el total)
            venta.setDetalles(detalles);
            
            System.out.println("Total calculado: " + venta.getTotal());
            
            // Llamo al método crearVenta existente
            crearVenta(venta);
            
            System.out.println("VENTA GUARDADA EXITOSAMENTE ");
            return true;
            
        } catch (VentaInvalidaExcepcion e) {
            System.err.println("ERROR: Venta inválida - " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } catch (InventarioInsuficienteExcepcion e) {
            System.err.println("ERROR: Inventario insuficiente - " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } catch (ProductoNoEncontradoExcepcion e) {
            System.err.println("ERROR: Producto no encontrado - " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } catch (IOException e) {
            System.err.println("ERROR: Error de I/O - " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } catch (Exception e) {
            System.err.println("ERROR: Error inesperado - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public void crearVenta(Venta venta) throws IOException, VentaInvalidaExcepcion, InventarioInsuficienteExcepcion, ProductoNoEncontradoExcepcion {
        System.out.println("Validando venta...");
        validarVenta(venta);
        
        System.out.println("Actualizando stock de productos...");
        // Actualizar stock de productos
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            
            if (producto == null) {
                System.err.println("ERROR: Producto null en detalle");
                throw new ProductoNoEncontradoExcepcion("Producto null en el detalle de venta");
            }
            
            int stockActual = producto.getStock();
            int cantidadVendida = detalle.getCantidad();
            int nuevoStock = stockActual - cantidadVendida;
            
            System.out.println("Producto: " + producto.getNombre() + 
                             " | Stock actual: " + stockActual + 
                             " | Cantidad vendida: " + cantidadVendida + 
                             " | Nuevo stock: " + nuevoStock);
            
            if (nuevoStock < 0) {
                throw new InventarioInsuficienteExcepcion(
                    producto.getNombre(), 
                    stockActual, 
                    cantidadVendida
                );
            }
            
            productoServicio.actualizarStock(producto.getCodigo(), nuevoStock);
        }
        
        System.out.println("Guardando venta en repositorio...");
        repositorio.guardar(venta);
        System.out.println("Venta guardada en repositorio correctamente");
    }
    
    public Venta buscarVenta(String codigo) throws Exception {
        return repositorio.buscar(codigo);
    }
    
    public List<Venta> listarVentas() {
        return repositorio.listarTodos();
    }
    
    public double calcularTotalVentas() {
        return repositorio.listarTodos().stream()
                .mapToDouble(Venta::getTotal)
                .sum();
    }
    
    private void validarVenta(Venta venta) throws VentaInvalidaExcepcion {
        if (venta == null) {
            throw new VentaInvalidaExcepcion("La venta no puede ser null");
        }
        
        if (venta.getCliente() == null) {
            throw new VentaInvalidaExcepcion("Debe seleccionar un cliente");
        }
        
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new VentaInvalidaExcepcion("La venta debe tener al menos un producto");
        }
        
        System.out.println("Venta validada correctamente");
    }
}