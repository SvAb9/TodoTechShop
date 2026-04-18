package todotech.observador;

import todotech.modelo.OrdenVenta;

/**
 * Interfaz del patrón Observer para cambios de estado en OrdenVenta.
 * Cualquier módulo que necesite reaccionar a un cambio de estado
 * debe implementar esta interfaz.
 */
public interface ObservadorOrden {

    /**
     * Se llama automáticamente cuando una OrdenVenta cambia de estado.
     * @param orden la orden que cambió de estado
     */
    void actualizar(OrdenVenta orden);
}
