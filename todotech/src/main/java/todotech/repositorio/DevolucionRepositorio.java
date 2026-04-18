package todotech.repositorio;

import todotech.config.ConexionDB;
import todotech.modelo.OrdenVenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de Devoluciones para Oracle.
 * MOD-09 — Gestionar Devoluciones.
 */
public class DevolucionRepositorio {

    private final Connection conexion;

    public DevolucionRepositorio() {
        this.conexion = ConexionDB.getInstance().getConexion();
    }

    /** Registra una solicitud de devolución */
    public boolean registrar(int idOrden, String motivo) throws SQLException {
        String sql = "INSERT INTO DEVOLUCIONES (ID_ORDEN, MOTIVO, ESTADO) VALUES (?, ?, 'PENDIENTE')";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            ps.setString(2, motivo);
            return ps.executeUpdate() > 0;
        }
    }

    /** Aprueba o rechaza una devolución */
    public boolean actualizarEstado(int idDevolucion, String estado) throws SQLException {
        String sql = "UPDATE DEVOLUCIONES SET ESTADO = ? WHERE ID = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idDevolucion);
            return ps.executeUpdate() > 0;
        }
    }

    /** Lista todas las devoluciones pendientes */
    public List<Object[]> listarPendientes() throws SQLException {
        String sql = "SELECT D.ID, D.ID_ORDEN, O.NUMERO_ORDEN, D.MOTIVO, D.FECHA " +
                     "FROM DEVOLUCIONES D JOIN ORDENES O ON D.ID_ORDEN = O.ID " +
                     "WHERE D.ESTADO = 'PENDIENTE' ORDER BY D.FECHA DESC";
        List<Object[]> lista = new ArrayList<>();
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("ID"),
                    rs.getInt("ID_ORDEN"),
                    rs.getString("NUMERO_ORDEN"),
                    rs.getString("MOTIVO"),
                    rs.getTimestamp("FECHA")
                });
            }
        }
        return lista;
    }

    /** Verifica si una orden ya tiene devolución pendiente */
    public boolean tienDevolucionPendiente(int idOrden) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DEVOLUCIONES WHERE ID_ORDEN = ? AND ESTADO = 'PENDIENTE'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }
}
