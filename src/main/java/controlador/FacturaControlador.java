package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import modelo.DetalleFacturaModelo;
import modelo.FacturaD;      // Clase para guardar en BD (DAO)
import modelo.FacturaModelo; // Clase de datos (Getters/Setters)
import vista.FacturaVista;   // La pantalla

public class FacturaControlador implements ActionListener {

    // Variables privadas
    private FacturaVista vista;
    private FacturaD dao; // Variable para manejar la base de datos

    // Constructor
    public FacturaControlador(FacturaVista vista, FacturaD dao) {
        this.vista = vista;
        this.dao = dao;
        
        // Usamos los GETTERS para acceder a los botones
        this.vista.getBtnAgregar().addActionListener(this);
        this.vista.getBtnGuardar().addActionListener(this);
    }

    public void iniciar() {
        vista.setVisible(true);
        vista.setTitle("Gestión de Ventas (MVC)");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // --- BOTÓN AGREGAR ---
        if (e.getSource() == vista.getBtnAgregar()) {
            try {
                // 1. Capturar datos usando los Getters
                String producto = vista.getTxtProducto().getText();
                int cantidad = Integer.parseInt(vista.getTxtCantidad().getText());
                double precio = Double.parseDouble(vista.getTxtPrecio().getText());
                
                // 2. Calcular importe
                double importe = cantidad * precio;
                
                // 3. Agregar a la tabla visual usando el Getter del Modelo
                Object[] fila = {producto, cantidad, precio, importe};
                vista.getModeloTabla().addRow(fila);
                
                calcularTotales();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "Por favor ingrese números válidos en Cantidad y Precio.");
            }
        }
        
        // --- BOTÓN GUARDAR ---
        if (e.getSource() == vista.getBtnGuardar()) {
            guardarEnBaseDeDatos();
        }
    }

    private void calcularTotales() {
        double subtotal = 0;
        // Recorrer la tabla para sumar
        for (int i = 0; i < vista.getModeloTabla().getRowCount(); i++) {
            subtotal += (double) vista.getModeloTabla().getValueAt(i, 3);
        }
        
        double iva = subtotal * 0.15;
        double total = subtotal + iva;
        
        // Mostrar en las etiquetas usando Getters
        vista.getLblSubtotal().setText("Subtotal: $" + String.format("%.2f", subtotal));
        vista.getLblIva().setText("IVA: $" + String.format("%.2f", iva));
        vista.getLblTotal().setText("TOTAL: $" + String.format("%.2f", total));
    }

    private void guardarEnBaseDeDatos() {
        FacturaModelo factura = new FacturaModelo();
        factura.setCliente(vista.getTxtCliente().getText());
        
        double subtotalAcumulado = 0.0;
        
        // --- AQUÍ RECORREMOS LA TABLA VISUAL PARA LLENAR EL MODELO ---
        javax.swing.table.DefaultTableModel modeloTabla = (javax.swing.table.DefaultTableModel) vista.getModeloTabla();
        
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            // 1. Extraer datos de la fila
            String prod = modeloTabla.getValueAt(i, 0).toString();
            int cant = Integer.parseInt(modeloTabla.getValueAt(i, 1).toString());
            double prec = Double.parseDouble(modeloTabla.getValueAt(i, 2).toString());
            double imp = Double.parseDouble(modeloTabla.getValueAt(i, 3).toString());
            
            // 2. Sumar al subtotal
            subtotalAcumulado += imp;
            
            // 3. Agregar a la lista de detalles del modelo (para guardar en BD)
            DetalleFacturaModelo detalle = new DetalleFacturaModelo(prod, cant, prec, imp);
            factura.getDetalles().add(detalle);
        }

        // --- CÁLCULOS FINALES ---
        double iva = subtotalAcumulado * 0.15; // 15% IVA
        
        // Redondear a 2 decimales para evitar problemas de visualización (ej: 10.555555)
        subtotalAcumulado = Math.round(subtotalAcumulado * 100.0) / 100.0;
        iva = Math.round(iva * 100.0) / 100.0;
        double total = subtotalAcumulado + iva;
        
        // Seteamos los valores calculados al modelo principal
        factura.setSubtotal(subtotalAcumulado);
        factura.setIva(iva);
        factura.setTotal(total);

        // --- ENVIAR AL DAO ---
        if (dao.registrarVenta(factura)) {
            javax.swing.JOptionPane.showMessageDialog(vista, "Factura y Detalles Guardados con Éxito");
            
            // Limpiar todo...
            modeloTabla.setRowCount(0);
            vista.getTxtCliente().setText("");
            vista.getLblSubtotal().setText("Subtotal: $0.00");
            vista.getLblIva().setText("IVA: $0.00");
            vista.getLblTotal().setText("TOTAL: $0.00");
        } else {
            javax.swing.JOptionPane.showMessageDialog(vista, "Error crítico al guardar en BD");
        }
    }
}