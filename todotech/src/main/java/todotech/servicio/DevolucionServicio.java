package todotech.servicio;

import todotech.modelo.EstadoOrden;
import todotech.modelo.OrdenVenta;
import todotech.repositorio.DevolucionRepositorio;
import todotech.repositorio.OrdenRepositorio;
import todotech.repositorio.ProductoRepositorio;

import java.util.List;

/**
 * Servicio de lógica de negocio para Devoluciones.
 * MOD-09 — Gestionar Devoluciones.
 *
 * Reglas:
 * - Solo órdenes CERRADAS pueden devolver (RN-09 MOD-09)
 * - Solo el Administrador aprueba
 * - Al aprobar, se reintegra stock automáticamente
 */
public class DevolucionServicio {

    private final DevolucionRepositorio devolucionRepo;
    private final OrdenRepositorio      ordenRepo;
    private final ProductoRepositorio   productoRepo;

    public DevolucionServicio() {
        this.devolucionRepo = new DevolucionRepositorio();
        this.ordenRepo      = new OrdenRepositorio();
        this.productoRepo   = new ProductoRepositorio();
    }

    /**
     * Registra una solicitud de devolución.
     * Solo aplica a órdenes en estado CERRADA.
     */
    public void solicitarDevolucion(String numeroOrden, String motivo) throws Exception {
        if (motivo == null || motivo.isBlank())
            throw new Exception("El motivo de la devolución es obligatorio.");

        OrdenVenta orden = ordenRepo.buscarPorNumero(numeroOrden);
        if (orden == null)
            throw new Exception("No se encontró la orden: " + numeroOrden);

        if (orden.getEstado() != EstadoOrden.CERRADA)
            throw new Exception("Solo se pueden devolver órdenes en estado CERRADA. " +
                                "Estado actual: " + orden.getEstado());

        if (devolucionRepo.tienDevolucionPendiente(orden.getId()))
            throw new Exception("Esta orden ya tiene una solicitud de devolución pendiente.");

        devolucionRepo.registrar(orden.getId(), motivo);
    }

    /**
     * Aprueba una devolución y reintegra el stock.
     * Solo puede ejecutar el Administrador.
     */
    public void aprobarDevolucion(int idDevolucion, int idOrden) throws Exception {
        devolucionRepo.actualizarEstado(idDevolucion, "APROBADA");

        // Cambiar estado de la orden a DEVUELTA
        ordenRepo.actualizarEstado(idOrden, EstadoOrden.DEVUELTA, null);

        // Reintegrar stock de cada producto de la orden
        OrdenVenta orden = ordenRepo.buscarPorId(idOrden);
        if (orden != null && orden.getDetalles() != null) {
            for (var detalle : orden.getDetalles()) {
                var producto = productoRepo.buscarPorCodigo(detalle.getProducto().getCodigo());
                if (producto != null) {
                    int nuevoStock = producto.getStockDisponible() + detalle.getCantidad();
                    productoRepo.actualizarStock(producto.getId(), nuevoStock);
                }
            }
        }
    }

    /**
     * Rechaza una devolución.
     */
    public void rechazarDevolucion(int idDevolucion) throws Exception {
        devolucionRepo.actualizarEstado(idDevolucion, "RECHAZADA");
    }

    /**
     * Lista devoluciones pendientes.
     */
    public List<Object[]> listarPendientes() throws Exception {
        return devolucionRepo.listarPendientes();
    }
}
