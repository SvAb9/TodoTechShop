package todotech.repositorio;

import todotech.config.ConexionDB;
import todotech.modelo.MovimientoInventario;
import todotech.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de MovimientoInventario para Oracle.
 * MOD-07 — Gestionar Inventario.
 * Solo hace operaciones de base de datos, sin lógica de negocio.
 */
public class MovimientoRepositorio {

    private final Connection conexion;

    public MovimientoRepositorio() {
        this.conexion = ConexionDB.getInstance().getConexion();
    }

    /** Registra un nuevo movimiento de inventario */
    public boolean registrar(MovimientoInventario m) throws SQLException {
        String sql = "INSERT INTO MOVIMIENTOS_INVENTARIO (ID_PRODUCTO, TIPO, CANTIDAD, MOTIVO, USUARIO) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, m.getProducto().getId());
            ps.setString(2, m.getTipo());
            ps.setInt(3, m.getCantidad());
            ps.setString(4, m.getMotivo());
            ps.setString(5, m.getUsuario());
            return ps.executeUpdate() > 0;
        }
    }

    /** Lista todos los movimientos de un producto específico */
    public List<MovimientoInventario> listarPorProducto(int idProducto) throws SQLException {
        String sql = "SELECT m.*, p.NOMBRE AS PROD_NOMBRE, p.CODIGO AS PROD_CODIGO, " +
                     "p.PRECIO, p.STOCK_DISPONIBLE, p.UBICACION_BODEGA, p.ACTIVO " +
                     "FROM MOVIMIENTOS_INVENTARIO m " +
                     "JOIN PRODUCTOS p ON m.ID_PRODUCTO = p.ID " +
                     "WHERE m.ID_PRODUCTO = ? ORDER BY m.FECHA DESC";
        List<MovimientoInventario> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** Lista todos los movimientos ordenados por fecha descendente */
    public List<MovimientoInventario> listarTodos() throws SQLException {
        String sql = "SELECT m.*, p.NOMBRE AS PROD_NOMBRE, p.CODIGO AS PROD_CODIGO, " +
                     "p.PRECIO, p.STOCK_DISPONIBLE, p.UBICACION_BODEGA, p.ACTIVO " +
                     "FROM MOVIMIENTOS_INVENTARIO m " +
                     "JOIN PRODUCTOS p ON m.ID_PRODUCTO = p.ID " +
                     "ORDER BY m.FECHA DESC";
        List<MovimientoInventario> lista = new ArrayList<>();
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** Lista movimientos filtrados por tipo (ENTRADA, SALIDA, AJUSTE) */
    public List<MovimientoInventario> listarPorTipo(String tipo) throws SQLException {
        String sql = "SELECT m.*, p.NOMBRE AS PROD_NOMBRE, p.CODIGO AS PROD_CODIGO, " +
                     "p.PRECIO, p.STOCK_DISPONIBLE, p.UBICACION_BODEGA, p.ACTIVO " +
                     "FROM MOVIMIENTOS_INVENTARIO m " +
                     "JOIN PRODUCTOS p ON m.ID_PRODUCTO = p.ID " +
                     "WHERE m.TIPO = ? ORDER BY m.FECHA DESC";
        List<MovimientoInventario> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** Mapea un ResultSet a MovimientoInventario */
    private MovimientoInventario mapear(ResultSet rs) throws SQLException {
        MovimientoInventario m = new MovimientoInventario();
        m.setId(rs.getInt("ID"));
        m.setTipo(rs.getString("TIPO"));
        m.setCantidad(rs.getInt("CANTIDAD"));
        m.setMotivo(rs.getString("MOTIVO"));
        m.setUsuario(rs.getString("USUARIO"));

        // Convertir Timestamp de Oracle a LocalDateTime
        Timestamp ts = rs.getTimestamp("FECHA");
        if (ts != null) m.setFecha(ts.toLocalDateTime());

        // Mapear producto asociado
        Producto p = new Producto();
        p.setId(rs.getInt("ID_PRODUCTO"));
        p.setCodigo(rs.getString("PROD_CODIGO"));
        p.setNombre(rs.getString("PROD_NOMBRE"));
        p.setPrecio(rs.getDouble("PRECIO"));
        p.setStockDisponible(rs.getInt("STOCK_DISPONIBLE"));
        p.setUbicacionBodega(rs.getString("UBICACION_BODEGA"));
        p.setActivo(rs.getInt("ACTIVO") == 1);
        m.setProducto(p);

        return m;
    }
}