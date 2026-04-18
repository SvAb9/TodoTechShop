package todotech.modelo;

import todotech.observador.ObservadorOrden;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad central del sistema TodoTech Shop.
 * MOD-01 — Crear Orden de Venta.
 * Implementa el patrón Observer: notifica a sus observadores
 * cada vez que cambia de estado.
 *
 * Estados: CREADA → PAGADA → CERRADA
 *          CREADA → CANCELADA
 *          CERRADA → DEVUELTA
 */
public class OrdenVenta {

    private int id;
    private String numeroOrden;        // RN-16: único, secuencial, inmutable
    private Cliente cliente;           // RN-01: un cliente por orden
    private String claveSecreta;       // RF-02: clave para validar despacho
    private List<DetalleOrden> detalles;
    private double total;              // RN-02: calculado automáticamente
    private EstadoOrden estado;
    private LocalDateTime fechaCreacion;
    private String medioPago;

    // Observadores — patrón Observer
    private final List<ObservadorOrden> observadores = new ArrayList<>();

    public OrdenVenta() {
        this.detalles = new ArrayList<>();
        this.estado = EstadoOrden.CREADA;
        this.fechaCreacion = LocalDateTime.now();
    }

    // ─── Patrón Observer ───────────────────────────────────────────────

    public void agregarObservador(ObservadorOrden observador) {
        observadores.add(observador);
    }

    public void removerObservador(ObservadorOrden observador) {
        observadores.remove(observador);
    }

    /** Notifica a todos los observadores del cambio de estado */
    private void notificarObservadores() {
        for (ObservadorOrden obs : observadores) {
            obs.actualizar(this);
        }
    }

    // ─── Cambios de estado ─────────────────────────────────────────────

    /** RF-16: cambia a PAGADA y notifica */
    public void marcarComoPagada(String medioPago) {
        this.estado = EstadoOrden.PAGADA;
        this.medioPago = medioPago;
        notificarObservadores();
    }

    /** RF-20: cambia a CERRADA y notifica */
    public void marcarComoCerrada() {
        this.estado = EstadoOrden.CERRADA;
        notificarObservadores();
    }

    /** RF-24: cancela la orden y notifica */
    public void cancelar() {
        this.estado = EstadoOrden.CANCELADA;
        notificarObservadores();
    }

    /** MOD-09: devuelve la orden y notifica */
    public void marcarComoDevuelta() {
        this.estado = EstadoOrden.DEVUELTA;
        notificarObservadores();
    }

    // ─── Cálculo de total ──────────────────────────────────────────────

    /** RN-02: el total se calcula automáticamente, nunca manualmente */
    public void recalcularTotal() {
        this.total = detalles.stream()
            .mapToDouble(DetalleOrden::getSubtotal)
            .sum();
    }

    // ─── Getters y Setters ─────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getClaveSecreta() { return claveSecreta; }
    public void setClaveSecreta(String claveSecreta) { this.claveSecreta = claveSecreta; }

    public List<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrden> detalles) { this.detalles = detalles; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getMedioPago() { return medioPago; }
    public void setMedioPago(String medioPago) { this.medioPago = medioPago; }

    @Override
    public String toString() {
        return numeroOrden + " — " + cliente.getNombre() + " — " + estado;
    }
}
