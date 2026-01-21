package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionBDD {
  // CAMBIA "nombre_de_tu_bd" POR EL NOMBRE REAL DE TU BASE DE DATOS EN MYSQL
    private static final String URL = "jdbc:mysql://localhost:3306/tienda_minorista"; 
    private static final String USER = "root";
    private static final String PASS = "clientmod23"; // Por defecto en XAMPP es vacío, si tienes clave ponla aquí

    public Connection conectar() {
        Connection con = null;
        try {
            // Cargamos el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de Conexión: " + e.getMessage());
        }
        return con;
    }
}