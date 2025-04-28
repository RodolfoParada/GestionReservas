package orientacion_objetos.modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {
    private final Connection conexion;

    public HabitacionDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public List<Habitacion> obtenerTodas() throws SQLException {
        List<Habitacion> habitaciones = new ArrayList<>();
        String sql = "SELECT idHabitacion, tipo, estado FROM habitaciones ORDER BY idHabitacion";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                habitaciones.add(new Habitacion(
                        rs.getInt("idHabitacion"),  // Exactamente como en la tabla
                        rs.getString("tipo"),
                        rs.getString("estado")
                ));
            }
        }
        return habitaciones;
    }


    public boolean actualizarEstado(int idHabitacion, String nuevoEstado) throws SQLException {
        String sql = "UPDATE habitaciones SET estado = ? WHERE idHabitacion = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idHabitacion);

            return pstmt.executeUpdate() > 0;
        }
    }

    // En HabitacionDAO
    public Habitacion obtenerHabitacionPorId(int idHabitacion) {
        String sql = "SELECT * FROM habitaciones WHERE idHabitacion = ?";
        try (Connection conn = databaseConexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idHabitacion);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String tipo = rs.getString("tipo");
                String estado = rs.getString("estado");
                return new Habitacion(idHabitacion, tipo, estado);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}