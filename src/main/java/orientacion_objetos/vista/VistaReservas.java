package orientacion_objetos.vista;

import orientacion_objetos.modelo.*;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VistaReservas extends JFrame {
    private JTable tablaReservas;
    private DefaultTableModel modeloReservas;
    private JButton btnCrear, btnActualizar, btnCancelar;
    private boolean editMode = false;
    final int[] clienteEnEdicion = {-1};  // ← BORRAR esto si aún lo tenés
    // variables para habitaciones
    private JTable tablaHabitaciones;
    private JComboBox<String> comboEstado;
    private JButton btnActualizarEstado;
    private JButton btnRefrescarHabitaciones;


    // Nuevos atributos para clientes
    private DefaultTableModel modeloClientes;
    private JTable tablaClientes;

    public VistaReservas() {
        setTitle("Sistema de Gestión del Hotel Boutique Andes");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Reservas", createReservaPanel());
        tabs.addTab("Clientes", createClientePanel());
        tabs.addTab("Habitaciones", createHabitacionPanel());
        add(tabs);
    }

    private JPanel createReservaPanel() {
        JPanel p = new JPanel(new BorderLayout());

        modeloReservas = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int col) {
                return editMode;
            }
        };
        modeloReservas.setColumnIdentifiers(
                new String[]{"ID Reserva", "Nombre Cliente", "Habitación",
                        "Tipo de habitación", "Fecha Entrada", "Fecha Salida", "Estado"}
        );

        tablaReservas = new JTable(modeloReservas);
        p.add(new JScrollPane(tablaReservas), BorderLayout.CENTER);

        JPanel bp = new JPanel();
        btnCrear = new JButton("Crear Reserva");
        btnActualizar = new JButton("Actualizar Reserva");
        btnCancelar = new JButton("Cancelar Reserva");
        bp.add(btnCrear);
        bp.add(btnActualizar);
        bp.add(btnCancelar);

        p.add(bp, BorderLayout.SOUTH);
        return p;
    }
    public JTable getTablaHabitaciones() {
        return tablaHabitaciones;
    }

    public DefaultTableModel getModeloHabitaciones() {
        return (DefaultTableModel) tablaHabitaciones.getModel();
    }

    public void actualizarEstadoEnVista(int idHabitacion, String nuevoEstado) {
        DefaultTableModel modelo = (DefaultTableModel) tablaHabitaciones.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if ((int) modelo.getValueAt(i, 0) == idHabitacion) {
                modelo.setValueAt(nuevoEstado, i, 2);
                break;
            }
        }
    }
    public Reserva mostrarDialogoCrearReserva() {
        JTextField txtNombreCliente = new JTextField();
        JTextField txtContacto = new JTextField();
        JTextField txtPreferencias = new JTextField();

        JComboBox<Habitacion> comboHabitacion = new JComboBox<>();
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Todos", "Estándar", "Deluxe", "Suite"});
        JComboBox<String> comboEstado = new JComboBox<>();
        comboEstado.setEnabled(false); // Estado no editable

        // Usar una lista final para almacenar las habitaciones
        final List<Habitacion> todasLasHabitaciones = new ArrayList<>();

        try {
            HabitacionDAO habitacionDAO = new HabitacionDAO(databaseConexion.getConnection());
            todasLasHabitaciones.addAll(habitacionDAO.obtenerTodas());

            // Añadir habitaciones al combo
            for (Habitacion h : todasLasHabitaciones) {
                comboHabitacion.addItem(h);
            }
        } catch (SQLException e) {
            mostrarMensaje("Error al cargar habitaciones desde la base de datos");
            e.printStackTrace();
            return null;
        }

        // Mostrar solo el número de habitación
        comboHabitacion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Habitacion habitacion) {
                    value = habitacion.getIdHabitacion(); // Mostrar solo el número
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Actualizar habitaciones según tipo seleccionado
        comboTipo.addActionListener(e -> {
            String tipoSeleccionado = (String) comboTipo.getSelectedItem();
            comboHabitacion.removeAllItems();

            // Usamos la lista final para filtrar por tipo
            for (Habitacion h : todasLasHabitaciones) {
                if (tipoSeleccionado.equals("Todos") || h.getTipo().equalsIgnoreCase(tipoSeleccionado)) {
                    comboHabitacion.addItem(h);
                }
            }
        });

        // Actualizar estado al seleccionar habitación
        comboHabitacion.addActionListener(e -> {
            Habitacion seleccionada = (Habitacion) comboHabitacion.getSelectedItem();
            comboEstado.removeAllItems();
            if (seleccionada != null) {
                comboEstado.addItem(seleccionada.getEstado());
            }
        });

        JFormattedTextField txtEntrada = createDateField();
        JFormattedTextField txtSalida = createDateField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Nombre Cliente:"));
        panel.add(txtNombreCliente);
        panel.add(new JLabel("Habitación:"));
        panel.add(comboHabitacion);
        panel.add(new JLabel("Tipo de Habitación:"));
        panel.add(comboTipo);
        panel.add(new JLabel("Fecha Entrada (dia-mes-año):"));
        panel.add(txtEntrada);
        panel.add(new JLabel("Fecha Salida (dia-mes-año):"));
        panel.add(txtSalida);
        panel.add(new JLabel("Estado:"));
        panel.add(comboEstado);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, panel, "Crear Nueva Reserva",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String nombreCliente = txtNombreCliente.getText().trim();
                String contacto = txtContacto.getText().trim();
                String preferencias = txtPreferencias.getText().trim();

                if (!nombreCliente.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                    mostrarMensaje("El nombre solo puede contener letras y espacios");
                    return null;
                }

                Habitacion habitacionSeleccionada = (Habitacion) comboHabitacion.getSelectedItem();
                String tipo = (String) comboTipo.getSelectedItem();
                String entrada = txtEntrada.getText().trim();
                String salida = txtSalida.getText().trim();
                String estado = (String) comboEstado.getSelectedItem();

                if (nombreCliente.isEmpty() || entrada.isEmpty() || salida.isEmpty()) {
                    mostrarMensaje("Todos los campos deben estar completos.");
                    return null;
                }

                if (!entrada.matches("\\d{2}-\\d{2}-\\d{4}") || !salida.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    mostrarMensaje("Formato de fecha inválido. Use dd-MM-yyyy (ej: 02-05-2025)");
                    return null;
                }

                Cliente cliente = new Cliente(1,nombreCliente);

                Reserva nuevaReserva = new Reserva();
                nuevaReserva.setCliente(cliente);
                nuevaReserva.setHabitacion(String.valueOf(habitacionSeleccionada.getIdHabitacion()));
                nuevaReserva.setTipoHabitacion(habitacionSeleccionada.getTipo());
                nuevaReserva.setFechaEntrada(entrada);
                nuevaReserva.setFechaSalida(salida);
                nuevaReserva.setEstado(estado);

                return nuevaReserva;
            }
            return null;
        }
    }





    // Método auxiliar para crear campos de fecha formateados
    private JFormattedTextField createDateField() {
        JFormattedTextField field = new JFormattedTextField();
        field.setColumns(10);
        field.setValue("dia-mes-año");
        field.setHorizontalAlignment(JTextField.CENTER);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if(field.getText().equals("dia-mes-año")) {
                    field.setValue("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(field.getText().isEmpty()) {
                    field.setValue("dia-mes-año");
                }
            }
        });

        return field;
    }
    private JPanel createClientePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Formulario
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField txtId = new JTextField();
        txtId.setEditable(false);
        JTextField txtNombre = new JTextField();
        JTextField txtContacto = new JTextField();
        JTextField txtPreferencias = new JTextField();

        formPanel.add(new JLabel("ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Contacto:"));
        formPanel.add(txtContacto);
        formPanel.add(new JLabel("Preferencias:"));
        formPanel.add(txtPreferencias);

        // Botones
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnEliminar);

        // Tabla
        modeloClientes = new DefaultTableModel(new String[]{"ID", "Nombre", "Contacto", "Preferencias"}, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaClientes = new JTable(modeloClientes);

        // Cargar datos iniciales
        cargarClientesDesdeBD();

        // Listeners
        btnNuevo.addActionListener(e -> {
            txtId.setText("");
            txtNombre.setText("");
            txtContacto.setText("");
            txtPreferencias.setText("");
        });

        btnGuardar.addActionListener(e -> guardarCliente(txtId, txtNombre, txtContacto, txtPreferencias));

        btnEliminar.addActionListener(e -> eliminarCliente(txtId));

        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaClientes.getSelectedRow();
                if (fila >= 0) {
                    txtId.setText(modeloClientes.getValueAt(fila, 0).toString());
                    txtNombre.setText(modeloClientes.getValueAt(fila, 1).toString());
                    txtContacto.setText(modeloClientes.getValueAt(fila, 2).toString());
                    txtPreferencias.setText(modeloClientes.getValueAt(fila, 3).toString());
                }
            }
        });

        // Organizar componentes
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        return panel;
    }

    private void cargarClientesDesdeBD() {
        try (Connection conn = databaseConexion.getConnection()) {
            ClienteDAO clienteDAO = new ClienteDAO(conn);
            List<Cliente> clientes = clienteDAO.obtenerTodosClientes();

            modeloClientes.setRowCount(0); // Limpiar tabla

            for (Cliente cliente : clientes) {
                modeloClientes.addRow(new Object[]{
                        cliente.getIdCliente(),
                        cliente.getNombre(),
                        cliente.getContacto(),
                        cliente.getPreferencias()
                });
            }
        } catch (SQLException ex) {
            mostrarMensaje("Error al cargar clientes: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void guardarCliente(JTextField txtId, JTextField txtNombre,
                                JTextField txtContacto, JTextField txtPreferencias) {
        try (Connection conn = databaseConexion.getConnection()) {
            ClienteDAO clienteDAO = new ClienteDAO(conn);
            Cliente cliente = new Cliente(
                    0, // ID se asignará si es nuevo
                    txtNombre.getText().trim(),
                    txtContacto.getText().trim(),
                    txtPreferencias.getText().trim()
            );

            if (txtId.getText().isEmpty()) {
                // Nuevo cliente
                int idGenerado = clienteDAO.crearCliente(cliente);
                mostrarMensaje("Cliente creado con ID: " + idGenerado);
            } else {
                // Actualizar existente
                cliente.setIdCliente(Integer.parseInt(txtId.getText()));
                if (clienteDAO.actualizarCliente(cliente)) {
                    mostrarMensaje("Cliente actualizado correctamente");
                } else {
                    mostrarMensaje("No se pudo actualizar el cliente");
                }
            }
            cargarClientesDesdeBD(); // Refrescar tabla
        } catch (SQLException | NumberFormatException ex) {
            mostrarMensaje("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void eliminarCliente(JTextField txtId) {
        if (txtId.getText().isEmpty()) {
            mostrarMensaje("Seleccione un cliente para eliminar");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este cliente y todas sus reservas asociadas?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = databaseConexion.getConnection()) {
                conn.setAutoCommit(false); // Iniciamos transacción

                try {
                    int idCliente = Integer.parseInt(txtId.getText());

                    // 1. Primero eliminamos las reservas asociadas
                    ReservaDAO reservaDAO = new ReservaDAO(conn);
                    int reservasEliminadas = reservaDAO.eliminarReservasPorCliente(idCliente);

                    // 2. Luego eliminamos el cliente
                    ClienteDAO clienteDAO = new ClienteDAO(conn);
                    boolean clienteEliminado = clienteDAO.eliminarCliente(idCliente);

                    if (clienteEliminado) {
                        conn.commit(); // Confirmamos la transacción
                        mostrarMensaje("Cliente eliminado correctamente. "
                                + (reservasEliminadas > 0 ?
                                "Se eliminaron " + reservasEliminadas + " reservas asociadas." :
                                "No tenía reservas asociadas."));
                        cargarClientesDesdeBD(); // Actualizamos la tabla
                    } else {
                        conn.rollback(); // Si falla, hacemos rollback
                        mostrarMensaje("No se pudo eliminar el cliente");
                    }
                } catch (SQLException ex) {
                    conn.rollback(); // Rollback en caso de error
                    manejarExcepcionEliminacion(ex);
                } catch (NumberFormatException ex) {
                    conn.rollback();
                    mostrarMensaje("ID de cliente inválido: " + txtId.getText());
                }
            } catch (SQLException ex) {
                mostrarMensaje("Error al conectar con la base de datos: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // Método auxiliar para manejar diferentes tipos de excepciones SQL
    private void manejarExcepcionEliminacion(SQLException ex) {
        if (ex.getSQLState().equals("23000") && ex.getMessage().contains("foreign key constraint")) {
            mostrarMensaje("No se puede eliminar el cliente porque tiene reservas asociadas "
                    + "y no se pudieron eliminar automáticamente.");
        } else {
            mostrarMensaje("Error al eliminar: " + ex.getMessage());
        }
        ex.printStackTrace();
    }

    // es la vista de la pestaña de habitación
    private JPanel createHabitacionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnas = {"ID Habitación", "Tipo", "Estado"};
        DefaultTableModel modeloHabitaciones = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo se puede editar el estado
                return column == 2;
            }
        };

        tablaHabitaciones = new JTable(modeloHabitaciones);
        // Configurar el editor de celda para el estado
        comboEstado = new JComboBox<>(new String[]{
                "Disponible", "Ocupada", "En limpieza",
                "En mantenimiento", "Reservada", "Confirmada"
        });


        // Configurar el editor para que se active al hacer clic
        DefaultCellEditor editor = new DefaultCellEditor(comboEstado) {
            @Override
            public boolean stopCellEditing() {
                boolean stopped = super.stopCellEditing();
                if (stopped) {
                    // Forzar la actualización inmediata del valor
                    int row = tablaHabitaciones.getEditingRow();
                    int col = tablaHabitaciones.getEditingColumn();
                    if (row >= 0 && col >= 0) {
                        modeloHabitaciones.setValueAt(comboEstado.getSelectedItem(), row, col);
                    }
                }
                return stopped;
            }
        };
        tablaHabitaciones.getColumnModel().getColumn(2).setCellEditor(editor);
        tablaHabitaciones.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);


        JScrollPane scrollPane = new JScrollPane(tablaHabitaciones);

        // Botones
        JPanel botonesPanel = new JPanel();
        btnActualizarEstado = new JButton("Actualizar Estado");
        btnRefrescarHabitaciones = new JButton("Refrescar");

        botonesPanel.add(btnActualizarEstado);
        botonesPanel.add(btnRefrescarHabitaciones);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(botonesPanel, BorderLayout.SOUTH);

        return panel;

    }
    public void addTableModelListener(TableModelListener listener) {
        tablaHabitaciones.getModel().addTableModelListener(listener);
    }
    public void mostrarHabitaciones(List<Habitacion> habitaciones) {
        DefaultTableModel modelo = (DefaultTableModel) tablaHabitaciones.getModel();
        modelo.setRowCount(0);

        for (Habitacion hab : habitaciones) {
            modelo.addRow(new Object[]{
                    hab.getIdHabitacion(),
                    hab.getTipo(),
                    hab.getEstado()
            });
        }
    }

    // Método para cargar habitaciones desde la base de datos
    public void cargarHabitaciones(List<Habitacion> habitaciones) {
        DefaultTableModel modelo = (DefaultTableModel) tablaHabitaciones.getModel();
        modelo.setRowCount(0);

        for (Habitacion hab : habitaciones) {
            modelo.addRow(new Object[]{
                    hab.getIdHabitacion(),
                    hab.getTipo(),
                    hab.getEstado()
            });
        }
    }
    // Método para obtener la habitación seleccionada
    public Habitacion getHabitacionSeleccionada() {
        int fila = tablaHabitaciones.getSelectedRow();
        if (fila == -1) return null;

        return new Habitacion(
                (int) tablaHabitaciones.getValueAt(fila, 0),
                (String) tablaHabitaciones.getValueAt(fila, 1),
                (String) tablaHabitaciones.getValueAt(fila, 2)
        );
    }

    // Método para actualizar una habitación en la vista
    public void actualizarHabitacionEnVista(Habitacion hab) {
        DefaultTableModel modelo = (DefaultTableModel) tablaHabitaciones.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if ((int) modelo.getValueAt(i, 0) == hab.getIdHabitacion()) {
                modelo.setValueAt(hab.getEstado(), i, 2);
                break;
            }
        }
    }

    // Agregar estos listeners en el constructor de VistaReservas
    public void addActualizarEstadoListener(ActionListener listener) {
        btnActualizarEstado.addActionListener(listener);
    }

    public void addRefrescarHabitacionesListener(ActionListener listener) {
        btnRefrescarHabitaciones.addActionListener(listener);
    }

    // --- Métodos públicos para el controlador ---
    public void addCrearReservaListener(ActionListener l)      { btnCrear.addActionListener(l); }
    public void addActualizarReservaListener(ActionListener l) { btnActualizar.addActionListener(l); }
    public void addCancelarReservaListener(ActionListener l)   { btnCancelar.addActionListener(l); }

    public void mostrarReservas(List<Reserva> reservas) {
        modeloReservas.setRowCount(0);
        for (Reserva r : reservas) {
            modeloReservas.addRow(new Object[]{
                    r.getIdReserva(),        // Columna 0: ID Reserva
                    r.getCliente(),         // Columna 1: Nombre Cliente
                    r.getHabitacion(),      // Columna 2: Habitación
                    r.getTipoHabitacion(),  // Columna 3: Tipo de habitación (FALTABA)
                    r.getFechaEntrada(),    // Columna 4: Fecha Entrada
                    r.getFechaSalida(),     // Columna 5: Fecha Salida
                    r.getEstado()           // Columna 6: Estado
            });
        }
    }

    public int getSelectedReservaId() {
        int fila = tablaReservas.getSelectedRow();
        return (fila == -1) ? -1 : (int) tablaReservas.getValueAt(fila, 0);
    }

    public void activarModoEdicion() {
        editMode = true;
        btnActualizar.setText("Guardar Cambios");
        configurarEditoresTabla();  // Aplica los ComboBox a las columnas
        modeloReservas.fireTableDataChanged();
    }

    public void desactivarModoEdicion() {
        editMode = false;
        btnActualizar.setText("Actualizar Reserva");
        modeloReservas.fireTableDataChanged();
    }

    public Reserva getEditedReserva() {
        int fila = tablaReservas.getSelectedRow();
        if (fila == -1) {
            mostrarMensaje("Selecciona una reserva para editar.");
            return null;
        }

        try {
            int idReserva = (int) tablaReservas.getValueAt(fila, 0);

            // Aquí corregimos: obtenemos el Cliente, no un String
            Cliente cliente = (Cliente) tablaReservas.getValueAt(fila, 1);
            String nombreCliente = cliente.getNombre();

            int numeroHabitacion = Integer.parseInt(tablaReservas.getValueAt(fila, 2).toString());
            String tipoHabitacion = tablaReservas.getValueAt(fila, 3).toString();
            String fechaEntrada = tablaReservas.getValueAt(fila, 4).toString();
            String fechaSalida = tablaReservas.getValueAt(fila, 5).toString();
            String estado = tablaReservas.getValueAt(fila, 6).toString();

            // Crear y devolver el nuevo objeto Reserva editado
            Reserva reserva = new Reserva();
            reserva.setIdReserva(idReserva);
            reserva.setNombreCliente(nombreCliente);
            reserva.setHabitacion(String.valueOf(numeroHabitacion));
            reserva.setTipoHabitacion(tipoHabitacion);
            reserva.setFechaEntrada(fechaEntrada);
            reserva.setFechaSalida(fechaSalida);
            reserva.setEstado(estado);

            return reserva;

        } catch (Exception e) {
            mostrarMensaje("Error al obtener los datos editados: " + e.getMessage());
            return null;
        }
    }


    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    // Método para configurar los JComboBox en las columnas de la tabla
    private void configurarEditoresTabla() {
        // ComboBox para Número de Habitación (1-50)
        JComboBox<String> comboNumHabitacion = new JComboBox<>();
        for (int i = 1; i <= 50; i++) {
            comboNumHabitacion.addItem(String.valueOf(i));
        }
        tablaReservas.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboNumHabitacion));
        // ComboBox para Tipo de Habitación (Estándar, Deluxe, Suite)
        JComboBox<String> comboHabitacion = new JComboBox<>(new String[]{"Estándar", "Deluxe", "Suite"});
        tablaReservas.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(comboHabitacion));

        // ComboBox para Estado (Disponible, Ocupada, En limpieza, etc.)
        JComboBox<String> comboEstado = new JComboBox<>(new String[]{
                "Disponible", "Ocupada", "En limpieza",
                "En mantenimiento", "Reservada", "Confirmada"
        });
        tablaReservas.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(comboEstado));
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }



    public void actualizarVistaReservas() {
        // Lógica para actualizar la tabla o vista de reservas
    }

}