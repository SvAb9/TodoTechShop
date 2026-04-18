package todotech.servicio;

import todotech.modelo.EstadoOrden;
import todotech.modelo.MovimientoInventario;
import todotech.modelo.OrdenVenta;
import todotech.modelo.Producto;
import todotech.observador.ObservadorOrden;
import todotech.repositorio.MovimientoRepositorio;
import todotech.repositorio.ProductoRepositorio;

import java.util.List;

/**
 * Servicio de lógica de negocio para Inventario.
 * MOD-07 — Gestionar Inventario.
 * Implementa ObservadorOrden (patrón Observer): cuando una orden pasa a
 * PAGADA, descuenta el stock automáticamente sin que OrdenVenta lo sepa.
 * RF-25: registrar entrada de stock.
 * RF-26: registrar ajuste de inventario.
 * RF-27: descontar stock automáticamente al pagar orden.
 * RN-05: el stock nunca puede ser negativo.
 */
public class InventarioServicio implements ObservadorOrden {

    private final MovimientoRepositorio movRepo      = new MovimientoRepositorio();
    private final ProductoRepositorio   productoRepo = new ProductoRepositorio();

    // ─── RF-25: Registrar entrada de stock ────────────────────────────

    /**
     * Registra una entrada de stock para un producto.
     * Actualiza STOCK_DISPONIBLE en la tabla PRODUCTOS.
     */
    public void registrarEntrada(Producto producto, int cantidad, String motivo, String usuario) throws Exception {
        validarCantidad(cantidad);
        if (motivo == null || motivo.isBlank()) throw new Exception("El motivo es obligatorio.");
        if (usuario == null || usuario.isBlank()) throw new Exception("El usuario es obligatorio.");

        // Actualizar stock en BD
        int nuevoStock = producto.getStockDisponible() + cantidad;
        productoRepo.actualizarStock(producto.getId(), nuevoStock);

        // Registrar movimiento
        MovimientoInventario m = new MovimientoInventario(producto, "ENTRADA", cantidad, motivo, usuario);
        movRepo.registrar(m);
    }

    // ─── RF-26: Registrar salida manual de stock ──────────────────────

    /**
     * Registra una salida manual de stock (ej: producto dañado, pérdida).
     * RN-05: el stock no puede quedar negativo.
     */
    public void registrarSalida(Producto producto, int cantidad, String motivo, String usuario) throws Exception {
        validarCantidad(cantidad);
        if (motivo == null || motivo.isBlank()) throw new Exception("El motivo es obligatorio.");

        // RN-05: verificar que el stock no quede negativo
        if (producto.getStockDisponible() < cantidad) {
            throw new Exception("Stock insuficiente. Disponible: " + producto.getStockDisponible());
        }

        int nuevoStock = producto.getStockDisponible() - cantidad;
        productoRepo.actualizarStock(producto.getId(), nuevoStock);

        MovimientoInventario m = new MovimientoInventario(producto, "SALIDA", cantidad, motivo, usuario);
        movRepo.registrar(m);
    }

    // ─── Ajuste de inventario ─────────────────────────────────────────

    /**
     * Ajusta el stock a una cantidad exacta (ej: tras conteo físico).
     * Registra la diferencia como AJUSTE.
     * RN-05: la nueva cantidad no puede ser negativa.
     */
    public void registrarAjuste(Producto producto, int nuevaCantidad, String motivo, String usuario) throws Exception {
        if (nuevaCantidad < 0) throw new Exception("El stock ajustado no puede ser negativo.");
        if (motivo == null || motivo.isBlank()) throw new Exception("El motivo del ajuste es obligatorio.");

        int diferencia = nuevaCantidad - producto.getStockDisponible();
        productoRepo.actualizarStock(producto.getId(), nuevaCantidad);

        String motivoCompleto = motivo + " (ajuste: " + (diferencia >= 0 ? "+" : "") + diferencia + ")";
        MovimientoInventario m = new MovimientoInventario(producto, "AJUSTE", Math.abs(diferencia), motivoCompleto, usuario);
        movRepo.registrar(m);
    }

    // ─── RF-27: Consultar movimientos ─────────────────────────────────

    public List<MovimientoInventario> listarTodos() throws Exception {
        return movRepo.listarTodos();
    }

    public List<MovimientoInventario> listarPorProducto(int idProducto) throws Exception {
        return movRepo.listarPorProducto(idProducto);
    }

    public List<MovimientoInventario> listarPorTipo(String tipo) throws Exception {
        return movRepo.listarPorTipo(tipo);
    }

    // ─── Patrón Observer ──────────────────────────────────────────────

    /**
     * Se ejecuta automáticamente cuando una OrdenVenta cambia de estado.
     * Si el nuevo estado es PAGADA, descuenta el stock de cada producto.
     * RF-27 — RN-05: el stock nunca queda negativo.
     */
    @Override
    public void actualizar(OrdenVenta orden) {
        if (orden.getEstado() != EstadoOrden.PAGADA) return;

        orden.getDetalles().forEach(detalle -> {
            try {
                Producto p = productoRepo.buscarPorCodigo(detalle.getProducto().getCodigo());
                if (p == null) return;

                // RN-05: protección extra contra stock negativo
                int nuevoStock = Math.max(0, p.getStockDisponible() - detalle.getCantidad());
                productoRepo.actualizarStock(p.getId(), nuevoStock);

                MovimientoInventario m = new MovimientoInventario(
                    p, "SALIDA", detalle.getCantidad(),
                    "Venta orden " + orden.getNumeroOrden(),
                    "SISTEMA"
                );
                movRepo.registrar(m);

            } catch (Exception e) {
                System.err.println("[InventarioServicio] Error descontando stock de "
                    + detalle.getProducto().getNombre() + ": " + e.getMessage());
            }
        });
    }

    // ─── Helper privado ───────────────────────────────────────────────

    private void validarCantidad(int cantidad) throws Exception {
        if (cantidad <= 0) throw new Exception("La cantidad debe ser mayor a 0.");
    }
}