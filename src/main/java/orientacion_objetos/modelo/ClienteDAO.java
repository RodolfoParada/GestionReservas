package orientacion_objetos.modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private final Connection conexion;

    public ClienteDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // Crear nuevo cliente
    public int crearCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, contacto, preferencias) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getContacto());
            pstmt.setString(3, cliente.getPreferencias());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear el cliente, ninguna fila afectada");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna el ID generado
                } else {
                    throw new SQLException("No se obtuvo ID generado");
                }
            }
        }
    }

    // Obtener todos los clientes
    public List<Cliente> obtenerTodosClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("idCliente"),
                        rs.getString("nombre"),
                        rs.getString("contacto"),
                        rs.getString("preferencias")
                );
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    // Actualizar cliente
    public boolean actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, contacto = ?, preferencias = ? WHERE idCliente = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getContacto());
            pstmt.setString(3, cliente.getPreferencias());
            pstmt.setInt(4, cliente.getIdCliente());

            return pstmt.executeUpdate() > 0;
        }
    }

    // Eliminar cliente
    public boolean eliminarCliente(int idCliente) throws SQLException {
        String sql = "DELETE FROM clientes WHERE idCliente = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Obtener cliente por ID
    public Cliente obtenerClientePorId(int idCliente) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE idCliente = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getInt("idCliente"),
                            rs.getString("nombre"),
                            rs.getString("contacto"),
                            rs.getString("preferencias")
                    );
                }
            }
        }
        return null;
    }
}