package todotech.vista;

import todotech.modelo.*;
import todotech.servicio.ClienteServicio;
import todotech.servicio.OrdenServicio;
import todotech.repositorio.ProductoRepositorio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Formulario de Crear Orden de Venta.
 * MOD-01 — Proceso de negocio: realizar venta.
 * Actividades: buscar cliente, ingresar clave, buscar producto,
 *              agregar a orden, calcular total, confirmar.
 */
public class FormOrden extends JPanel {

    private final OrdenServicio    ordenServicio    = new OrdenServicio();
    private final ClienteServicio  clienteServicio  = new ClienteServicio();
    private final ProductoRepositorio productoRepo  = new ProductoRepositorio();

    // Orden activa
    private OrdenVenta ordenActual;

    // Campos cliente
    private JTextField txtBuscarCliente;
    private JLabel     lblClienteSeleccionado;
    private JPasswordField txtClave;

    // Campos producto
    private JTextField txtBuscarProducto;
    private JComboBox<Producto> cmbProductos;
    private JSpinner   spnCantidad;

    // Tabla de detalles
    private JTable             tablaDetalles;
    private DefaultTableModel  modeloDetalles;

    // Total
    private JLabel lblTotal;
    private JLabel lblNumeroOrden;

    // Cliente seleccionado
    private Cliente clienteSeleccionado;

