package todotech.estrategia;

/**
 * Estrategia concreta: pago en efectivo.
 * RF-12 — opción efectivo del menú de pago.
 */
public class PagoEfectivo implements MetodoPago {

    private double montoBrindado; // dinero que entrega el cliente
    private double cambio;

    public PagoEfectivo(double montoBrindado) {
        this.montoBrindado = montoBrindado;
    }

    @Override
    public boolean pagar(double monto) {
        if (montoBrindado < monto) {
            return false; // dinero insuficiente
        }
        this.cambio = montoBrindado - monto;
        return true;
    }

    @Override
    public String getNombre() {
        return "Efectivo";
    }

    public double getCambio() {
        return cambio;
    }

    public double getMontoBrindado() {
        return montoBrindado;
    }
}
