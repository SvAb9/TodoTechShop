package todotech.modelo;

/**
 * Línea de detalle dentro de una OrdenVenta.
 * Representa un producto y su cantidad dentro de la orden.
 * MOD-01 — Crear Orden de Venta.
 */
public class DetalleOrden {

    private int id;
    private Producto producto;
    private int cantidad;
    private double precioUnitario; // precio al momento de la venta

    public DetalleOrden() {}

    public DetalleOrden(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
    }

    /** Calcula el subtotal de esta línea */
    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
}
