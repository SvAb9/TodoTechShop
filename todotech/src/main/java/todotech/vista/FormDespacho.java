package todotech.vista;

import todotech.modelo.DetalleOrden;
import todotech.modelo.OrdenVenta;
import todotech.servicio.DespachoServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Formulario de Gestión de Despacho.
 * MOD-04 — Gestionar Despacho.
 * El despachador ingresa el número de orden y la clave secreta
 * para validar y confirmar la entrega al cliente.
 * RF-17: buscar orden por número + clave.
 * RF-20: confirmar despacho → orden pasa a CERRADA.
 */
public class FormDespacho extends JPanel {

    private final DespachoServicio servicio = new DespachoServicio();

    // Campos de búsqueda
    private JTextField    txtNumeroOrden;
    private JPasswordField txtClaveSecreta;

    // Info de la orden encontrada
    private JLabel        lblCliente;
    private JLabel        lblTotal;
    private JLabel        lblEstado;
    private JLabel        lblMedioPago;

    // Tabla de productos a despachar
    private JTable        tablaProductos;
    private DefaultTableModel modeloTabla;

    // Botón confirmar
    private JButton       btnConfirmar;

    // Orden activa
    private OrdenVenta ordenActual;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public FormDespacho() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        construirUI();
    }

    private void construirUI() {
        JLabel titulo = new JLabel("Gestión de Despacho");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(31, 78, 121));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            panelBusqueda(), panelDetalle());
        split.setDividerLocation(340);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);

        add(panelBotonConfirmar(), BorderLayout.SOUTH);
    }

    // ─── PANEL IZQUIERDO: búsqueda ────────────────────────────────────
    private JPanel panelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Validar Orden", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Número de orden
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(etiqueta("Número de orden:"), gbc);
        gbc.gridy = 1;
        txtNumeroOrden = new JTextField(15);
        txtNumeroOrden.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtNumeroOrden, gbc);

        // Clave secreta
        gbc.gridy = 2;
        panel.add(etiqueta("Clave secreta:"), gbc);
        gbc.gridy = 3;
        txtClaveSecreta = new JPasswordField(15);
        txtClaveSecreta.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtClaveSecreta, gbc);

        // Botón buscar
        gbc.gridy = 4;
        JButton btnBuscar = boton("Buscar Orden", new Color(46, 117, 182));
        btnBuscar.addActionListener(e -> buscarOrden());
        panel.add(btnBuscar, gbc);

        // Separador
        gbc.gridy = 5;
        panel.add(new JSeparator(), gbc);

        // Info de la orden
        gbc.gridy = 6;
        panel.add(etiquetaBold("Datos de la Orden:"), gbc);

        gbc.gridy = 7;
        lblCliente = new JLabel("Cliente: —");
        lblCliente.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lblCliente, gbc);

        gbc.gridy = 8;
        lblTotal = new JLabel("Total: —");
        lblTotal.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lblTotal, gbc);

        gbc.gridy = 9;
        lblMedioPago = new JLabel("Medio de pago: —");
        lblMedioPago.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lblMedioPago, gbc);

        gbc.gridy = 10;
        lblEstado = new JLabel("Estado: —");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblEstado, gbc);

        // Botón limpiar
        gbc.gridy = 11;
        JButton btnLimpiar = boton("Limpiar", new Color(80, 80, 80));
        btnLimpiar.addActionListener(e -> limpiar());
        panel.add(btnLimpiar, gbc);

        return panel;
    }

    // ─── PANEL DERECHO: productos a despachar ─────────────────────────
    private JPanel panelDetalle() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Productos a Despachar", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        String[] columnas = { "#", "Código", "Producto", "Ubicación", "Cantidad", "Precio Unit.", "Subtotal" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaProductos.setRowHeight(26);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablaProductos.getTableHeader().setBackground(new Color(31, 78, 121));
        tablaProductos.getTableHeader().setForeground(Color.WHITE);
        tablaProductos.setSelectionBackground(new Color(173, 214, 241));
        tablaProductos.getColumnModel().getColumn(0).setMaxWidth(35);
        tablaProductos.getColumnModel().getColumn(4).setMaxWidth(70);

        panel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        return panel;
    }

    // ─── PANEL INFERIOR: botón confirmar ─────────────────────────────
    private JPanel panelBotonConfirmar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(31, 78, 121));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel lblInfo = new JLabel("  Verifique los productos antes de confirmar el despacho.");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(200, 220, 240));
        panel.add(lblInfo, BorderLayout.WEST);

        btnConfirmar = boton("Confirmar Despacho", new Color(55, 183, 100));
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirmar.setEnabled(false); // solo se habilita al encontrar una orden válida
        btnConfirmar.addActionListener(e -> confirmarDespacho());

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pBtn.setBackground(new Color(31, 78, 121));
        pBtn.add(btnConfirmar);
        panel.add(pBtn, BorderLayout.EAST);

        return panel;
    }

    // ─── ACCIONES ─────────────────────────────────────────────────────

    private void buscarOrden() {
        String numero = txtNumeroOrden.getText().trim();
        String clave  = new String(txtClaveSecreta.getPassword()).trim();

        try {
            ordenActual = servicio.buscarParaDespacho(numero, clave);
            mostrarDatosOrden();
            btnConfirmar.setEnabled(true);
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
            limpiar();
        }
    }

    private void confirmarDespacho() {
        if (ordenActual == null) return;

        int conf = JOptionPane.showConfirmDialog(this,
            "¿Confirma el despacho de la orden " + ordenActual.getNumeroOrden() +
            " para " + ordenActual.getCliente().getNombre() + "?",
            "Confirmar Despacho", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (conf != JOptionPane.YES_OPTION) return;

        try {
            servicio.confirmarDespacho(ordenActual);
            mostrarExito("Orden " + ordenActual.getNumeroOrden() +
                         " despachada correctamente. Estado: CERRADA.");
            limpiar();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    // ─── HELPERS ──────────────────────────────────────────────────────

    private void mostrarDatosOrden() {
        lblCliente.setText("Cliente: " + ordenActual.getCliente().getNombre()
            + " (" + ordenActual.getCliente().getIdentificacion() + ")");
        lblTotal.setText(String.format("Total: $%.2f", ordenActual.getTotal()));
        lblMedioPago.setText("Medio de pago: " + (ordenActual.getMedioPago() != null
            ? ordenActual.getMedioPago() : "—"));
        lblEstado.setText("Estado: " + ordenActual.getEstado().name());
        lblEstado.setForeground(new Color(55, 86, 35));

        // Poblar tabla de productos
        modeloTabla.setRowCount(0);
        int i = 1;
        for (DetalleOrden d : ordenActual.getDetalles()) {
            modeloTabla.addRow(new Object[]{
                i++,
                d.getProducto().getCodigo(),
                d.getProducto().getNombre(),
                d.getProducto().getUbicacionBodega() != null ? d.getProducto().getUbicacionBodega() : "—",
                d.getCantidad(),
                String.format("$%.2f", d.getPrecioUnitario()),
                String.format("$%.2f", d.getSubtotal())
            });
        }
    }

    private void limpiar() {
        ordenActual = null;
        txtNumeroOrden.setText("");
        txtClaveSecreta.setText("");
        lblCliente.setText("Cliente: —");
        lblTotal.setText("Total: —");
        lblMedioPago.setText("Medio de pago: —");
        lblEstado.setText("Estado: —");
        lblEstado.setForeground(Color.DARK_GRAY);
        modeloTabla.setRowCount(0);
        btnConfirmar.setEnabled(false);
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        return l;
    }

    private JLabel etiquetaBold(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(new Color(31, 78, 121));
        return l;
    }

    private JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return btn;
    }

    private void mostrarExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Despacho Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}