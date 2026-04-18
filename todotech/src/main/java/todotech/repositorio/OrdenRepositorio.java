package todotech.repositorio;

import todotech.config.ConexionDB;
import todotech.modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de OrdenVenta para Oracle.
 * MOD-01 — Crear Orden de Venta.
 */
public class OrdenRepositorio {

    private final Connection conexion;
    private final ClienteRepositorio clienteRepo;
    private final ProductoRepositorio productoRepo;

    public OrdenRepositorio() {
        this.conexion     = ConexionDB.getInstance().getConexion();
        this.clienteRepo  = new ClienteRepositorio();
        this.productoRepo = new ProductoRepositorio();
    }

    /** Guarda una orden y sus detalles en una transacción */
    public boolean guardar(OrdenVenta orden) throws SQLException {
        conexion.setAutoCommit(false);
        try {
            // Insertar cabecera de la orden
            String sqlOrden = "INSERT INTO ORDENES (NUMERO_ORDEN, ID_CLIENTE, CLAVE_SECRETA, TOTAL, ESTADO) " +
                              "VALUES (?, ?, ?, ?, ?)";
            int idOrden;
            try (PreparedStatement ps = conexion.prepareStatement(sqlOrden,
                    new String[]{"ID"})) {
                ps.setString(1, orden.getNumeroOrden());
                ps.setInt(2, orden.getCliente().getId());
                ps.setString(3, orden.getClaveSecreta());
                ps.setDouble(4, orden.getTotal());
                ps.setString(5, orden.getEstado().name());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                idOrden = rs.getInt(1);
                orden.setId(idOrden);
            }

            // Insertar detalles
            String sqlDetalle = "INSERT INTO DETALLE_ORDEN (ID_ORDEN, ID_PRODUCTO, CANTIDAD, PRECIO_UNITARIO) " +
                                "VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conexion.prepareStatement(sqlDetalle)) {
                for (DetalleOrden d : orden.getDetalles()) {
                    ps.setInt(1, idOrden);
                    ps.setInt(2, d.getProducto().getId());
                    ps.setInt(3, d.getCantidad());
                    ps.setDouble(4, d.getPrecioUnitario());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conexion.commit();
            return true;
        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
    }

    /** Actualiza el estado de una orden */
    public boolean actualizarEstado(int idOrden, EstadoOrden estado, String medioPago) throws SQLException {
        String sql = "UPDATE ORDENES SET ESTADO = ?, MEDIO_PAGO = ? WHERE ID = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setString(2, medioPago);
            ps.setInt(3, idOrden);
            return ps.executeUpdate() > 0;
        }
    }

    /** Genera el siguiente número de orden usando la secuencia Oracle */
    public String generarNumeroOrden() throws SQLException {
        String sql = "SELECT 'ORD-' || LPAD(SEQ_NUMERO_ORDEN.NEXTVAL, 6, '0') AS NUMERO FROM DUAL";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getString("NUMERO");
        }
        return "ORD-000001";
    }

    /** Busca una orden por ID */
    public OrdenVenta buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM ORDENES WHERE ID = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    /** Busca una orden por número */
    public OrdenVenta buscarPorNumero(String numeroOrden) throws SQLException {
        String sql = "SELECT * FROM ORDENES WHERE NUMERO_ORDEN = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, numeroOrden);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    /** Lista órdenes por estado */
    public List<OrdenVenta> listarPorEstado(EstadoOrden estado) throws SQLException {
        String sql = "SELECT * FROM ORDENES WHERE ESTADO = ? ORDER BY FECHA_CREACION DESC";
        List<OrdenVenta> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private OrdenVenta mapear(ResultSet rs) throws SQLException {
        OrdenVenta o = new OrdenVenta();
        o.setId(rs.getInt("ID"));
        o.setNumeroOrden(rs.getString("NUMERO_ORDEN"));
        o.setClaveSecreta(rs.getString("CLAVE_SECRETA"));
        o.setTotal(rs.getDouble("TOTAL"));
        o.setEstado(EstadoOrden.valueOf(rs.getString("ESTADO")));
        o.setMedioPago(rs.getString("MEDIO_PAGO"));
        // Cargar cliente
        try {
            Cliente c = clienteRepo.buscarPorId(rs.getInt("ID_CLIENTE"));
            o.setCliente(c);
        } catch (Exception ignored) {}
        return o;
    }
}
