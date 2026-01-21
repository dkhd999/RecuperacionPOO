package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
// No necesitamos importar "config" ni "conexion" porque ConexionBDD está aquí mismo en "modelo"

public class FacturaD {

    // Método para guardar la venta
    public boolean registrarVenta(FacturaModelo factura) {
        
        Connection con = null;
        PreparedStatement psVentas = null;   // Para tabla 'ventas'
        PreparedStatement psFactura = null;  // Para tabla 'facturas'
        PreparedStatement psDetalle = null;  // Para tabla 'detalles_factura'
        ResultSet rs = null;
        
        // 1. SQL para la tabla VENTAS (Resumen simple + FECHA)
        String sqlVentas = "INSERT INTO ventas (cliente, total, fecha) VALUES (?, ?, ?)";
        
        // 2. SQL para la tabla FACTURAS (Completa para Master-Detalle)
        String sqlFactura = "INSERT INTO facturas (cliente, fecha, subtotal, iva, total) VALUES (?, ?, ?, ?, ?)";
        
        // 3. SQL para los DETALLES
        String sqlDetalle = "INSERT INTO detalles_factura (id_factura, producto, cantidad, precio_unitario, importe) VALUES (?, ?, ?, ?, ?)";

        try {
            con = new ConexionBDD().conectar();
            // Desactivamos el auto-guardado para que todo se haga en grupo (Transacción)
            con.setAutoCommit(false);

            // --- PASO EXTRA: GUARDAR EN TABLA 'VENTAS' ---
            psVentas = con.prepareStatement(sqlVentas);
            psVentas.setString(1, factura.getCliente());
            psVentas.setDouble(2, factura.getTotal());
            // Aquí agregamos la FECHA que faltaba:
            psVentas.setDate(3, java.sql.Date.valueOf(factura.getFecha()));
            psVentas.executeUpdate();
            // ---------------------------------------------

            // --- PASO 1: GUARDAR EN TABLA 'FACTURAS' ---
            psFactura = con.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS);
            psFactura.setString(1, factura.getCliente());
            psFactura.setDate(2, java.sql.Date.valueOf(factura.getFecha()));
            psFactura.setDouble(3, factura.getSubtotal());
            psFactura.setDouble(4, factura.getIva());
            psFactura.setDouble(5, factura.getTotal());
            psFactura.executeUpdate();

            // --- PASO 2: OBTENER EL ID DE LA FACTURA ---
            rs = psFactura.getGeneratedKeys();
            int idFacturaGenerado = 0;
            if (rs.next()) {
                idFacturaGenerado = rs.getInt(1);
            }

            // --- PASO 3: GUARDAR LOS DETALLES ---
            psDetalle = con.prepareStatement(sqlDetalle);
            for (DetalleFacturaModelo detalle : factura.getDetalles()) {
                psDetalle.setInt(1, idFacturaGenerado);
                psDetalle.setString(2, detalle.getProducto());
                psDetalle.setInt(3, detalle.getCantidad());
                psDetalle.setDouble(4, detalle.getPrecioUnitario());
                psDetalle.setDouble(5, detalle.getImporte());
                psDetalle.addBatch();
            }
            psDetalle.executeBatch();
            
            // Si todo salió bien, confirmamos los cambios
            con.commit(); 
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar venta: " + e);
            try {
                if (con != null) con.rollback(); // Si falla, deshacemos todo
            } catch (SQLException ex) { }
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (psVentas != null) psVentas.close();
                if (psFactura != null) psFactura.close();
                if (psDetalle != null) psDetalle.close();
                if (con != null) con.close();
            } catch (SQLException e) { }
        }
    }
}