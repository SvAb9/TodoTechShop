package todotech.estrategia;

/**
 * Estrategia concreta: pago con tarjeta bancaria o Redcompra.
 * RF-13 — valida con Transbank antes de confirmar.
 * RN-07: debe recibir confirmación externa antes de registrar el pago.
 *
 * En producción: llamaría al SDK de Transbank.
 * En desarrollo: simula la respuesta con un flag.
 */
public class PagoTarjeta implements MetodoPago {

    private String numeroTarjeta;
    private boolean modoSimulacion;

    public PagoTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
        this.modoSimulacion = true; // cambiar a false en producción
    }

    @Override
    public boolean pagar(double monto) {
        if (modoSimulacion) {
            // Simula respuesta de Transbank
            // Tarjetas que terminan en 0000 son rechazadas (para pruebas)
            return !numeroTarjeta.endsWith("0000");
        }
        // TODO: integrar SDK Transbank real (RFC-02)
        return false;
    }

    @Override
    public String getNombre() {
        return "Tarjeta/Redcompra";
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setModoSimulacion(boolean modoSimulacion) {
        this.modoSimulacion = modoSimulacion;
    }
}
