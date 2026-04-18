package todotech.modelo;

/**
 * Entidad Cliente del sistema TodoTech Shop.
 * MOD-08 — Gestionar Clientes (CRUD).
 * RN-01: identificación única por cliente.
 */
public class Cliente {

    private int id;
    private String identificacion; // RN-01: única e inmutable
    private String nombre;
    private String telefono;
    private String correo;
    private boolean activo;

    public Cliente() {
        this.activo = true;
    }

    public Cliente(String identificacion, String nombre, String telefono, String correo) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.activo = true;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return identificacion + " — " + nombre;
    }
}
