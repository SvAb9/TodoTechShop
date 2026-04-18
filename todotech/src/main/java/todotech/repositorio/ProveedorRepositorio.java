package todotech.repositorio;

import todotech.config.ConexionDB;
import todotech.modelo.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio CRUD de Proveedor para Oracle.
 * MOD-10 — Gestionar Proveedores.
 * Solo hace operaciones de base de datos, sin lógica de negocio.
 * Requiere ejecutar primero en BD:
 *   CREATE TABLE PROVEEDORES ...
 *   CREATE TABLE PRODUCTO_PROVEEDOR ...
 * (ver database.sql actualizado)
 */
public class ProveedorRepositorio {

    private final Connection conexion;

    public ProveedorRepositorio() {
        this.conexion = ConexionDB.getInstance().getConexion();
    }

    /** CREATE */
    public boolean crear(Proveedor p) throws SQLException {
        String sql = "INSERT INTO PROVEEDORES (NOMBRE, NIT, TELEFONO, CORREO, ACTIVO) VALUES (?,?,?,?,1)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getNit());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getCorreo());
            return ps.executeUpdate() > 0;
        }
    }

    /** READ — por NIT */
    public Proveedor buscarPorNit(String nit) throws SQLException {
        String sql = "SELECT * FROM PROVEEDORES WHERE NIT = ? AND ACTIVO = 1";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nit);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    /** READ — por ID */
    public Proveedor buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM PROVEEDORES WHERE ID = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    /** READ — listar activos */
    public List<Proveedor> listarActivos() throws SQLException {
        String sql = "SELECT * FROM PROVEEDORES WHERE ACTIVO = 1 ORDER BY NOMBRE";
        List<Proveedor> lista = new ArrayList<>();
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** READ — buscar por nombre */
    public List<Proveedor> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM PROVEEDORES WHERE UPPER(NOMBRE) LIKE UPPER(?) AND ACTIVO = 1";
        List<Proveedor> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** UPDATE */
    public boolean actualizar(Proveedor p) throws SQLException {
        String sql = "UPDATE PROVEEDORES SET NOMBRE=?, TELEFONO=?, CORREO=? WHERE ID=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTelefono());
            ps.setString(3, p.getCorreo());
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** DELETE lógico */
    public boolean desactivar(int id) throws SQLException {
        String sql = "UPDATE PROVEEDORES SET ACTIVO = 0 WHERE ID = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Verifica si el NIT ya existe */
    public boolean existeNit(String nit) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PROVEEDORES WHERE NIT = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nit);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    /** Verifica si el proveedor tiene productos activos asociados */
    public boolean tieneProductosActivos(int idProveedor) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PRODUCTO_PROVEEDOR pp " +
                     "JOIN PRODUCTOS pr ON pp.ID_PRODUCTO = pr.ID " +
                     "WHERE pp.ID_PROVEEDOR = ? AND pr.ACTIVO = 1";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    /** Asocia un proveedor a un producto */
    public boolean asociarProducto(int idProducto, int idProveedor) throws SQLException {
        String sql = "INSERT INTO PRODUCTO_PROVEEDOR (ID_PRODUCTO, ID_PROVEEDOR) VALUES (?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setInt(2, idProveedor);
            return ps.executeUpdate() > 0;
        }
    }

    private Proveedor mapear(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setId(rs.getInt("ID"));
        p.setNombre(rs.getString("NOMBRE"));
        p.setNit(rs.getString("NIT"));
        p.setTelefono(rs.getString("TELEFONO"));
        p.setCorreo(rs.getString("CORREO"));
        p.setActivo(rs.getInt("ACTIVO") == 1);
        return p;
    }
}