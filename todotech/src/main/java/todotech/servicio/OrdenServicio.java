package todotech.servicio;

import todotech.modelo.*;
import todotech.repositorio.OrdenRepositorio;
import todotech.repositorio.ProductoRepositorio;
import todotech.estrategia.MetodoPago;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio de lógica de negocio para OrdenVenta.
 * MOD-01 — Crear Orden de Venta.
 * MOD-03 — Procesar Métodos de Pago.
 */
public class OrdenServicio {

    private final OrdenRepositorio ordenRepo;
    private final ProductoRepositorio productoRepo;

    public OrdenServicio() {
        this.ordenRepo    = new OrdenRepositorio();
        this.productoRepo = new ProductoRepositorio();
    }

    /**
     * Crea una nueva orden vacía para un cliente.
     * RF-01, RF-02: genera número único automático.
     */
    public OrdenVenta iniciarOrden(Cliente cliente, String claveSecreta) throws Exception {
        if (cliente == null) throw new Exception("Debe seleccionar un cliente.");
        if (claveSecreta == null || claveSecreta.isBlank())
            throw new Exception("La clave secreta es obligatoria.");

        OrdenVenta orden = new OrdenVenta();
        orden.setCliente(cliente);
        orden.setClaveSecreta(claveSecreta);
        orden.setNumeroOrden(ordenRepo.generarNumeroOrden()); // RN-16
        return orden;
    }

    /**
     * Agrega un producto a la orden validando stock.
     * RF-05, RF-08 — RN-04: no agregar si stock insuficiente.
     */
    public void agregarProducto(OrdenVenta orden, Producto producto, int cantidad) throws Exception {
        if (cantidad <= 0) throw new Exception("La cantidad debe ser mayor a cero.");

        // RN-04: verificar stock real en ese momento (RN-18: no datos desactualizados)
        Producto actual = productoRepo.buscarPorCodigo(producto.getCodigo());
        if (actual == null) throw new Exception("Producto no encontrado.");
        if (!actual.tieneStock(cantidad))
            throw new Exception("Stock insuficiente. Disponible: " + actual.getStockDisponible());

        // Si ya está en la orden, actualizar cantidad
        for (DetalleOrden d : orden.getDetalles()) {
            if (d.getProducto().getId() == producto.getId()) {
                int nuevaCantidad = d.getCantidad() + cantidad;
                if (!actual.tieneStock(nuevaCantidad))
                    throw new Exception("Stock insuficiente para la cantidad total. Disponible: " + actual.getStockDisponible());
                d.setCantidad(nuevaCantidad);
                orden.recalcularTotal(); // RN-02
                return;
            }
        }

        // Agregar nuevo detalle
        orden.getDetalles().add(new DetalleOrden(producto, cantidad));
        orden.recalcularTotal(); // RN-02
    }

    /**
     * Elimina un producto de la orden.
     * RF-05 — modificar antes de completar.
     */
    public void eliminarProducto(OrdenVenta orden, int indice) throws Exception {
        if (indice < 0 || indice >= orden.getDetalles().size())
            throw new Exception("Producto no encontrado en la orden.");
        orden.getDetalles().remove(indice);
        orden.recalcularTotal();
    }

    /**
     * Confirma y guarda la orden en BD.
     * RF-04, RN-03: valida campos obligatorios antes de guardar.
     */
    public void confirmarOrden(OrdenVenta orden) throws Exception {
        // RN-03: validar campos obligatorios
        if (orden.getCliente() == null) throw new Exception("La orden no tiene cliente asignado.");
        if (orden.getDetalles().isEmpty()) throw new Exception("La orden no tiene productos.");
        if (orden.getClaveSecreta() == null || orden.getClaveSecreta().isBlank())
            throw new Exception("La clave secreta es obligatoria.");

        orden.setEstado(EstadoOrden.CREADA);
        boolean guardado = ordenRepo.guardar(orden);
        if (!guardado) throw new Exception("No se pudo guardar la orden.");
    }

    /**
     * Procesa el pago de una orden usando el patrón Strategy.
     * RF-12, RF-13, RF-14, RF-16.
     * RN-07: confirmación externa antes de registrar el pago.
     */
    public void procesarPago(OrdenVenta orden, MetodoPago estrategia) throws Exception {
        if (orden.getEstado() != EstadoOrden.CREADA)
            throw new Exception("Solo se pueden pagar órdenes en estado CREADA.");

        // Strategy: delega el pago a la estrategia sin saber cuál es
        boolean aprobado = estrategia.pagar(orden.getTotal());
        if (!aprobado)
            throw new Exception("Pago rechazado por " + estrategia.getNombre() + ".");

        // Actualizar estado en BD
        orden.marcarComoPagada(estrategia.getNombre()); // notifica observadores
        ordenRepo.actualizarEstado(orden.getId(), EstadoOrden.PAGADA, estrategia.getNombre());

        // RN-06: descontar stock automáticamente
        for (DetalleOrden d : orden.getDetalles()) {
            Producto p = productoRepo.buscarPorCodigo(d.getProducto().getCodigo());
            int nuevoStock = p.getStockDisponible() - d.getCantidad();
            productoRepo.actualizarStock(p.getId(), nuevoStock);
        }
    }

    /** Busca una orden por número para el cajero */
    public OrdenVenta buscarPorNumero(String numero) throws Exception {
        if (numero == null || numero.isBlank())
            throw new Exception("Ingrese un número de orden.");
        OrdenVenta orden = ordenRepo.buscarPorNumero(numero);
        if (orden == null) throw new Exception("No se encontró la orden: " + numero);
        return orden;
    }

    /** Lista órdenes por estado */
    public List<OrdenVenta> listarPorEstado(EstadoOrden estado) throws Exception {
        return ordenRepo.listarPorEstado(estado);
    }
}
