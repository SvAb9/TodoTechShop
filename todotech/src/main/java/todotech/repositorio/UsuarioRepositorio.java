package todotech.repositorio;

import todotech.config.ConexionDB;
import todotech.modelo.RolUsuario;
import todotech.modelo.Usuario;
import java.sql.*;

public class UsuarioRepositorio {
    private final Connection conexion;

    public UsuarioRepositorio() {
        this.conexion = ConexionDB.getInstance().getConexion();
    }

    public Usuario buscarPorCredenciales(String usuario, String password) throws SQLException {
        String sql = "SELECT * FROM USUARIOS WHERE USUARIO = ? AND PASSWORD = ? AND ACTIVO = 1";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public boolean crear(Usuario u) throws SQLException {
        String sql = "INSERT INTO USUARIOS (NOMBRE, USUARIO, PASSWORD, ROL, ACTIVO) VALUES (?,?,?,?,1)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRol().name());
            return ps.executeUpdate() > 0;
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("ID"));
        u.setNombre(rs.getString("NOMBRE"));
        u.setUsuario(rs.getString("USUARIO"));
        u.setPassword(rs.getString("PASSWORD"));
        u.setRol(RolUsuario.valueOf(rs.getString("ROL")));
        u.setActivo(rs.getInt("ACTIVO") == 1);
        return u;
    }
}