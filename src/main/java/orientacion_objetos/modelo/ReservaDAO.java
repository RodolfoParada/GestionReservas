package orientacion_objetos.modelo;

import orientacion_objetos.vista.VistaReservas;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.String.valueOf;

public class ReservaDAO {
    private final Connection conexion;
    private List<Reserva> reservas = new ArrayList<>();
    private int siguienteId = 1;


    // Constructor modificado para recibir la conexión
    public ReservaDAO(Connection conexion) {
        if (conexion == null) {
            throw new IllegalArgumentException("La conexión no puede ser nula");
        }
        this.conexion = conexion;
    }
    public List<Reserva> obtenerTodas() throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT id_reserva, id_cliente, id_habitacion, fecha_entrada, fecha_salida, estado FROM reservas";

        try (Statement stmt = this.conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // ... tu lógica para crear objetos Reserva
            }
        }
        return reservas;
    }

    public boolean esNombreValido(String nombre) {
        String patron = "^[A-Za-zÁáÉéÍíÓóÚúÑñÜü\\s]+$";
        return nombre.matches(patron);
    }

    public List<Reserva> obtenerReservas() {

            List<Reserva> reservas = new ArrayList<>();
            String sql = "SELECT r.idReserva, r.idCliente, r.idHabitacion, r.numero_habitacion, r.tipo_habitacion, r.fecha_entrada, r.fecha_salida, r.estado, " +
                    "c.nombre, c.contacto, c.preferencias " +
                    "FROM reservas r " +
                    "JOIN clientes c ON r.idCliente = c.idCliente";

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            try (Connection conn = databaseConexion.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdCliente(rs.getInt("idCliente"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setContacto(rs.getString("contacto"));
                    cliente.setPreferencias(rs.getString("preferencias"));

                    Reserva reserva = new Reserva();
                    reserva.setIdReserva(rs.getInt("idReserva"));
                    reserva.setCliente(cliente);
                    reserva.setHabitacion(rs.getString("numero_habitacion"));
                    reserva.setTipoHabitacion(rs.getString("tipo_habitacion"));

                    // Convertir java.sql.Date a String con formato dd-MM-yyyy
                    Date fechaEntrada = rs.getDate("fecha_entrada");
                    if (fechaEntrada != null) {
                        reserva.setFechaEntrada(sdf.format(fechaEntrada));
                    }

                    Date fechaSalida = rs.getDate("fecha_salida");
                    if (fechaSalida != null) {
                        reserva.setFechaSalida(sdf.format(fechaSalida));
                    }

                    reserva.setEstado(rs.getString("estado"));

                    reservas.add(reserva);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Manejo de excepciones
            }

            return reservas;
        }

    




    public Reserva obtenerPorId(int id) {
        for (Reserva r : reservas) {
            if (r.getIdReserva() == id) return r;
        }
        return null;
    }

    public void agregarReserva(Reserva reserva) {
        String sqlSelectHabitacion = "SELECT idHabitacion, estado FROM habitaciones WHERE idHabitacion = ?";
        String sqlSelectCliente = "SELECT idCliente FROM clientes WHERE idCliente = ?";
        String sqlInsertCliente = "INSERT INTO clientes (nombre, contacto, preferencias) VALUES (?, ?, ?)";
        String sqlInsertReserva = "INSERT INTO reservas (idCliente, idHabitacion, numero_habitacion, tipo_habitacion, fecha_entrada, fecha_salida, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseConexion.getConnection()) {
            // Verificar si la habitación existe y tiene el estado correcto
            try (PreparedStatement stmtHabitacion = conn.prepareStatement(sqlSelectHabitacion)) {
                stmtHabitacion.setInt(1, Integer.parseInt(reserva.getHabitacion()));  // Asegurarse de que habitacion es un número
                ResultSet rsHabitacion = stmtHabitacion.executeQuery();

                if (!rsHabitacion.next()) {
                    throw new SQLException("La habitación con ID " + reserva.getHabitacion() + " no existe.");
                }

                String estadoHabitacion = rsHabitacion.getString("estado");
                if (!estadoHabitacion.equals("Disponible") && !estadoHabitacion.equals("Reservada")) {
                    throw new SQLException("La habitación no está disponible para la reserva. Estado actual: " + estadoHabitacion);
                }
            }

            // Paso 1: Verificar si el cliente existe
            try (PreparedStatement stmtClienteExistente = conn.prepareStatement(sqlSelectCliente)) {
                stmtClienteExistente.setInt(1, reserva.getCliente().getIdCliente());
                ResultSet rsCliente = stmtClienteExistente.executeQuery();

                if (!rsCliente.next()) {
                    // El cliente no existe, lo insertamos
                    try (PreparedStatement stmtInsertCliente = conn.prepareStatement(sqlInsertCliente, Statement.RETURN_GENERATED_KEYS)) {
                        stmtInsertCliente.setString(1, reserva.getCliente().getNombre());
                        stmtInsertCliente.setString(2, reserva.getCliente().getContacto());
                        stmtInsertCliente.setString(3, reserva.getCliente().getPreferencias());
                        stmtInsertCliente.executeUpdate();

                        ResultSet rsInsertCliente = stmtInsertCliente.getGeneratedKeys();
                        if (rsInsertCliente.next()) {
                            int idGenerado = rsInsertCliente.getInt(1);
                            reserva.getCliente().setIdCliente(idGenerado); // actualizamos el objeto en memoria
                        } else {
                            throw new SQLException("No se pudo obtener el ID del cliente insertado.");
                        }
                    }
                }
            }

            // Paso 2: Insertar la reserva
            try (PreparedStatement stmtReserva = conn.prepareStatement(sqlInsertReserva, Statement.RETURN_GENERATED_KEYS)) {
                // Parsear y convertir fechas
                SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat sdfSalida = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat sdfMySQL = new SimpleDateFormat("yyyy-MM-dd");

                Date fechaEntrada = sdfEntrada.parse(reserva.getFechaEntrada());
                Date fechaSalida = sdfSalida.parse(reserva.getFechaSalida());

                String fechaEntradaFormatted = sdfMySQL.format(fechaEntrada);
                String fechaSalidaFormatted = sdfMySQL.format(fechaSalida);

                // Establecer parámetros
                stmtReserva.setInt(1, reserva.getCliente().getIdCliente());
                stmtReserva.setInt(2, Integer.parseInt(reserva.getHabitacion()));
                stmtReserva.setString(3, reserva.getHabitacion());
                stmtReserva.setString(4, reserva.getTipoHabitacion());
                stmtReserva.setString(5, fechaEntradaFormatted);
                stmtReserva.setString(6, fechaSalidaFormatted);
                stmtReserva.setString(7, reserva.getEstado());

                stmtReserva.executeUpdate(); // Insertar la reserva

                ResultSet rsReserva = stmtReserva.getGeneratedKeys();
                if (rsReserva.next()) {
                    reserva.setIdReserva(rsReserva.getInt(1));
                }

                // Añadir a la lista en memoria
                reservas.add(reserva);
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error al parsear las fechas", ex);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error en la base de datos", ex);
        }
    }



    // MÉTODO PARA ACTUALIZAR EL ESTADO DE UNA HABITACIÓN
    private void actualizarEstadoHabitacion(Connection conn, String idHabitacion, String nuevoEstado) throws SQLException {
        String sqlUpdateHabitacion = "UPDATE habitaciones SET estado = ? WHERE idHabitacion = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateHabitacion)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, Integer.parseInt(idHabitacion));
            stmt.executeUpdate();
        }
    }


    public void actualizarReserva(Reserva reserva) throws SQLException {
        String sql = "UPDATE reservas SET id_cliente=?, id_habitacion=?, fecha_entrada=?, fecha_salida=?, estado=? WHERE id_reserva=?";
        try (Connection conn = conexion;
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reserva.getCliente().getIdCliente());
            stmt.setInt(2, reserva.getHabitaciones().getIdHabitacion());

            stmt.setString(3, reserva.getFechaEntrada());
            stmt.setString(4, reserva.getFechaSalida());
            stmt.setString(5, reserva.getEstado());
            stmt.setInt(6, reserva.getIdReserva()); // ID de la reserva a actualizar

            stmt.executeUpdate();
        }
    }



    public void eliminarReserva(int id) {
       // reservas.removeIf(r -> r.getIdReserva() == id);
        String sql = "DELETE FROM reservas WHERE idReserva=?";
        try (Connection conn = databaseConexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejo adicional de la excepción si es necesario
        }
    }

    public int generarNuevoId() {
        return siguienteId++;
    }
    private int guardarClienteSiEsNuevo(Cliente cliente, Connection conn) throws SQLException {
        if (cliente.getIdCliente() != 0) {
            return cliente.getIdCliente();  // Ya tiene un ID
        }

        // Insertar cliente nuevo
        String sql = "INSERT INTO clientes (nombre, contacto, preferencias) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getContacto());
            stmt.setString(3, cliente.getPreferencias());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int nuevoId = rs.getInt(1);
                cliente.setIdCliente(nuevoId);  // Actualiza el objeto Cliente
                return nuevoId;
            } else {
                throw new SQLException("No se pudo obtener el ID del nuevo cliente.");
            }
        }
    }


    public void guardarCambiosReserva(Reserva reserva) throws SQLException {
        String sqlActualizarCliente = "UPDATE clientes SET nombre = ? WHERE idCliente = ?";
        String sqlActualizarReserva = "UPDATE reservas SET idHabitacion = ?, numero_habitacion = ?, tipo_habitacion = ?, fecha_entrada = ?, fecha_salida = ?, estado = ? WHERE id_reserva = ?";

        try (Connection conn = databaseConexion.getConnection()) {
            conn.setAutoCommit(false); // Empieza una transacción

            try {
                // Actualizar cliente
                try (PreparedStatement stmtCliente = conn.prepareStatement(sqlActualizarCliente)) {
                    stmtCliente.setString(1, reserva.getCliente().getNombre());
                    stmtCliente.setInt(2, reserva.getCliente().getIdCliente());
                    stmtCliente.executeUpdate();
                }

                // Actualizar reserva
                try (PreparedStatement stmtReserva = conn.prepareStatement(sqlActualizarReserva)) {
                    stmtReserva.setInt(1, Integer.parseInt(reserva.getHabitacion()));
                    stmtReserva.setString(2, reserva.getHabitacion());
                    stmtReserva.setString(3, reserva.getTipoHabitacion());

                    SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat sdfMySQL = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaEntrada = sdfEntrada.parse(reserva.getFechaEntrada());
                    Date fechaSalida = sdfEntrada.parse(reserva.getFechaSalida());

                    stmtReserva.setString(4, sdfMySQL.format(fechaEntrada));
                    stmtReserva.setString(5, sdfMySQL.format(fechaSalida));
                    stmtReserva.setString(6, reserva.getEstado());
                    stmtReserva.setInt(7, reserva.getIdReserva());

                    stmtReserva.executeUpdate();
                }

                conn.commit(); // Confirmar cambios

            } catch (Exception e) {
                conn.rollback(); // Deshacer si algo falla
                throw new SQLException("Error al guardar cambios en la reserva", e);
            }
        }
    }

    public int eliminarReservasPorCliente(int idCliente) throws SQLException {
        String sql = "DELETE FROM reservas WHERE idCliente = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            return stmt.executeUpdate(); // Retorna el número de filas afectadas
        }

    }


        public void insertarReserva(Reserva reserva) throws SQLException {
            String sql = "INSERT INTO reservas (idCliente, idHabitacion, numero_habitacion, tipo_habitacion, fecha_entrada, fecha_salida, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                // Validación y asignación de idHabitacion
                try {
                    int habitacionId = Integer.parseInt(reserva.getHabitacion());
                    ps.setInt(2, habitacionId);  // Asumimos que habitacion es un número entero
                } catch (NumberFormatException e) {
                    throw new SQLException("El número de habitación no es válido", e);
                }

                // Insertar el resto de los parámetros
                ps.setInt(1, reserva.getCliente().getIdCliente()); // ID del cliente
                ps.setString(3, reserva.getHabitacion());  // Número de habitación (como String)
                ps.setString(4, reserva.getTipoHabitacion()); // Tipo de habitación

                // Convertir fecha_entrada
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date fechaEntrada = sdf.parse(reserva.getFechaEntrada());
                    ps.setDate(5, new java.sql.Date(fechaEntrada.getTime())); // Fecha de entrada
                } catch (ParseException e) {
                    throw new SQLException("Formato de fecha de entrada no válido", e);
                }

                // Convertir fecha_salida
                try {
                    Date fechaSalida = sdf.parse(reserva.getFechaSalida());
                    ps.setDate(6, new java.sql.Date(fechaSalida.getTime())); // Fecha de salida
                } catch (ParseException e) {
                    throw new SQLException("Formato de fecha de salida no válido", e);
                }

                ps.setString(7, reserva.getEstado());  // Estado de la reserva

                // Ejecutar la inserción
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error al insertar la reserva: " + e.getMessage());
                throw e; // Propaga la excepción para manejo superior
            }
        }
    }


