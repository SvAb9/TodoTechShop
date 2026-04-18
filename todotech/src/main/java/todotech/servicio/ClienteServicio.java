package todotech.servicio;

import todotech.modelo.Cliente;
import todotech.repositorio.ClienteRepositorio;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio de lógica de negocio para Cliente.
 * MOD-08 — Gestionar Clientes.
 * Aquí van las validaciones de negocio antes de llamar al repositorio.
 */
public class ClienteServicio {

    private final ClienteRepositorio repositorio;

    public ClienteServicio() {
        this.repositorio = new ClienteRepositorio();
    }

    /**
     * Registra un nuevo cliente.
     * Valida: campos obligatorios y que la identificación no exista.
     */
    public void registrar(Cliente cliente) throws Exception {
        validarCamposObligatorios(cliente);

        if (repositorio.existeIdentificacion(cliente.getIdentificacion())) {
            throw new Exception("Ya existe un cliente con la identificación: " + cliente.getIdentificacion());
        }

        boolean guardado = repositorio.crear(cliente);
        if (!guardado) throw new Exception("No se pudo registrar el cliente.");
    }

    /**
     * Busca un cliente por su número de identificación.
     */
    public Cliente buscarPorIdentificacion(String identificacion) throws Exception {
        if (identificacion == null || identificacion.isBlank()) {
            throw new Exception("La identificación no puede estar vacía.");
        }
        Cliente cliente = repositorio.buscarPorIdentificacion(identificacion);
        if (cliente == null) throw new Exception("No se encontró ningún cliente con identificación: " + identificacion);
        return cliente;
    }

    /**
     * Retorna todos los clientes activos.
     */
    public List<Cliente> listarActivos() throws Exception {
        return repositorio.listarActivos();
    }

    /**
     * Busca clientes por nombre (para autocompletar).
     */
    public List<Cliente> buscarPorNombre(String nombre) throws Exception {
        return repositorio.buscarPorNombre(nombre);
    }

    /**
     * Actualiza los datos de un cliente.
     * No permite cambiar la identificación (RN-01).
     */
    public void actualizar(Cliente cliente) throws Exception {
        validarCamposObligatorios(cliente);

        if (cliente.getId() <= 0) {
            throw new Exception("El cliente no tiene un ID válido.");
        }

        boolean actualizado = repositorio.actualizar(cliente);
        if (!actualizado) throw new Exception("No se pudo actualizar el cliente.");
    }

    /**
     * Desactiva un cliente (eliminación lógica).
     * No permite desactivar si tiene órdenes activas.
     */
    public void desactivar(int idCliente) throws Exception {
        if (repositorio.tieneOrdenesActivas(idCliente)) {
            throw new Exception("No se puede eliminar el cliente porque tiene órdenes activas (CREADA o PAGADA).");
        }

        boolean desactivado = repositorio.desactivar(idCliente);
        if (!desactivado) throw new Exception("No se pudo eliminar el cliente.");
    }

    /** Valida que los campos obligatorios no estén vacíos */
    private void validarCamposObligatorios(Cliente cliente) throws Exception {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank())
            throw new Exception("El nombre es obligatorio.");
        if (cliente.getIdentificacion() == null || cliente.getIdentificacion().isBlank())
            throw new Exception("La identificación es obligatoria.");
        if (cliente.getTelefono() == null || cliente.getTelefono().isBlank())
            throw new Exception("El teléfono es obligatorio.");
        if (cliente.getCorreo() == null || cliente.getCorreo().isBlank())
            throw new Exception("El correo es obligatorio.");
    }
}
