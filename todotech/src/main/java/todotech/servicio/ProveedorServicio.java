package todotech.servicio;

import todotech.modelo.Proveedor;
import todotech.repositorio.ProveedorRepositorio;

import java.util.List;

/**
 * Servicio de lógica de negocio para Proveedor.
 * MOD-10 — Gestionar Proveedores.
 * RN: NIT único e inmutable.
 * RN: no se puede eliminar si tiene productos activos asociados.
 * RN: campos obligatorios — nombre, NIT, teléfono, correo.
 */
public class ProveedorServicio {

    private final ProveedorRepositorio repositorio = new ProveedorRepositorio();

    /** Registra un nuevo proveedor */
    public void registrar(Proveedor proveedor) throws Exception {
        validarCamposObligatorios(proveedor);

        if (repositorio.existeNit(proveedor.getNit()))
            throw new Exception("Ya existe un proveedor con el NIT: " + proveedor.getNit());

        boolean guardado = repositorio.crear(proveedor);
        if (!guardado) throw new Exception("No se pudo registrar el proveedor.");
    }

    /** Busca un proveedor por NIT */
    public Proveedor buscarPorNit(String nit) throws Exception {
        if (nit == null || nit.isBlank()) throw new Exception("El NIT no puede estar vacío.");
        Proveedor p = repositorio.buscarPorNit(nit);
        if (p == null) throw new Exception("No se encontró proveedor con NIT: " + nit);
        return p;
    }

    /** Lista todos los proveedores activos */
    public List<Proveedor> listarActivos() throws Exception {
        return repositorio.listarActivos();
    }

    /** Busca por nombre (para autocompletar) */
    public List<Proveedor> buscarPorNombre(String nombre) throws Exception {
        return repositorio.buscarPorNombre(nombre);
    }

    /** Actualiza datos del proveedor. El NIT no se puede cambiar. */
    public void actualizar(Proveedor proveedor) throws Exception {
        validarCamposObligatorios(proveedor);
        if (proveedor.getId() <= 0) throw new Exception("El proveedor no tiene un ID válido.");

        boolean actualizado = repositorio.actualizar(proveedor);
        if (!actualizado) throw new Exception("No se pudo actualizar el proveedor.");
    }

    /** Desactiva un proveedor (eliminación lógica) */
    public void desactivar(int idProveedor) throws Exception {
        if (repositorio.tieneProductosActivos(idProveedor))
            throw new Exception("No se puede eliminar el proveedor porque tiene productos activos asociados.");

        boolean desactivado = repositorio.desactivar(idProveedor);
        if (!desactivado) throw new Exception("No se pudo eliminar el proveedor.");
    }

    /** Asocia un proveedor a un producto */
    public void asociarProducto(int idProducto, int idProveedor) throws Exception {
        boolean ok = repositorio.asociarProducto(idProducto, idProveedor);
        if (!ok) throw new Exception("No se pudo asociar el proveedor al producto.");
    }

    private void validarCamposObligatorios(Proveedor p) throws Exception {
        if (p.getNombre() == null || p.getNombre().isBlank())
            throw new Exception("El nombre es obligatorio.");
        if (p.getNit() == null || p.getNit().isBlank())
            throw new Exception("El NIT es obligatorio.");
        if (p.getTelefono() == null || p.getTelefono().isBlank())
            throw new Exception("El teléfono es obligatorio.");
        if (p.getCorreo() == null || p.getCorreo().isBlank())
            throw new Exception("El correo es obligatorio.");
    }
}