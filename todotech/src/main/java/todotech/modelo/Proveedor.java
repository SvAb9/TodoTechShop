package todotech.modelo;

/**
 * Entidad Proveedor del sistema TodoTech Shop.
 * MOD-10 — Gestionar Proveedores.
 * RN: cada producto debe tener al menos un proveedor.
 * RN: no se puede eliminar un proveedor con productos activos asociados.
 */
public class Proveedor {

    private int id;
    private String nombre;
    private String nit;        // identificador único del proveedor
    private String telefono;
    private String correo;
    private boolean activo;

    public Proveedor() {
        this.activo = true;
    }

    public Proveedor(String nombre, String nit, String telefono, String correo) {
        this.nombre   = nombre;
        this.nit      = nit;
        this.telefono = telefono;
        this.correo   = correo;
        this.activo   = true;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nit + " — " + nombre;
    }
}