package todotech.servicio;

import todotech.modelo.EstadoOrden;
import todotech.modelo.OrdenVenta;
import todotech.repositorio.OrdenRepositorio;

/**
 * Servicio de lógica de negocio para Despacho.
 * MOD-04 — Gestionar Despacho.
 * RF-17: buscar orden por número y clave secreta para despachar.
 * RF-20: confirmar despacho — cambia estado a CERRADA y notifica observadores.
 */
public class DespachoServicio {

    private final OrdenRepositorio ordenRepo = new OrdenRepositorio();

    /**
     * Busca una orden lista para despachar.
     * Valida: que exista, que la clave coincida, y que esté en estado PAGADA.
     * RF-17 — RN-08: solo órdenes PAGADAS pueden despacharse.
     */
    public OrdenVenta buscarParaDespacho(String numeroOrden, String claveSecreta) throws Exception {
        if (numeroOrden == null || numeroOrden.isBlank())
            throw new Exception("El número de orden es obligatorio.");
        if (claveSecreta == null || claveSecreta.isBlank())
            throw new Exception("La clave secreta es obligatoria.");

        OrdenVenta orden = ordenRepo.buscarPorNumero(numeroOrden.trim().toUpperCase());
        if (orden == null)
            throw new Exception("No se encontró la orden: " + numeroOrden);

        // Validar clave secreta
        if (!claveSecreta.trim().equals(orden.getClaveSecreta()))
            throw new Exception("Clave secreta incorrecta.");

        // RF-17: solo órdenes PAGADAS se pueden despachar
        if (orden.getEstado() != EstadoOrden.PAGADA)
            throw new Exception("Solo se pueden despachar órdenes en estado PAGADA. " +
                                "Estado actual: " + orden.getEstado());

        return orden;
    }

    /**
     * Confirma el despacho de una orden.
     * Cambia su estado a CERRADA y notifica a los observadores (Observer).
     * RF-20.
     */
    public void confirmarDespacho(OrdenVenta orden) throws Exception {
        if (orden == null) throw new Exception("La orden no puede ser nula.");
        if (orden.getEstado() != EstadoOrden.PAGADA)
            throw new Exception("Solo se pueden despachar órdenes en estado PAGADA.");

        // Cambia estado y notifica observadores (ej: RegistroAuditoria, MarcadorBatch)
        orden.marcarComoCerrada();

        // Persistir el nuevo estado en BD
        boolean ok = ordenRepo.actualizarEstado(orden.getId(), EstadoOrden.CERRADA, orden.getMedioPago());
        if (!ok) throw new Exception("No se pudo actualizar el estado de la orden en la base de datos.");
    }
}