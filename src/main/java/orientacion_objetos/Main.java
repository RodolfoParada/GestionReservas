package orientacion_objetos;
import javax.swing.SwingUtilities;

import orientacion_objetos.controlador.HabitacionController;
import orientacion_objetos.controlador.ReservaController;
import orientacion_objetos.modelo.HabitacionDAO;
import orientacion_objetos.modelo.databaseConexion;
import orientacion_objetos.vista.VistaReservas;
import orientacion_objetos.modelo.ReservaDAO;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Connection conexion = databaseConexion.getConnection();
                if (databaseConexion.testConnection()) {
                    VistaReservas vista = new VistaReservas();

                    // Inicializar DAOs
                    HabitacionDAO habitacionDAO = new HabitacionDAO(conexion);
                    ReservaDAO reservaDAO = new ReservaDAO(conexion);

                    // Inicializar controladores
                    new HabitacionController(vista, habitacionDAO);
                    new ReservaController(vista, reservaDAO);

                    vista.setVisible(true);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error de conexión a la BD: " + ex.getMessage(),
                        "Error crítico",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}