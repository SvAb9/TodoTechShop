package todotech.repositorio;

import todotech.config.ConexionDB;
import todotech.modelo.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio CRUD de Cliente para Oracle.
 * MOD-08 — Gestionar Clientes.
 * Solo hace operaciones de base de datos, sin lógica de negocio.
 */
public class ClienteRepositorio {

    private final Connection conexion;

    public ClienteRepositorio() {
        this.conexion = ConexionDB.getInstance().getConexion();
    }

    /** CREATE — Registrar nuevo cliente */
    public boolean crear(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO CLIENTES (IDENTIFICACION, NOMBRE, TELEFONO, CORREO, ACTIVO) " +
                     "VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cliente.getIdentificacion());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getCorreo());
            return ps.executeUpdate() > 0;
        }
    }

    /** READ — Buscar cliente por identificación */
    public Cliente buscarPorIdentificacion(String identificacion) throws SQLException {
        String sql = "SELECT * FROM CLIENTES WHERE IDENTIFICACION = ? AND ACTIVO = 1";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    /** READ — Buscar cliente por ID */
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM CLIENTES WHERE ID = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    /** READ — Listar todos los clientes activos */
    public List<Cliente> listarActivos() throws SQLException {
        String sql = "SELECT * FROM CLIENTES WHERE ACTIVO = 1 ORDER BY NOMBRE";
        List<Cliente> lista = new ArrayList<>();
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** READ — Buscar por nombre (para autocompletar en formulario) */
    public List<Cliente> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM CLIENTES WHERE UPPER(NOMBRE) LIKE UPPER(?) AND ACTIVO = 1";
        List<Cliente> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** UPDATE — Actualizar datos del cliente */
    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE CLIENTES SET NOMBRE = ?, TELEFONO = ?, CORREO = ? WHERE ID = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getCorreo());
            ps.setInt(4, cliente.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** DELETE lógico — No elimina, desactiva. RN: no eliminar si tiene órdenes activas */
    public boolean desactivar(int id) throws SQLException {
        String sql = "UPDATE CLIENTES SET ACTIVO = 0 WHERE ID = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Verifica si el cliente tiene órdenes activas (CREADA o PAGADA) */
    public boolean tieneOrdenesActivas(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ORDENES WHERE ID_CLIENTE = ? AND ESTADO IN ('CREADA','PAGADA')";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    /** Verifica si ya existe un cliente con esa identificación */
    public boolean existeIdentificacion(String identificacion) throws SQLException {
        String sql = "SELECT COUNT(*) FROM CLIENTES WHERE IDENTIFICACION = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, identificacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    /** Mapea un ResultSet a un objeto Cliente */
    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("ID"));
        c.setIdentificacion(rs.getString("IDENTIFICACION"));
        c.setNombre(rs.getString("NOMBRE"));
        c.setTelefono(rs.getString("TELEFONO"));
        c.setCorreo(rs.getString("CORREO"));
        c.setActivo(rs.getInt("ACTIVO") == 1);
        return c;
    }
}
