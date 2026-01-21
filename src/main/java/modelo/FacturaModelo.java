package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FacturaModelo {

    // 1. ATRIBUTOS (Definidos una sola vez)
    private int id;
    private String cliente;
    private LocalDate fecha;
    private double subtotal;
    private double iva;
    private double total;
    
    // Lista para guardar los productos (Opcional, pero buena práctica mantenerla)
    private List<DetalleFacturaModelo> detalles; 

    // 2. CONSTRUCTOR VACÍO 
    // (ESTE ES EL QUE NECESITA TU CONTROLADOR para hacer "new FacturaModelo()")
    public FacturaModelo() {
        this.fecha = LocalDate.now();
        this.detalles = new ArrayList<>(); // Inicializamos la lista para evitar errores null
    }

    // 3. CONSTRUCTOR CON PARÁMETROS (Opcional)
    public FacturaModelo(String cliente, double total) {
        this.cliente = cliente;
        this.total = total;
        this.fecha = LocalDate.now();
        this.detalles = new ArrayList<>();
    }

    // 4. GETTERS Y SETTERS (Las "llaves" para que el Controlador entre)

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public double getTotal() {
        return total;
    }

    // Este es vital porque el controlador calcula el total y lo guarda aquí
    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    // Getters extra por si los necesitas después
    public List<DetalleFacturaModelo> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaModelo> detalles) {
        this.detalles = detalles;
    }
    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }
}