package orientacion_objetos.controlador;

import orientacion_objetos.modelo.Reserva;
import orientacion_objetos.modelo.ReservaDAO;
import orientacion_objetos.modelo.databaseConexion;
import orientacion_objetos.vista.VistaReservas;

import javax.swing.*;  // IMPORT necesario para JOptionPane
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReservaController {
    private VistaReservas vista;
    private ReservaDAO modelo;
    private boolean editing = false;

    public ReservaController(VistaReservas vista, ReservaDAO modelo) {
        this.vista = vista;
        this.modelo = modelo;

        // Configurar todos los listeners
        vista.addCrearReservaListener(new CrearReservaListener());
        vista.addActualizarReservaListener(new ActualizarReservaListener());
        vista.addCancelarReservaListener(new CancelarReservaListener());

        actualizarTabla();
    }

    private void actualizarTabla() {
        List<Reserva> list = modelo.obtenerReservas();
        vista.mostrarReservas(list);
    }

    class CrearReservaListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Reserva nuevaReserva = vista.mostrarDialogoCrearReserva();
            if (nuevaReserva != null) {
                try {
                    // Validar fechas (opcional, podría ir en el modelo)
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date fechaEntrada = sdf.parse(nuevaReserva.getFechaEntrada());
                    Date fechaSalida = sdf.parse(nuevaReserva.getFechaSalida());

                    if(fechaSalida.before(fechaEntrada)) {
                        vista.mostrarMensaje("La fecha de salida debe ser posterior a la de entrada");
                        return;
                    }

                    modelo.agregarReserva(nuevaReserva);
                    actualizarTabla();
                    vista.mostrarMensaje("Reserva creada exitosamente!");
                } catch (ParseException ex) {
                    vista.mostrarMensaje("Error en formato de fecha: " + ex.getMessage());
                } catch (Exception ex) {
                    vista.mostrarMensaje("Error al crear reserva: " + ex.getMessage());
                }
            }
        }
    }

    class ActualizarReservaListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!editing) {
                int id = vista.getSelectedReservaId();
                if (id == -1) {
                    vista.mostrarMensaje("Selecciona primero una reserva.");
                    return;
                }
                vista.activarModoEdicion();
                editing = true;
            } else {
                // Obtener la reserva editada desde la vista
                Reserva reservaEditada = vista.getEditedReserva();

                if (reservaEditada != null) {
                    try {
                        // No es necesario verificar que la reserva tenga cliente y habitación asociados,
                        // ya que se asume que no se modificarán en esta edición.

                        // Verificar fechas (asegúrate que las fechas sean válidas)
                        SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd-MM-yyyy");
                        Date fechaEntrada = sdfEntrada.parse(reservaEditada.getFechaEntrada());
                        SimpleDateFormat sdfSalida = new SimpleDateFormat("dd-MM-yyyy");
                        Date fechaSalida = sdfSalida.parse(reservaEditada.getFechaSalida());

                        if (fechaSalida.before(fechaEntrada)) {
                            vista.mostrarMensaje("La fecha de salida debe ser posterior a la de entrada.");
                            return;
                        }

                        // Convertir las fechas a formato 'yyyy-MM-dd' para la base de datos
                        SimpleDateFormat sdfBD = new SimpleDateFormat("yyyy-MM-dd");
                        String fechaEntradaBD = sdfBD.format(fechaEntrada);
                        String fechaSalidaBD = sdfBD.format(fechaSalida);

                        // Ahora actualizamos directamente los datos de la reserva, sin cambiar los IDs de cliente y habitación
                        Connection con = databaseConexion.getConnection();
                        StringBuilder query = new StringBuilder("UPDATE reservas SET ");

                        // Lista de parámetros de actualización
                        boolean primero = true;

                        // Condición para agregar la fecha de entrada si se modificó
                        if (fechaEntrada != null) {
                            if (!primero) query.append(", ");
                            query.append("fecha_entrada=?");
                            primero = false;
                        }

                        // Condición para agregar la fecha de salida si se modificó
                        if (fechaSalida != null) {
                            if (!primero) query.append(", ");
                            query.append("fecha_salida=?");
                            primero = false;
                        }

                        // Condición para agregar el estado si se modificó
                        if (reservaEditada.getEstado() != null) {
                            if (!primero) query.append(", ");
                            query.append("estado=?");
                            primero = false;
                        }

                        // Condición para agregar la habitación si se modificó
                        if (reservaEditada.getHabitacion() != null) {
                            if (!primero) query.append(", ");
                            query.append("numero_habitacion=?");
                            primero = false;
                        }

                        // Finalizar la consulta con la condición de actualización para la idReserva
                        query.append(" WHERE idReserva=?");

                        // Preparar la sentencia SQL
                        PreparedStatement ps = con.prepareStatement(query.toString());

                        // Establecer los valores de los parámetros
                        int index = 1;
                        if (fechaEntrada != null) {
                            ps.setString(index++, fechaEntradaBD);  // Usar el formato adecuado para la fecha
                        }
                        if (fechaSalida != null) {
                            ps.setString(index++, fechaSalidaBD);  // Usar el formato adecuado para la fecha
                        }
                        if (reservaEditada.getEstado() != null) {
                            ps.setString(index++, reservaEditada.getEstado());
                        }
                        if (reservaEditada.getHabitacion() != null) {
                            ps.setString(index++, reservaEditada.getHabitacion());
                        }
                        ps.setInt(index++, reservaEditada.getIdReserva()); // ID de la reserva a actualizar

                        // Ejecutar la actualización
                        int filasAfectadas = ps.executeUpdate();

                        if (filasAfectadas > 0) {
                            vista.mostrarMensaje("Reserva actualizada correctamente.");
                            vista.actualizarVistaReservas();  // o actualizarTabla() si prefieres
                        } else {
                            vista.mostrarMensaje("No se pudo actualizar la reserva.");
                        }

                        ps.close();
                        con.close();

                        vista.desactivarModoEdicion();
                        editing = false;
                    } catch (SQLException ex) {
                        vista.mostrarMensaje("Error al guardar cambios: " + ex.getMessage());
                    } catch (ParseException ex) {
                        vista.mostrarMensaje("Error en formato de fecha: " + ex.getMessage());
                    }
                }
            }
        }
    }





    class CancelarReservaListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int id = vista.getSelectedReservaId();
            if (id == -1) {
                vista.mostrarMensaje("Selecciona primero una reserva.");
                return;
            }
            int resp = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Cancelar reserva #" + id + "?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );
            if (resp == JOptionPane.YES_OPTION) {
                modelo.eliminarReserva(id);
                actualizarTabla();
                vista.mostrarMensaje("Reserva eliminada.");
            }
        }
    }



}