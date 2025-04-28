package orientacion_objetos.modelo;

public class Reserva {
    private int idReserva;
    private Cliente cliente;
    private Habitacion habitaciones;
    private String habitacion;
    private String tipoHabitacion;
    private String fechaEntrada;
    private String fechaSalida;
    private String estado;

    // Constructor sin parámetros
    public Reserva() {
    }

    // Constructor con parámetros
    public Reserva(int idReserva, Cliente cliente,Habitacion habitaciones, String habitacion, String tipoHabitacion, String fechaEntrada, String fechaSalida, String estado) {
        this.idReserva = idReserva;
        this.cliente = cliente;
        this.habitaciones = habitaciones;
        this.habitacion = habitacion;
        this.tipoHabitacion = tipoHabitacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.estado = estado;
    }
    // Getters y setters
    public Habitacion getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(Habitacion habitaciones) {
        this.habitaciones = habitaciones;
    }

    public void setNombreCliente(String nombre) {
        if (this.cliente != null) {
            this.cliente.setNombre(nombre);  // Asumimos que Cliente tiene un setter para nombre
        }
    }

    public String getNombreCliente() {
        return this.cliente != null ? this.cliente.getNombre() : "";  // Devuelve el nombre del cliente
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(String habitacion) {
        this.habitacion = habitacion;
    }

    public String getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(String fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoHabitacion() {
        return tipoHabitacion;
    }
    public void setTipoHabitacion(String tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

}



