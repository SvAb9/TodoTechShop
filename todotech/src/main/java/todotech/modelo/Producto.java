package todotech.modelo;

/**
 * Entidad Producto del sistema TodoTech Shop.
 * MOD-02 — Manejar Catálogo de Productos.
 * RN-04: no se puede agregar a orden si stock <= 0.
 * RN-05: el stock nunca puede ser negativo.
 */
public class Producto {

    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stockDisponible;
    private String ubicacionBodega;
    private boolean activo;

    public Producto() {
        this.activo = true;
    }

    public Producto(String codigo, String nombre, String descripcion,
                    double precio, int stockDisponible, String ubicacionBodega) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stockDisponible = stockDisponible;
        this.ubicacionBodega = ubicacionBodega;
        this.activo = true;
    }

    /** RN-04: verifica si hay suficiente stock para la cantidad solicitada */
    public boolean tieneStock(int cantidad) {
        return this.stockDisponible >= cantidad;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStockDisponible() { return stockDisponible; }
    public void setStockDisponible(int stockDisponible) { this.stockDisponible = stockDisponible; }

    public String getUbicacionBodega() { return ubicacionBodega; }
    public void setUbicacionBodega(String ubicacionBodega) { this.ubicacionBodega = ubicacionBodega; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return codigo + " — " + nombre + " ($" + precio + ")";
    }
}
