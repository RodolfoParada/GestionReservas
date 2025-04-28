package orientacion_objetos.modelo;

public class Cliente {
    private int idCliente;
    private String nombre;
    private String contacto;
    private String preferencias;

    // Constructor vacío
    public Cliente() {
    }

    // Constructor modificado para aceptar idCliente y nombre
    public Cliente(int idCliente, String nombre) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.contacto = "";  // Valor predeterminado
        this.preferencias = "";  // Valor predeterminado
    }
    // Constructor que acepta idCliente, nombre, contacto y preferencias
    public Cliente(int idCliente, String nombre, String contacto, String preferencias) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.contacto = contacto;
        this.preferencias = preferencias;
    }

    // Getters y Setters
    public int getIdCliente(){
        return idCliente;
    }
    public void setIdCliente(int idCliente){
        this.idCliente = idCliente;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getContacto() {
        return contacto;
    }
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
    public String getPreferencias() {
        return preferencias;
    }
    public void setPreferencias(String preferencias) {
        this.preferencias = preferencias;
    }
    @Override
    public String toString() {
        return nombre;
    }

}
