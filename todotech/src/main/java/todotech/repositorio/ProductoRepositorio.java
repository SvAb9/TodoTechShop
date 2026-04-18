package todotech.repositorio;

import todotech.config.ConexionDB;
import todotech.modelo.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de Producto para Oracle.
 * MOD-02 — Manejar Catálogo de Productos.
 * Solo lectura desde el módulo de ventas (RN-17).
 */
public class ProductoRepositorio {

    private final Connection conexion;

    public ProductoRepositorio() {
        this.conexion = ConexionDB.getInstance().getConexion();
    }

    /** Listar todos los productos activos con stock */
    public List<Producto> listarActivos() throws SQLException {
        String sql = "SELECT * FROM PRODUCTOS WHERE ACTIVO = 1 ORDER BY NOMBRE";
        List<Producto> lista = new ArrayList<>();
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** Buscar producto por código */
    public Producto buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM PRODUCTOS WHERE CODIGO = ? AND ACTIVO = 1";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    /** Buscar por nombre o código (para buscador en formulario) */
    public List<Producto> buscar(String texto) throws SQLException {
        String sql = "SELECT * FROM PRODUCTOS WHERE ACTIVO = 1 AND " +
                     "(UPPER(NOMBRE) LIKE UPPER(?) OR UPPER(CODIGO) LIKE UPPER(?))";
        List<Producto> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** Actualiza el stock después de una venta (RF-10, RN-06) */
    public boolean actualizarStock(int idProducto, int nuevoStock) throws SQLException {
        String sql = "UPDATE PRODUCTOS SET STOCK_DISPONIBLE = ? WHERE ID = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("ID"));
        p.setCodigo(rs.getString("CODIGO"));
        p.setNombre(rs.getString("NOMBRE"));
        p.setDescripcion(rs.getString("DESCRIPCION"));
        p.setPrecio(rs.getDouble("PRECIO"));
        p.setStockDisponible(rs.getInt("STOCK_DISPONIBLE"));
        p.setUbicacionBodega(rs.getString("UBICACION_BODEGA"));
        p.setActivo(rs.getInt("ACTIVO") == 1);
        return p;
    }
}
