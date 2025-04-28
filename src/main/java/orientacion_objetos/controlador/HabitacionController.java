package orientacion_objetos.controlador;

import orientacion_objetos.modelo.Habitacion;
import orientacion_objetos.modelo.HabitacionDAO;
import orientacion_objetos.vista.VistaReservas;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.sql.SQLException;
import java.util.List;

public class HabitacionController {
    private final VistaReservas vista;
    private final HabitacionDAO habitacionDAO;
    private boolean actualizando = false; // Bandera para evitar recursión

    public HabitacionController(VistaReservas vista, HabitacionDAO habitacionDAO) {
        this.vista = vista;
        this.habitacionDAO = habitacionDAO;
        configurarListeners();
        cargarHabitaciones();
    }

    private void configurarListeners() {
        vista.addActualizarEstadoListener(e -> actualizarEstadoHabitacion());
        vista.addRefrescarHabitacionesListener(e -> cargarHabitaciones());

        // Listener para cambios directos en la tabla
        vista.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2 && !actualizando) {
                    SwingUtilities.invokeLater(() -> {
                        actualizarEstadoHabitacion();
                    });
                }
            }
        });
    }

    private void cargarHabitaciones() {
        try {
            List<Habitacion> habitaciones = habitacionDAO.obtenerTodas();
            if (habitaciones.isEmpty()) {
                vista.mostrarMensaje("No se encontraron habitaciones en la BD");
            }
            vista.mostrarHabitaciones(habitaciones);
        } catch (SQLException ex) {
            vista.mostrarMensaje("Error al cargar habitaciones: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void actualizarEstadoHabitacion() {
        actualizando = true; // Evitar recursión
        try {
            Habitacion hab = vista.getHabitacionSeleccionada();
            if (hab == null) {
                vista.mostrarMensaje("Seleccione una habitación primero");
                return;
            }

            int filaSeleccionada = vista.getTablaHabitaciones().getSelectedRow();
            String nuevoEstado = (String) vista.getModeloHabitaciones().getValueAt(filaSeleccionada, 2);

            if (habitacionDAO.actualizarEstado(hab.getIdHabitacion(), nuevoEstado)) {
                hab.setEstado(nuevoEstado);
                vista.mostrarMensaje("Estado actualizado correctamente");
            } else {
                vista.mostrarMensaje("No se pudo actualizar el estado");
                vista.getModeloHabitaciones().setValueAt(hab.getEstado(), filaSeleccionada, 2);
            }
        } catch (SQLException ex) {
            vista.mostrarMensaje("Error al actualizar: " + ex.getMessage());
            int fila = vista.getTablaHabitaciones().getSelectedRow();
            if (fila >= 0) {
                Habitacion hab = vista.getHabitacionSeleccionada();
                vista.getModeloHabitaciones().setValueAt(hab.getEstado(), fila, 2);
            }
        } finally {
            actualizando = false;
        }
    }
}