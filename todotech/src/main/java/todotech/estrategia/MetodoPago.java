package todotech.estrategia;

/**
 * Interfaz del patrón Strategy para métodos de pago.
 * RF-12: efectivo, tarjeta bancaria, Redcompra o cheque.
 *
 * Quien quiera ser un método de pago debe implementar pagar(monto).
 * ProcesadorPago usa esta interfaz sin saber qué implementación tiene.
 */
public interface MetodoPago {

    /**
     * Procesa el pago por el monto indicado.
     * @param monto total a cobrar
     * @return true si el pago fue aprobado, false si fue rechazado
     */
    boolean pagar(double monto);

    /**
     * Nombre descriptivo del método de pago para mostrar en comprobante.
     */
    String getNombre();
}
