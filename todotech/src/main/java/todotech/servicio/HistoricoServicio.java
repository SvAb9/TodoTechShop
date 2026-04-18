package todotech.servicio;

import todotech.modelo.EstadoOrden;
import todotech.modelo.OrdenVenta;
import todotech.repositorio.OrdenRepositorio;

import java.util.List;

/**
 * Servicio de lógica de negocio para el Registro Histórico.
 * MOD-05 — Gestionar Registro Histórico.
 * RF-21: consultar historial de órdenes con filtros.
 * RF-22: ver detalle completo de una orden específica.
 */
public class HistoricoServicio {

    private final OrdenRepositorio ordenRepo = new OrdenRepositorio();

    /**
     * Lista todas las órdenes en un estado específico.
     * Si estadoStr es null o "TODOS", retorna todas las órdenes.
     * RF-21.
     */
    public List<OrdenVenta> listarPorEstado(String estadoStr) throws Exception {
        if (estadoStr == null || estadoStr.isBlank() || "TODOS".equals(estadoStr)) {
            // Traer todas: unir los resultados de cada estado
            return listarTodas();
        }
        try {
            EstadoOrden estado = EstadoOrden.valueOf(estadoStr.toUpperCase());
            return ordenRepo.listarPorEstado(estado);
        } catch (IllegalArgumentException e) {
            throw new Exception("Estado de orden no válido: " + estadoStr);
        }
    }

    /**
     * Retorna todas las órdenes sin importar el estado.
     */
    public List<OrdenVenta> listarTodas() throws Exception {
        // Acumula órdenes de todos los estados posibles
        List<OrdenVenta> todas = new java.util.ArrayList<>();
        for (EstadoOrden estado : EstadoOrden.values()) {
            todas.addAll(ordenRepo.listarPorEstado(estado));
        }
        // Ordena por número de orden descendente (más reciente primero)
        todas.sort((a, b) -> b.getNumeroOrden().compareTo(a.getNumeroOrden()));
        return todas;
    }

    /**
     * Busca una orden específica por su número.
     * RF-22: ver detalle completo.
     */
    public OrdenVenta buscarPorNumero(String numeroOrden) throws Exception {
        if (numeroOrden == null || numeroOrden.isBlank())
            throw new Exception("Ingrese un número de orden.");
        OrdenVenta orden = ordenRepo.buscarPorNumero(numeroOrden.trim().toUpperCase());
        if (orden == null)
            throw new Exception("No se encontró la orden: " + numeroOrden);
        return orden;
    }
}