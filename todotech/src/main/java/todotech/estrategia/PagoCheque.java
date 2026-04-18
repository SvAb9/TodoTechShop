package todotech.estrategia;

/**
 * Estrategia concreta: pago con cheque.
 * RF-14 — valida con Orsan antes de confirmar.
 * RN-07: confirmación externa obligatoria.
 * RFC-03: si Orsan no responde en 5 seg, fallback a validación manual.
 */
public class PagoCheque implements MetodoPago {

    private String numeroCheque;
    private boolean aprobadoManualmente;
    private boolean modoSimulacion;

    public PagoCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
        this.aprobadoManualmente = false;
        this.modoSimulacion = true;
    }

    @Override
    public boolean pagar(double monto) {
        if (modoSimulacion) {
            // Cheques que terminan en 999 son rechazados (para pruebas)
            if (numeroCheque.endsWith("999")) return false;
            return true;
        }
        // TODO: integrar API Orsan (RFC-03)
        // Si timeout > 5s, activar fallback a validación manual
        return aprobadoManualmente;
    }

    @Override
    public String getNombre() {
        return "Cheque";
    }

    /** Fallback RFC-03: cajero aprueba manualmente si Orsan no responde */
    public void aprobarManualmente() {
        this.aprobadoManualmente = true;
    }

    public String getNumeroCheque() {
        return numeroCheque;
    }
}
