package todotech.servicio;

import todotech.modelo.RolUsuario;
import todotech.modelo.Usuario;
import todotech.repositorio.UsuarioRepositorio;

public class UsuarioServicio {
    private final UsuarioRepositorio repositorio = new UsuarioRepositorio();
    private Usuario usuarioActual;

    public Usuario login(String usuario, String password) throws Exception {
        if (usuario == null || usuario.isBlank())
            throw new Exception("El usuario es obligatorio.");
        Usuario u = repositorio.buscarPorCredenciales(usuario, password);
        if (u == null) throw new Exception("Credenciales inválidas.");
        this.usuarioActual = u;
        return u;
    }

    public void verificarPermiso(RolUsuario rolRequerido) throws Exception {
        if (usuarioActual == null)
            throw new Exception("No hay sesión activa.");
        if (usuarioActual.getRol() != rolRequerido && usuarioActual.getRol() != RolUsuario.ADMINISTRADOR)
            throw new Exception("No tienes permiso para realizar esta acción.");
    }

    public Usuario getUsuarioActual() { return usuarioActual; }
}