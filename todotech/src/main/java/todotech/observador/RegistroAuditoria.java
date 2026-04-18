package todotech.observador;

import todotech.modelo.OrdenVenta;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Observador concreto — Registro de Auditoría.
 * MOD-05 — Gestionar Registro Histórico.
 * Patrón Observer: se notifica automáticamente cuando una OrdenVenta
 * cambia de estado y registra el evento en consola/log del sistema.
 * RF-21: registrar cada cambio de estado de una orden.
 * RF-22: trazabilidad completa del ciclo de vida de una orden.
 *
 * En producción: reemplazar System.out por escritura a tabla AUDITORIA o archivo.
 */
public class RegistroAuditoria implements ObservadorOrden {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void actualizar(OrdenVenta orden) {
        String timestamp = LocalDateTime.now().format(FMT);
        String cliente   = orden.getCliente() != null
            ? orden.getCliente().getNombre() : "Desconocido";
        String medioPago = orden.getMedioPago() != null
            ? " | Pago: " + orden.getMedioPago() : "";

        String log = String.format(
            "[AUDITORIA] %s | Orden: %s | Estado: %s | Cliente: %s | Total: $%.2f%s",
            timestamp,
            orden.getNumeroOrden(),
            orden.getEstado().name(),
            cliente,
            orden.getTotal(),
            medioPago
        );

        // Imprime en consola — en producción escribir a BD o archivo de log
        System.out.println(log);

        // TODO producción: guardar en tabla AUDITORIA_ORDENES con:
        // INSERT INTO AUDITORIA_ORDENES (ID_ORDEN, ESTADO_NUEVO, FECHA, DETALLE)
        // VALUES (orden.getId(), orden.getEstado().name(), SYSTIMESTAMP, log)
    }
}