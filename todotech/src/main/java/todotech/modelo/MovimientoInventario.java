package todotech.modelo;

import java.time.LocalDateTime;

/**
 * Entidad MovimientoInventario del sistema TodoTech Shop.
 * MOD-07 — Gestionar Inventario.
 * Registra cada entrada, salida o ajuste de stock de un producto.
 */
public class MovimientoInventario {

    private int id;
    private Producto producto;
    private String tipo;           // ENTRADA, SALIDA, AJUSTE
    private int cantidad;
    private String motivo;
    private String usuario;
    private LocalDateTime fecha;

    public MovimientoInventario() {}

    public MovimientoInventario(Producto producto, String tipo, int cantidad, String motivo, String usuario) {
        this.producto = producto;
        this.tipo     = tipo;
        this.cantidad = cantidad;
        this.motivo   = motivo;
        this.usuario  = usuario;
        this.fecha    = LocalDateTime.now();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return tipo + " | " + cantidad + " uds | " + motivo;
    }
}