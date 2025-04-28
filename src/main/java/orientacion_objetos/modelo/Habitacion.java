package orientacion_objetos.modelo;

public class Habitacion {
    private int idHabitacion;
    private String tipo;
    private String estado;

    // Constructor
    public Habitacion(int idHabitacion, String tipo, String estado) {
        this.idHabitacion = idHabitacion;
        this.tipo = tipo;
        this.estado = estado;
    }

    // Getters
    public int getIdHabitacion() {
        return idHabitacion;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    // Setters (añade estos métodos)
    public void setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return idHabitacion + " - " + tipo + " (" + estado + ")";
    }
}