    public FormOrden() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        construirUI();
    }

    private void construirUI() {
        JLabel titulo = new JLabel("Crear Orden de Venta");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(31, 78, 121));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel central dividido: izquierda datos, derecha tabla
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            panelDatos(), panelDetalles());
        split.setDividerLocation(370);
        add(split, BorderLayout.CENTER);

        // Panel inferior: total y botones
        add(panelInferior(), BorderLayout.SOUTH);
    }

    // ─── PANEL IZQUIERDO: datos de orden ─────────────────────────────
    private JPanel panelDatos() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Número de orden
        JPanel pNumero = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pNumero.setBackground(Color.WHITE);
        pNumero.add(new JLabel("Número de orden:"));
        lblNumeroOrden = new JLabel("(se genera al confirmar)");
        lblNumeroOrden.setFont(new Font("Arial", Font.BOLD, 13));
        lblNumeroOrden.setForeground(new Color(31, 78, 121));
        pNumero.add(lblNumeroOrden);
        panel.add(pNumero);
        panel.add(separador("Buscar Cliente"));

        // Buscar cliente
        JPanel pBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pBuscar.setBackground(Color.WHITE);
        txtBuscarCliente = new JTextField(15);
        JButton btnBuscarCliente = boton("Buscar", new Color(46, 117, 182));
        btnBuscarCliente.addActionListener(e -> buscarCliente());
        pBuscar.add(txtBuscarCliente);
        pBuscar.add(btnBuscarCliente);
        panel.add(pBuscar);

        lblClienteSeleccionado = new JLabel("Sin cliente seleccionado");
        lblClienteSeleccionado.setFont(new Font("Arial", Font.ITALIC, 13));
        lblClienteSeleccionado.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        panel.add(lblClienteSeleccionado);

        // Clave secreta
        panel.add(separador("Clave Secreta del Cliente"));
        JPanel pClave = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pClave.setBackground(Color.WHITE);
        pClave.add(new JLabel("Clave:"));
        txtClave = new JPasswordField(10);
        pClave.add(txtClave);
        panel.add(pClave);

        // Botón iniciar orden
        JPanel pIniciar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pIniciar.setBackground(Color.WHITE);
        JButton btnIniciar = boton("Iniciar Orden", new Color(55, 86, 35));
        btnIniciar.addActionListener(e -> iniciarOrden());
        pIniciar.add(btnIniciar);
        panel.add(pIniciar);

        // Buscar producto
        panel.add(separador("Agregar Producto"));
        JPanel pBuscarProd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pBuscarProd.setBackground(Color.WHITE);
        txtBuscarProducto = new JTextField(15);
        JButton btnBuscarProd = boton("Buscar", new Color(46, 117, 182));
        btnBuscarProd.addActionListener(e -> buscarProducto());
        pBuscarProd.add(txtBuscarProducto);
        pBuscarProd.add(btnBuscarProd);
        panel.add(pBuscarProd);

        cmbProductos = new JComboBox<>();
        cmbProductos.setPreferredSize(new Dimension(340, 28));
        JPanel pCombo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pCombo.setBackground(Color.WHITE);
        pCombo.add(cmbProductos);
        panel.add(pCombo);

        JPanel pCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pCantidad.setBackground(Color.WHITE);
        pCantidad.add(new JLabel("Cantidad:"));
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spnCantidad.setPreferredSize(new Dimension(70, 28));
        pCantidad.add(spnCantidad);
        JButton btnAgregar = boton("Agregar", new Color(55, 86, 35));
        btnAgregar.addActionListener(e -> agregarProducto());
        pCantidad.add(btnAgregar);
        panel.add(pCantidad);

        return panel;
    }

    // ─── PANEL DERECHO: tabla de detalles ────────────────────────────
    private JPanel panelDetalles() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Productos en la Orden", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        String[] cols = { "#", "Código", "Producto", "Precio Unit.", "Cantidad", "Subtotal" };
        modeloDetalles = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDetalles = new JTable(modeloDetalles);
        tablaDetalles.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaDetalles.setRowHeight(24);
        tablaDetalles.getTableHeader().setBackground(new Color(31, 78, 121));
        tablaDetalles.getTableHeader().setForeground(Color.WHITE);
        tablaDetalles.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablaDetalles.getColumnModel().getColumn(0).setMaxWidth(35);

        JButton btnEliminarLinea = boton("Quitar línea seleccionada", new Color(123, 36, 28));
        btnEliminarLinea.addActionListener(e -> eliminarLinea());

        panel.add(new JScrollPane(tablaDetalles), BorderLayout.CENTER);
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtn.add(btnEliminarLinea);
        panel.add(pBtn, BorderLayout.SOUTH);
        return panel;
    }

    // ─── PANEL INFERIOR: total y confirmar ───────────────────────────
    private JPanel panelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(31, 78, 121));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        lblTotal = new JLabel("TOTAL: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(Color.WHITE);
        panel.add(lblTotal, BorderLayout.WEST);

        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pBotones.setBackground(new Color(31, 78, 121));

        JButton btnConfirmar = boton("Confirmar Orden", new Color(55, 183, 100));
        JButton btnCancelar  = boton("Cancelar",        new Color(180, 60, 40));
        btnConfirmar.addActionListener(e -> confirmarOrden());
        btnCancelar.addActionListener(e  -> cancelarOrden());

        pBotones.add(btnCancelar);
        pBotones.add(btnConfirmar);
        panel.add(pBotones, BorderLayout.EAST);
        return panel;
    }

    // ─── ACCIONES ─────────────────────────────────────────────────────

    private void buscarCliente() {
        String texto = txtBuscarCliente.getText().trim();
        if (texto.isEmpty()) { mostrarError("Ingrese identificación o nombre."); return; }
        try {
            // Intenta primero por identificación exacta
            Cliente c = clienteServicio.buscarPorIdentificacion(texto);
            seleccionarCliente(c);
        } catch (Exception e1) {
            try {
                List<Cliente> lista = clienteServicio.buscarPorNombre(texto);
                if (lista.isEmpty()) { mostrarError("No se encontró ningún cliente."); return; }
                if (lista.size() == 1) { seleccionarCliente(lista.get(0)); return; }
                // Si hay varios, mostrar selector
                Cliente[] arr = lista.toArray(new Cliente[0]);
                Cliente seleccionado = (Cliente) JOptionPane.showInputDialog(
                    this, "Seleccione el cliente:", "Clientes encontrados",
                    JOptionPane.PLAIN_MESSAGE, null, arr, arr[0]);
                if (seleccionado != null) seleccionarCliente(seleccionado);
            } catch (Exception e2) {
                mostrarError(e2.getMessage());
            }
        }
    }

    private void seleccionarCliente(Cliente c) {
        clienteSeleccionado = c;
        lblClienteSeleccionado.setText(c.getIdentificacion() + " — " + c.getNombre());
        lblClienteSeleccionado.setForeground(new Color(55, 86, 35));
    }

    private void iniciarOrden() {
        if (clienteSeleccionado == null) { mostrarError("Primero busque y seleccione un cliente."); return; }
        String clave = new String(txtClave.getPassword()).trim();
        try {
            ordenActual = ordenServicio.iniciarOrden(clienteSeleccionado, clave);
            lblNumeroOrden.setText(ordenActual.getNumeroOrden());
            modeloDetalles.setRowCount(0);
            actualizarTotal();
            mostrarExito("Orden iniciada: " + ordenActual.getNumeroOrden());
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void buscarProducto() {
        String texto = txtBuscarProducto.getText().trim();
        if (texto.isEmpty()) { mostrarError("Ingrese nombre o código del producto."); return; }
        try {
            List<Producto> lista = productoRepo.buscar(texto);
            cmbProductos.removeAllItems();
            if (lista.isEmpty()) { mostrarError("No se encontraron productos."); return; }
            for (Producto p : lista) cmbProductos.addItem(p);
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void agregarProducto() {
        if (ordenActual == null) { mostrarError("Primero inicie la orden."); return; }
        Producto p = (Producto) cmbProductos.getSelectedItem();
        if (p == null) { mostrarError("Seleccione un producto."); return; }
        int cantidad = (int) spnCantidad.getValue();
        try {
            ordenServicio.agregarProducto(ordenActual, p, cantidad);
            refrescarTabla();
            actualizarTotal();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarLinea() {
        if (ordenActual == null) return;
        int fila = tablaDetalles.getSelectedRow();
        if (fila < 0) { mostrarError("Seleccione una línea para quitar."); return; }
        try {
            ordenServicio.eliminarProducto(ordenActual, fila);
            refrescarTabla();
            actualizarTotal();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void confirmarOrden() {
        if (ordenActual == null) { mostrarError("No hay orden activa."); return; }
        try {
            ordenServicio.confirmarOrden(ordenActual);
            mostrarExito("Orden " + ordenActual.getNumeroOrden() + " confirmada. Pase a caja.");
            limpiar();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cancelarOrden() {
        int conf = JOptionPane.showConfirmDialog(this,
            "¿Desea cancelar la orden actual?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) limpiar();
    }

    // ─── HELPERS ──────────────────────────────────────────────────────

    private void refrescarTabla() {
        modeloDetalles.setRowCount(0);
        int i = 1;
        for (DetalleOrden d : ordenActual.getDetalles()) {
            modeloDetalles.addRow(new Object[]{
                i++,
                d.getProducto().getCodigo(),
                d.getProducto().getNombre(),
                String.format("$%.2f", d.getPrecioUnitario()),
                d.getCantidad(),
                String.format("$%.2f", d.getSubtotal())
            });
        }
    }

    private void actualizarTotal() {
        double total = ordenActual != null ? ordenActual.getTotal() : 0;
        lblTotal.setText(String.format("TOTAL: $%.2f", total));
    }

    private void limpiar() {
        ordenActual = null;
        clienteSeleccionado = null;
        txtBuscarCliente.setText("");
        txtClave.setText("");
        txtBuscarProducto.setText("");
        cmbProductos.removeAllItems();
        spnCantidad.setValue(1);
        modeloDetalles.setRowCount(0);
        lblClienteSeleccionado.setText("Sin cliente seleccionado");
        lblClienteSeleccionado.setForeground(Color.GRAY);
        lblNumeroOrden.setText("(se genera al confirmar)");
        lblTotal.setText("TOTAL: $0.00");
    }

    private JSeparator separador(String titulo) {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(200, 200, 200));
        return sep;
    }

    private JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return btn;
    }

    private void mostrarExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
