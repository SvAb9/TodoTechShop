package todotech.vista;

import todotech.modelo.DetalleOrden;
import todotech.modelo.OrdenVenta;
import todotech.servicio.HistoricoServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formulario de Registro Histórico de Órdenes.
 * MOD-05 — Gestionar Registro Histórico.
 * RF-21: listar y filtrar órdenes por estado.
 * RF-22: ver detalle completo de una orden al hacer doble clic o presionar "Ver detalle".
 */
public class FormHistorico extends JPanel {

    private final HistoricoServicio servicio = new HistoricoServicio();

    // Filtros
    private JComboBox<String> cmbEstado;
    private JTextField        txtNumeroOrden;

    // Tabla principal
    private JTable           tablaOrdenes;
    private DefaultTableModel modeloOrdenes;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public FormHistorico() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        construirUI();
        cargarOrdenes();
    }

    private void construirUI() {
        JLabel titulo = new JLabel("Registro Histórico de Órdenes");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(31, 78, 121));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        add(panelFiltros(), BorderLayout.CENTER);
    }

    // ─── PANEL PRINCIPAL ──────────────────────────────────────────────
    private JPanel panelFiltros() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(new Color(245, 247, 250));

        // Barra de filtros
        JPanel pFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pFiltros.setBackground(Color.WHITE);
        pFiltros.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Filtros", 0, 0,
            new Font("Arial", Font.BOLD, 12), new Color(31, 78, 121)));

        pFiltros.add(etiqueta("Estado:"));
        cmbEstado = new JComboBox<>(new String[]{
            "TODOS", "CREADA", "PAGADA", "CERRADA", "CANCELADA", "DEVUELTA"
        });
        pFiltros.add(cmbEstado);

        pFiltros.add(etiqueta("  Número de orden:"));
        txtNumeroOrden = new JTextField(12);
        pFiltros.add(txtNumeroOrden);

        JButton btnBuscar    = boton("Buscar",    new Color(46, 117, 182));
        JButton btnVerTodos  = boton("Ver Todos", new Color(80, 80, 80));
        JButton btnDetalle   = boton("Ver Detalle", new Color(31, 78, 121));

        btnBuscar.addActionListener(e   -> filtrar());
        btnVerTodos.addActionListener(e -> cargarOrdenes());
        btnDetalle.addActionListener(e  -> verDetalle());

        pFiltros.add(btnBuscar);
        pFiltros.add(btnVerTodos);
        pFiltros.add(btnDetalle);

        panel.add(pFiltros, BorderLayout.NORTH);
        panel.add(panelTabla(), BorderLayout.CENTER);
        return panel;
    }

    // ─── TABLA ────────────────────────────────────────────────────────
    private JPanel panelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(31, 78, 121)),
            "Órdenes", 0, 0,
            new Font("Arial", Font.BOLD, 13), new Color(31, 78, 121)));

        String[] columnas = { "Número", "Cliente", "Total", "Estado", "Medio Pago", "Fecha" };
        modeloOrdenes = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaOrdenes = new JTable(modeloOrdenes);
        tablaOrdenes.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaOrdenes.setRowHeight(26);
        tablaOrdenes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablaOrdenes.getTableHeader().setBackground(new Color(31, 78, 121));
        tablaOrdenes.getTableHeader().setForeground(Color.WHITE);
        tablaOrdenes.setSelectionBackground(new Color(173, 214, 241));
        tablaOrdenes.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaOrdenes.getColumnModel().getColumn(2).setMaxWidth(90);
        tablaOrdenes.getColumnModel().getColumn(3).setMaxWidth(90);

        // Doble clic abre el detalle
        tablaOrdenes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) verDetalle();
            }
        });

        panel.add(new JScrollPane(tablaOrdenes), BorderLayout.CENTER);

        // Contador de resultados
        JLabel lblContador = new JLabel("  Doble clic en una fila para ver el detalle completo.");
        lblContador.setFont(new Font("Arial", Font.ITALIC, 11));
        lblContador.setForeground(new Color(100, 100, 100));
        panel.add(lblContador, BorderLayout.SOUTH);

        return panel;
    }

    // ─── ACCIONES ─────────────────────────────────────────────────────

    private void filtrar() {
        String numeroOrden = txtNumeroOrden.getText().trim();

        // Si ingresó número de orden, busca por ese
        if (!numeroOrden.isEmpty()) {
            try {
                OrdenVenta orden = servicio.buscarPorNumero(numeroOrden);
                modeloOrdenes.setRowCount(0);
                agregarFila(orden);
            } catch (Exception ex) {
                mostrarError(ex.getMessage());
            }
            return;
        }

        // Si no, filtra por estado
        try {
            String estado = (String) cmbEstado.getSelectedItem();
            List<OrdenVenta> lista = servicio.listarPorEstado(estado);
            poblarTabla(lista);
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cargarOrdenes() {
        try {
            poblarTabla(servicio.listarTodas());
            cmbEstado.setSelectedIndex(0);
            txtNumeroOrden.setText("");
        } catch (Exception ex) {
            mostrarError("Error cargando historial: " + ex.getMessage());
        }
    }

    private void verDetalle() {
        int fila = tablaOrdenes.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccione una orden de la tabla primero.");
            return;
        }

        String numeroOrden = (String) modeloOrdenes.getValueAt(fila, 0);
        try {
            OrdenVenta orden = servicio.buscarPorNumero(numeroOrden);
            mostrarDialogoDetalle(orden);
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    /** Abre un diálogo modal con el detalle completo de la orden */
    private void mostrarDialogoDetalle(OrdenVenta orden) {
        JDialog dialogo = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Detalle — " + orden.getNumeroOrden(),
            java.awt.Dialog.ModalityType.APPLICATION_MODAL
        );
        dialogo.setSize(620, 450);
        dialogo.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Info general
        JPanel pInfo = new JPanel(new GridLayout(0, 2, 5, 5));
        pInfo.setBackground(Color.WHITE);
        pInfo.setBorder(BorderFactory.createTitledBorder("Información General"));

        pInfo.add(etiqueta("Número:"));
        pInfo.add(etiquetaBold(orden.getNumeroOrden()));
        pInfo.add(etiqueta("Cliente:"));
        pInfo.add(etiquetaBold(orden.getCliente() != null ? orden.getCliente().getNombre() : "—"));
        pInfo.add(etiqueta("Estado:"));
        pInfo.add(etiquetaBold(orden.getEstado().name()));
        pInfo.add(etiqueta("Medio de pago:"));
        pInfo.add(etiquetaBold(orden.getMedioPago() != null ? orden.getMedioPago() : "—"));
        pInfo.add(etiqueta("Total:"));
        pInfo.add(etiquetaBold(String.format("$%.2f", orden.getTotal())));
        pInfo.add(etiqueta("Fecha:"));
        pInfo.add(etiquetaBold(orden.getFechaCreacion() != null ? orden.getFechaCreacion().format(FMT) : "—"));

        panel.add(pInfo, BorderLayout.NORTH);

        // Tabla de detalles
        String[] cols = { "Producto", "Código", "Precio Unit.", "Cantidad", "Subtotal" };
        DefaultTableModel modeloDetalle = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (DetalleOrden d : orden.getDetalles()) {
            modeloDetalle.addRow(new Object[]{
                d.getProducto().getNombre(),
                d.getProducto().getCodigo(),
                String.format("$%.2f", d.getPrecioUnitario()),
                d.getCantidad(),
                String.format("$%.2f", d.getSubtotal())
            });
        }

        JTable tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaDetalle.setRowHeight(24);
        tablaDetalle.getTableHeader().setBackground(new Color(31, 78, 121));
        tablaDetalle.getTableHeader().setForeground(Color.WHITE);
        tablaDetalle.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JPanel pTabla = new JPanel(new BorderLayout());
        pTabla.setBorder(BorderFactory.createTitledBorder("Productos"));
        pTabla.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);
        panel.add(pTabla, BorderLayout.CENTER);

        // Botón cerrar
        JButton btnCerrar = boton("Cerrar", new Color(80, 80, 80));
        btnCerrar.addActionListener(e -> dialogo.dispose());
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtn.setBackground(Color.WHITE);
        pBtn.add(btnCerrar);
        panel.add(pBtn, BorderLayout.SOUTH);

        dialogo.setContentPane(panel);
        dialogo.setVisible(true);
    }

    // ─── HELPERS ──────────────────────────────────────────────────────

    private void poblarTabla(List<OrdenVenta> lista) {
        modeloOrdenes.setRowCount(0);
        for (OrdenVenta o : lista) agregarFila(o);
    }

    private void agregarFila(OrdenVenta o) {
        modeloOrdenes.addRow(new Object[]{
            o.getNumeroOrden(),
            o.getCliente() != null ? o.getCliente().getNombre() : "—",
            String.format("$%.2f", o.getTotal()),
            o.getEstado().name(),
            o.getMedioPago() != null ? o.getMedioPago() : "—",
            o.getFechaCreacion() != null ? o.getFechaCreacion().format(FMT) : "—"
        });
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        return l;
    }

    private JLabel etiquetaBold(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        return l;
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

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}